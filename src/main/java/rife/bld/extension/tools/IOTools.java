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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * I/O Tools.
 *
 * <p>Utility methods for common file system operations including existence checks,
 * executability checks, directory creation, and path resolution. All methods
 * accept {@code null} inputs and return {@code false} (or an appropriate default)
 * rather than throwing {@link NullPointerException}.</p>
 *
 * @author <a href="https://erik.thauvin.net/">Erik C. Thauvin</a>
 * @since 1.0
 */
@NullMarked
public final class IOTools {

    private IOTools() {
        // no-op
    }

    /**
     * Determines if the specified file exists, is a file, and is executable.
     *
     * @param file the file to be checked
     * @return {@code true} if the file exists, is a file, and can be executed;
     * {@code false} otherwise
     * @since 1.0
     */
    public static boolean canExecute(@Nullable File file) {
        return file != null && file.isFile() && file.canExecute();
    }

    /**
     * Determines if the specified path exists, is a regular file, and is executable.
     *
     * @param path the path to be checked
     * @return {@code true} if the path exists, is a regular file, and can be executed;
     * {@code false} otherwise
     * @since 1.0
     */
    public static boolean canExecute(@Nullable Path path) {
        return path != null && Files.isRegularFile(path) && Files.isExecutable(path);
    }

    /**
     * Determines if the file at the specified path string exists, is a regular file, and is executable.
     *
     * @param path the path string to be checked
     * @return {@code true} if the path exists, is a regular file, and can be executed;
     * {@code false} otherwise, including when the path is {@code null}, blank, or invalid
     * @since 1.0
     */
    public static boolean canExecute(@Nullable String path) {
        if (TextTools.isBlank(path)) {
            return false;
        }
        try {
            return canExecute(Path.of(path));
        } catch (InvalidPathException e) {
            return false;
        }
    }

    /**
     * Creates the directory named by the given path, including any
     * nonexistent parent directories.
     *
     * <p>If {@code path} is {@code null}, this method does nothing and returns {@code false}.
     * If the directory already exists, this method does nothing and returns {@code true}.
     *
     * @param path the directory to create, may be {@code null}
     * @return {@code true} if the directory exists after the call, {@code false} if {@code path} is {@code null}
     * @throws java.nio.file.FileAlreadyExistsException if {@code path} exists and is not a directory
     * @throws java.nio.file.AccessDeniedException      if the process does not have permission to create the directory
     * @throws IOException                              if an I/O error occurs while creating the directory
     * @throws SecurityException                        if a security manager denies write access
     * @since 1.3
     */
    public static boolean createDirs(@Nullable Path path) throws IOException {
        if (path == null) {
            return false;
        }
        Files.createDirectories(path);
        return true;
    }

    /**
     * Creates the directory named by the given file, including any
     * nonexistent parent directories.
     *
     * <p>If {@code file} is {@code null}, this method does nothing and returns {@code false}.
     *
     * @param file the directory to create, may be {@code null}
     * @return {@code true} if the directory exists after the call, {@code false} if {@code file} is {@code null}
     * @throws java.nio.file.FileAlreadyExistsException if {@code file} exists and is not a directory
     * @throws java.nio.file.AccessDeniedException      if the process does not have permission to create the directory
     * @throws IOException                              if an I/O error occurs while creating the directory
     * @throws SecurityException                        if a security manager denies write access
     * @since 1.3
     */
    public static boolean createDirs(@Nullable File file) throws IOException {
        return file != null && createDirs(file.toPath());
    }

    /**
     * Creates the directory named by the given path string, including any
     * nonexistent parent directories.
     *
     * <p>If {@code path} is {@code null} or blank, this method does nothing and returns {@code false}.
     *
     * @param path the path string of the directory to create, may be {@code null} or blank
     * @return {@code true} if the directory exists after the call, {@code false} if {@code path} is {@code null} or blank
     * @throws java.nio.file.InvalidPathException       if {@code path} cannot be converted to a {@code Path}
     * @throws java.nio.file.FileAlreadyExistsException if {@code path} exists and is not a directory
     * @throws java.nio.file.AccessDeniedException      if the process does not have permission to create the directory
     * @throws IOException                              if an I/O error occurs while creating the directory
     * @throws SecurityException                        if a security manager denies write access
     * @since 1.3
     */
    public static boolean createDirs(@Nullable String path) throws IOException {
        return TextTools.isNotBlank(path) && createDirs(Path.of(path));
    }

    /**
     * Checks if the specified file exists.
     *
     * @param file the file to check for existence
     * @return {@code true} if the file is not {@code null} and exists;
     * {@code false} otherwise
     * @since 1.0
     */
    public static boolean exists(@Nullable File file) {
        return file != null && file.exists();
    }

    /**
     * Checks if the specified path exists.
     *
     * @param path the path to check for existence
     * @return {@code true} if the path is not {@code null} and exists;
     * {@code false} otherwise
     * @since 1.0
     */
    public static boolean exists(@Nullable Path path) {
        return path != null && Files.exists(path);
    }

    /**
     * Checks whether a file or directory exists at the specified path.
     *
     * @param path the file system path to check for existence
     * @return {@code true} if the path is not {@code null} and a file or directory
     * exists at the specified path; {@code false} otherwise, including when the path is blank or invalid
     * @since 1.0
     */
    public static boolean exists(@Nullable String path) {
        if (TextTools.isBlank(path)) {
            return false;
        }
        try {
            return Files.exists(Path.of(path));
        } catch (InvalidPathException e) {
            return false;
        }
    }

    /**
     * Finds regular files located directly in {@code directory} whose file name
     * ends with one of the given extensions (case-insensitive).
     * <p>
     * This is non-recursive - it does not search subdirectories. If the directory
     * cannot be read, an empty list is returned.
     *
     * @param directory  the directory to list, must not be null
     * @param extensions one or more extensions to match, e.g. ".java" or "java"
     * @return an unmodifiable list of matching files; never null, may be empty
     * @throws NullPointerException if directory or extensions is null
     * @since 1.4
     */
    public static List<Path> findFilesByExtensions(Path directory, String... extensions) {
        ObjectTools.requireNonNull(directory, "directory");
        ObjectTools.requireNonNull(extensions, "extensions");

        if (extensions.length == 0) {
            return List.of();
        }

        // Normalize: ".JAVA" -> ".java", "java" -> ".java"
        final Set<String> normalizedExtensions = Arrays.stream(extensions)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(s -> s.startsWith(".") ? s : "." + s)
                .map(s -> s.toLowerCase(Locale.ROOT))
                .collect(Collectors.toUnmodifiableSet());

        if (normalizedExtensions.isEmpty()) {
            return List.of();
        }

        try (var stream = Files.list(directory)) {
            return stream
                    .filter(Files::isRegularFile)
                    .filter(path -> {
                        var fileName = path.getFileName();
                        if (fileName == null) {
                            return false;
                        }
                        var lowerName = fileName.toString().toLowerCase(Locale.ROOT);
                        return normalizedExtensions.stream().anyMatch(lowerName::endsWith);
                    })
                    .toList();
        } catch (IOException e) {
            // Directory does not exist, not a directory, or not readable
            return List.of();
        }
    }

    /**
     * Determines if the specified {@code File} is a directory.
     *
     * @param file the {@code File} object to be checked; if {@code null}, returns {@code false}
     * @return {@code true} if the file exists and is a directory; {@code false} otherwise
     * @since 1.0
     */
    public static boolean isDirectory(@Nullable File file) {
        return file != null && file.isDirectory();
    }

    /**
     * Determines if the specified {@code Path} represents an existing directory.
     *
     * @param path the {@code Path} object to be checked; if {@code null}, returns {@code false}
     * @return {@code true} if the path exists and is a directory; {@code false} otherwise
     * @since 1.0
     */
    public static boolean isDirectory(@Nullable Path path) {
        return path != null && Files.isDirectory(path);
    }

    /**
     * Determines if the specified path string represents an existing directory.
     *
     * @param path the path string to be checked; if {@code null} or blank, returns {@code false}
     * @return {@code true} if the specified path exists and is a directory;
     * {@code false} otherwise, including when the path string is invalid
     * @since 1.0
     */
    public static boolean isDirectory(@Nullable String path) {
        if (TextTools.isBlank(path)) {
            return false;
        }
        try {
            return Files.isDirectory(Path.of(path));
        } catch (InvalidPathException e) {
            return false;
        }
    }

    /**
     * Creates the directory specified by the given file, including any
     * nonexistent parent directories as necessary.
     *
     * <p>Unlike {@link #createDirs(File)}, this method catches all exceptions
     * and returns {@code false} on failure instead of throwing.
     *
     * @param file the directory to be created
     * @return {@code true} if the directory was created successfully or already exists;
     * {@code false} if the directory could not be created or {@code file} is {@code null}
     * @since 1.0
     */
    public static boolean mkdirs(@Nullable File file) {
        if (file == null) {
            return false;
        }
        try {
            Files.createDirectories(file.toPath());
            return true;
        } catch (IOException | SecurityException e) {
            return false;
        }
    }

    /**
     * Creates the directory specified by the given path, including any
     * nonexistent parent directories as necessary.
     *
     * <p>Unlike {@link #createDirs(Path)}, this method catches all exceptions
     * and returns {@code false} on failure instead of throwing.
     *
     * @param path the directory to be created
     * @return {@code true} if the directory was created successfully or already exists;
     * {@code false} if the directory could not be created or {@code path} is {@code null}
     * @since 1.0
     */
    public static boolean mkdirs(@Nullable Path path) {
        if (path == null) {
            return false;
        }
        try {
            Files.createDirectories(path);
            return true;
        } catch (IOException | SecurityException e) {
            return false;
        }
    }

    /**
     * Creates the directory specified by the given path string, including any
     * nonexistent parent directories as necessary.
     *
     * <p>Unlike {@link #createDirs(String)}, this method catches all exceptions
     * and returns {@code false} on failure instead of throwing.
     *
     * @param path the directory to be created
     * @return {@code true} if the directory was created successfully or already exists;
     * {@code false} if the directory could not be created or {@code path} is {@code null}, blank, or invalid
     * @since 1.0
     */
    public static boolean mkdirs(@Nullable String path) {
        if (TextTools.isBlank(path)) {
            return false;
        }
        try {
            // InvalidPathException is not caught by mkdirs(Path), so handle it here
            return mkdirs(Path.of(path));
        } catch (InvalidPathException e) {
            return false;
        }
    }

    /**
     * Checks if the specified file does not exist.
     *
     * <p><b>Note:</b> This method returns {@code true} for both {@code null} input and
     * non-existent files. This diverges from {@link Files#notExists(Path, java.nio.file.LinkOption...)}
     * which returns {@code false} when existence cannot be determined. The behavior here
     * is a deliberate choice to simplify null-checking call sites.</p>
     *
     * @param file the file to check for non-existence
     * @return {@code true} if the file is {@code null} or does not exist;
     * {@code false} otherwise
     * @since 1.0
     */
    public static boolean notExists(@Nullable File file) {
        return !exists(file);
    }

    /**
     * Checks if the specified path does not exist.
     *
     * <p><b>Note:</b> This method returns {@code true} for both {@code null} input and
     * non-existent paths. This diverges from {@link Files#notExists(Path, java.nio.file.LinkOption...)}
     * which returns {@code false} when existence cannot be determined. The behavior here
     * is a deliberate choice to simplify null-checking call sites.</p>
     *
     * @param path the path to check for non-existence
     * @return {@code true} if the path is {@code null} or does not exist;
     * {@code false} otherwise
     * @since 1.0
     */
    public static boolean notExists(@Nullable Path path) {
        return !exists(path);
    }

    /**
     * Checks whether a file or directory does not exist at the specified path.
     *
     * <p><b>Note:</b> This method returns {@code true} for {@code null}, blank, invalid,
     * or non-existent paths. This diverges from {@link Files#notExists(Path, java.nio.file.LinkOption...)}
     * which returns {@code false} when existence cannot be determined. The behavior here
     * is a deliberate choice to simplify null-checking call sites.</p>
     *
     * @param path the file system path to check for non-existence
     * @return {@code true} if the path is {@code null} or no file or directory
     * exists at the specified path; {@code false} otherwise
     * @since 1.0
     */
    public static boolean notExists(@Nullable String path) {
        return !exists(path);
    }

    /**
     * Resolves a file path by joining a base file with additional path segments.
     *
     * <p>This method constructs a file path by appending one or more path segments
     * to a base file. {@code null} or empty segments are silently skipped. To keep
     * resolution relative to {@code base}, segments starting with {@code "/"} have
     * the leading slash stripped before resolving. This is a deliberate design choice
     * that prevents absolute segments from resetting the path to the filesystem root,
     * which would violate the expectation that resolution is relative to the base.
     * Callers passing absolute Unix paths should pre-strip the slash themselves if
     * they intend root-relative semantics.</p>
     * <p>If {@code base} is {@code null}, this behaves like {@code new File("")}:
     * segments are resolved against the current directory.</p>
     *
     * @param base     the base file path to start from; may be {@code null}
     * @param segments additional path segments to append, in order; may be {@code null},
     *                 and individual {@code null} or empty segments are silently skipped
     * @return a {@link File} representing the resolved path
     * @since 1.0
     */
    public static File resolveFile(@Nullable File base, @Nullable String @Nullable ... segments) {
        var path = (base == null ? Path.of("") : base.toPath());

        if (segments != null) {
            for (var segment : segments) {
                if (segment != null && !segment.isEmpty()) {
                    // Strip leading "/" to ensure segments are always resolved relatively
                    // and don't reset to filesystem root
                    var normalized = segment.startsWith("/") ? segment.substring(1) : segment;
                    if (!normalized.isEmpty()) {
                        path = path.resolve(normalized);
                    }
                }
            }
        }
        return path.toFile();
    }
}