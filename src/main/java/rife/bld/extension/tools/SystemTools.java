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
 * System Tools.
 */
public final class SystemTools {

    private static final String OS_NAME = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH);

    private SystemTools() {
        // no-op
    }

    /**
     * Determines if the given operating system name corresponds to AIX.
     *
     * @param osName the name of the operating system to evaluate
     * @return {@code true} if the operating system name contains {@code aix} (case-insensitive),
     * {@code false} otherwise
     * @since 1.0
     */
    static boolean isAix(String osName) {
        return normalize(osName).contains("aix");
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
     * Determines if the current environment is running in a Cygwin environment.
     *
     * <p>This method delegates to {@link #isCygwin(String, Function)} using the current
     * OS name and {@link System#getenv} as the environment provider. For testing, use
     * {@link #isCygwin(String, Function)} directly with a custom environment provider.
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
    static boolean isCygwin(String osName, Function<String, String> envProvider) {
        if (!isWindows(osName)) {
            return false;
        }

        var term = envProvider.apply("TERM");
        var shell = envProvider.apply("SHELL");
        var path = envProvider.apply("PATH");

        boolean hasCygwinShell = shell != null &&
                (shell.contains("cygwin")
                        || shell.startsWith("/bin/")
                        || shell.startsWith("/usr/bin/"));

        boolean hasCygwinPath = path != null &&
                (path.contains("/cygdrive/")
                        || path.contains("/usr/bin"));

        boolean hasCygwinTerm = term != null &&
                (term.contains("cygwin")
                        || term.contains("xterm"));

        return hasCygwinShell || hasCygwinPath || hasCygwinTerm;
    }


    /**
     * Determines if the given operating system name corresponds to FreeBSD.
     *
     * @param osName the name of the operating system to evaluate
     * @return {@code true} if the operating system is FreeBSD, {@code false} otherwise
     * @since 1.0
     */
    static boolean isFreeBsd(String osName) {
        return normalize(osName).contains("freebsd");
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
    static boolean isLinux(String osName) {
        var n = normalize(osName);
        return n.contains("linux");
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
    static boolean isMacOS(String osName) {
        var n = normalize(osName);
        return n.contains("mac") || n.contains("darwin") || n.contains("osx");
    }

    /**
     * Determines if the current environment is running in a MinGW environment.
     *
     * <p>This method delegates to {@link #isMinGw(String, Function)} using the current
     * OS name and {@link System#getenv} as the environment provider. For testing, use
     * {@link #isMinGw(String, Function)} directly with a custom environment provider.
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
    static boolean isMinGw(String osName, Function<String, String> envProvider) {
        if (!isWindows(osName)) {
            return false;
        }

        // Strategy 1: MSYSTEM variable (set by MSYS2/MinGW terminals)
        var msystem = envProvider.apply("MSYSTEM");
        boolean hasMsystem = msystem != null && (msystem.contains("MINGW") || msystem.contains("MSYS"));
        if (hasMsystem) {
            return true;
        }

        // Strategy 2: Explicit MinGW environment variables
        var mingwPrefix = envProvider.apply("MINGW_PREFIX");
        var mingwChost = envProvider.apply("MINGW_CHOST");
        boolean hasMingwVars = mingwPrefix != null || mingwChost != null;
        if (hasMingwVars) {
            return true;
        }

        // Strategy 3: PATH heuristic combined with shell check
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
    static boolean isOpenVms(String osName) {
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
        return isOtherOs(OS_NAME);
    }

    /**
     * Determines if the given operating system name is other than AIX, FreeBSD, Linux, macOS,
     * OpenVMS, Solaris, or Windows.
     *
     * @param osName the name of the operating system to evaluate
     * @return {@code true} if the operating system is none of the above, {@code false} otherwise
     * @since 1.0
     */
    static boolean isOtherOs(String osName) {
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
    static boolean isSolaris(String osName) {
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
    static boolean isWindows(String osName) {
        var n = normalize(osName);
        return n.contains("windows") || n.startsWith("win");
    }

    /**
     * Normalize the given OS name.
     *
     * @param osName the OS name string to normalize
     * @return the normalized name or empty if {@code null}
     */
    private static String normalize(String osName) {
        return osName != null ? osName.toLowerCase(Locale.ENGLISH) : "";
    }
}