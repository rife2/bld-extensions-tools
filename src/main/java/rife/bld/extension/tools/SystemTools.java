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
     * @return {@code true} if the operating system name contains "aix" (case-insensitive),
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
        return OS_NAME.contains("aix");
    }

    /**
     * Determines if the current environment is running in a Cygwin environment.
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
        return (shell != null && shell.startsWith("/"))
                || (path != null && (path.contains("/cygdrive/") || path.contains("/usr/bin")))
                || (term != null && (term.contains("xterm") || term.contains("cygwin")));
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
        return OS_NAME.contains("freebsd");
    }

    /**
     * Determines if the current operating system is Linux.
     *
     * @return {@code true} if the operating system is Linux, {@code false} otherwise
     * @since 1.0
     */
    public static boolean isLinux() {
        return OS_NAME.contains("linux") || OS_NAME.contains("unix");
    }

    /**
     * Determines if the given operating system name corresponds to Linux or a similar Unix-based system.
     *
     * @param osName the name of the operating system to evaluate
     * @return {@code true} if the operating system name contains "linux" or "unix" (case-insensitive),
     * {@code false} otherwise
     * @since 1.0
     */
    static boolean isLinux(String osName) {
        var n = normalize(osName);
        return n.contains("linux") || n.contains("unix");
    }

    /**
     * Determines if the current operating system is macOS.
     *
     * @return {@code true} if the OS is macOS, {@code false} otherwise
     * @since 1.0
     */
    public static boolean isMacOS() {
        return OS_NAME.contains("mac") || OS_NAME.contains("darwin") || OS_NAME.contains("osx");
    }

    /**
     * Determines if the given operating system name corresponds to macOS.
     *
     * @param osName the name of the operating system to evaluate
     * @return {@code true} if the operating system name contains "mac", "darwin", or "osx" (case-insensitive),
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
     * @return {@code true} if the environment is detected as MinGW/MSYS2, {@code false} otherwise
     * @since 1.0
     */
    public static boolean isMingw() {
        return isMingw(OS_NAME, System::getenv);
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
    static boolean isMingw(String osName, Function<String, String> envProvider) {
        if (!isWindows(osName)) {
            return false;
        }
        var msystem = envProvider.apply("MSYSTEM");
        var mingwPrefix = envProvider.apply("MINGW_PREFIX");
        var mingwChost = envProvider.apply("MINGW_CHOST");
        var shell = envProvider.apply("SHELL");
        var path = envProvider.apply("PATH");

        boolean hasMsystem = msystem != null && (msystem.contains("MINGW") || msystem.contains("MSYS"));
        boolean hasMingwVars = mingwPrefix != null || mingwChost != null;
        boolean hasMingwPath = path != null && (path.contains("/mingw") || path.contains("\\mingw")
                || path.contains("/msys") || path.contains("\\msys"));
        boolean hasMingwShell = shell != null && (shell.contains("/bin/bash") || shell.contains("/bin/sh"));

        return hasMsystem || hasMingwVars || (hasMingwPath && hasMingwShell);
    }

    /**
     * Determines if the current operating system is OpenVMS.
     *
     * @return {@code true} if the operating system is OpenVMS, {@code false} otherwise
     * @since 1.0
     */
    public static boolean isOpenVms() {
        return OS_NAME.contains("openvms");
    }

    /**
     * Determines if the given operating system name corresponds to OpenVMS.
     *
     * @param osName the name of the operating system to evaluate
     * @return {@code true} if the operating system name contains "openvms" (case-insensitive),
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
    public static boolean isOtherOS() {
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
        return !isAix(osName) && !isFreeBsd(osName) && !isLinux(osName) && !isMacOS(osName)
                && !isOpenVms(osName) && !isSolaris(osName) && !isWindows(osName);
    }

    /**
     * Determines if the current operating system is Solaris.
     *
     * @return {@code true} if the operating system is Solaris, {@code false} otherwise
     * @since 1.0
     */
    public static boolean isSolaris() {
        return OS_NAME.contains("solaris") || OS_NAME.contains("sunos");
    }

    /**
     * Determines if the given operating system name corresponds to Solaris.
     *
     * @param osName the name of the operating system to evaluate
     * @return {@code true} if the operating system name contains "solaris" or "sunos" (case-insensitive),
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
        return OS_NAME.contains("windows") || OS_NAME.startsWith("win");
    }

    /**
     * Determines if the given operating system name corresponds to Windows.
     *
     * @param osName the name of the operating system to evaluate
     * @return {@code true} if the operating system name contains "windows" or starts with "win"
     * (case-insensitive), {@code false} otherwise
     * @since 1.0
     */
    static boolean isWindows(String osName) {
        var n = normalize(osName);
        return n.contains("windows") || n.startsWith("win");
    }

    private static String normalize(String osName) {
        return osName != null ? osName.toLowerCase(Locale.ENGLISH) : "";
    }
}