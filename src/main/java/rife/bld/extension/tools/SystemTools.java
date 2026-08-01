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

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Utility methods for detecting the current operating system, architecture, and related
 * execution environments.
 * <p>
 * Provides normalized OS-name checks for common platforms (AIX, FreeBSD,
 * Linux, macOS, OpenVMS, Solaris, Windows) along with heuristics for
 * identifying Cygwin, MinGW/MSYS2, and WSL environments. Also provides architecture
 * detection for x86 and ARM families.
 * <p>
 * Designed for null-safety, testability, and minimal overhead. All system properties
 * are cached at class load time.
 *
 * @author <a href="https://erik.thauvin.net/">Erik C. Thauvin</a>
 * @since 1.0
 */
@NullMarked
public final class SystemTools {

    private static final String OS_ARCH = System.getProperty("os.arch", "").toLowerCase(Locale.ROOT);
    private static final String OS_NAME = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
    private static final boolean IS_OTHER_OS = isOtherOs(OS_NAME);
    private static final boolean IS_WSL = computeWsl(OS_NAME, () -> {
        try {
            return readProcVersion();
        } catch (IOException e) {
            return null;
        }
    });

    private SystemTools() {
        // no-op
    }

    /**
     * Returns the normalized system architecture string.
     *
     * <p>Equivalent to {@code System.getProperty("os.arch").toLowerCase(Locale.ROOT)}.
     * Use the {@code is*} methods for reliable checks instead of comparing this value directly,
     * as {@code os.arch} values vary across JVM implementations.
     *
     * @return the architecture string in lowercase, or empty string if unavailable
     * @since 1.3
     */
    public static String arch() {
        return OS_ARCH;
    }

    /**
     * Determines if the environment is Windows Subsystem for Linux based on
     * the provided OS name and /proc/version content supplier.
     *
     * @param osName              the name of the operating system
     * @param procVersionSupplier supplier for /proc/version content
     * @return {@code true} if WSL, {@code false} otherwise
     * @since 1.3
     */
    private static boolean computeWsl(@Nullable String osName, Supplier<@Nullable String> procVersionSupplier) {
        if (osName == null || !isLinux(osName)) {
            return false;
        }
        var version = procVersionSupplier.get();
        if (version == null) {
            return false;
        }
        var lower = version.toLowerCase(Locale.ROOT);
        return lower.contains("microsoft") || lower.contains("wsl");
    }

    /**
     * Determines if the current operating system is AIX.
     *
     * @return {@code true} if the operating system is AIX, {@code false} otherwise
     * @since 1.0
     */
    public static boolean isAix() {
        return isAix(OS_NAME);
    }

    /**
     * Determines if the given operating system name corresponds to AIX.
     *
     * @param osName the name of the operating system to evaluate
     * @return {@code true} if the operating system name contains {@code aix} (case-insensitive),
     * {@code false} otherwise
     * @since 1.0
     */
    static boolean isAix(@Nullable String osName) {
        return normalize(osName).contains("aix");
    }

    /**
     * Determines if the current system architecture is any ARM variant.
     *
     * <p>Equivalent to {@code isArm32() || isArm64()}.
     *
     * @return {@code true} if the architecture is ARM 32-bit or 64-bit, {@code false} otherwise
     * @since 1.3
     */
    public static boolean isArm() {
        return isArm32() || isArm64();
    }

    /**
     * Determines if the current system architecture is ARM 32-bit.
     *
     * <p>Checks {@code os.arch} for {@code "arm"}, {@code "arm32"}, or {@code "armv*"} without {@code "64"}.
     * Covers older Raspberry Pi, Android devices, etc.
     *
     * @return {@code true} if the architecture is ARM 32-bit, {@code false} otherwise
     * @since 1.3
     */
    public static boolean isArm32() {
        return isArm32(OS_ARCH);
    }

    /**
     * Determines if the given architecture string corresponds to ARM 32-bit.
     *
     * @param arch the architecture string to evaluate
     * @return {@code true} if ARM 32-bit, {@code false} otherwise
     * @since 1.3
     */
    static boolean isArm32(@Nullable String arch) {
        var a = normalize(arch);
        return "arm".equals(a)
                || "arm32".equals(a)
                || (a.startsWith("armv") && !a.contains("64"));
    }

    /**
     * Determines if the current system architecture is ARM 64-bit.
     *
     * <p>Checks {@code os.arch} for {@code "aarch64"} or {@code "arm64"}.
     * Covers Apple Silicon, AWS Graviton, Raspberry Pi 64-bit, etc.
     *
     * @return {@code true} if the architecture is ARM64, {@code false} otherwise
     * @since 1.3
     */
    public static boolean isArm64() {
        return isArm64(OS_ARCH);
    }

    /**
     * Determines if the given architecture string corresponds to ARM 64-bit.
     *
     * @param arch the architecture string to evaluate
     * @return {@code true} if ARM64, {@code false} otherwise
     * @since 1.3
     */
    static boolean isArm64(@Nullable String arch) {
        var a = normalize(arch);
        return "aarch64".equals(a) || "arm64".equals(a);
    }

    /**
     * Determines if the current environment is running in a Cygwin environment.
     *
     * <p>This method delegates to {@link #isCygwin(String, Function)} using the current
     * OS name and {@link System#getenv} as the environment provider. For testing, use
     * {@link #isCygwin(String, Function)} directly with a custom environment provider.
     * <p>
     * Heuristic-based detection; may have false positives on Windows with Unix-like tools.
     *
     * @return {@code true} if the environment is detected as Cygwin, {@code false} otherwise
     * @since 1.0
     */
    public static boolean isCygwin() {
        return isCygwin(OS_NAME, System::getenv);
    }

    /**
     * Determines if the environment is running in a Cygwin environment based on
     * the provided OS name and environment variables.
     *
     * @param osName      the name of the operating system
     * @param envProvider a function to retrieve environment variables
     * @return {@code true} if the environment is detected as Cygwin, {@code false} otherwise
     * @since 1.0
     */
    static boolean isCygwin(@Nullable String osName, Function<String, @Nullable String> envProvider) {
        if (!isWindows(osName)) {
            return false;
        }

        var shell = envProvider.apply("SHELL");
        var path = envProvider.apply("PATH");
        var term = envProvider.apply("TERM");

        boolean hasCygwinShell = shell != null &&
                (shell.contains("cygwin")
                        || shell.startsWith("/bin/")
                        || shell.startsWith("/usr/bin/"));

        boolean hasCygwinPath = path != null &&
                (path.contains("/cygdrive/")
                        || path.contains("/usr/bin/cygwin"));

        // FIX: removed xterm from hasCygwinTerm — xterm is a generic terminal type common in
        //      many non-Cygwin Windows environments (Git Bash, VS Code, etc.) and produces
        //      false positives. Only match an explicit "cygwin" term value.
        boolean hasCygwinTerm = term != null && term.contains("cygwin");

        return hasCygwinShell || hasCygwinPath || hasCygwinTerm;
    }

    /**
     * Determines if the current operating system is FreeBSD.
     *
     * @return {@code true} if the operating system is FreeBSD, {@code false} otherwise
     * @since 1.0
     */
    public static boolean isFreeBsd() {
        return isFreeBsd(OS_NAME);
    }

    /**
     * Determines if the given operating system name corresponds to FreeBSD.
     *
     * @param osName the name of the operating system to evaluate
     * @return {@code true} if the operating system is FreeBSD, {@code false} otherwise
     * @since 1.0
     */
    static boolean isFreeBsd(@Nullable String osName) {
        return normalize(osName).contains("freebsd");
    }

    /**
     * Determines if the current operating system is Linux.
     *
     * @return {@code true} if the operating system is Linux, {@code false} otherwise
     * @since 1.0
     */
    public static boolean isLinux() {
        return isLinux(OS_NAME);
    }

    /**
     * Determines if the given operating system name corresponds to Linux.
     *
     * @param osName the name of the operating system to evaluate
     * @return {@code true} if the operating system name contains {@code linux} (case-insensitive),
     * {@code false} otherwise
     * @since 1.0
     */
    // FIX: inlined the redundant local variable `n` to match the style of other single-condition methods
    static boolean isLinux(@Nullable String osName) {
        return normalize(osName).contains("linux");
    }

    /**
     * Determines if the current operating system is macOS.
     *
     * @return {@code true} if the OS is macOS, {@code false} otherwise
     * @since 1.0
     */
    public static boolean isMacOS() {
        return isMacOS(OS_NAME);
    }

    /**
     * Determines if the given operating system name corresponds to macOS.
     *
     * @param osName the name of the operating system to evaluate
     * @return {@code true} if the operating system name contains {@code mac}, {@code darwin},
     * or {@code osx} (case-insensitive),
     * {@code false} otherwise
     * @since 1.0
     */
    static boolean isMacOS(@Nullable String osName) {
        var n = normalize(osName);
        return n.contains("mac") || n.contains("darwin") || n.contains("osx");
    }

    /**
     * Determines if the current environment is running in a MinGW/MSYS2 environment.
     *
     * <p>This method delegates to {@link #isMinGw(String, Function)} using the current
     * OS name and {@link System#getenv} as the environment provider. For testing, use
     * {@link #isMinGw(String, Function)} directly with a custom environment provider.
     * <p>
     * Heuristic-based detection; may have false positives on Windows with Unix-like tools.
     *
     * @return {@code true} if the environment is detected as MinGW/MSYS2, {@code false} otherwise
     * @since 1.0
     */
    public static boolean isMinGw() {
        return isMinGw(OS_NAME, System::getenv);
    }

    /**
     * Determines if the environment is running in a MinGW environment based on
     * the provided OS name and environment variables.
     *
     * @param osName      the name of the operating system
     * @param envProvider a function to retrieve environment variables
     * @return {@code true} if the environment is detected as MinGW/MSYS2, {@code false} otherwise
     * @since 1.0
     */
    static boolean isMinGw(@Nullable String osName, Function<String, @Nullable String> envProvider) {
        if (!isWindows(osName)) {
            return false;
        }

        var msystem = envProvider.apply("MSYSTEM");
        boolean hasMsystem = msystem != null && (msystem.contains("MINGW") || msystem.contains("MSYS"));
        if (hasMsystem) {
            return true;
        }

        var mingwPrefix = envProvider.apply("MINGW_PREFIX");
        var mingwChost = envProvider.apply("MINGW_CHOST");
        boolean hasMingwVars = mingwPrefix != null || mingwChost != null;
        if (hasMingwVars) {
            return true;
        }

        var path = envProvider.apply("PATH");
        var shell = envProvider.apply("SHELL");
        boolean hasMingwPath = path != null
                && (path.contains("/mingw")
                || path.contains("\\mingw")
                || path.contains("/msys")
                || path.contains("\\msys"));
        boolean hasMingwShell = shell != null && (shell.contains("/bin/bash") || shell.contains("/bin/sh"));

        return hasMingwPath && hasMingwShell;
    }

    /**
     * Determines if the current operating system is OpenVMS.
     *
     * @return {@code true} if the operating system is OpenVMS, {@code false} otherwise
     * @since 1.0
     */
    public static boolean isOpenVms() {
        return isOpenVms(OS_NAME);
    }

    /**
     * Determines if the given operating system name corresponds to OpenVMS.
     *
     * @param osName the name of the operating system to evaluate
     * @return {@code true} if the operating system name contains {@code openvms} (case-insensitive),
     * {@code false} otherwise
     * @since 1.0
     */
    static boolean isOpenVms(@Nullable String osName) {
        return normalize(osName).contains("openvms");
    }

    /**
     * Determines if the current operating system is other than AIX, FreeBSD, Linux, macOS,
     * OpenVMS, Solaris, or Windows.
     *
     * @return {@code true} if the operating system is none of the above, {@code false} otherwise
     * @since 1.0
     */
    public static boolean isOtherOs() {
        return IS_OTHER_OS;
    }

    /**
     * Determines if the given operating system name is other than AIX, FreeBSD, Linux, macOS,
     * OpenVMS, Solaris, or Windows.
     *
     * @param osName the name of the operating system to evaluate
     * @return {@code true} if the operating system is none of the above, {@code false} otherwise
     * @since 1.0
     */
    static boolean isOtherOs(@Nullable String osName) {
        return !isAix(osName)
                && !isFreeBsd(osName)
                && !isLinux(osName)
                && !isMacOS(osName)
                && !isOpenVms(osName)
                && !isSolaris(osName)
                && !isWindows(osName);
    }

    /**
     * Determines if the current operating system is Solaris.
     *
     * @return {@code true} if the operating system is Solaris, {@code false} otherwise
     * @since 1.0
     */
    public static boolean isSolaris() {
        return isSolaris(OS_NAME);
    }

    /**
     * Determines if the given operating system name corresponds to Solaris.
     *
     * @param osName the name of the operating system to evaluate
     * @return {@code true} if the operating system name contains {@code solaris} or {@code sunos} (case-insensitive),
     * {@code false} otherwise
     * @since 1.0
     */
    static boolean isSolaris(@Nullable String osName) {
        var n = normalize(osName);
        return n.contains("solaris") || n.contains("sunos");
    }

    /**
     * Determines if the current operating system is Windows.
     *
     * @return {@code true} if the operating system is Windows, {@code false} otherwise
     * @since 1.0
     */
    public static boolean isWindows() {
        return isWindows(OS_NAME);
    }

    /**
     * Determines if the given operating system name corresponds to Windows.
     *
     * @param osName the name of the operating system to evaluate
     * @return {@code true} if the operating system name contains {@code windows} or starts with {@code win}
     * (case-insensitive), {@code false} otherwise
     * @since 1.0
     */
    static boolean isWindows(@Nullable String osName) {
        var n = normalize(osName);
        return n.contains("windows") || n.startsWith("win");
    }

    /**
     * Determines if the current environment is Windows Subsystem for Linux.
     *
     * <p>Checks {@code /proc/version} for {@code "Microsoft"} or {@code "WSL"} signatures.
     * Result is cached at class load time.
     *
     * @return {@code true} if running under WSL, {@code false} otherwise
     * @since 1.3
     */
    public static boolean isWsl() {
        return IS_WSL;
    }

    /**
     * Determines if the environment is Windows Subsystem for Linux based on
     * the provided OS name and /proc/version content supplier.
     *
     * <p>For testing. Production code should use {@link #isWsl()}.
     *
     * @param osName              the name of the operating system
     * @param procVersionSupplier supplier for /proc/version content
     * @return {@code true} if WSL, {@code false} otherwise
     * @since 1.3
     */
    static boolean isWsl(@Nullable String osName, Supplier<@Nullable String> procVersionSupplier) {
        return computeWsl(osName, procVersionSupplier);
    }

    /**
     * Determines if the current system architecture is x86 64-bit.
     *
     * <p>Checks {@code os.arch} for {@code "amd64"} or {@code "x86_64"}.
     * Covers Intel/AMD 64-bit, including Apple Intel Macs.
     *
     * @return {@code true} if the architecture is x86 64-bit, {@code false} otherwise
     * @since 1.3
     */
    public static boolean isX64() {
        return isX64(OS_ARCH);
    }

    /**
     * Determines if the given architecture string corresponds to x86 64-bit.
     *
     * @param arch the architecture string to evaluate
     * @return {@code true} if x86 64-bit, {@code false} otherwise
     * @since 1.3
     */
    static boolean isX64(@Nullable String arch) {
        var a = normalize(arch);
        return "amd64".equals(a) || "x86_64".equals(a);
    }

    /**
     * Determines if the current system architecture is x86 32-bit.
     *
     * <p>Checks {@code os.arch} for {@code "x86"}, {@code "i386"}, {@code "i486"},
     * {@code "i586"}, or {@code "i686"}.
     *
     * @return {@code true} if the architecture is x86 32-bit, {@code false} otherwise
     * @since 1.3
     */
    public static boolean isX86() {
        return isX86(OS_ARCH);
    }

    /**
     * Determines if the given architecture string corresponds to x86 32-bit.
     *
     * @param arch the architecture string to evaluate
     * @return {@code true} if x86 32-bit, {@code false} otherwise
     * @since 1.3
     */
    static boolean isX86(@Nullable String arch) {
        var a = normalize(arch);
        return "x86".equals(a)
                || "i386".equals(a)
                || "i486".equals(a)
                || "i586".equals(a)
                || "i686".equals(a);
    }

    /**
     * Determines if the current system architecture is any x86 variant.
     *
     * <p>Equivalent to {@code isX86() || isX64()}.
     *
     * @return {@code true} if the architecture is x86 32-bit or 64-bit, {@code false} otherwise
     * @since 1.3
     */
    public static boolean isX86Family() {
        return isX86() || isX64();
    }

    /**
     * Normalize the given OS name.
     *
     * @param osName the OS name string to normalize
     * @return the normalized name or empty if {@code null}
     */
    private static String normalize(@Nullable String osName) {
        return osName != null ? osName.toLowerCase(Locale.ROOT) : "";
    }

    /**
     * Reads /proc/version content. Extracted for testability.
     *
     * @return the content of /proc/version
     * @throws IOException if read fails
     */
    private static String readProcVersion() throws IOException {
        return Files.readString(Path.of("/proc/version"));
    }
}