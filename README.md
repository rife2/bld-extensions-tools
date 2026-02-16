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
repositories = List.of(RIFE2_SNAPSHOTS, RIFE2_RELEASES);

scope(compile).include(
    dependency("com.uwyn.rife2", "bld-extensions-tools", version(0, 9,0,"SNAPSHOT"))
);
```

Please check the [documentation](https://rife2.github.io/bld-extensions-tools)
for more information.

## Classpath Tools

The following static methods are provided:

| Method                                                                                                                                                                           | Description                            |
|:---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|:---------------------------------------|
| [`joinClasspath(Collection<String>... files)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/ClasspathTools.html#joinClasspath(java.util.Collection...)) | Joins lists of files into a classpath. |
| [`joinClasspath(String... paths)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/ClasspathTools.html#joinClasspath(java.lang.String...))                 | Join string paths into a classpath.    |

## I/O Tools

The following static methods are provided:

| Method                                                                                                                                                                          | Description                                   |
|:--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|:----------------------------------------------|
| [`canExecute(File file)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/IOTools.html#canExecute(java.io.File))                                          | Check if a file is executable.                |
| [`canExecute(Path path)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/IOTools.html#canExecute(java.nio.file.Path))                                    | Check if a file path is exectuable.           |
| [`canExecute(String path)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/IOTools.html#canExecute(java.lang.String))                                    | Check if a file path is exectuable.           |
| [`exists(File file)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/IOTools.html#exists(java.io.File))                                                  | Check if a file exists.                       |
| [`exists(Path path)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/IOTools.html#exists(java.nio.file.Path))                                            | Check if a path exists.                       |
| [`exists(String path)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/IOTools.html#exits(java.lang.String))                                             | Check if a path exists.                       |
| [`isDirectory(File file)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/IOTools.html#isDirectory(java.io.File))                                        | Check if a file is a directory.               |
| [`isDirectory(Path path)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/IOTools.html#isDirectory(java.nio.file.Path))                                  | Check if a path is a directory.               |
| [`isDirectory(String path)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/IOTools.html#isDirectory(java.lang.String))                                  | Check if a path is a directory.               |
| [`mkdirs(File file)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/IOTools.html#mkdirs(java.io.File))                                                  | Make directories.                             |
| [`mkdirs(Path path)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/IOTools.html#mkdirs(java.nio.file.Path))                                            | Make directories.                             |
| [`mkdirs(String path)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/IOTools.html#mkdirs(java.lang.String))                                            | Make directories.                             |
| [`notExists(File file)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/IOTools.html#notExists(java.io.File))                                            | Check if a file exists.                       |
| [`notExists(Path path)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/IOTools.html#notExists(java.nio.file.Path))                                      | Check if a path exists.                       |
| [`notExists(String path)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/IOTools.html#notExits(java.lang.String))                                       | Check if a path exists.                       |
| [`resolveFile(File base, String... segments)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/IOTools.html#resolveFile(java.io.File,java.lang.String...)) | Resolve a file with additional path segments. |

*NOTE:* All methods properly handle `null` values

## Object Tools

The following static methods are provided:

| Method                                                                                                                                                            | Description                                           |
|:------------------------------------------------------------------------------------------------------------------------------------------------------------------|:------------------------------------------------------|
| [`isAnyNull(Collection<?> objects)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/ObjectTools.html#isAnyNull(java.util.Collection))      | Checks if any of the provided objects are `null`.     |
| [`isAnyNull(T... objects)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/ObjectTools.html#isAnyNull(T...))                               | Checks if any of the provided objects are `null`.     |
| [`isEmpty(Collection<?> collection)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/ObjectTools.html#isEmpty(java.util.Collection))       | Checks if a collection is empty.                      |
| [`isEmpty(Map<?,?> map)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/ObjectTools.html#isEmpty(java.util.Map))                          | Checks if a  map is empty.                            |
| [`isEmpty(T[] array)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/ObjectTools.html#isEmpty(T[]))                                       | Checks if an array is empty.                          |
| [`isEmpty(T[]... arrays)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/ObjectTools.html#isEmpty(T[]...))                                | Checks if any of the provided arrays are empty.       |
| [`isNotEmpty(Collection<?> collection)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/ObjectTools.html#isNotEmpty(java.util.Collection)) | Checks if a collection is not empty.                  |
| [`isNotEmpty(Map<?,?> map)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/ObjectTools.html#isNotEmpty(java.util.Map))                    | Checks if a map is not empty.                         |
| [`isNotEmpty(T[] array)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/ObjectTools.html#isNotEmpty(T[]))                                 | Checks if an array is not empty.                      |
| [`isNotEmpty(T[]... arrays)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/ObjectTools.html#isNotEmpty(T[]...))                          | Checks if any of the provided arrays are not empty.   |
| [`isNotNull(Collection<?> objects)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/ObjectTools.html#isNotNull(java.util.Collection))      | Checks if any of the provided objects are not `null`. |
| [`isNotNull(T... objects)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/ObjectTools.html#isNotNull(T...))                               | Checks if any of the provided objects are not `null`. |
| [`isNull(Collection<?> objects)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/ObjectTools.html#isNull(java.util.Collection))            | Checks if the provided objects are all `null`.        |
| [`isNull(T... objects)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/ObjectTools.html#isNull(T...))                                     | Checks if the provided objects are all `null`.        |

*NOTE:* All methods properly handle `null` objects

## System Tools

The following static methods are provided:

| Method                                                                                                              | Description                                                                                                      |
|:--------------------------------------------------------------------------------------------------------------------|:-----------------------------------------------------------------------------------------------------------------|
| [`isAix()`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/SystemTools.html#isAix())         | Determines if the current operating system is AIX.                                                               |
| [`isCygwin()`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/SystemTools.html#isCygwin())   | Determines if the current operating system is Cygwin.                                                            |
| [`isFreeBsd()`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/SystemTools.html#isFreeBsd()) | Determines if the current operating system is FreeBSD.                                                           |
| [`isLinux()`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/SystemTools.html#isLinux())     | Determines if the current operating system is Linux.                                                             |
| [`isMacOS()`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/SystemTools.html#isMacOS())     | Determines if the current operating system is macOS.                                                             |
| [`isMingw()`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/SystemTools.html#isMingw())     | Determines if the current operating system is MinGW.                                                             |
| [`isOpenVms()`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/SystemTools.html#isOpenVms()) | Determines if the current operating system is OpenVMS.                                                           |
| [`isOtherOS()`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/SystemTools.html#isOtherOS()) | Determines if the current operating system is other than AIX, FreeBSD, Linux, macOS, OpenVMS, Solaris or Windows |
| [`isSolaris()`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/SystemTools.html#isSolaris()) | Determines if the current operating system is Solaris.                                                           |
| [`isWindows()`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/SystemTools.html#isWindows()) | Determines if the current operating system is Windows.                                                           |

## Text Tools

The following static methods are provided:

| Method                                                                                                                                                              | Description                             |
|:--------------------------------------------------------------------------------------------------------------------------------------------------------------------|:----------------------------------------|
| [`isBlank(CharSequence str)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/TextTools.html#isBlank(java.lang.CharSequence))                 | Checks if a string is blank.            |
| [`isBlank(CharSequence... strings)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/TextTools.html#isBlank(java.lang.CharSequence...))       | Checks if strings are blank.            |
| [`isBlank(Object... strings)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/TextTools.html#isBlank(java.lang.Object...))                   | Checks if string objects are blank.     |
| [`isEmpty(CharSequence str)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/TextTools.html#isEmpty(java.lang.CharSequence))                 | Checks if a string is empty.            |
| [`isEmpty(CharSequence... strings)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/TextTools.html#isEmpty(java.lang.CharSequence...))       | Checks if strings are empty.            |
| [`isEmpty(Object... strings)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/TextTools.html#isEmpty(java.lang.Object...))                   | Checks if string objects are empty.     |
| [`isNotBlank(CharSequence str)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/TextTools.html#isNotBlank(java.lang.CharSequence))           | Checks if a string is not blank.        |
| [`isNotBlank(CharSequence... strings)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/TextTools.html#isNotBlank(java.lang.CharSequence...)) | Checks if strings are not blank.        |
| [`isNotBlank(Objects... strings)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/TextTools.html#isNotBlank(java.lang.Object...))            | Checks if string objects are not blank. |
| [`isNotEmpty(CharSequence str)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/TextTools.html#isNotEmpty(java.lang.CharSequence))           | Checks if a string is not empty.        |
| [`isNotEmpty(CharSequence... strings)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/TextTools.html#isNotEmpty(java.lang.CharSequence...)) | Checks if strings are not empty.        |
| [`isNotEmpty(Objects... strings)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/TextTools.html#isNotEmpty(java.lang.Object...))            | Checks if string objects are not empty. |

*NOTE:* All methods properly handle `null` strings.
