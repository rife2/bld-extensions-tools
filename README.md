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
    dependency("com.uwyn.rife2", "bld-extensions-tools", version(1, 2, 0))
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

## Collection Tools

The following static methods are provided:

| Method                                                                                                                                                                                                 | Description                                      |
|:-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|:-------------------------------------------------|
| [`combine(Collection<T>... collections)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/CollectionTools.html#combine(java.util.Collection...))                                 | Combine collections into a list.                 |
| [`combineFilesToPaths(Collection<File>... collections)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/CollectionTools.html#combineFilesToPaths(java.util.Collection...))      | Combine `File` collections into a `Path` list.   |
| [`combineFilesToPaths(File... files`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/CollectionTools.html#combineFilesToPaths(java.io.File...))                                 | Combine files into a `Path` list.                |
| [`combineFilesToStrings(Collection<File>... collections`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/CollectionTools.html#combineFilesToStrings(java.util.Collection...))   | Combine `File` collections into a `String` list. |
| [`combineFilesToStrings(File... files`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/CollectionTools.html#combineFilesToStrings(java.io.File...))                             | Combine files into a `String` list.              |
| [`combinePathsToFiles(Collection<Path>... collections`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/CollectionTools.html#combinePathsToFiles(java.util.Collection...))       | Combine `Path` collections into a `String` list. |
| [`combinePathsToFiles(Path... paths`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/CollectionTools.html#combinePathsToFiles(java.nio.file.Path...))                           | Combine paths into a `String` list.              |
| [`combinePathsToStrings(Collection<Path>... collections`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/CollectionTools.html#combinePathsToStrings(java.util.Collection...))   | Combine `Path` collections into a `String` list. |
| [`combinePathsToStrings(Path... paths`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/CollectionTools.html#combinePathsToStrings(java.nio.file.Path...))                       | Combine  paths to a `String` list.               |
| [`combineStringsToFiles(Collection<String>... collections`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/CollectionTools.html#combineStringsToFiles(java.util.Collection...)) | Combine `String` collections into a `File` list. |
| [`combineStringsToFiles(String... strings`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/CollectionTools.html#combineStringsToFiles(java.lang.String...))                     | Combine strings into a `File` list.              |
| [`combineStringsToPaths(Collection<String>... collections`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/CollectionTools.html#combineStringsToPaths(java.util.Collection...)) | Combine `String` collections into a `Path` list. |
| [`combineStringsToPaths(String... strings)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/CollectionTools.html#combineStringsToPaths(java.lang.String...))                    | Combine strings into a `Path` list.              |

NOTE: All methods properly handle null values

## I/O Tools

The following static methods are provided:

| Method                                                                                                                                                                           | Description                                   |
|:---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|:----------------------------------------------|
| [`canExecute(File file)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/IOTools.html#canExecute(java.io.File))                                           | Check if a file is executable.                |
| [`canExecute(Path path)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/IOTools.html#canExecute(java.nio.file.Path))                                     | Check if a file path is exectuable.           |
| [`canExecute(String path)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/IOTools.html#canExecute(java.lang.String))                                     | Check if a file path is exectuable.           |
| [`exists(File file)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/IOTools.html#exists(java.io.File))                                                   | Check if a file exists.                       |
| [`exists(Path path)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/IOTools.html#exists(java.nio.file.Path))                                             | Check if a path exists.                       |
| [`exists(String path)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/IOTools.html#exits(java.lang.String))                                              | Check if a path exists.                       |
| [`isDirectory(File file)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/IOTools.html#isDirectory(java.io.File))                                         | Check if a file is a directory.               |
| [`isDirectory(Path path)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/IOTools.html#isDirectory(java.nio.file.Path))                                   | Check if a path is a directory.               |
| [`isDirectory(String path)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/IOTools.html#isDirectory(java.lang.String))                                   | Check if a path is a directory.               |
| [`mkdirs(File file)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/IOTools.html#mkdirs(java.io.File))                                                   | Make directories.                             |
| [`mkdirs(Path path)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/IOTools.html#mkdirs(java.nio.file.Path))                                             | Make directories.                             |
| [`mkdirs(String path)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/IOTools.html#mkdirs(java.lang.String))                                             | Make directories.                             |
| [`notExists(File file)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/IOTools.html#notExists(java.io.File))                                             | Check if a file exists.                       |
| [`notExists(Path path)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/IOTools.html#notExists(java.nio.file.Path))                                       | Check if a path exists.                       |
| [`notExists(String path)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/IOTools.html#notExits(java.lang.String))                                        | Check if a path exists.                       |
| [`resolveFile(File base, String... segments)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/IOTools.html#resolveFile(java.io.File,java.lang.String...)) | Resolve a file with additional path segments. |

*NOTE:* All methods properly handle `null` values

## Object Tools

The following static methods are provided:

| Method                                                                                                                                                                                                                                             | Description                                                                                                               |
|:---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|:--------------------------------------------------------------------------------------------------------------------------|
| [`allEmpty(Object... values)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/ObjectTools.html#allEmpty(java.lang.Object...))                                                                                               | Returns `true` if all provided values are empty.                                                                          |
| [`allNotEmpty(Object... values)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/ObjectTools.html#allNotEmpty(java.lang.Object...))                                                                                         | Returns `true` if all provided values are not empty.                                                                      |
| [`anyNotEmpty(Object... values)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/ObjectTools.html#anyNotEmpty(java.lang.Object...))                                                                                         | Returns `true` if at least one provided value is not empty.                                                               |
| [`isEmpty(Object value)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/ObjectTools.html#isEmpty(java.lang.Object))                                                                                                        | Returns `true` if the value is `null`, an empty `CharSequence`, empty `Collection`, empty `Map`, or empty array.          |
| [`isNotEmpty(Object value)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/ObjectTools.html#isNotEmpty(java.lang.Object))                                                                                                  | Returns `true` if the value is not `null` and not empty according to the rules of `isEmpty(Object)`.                      |
| [`requireAllEmpty(Map<?, ?> map, String message)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/ObjectTools.html#requireAllEmpty(java.util.Map,java.lang.String))                                                         | Throws `IllegalArgumentException` if the map is non-empty and any value is not empty.                                     |
| [`requireAllEmpty(Map<?, ?> map, String message, Object... args)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/ObjectTools.html#requireAllEmpty(java.util.Map,java.lang.String,java.lang.Object...))                     | Throws `IllegalArgumentException` if the map is non-empty and any value is not empty, formatting the message.             |
| [`requireAllEmpty(Object[] values, String message)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/ObjectTools.html#requireAllEmpty(java.lang.Object[],java.lang.String))                                                  | Throws `IllegalArgumentException` if any provided value is not empty.                                                     |
| [`requireAllEmpty(Object[] values, String message, Object... args)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/ObjectTools.html#requireAllEmpty(java.lang.Object[],java.lang.String,java.lang.Object...))              | Throws `IllegalArgumentException` if any provided value is not empty, formatting the message using `String.format`.       |
| [`requireAllEmpty(Collection<?> values, String message)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/ObjectTools.html#requireAllEmpty(java.util.Collection,java.lang.String))                                           | Throws `IllegalArgumentException` if the collection is `null` or contains any non-empty element.                          |
| [`requireAllEmpty(Collection<?> values, String message, Object... args)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/ObjectTools.html#requireAllEmpty(java.util.Collection,java.lang.String,java.lang.Object...))       | Throws `IllegalArgumentException` if the collection is `null` or contains any non-empty element, formatting the message.  |
| [`requireAllNotEmpty(Map<?, ?> map, String message)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/ObjectTools.html#requireAllNotEmpty(java.util.Map,java.lang.String))                                                   | Throws `IllegalArgumentException` if the map is `null`, empty, or contains any `null` or empty value.                     |
| [`requireAllNotEmpty(Map<?, ?> map, String message, Object... args)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/ObjectTools.html#requireAllNotEmpty(java.util.Map,java.lang.String,java.lang.Object...))               | Throws `IllegalArgumentException` if the map is `null`, empty, or contains any `null` or empty value, formatting message. |
| [`requireAllNotEmpty(Object[] values, String message)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/ObjectTools.html#requireAllNotEmpty(java.lang.Object[],java.lang.String))                                            | Throws `IllegalArgumentException` if any provided value is `null` or empty.                                               |
| [`requireAllNotEmpty(Object[] values, String message, Object... args)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/ObjectTools.html#requireAllNotEmpty(java.lang.Object[],java.lang.String,java.lang.Object...))        | Throws `IllegalArgumentException` if any provided value is `null` or empty, formatting the message using `String.format`. |
| [`requireAllNotEmpty(Collection<?> values, String message)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/ObjectTools.html#requireAllNotEmpty(java.util.Collection,java.lang.String))                                     | Throws `IllegalArgumentException` if the collection is `null`, empty, or contains any `null` or empty element.            |
| [`requireAllNotEmpty(Collection<?> values, String message, Object... args)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/ObjectTools.html#requireAllNotEmpty(java.util.Collection,java.lang.String,java.lang.Object...)) | Throws `IllegalArgumentException` if the collection is `null`, empty, or contains any `null` or empty element, formatting the message. |
| [`requireEmpty(T value, String message)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/ObjectTools.html#requireEmpty(T,java.lang.String))                                                                                 | Returns the original value, throwing `IllegalArgumentException` if it is not empty.                                       |
| [`requireEmpty(T value, String message, Object... args)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/ObjectTools.html#requireEmpty(T,java.lang.String,java.lang.Object...))                                             | Returns the original value, throwing `IllegalArgumentException` if it is not empty, formatting the message if possible.   |
| [`requireNotEmpty(T value, String message)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/ObjectTools.html#requireNotEmpty(T,java.lang.String))                                                                           | Returns the original value, throwing `IllegalArgumentException` if it is `null` or empty.                                 |
| [`requireNotEmpty(T value, String message, Object... args)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/ObjectTools.html#requireNotEmpty(T,java.lang.String,java.lang.Object...))                                       | Returns the original value, throwing `IllegalArgumentException` if it is `null` or empty, formatting the message.         |

*NOTE:* All methods properly handle `null` objects

## Process Executor

The [ProcessExecutor](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/ProcessExecutor.html)
class implements a generic process executor with timeout, I/O control, and output capture.

## System Tools

The following static methods are provided:

| Method                                                                                                              | Description                                                                                                      |
|:--------------------------------------------------------------------------------------------------------------------|:-----------------------------------------------------------------------------------------------------------------|
| [`isAix()`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/SystemTools.html#isAix())         | Determines if the current operating system is AIX.                                                               |
| [`isCygwin()`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/SystemTools.html#isCygwin())   | Determines if the current operating system is Cygwin.                                                            |
| [`isFreeBsd()`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/SystemTools.html#isFreeBsd()) | Determines if the current operating system is FreeBSD.                                                           |
| [`isLinux()`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/SystemTools.html#isLinux())     | Determines if the current operating system is Linux.                                                             |
| [`isMacOS()`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/SystemTools.html#isMacOS())     | Determines if the current operating system is macOS.                                                             |
| [`isMinGw()`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/SystemTools.html#isMingw())     | Determines if the current operating system is MinGW.                                                             |
| [`isOpenVms()`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/SystemTools.html#isOpenVms()) | Determines if the current operating system is OpenVMS.                                                           |
| [`isOtherOS()`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/SystemTools.html#isOtherOS()) | Determines if the current operating system is other than AIX, FreeBSD, Linux, macOS, OpenVMS, Solaris or Windows |
| [`isSolaris()`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/SystemTools.html#isSolaris()) | Determines if the current operating system is Solaris.                                                           |
| [`isWindows()`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/SystemTools.html#isWindows()) | Determines if the current operating system is Windows.                                                           |

## Text Tools

The following static methods are provided:

| Method                                                                                                                                                                            | Description                                      |
|:----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|:-------------------------------------------------|
| [`equalsIgnoreWhitespace(CharSequence str)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/TextTools.html#equalsIgnoreWhitespace(java.lang.CharSequence)) | Check if strings ae equals, ignoring whitespace. |
| [`isBlank(CharSequence str)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/TextTools.html#isBlank(java.lang.CharSequence))                               | Checks if a string is blank.                     |
| [`isBlank(CharSequence... strings)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/TextTools.html#isBlank(java.lang.CharSequence...))                     | Checks if strings are blank.                     |
| [`isBlank(Object... strings)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/TextTools.html#isBlank(java.lang.Object...))                                 | Checks if string objects are blank.              |
| [`isEmpty(CharSequence str)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/TextTools.html#isEmpty(java.lang.CharSequence))                               | Checks if a string is empty.                     |
| [`isEmpty(CharSequence... strings)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/TextTools.html#isEmpty(java.lang.CharSequence...))                     | Checks if strings are empty.                     |
| [`isEmpty(Object... strings)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/TextTools.html#isEmpty(java.lang.Object...))                                 | Checks if string objects are empty.              |
| [`isNotBlank(CharSequence str)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/TextTools.html#isNotBlank(java.lang.CharSequence))                         | Checks if a string is not blank.                 |
| [`isNotBlank(CharSequence... strings)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/TextTools.html#isNotBlank(java.lang.CharSequence...))               | Checks if strings are not blank.                 |
| [`isNotBlank(Objects... strings)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/TextTools.html#isNotBlank(java.lang.Object...))                          | Checks if string objects are not blank.          |
| [`isNotEmpty(CharSequence str)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/TextTools.html#isNotEmpty(java.lang.CharSequence))                         | Checks if a string is not empty.                 |
| [`isNotEmpty(CharSequence... strings)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/TextTools.html#isNotEmpty(java.lang.CharSequence...))               | Checks if strings are not empty.                 |
| [`isNotEmpty(Objects... strings)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/TextTools.html#isNotEmpty(java.lang.Object...))                          | Checks if string objects are not empty.          |

*NOTE:* All methods properly handle `null` strings.
