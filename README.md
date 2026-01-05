[![License](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Java](https://img.shields.io/badge/java-17%2B-blue)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
[![bld](https://img.shields.io/badge/2.3.0-FA9052?label=bld&labelColor=2392FF)](https://rife2.com/bld)
[![Release](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Frepo.rife2.com%2Freleases%2Fcom%2Fuwyn%2Frife2%2Fbld-extensions-tools%2Fmaven-metadata.xml&color=blue)](https://repo.rife2.com/#/releases/com/uwyn/rife2/bld-extensions-tools)
[![Snapshot](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Frepo.rife2.com%2Fsnapshots%2Fcom%2Fuwyn%2Frife2%2Fbld-extensions-tools%2Fmaven-metadata.xml&label=snapshot)](https://repo.rife2.com/#/snapshots/com/uwyn/rife2/bld-extensions-tools)
[![GitHub CI](https://github.com/rife2/bld-extensions-tools/actions/workflows/bld.yml/badge.svg)](https://github.com/rife2/bld-extensions-tools/actions/workflows/bld.yml)

# Tools for [bld Extensions](https://github.com/rife2/bld/wiki/Extensions)

This project provides a collection of tools used by various
[bld extensions](https://github.com/rife2/bld/wiki/Extensions).

To use, include the following in your `bld` build file:

```java
repositories =List.

of(RIFE2_SNAPSHOTS, RIFE2_RELEASES);

scope(test).include(
    dependency("com.uwyn.rife2", "bld-extensions-tools",version(0, 9,0,"SNAPSHOT"))
);
```

Please check the [documentation](https://rife2.github.io/bld-extensions-tools)
for more information.

## Classpath Utilities

The following static methods are provided:

| Method                                                                                                                                                                          | Description                                             |
|:--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|:--------------------------------------------------------|
| [`buildClasspath(String... path)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/ClasspathUtils.html#buildClasspath(java.lang.String...))               | Constructs a classpath string.                          |
| [`joinClasspathJar(Collection<String> jars)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/ClasspathUtils.html#joinClasspathJar(java.util.Collection)) | Joins a list of JAR file paths into a single classpath. |

## Collection Utilities

The following static methods are provided:

| Method                                                                                                                                                                | Description                          |
|:----------------------------------------------------------------------------------------------------------------------------------------------------------------------|:-------------------------------------|
| [`isEmpty(Collection<?> collection)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/CollectionUtils.html#isEmpty(java.util.Collection))       | Checks if a collection is empty.     |
| [`isNotEmpty(Collection<?> collection)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/CollectionUtils.html#isNotEmpty(java.util.Collection)) | Checks if a collection is not empty. |

*NOTE:* All methods properly handle `null` collections.

## Files Utilities

The following static methods are provided:

| Method                                                                                                                                            | Description                         |
|:--------------------------------------------------------------------------------------------------------------------------------------------------|:------------------------------------|
| [`canExecute(File file)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/FilesUtils.html#canExecute(java.io.File))         | Check if a file is executable.      |
| [`canExecute(Path path)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/FilesUtils.html#canExecute(java.nio.file.Path))   | Check if a file path is exectuable. |
| [`canExecute(String path)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/FilesUtils.html#canExecute(java.lang.String))   | Check if a file path is exectuable. |
| [`exists(File file)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/FilesUtils.html#exists(java.io.File))                 | Check if a file exists.             |
| [`exists(Path path)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/FilesUtils.html#exists(java.nio.file.Path))           | Check if a path exists.             |
| [`exists(String path)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/FilesUtils.html#exits(java.lang.String))            | Check if a path exists.             |
| [`isDirectory(File file)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/FilesUtils.html#isDirectory(java.io.File))       | Check if a file is a directory.     |
| [`isDirectory(Path path)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/FilesUtils.html#isDirectory(java.nio.file.Path)) | Check if a path is a directory.     |
| [`isDirectory(String path)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/FilesUtils.html#isDirectory(java.lang.String)) | Check if a path is a directory.     |
| [`mkdirs(File file)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/FilesUtils.html#mkdirs(java.io.File))                 | Make directories.                   |
| [`mkdirs(Path path)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/FilesUtils.html#mkdirs(java.nio.file.Path))           | Make directories.                   |
| [`mkdirs(String path)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/FilesUtils.html#mkdirs(java.lang.String))           | Make directories.                   |
| [`notExists(File file)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/FilesUtils.html#notExists(java.io.File))           | Check if a file exists.             |
| [`notExists(Path path)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/FilesUtils.html#notExists(java.nio.file.Path))     | Check if a path exists.             |
| [`notExists(String path)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/FilesUtils.html#notExits(java.lang.String))      | Check if a path exists.             |

*NOTE:* All methods properly handle `null` values

## Objects Utilities

The following static methods are provided:

| Method                                                                                                                                                        | Description                                           |
|:--------------------------------------------------------------------------------------------------------------------------------------------------------------|:------------------------------------------------------|
| [`isAnyNull(Object... objects)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/ObjectsUtils.html#isAnyNull(java.lang.Object...))      | Checks if any of the provided objects are `null`.     |
| [`isAnyNull(Collection<?> objects)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/ObjectsUtils.html#isAnyNull(java.util.Collection)) | Checks if any of the provided objects are `null`.     |
| [`isNotNull(Object... objects)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/ObjectsUtils.html#isNotNull(java.lang.Object...))      | Checks if any of the provided objects are not `null`. |
| [`isNotNull(Collection<?> objects)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/ObjectsUtils.html#isNotNull(java.util.Collection)) | Checks if any of the provided objects are not `null`. |
| [`isNull(Object... objects)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/ObjectsUtils.html#isNull(java.lang.Object...))            | Checks if the provided objects are all `null`.        |
| [`isNull(Collection<?> objects)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/ObjectsUtils.html#isNull(java.util.Collection))       | Checks if the provided objects are all `null`.        | Description                         |
|
## System Utilities

The following static methods are provided:

| Method                                                                                                              | Description                                                                                                      |
|:--------------------------------------------------------------------------------------------------------------------|:-----------------------------------------------------------------------------------------------------------------|
| [`isAix()`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/SystemUtils.html#isAix())         | Determines if the current operating system is AIX.                                                               |
| [`isCygwin()`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/SystemUtils.html#isCygwin())   | Determines if the current operating system is Cygwin.                                                            |
| [`isFreeBsd()`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/SystemUtils.html#isFreeBsd()) | Determines if the current operating system is FreeBSD.                                                           |
| [`isLinux()`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/SystemUtils.html#isLinux())     | Determines if the current operating system is Linux.                                                             |
| [`isMacOS()`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/SystemUtils.html#isMacOS())     | Determines if the current operating system is macOS.                                                             |
| [`isMingw()`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/SystemUtils.html#isMingw())     | Determines if the current operating system is MinGW.                                                             |
| [`isOpenVms()`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/SystemUtils.html#isOpenVms()) | Determines if the current operating system is OpenVMS.                                                           |
| [`isOtherOS()`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/SystemUtils.html#isOtherOS()) | Determines if the current operating system is other than AIX, FreeBSD, Linux, macOS, OpenVMS, Solaris or Windows |
| [`isSolaris()`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/SystemUtils.html#isSolaris()) | Determines if the current operating system is Solaris.                                                           |
| [`isWindows()`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/SystemUtils.html#isWindows()) | Determines if the current operating system is Windows.                                                           |

## Text Utilities

The following static methods are provided:

| Method                                                                                                                                                      | Description                      |
|:------------------------------------------------------------------------------------------------------------------------------------------------------------|:---------------------------------|
| [`isBlank(String str)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/TextUtils.html#isBlank(java.lang.String))                     | Checks if a string is blank.     |
| [`isEmpty(String str)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/TextUtils.html#isEmpty(java.lang.String))                     | Checks if a string is empty.     |
| [`isBlank(String... strings)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/TextUtils.html#isBlank(java.lang.String...))           | Checks if strings are blank.     |
| [`isEmpty(String... strings str)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/TextUtils.html#isEmpty(java.lang.String...))       | Checks if strings are empty.     |
| [`isNotBlank(String str)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/TextUtils.html#isNotBlank(java.lang.String))               | Checks if a string is not blank. |
| [`isNotEmpty(String str)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/TextUtils.html#isNotEmpty(java.lang.String))               | Checks if a string is not empty. |
| [`isNotBlank(String... strings)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/TextUtils.html#isNotBlank(java.lang.String...))     | Checks if strings are not blank. |
| [`isNotEmpty(String... strings str)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/TextUtils.html#isNotEmpty(java.lang.String...)) | Checks if strings are not empty. |

*NOTE:* All methods properly handle `null` strings.