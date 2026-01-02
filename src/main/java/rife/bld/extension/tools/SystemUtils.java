package rife.bld.extension.tools;

import java.util.Locale;

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
     * @param osName The name of the operating system to evaluate. Must not be null.
     * @return {@code true} if the operating system name contains "aix" (case-insensitive),
     * {@code false} otherwise.
     */
    static boolean isAix(String osName) {
        var normalized = normalizeOSName(osName);
        return normalized.contains("aix");
    }

    /**
     * Determines if the current operating system is AIX.
     *
     * @return {@code true} if the operating system is identified as AIX, {@code false} otherwise.
     */
    public static boolean isAix() {
        return isAix(OS_NAME);
    }

    /**
     * Determines if the given operating system name corresponds to FreeBSD.
     *
     * @param osName The name of the operating system to evaluate.
     * @return {@code true} if the operating system is FreeBSD, {@code false} otherwise.
     */
    static boolean isFreeBsd(String osName) {
        var normalized = normalizeOSName(osName);
        return normalized.contains("freebsd");
    }

    /**
     * {@code false}
     * Determines if the current operating system is FreeBSD.
     *
     * @return {@code true} if the operating system is FreeBSD, {@code false} otherwise.
     */
    public static boolean isFreeBsd() {
        return isFreeBsd(OS_NAME);
    }

    /**
     * Determines if the operating system is Linux.
     *
     * @return {@code true} if the operating system is Linux, {@code false} otherwise.
     */
    public static boolean isLinux() {
        return isLinux(OS_NAME);
    }

    /**
     * Determines if the given operating system name corresponds to Linux or a similar Unix-based system.
     *
     * @param osName The name of the operating system to evaluate. Must not be null.
     * @return {@code true} if the operating system name contains "linux" or "unix" (case-insensitive),
     * {@code false} otherwise.
     */
    static boolean isLinux(String osName) {
        var normalized = normalizeOSName(osName);
        return normalized.contains("linux") || normalized.contains("unix");
    }

    /**
     * Determines if the current operating system is macOS.
     *
     * @return {@code true} if the OS is macOS, {@code false} otherwise.
     */
    public static boolean isMacOS() {
        return isMacOS(OS_NAME);
    }

    /**
     * Determines if the given operating system name corresponds to macOS.
     *
     * @param osName The name of the operating system to evaluate. Must not be null.
     * @return {@code true} if the operating system name contains "mac", "darwin", or "osx" (case-insensitive),
     * {@code false} otherwise.
     */
    static boolean isMacOS(String osName) {
        var normalized = normalizeOSName(osName);
        return normalized.contains("mac") || normalized.contains("darwin") || normalized.contains("osx");
    }

    /**
     * Determines if the current operating system is OpenVMS.
     *
     * @return {@code true} if the operating system is OpenVMS, {@code false} otherwise.
     */
    public static boolean isOpenVms() {
        return isOpenVms(OS_NAME);
    }

    /**
     * Determines if the given operating system name corresponds to OpenVMS.
     *
     * @param osName The name of the operating system to evaluate. Must not be null.
     * @return {@code true} if the operating system name contains "openvms" (case-insensitive), {@code false} otherwise.
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
     * OpenVMS, Solaris, or Windows, {@code false} otherwise.
     */
    public static boolean isOtherOS() {
        return isOtherOs(OS_NAME);
    }

    /**
     * Determines if the current operating system is other than AIX, FreeBSD, Linux, macOS, OpenVMS, Solaris,
     * or Windows.
     *
     * @return {@code true} if the operating system is other than AIX, FreeBSD, Linux, macOS, OpenVMS, Solaris,
     * or Windows, {@code false} otherwise.
     */
    static boolean isOtherOs(String osName) {
        return !isAix(osName) && !isFreeBsd(osName) && !isLinux(osName) && !isMacOS(osName) && !isOpenVms(osName)
                && !isSolaris(osName) && !isWindows(osName);
    }

    /**
     * Determines if the current operating system is Solaris.
     *
     * @return {@code true} if the operating system is Solaris, {@code false} otherwise.
     */
    public static boolean isSolaris() {
        return isSolaris(OS_NAME);
    }

    /**
     * Determines if the given operating system name corresponds to Solaris.
     *
     * @param osNanme The name of the operating system to evaluate. Must not be null.
     * @return {@code true} if the operating system name contains "solaris" or "sunos" (case-insensitive),
     * {@code false} otherwise.
     */
    static boolean isSolaris(String osNanme) {
        var normalized = normalizeOSName(osNanme);
        return normalized.contains("solaris") || normalized.contains("sunos");
    }

    /**
     * Determines if the current operating system is Windows.
     *
     * @return {@code true} if the operating system is Windows, {@code false} otherwise.
     */
    public static boolean isWindows() {
        return isWindows(OS_NAME);
    }

    static boolean isWindows(String osName) {
        var normalized = normalizeOSName(osName);
        return normalized.contains("windows") || normalized.startsWith("win");
    }

    static String normalizeOSName(String osName) {
        return osName != null ? osName.toLowerCase(Locale.ENGLISH) : "";
    }
}
