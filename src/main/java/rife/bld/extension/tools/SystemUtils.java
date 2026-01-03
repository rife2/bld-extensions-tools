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

import java.util.Locale;
import java.util.function.Function;

/**
 * System Utilities and Tools.
 */
public final class SystemUtils {
    private static final String OS_NAME = getOSName(); // cached

    private SystemUtils() {
        // no-op
    }

    private static String getOSName() {
        var osName = System.getProperty("os.name");
        return normalizeOSName(osName);
    }

    /**
     * Determines if the given operating system name corresponds to AIX.
     *
     * @param osName The name of the operating system to evaluate
     * @return {@code true} if the operating system name contains "aix" (case-insensitive),
     * {@code false} otherwise
     */
    static boolean isAix(String osName) {
        var normalized = normalizeOSName(osName);
        return normalized.contains("aix");
    }

    /**
     * Determines if the current operating system is AIX.
     *
     * @return {@code true} if the operating system is identified as AIX, {@code false} otherwise
     */
    public static boolean isAix() {
        return isAix(OS_NAME);
    }

    /**
     * Determines if the current environment is running in a Cygwin environment.
     *
     * @return {@code true} if the environment is detected as Cygwin, {@code false} otherwise
     */
    public static boolean isCygwin() {
        return isCygwin(OS_NAME, System::getenv);
    }

    /**
     * Determines if the environment is running in a Cygwin environment based on
     * the provided OS name and environment variables.
     *
     * @param osName      The name of the operating system
     * @param envProvider A function to retrieve environment variables
     * @return {@code true} if the environment is detected as Cygwin, {@code false} otherwise
     */
    static boolean isCygwin(String osName, Function<String, String> envProvider) {
        // Check if OS is Windows first
        if (!isWindows(osName)) {
            return false;
        }

        // Check for Cygwin-specific environment variables
        var term = envProvider.apply("TERM");
        var shell = envProvider.apply("SHELL");
        var path = envProvider.apply("PATH");

        // Cygwin typically sets TERM to something like "xterm" or "cygwin"
        boolean hasCygwinTerm = term != null &&
                (term.contains("xterm") || term.contains("cygwin"));

        // Cygwin sets SHELL to a Unix-style path like /bin/bash
        boolean hasCygwinShell = shell != null && shell.startsWith("/");

        // PATH in Cygwin contains Unix-style paths with /cygdrive/ or /usr/bin
        boolean hasCygwinPath = path != null &&
                (path.contains("/cygdrive/") || path.contains("/usr/bin"));

        // If any strong Cygwin indicators are present, return true
        return hasCygwinShell || hasCygwinPath || hasCygwinTerm;
    }

    /**
     * Determines if the given operating system name corresponds to FreeBSD.
     *
     * @param osName The name of the operating system to evaluate.
     * @return {@code true} if the operating system is FreeBSD, {@code false} otherwise
     */
    static boolean isFreeBsd(String osName) {
        var normalized = normalizeOSName(osName);
        return normalized.contains("freebsd");
    }

    /**
     * Determines if the current operating system is FreeBSD.
     *
     * @return {@code true} if the operating system is FreeBSD, {@code false} otherwise
     */
    public static boolean isFreeBsd() {
        return isFreeBsd(OS_NAME);
    }

    /**
     * Determines if the operating system is Linux.
     *
     * @return {@code true} if the operating system is Linux, {@code false} otherwise
     */
    public static boolean isLinux() {
        return isLinux(OS_NAME);
    }

    /**
     * Determines if the given operating system name corresponds to Linux or a similar Unix-based system.
     *
     * @param osName The name of the operating system to evaluate
     * @return {@code true} if the operating system name contains "linux" or "unix" (case-insensitive),
     * {@code false} otherwise
     */
    static boolean isLinux(String osName) {
        var normalized = normalizeOSName(osName);
        return normalized.contains("linux") || normalized.contains("unix");
    }

    /**
     * Determines if the current operating system is macOS.
     *
     * @return {@code true} if the OS is macOS, {@code false} otherwise
     */
    public static boolean isMacOS() {
        return isMacOS(OS_NAME);
    }

    /**
     * Determines if the given operating system name corresponds to macOS.
     *
     * @param osName The name of the operating system to evaluate
     * @return {@code true} if the operating system name contains "mac", "darwin", or "osx" (case-insensitive),
     * {@code false} otherwise
     */
    static boolean isMacOS(String osName) {
        var normalized = normalizeOSName(osName);
        return normalized.contains("mac") || normalized.contains("darwin") || normalized.contains("osx");
    }

    /**
     * Determines if the current environment is running in a Mingw environment.
     *
     * @return {@code true} if the environment is detected as MinGW/MSYS2, {@code false} otherwise
     */
    public static boolean isMingw() {
        return isMingw(OS_NAME, System::getenv);
    }

    /**
     * Determines if the environment is running in a MinGW environment based on
     * the provided OS name and environment variables.
     *
     * @param osName      The name of the operating system
     * @param envProvider A function to retrieve environment variables
     * @return {@code true} if the environment is detected as MinGW/MSYS2, {@code false} otherwise
     */
    static boolean isMingw(String osName, Function<String, String> envProvider) {
        // Check if OS is Windows first
        if (!isWindows(osName)) {
            return false;
        }

        // Check for MinGW-specific environment variables
        var msystem = envProvider.apply("MSYSTEM");
        var mingwPrefix = envProvider.apply("MINGW_PREFIX");
        var mingwChost = envProvider.apply("MINGW_CHOST");
        var shell = envProvider.apply("SHELL");
        var path = envProvider.apply("PATH");

        // MSYSTEM is a strong indicator of MinGW/MSYS2
        // Common values: MINGW64, MINGW32, MSYS, UCRT64, CLANG64, etc.
        boolean hasMsystem = msystem != null &&
                (msystem.contains("MINGW") || msystem.contains("MSYS"));

        // MINGW_PREFIX and MINGW_CHOST are set in MinGW environments
        boolean hasMingwVars = mingwPrefix != null || mingwChost != null;

        // Check for MinGW-specific paths
        boolean hasMingwPaths = path != null &&
                (path.contains("/mingw") ||
                        path.contains("\\mingw") ||
                        path.contains("/msys") ||
                        path.contains("\\msys"));

        // MinGW sets SHELL to a Unix-style path
        boolean hasMingwShell = shell != null &&
                (shell.contains("/bin/bash") || shell.contains("/bin/sh"));

        // Strong indicators: MSYSTEM or MINGW-specific variables
        // Weak indicators: paths and shell (could be other environments)
        return hasMsystem || hasMingwVars || (hasMingwPaths && hasMingwShell);
    }

    /**
     * Determines if the current operating system is OpenVMS.
     *
     * @return {@code true} if the operating system is OpenVMS, {@code false} otherwise
     */
    public static boolean isOpenVms() {
        return isOpenVms(OS_NAME);
    }

    /**
     * Determines if the given operating system name corresponds to OpenVMS.
     *
     * @param osName The name of the operating system to evaluate
     * @return {@code true} if the operating system name contains "openvms" (case-insensitive), {@code false} otherwise
     */
    static boolean isOpenVms(String osName) {
        var normalized = normalizeOSName(osName);
        return normalized.contains("openvms");
    }

    /**
     * Determines if the current operating system is other than AIX, FreeBSD, Linux, macOS, OpenVMS,
     * Solaris, or Windows.
     *
     * @return {@code true} if the operating system is other than AIX, FreeBSD, Linux, macOS,
     * OpenVMS, Solaris, or Windows, {@code false} otherwise
     */
    public static boolean isOtherOS() {
        return isOtherOs(OS_NAME);
    }

    /**
     * Determines if the current operating system is other than AIX, FreeBSD, Linux, macOS, OpenVMS, Solaris,
     * or Windows.
     *
     * @return {@code true} if the operating system is other than AIX, FreeBSD, Linux, macOS, OpenVMS, Solaris,
     * or Windows, {@code false} otherwise
     */
    static boolean isOtherOs(String osName) {
        return !isAix(osName) && !isFreeBsd(osName) && !isLinux(osName) && !isMacOS(osName) && !isOpenVms(osName)
                && !isSolaris(osName) && !isWindows(osName);
    }

    /**
     * Determines if the current operating system is Solaris.
     *
     * @return {@code true} if the operating system is Solaris, {@code false} otherwise
     */
    public static boolean isSolaris() {
        return isSolaris(OS_NAME);
    }

    /**
     * Determines if the given operating system name corresponds to Solaris.
     *
     * @param osName The name of the operating system to evaluate
     * @return {@code true} if the operating system name contains "solaris" or "sunos" (case-insensitive),
     * {@code false} otherwise
     */
    static boolean isSolaris(String osName) {
        var normalized = normalizeOSName(osName);
        return normalized.contains("solaris") || normalized.contains("sunos");
    }

    /**
     * Determines if the current operating system is Windows.
     *
     * @return {@code true} if the operating system is Windows, {@code false} otherwise
     */
    public static boolean isWindows() {
        return isWindows(OS_NAME);
    }

    /**
     * Determines if the given operating system name corresponds to Windows.
     *
     * @param osName The name of the operating system to evaluate
     */
    static boolean isWindows(String osName) {
        var normalized = normalizeOSName(osName);
        return normalized.contains("windows") || normalized.startsWith("win");
    }

    /**
     * Normalizes the given operating system name by converting it to lowercase
     * using the English locale. If the input is null, an empty string is returned.
     *
     * @param osName The name of the operating system to normalize
     * @return A normalized, lowercase version of the operating system name, or an
     * empty string if the input is {@code null}
     */
    static String normalizeOSName(String osName) {
        return osName != null ? osName.toLowerCase(Locale.ENGLISH) : "";
    }
}