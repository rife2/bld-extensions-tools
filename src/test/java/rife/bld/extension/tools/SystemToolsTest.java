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

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("System Tools Tests")
@SuppressWarnings({"PMD.AvoidDuplicateLiterals", "PMD.TooManyStaticImports"})
class SystemToolsTest {

    private static Function<String, String> createEnvProvider(Map<String, String> env) {
        return env::get;
    }

    /**
     * Builds an environment map from alternating key/value pairs.
     * <p>
     * Preferred over {@link Map#of} for test fixtures because it:
     * <ul>
     *   <li>is not limited to 10 pairs,</li>
     *   <li>does not throw on accidental duplicate keys,</li>
     *   <li>reads naturally as {@code env("KEY1", "val1", "KEY2", "val2")}.</li>
     * </ul>
     */
    private static Map<String, String> env(String... keyValues) {
        if (keyValues.length % 2 != 0) {
            throw new IllegalArgumentException("env() requires an even number of arguments");
        }
        var map = new HashMap<String, String>();
        for (int i = 0; i < keyValues.length; i += 2) {
            map.put(keyValues[i], keyValues[i + 1]);
        }
        return map;
    }

    @Nested
    @DisplayName("AIX Detection Tests")
    class AixTests {

        @ParameterizedTest
        @ValueSource(strings = {"aix", "AIX", "Aix", "AIX 7.1"})
        @DisplayName("Should detect AIX")
        void shouldDetectAix(String osName) {
            assertTrue(SystemTools.isAix(osName));
        }

        @ParameterizedTest
        @ValueSource(strings = {"linux", "windows", "mac os x", "darwin"})
        @DisplayName("Should reject non-AIX systems")
        void shouldRejectNonAix(String osName) {
            assertFalse(SystemTools.isAix(osName));
        }

        @Test
        @EnabledOnOs(OS.AIX)
        @DisplayName("Should verify current system is AIX")
        void shouldVerifyCurrentSystemIsAix() {
            assertTrue(SystemTools.isAix());
        }
    }

    @Nested
    @DisplayName("Architecture Detection Tests")
    class ArchitectureTests {

        @Test
        @DisplayName("arch() should return current architecture")
        void archShouldReturnCurrentArch() {
            var arch = SystemTools.arch();
            assertNotNull(arch);
            assertEquals(System.getProperty("os.arch", "").toLowerCase(Locale.ROOT), arch);
            // Not guaranteed by the JVM spec, but true for all mainstream JVMs.
            // Use an assumption so unusual JVMs skip rather than false-fail.
            Assumptions.assumeFalse(arch.contains(" "),
                    "os.arch contains a space on this JVM; skipping single-token check");
        }

        @Test
        @DisplayName("isArm() should work for current system")
        void isArmShouldWorkForCurrentSystem() {
            assertEquals(SystemTools.isArm32() || SystemTools.isArm64(), SystemTools.isArm());
        }

        @Test
        @DisplayName("isX86Family() should work for current system")
        void isX86FamilyShouldWorkForCurrentSystem() {
            assertEquals(SystemTools.isX86() || SystemTools.isX64(), SystemTools.isX86Family());
        }

        @ParameterizedTest
        @ValueSource(strings = {"arm", "arm32", "armv7", "armv8"})
        @DisplayName("Should detect ARM 32-bit architectures")
        void shouldDetectArm32(String arch) {
            assertTrue(SystemTools.isArm32(arch));
        }

        @ParameterizedTest
        @ValueSource(strings = {"aarch64", "arm64"})
        @DisplayName("Should detect ARM 64-bit architectures")
        void shouldDetectArm64(String arch) {
            assertTrue(SystemTools.isArm64(arch));
        }

        @ParameterizedTest
        @ValueSource(strings = {"amd64", "x86_64"})
        @DisplayName("Should detect x64 architectures")
        void shouldDetectX64(String arch) {
            assertTrue(SystemTools.isX64(arch));
        }

        @ParameterizedTest
        @ValueSource(strings = {"x86", "i386", "i486", "i586", "i686"})
        @DisplayName("Should detect x86 architectures")
        void shouldDetectX86(String arch) {
            assertTrue(SystemTools.isX86(arch));
        }
    }

    @Nested
    @DisplayName("Cygwin Detection Tests")
    class CygwinTests {

        static Stream<Arguments> provideCygwinEnvironments() {
            return Stream.of(
                    Arguments.of("PATH with /cygdrive/", env("PATH", "/usr/bin:/cygdrive/c/Windows")),
                    Arguments.of("PATH with both indicators", env("PATH", "/cygdrive/c/Windows:/usr/bin")),
                    Arguments.of("TERM cygwin", env("TERM", "cygwin")),
                    Arguments.of("SHELL cygwin", env("SHELL", "/usr/bin/cygwin")),
                    Arguments.of("Complete Cygwin environment",
                            env("TERM", "cygwin", "SHELL", "/usr/bin/cygwin",
                                    "PATH", "/usr/bin:/cygdrive/c/Windows"))
            );
        }

        static Stream<Arguments> provideNonCygwinEnvironments() {
            return Stream.of(
                    Arguments.of("Empty environment", env()),
                    Arguments.of("Windows shell", env("SHELL", "C:\\Windows\\System32\\cmd.exe")),
                    Arguments.of("Windows PATH", env("PATH", "C:\\Windows\\System32;C:\\Program Files")),
                    Arguments.of("Windows TERM", env("TERM", "dumb")),
                    Arguments.of("PowerShell environment",
                            env("SHELL", "C:\\Windows\\System32\\WindowsPowerShell\\v1.0\\powershell.exe",
                                    "PATH", "C:\\Windows\\System32")),
                    // Bare Unix shells are NOT Cygwin markers: they are also used by
                    // MSYS2, Git Bash, and other Unix-like Windows environments.
                    Arguments.of("Bare Unix shell /bin/bash", env("SHELL", "/bin/bash")),
                    Arguments.of("Bare Unix shell /bin/sh", env("SHELL", "/bin/sh")),
                    Arguments.of("Bare Unix shell /usr/bin/zsh", env("SHELL", "/usr/bin/zsh")),
                    Arguments.of("Generic xterm terminal", env("TERM", "xterm")),
                    Arguments.of("Git Bash environment",
                            env("SHELL", "/usr/bin/bash", "TERM", "xterm",
                                    "PATH", "/mingw64/bin:/usr/bin"))
            );
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("provideCygwinEnvironments")
        @DisplayName("Should detect valid Cygwin environments")
        void shouldDetectCygwinEnvironments(String description, Map<String, String> env) {
            assertTrue(SystemTools.isCygwin("Windows 10", createEnvProvider(env)),
                    "Failed to detect Cygwin: " + description);
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("provideNonCygwinEnvironments")
        @DisplayName("Should reject non-Cygwin environments")
        void shouldRejectNonCygwinEnvironments(String description, Map<String, String> env) {
            assertFalse(SystemTools.isCygwin("Windows 10", createEnvProvider(env)),
                    "Incorrectly detected Cygwin: " + description);
        }

        @ParameterizedTest
        @ValueSource(strings = {"Linux", "Mac OS X", "FreeBSD"})
        @DisplayName("Should reject non-Windows systems even with Cygwin indicators")
        void shouldRejectNonWindowsSystems(String osName) {
            var env = env("TERM", "cygwin", "SHELL", "/usr/bin/cygwin",
                    "PATH", "/usr/bin:/cygdrive/c");
            assertFalse(SystemTools.isCygwin(osName, createEnvProvider(env)));
        }

        @ParameterizedTest
        @CsvSource({"Windows 10, true", "Windows 11, true", "Win XP, true", "windows, true",
                "Linux, false", "Mac OS X, false"})
        @DisplayName("Should require Windows OS for Cygwin detection")
        void shouldRequireWindowsForCygwin(String osName, boolean expected) {
            // TERM=cygwin is an explicit Cygwin marker (not a bare Unix shell).
            var env = env("TERM", "cygwin");
            assertEquals(expected, SystemTools.isCygwin(osName, createEnvProvider(env)));
        }

        @Test
        @DisplayName("Should use system environment")
        void shouldUseSystemEnvironment() {
            assertDoesNotThrow(() -> SystemTools.isCygwin());
        }
    }

    @Nested
    @DisplayName("Environment Edge Cases Tests")
    class EnvironmentEdgeCasesTests {

        @Test
        @DisplayName("Should detect Cygwin")
        void shouldDetectCygwin() {
            var cygwinEnv = env(
                    "PATH", "/usr/bin:/cygdrive/c/Windows",
                    "TERM", "cygwin"
            );
            var provider = createEnvProvider(cygwinEnv);
            assertTrue(SystemTools.isCygwin("Windows 10", provider));

            var nonCygwinEnv = env("PATH", "C:\\Windows\\System32");
            assertFalse(SystemTools.isCygwin("Windows 10", createEnvProvider(nonCygwinEnv)));
        }

        @Test
        @DisplayName("Should handle empty environment map")
        void shouldHandleEmptyEnvironment() {
            var emptyEnv = env();
            var provider = createEnvProvider(emptyEnv);

            assertFalse(SystemTools.isCygwin("Windows 10", provider));
            assertFalse(SystemTools.isMinGw("Windows 10", provider));
        }

        @Test
        @DisplayName("Should handle null environment variables")
        void shouldHandleNullEnvironmentVariables() {
            Function<String, String> nullProvider = ignored -> null;

            assertFalse(SystemTools.isCygwin("Windows 10", nullProvider));
            assertFalse(SystemTools.isMinGw("Windows 10", nullProvider));
        }
    }

    @Nested
    @DisplayName("FreeBSD Detection Tests")
    class FreeBsdTests {

        @ParameterizedTest
        @ValueSource(strings = {"freebsd", "FreeBSD", "FREEBSD", "FreeBSD 13.0"})
        @DisplayName("Should detect FreeBSD")
        void shouldDetectFreeBsd(String osName) {
            assertTrue(SystemTools.isFreeBsd(osName));
        }

        @ParameterizedTest
        @ValueSource(strings = {"linux", "windows", "mac os x", "openbsd"})
        @DisplayName("Should reject non-FreeBSD systems")
        void shouldRejectNonFreeBsd(String osName) {
            assertFalse(SystemTools.isFreeBsd(osName));
        }

        @Test
        @DisplayName("Should verify current system")
        void shouldVerifyCurrentSystem() {
            assertDoesNotThrow(() -> SystemTools.isFreeBsd());
        }
    }

    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {

        @Test
        @EnabledOnOs(OS.AIX)
        @DisplayName("Should detect only AIX on AIX system")
        void shouldDetectOnlyAixOnAixSystem() {
            assertTrue(SystemTools.isAix());
            assertFalse(SystemTools.isFreeBsd());
            assertFalse(SystemTools.isLinux());
            assertFalse(SystemTools.isMacOS());
            assertFalse(SystemTools.isOpenVms());
            assertFalse(SystemTools.isSolaris());
            assertFalse(SystemTools.isWindows());
            assertFalse(SystemTools.isOtherOs());
        }

        @Test
        @EnabledOnOs(OS.LINUX)
        @DisplayName("Should detect only Linux on Linux system")
        void shouldDetectOnlyLinuxOnLinuxSystem() {
            assertTrue(SystemTools.isLinux());
            assertFalse(SystemTools.isAix());
            assertFalse(SystemTools.isFreeBsd());
            assertFalse(SystemTools.isMacOS());
            assertFalse(SystemTools.isOpenVms());
            assertFalse(SystemTools.isSolaris());
            assertFalse(SystemTools.isWindows());
            assertFalse(SystemTools.isOtherOs());
        }

        @Test
        @EnabledOnOs(OS.MAC)
        @DisplayName("Should detect only macOS on macOS system")
        void shouldDetectOnlyMacOsOnMacOsSystem() {
            assertTrue(SystemTools.isMacOS());
            assertFalse(SystemTools.isAix());
            assertFalse(SystemTools.isFreeBsd());
            assertFalse(SystemTools.isLinux());
            assertFalse(SystemTools.isOpenVms());
            assertFalse(SystemTools.isSolaris());
            assertFalse(SystemTools.isWindows());
            assertFalse(SystemTools.isOtherOs());
        }

        @Test
        @EnabledOnOs(OS.OTHER)
        @DisplayName("Should detect only other OS on an unrecognized system")
        void shouldDetectOnlyOtherOsOnOtherSystem() {
            // Note: this relies on JUnit's OS.OTHER enumeration matching SystemTools'
            // known-OS set. If JUnit adds a newly-enumerated OS that SystemTools does
            // not recognize, this test will fail on that platform; update the known-OS
            // list in SystemTools accordingly.
            assertTrue(SystemTools.isOtherOs());
            assertFalse(SystemTools.isAix());
            assertFalse(SystemTools.isFreeBsd());
            assertFalse(SystemTools.isLinux());
            assertFalse(SystemTools.isMacOS());
            assertFalse(SystemTools.isOpenVms());
            assertFalse(SystemTools.isSolaris());
            assertFalse(SystemTools.isWindows());
        }

        @Test
        @EnabledOnOs(OS.SOLARIS)
        @DisplayName("Should detect only Solaris on Solaris system")
        void shouldDetectOnlySolarisOnSolarisSystem() {
            assertTrue(SystemTools.isSolaris());
            assertFalse(SystemTools.isAix());
            assertFalse(SystemTools.isFreeBsd());
            assertFalse(SystemTools.isLinux());
            assertFalse(SystemTools.isMacOS());
            assertFalse(SystemTools.isOpenVms());
            assertFalse(SystemTools.isWindows());
            assertFalse(SystemTools.isOtherOs());
        }

        @Test
        @EnabledOnOs(OS.WINDOWS)
        @DisplayName("Should detect only Windows on Windows system")
        void shouldDetectOnlyWindowsOnWindowsSystem() {
            assertTrue(SystemTools.isWindows());
            assertFalse(SystemTools.isAix());
            assertFalse(SystemTools.isFreeBsd());
            assertFalse(SystemTools.isLinux());
            assertFalse(SystemTools.isMacOS());
            assertFalse(SystemTools.isOpenVms());
            assertFalse(SystemTools.isSolaris());
            assertFalse(SystemTools.isOtherOs());
        }
    }

    @Nested
    @DisplayName("javaExecutable() Tests")
    class JavaExecutableTests {

        /**
         * Saves and restores {@code java.home} around tests that temporarily modify it,
         * keeping the JVM-global property isolated.
         */
        private static final String JAVA_HOME_KEY = "java.home";

        @Nested
        @DisplayName("Default resolution")
        class DefaultResolutionTests {

            @Test
            @DisplayName("should return an absolute path when resolved from java.home")
            void shouldBeAbsoluteWhenResolved() {
                String result = SystemTools.javaExecutable();
                if (!result.contains(File.separator)) {
                    return; // bare-name fallback: not a filesystem path
                }
                assertTrue(Path.of(result).isAbsolute(),
                        "Expected an absolute path but was: " + result);
            }

            @Test
            @DisplayName("should end with the platform-appropriate binary name")
            void shouldEndWithPlatformBinaryName() {
                String result = SystemTools.javaExecutable();
                String expectedName = SystemTools.isWindows() ? "java.exe" : "java";
                assertTrue(result.endsWith(expectedName),
                        "Expected path to end with '" + expectedName + "' but was: " + result);
            }

            @Test
            @DisplayName("should point to an executable file when java.home is valid")
            void shouldPointToExecutable() {
                // Sanity check: the running JVM's java binary should be executable
                String result = SystemTools.javaExecutable();
                if (!result.contains(File.separator)) {
                    // Fell back to the bare name; PATH resolution happens at launch,
                    // so there is nothing to assert about the filesystem here.
                    return;
                }
                assertTrue(IOTools.canExecute(Path.of(result)),
                        "Expected javaExecutable() to point to an executable: " + result);
            }

            @Test
            @DisplayName("should return a non-blank string")
            void shouldReturnNonBlank() {
                String result = SystemTools.javaExecutable();
                assertNotNull(result);
                assertFalse(result.isBlank());
            }
        }

        @Nested
        @DisplayName("Fallback behavior")
        class FallbackTests {

            private String originalJavaHome;

            @AfterEach
            void restoreJavaHome() {
                if (originalJavaHome == null) {
                    System.clearProperty(JAVA_HOME_KEY);
                } else {
                    System.setProperty(JAVA_HOME_KEY, originalJavaHome);
                }
            }

            @BeforeEach
            void saveJavaHome() {
                originalJavaHome = System.getProperty(JAVA_HOME_KEY);
            }

            @Test
            @DisplayName("should fall back to bare name when bin/java is present but not executable")
            void shouldFallBackWhenBinaryNotExecutable(@TempDir Path fakeHome) throws IOException {
                Path binDir = Files.createDirectories(fakeHome.resolve("bin"));
                String exeName = SystemTools.isWindows() ? "java.exe" : "java";
                Path fakeExe = binDir.resolve(exeName);
                Files.createFile(fakeExe);

                try {
                    if (!fakeExe.toFile().setExecutable(false)) {
                        // Could not clear the exec bit (e.g. running as root on some FSes);
                        // skip rather than produce a false failure.
                        Assumptions.assumeFalse(fakeExe.toFile().canExecute(),
                                "Cannot clear executable bit on this file system");
                    }
                } catch (SecurityException e) {
                    Assumptions.abort("Security manager denied setExecutable");
                }

                System.setProperty(JAVA_HOME_KEY, fakeHome.toString());

                String expected = SystemTools.isWindows() ? "java.exe" : "java";
                assertEquals(expected, SystemTools.javaExecutable());
            }

            @Test
            @DisplayName("should fall back to bare name when java.home is blank")
            void shouldFallBackWhenJavaHomeBlank() {
                System.setProperty(JAVA_HOME_KEY, "   ");

                String expected = SystemTools.isWindows() ? "java.exe" : "java";
                assertEquals(expected, SystemTools.javaExecutable());
            }

            @Test
            @DisplayName("should fall back to bare name when java.home points to a non-existent directory")
            void shouldFallBackWhenJavaHomeInvalid() {
                System.setProperty(JAVA_HOME_KEY, "/non/existent/java/home/12345");

                String expected = SystemTools.isWindows() ? "java.exe" : "java";
                assertEquals(expected, SystemTools.javaExecutable());
            }

            @Test
            @DisplayName("should fall back when java.home points to a regular file")
            void shouldFallBackWhenJavaHomeIsFile(@TempDir Path tempDir) throws IOException {
                Path file = tempDir.resolve("not-a-dir");
                Files.createFile(file);
                System.setProperty(JAVA_HOME_KEY, file.toString());

                String expected = SystemTools.isWindows() ? "java.exe" : "java";
                assertEquals(expected, SystemTools.javaExecutable());
            }

            @Test
            @DisplayName("should fall back to bare name when java.home is unset")
            void shouldFallBackWhenJavaHomeUnset() {
                System.clearProperty(JAVA_HOME_KEY);

                String expected = SystemTools.isWindows() ? "java.exe" : "java";
                assertEquals(expected, SystemTools.javaExecutable());
            }
        }

        @Nested
        @DisplayName("Platform-specific behavior")
        class PlatformTests {

            @Test
            @DisabledOnOs(OS.WINDOWS)
            @DisplayName("on Unix-like OS should use java (no .exe) when resolved from java.home")
            void shouldUseBareNameOnUnix() {
                String result = SystemTools.javaExecutable();
                assertTrue(result.endsWith("java"),
                        "Expected path to end with 'java' but was: " + result);
                assertFalse(result.endsWith("java.exe"),
                        "Did not expect 'java.exe' on Unix-like OS but was: " + result);
            }

            @Test
            @EnabledOnOs(OS.WINDOWS)
            @DisplayName("on Windows should use java.exe when resolved from java.home")
            void shouldUseExeOnWindows() {
                String result = SystemTools.javaExecutable();
                assertTrue(result.endsWith("java.exe"),
                        "Expected 'java.exe' on Windows but was: " + result);
            }
        }
    }

    @Nested
    @DisplayName("Linux Detection Tests")
    class LinuxTests {

        @ParameterizedTest
        @ValueSource(strings = {"linux", "Linux", "LINUX", "linux 5.15"})
        @DisplayName("Should detect Linux systems")
        void shouldDetectLinux(String osName) {
            assertTrue(SystemTools.isLinux(osName));
        }

        @ParameterizedTest
        @ValueSource(strings = {"windows", "mac os x", "darwin"})
        @DisplayName("Should reject non-Linux systems")
        void shouldRejectNonLinux(String osName) {
            assertFalse(SystemTools.isLinux(osName));
        }

        @Test
        @EnabledOnOs(OS.LINUX)
        @DisplayName("Should verify current system is Linux")
        void shouldVerifyCurrentSystemIsLinux() {
            assertTrue(SystemTools.isLinux());
        }
    }

    @Nested
    @DisplayName("macOS Detection Tests")
    class MacOsTests {

        @ParameterizedTest
        @ValueSource(strings = {"mac os x", "Mac OS X", "MAC OS X", "macos", "macOS", "MACOS",
                "darwin", "Darwin", "DARWIN", "osx", "OSX"})
        @DisplayName("Should detect macOS variants")
        void shouldDetectMacOsVariants(String osName) {
            assertTrue(SystemTools.isMacOS(osName));
        }

        @ParameterizedTest
        @ValueSource(strings = {"windows", "linux", "freebsd"})
        @DisplayName("Should reject non-macOS systems")
        void shouldRejectNonMacOs(String osName) {
            assertFalse(SystemTools.isMacOS(osName));
        }

        @Test
        @EnabledOnOs(OS.MAC)
        @DisplayName("Should verify current system is macOS")
        void shouldVerifyCurrentSystemIsMacOs() {
            assertTrue(SystemTools.isMacOS());
        }
    }

    @Nested
    @DisplayName("MinGW Detection Tests")
    class MingwTests {

        static Stream<Arguments> provideMingwEnvironments() {
            return Stream.of(
                    Arguments.of("MSYSTEM=MINGW64", env("MSYSTEM", "MINGW64")),
                    Arguments.of("MSYSTEM=MINGW32", env("MSYSTEM", "MINGW32")),
                    Arguments.of("MSYSTEM=MSYS", env("MSYSTEM", "MSYS")),
                    // MSYS2 environments: UCRT and CLANG variants are valid MinGW-family.
                    Arguments.of("MSYSTEM=UCRT64", env("MSYSTEM", "UCRT64")),
                    Arguments.of("MSYSTEM=CLANG64", env("MSYSTEM", "CLANG64")),
                    Arguments.of("MSYSTEM=CLANGARM64", env("MSYSTEM", "CLANGARM64")),
                    Arguments.of("MINGW_PREFIX", env("MINGW_PREFIX", "/mingw64")),
                    Arguments.of("MINGW_CHOST", env("MINGW_CHOST", "x86_64-w64-mingw32")),
                    Arguments.of("Both MINGW variables",
                            env("MINGW_PREFIX", "/mingw64", "MINGW_CHOST", "x86_64-w64-mingw32")),
                    Arguments.of("MinGW paths with shell",
                            env("PATH", "/mingw64/bin:/usr/bin", "SHELL", "/bin/bash")),
                    Arguments.of("Unix-style MSYS paths with shell",
                            env("PATH", "/msys64/usr/bin:/usr/bin", "SHELL", "/bin/sh")),
                    Arguments.of("Windows-style MSYS paths with shell",
                            env("PATH", "C:\\msys64\\usr\\bin", "SHELL", "/bin/sh")),
                    Arguments.of("Windows-style MinGW paths",
                            env("PATH", "C:\\mingw64\\bin;C:\\Windows", "SHELL", "/bin/bash")),
                    Arguments.of("Complete MinGW environment",
                            env("MSYSTEM", "MINGW64", "MINGW_PREFIX", "/mingw64",
                                    "MINGW_CHOST", "x86_64-w64-mingw32", "SHELL", "/bin/bash",
                                    "PATH", "/mingw64/bin:/usr/bin"))
            );
        }

        static Stream<Arguments> provideNonMingwEnvironments() {
            return Stream.of(
                    Arguments.of("Empty environment", env()),
                    Arguments.of("Windows PATH only", env("PATH", "C:\\Windows\\System32;C:\\Program Files")),
                    Arguments.of("MinGW paths without shell", env("PATH", "/mingw64/bin")),
                    Arguments.of("Shell without MinGW paths", env("SHELL", "/bin/bash")),
                    Arguments.of("CMD environment",
                            env("SHELL", "C:\\Windows\\System32\\cmd.exe", "PATH", "C:\\Windows\\System32")),
                    Arguments.of("PowerShell environment",
                            env("PATH", "C:\\Windows\\System32;C:\\Program Files",
                                    "SHELL", "C:\\Windows\\System32\\WindowsPowerShell\\v1.0\\powershell.exe")),
                    Arguments.of("MSYSTEM=MSVC", env("MSYSTEM", "MSVC")),
                    Arguments.of("MSYSTEM=FOO", env("MSYSTEM", "FOO"))
            );
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("provideMingwEnvironments")
        @DisplayName("Should detect valid MinGW environments")
        void shouldDetectMingwEnvironments(String description, Map<String, String> env) {
            assertTrue(SystemTools.isMinGw("Windows 10", createEnvProvider(env)),
                    "Failed to detect MinGW: " + description);
        }

        @Test
        @DisplayName("Should distinguish MinGW from Cygwin")
        void shouldDistinguishMinGwFromCygwin() {
            var mingwEnv = env("MSYSTEM", "MINGW64", "MINGW_PREFIX", "/mingw64", "SHELL", "/bin/bash");
            var mingwProvider = createEnvProvider(mingwEnv);

            // Explicit Cygwin marker; bare Unix shells are no longer a Cygwin signal.
            var cygwinEnv = env("TERM", "cygwin");
            var cygwinProvider = createEnvProvider(cygwinEnv);

            assertTrue(SystemTools.isMinGw("Windows 10", mingwProvider));
            assertFalse(SystemTools.isCygwin("Windows 10", mingwProvider));
            assertFalse(SystemTools.isMinGw("Windows 10", cygwinProvider));
            assertTrue(SystemTools.isCygwin("Windows 10", cygwinProvider));
        }

        @Test
        @DisplayName("Should handle case-insensitive MSYSTEM values")
        @SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
        void shouldHandleCaseInsensitiveMsystem() {
            var testCases = env(
                    "MINGW64", "true",
                    "MSYS", "true",
                    "mingw_MINGW64", "true",
                    "mingw64", "true",
                    "MiNgW64", "true",
                    "msys", "true",
                    "UCRT64", "true",
                    "ucrt64", "true",
                    "CLANG64", "true",
                    "clang64", "true"
            );

            testCases.forEach((msystem, expectedStr) -> {
                var expected = Boolean.parseBoolean(expectedStr);
                var env = env("MSYSTEM", msystem);
                assertEquals(expected, SystemTools.isMinGw("Windows 10", createEnvProvider(env)),
                        "MSYSTEM=" + msystem);
            });
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("provideNonMingwEnvironments")
        @DisplayName("Should reject non-MinGW environments")
        void shouldRejectNonMingwEnvironments(String description, Map<String, String> env) {
            assertFalse(SystemTools.isMinGw("Windows 10", createEnvProvider(env)),
                    "Incorrectly detected MinGW: " + description);
        }

        @ParameterizedTest
        @ValueSource(strings = {"Linux", "Mac OS X", "FreeBSD"})
        @DisplayName("Should reject non-Windows systems even with MinGW indicators")
        void shouldRejectNonWindowsSystems(String osName) {
            var env = env("MSYSTEM", "MINGW64", "MINGW_PREFIX", "/mingw64", "SHELL", "/bin/bash");
            assertFalse(SystemTools.isMinGw(osName, createEnvProvider(env)));
        }

        @ParameterizedTest
        @CsvSource({"Windows 10, true", "Windows 11, true", "Win 7, true", "windows, true",
                "Linux, false", "Mac OS X, false"})
        @DisplayName("Should require Windows OS for MinGW detection")
        void shouldRequireWindowsForMingw(String osName, boolean expected) {
            var env = env("MSYSTEM", "MINGW64");
            assertEquals(expected, SystemTools.isMinGw(osName, createEnvProvider(env)));
        }

        @Test
        @DisplayName("Should use system environment")
        void shouldUseSystemEnvironment() {
            assertDoesNotThrow(() -> SystemTools.isMinGw());
        }
    }

    @Nested
    @DisplayName("OS Name Normalization Tests")
    class NormalizationTests {

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("Should handle null or empty OS names")
        void shouldHandleNullOrEmpty(String osName) {
            // Semantic note: normalize(null) and normalize("") both yield "",
            // which matches no known OS, so isOtherOs returns true. This is the
            // intentional contract: "unknown/absent" is classified as "other".
            assertFalse(SystemTools.isAix(osName));
            assertFalse(SystemTools.isFreeBsd(osName));
            assertFalse(SystemTools.isLinux(osName));
            assertFalse(SystemTools.isMacOS(osName));
            assertFalse(SystemTools.isOpenVms(osName));
            assertFalse(SystemTools.isSolaris(osName));
            assertFalse(SystemTools.isWindows(osName));
            assertTrue(SystemTools.isOtherOs(osName));
        }

        @ParameterizedTest
        @ValueSource(strings = {"", "UnknownOS", "foo"})
        @DisplayName("Should handle unknown OS names")
        void shouldHandleUnknownOs(String osName) {
            assertFalse(SystemTools.isLinux(osName));
            assertFalse(SystemTools.isMacOS(osName));
            assertFalse(SystemTools.isWindows(osName));
        }

        @Test
        @DisplayName("Should normalize to lowercase")
        void shouldNormalizeToLowercase() {
            assertTrue(SystemTools.isLinux("LINUX"));
            assertTrue(SystemTools.isMacOS("MACOS"));
            assertTrue(SystemTools.isWindows("WINDOWS"));
            assertTrue(SystemTools.isAix("AIX"));
            assertTrue(SystemTools.isFreeBsd("FREEBSD"));
            assertTrue(SystemTools.isOpenVms("OPENVMS"));
            assertTrue(SystemTools.isSolaris("SOLARIS"));
        }

        @Test
        @DisplayName("Should not match partial OS names")
        void shouldNotMatchPartialOsNames() {
            assertFalse(SystemTools.isWindows("darwin"));
            assertFalse(SystemTools.isMacOS("linux"));
            assertFalse(SystemTools.isLinux("windows"));
        }
    }

    @Nested
    @DisplayName("OpenVMS Detection Tests")
    class OpenVmsTests {

        @ParameterizedTest
        @ValueSource(strings = {"openvms", "OpenVMS", "OPENVMS", "OpenVMS 8.4"})
        @DisplayName("Should detect OpenVMS")
        void shouldDetectOpenVms(String osName) {
            assertTrue(SystemTools.isOpenVms(osName));
        }

        @ParameterizedTest
        @ValueSource(strings = {"linux", "windows", "vms"})
        @DisplayName("Should reject non-OpenVMS systems")
        void shouldRejectNonOpenVms(String osName) {
            assertFalse(SystemTools.isOpenVms(osName));
        }

        @Test
        @DisplayName("Should verify current system")
        void shouldVerifyCurrentSystem() {
            assertDoesNotThrow(() -> SystemTools.isOpenVms());
        }
    }

    @Nested
    @DisplayName("Other OS Detection Tests")
    class OtherOsTests {

        @Test
        @EnabledOnOs(OS.OTHER)
        @DisplayName("Should detect current system as other OS")
        void shouldDetectCurrentSystemAsOther() {
            // Relies on JUnit's OS.OTHER enumeration matching SystemTools' known-OS set.
            // If JUnit adds a newly-enumerated OS that SystemTools does not recognize,
            // update SystemTools' known-OS list and adjust this expectation.
            assertTrue(SystemTools.isOtherOs());
        }

        @ParameterizedTest
        @ValueSource(strings = {"foo", "bar", "unknown", "custom-os"})
        @DisplayName("Should detect unknown operating systems")
        void shouldDetectUnknownOs(String osName) {
            assertTrue(SystemTools.isOtherOs(osName));
        }

        @Test
        @EnabledOnOs({OS.LINUX, OS.MAC, OS.WINDOWS})
        @DisplayName("Should not detect current system as other OS")
        void shouldNotDetectCurrentSystemAsOther() {
            assertFalse(SystemTools.isOtherOs());
        }

        @ParameterizedTest
        @ValueSource(strings = {"aix", "darwin", "freebsd", "linux", "mac", "openvms",
                "osx", "solaris", "sunos", "windows"})
        @DisplayName("Should reject known operating systems")
        void shouldRejectKnownOs(String osName) {
            assertFalse(SystemTools.isOtherOs(osName));
        }
    }

    @Nested
    @DisplayName("Package-Private Method Coverage Tests")
    class PackagePrivateCoverageTests {

        @Test
        @DisplayName("isOtherOs() no-arg should match current system")
        void isOtherOsNoArgShouldMatchCurrent() {
            boolean isKnown = SystemTools.isAix() || SystemTools.isFreeBsd() || SystemTools.isLinux()
                    || SystemTools.isMacOS() || SystemTools.isOpenVms() || SystemTools.isSolaris()
                    || SystemTools.isWindows();
            assertEquals(!isKnown, SystemTools.isOtherOs());
        }

        @Test
        @DisplayName("isOtherOs(String) should reject known OS and accept unknown")
        void isOtherOsParamShouldWork() {
            assertTrue(SystemTools.isOtherOs("amiga-os"));
            assertTrue(SystemTools.isOtherOs("beos"));
            assertFalse(SystemTools.isOtherOs("linux"));
            assertFalse(SystemTools.isOtherOs("Windows 11"));
            assertFalse(SystemTools.isOtherOs("Mac OS X"));
        }

        @ParameterizedTest
        @ValueSource(strings = {"aix", "freebsd", "linux", "darwin", "mac os x", "openvms",
                "solaris", "sunos", "windows", "win"})
        @DisplayName("Param versions should handle all known OS names")
        void paramVersionsShouldHandleAllKnownOs(String fakeOsName) {
            assertDoesNotThrow(() -> SystemTools.isAix(fakeOsName));
            assertDoesNotThrow(() -> SystemTools.isFreeBsd(fakeOsName));
            assertDoesNotThrow(() -> SystemTools.isLinux(fakeOsName));
            assertDoesNotThrow(() -> SystemTools.isMacOS(fakeOsName));
            assertDoesNotThrow(() -> SystemTools.isOpenVms(fakeOsName));
            assertDoesNotThrow(() -> SystemTools.isSolaris(fakeOsName));
            assertDoesNotThrow(() -> SystemTools.isWindows(fakeOsName));
            assertDoesNotThrow(() -> SystemTools.isOtherOs(fakeOsName));
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "  \t  "})
        @DisplayName("Param versions should handle blank/null consistently")
        void paramVersionsShouldHandleBlankNull(String osName) {
            assertFalse(SystemTools.isAix(osName));
            assertFalse(SystemTools.isLinux(osName));
            assertFalse(SystemTools.isWindows(osName));
            assertTrue(SystemTools.isOtherOs(osName));
        }
    }

    @Nested
    @DisplayName("Solaris Detection Tests")
    class SolarisTests {

        @ParameterizedTest
        @ValueSource(strings = {"solaris", "Solaris", "SOLARIS", "Solaris 11", "sunos", "SunOS", "SUNOS"})
        @DisplayName("Should detect Solaris and SunOS")
        void shouldDetectSolarisAndSunOs(String osName) {
            assertTrue(SystemTools.isSolaris(osName));
        }

        @ParameterizedTest
        @ValueSource(strings = {"linux", "windows", "mac os x"})
        @DisplayName("Should reject non-Solaris systems")
        void shouldRejectNonSolaris(String osName) {
            assertFalse(SystemTools.isSolaris(osName));
        }

        @Test
        @EnabledOnOs(OS.SOLARIS)
        @DisplayName("Should verify current system is Solaris")
        void shouldVerifyCurrentSystemIsSolaris() {
            assertTrue(SystemTools.isSolaris());
        }
    }

    @Nested
    @DisplayName("Windows Detection Tests")
    class WindowsTests {

        @ParameterizedTest
        @ValueSource(strings = {"windows", "Windows", "WINDOWS", "windows 10", "windows 11",
                "Windows Server 2022", "win", "Win", "WIN"})
        @DisplayName("Should detect Windows variants")
        void shouldDetectWindowsVariants(String osName) {
            assertTrue(SystemTools.isWindows(osName));
        }

        @ParameterizedTest
        @ValueSource(strings = {"mac os x", "linux", "darwin"})
        @DisplayName("Should reject non-Windows systems")
        void shouldRejectNonWindows(String osName) {
            assertFalse(SystemTools.isWindows(osName));
        }

        @Test
        @EnabledOnOs(OS.WINDOWS)
        @DisplayName("Should verify current system is Windows")
        void shouldVerifyCurrentSystemIsWindows() {
            assertTrue(SystemTools.isWindows());
        }
    }

    @Nested
    @DisplayName("WSL Detection Tests")
    @SuppressWarnings("PMD.UnusedPrivateMethod")
    class WslTests {

        @Test
        @DisplayName("isWsl() should work for current system")
        void isWslShouldWorkForCurrentSystem() {
            Assumptions.assumeTrue(SystemTools.isWsl(), "Not running under WSL; skipping");
            assertTrue(SystemTools.isLinux(), "WSL must run on Linux");
        }

        @Test
        @DisplayName("Should detect WSL with Microsoft signature")
        void shouldDetectWslWithMicrosoft() {
            assertTrue(SystemTools.isWsl("Linux", () -> "Linux version 5.15.0-microsoft-standard-WSL2"));
        }

        @Test
        @DisplayName("Should detect WSL with wsl signature")
        void shouldDetectWslWithWslSignature() {
            assertTrue(SystemTools.isWsl("Linux", () -> "Linux version 5.10.102.1-wsl2"));
        }

        @Test
        @DisplayName("Should handle null proc version")
        void shouldHandleNullProcVersion() {
            assertFalse(SystemTools.isWsl("Linux", () -> null));
        }

        @Test
        @DisplayName("Should not invoke supplier for non-Linux OS")
        void shouldNotInvokeSupplierForNonLinux() {
            assertFalse(SystemTools.isWsl("Windows 10", () -> {
                throw new AssertionError("Supplier must not be invoked for non-Linux OS");
            }));
        }

        @Test
        @DisplayName("Should reject non-Linux OS")
        void shouldRejectNonLinuxOs() {
            assertFalse(SystemTools.isWsl("Windows 10", () -> "Linux version 5.15.0-microsoft-standard"));
        }

        @Test
        @DisplayName("Should reject non-WSL Linux")
        void shouldRejectNonWslLinux() {
            assertFalse(SystemTools.isWsl("Linux", () -> "Linux version 5.15.0-generic"));
        }
    }
}