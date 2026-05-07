/*
 * Copyright 2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package rife.bld.extension.tools;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Generic process executor with timeout, I/O control, and output capture.
 * <p>
 * Framework-agnostic utility that can be composed by any extension.
 * Handles process tree cleanup and stream management for Windows compatibility.
 *
 * @author <a href="https://erik.thauvin.net/">Erik C. Thauvin</a>
 * @since 1.0
 */
@SuppressFBWarnings(value = "EI_EXPOSE_REP", justification = "intentional and documented")
public class ProcessExecutor {

    public static final String COMMAND_NOT_VALID = "command values must not be null or empty";

    /**
     * Default timeout in seconds.
     */
    static final int DEFAULT_TIMEOUT_SECONDS = 30;

    private final List<String> command_ = new ArrayList<>();
    private final Map<String, String> env_ = new HashMap<>();
    private boolean inheritIO_;
    @Nullable
    private Consumer<String> outputConsumer_;
    private int timeout_ = DEFAULT_TIMEOUT_SECONDS;
    private File workDir_;

    /**
     * Sets the command and arguments to be executed, replacing any previously configured command.
     *
     * @param args one or more arguments, must not be null or contain null/empty elements
     * @return this instance
     * @throws NullPointerException     if args is null
     * @throws IllegalArgumentException if args contains null or empty elements
     */
    public ProcessExecutor command(@NonNull String... args) {
        ObjectTools.requireAllNotEmpty(args, COMMAND_NOT_VALID);
        command_.clear();
        command_.addAll(List.of(args));
        return this;
    }

    /**
     * Returns the mutable command and arguments list.
     *
     * @return the command list, never null
     */
    public List<String> command() {
        return command_;
    }

    /**
     * Sets the command and arguments to be executed, replacing any previously configured command.
     *
     * @param args the list of arguments, must not be null or contain null/empty elements
     * @return this instance
     * @throws NullPointerException     if args is null
     * @throws IllegalArgumentException if args contains null or empty elements
     */
    public ProcessExecutor command(@NonNull Collection<String> args) {
        ObjectTools.requireAllNotEmpty(args, COMMAND_NOT_VALID);
        command_.clear();
        command_.addAll(args);
        return this;
    }

    /**
     * Adds an environment variable for the command.
     *
     * @param name  the variable name, must not be null
     * @param value the variable value, must not be null
     * @return this instance
     * @throws NullPointerException if name or value is null
     */
    public ProcessExecutor env(@NonNull String name, @NonNull String value) {
        Objects.requireNonNull(name, "environment variable name must not be null");
        Objects.requireNonNull(value, "environment variable value must not be null");
        env_.put(name, value);
        return this;
    }

    /**
     * Adds environment variables for the command.
     *
     * @param vars the map of environment variables, must not be null
     * @return this instance
     * @throws NullPointerException if vars is null
     */
    public ProcessExecutor env(@NonNull Map<String, String> vars) {
        Objects.requireNonNull(vars, "environment variables map must not be null");
        env_.putAll(vars);
        return this;
    }

    /**
     * Returns the mutable environment variables map.
     *
     * @return the environment map, never null
     */
    public Map<String, String> env() {
        return env_;
    }

    /**
     * Executes the command and returns the result.
     *
     * @return the process result containing exit code and captured output
     * @throws IOException           if the process cannot be started
     * @throws InterruptedException  if the thread is interrupted while waiting
     * @throws IllegalStateException if no command is set, the working directory is invalid,
     *                               or both {@link #inheritIO()} and {@link #outputConsumer(Consumer)} are configured
     */
    public ProcessResult execute() throws IOException, InterruptedException {
        validatePreconditions();

        var pb = createProcessBuilder();
        var outputLines = new ArrayList<String>();
        @SuppressWarnings("PMD.CloseResource")
        Process proc = null;
        Thread outputThread = null;
        boolean timedOut = false;

        try {
            proc = pb.start();
            outputThread = startOutputReader(proc, outputLines);
            boolean finished = proc.waitFor(timeout_, TimeUnit.SECONDS);

            if (!finished) {
                timedOut = true;
            }

            if (outputThread != null) {
                outputThread.join(timedOut ? 500 : 10_000);
            }

            int exitCode = timedOut ? -1 : proc.exitValue();
            return new ProcessResult(exitCode, joinLines(outputLines), timedOut);
        } finally {
            cleanupProcess(proc);
            cleanupThread(outputThread);
        }
    }

    /**
     * Configures whether the child process should inherit the I/O streams of the current JVM.
     * <p>
     * When {@code true}, the child process uses the same stdin, stdout, and stderr as the current
     * Java process. Output is <em>not</em> captured; {@link ProcessResult#output()} will return
     * an empty string in this mode.
     * <p>
     * When {@code false} (the default), stdout and stderr are merged and captured. Stdin receives EOF.
     * <p>
     * Cannot be used with {@link #outputConsumer(Consumer)}.
     *
     * @param inheritIO {@code true} to inherit I/O, {@code false} to capture output
     * @return this instance
     */
    public ProcessExecutor inheritIO(boolean inheritIO) {
        inheritIO_ = inheritIO;
        return this;
    }

    /**
     * Returns whether the child process inherits the I/O streams of the current JVM.
     *
     * @return {@code true} if I/O is inherited, {@code false} if output is captured
     */
    public boolean inheritIO() {
        return inheritIO_;
    }

    /**
     * Sets a consumer to receive output lines as they arrive.
     * <p>
     * Only effective when {@link #inheritIO()} is {@code false}. The consumer is called from a
     * background thread. Setting this implies output should be captured, not inherited.
     *
     * @param consumer the output consumer, or null to disable
     * @return this instance
     */
    public ProcessExecutor outputConsumer(Consumer<String> consumer) {
        outputConsumer_ = consumer;
        return this;
    }

    /**
     * Configure the command timeout in seconds.
     *
     * @param timeout the timeout, must be greater than 0
     * @return this instance
     * @throws IllegalArgumentException if timeout is less than or equal to 0
     */
    public ProcessExecutor timeout(int timeout) {
        if (timeout <= 0) {
            throw new IllegalArgumentException("timeout must be > 0");
        }
        timeout_ = timeout;
        return this;
    }

    /**
     * Returns the command timeout in seconds.
     *
     * @return the timeout
     */
    public int timeout() {
        return timeout_;
    }

    /**
     * Configures the working directory.
     *
     * @param dir the directory, must not be null
     * @return this instance
     * @throws NullPointerException if dir is null
     */
    public ProcessExecutor workDir(@NonNull File dir) {
        Objects.requireNonNull(dir, "directory must not be null");
        workDir_ = dir;
        return this;
    }

    /**
     * Configures the working directory.
     *
     * @param dir the directory, must not be null
     * @return this instance
     * @throws NullPointerException if dir is null
     */
    public ProcessExecutor workDir(@NonNull Path dir) {
        Objects.requireNonNull(dir, "directory must not be null");
        return workDir(dir.toFile());
    }

    /**
     * Configures the working directory.
     *
     * @param dir the directory path, must not be null or empty
     * @return this instance
     * @throws IllegalArgumentException if dir is null or empty
     */
    @SuppressFBWarnings("PATH_TRAVERSAL_IN")
    public ProcessExecutor workDir(@NonNull String dir) {
        ObjectTools.requireNotEmpty(dir, "directory must not be null or empty");
        return workDir(new File(dir));
    }

    /**
     * Returns the working directory.
     *
     * @return the directory, or null if not set
     */
    @Nullable
    public File workDir() {
        return workDir_;
    }

    private void cleanupProcess(Process proc) {
        if (proc != null) {
            var handle = proc.toHandle();
            handle.descendants().forEach(ProcessHandle::destroyForcibly);
            handle.destroyForcibly();
            closeQuietly(proc.getInputStream());
            closeQuietly(proc.getErrorStream());
            closeQuietly(proc.getOutputStream());
        }
    }

    private void cleanupThread(Thread outputThread) {
        if (outputThread != null && outputThread.isAlive()) {
            outputThread.interrupt();
            try {
                outputThread.join(100);
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void closeQuietly(Closeable closeable) {
        try {
            closeable.close();
        } catch (IOException ignored) {
        }
    }

    @SuppressFBWarnings("COMMAND_INJECTION")
    private ProcessBuilder createProcessBuilder() {
        var pb = new ProcessBuilder();
        pb.command(command_);
        pb.directory(workDir_);

        if (!env_.isEmpty()) {
            pb.environment().putAll(env_);
        }

        if (inheritIO_) {
            pb.inheritIO();
        } else {
            pb.redirectErrorStream(true);
            pb.redirectInput(ProcessBuilder.Redirect.from(new File(
                    SystemTools.isWindows() ? "NUL" : "/dev/null")));
        }
        return pb;
    }

    private String joinLines(Collection<String> lines) {
        return String.join(System.lineSeparator(), lines);
    }

    private Thread startOutputReader(Process proc, Collection<String> outputLines) {
        if (inheritIO_) {
            return null;
        }

        var thread = new Thread(() -> {
            try (var reader = new BufferedReader(
                    new InputStreamReader(proc.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    outputLines.add(line);
                    if (outputConsumer_ != null) {
                        outputConsumer_.accept(line);
                    }
                }
            } catch (IOException ignored) {
            }
        });
        thread.setDaemon(true);
        thread.start();
        return thread;
    }

    private void validatePreconditions() {
        if (ObjectTools.isEmpty(command_)) {
            throw new IllegalStateException("A command must be specified.");
        }
        if (!IOTools.isDirectory(workDir_)) {
            throw new IllegalStateException("A valid working directory must be specified.");
        }
        if (inheritIO_ && outputConsumer_ != null) {
            throw new IllegalStateException("Cannot use both inheritIO(true) and outputConsumer()");
        }
    }

    /**
     * Result of a process execution.
     *
     * @param exitCode the exit code, or -1 if timed out
     * @param output   the captured stdout/stderr joined with the system line separator;
     *                 empty string when {@link #inheritIO()} was {@code true}
     * @param timedOut true if the process exceeded the timeout
     */
    public record ProcessResult(int exitCode, String output, boolean timedOut) {

        /**
         * Returns true if the process exited with code 0 and did not time out.
         */
        public boolean isSuccess() {
            return exitCode == 0 && !timedOut;
        }
    }
}