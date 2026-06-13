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
import rife.bld.extension.testing.VisibleForTesting;

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
 * <p>
 * This class is not thread-safe. Configure and execute from a single thread.
 *
 * @author <a href="https://erik.thauvin.net/">Erik C. Thauvin</a>
 * @since 1.0
 */
@SuppressFBWarnings(value = "EI_EXPOSE_REP", justification = "intentional and documented")
public class ProcessExecutor {

    /**
     * Default timeout in seconds.
     */
    public static final long DEFAULT_TIMEOUT_SECONDS = 30L;

    private final List<String> command_ = new ArrayList<>();
    private final Map<String, String> env_ = new HashMap<>();
    private boolean inheritIO_;
    @Nullable
    private Consumer<String> outputConsumer_;
    private long timeout_ = DEFAULT_TIMEOUT_SECONDS;
    private File workDir_;

    /**
     * Sets the command and arguments to be executed, replacing any previously configured command.
     *
     * @param args one or more arguments, must not be null or contain null/empty elements.
     *             The first element must be a non-blank program name.
     * @return this instance
     * @throws NullPointerException     if args is null
     * @throws IllegalArgumentException if args contains null/empty elements.
     */
    public ProcessExecutor command(@NonNull String... args) {
        ObjectTools.requireNotEmpty(args, "command");
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
     * @param args the list of arguments, must not be null or contain null/empty elements.
     *             The first element must be a non-blank program name.
     * @return this instance
     * @throws NullPointerException     if args is null
     * @throws IllegalArgumentException if args contains null/empty elements
     */
    public ProcessExecutor command(@NonNull Collection<String> args) {
        ObjectTools.requireNotEmpty(args, "command");
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
        ObjectTools.requireNonNull(vars, "environment variables");
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

            boolean finished;
            if (timeout_ < 0) {
                proc.waitFor();
                finished = true;
            } else {
                finished = proc.waitFor(timeout_, TimeUnit.SECONDS);
            }

            if (!finished) {
                timedOut = true;
            }

            if (outputThread != null) {
                outputThread.join(timedOut ? 5_000 : 10_000);
            }

            int exitCode;
            try {
                exitCode = proc.exitValue();
            } catch (IllegalThreadStateException e) {
                exitCode = -1;
                timedOut = true;
            }
            return new ProcessResult(exitCode, String.join(System.lineSeparator(), outputLines), timedOut);
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
     * <p>
     * Exceptions thrown by the consumer are silently ignored to protect the output reader thread.
     * Ensure the consumer handles its own errors if reliable delivery is required.
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
     * <p>
     * Use a negative value to disable the timeout and wait indefinitely.
     * <p>
     * A value of 0 is invalid as it would cause the process to fail immediately if not completed instantly.
     * <p>
     * Default: {@link #DEFAULT_TIMEOUT_SECONDS}
     *
     * @param timeout the timeout in seconds; use negative value for no timeout
     * @return this instance
     * @throws IllegalArgumentException if timeout is 0
     */
    public ProcessExecutor timeout(long timeout) {
        if (timeout == 0) {
            throw new IllegalArgumentException("timeout 0 is ambiguous; use negative value for no timeout");
        }
        timeout_ = timeout;
        return this;
    }

    /**
     * Returns the command timeout in seconds.
     *
     * @return the timeout
     */
    public long timeout() {
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

    // Internal helpers. Package-private to allow unit testing and keep execute() readable.

    /**
     * Cleans up the process and its descendants.
     * <p>
     * Destroys the process tree and closes all standard streams. Safe to call with null.
     */
    @VisibleForTesting
    void cleanupProcess(@Nullable Process proc) {
        if (proc == null) {
            return;
        }
        destroyProcessTree(proc.toHandle());
        closeAllStreams(proc);
    }

    /**
     * Interrupts and joins the output reader thread if still alive.
     * <p>
     * Used during cleanup to ensure background threads don't leak.
     */
    @VisibleForTesting
    void cleanupThread(Thread outputThread) {
        if (outputThread != null && outputThread.isAlive()) {
            outputThread.interrupt();
            try {
                outputThread.join(100);
            } catch (InterruptedException ignored) {
                restoreInterruptFlag();
            }
        }
    }

    /**
     * Closes all standard streams of the given process.
     * <p>
     * Safe to call even if streams are already closed.
     */
    @VisibleForTesting
    void closeAllStreams(Process proc) {
        closeQuietly(proc.getInputStream());
        closeQuietly(proc.getErrorStream());
        closeQuietly(proc.getOutputStream());
    }

    /**
     * Closes a {@code Closeable}, ignoring any {@code IOException}.
     */
    @VisibleForTesting
    void closeQuietly(Closeable closeable) {
        try {
            closeable.close();
        } catch (IOException ignored) {
        }
    }

    /**
     * Creates a configured {@code ProcessBuilder} from the current state.
     * <p>
     * Applies command, working directory, environment variables, and I/O redirection.
     * Returns a new instance on each call. Do not mutate the result.
     */
    @VisibleForTesting
    ProcessBuilder createProcessBuilder() {
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
            pb.redirectInput(ProcessBuilder.Redirect.from(new File(nullDevicePath())));
        }
        return pb;
    }

    /**
     * Destroys a process handle forcibly, ignoring {@code UnsupportedOperationException}.
     * <p>
     * Some platforms may not support process tree operations.
     */
    @VisibleForTesting
    void destroyHandleQuietly(ProcessHandle handle) {
        try {
            handle.destroyForcibly();
        } catch (UnsupportedOperationException ignored) {
        }
    }

    /**
     * Destroys the process and all its descendants.
     * <p>
     * Attempts graceful cleanup on platforms that support it.
     */
    @VisibleForTesting
    void destroyProcessTree(ProcessHandle handle) {
        try {
            handle.descendants().forEach(this::destroyHandleQuietly);
            destroyHandleQuietly(handle);
        } catch (UnsupportedOperationException ignored) {
        }
    }

    /**
     * Notifies the output consumer of a new line, if configured.
     * <p>
     * Consumer exceptions are swallowed to protect the reader thread.
     */
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    @VisibleForTesting
    void notifyOutputConsumer(String line) {
        if (outputConsumer_ != null) {
            try {
                outputConsumer_.accept(line);
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * Returns the platform-specific null device path.
     * <p>
     * Used to redirect stdin when output is captured.
     */
    @VisibleForTesting
    String nullDevicePath() {
        return SystemTools.isWindows() ? "NUL" : "/dev/null";
    }

    /**
     * Restores the current thread's interrupt flag.
     */
    @VisibleForTesting
    void restoreInterruptFlag() {
        Thread.currentThread().interrupt();
    }

    /**
     * Starts the background thread that reads process output.
     * <p>
     * Returns {@code null} if {@link #inheritIO()} is {@code true}. Otherwise reads
     * stdout/stderr line by line, appending to {@code outputLines} and notifying
     * the {@link #outputConsumer(Consumer)} if set.
     *
     * @param proc        the process to read from
     * @param outputLines collection to append captured lines to
     * @return the started thread, or null if I/O is inherited
     */
    @VisibleForTesting
    Thread startOutputReader(Process proc, Collection<String> outputLines) {
        if (inheritIO_) {
            return null;
        }

        var thread = new Thread(() -> {
            try (var reader = new BufferedReader(
                    new InputStreamReader(proc.getInputStream(), StandardCharsets.UTF_8))) {
                reader.lines().forEach(line -> {
                    outputLines.add(line);
                    notifyOutputConsumer(line);
                });
            } catch (IOException ignored) {
            }
        }, "process-executor-output");
        thread.setDaemon(true);
        thread.start();
        return thread;
    }

    /**
     * Validates preconditions before process execution.
     * <p>
     * Ensures a command is set, the working directory exists if specified,
     * and I/O configuration is valid.
     *
     * @throws IllegalStateException if preconditions are not met
     */
    @VisibleForTesting
    void validatePreconditions() {
        if (ObjectTools.isEmpty(command_)) {
            throw new IllegalStateException("A command must be specified.");
        }

        if (workDir_ != null && !IOTools.isDirectory(workDir_)) {
            throw new IllegalStateException("A valid working directory must be specified.");
        }
        if (inheritIO_ && outputConsumer_ != null) {
            throw new IllegalStateException("Cannot use both inheritIO(true) and outputConsumer()");
        }
    }

    /**
     * Result of a process execution.
     *
     * @param exitCode the exit code, or -1 if timed out or process did not terminate
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