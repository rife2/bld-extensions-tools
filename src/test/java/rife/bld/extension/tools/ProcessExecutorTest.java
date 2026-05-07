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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.io.TempDir;
import rife.bld.extension.testing.LoggingExtension;
import rife.bld.extension.testing.TestLogHandler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(LoggingExtension.class)
@SuppressWarnings({"PMD.AvoidDuplicateLiterals", "PMD.TooManyMethods"})
class ProcessExecutorTest {

    private static final String BAR = "bar";
    private static final String FOO = "foo";

    @SuppressWarnings("LoggerInitializedWithForeignClass")
    private static final Logger logger = Logger.getLogger(ProcessExecutor.class.getName());
    private static final TestLogHandler testLogHandler = new TestLogHandler();

    @RegisterExtension
    @SuppressWarnings("unused")
    private static final LoggingExtension loggingExtension = new LoggingExtension(
            logger,
            testLogHandler,
            Level.ALL
    );

    private ProcessExecutor createBasicExecutor(File workDir) {
        return new ProcessExecutor().workDir(workDir);
    }

    @SuppressWarnings("SameParameterValue")
    private List<String> echoCommand(String... args) {
        return SystemTools.isWindows()
                ? List.of("cmd", "/c", "echo", String.join(" ", args))
                : List.of("echo", String.join(" ", args));
    }

    @BeforeEach
    void setUp() {
        testLogHandler.clear();
    }

    @SuppressWarnings("PMD.AvoidUsingHardCodedIP")
    private List<String> sleepCommand() {
        return SystemTools.isWindows()
                ? List.of("cmd", "/c", "ping -n 12 127.0.0.1") // single arg
                : List.of("sleep", "5");
    }

    private List<String> multiLineEchoCommand() {
        return SystemTools.isWindows()
                ? List.of("cmd", "/c", "echo line1 & echo line2") // single arg
                : List.of("sh", "-c", "echo line1; echo line2");
    }

    @SuppressWarnings("SameParameterValue")
    private List<String> envEchoCommand(String var) {
        return SystemTools.isWindows()
                ? List.of("cmd", "/c", "echo %" + var + "%") // single arg
                : List.of("sh", "-c", "echo $" + var);
    }

    @SuppressWarnings("SameParameterValue")
    private List<String> exitCommand(int code) {
        return SystemTools.isWindows()
                ? List.of("cmd", "/c", "exit " + code) // single arg
                : List.of("sh", "-c", "exit " + code);
    }

    @Nested
    @DisplayName("Command Tests")
    class CommandTests {

        @Test
        void commandCollection(@TempDir Path tmp) {
            var executor = createBasicExecutor(tmp.toFile()).command(List.of(FOO, BAR));
            assertEquals(List.of(FOO, BAR), executor.command());
        }

        @Test
        void commandEmptyElementThrows(@TempDir Path tmp) {
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> createBasicExecutor(tmp.toFile()).command("echo", ""));
            assertTrue(ex.getMessage().contains(ProcessExecutor.COMMAND_NOT_VALID));
        }

        @Test
        void commandMutableList(@TempDir Path tmp) {
            var exec = createBasicExecutor(tmp.toFile()).command("git", "status");
            exec.command().add("--verbose");
            assertEquals(List.of("git", "status", "--verbose"), exec.command());
        }

        @Test
        @SuppressWarnings("DataFlowIssue")
        void commandNullArrayThrows(@TempDir Path tmp) {
            assertThrows(IllegalArgumentException.class,
                    () -> createBasicExecutor(tmp.toFile()).command((String[]) null));
        }

        @Test
        @SuppressWarnings("DataFlowIssue")
        void commandNullCollectionThrows(@TempDir Path tmp) {
            assertThrows(IllegalArgumentException.class,
                    () -> createBasicExecutor(tmp.toFile()).command((Collection<String>) null));
        }

        @Test
        void commandNullElementThrows(@TempDir Path tmp) {
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> createBasicExecutor(tmp.toFile()).command("echo", null));
            assertTrue(ex.getMessage().contains(ProcessExecutor.COMMAND_NOT_VALID));
        }

        @Test
        void commandVarargs(@TempDir Path tmp) {
            var executor = createBasicExecutor(tmp.toFile()).command(FOO, BAR);
            assertEquals(List.of(FOO, BAR), executor.command());
        }
    }

    @Nested
    @DisplayName("Environment Tests")
    class EnvironmentTests {

        @Test
        void envMap(@TempDir Path tmp) {
            var exec = createBasicExecutor(tmp.toFile())
                    .env(Map.of("KEY1", "val1", "KEY2", "val2"));
            assertEquals("val1", exec.env().get("KEY1"));
            assertEquals("val2", exec.env().get("KEY2"));
        }

        @Test
        void envMutable(@TempDir Path tmp) {
            var exec = createBasicExecutor(tmp.toFile()).env("FOO", "bar");
            exec.env().put("BAZ", "qux");
            assertEquals("qux", exec.env().get("BAZ"));
        }

        @Test
        @SuppressWarnings("DataFlowIssue")
        void envNullKeyThrows(@TempDir Path tmp) {
            assertThrows(NullPointerException.class,
                    () -> createBasicExecutor(tmp.toFile()).env(null, "value"));
        }

        @Test
        @SuppressWarnings("DataFlowIssue")
        void envNullValueThrows(@TempDir Path tmp) {
            assertThrows(NullPointerException.class,
                    () -> createBasicExecutor(tmp.toFile()).env("KEY", null));
        }

        @Test
        void envPassedToProcess(@TempDir Path tmp) throws Exception {
            createBasicExecutor(tmp.toFile())
                    .command(envEchoCommand("TEST_VAR"))
                    .env("TEST_VAR", "hello_env")
                    .outputConsumer(logger::info)
                    .execute();

            assertTrue(testLogHandler.containsMessage("hello_env"));
        }

        @Test
        void envSingle(@TempDir Path tmp) {
            var exec = createBasicExecutor(tmp.toFile()).env("KEY", "value");
            assertEquals("value", exec.env().get("KEY"));
        }
    }

    @Nested
    @DisplayName("Cleanup Tests")
    class CleanupTests {

        private List<String> compileAndGetRunCommand(@TempDir Path tmp, String className, String code)
                throws Exception {
            var javaFile = tmp.resolve(className + ".java");
            Files.writeString(javaFile, code);

            // Compile using ProcessExecutor itself
            var compileResult = new ProcessExecutor()
                    .workDir(tmp.toFile())
                    .command("javac", javaFile.toString())
                    .timeout(10)
                    .execute();

            assertTrue(compileResult.isSuccess(), "Test helper should compile: " + compileResult.output());
            return List.of("java", "-cp", tmp.toString(), className);
        }

        @Test
        @DisplayName("cleanupThread interrupts reader on timeout")
        void cleanupThreadInterruptedOnTimeout(@TempDir Path tmp) throws Exception {
            var interrupted = new AtomicBoolean(false);
            var started = new CountDownLatch(1);

            var runCmd = compileAndGetRunCommand(tmp, "Blocker", """
            public class Blocker {
                public static void main(String[] args) throws Exception {
                    System.out.println("started");
                    System.out.flush();
                    Thread.sleep(10000);
                }
            }
            """);

            var result = createBasicExecutor(tmp.toFile())
                    .command(runCmd)
                    .timeout(1)
                    .outputConsumer(line -> {
                        if ("started".equals(line)) {
                            started.countDown();
                            try {
                                Thread.sleep(10_000);
                            } catch (InterruptedException e) {
                                interrupted.set(true);
                                Thread.currentThread().interrupt();
                            }
                        }
                    })
                    .execute();

            assertTrue(started.await(5, TimeUnit.SECONDS), "Consumer should have started");
            assertTrue(result.timedOut());
            assertTrue(interrupted.get(), "Reader thread should be interrupted by cleanupThread()");
        }

        @Test
        void executeTimeout(@TempDir Path tmp) throws Exception {
            var runCmd = compileAndGetRunCommand(tmp, "Sleeper", """
            public class Sleeper {
                public static void main(String[] args) throws Exception {
                    Thread.sleep(10000);
                }
            }
            """);

            var result = createBasicExecutor(tmp.toFile())
                    .command(runCmd)
                    .timeout(1)
                    .execute();

            assertTrue(result.timedOut());
            assertEquals(-1, result.exitCode());
            assertFalse(result.isSuccess());
        }
    }

    @Nested
    @DisplayName("Execution Tests")
    class ExecutionTests {

        @Test
        void executeFailure(@TempDir Path tmp) throws Exception {
            var result = createBasicExecutor(tmp.toFile())
                    .command(exitCommand(1))
                    .execute();

            assertFalse(result.isSuccess());
            assertEquals(1, result.exitCode());
            assertFalse(result.timedOut());
        }

        @Test
        void executeInvalidCommandThrows(@TempDir Path tmp) {
            assertThrows(IOException.class, () -> createBasicExecutor(tmp.toFile())
                    .command("this_command_does_not_exist_12345")
                    .execute());
        }

        @Test
        void executeSuccess(@TempDir Path tmp) throws Exception {
            var result = createBasicExecutor(tmp.toFile())
                    .command(echoCommand(FOO))
                    .outputConsumer(logger::info)
                    .execute();

            assertTrue(result.isSuccess());
            assertEquals(0, result.exitCode());
            assertFalse(result.timedOut());
            assertTrue(testLogHandler.containsMessage(FOO));
        }

        @Test
        void executeTimeout(@TempDir Path tmp) throws Exception {
            var result = createBasicExecutor(tmp.toFile())
                    .command(sleepCommand())
                    .timeout(1)
                    .execute();

            assertTrue(result.timedOut());
            assertEquals(-1, result.exitCode());
            assertFalse(result.isSuccess());
        }

        @Test
        void executeWithoutCommandThrows(@TempDir Path tmp) {
            var ex = assertThrows(IllegalStateException.class,
                    () -> createBasicExecutor(tmp.toFile()).execute());
            assertTrue(ex.getMessage().contains("A command must be specified"));
        }

        @Test
        void executeWithoutWorkDirThrows() {
            var ex = assertThrows(IllegalStateException.class,
                    () -> new ProcessExecutor().command("echo", FOO).execute());
            assertTrue(ex.getMessage().contains("A valid working directory must be specified"));
        }
    }

    @Nested
    @DisplayName("Fluent API Tests")
    class FluentApiTests {

        @Test
        void chainedCallsReturnSameInstance(@TempDir Path tmp) {
            var exec = new ProcessExecutor();
            assertSame(exec, exec.workDir(tmp.toFile()));
            assertSame(exec, exec.command("echo"));
            assertSame(exec, exec.env("K", "V"));
            assertSame(exec, exec.timeout(10));
            assertSame(exec, exec.inheritIO(true));
        }
    }

    @Nested
    @DisplayName("Timeout Tests")
    class TimeoutTests {

        @Test
        void timeoutDefaultIs30(@TempDir Path tmp) {
            assertEquals(30, createBasicExecutor(tmp.toFile()).timeout());
        }

        @Test
        void timeoutNegativeThrows(@TempDir Path tmp) {
            assertThrows(IllegalArgumentException.class,
                    () -> createBasicExecutor(tmp.toFile()).timeout(-1));
        }

        @Test
        void timeoutSetterGetter(@TempDir Path tmp) {
            var exec = createBasicExecutor(tmp.toFile()).timeout(5);
            assertEquals(5, exec.timeout());
        }

        @Test
        void timeoutZeroThrows(@TempDir Path tmp) {
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> createBasicExecutor(tmp.toFile()).timeout(0));
            assertEquals("timeout must be > 0", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("I/O Tests")
    class IOTests {

        @Test
        void inheritIOFalseCapturesOutput(@TempDir Path tmp) throws Exception {
            var result = createBasicExecutor(tmp.toFile())
                    .command(echoCommand(FOO))
                    .inheritIO(false)
                    .outputConsumer(logger::info)
                    .execute();

            assertTrue(testLogHandler.containsMessage(FOO));
            assertFalse(result.output().isEmpty());
        }

        @Test
        void inheritIOGetterSetter(@TempDir Path tmp) {
            var exec = createBasicExecutor(tmp.toFile());
            assertFalse(exec.inheritIO()); // default false
            assertSame(exec, exec.inheritIO(true));
            assertTrue(exec.inheritIO());
        }

        @Test
        void inheritIOTrueDoesNotCapture(@TempDir Path tmp) throws Exception {
            var result = createBasicExecutor(tmp.toFile())
                    .command(echoCommand(FOO))
                    .inheritIO(true)
                    .execute();

            assertTrue(result.output().isEmpty());  // this is the real contract
            assertEquals(0, result.exitCode());
        }

        @Test
        void outputConsumerNotCalledWithInheritIO(@TempDir Path tmp) throws Exception {
            var count = new AtomicInteger(0);
            var result = createBasicExecutor(tmp.toFile())
                    .command(echoCommand(FOO))
                    .inheritIO(true)  // this must be last
                    .execute();

            assertEquals(0, count.get()); // no consumer was set
            assertTrue(result.output().isEmpty());
            assertEquals(0, result.exitCode());
        }

        @Test
        void outputConsumerReceivesLines(@TempDir Path tmp) throws Exception {
            createBasicExecutor(tmp.toFile())
                    .command(multiLineEchoCommand())
                    .outputConsumer(logger::info)
                    .execute();

            assertTrue(testLogHandler.containsMessage("line1"));
            assertTrue(testLogHandler.containsMessage("line2"));
        }
    }

    @Nested
    @DisplayName("WorkDir Tests")
    class WorkDirTests {

        @Test
        void workDirAsFile(@TempDir Path tmp) {
            var exec = new ProcessExecutor().workDir(tmp.toFile());
            assertEquals(tmp.toFile(), exec.workDir());
        }

        @Test
        void workDirAsPath(@TempDir Path tmp) {
            var exec = new ProcessExecutor().workDir(tmp);
            assertEquals(tmp.toFile(), exec.workDir());
        }

        @Test
        void workDirAsString(@TempDir Path tmp) {
            var exec = new ProcessExecutor().workDir(tmp.toString());
            assertEquals(tmp.toFile(), exec.workDir());
        }

        @Test
        void workDirEmptyStringThrows() {
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> new ProcessExecutor().workDir(""));
            assertTrue(ex.getMessage().contains("directory must not be null or empty"));
        }

        @Test
        @SuppressWarnings("DataFlowIssue")
        void workDirNullThrows() {
            assertThrows(NullPointerException.class,
                    () -> new ProcessExecutor().workDir((File) null));
        }

        @Test
        @EnabledOnOs({OS.LINUX, OS.MAC})
        void workDirUsedByProcessUnix(@TempDir Path tmp) throws Exception {
            var testFile = tmp.resolve("test.txt");
            Files.writeString(testFile, "content");

            createBasicExecutor(tmp.toFile())
                    .command("cat", "test.txt")
                    .outputConsumer(logger::info)
                    .execute();

            assertTrue(testLogHandler.containsMessage("content"));
        }

        @Test
        @EnabledOnOs(OS.WINDOWS)
        void workDirUsedByProcessWindows(@TempDir Path tmp) throws Exception {
            var testFile = tmp.resolve("test.txt");
            Files.writeString(testFile, "content");

            createBasicExecutor(tmp.toFile())
                    .command("cmd", "/c", "type", "test.txt")
                    .outputConsumer(logger::info)
                    .execute();

            assertTrue(testLogHandler.containsMessage("content"));
        }
    }
}