[![License](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Java](https://img.shields.io/badge/java-17%2B-blue)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
[![bld](https://img.shields.io/badge/2.3.0-FA9052?label=bld&labelColor=2392FF)](https://rife2.com/bld)
[![Release](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Frepo.rife2.com%2Freleases%2Fcom%2Fuwyn%2Frife2%2Fbld-extensions-tools%2Fmaven-metadata.xml&color=blue)](https://repo.rife2.com/#/releases/com/uwyn/rife2/bld-extensions-tools)
[![Snapshot](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Frepo.rife2.com%2Fsnapshots%2Fcom%2Fuwyn%2Frife2%2Fbld-extensions-tools%2Fmaven-metadata.xml&label=snapshot)](https://repo.rife2.com/#/snapshots/com/uwyn/rife2/bld-extensions-tools)
[![GitHub CI](https://github.com/rife2/bld-extensions-tools/actions/workflows/bld.yml/badge.svg)](https://github.com/rife2/bld-extensions-tools/actions/workflows/bld.yml)

# Tools for [bld Extensions](https://github.com/rife2/bld/wiki/Extensions)

This project provides a collection of tools by various
[bld extensions](https://github.com/rife2/bld/wiki/Extensions).

To use, include the following in your `bld` build file:

```java
repositories = List.of(RIFE2_SNAPSHOTS, RIFE2_RELEASES);

scope(test).include(
    dependency("com.uwyn.rife2", "bld-extensions-tools", version(0, 9, 0, "SNAPSHOT"))
);
```

Please check the [documentation](https://rife2.github.io/bld-extensions-tools)
for more information.

## System Utilities

The following static methods are provided:

| Utilites          | Description                                                                                                      |
|:------------------|:-----------------------------------------------------------------------------------------------------------------|
| [`isAix()`]()     | Determines if the current operating system is AIX.                                                               |
| [`isFreeBsd()`]() | Determines if the current operating system is FreeBSD.                                                           |
| [`isLinux()`]()   | Determines if the current operating system is Linux.                                                             |
| [`isMacOS()`]()   | Determines if the current operating system is macOS.                                                             |
| [`isOpenVms()`]() | Determines if the current operating system is OpenVMS.                                                           |
| [`isOtherOS()`]() | Determines if the current operating system is other than AIX, FreeBSD, Linux, macOS, OpenVMS, Solaris or Windows |
| [`isSolaris()`]() | Determines if the current operating system is Solaris.                                                           |
| [`isWindows()`]() | Determines if the current operating system is Windows.                                                           |
