[![License](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Java](https://img.shields.io/badge/java-17%2B-blue)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
[![bld](https://img.shields.io/badge/3.0.1-FA9052?label=bld&labelColor=2392FF)](https://rife2.com/bld)
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

## Collection Tools

The following static methods are provided:

| Method                                                                                                                                                                                                 | Description                                      |
| :----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | :----------------------------------------------- |
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

| Method                                                                                                                                                                                                            | Description                                                                                 |
|:------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|:--------------------------------------------------------------------------------------------|
| [`canExecute(File file)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/IOTools.html#canExecute(java.io.File))                                                                            | Check if a file is executable.                                                              |
| [`canExecute(Path path)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/IOTools.html#canExecute(java.nio.file.Path))                                                                      | Check if a path is executable.                                                              |
| [`canExecute(String path)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/IOTools.html#canExecute(java.lang.String))                                                                      | Check if a path string is executable.                                                       |
| [`createDirs(File file)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/IOTools.html#createDirs(java.io.File))                                                                            | Create directory including any nonexistent parent directories.                              |
| [`createDirs(Path path)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/IOTools.html#createDirs(java.nio.file.Path))                                                                      | Create directory including any nonexistent parent directories.                              |
| [`createDirs(String path)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/IOTools.html#createDirs(java.lang.String))                                                                      | Create directory including any nonexistent parent directories.                              |
| [`exists(File file)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/IOTools.html#exists(java.io.File))                                                                                    | Check if a file exists.                                                                     |
| [`exists(Path path)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/IOTools.html#exists(java.nio.file.Path))                                                                              | Check if a path exists.                                                                     |
| [`exists(String path)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/IOTools.html#exists(java.lang.String))                                                                              | Check if a path string exists.                                                              |
| [`findFilesByExtensions(Path directory, String... extensions)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/IOTools.html#findFilesByExtensions(java.nio.file.Path,java.lang.String...)) | Find regular files directly in a directory by extensions (non-recursive, case-insensitive). |
| [`isDirectory(File file)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/IOTools.html#isDirectory(java.io.File))                                                                          | Check if a file is a directory.                                                             |
| [`isDirectory(Path path)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/IOTools.html#isDirectory(java.nio.file.Path))                                                                    | Check if a path is a directory.                                                             |
| [`isDirectory(String path)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/IOTools.html#isDirectory(java.lang.String))                                                                    | Check if a path string is a directory.                                                      |
| [`mkdirs(File file)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/IOTools.html#mkdirs(java.io.File))                                                                                    | Create directory including parents, returning false on failure.                             |
| [`mkdirs(Path path)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/IOTools.html#mkdirs(java.nio.file.Path))                                                                              | Create directory including parents, returning false on failure.                             |
| [`mkdirs(String path)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/IOTools.html#mkdirs(java.lang.String))                                                                              | Create directory including parents, returning false on failure.                             |
| [`notExists(File file)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/IOTools.html#notExists(java.io.File))                                                                              | Check if a file does not exist.                                                             |
| [`notExists(Path path)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/IOTools.html#notExists(java.nio.file.Path))                                                                        | Check if a path does not exist.                                                             |
| [`notExists(String path)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/IOTools.html#notExists(java.lang.String))                                                                        | Check if a path string does not exist.                                                      |
| [`resolveFile(File base, String... segments)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/IOTools.html#resolveFile(java.io.File,java.lang.String...))                                  | Resolve a file path by joining a base with additional segments.                             |

*NOTE:* All methods properly handle `null` values

## Object Tools

The following static methods are provided:

| Method                                                                                                                                                                                                             | Description                                                                                                                                                                                                                                                                             |
|:-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|:----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| [`allEmpty(Object value)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/ObjectTools.html#allEmpty(java.lang.Object))                                                                      | Checks that the value and all nested elements are empty. Returns `true` if so. `null` returns `true`. For arrays, `Collection`, or `Map`, all elements/entries must be empty. For `Map`, both keys and values are checked. Empty container returns `true` vacuously.                    |
| [`allNotEmpty(Object value)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/ObjectTools.html#allNotEmpty(java.lang.Object))                                                                | Checks that the value and all nested elements are not empty. Returns `true` if so. `null` returns `false`. For containers, must be non-empty and all elements/entries must be non-`null` and not empty. For `Map`, both keys and values are checked. Empty container returns `false`.   |
| [`anyEmpty(Object value)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/ObjectTools.html#anyEmpty(java.lang.Object))                                                                      | Checks that the value or any nested element is empty. Returns `true` if so. `null` returns `true`. For containers, returns `true` if empty or any element/entry is `null` or empty. For `Map`, both keys and values are checked.                                                        |
| [`anyNotEmpty(Object value)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/ObjectTools.html#anyNotEmpty(java.lang.Object))                                                                | Checks that the value or any nested element is not empty. Returns `true` if so. `null` returns `false`. For containers, returns `true` if any element/entry is non-`null` and not empty. For `Map`, both keys and values are checked.                                                   |
| [`isEmpty(Object value)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/ObjectTools.html#isEmpty(java.lang.Object))                                                                        | Checks that the value is `null` or empty. Returns `true` if so. For arrays, `Collection`, or `Map`, only checks if the container itself is empty. Does not check nested elements.                                                                                                       |
| [`isNotEmpty(Object value)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/ObjectTools.html#isNotEmpty(java.lang.Object))                                                                  | Checks that the value is not `null` and not empty. Returns `true` if so. Does not check nested elements.                                                                                                                                                                                |
| [`requireEmpty(T value, String message)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/ObjectTools.html#requireEmpty(T,java.lang.String))                                                 | Requires the value and all nested elements to be empty. Throws `NullPointerException` if value or any element is `null`. Throws `IllegalArgumentException` if not empty.                                                                                                                |
| [`requireEmpty(T value, Supplier<String> messageSupplier)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/ObjectTools.html#requireEmpty(T,java.util.function.Supplier))                    | Requires the value and all nested elements to be empty. Throws `NullPointerException` if value or any element is `null`. Throws `IllegalArgumentException` with message from supplier if not empty.                                                                                     |
| [`requireNonNull(T value, String context)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/ObjectTools.html#requireNonNull(T,java.lang.String))                                             | Requires the value and all nested elements to be not `null`. Throws `NullPointerException` if value or any element is `null`. Checks up to 128 nesting levels deep; logs warning if depth limit reached.                                                                                |
| [`requireNonNull(T value, Supplier<String> messageSupplier)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/ObjectTools.html#requireNonNull(T,java.util.function.Supplier))                | Requires the value and all nested elements to be not `null`. Throws `NullPointerException` with message from supplier if value or any element is `null`. Checks up to 128 nesting levels deep; logs warning if depth limit reached.                                                     |
| [`requireNotEmpty(T value, String context)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/ObjectTools.html#requireNotEmpty(T,java.lang.String))                                           | Requires the value and all nested elements to be not `null` and not empty. Throws `NullPointerException` if `null`. Throws `IllegalArgumentException` if empty.                                                                                                                         |
| [`requireNotEmpty(T value, String nullMessage, String emptyMessage)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/ObjectTools.html#requireNotEmpty(T,java.lang.String,java.lang.String)) | Requires the value and all nested elements to be not `null` and not empty. Throws `NullPointerException` with `nullMessage` if `null`. Throws `IllegalArgumentException` with `emptyMessage` if empty.                                                                                  |
| [`requireNegative(T value, String context)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/ObjectTools.html#requireNegative(T,java.lang.String))                                           | Requires the value to be negative. Works with `Integer`, `Long`, `Double`, `Float`, `Byte`, `Short`, `BigInteger`, `BigDecimal`. Throws `NullPointerException` if `null`. Throws `IllegalArgumentException` if zero or positive. Throws `IllegalArgumentException` if unsupported type. |
| [`requireNegative(T value, Supplier<String> messageSupplier)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/ObjectTools.html#requireNegative(T,java.util.function.Supplier))              | Requires the value to be negative. Throws `NullPointerException` if `null`. Throws `IllegalArgumentException` with message from supplier if zero or positive. Throws `IllegalArgumentException` if unsupported type.                                                                    |
| [`requireNonNegative(T value, String context)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/ObjectTools.html#requireNonNegative(T,java.lang.String))                                     | Requires the value to be zero or positive. Works with numeric `Comparable` types. Throws `NullPointerException` if `null`. Throws `IllegalArgumentException` if negative. Throws `IllegalArgumentException` if unsupported type.                                                        |
| [`requireNonNegative(T value, Supplier<String> messageSupplier)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/ObjectTools.html#requireNonNegative(T,java.util.function.Supplier))        | Requires the value to be zero or positive. Throws `NullPointerException` if `null`. Throws `IllegalArgumentException` with message from supplier if negative. Throws `IllegalArgumentException` if unsupported type.                                                                    |
| [`requirePositive(T value, String context)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/ObjectTools.html#requirePositive(T,java.lang.String))                                           | Requires the value to be positive. Works with numeric `Comparable` types. Throws `NullPointerException` if `null`. Throws `IllegalArgumentException` if zero or negative. Throws `IllegalArgumentException` if unsupported type.                                                        |
| [`requirePositive(T value, Supplier<String> messageSupplier)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/ObjectTools.html#requirePositive(T,java.util.function.Supplier))              | Requires the value to be positive. Throws `NullPointerException` if `null`. Throws `IllegalArgumentException` with message from supplier if zero or negative. Throws `IllegalArgumentException` if unsupported type.                                                                    |

## Path Tools

The following static methods are provided:

| Method                                                                                                                                                                       | Description                                                                                                      |
|:-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------|:-----------------------------------------------------------------------------------------------------------------|
| [`formatCommandLine(Collection<String> args)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/PathTools.html#formatCommandLine(java.util.Collection)) | Formats command-line arguments into a single string suitable for logging and safe copy/paste into a POSIX shell. |
| [`joinClasspath()`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/PathTools.html#joinClasspath())                                                    | Returns an empty classpath string to disambiguate zero-arg calls.                                                |
| [`joinClasspath(Collection<File>... files)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/PathTools.html#joinClasspath(java.util.Collection...))    | Joins multiple collections of files into a classpath string using normalized absolute paths.                     |
| [`joinClasspath(File... files)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/PathTools.html#joinClasspath(java.io.File...))                        | Joins multiple files into a classpath string using normalized absolute paths.                                    |
| [`joinClasspath(String... paths)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/PathTools.html#joinClasspath(java.lang.String...))                  | Joins non-blank string paths into a classpath string.                                                            |
| [`joinClasspath(Collection<Path> paths)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/PathTools.html#joinClasspath(java.util.Collection))          | Joins a collection of paths into a classpath string using normalized absolute paths.                             |
| [`joinClasspath(Path... paths)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/PathTools.html#joinClasspath(java.nio.file.Path...))                  | Joins multiple paths into a classpath string using normalized absolute paths.                                    |

## Process Executor

The [ProcessExecutor](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/ProcessExecutor.html)
class is a generic process executor with timeout, I/O control, and output capture.

It handles process tree cleanup and stream management for Windows compatibility.
Not thread-safe. Configure and execute from a single thread.

To use, build the command and execute:

```java
var result = new ProcessExecutor()
    .command("git", "status")
    .workDir(new File("."))
    .timeout(30)
    .execute();

if (!result.isSuccess()) {
    System.out.println(result.output());
}
```

The following methods are provided:

| Method                                                                                                                                                                                | Description                                                                |
|:--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|:---------------------------------------------------------------------------|
| [`command(String... args)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/ProcessExecutor.html#command(java.lang.String...))                                  | Sets the command and arguments, replacing any previous command.            |
| [`command(Collection<String> args)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/ProcessExecutor.html#command(java.util.Collection))                        | Sets the command from a collection, replacing any previous command.        |
| [`command()`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/ProcessExecutor.html#command())                                                                   | Returns the mutable command list.                                          |
| [`env(String name, String value)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/ProcessExecutor.html#env(java.lang.String,java.lang.String))                 | Adds an environment variable.                                              |
| [`env(Map<String,String> vars)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/ProcessExecutor.html#env(java.util.Map))                                       | Adds environment variables.                                                |
| [`env()`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/ProcessExecutor.html#env())                                                                           | Returns the mutable environment map.                                       |
| [`workDir(File dir)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/ProcessExecutor.html#workDir(java.io.File))                                               | Configures the working directory.                                          |
| [`workDir(Path dir)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/ProcessExecutor.html#workDir(java.nio.file.Path))                                         | Configures the working directory.                                          |
| [`workDir(String dir)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/ProcessExecutor.html#workDir(java.lang.String))                                         | Configures the working directory.                                          |
| [`workDir()`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/ProcessExecutor.html#workDir())                                                                   | Returns the working directory, or null if not set.                         |
| [`timeout(long timeout)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/ProcessExecutor.html#timeout(long))                                                   | Configures the timeout in seconds; negative disables it.                   |
| [`timeout()`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/ProcessExecutor.html#timeout())                                                                   | Returns the timeout in seconds.                                            |
| [`inheritIO(boolean inheritIO)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/ProcessExecutor.html#inheritIO(boolean))                                       | Configures whether the child inherits the JVM's I/O streams.               |
| [`inheritIO()`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/ProcessExecutor.html#inheritIO())                                                               | Returns whether I/O is inherited.                                          |
| [`outputConsumer(Consumer<String> consumer)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/ProcessExecutor.html#outputConsumer(java.util.function.Consumer)) | Sets a consumer to receive output lines as they arrive.                    |
| [`execute()`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/ProcessExecutor.html#execute())                                                                   | Executes the command and returns the result.                               |
| [`ProcessResult`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/ProcessExecutor.html)                                                                         | Record containing `exitCode`, `output`, and `timedOut` with `isSuccess()`. |

## Sandbox

The [Sandbox](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/Sandbox.html)
class manages an isolated download directory for a bld extension.

Each extension gets its own subdirectory under `lib/bld/.sandbox/<extensionName>`
where its compile and runtime dependencies are downloaded. To avoid unnecessary
re-downloads, the state is tracked with a hash-based snapshot in
`lib/bld/.sandbox/sandbox.snapshot`. If the snapshot is valid,
`downloadDependencies` becomes a no-op.

Downloads are serialized per-extension within a single JVM. Concurrent
processes sharing the same sandbox directory are not coordinated; the worst
case is a redundant download.

To use, create a `Sandbox` for your extension name and download:

```java
var sandbox = new Sandbox("my-extension", project);

var dir = sandbox.downloadDependencies(
    List.of(dependency("com.example", "my-lib", version(1,0,0))),
    List.of(MAVEN_CENTRAL),
    new VersionResolution(null)
);
// artifacts in lib/bld/.sandbox/my-extension
```

The following methods are provided:

| Method                                                                                                                                                                | Description                                                        |
|:----------------------------------------------------------------------------------------------------------------------------------------------------------------------|:-------------------------------------------------------------------|
| [`Sandbox(String, BaseProject)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/Sandbox.html#%3Cinit%3E(java.lang.String,rife.bld.BaseProject)) | Creates a sandbox for the given extension name.                    |
| [`downloadDependencies(List<Dependency>, List<Repository>)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/Sandbox.html#downloadDependencies(java.util.List,java.util.List)) | Downloads dependencies with default resolution into the extension root. |
| [`downloadDependencies(List<Dependency>, List<Repository>, VersionResolution)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/Sandbox.html#downloadDependencies(java.util.List,java.util.List,rife.bld.dependencies.VersionResolution)) | Downloads dependencies with a resolution into the extension root, skips if snapshot is valid. |
| [`downloadDependencies(List<Dependency>, List<Repository>, VersionResolution, Path)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/Sandbox.html#downloadDependencies(java.util.List,java.util.List,rife.bld.dependencies.VersionResolution,java.nio.file.Path)) | Downloads dependencies into the sandbox, optionally under a subdirectory. |
| [`downloadDependencies(List<Dependency>, List<Repository>, VersionResolution, File)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/Sandbox.html#downloadDependencies(java.util.List,java.util.List,rife.bld.dependencies.VersionResolution,java.io.File)) | File-based overload for backward compatibility.                    |
| [`getSandboxDirectory()`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/Sandbox.html#getSandboxDirectory()) | Returns `lib/bld/.sandbox`.                                        |
| [`getSandboxDirectoryAsFile()`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/Sandbox.html#getSandboxDirectoryAsFile()) | Returns `lib/bld/.sandbox` as a `File`.                            |
| [`getSandboxExtensionDirectory()`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/Sandbox.html#getSandboxExtensionDirectory()) | Returns `lib/bld/.sandbox/<extensionName>`.                        |
| [`getSandboxExtensionDirectoryAsFile()`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/Sandbox.html#getSandboxExtensionDirectoryAsFile()) | Returns `lib/bld/.sandbox/<extensionName>` as a `File`.            |

## System Tools

The following static methods are provided:

| Method                                                                                                              | Description                                                                                                      |
| :------------------------------------------------------------------------------------------------------------------ | :--------------------------------------------------------------------------------------------------------------- |
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

| Method                                                                                                                                                                                                                                                                                   | Description                                                                                                                      |
|:-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|:---------------------------------------------------------------------------------------------------------------------------------|
| [`equalsIgnoreWhitespace(CharSequence... strings)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/TextTools.html#equalsIgnoreWhitespace(java.lang.CharSequence...))                                                                                              | Compares two or more character sequences by removing all whitespace. Returns `false` if `null` or fewer than 2 elements.         |
| [`isBlank(CharSequence str)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/TextTools.html#isBlank(java.lang.CharSequence))                                                                                                                                      | Checks if a character sequence is `null`, empty, or contains only whitespace characters.                                         |
| [`isBlank(CharSequence... strings)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/TextTools.html#isBlank(java.lang.CharSequence...))                                                                                                                            | Checks if all character sequences are `null`, empty, or whitespace-only. Returns `true` for `null` or empty array.               |
| [`isBlank(Object... objects)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/TextTools.html#isBlank(java.lang.Object...))                                                                                                                                        | Checks if all objects are `null`, empty, or whitespace-only via `toString()`.                                                    |
| [`isEmpty(CharSequence str)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/TextTools.html#isEmpty(java.lang.CharSequence))                                                                                                                                      | Checks if a character sequence is `null` or empty.                                                                               |
| [`isEmpty(CharSequence... strings)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/TextTools.html#isEmpty(java.lang.CharSequence...))                                                                                                                            | Checks if all character sequences are `null` or empty. Returns `true` for `null` or empty array.                                 |
| [`isEmpty(Object... objects)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/TextTools.html#isEmpty(java.lang.Object...))                                                                                                                                        | Checks if all objects are `null` or their string representations are empty.                                                      |
| [`isNotBlank(CharSequence str)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/TextTools.html#isNotBlank(java.lang.CharSequence))                                                                                                                                | Checks if a character sequence is not `null`, not empty, and not whitespace-only.                                                |
| [`isNotBlank(CharSequence... strings)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/TextTools.html#isNotBlank(java.lang.CharSequence...))                                                                                                                      | Checks if all character sequences are not `null`, not empty, and not whitespace-only. Returns `false` for `null` or empty array. |
| [`isNotBlank(Object... objects)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/TextTools.html#isNotBlank(java.lang.Object...))                                                                                                                                  | Checks if all objects are not `null`, not empty, and not whitespace-only via `toString()`.                                       |
| [`isNotEmpty(CharSequence str)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/TextTools.html#isNotEmpty(java.lang.CharSequence))                                                                                                                                | Checks if a character sequence is not `null` and not empty.                                                                      |
| [`isNotEmpty(CharSequence... strings)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/TextTools.html#isNotEmpty(java.lang.CharSequence...))                                                                                                                      | Checks if all character sequences are not `null` and not empty. Returns `false` for `null` or empty array.                       |
| [`isNotEmpty(Object... objects)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/TextTools.html#isNotEmpty(java.lang.Object...))                                                                                                                                  | Checks if all objects are not `null` and their string representations are not empty.                                             |
| [`requireNotBlank(T str, Supplier<String> nullMessage, Supplier<String> blankMessage)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/TextTools.html#requireNotBlank(java.lang.CharSequence,java.util.function.Supplier,java.util.function.Supplier))            | Checks that the character sequence is not `null`, not empty, and not whitespace-only. Throws with supplied messages.             |
| [`requireNotBlank(T str, String context)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/TextTools.html#requireNotBlank(java.lang.CharSequence,java.lang.String))                                                                                                | Checks that the character sequence is not blank. Uses `context` for exception messages.                                          |
| [`requireNotBlank(Collection<T> coll, Supplier<String> nullMessage, Supplier<String> blankMessage)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/TextTools.html#requireNotBlank(java.util.Collection,java.util.function.Supplier,java.util.function.Supplier)) | Checks that the collection is not `null`, not empty, and contains no blank elements.                                             |
| [`requireNotBlank(Collection<T> coll, String context)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/TextTools.html#requireNotBlank(java.util.Collection,java.lang.String))                                                                                     | Checks that the collection is not blank. Uses `context` for exception messages.                                                  |
| [`requireNotBlank(Supplier<String> nullMessage, Supplier<String> blankMessage, T... elements)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/TextTools.html#requireNotBlank(java.util.function.Supplier,java.util.function.Supplier,java.lang.CharSequence...)) | Checks that the varargs array is not `null`, not empty, and contains no blank elements.                                          |
| [`requireNotBlank(String context, T... elements)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/TextTools.html#requireNotBlank(java.lang.String,java.lang.CharSequence...))                                                                                     | Checks that the varargs array is not blank. Uses `context` for exception messages.                                               |
| [`requireNotEmpty(T str, Supplier<String> nullMessage, Supplier<String> emptyMessage)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/TextTools.html#requireNotEmpty(java.lang.CharSequence,java.util.function.Supplier,java.util.function.Supplier))            | Checks that the character sequence is not `null` and not empty. Throws with supplied messages.                                   |
| [`requireNotEmpty(T str, String context)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/TextTools.html#requireNotEmpty(java.lang.CharSequence,java.lang.String))                                                                                                | Checks that the character sequence is not empty. Uses `context` for exception messages.                                          |
| [`requireNotEmpty(Collection<T> coll, Supplier<String> nullMessage, Supplier<String> emptyMessage)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/TextTools.html#requireNotEmpty(java.util.Collection,java.util.function.Supplier,java.util.function.Supplier)) | Checks that the collection is not `null`, not empty, and contains no empty elements.                                             |
| [`requireNotEmpty(Collection<T> coll, String context)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/TextTools.html#requireNotEmpty(java.util.Collection,java.lang.String))                                                                                     | Checks that the collection is not empty. Uses `context` for exception messages.                                                  |
| [`requireNotEmpty(Supplier<String> nullMessage, Supplier<String> emptyMessage, T... elements)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/TextTools.html#requireNotEmpty(java.util.function.Supplier,java.util.function.Supplier,java.lang.CharSequence...)) | Checks that the varargs array is not `null`, not empty, and contains no empty elements.                                          |
| [`requireNotEmpty(String context, T... elements)`](https://rife2.github.io/bld-extensions-tools/rife/bld/extension/tools/TextTools.html#requireNotEmpty(java.lang.String,java.lang.CharSequence...))                                                                                     | Checks that the varargs array is not empty. Uses `context` for exception messages.                                               |

*NOTE:* All methods properly handle `null` strings.
