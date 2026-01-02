package rife.bld.extension.tools;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SystemUtilsTest {
    @Nested
    @DisplayName("OS Tests")
    class OsTests {
        @Nested
        @DisplayName("Edge Cases Tests")
        class EdgeCaseTests {
            @Test
            void handlesEmptyOsName() {
                assertFalse(SystemUtils.isLinux(""));
                assertFalse(SystemUtils.isMacOS(""));
                assertFalse(SystemUtils.isWindows(""));
            }

            @Test
            void handlesPartialMatches() {
                assertFalse(SystemUtils.isWindows("darwin"));
                assertFalse(SystemUtils.isMacOS("linux"));
                assertFalse(SystemUtils.isLinux("windows"));
            }

            @Test
            void handlesUnknownOS() {
                assertFalse(SystemUtils.isLinux("someunknownos"));
                assertFalse(SystemUtils.isMacOS("someunknownos"));
                assertFalse(SystemUtils.isWindows("someunknownos"));
            }
        }

        @Nested
        @DisplayName("Linux Detection Tests")
        class LinuxDetectionTests {
            @Test
            void detectsLinux() {
                assertTrue(SystemUtils.isLinux("Linux"));
            }

            @Test
            void detectsLinuxCaseInsensitive() {
                assertTrue(SystemUtils.isLinux("linux"));
            }

            @Test
            void detectsUnix() {
                assertTrue(SystemUtils.isLinux("Unix"));
            }

            @Test
            void detectsUnixVariants() {
                assertTrue(SystemUtils.isLinux("freebsd unix"));
            }

            @Test
            void rejectsNonLinux() {
                assertFalse(SystemUtils.isLinux("Windows 10"));
                assertFalse(SystemUtils.isLinux("Mac OS X"));
            }
        }

        @Nested
        @DisplayName("macOS Detection Tests")
        class MacOSDetectionTests {
            @Test
            void detectsAix() {
                assertTrue(SystemUtils.isAix("aix"));
            }

            @Test
            void detectsDarwin() {
                assertTrue(SystemUtils.isMacOS("Darwin"));
            }

            @Test
            void detectsFreeBsd() {
                assertTrue(SystemUtils.isFreeBsd("freebsd"));
            }

            @Test
            void detectsMacCaseInsensitive() {
                assertTrue(SystemUtils.isMacOS("MAC OS X"));
                assertTrue(SystemUtils.isMacOS("MACOS"));
            }

            @Test
            void detectsMacOS() {
                assertTrue(SystemUtils.isMacOS("macOS"));
            }

            @Test
            void detectsMacOSX() {
                assertTrue(SystemUtils.isMacOS("Mac OS X"));
            }

            @Test
            void detectsOpenVms() {
                assertTrue(SystemUtils.isOpenVms("openVms"));
            }

            @Test
            void detectsSolaris() {
                assertTrue(SystemUtils.isSolaris("solaris"));
            }

            @Test
            void detectsSunOS() {
                assertTrue(SystemUtils.isSolaris("sunos"));
            }

            @Test
            void rejectsNonMac() {
                assertFalse(SystemUtils.isMacOS("Windows 10"));
                assertFalse(SystemUtils.isMacOS("Linux"));
            }
        }

        @Nested
        @DisplayName("OS Detection Tests")
        class OsDetectionTests {
            @Test
            @EnabledOnOs(OS.AIX)
            void verifyIsAix() {
                assertTrue(SystemUtils.isAix());
                assertFalse(SystemUtils.isFreeBsd());
                assertFalse(SystemUtils.isLinux());
                assertFalse(SystemUtils.isMacOS());
                assertFalse(SystemUtils.isOpenVms());
                assertFalse(SystemUtils.isSolaris());
                assertFalse(SystemUtils.isWindows());
            }

            @Test
            @EnabledOnOs(OS.LINUX)
            void verifyIsLinux() {
                assertTrue(SystemUtils.isLinux());
                assertFalse(SystemUtils.isAix());
                assertFalse(SystemUtils.isFreeBsd());
                assertFalse(SystemUtils.isMacOS());
                assertFalse(SystemUtils.isOpenVms());
                assertFalse(SystemUtils.isSolaris());
                assertFalse(SystemUtils.isWindows());
            }

            @Test
            @EnabledOnOs(OS.MAC)
            void verifyIsMacOS() {
                assertTrue(SystemUtils.isMacOS());
                assertFalse(SystemUtils.isAix());
                assertFalse(SystemUtils.isFreeBsd());
                assertFalse(SystemUtils.isLinux());
                assertFalse(SystemUtils.isOpenVms());
                assertFalse(SystemUtils.isSolaris());
                assertFalse(SystemUtils.isWindows());
            }

            @Test
            @EnabledOnOs(OS.SOLARIS)
            void verifyIsSolaris() {
                assertTrue(SystemUtils.isSolaris());
                assertFalse(SystemUtils.isAix());
                assertFalse(SystemUtils.isFreeBsd());
                assertFalse(SystemUtils.isLinux());
                assertFalse(SystemUtils.isMacOS());
                assertFalse(SystemUtils.isOpenVms());
                assertFalse(SystemUtils.isWindows());
            }

            @Test
            @EnabledOnOs(OS.WINDOWS)
            void verifyIsWindows() {
                assertTrue(SystemUtils.isWindows());
                assertFalse(SystemUtils.isAix());
                assertFalse(SystemUtils.isFreeBsd());
                assertFalse(SystemUtils.isLinux());
                assertFalse(SystemUtils.isMacOS());
                assertFalse(SystemUtils.isOpenVms());
                assertFalse(SystemUtils.isSolaris());
            }
        }

        @Nested
        @DisplayName("Other Tests")
        class OtherTests {
            @Test
            void detectsOtherOS() {
                assertTrue(SystemUtils.isOtherOs("foo"));
            }

            @Test
            void rejectsKnownOS() {
                assertFalse(SystemUtils.isOtherOs("aix"));
                assertFalse(SystemUtils.isOtherOs("darwin"));
                assertFalse(SystemUtils.isOtherOs("freebsd"));
                assertFalse(SystemUtils.isOtherOs("linux"));
                assertFalse(SystemUtils.isOtherOs("mac"));
                assertFalse(SystemUtils.isOtherOs("openvms"));
                assertFalse(SystemUtils.isOtherOs("osx"));
                assertFalse(SystemUtils.isOtherOs("solaris"));
                assertFalse(SystemUtils.isOtherOs("sunos"));
                assertFalse(SystemUtils.isOtherOs("windows"));
            }

            @Test
            @EnabledOnOs(OS.OTHER)
            void unknownOS() {
                assertTrue(SystemUtils.isOtherOS());
            }
        }

        @Nested
        @DisplayName("Windows Detection Tests")
        class WindowsDetectionTests {
            @Test
            void detectsWindows() {
                assertTrue(SystemUtils.isWindows("windows 10"));
            }

            @Test
            void detectsWindows11() {
                assertTrue(SystemUtils.isWindows("windows 11"));
            }

            @Test
            void detectsWindowsCaseInsensitive() {
                assertTrue(SystemUtils.isWindows("windows"));
            }

            @Test
            void detectsWindowsServer() {
                assertTrue(SystemUtils.isWindows("windows server 2022"));
            }

            @Test
            void rejectsNonWindows() {
                assertFalse(SystemUtils.isWindows("Mac OS X"));
                assertFalse(SystemUtils.isWindows("Linux"));
                assertFalse(SystemUtils.isWindows("darwin")); // "win" should not match "darwin"
            }
        }
    }
}
