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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("System Tools Tests")
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
class SystemToolsTest {

    private static Function<String, String> createEnvProvider(Map<String, String> env) {
        return env::get;
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
    @DisplayName("Cygwin Detection Tests")
    class CygwinTests {

        static Stream<Arguments> provideCygwinEnvironments() {
            return Stream.of(
                    Arguments.of("Unix shell /bin/bash", Map.of("SHELL", "/bin/bash")),
                    Arguments.of("Unix shell /bin/sh", Map.of("SHELL", "/bin/sh")),
                    Arguments.of("Unix shell /usr/bin/zsh", Map.of("SHELL", "/usr/bin/zsh")),
                    Arguments.of("PATH with /cygdrive/", Map.of("PATH", "/usr/bin:/cygdrive/c/Windows")),
                    Arguments.of("PATH with /usr/bin", Map.of("PATH", "/usr/bin:/usr/local/bin")),
                    Arguments.of("PATH with both indicators", Map.of("PATH", "/cygdrive/c/Windows:/usr/bin")),
                    Arguments.of("TERM xterm", Map.of("TERM", "xterm")),
                    Arguments.of("TERM cygwin", Map.of("TERM", "cygwin")),
                    Arguments.of("TERM xterm-256color", Map.of("TERM", "xterm-256color")),
                    Arguments.of("Complete Cygwin environment",
                            Map.of("TERM", "xterm", "SHELL", "/bin/bash", "PATH", "/usr/bin:/cygdrive/c/Windows"))
            );
        }

        static Stream<Arguments> provideNonCygwinEnvironments() {
            return Stream.of(
                    Arguments.of("Empty environment", Map.of()),
                    Arguments.of("Windows shell", Map.of("SHELL", "C:\\Windows\\System32\\cmd.exe")),
                    Arguments.of("Windows PATH", Map.of("PATH", "C:\\Windows\\System32;C:\\Program Files")),
                    Arguments.of("Windows TERM", Map.of("TERM", "dumb")),
                    Arguments.of("PowerShell environment",
                            Map.of("SHELL", "C:\\Windows\\System32\\WindowsPowerShell\\v1.0\\powershell.exe",
                                    "PATH", "C:\\Windows\\System32"))
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
            var env = Map.of("TERM", "xterm", "SHELL", "/bin/bash", "PATH", "/usr/bin:/cygdrive/c");
            assertFalse(SystemTools.isCygwin(osName, createEnvProvider(env)));
        }

        @ParameterizedTest
        @CsvSource({"Windows 10, true", "Windows 11, true", "Win XP, true", "windows, true",
                "Linux, false", "Mac OS X, false"})
        @DisplayName("Should require Windows OS for Cygwin detection")
        void shouldRequireWindowsForCygwin(String osName, boolean expected) {
            var env = Map.of("SHELL", "/bin/bash");
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
        @DisplayName("Should handle empty environment map")
        void shouldHandleEmptyEnvironment() {
            var emptyEnv = Map.<String, String>of();
            var provider = createEnvProvider(emptyEnv);

            assertFalse(SystemTools.isCygwin("Windows 10", provider));
            assertFalse(SystemTools.isMingw("Windows 10", provider));
        }

        @Test
        @DisplayName("Should handle mixed null and present variables")
        void shouldHandleMixedEnvironment() {
            var mixedEnv = Map.of("PATH", "/usr/bin");
            var provider = createEnvProvider(mixedEnv);

            // PATH with /usr/bin is a Cygwin indicator, so this will be detected as Cygwin
            assertTrue(SystemTools.isCygwin("Windows 10", provider));

            // PATH without Cygwin indicators should not detect Cygwin
            var nonCygwinEnv = Map.of("PATH", "C:\\Windows\\System32");
            assertFalse(SystemTools.isCygwin("Windows 10", createEnvProvider(nonCygwinEnv)));
        }

        @Test
        @DisplayName("Should handle null environment variables")
        void shouldHandleNullEnvironmentVariables() {
            Function<String, String> nullProvider = key -> null;

            assertFalse(SystemTools.isCygwin("Windows 10", nullProvider));
            assertFalse(SystemTools.isMingw("Windows 10", nullProvider));
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
            assertFalse(SystemTools.isOtherOS());
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
            assertFalse(SystemTools.isOtherOS());
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
            assertFalse(SystemTools.isOtherOS());
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
            assertFalse(SystemTools.isOtherOS());
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
            assertFalse(SystemTools.isOtherOS());
        }
    }

    @Nested
    @DisplayName("Linux Detection Tests")
    class LinuxTests {

        @ParameterizedTest
        @ValueSource(strings = {"linux", "Linux", "LINUX", "linux 5.15", "unix", "UNIX", "freebsd unix"})
        @DisplayName("Should detect Linux and Unix systems")
        void shouldDetectLinuxAndUnix(String osName) {
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
                    Arguments.of("MSYSTEM=MINGW64", Map.of("MSYSTEM", "MINGW64")),
                    Arguments.of("MSYSTEM=MINGW32", Map.of("MSYSTEM", "MINGW32")),
                    Arguments.of("MSYSTEM=MSYS", Map.of("MSYSTEM", "MSYS")),
                    Arguments.of("MINGW_PREFIX", Map.of("MINGW_PREFIX", "/mingw64")),
                    Arguments.of("MINGW_CHOST", Map.of("MINGW_CHOST", "x86_64-w64-mingw32")),
                    Arguments.of("Both MINGW variables",
                            Map.of("MINGW_PREFIX", "/mingw64", "MINGW_CHOST", "x86_64-w64-mingw32")),
                    Arguments.of("MinGW paths with shell",
                            Map.of("PATH", "/mingw64/bin:/usr/bin", "SHELL", "/bin/bash")),
                    Arguments.of("Unix-style MSYS paths with shell",
                            Map.of("PATH", "/msys64/usr/bin:/usr/bin", "SHELL", "/bin/sh")),
                    Arguments.of("Windows-style MSYS paths with shell",
                            Map.of("PATH", "C:\\msys64\\usr\\bin", "SHELL", "/bin/sh")),
                    Arguments.of("Windows-style MinGW paths",
                            Map.of("PATH", "C:\\mingw64\\bin;C:\\Windows", "SHELL", "/bin/bash")),
                    Arguments.of("Complete MinGW environment",
                            Map.of("MSYSTEM", "MINGW64", "MINGW_PREFIX", "/mingw64",
                                    "MINGW_CHOST", "x86_64-w64-mingw32", "SHELL", "/bin/bash",
                                    "PATH", "/mingw64/bin:/usr/bin"))
            );
        }

        static Stream<Arguments> provideNonMingwEnvironments() {
            return Stream.of(
                    Arguments.of("Empty environment", Map.of()),
                    Arguments.of("Windows PATH only", Map.of("PATH", "C:\\Windows\\System32;C:\\Program Files")),
                    Arguments.of("MinGW paths without shell", Map.of("PATH", "/mingw64/bin")),
                    Arguments.of("Shell without MinGW paths", Map.of("SHELL", "/bin/bash")),
                    Arguments.of("CMD environment",
                            Map.of("SHELL", "C:\\Windows\\System32\\cmd.exe", "PATH", "C:\\Windows\\System32")),
                    Arguments.of("PowerShell environment",
                            Map.of("PATH", "C:\\Windows\\System32;C:\\Program Files",
                                    "SHELL", "C:\\Windows\\System32\\WindowsPowerShell\\v1.0\\powershell.exe")),
                    Arguments.of("MSYSTEM=UCRT64", Map.of("MSYSTEM", "UCRT64")),
                    Arguments.of("MSYSTEM=CLANG64", Map.of("MSYSTEM", "CLANG64"))
            );
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("provideMingwEnvironments")
        @DisplayName("Should detect valid MinGW environments")
        void shouldDetectMingwEnvironments(String description, Map<String, String> env) {
            assertTrue(SystemTools.isMingw("Windows 10", createEnvProvider(env)),
                    "Failed to detect MinGW: " + description);
        }

        @Test
        @DisplayName("Should distinguish MinGW from Cygwin")
        void shouldDistinguishMinGwFromCygwin() {
            var mingwEnv = Map.of("MSYSTEM", "MINGW64", "MINGW_PREFIX", "/mingw64", "SHELL", "/bin/bash");
            var mingwProvider = createEnvProvider(mingwEnv);

            var cygwinEnv = Map.of("TERM", "xterm", "SHELL", "/bin/bash");
            var cygwinProvider = createEnvProvider(cygwinEnv);

            assertTrue(SystemTools.isMingw("Windows 10", mingwProvider));
            assertTrue(SystemTools.isCygwin("Windows 10", mingwProvider));
            assertFalse(SystemTools.isMingw("Windows 10", cygwinProvider));
            assertTrue(SystemTools.isCygwin("Windows 10", cygwinProvider));
        }

        @Test
        @DisplayName("Should handle case-sensitive MSYSTEM values")
        @SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
        void shouldHandleCaseSensitiveMsystem() {
            var testCases = Map.of(
                    "MINGW64", true,
                    "MSYS", true,
                    "mingw_MINGW64", true,
                    "mingw64", false,
                    "MiNgW64", false,
                    "msys", false
            );

            testCases.forEach((msystem, expected) -> {
                var env = Map.of("MSYSTEM", msystem);
                assertEquals(expected, SystemTools.isMingw("Windows 10", createEnvProvider(env)),
                        "MSYSTEM=" + msystem);
            });
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("provideNonMingwEnvironments")
        @DisplayName("Should reject non-MinGW environments")
        void shouldRejectNonMingwEnvironments(String description, Map<String, String> env) {
            assertFalse(SystemTools.isMingw("Windows 10", createEnvProvider(env)),
                    "Incorrectly detected MinGW: " + description);
        }

        @ParameterizedTest
        @ValueSource(strings = {"Linux", "Mac OS X", "FreeBSD"})
        @DisplayName("Should reject non-Windows systems even with MinGW indicators")
        void shouldRejectNonWindowsSystems(String osName) {
            var env = Map.of("MSYSTEM", "MINGW64", "MINGW_PREFIX", "/mingw64", "SHELL", "/bin/bash");
            assertFalse(SystemTools.isMingw(osName, createEnvProvider(env)));
        }

        @ParameterizedTest
        @CsvSource({"Windows 10, true", "Windows 11, true", "Win 7, true", "windows, true",
                "Linux, false", "Mac OS X, false"})
        @DisplayName("Should require Windows OS for MinGW detection")
        void shouldRequireWindowsForMingw(String osName, boolean expected) {
            var env = Map.of("MSYSTEM", "MINGW64");
            assertEquals(expected, SystemTools.isMingw(osName, createEnvProvider(env)));
        }

        @Test
        @DisplayName("Should use system environment")
        void shouldUseSystemEnvironment() {
            assertDoesNotThrow(() -> SystemTools.isMingw());
        }
    }

    @Nested
    @DisplayName("OS Name Normalization Tests")
    class NormalizationTests {

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("Should handle null or empty OS names")
        void shouldHandleNullOrEmpty(String osName) {
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
        @ValueSource(strings = {"", "someunknownos", "foo"})
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
            assertTrue(SystemTools.isOtherOS());
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
            assertFalse(SystemTools.isOtherOS());
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
}