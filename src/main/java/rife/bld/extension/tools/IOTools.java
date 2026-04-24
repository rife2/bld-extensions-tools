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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;

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
    public static boolean canExecute(File file) {
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
    public static boolean canExecute(Path path) {
        return path != null && Files.isRegularFile(path) && Files.isExecutable(path);
    }

    /**
     * Determines if the file at the specified path string exists, is a regular file, and is executable.
     *
     * @param path the path string to be checked
     * @return {@code true} if the path exists, is a regular file, and can be executed;
     * {@code false} otherwise
     * @since 1.0
     */
    public static boolean canExecute(String path) {
        try {
            return TextTools.isNotBlank(path) && canExecute(Path.of(path));
        } catch (InvalidPathException | SecurityException e) {
            return false;
        }
    }

    /**
     * Checks if the specified file exists.
     *
     * @param file the file to check for existence
     * @return {@code true} if the file is not {@code null} and exists;
     * {@code false} otherwise
     * @since 1.0
     */
    public static boolean exists(File file) {
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
    public static boolean exists(Path path) {
        return path != null && Files.exists(path);
    }

    /**
     * Checks whether a file or directory exists at the specified path.
     *
     * @param path the file system path to check for existence
     * @return {@code true} if the path is not {@code null} and a file or directory
     * exists at the specified path; {@code false} otherwise
     * @since 1.0
     */
    public static boolean exists(String path) {
        try {
            return TextTools.isNotBlank(path) && Files.exists(Path.of(path));
        } catch (InvalidPathException | SecurityException e) {
            return false;
        }
    }

    /**
     * Determines if the specified {@code File} is a directory.
     *
     * @param file the {@code File} object to be checked; if {@code null}, returns {@code false}
     * @return {@code true} if the file exists and is a directory; {@code false} otherwise
     * @since 1.0
     */
    public static boolean isDirectory(File file) {
        return file != null && file.isDirectory();
    }

    /**
     * Determines if the specified {@code Path} represents an existing directory.
     *
     * @param path the {@code Path} object to be checked; if {@code null}, returns {@code false}
     * @return {@code true} if the path exists and is a directory; {@code false} otherwise
     * @since 1.0
     */
    public static boolean isDirectory(Path path) {
        return path != null && Files.isDirectory(path);
    }

    /**
     * Determines if the specified path string represents an existing directory.
     *
     * @param path the path string to be checked; if {@code null} or blank, returns {@code false}
     * @return {@code true} if the specified path exists and is a directory;
     * {@code false} otherwise (including when the path string is invalid)
     * @since 1.0
     */
    public static boolean isDirectory(String path) {
        if (TextTools.isBlank(path)) {
            return false;
        }
        try {
            return Files.isDirectory(Path.of(path));
        } catch (InvalidPathException | SecurityException e) {
            return false;
        }
    }

    /**
     * Creates the directory named by the specified {@code File}, including any
     * necessary but nonexistent parent directories.
     *
     * <p>Delegates to {@link #mkdirs(Path)} for consistent, race-condition-free behavior.</p>
     *
     * @param file the {@code File} object representing the directory to be created;
     *             if {@code null}, this method returns {@code false}
     * @return {@code true} if the directory was created successfully or already exists;
     * {@code false} if the directory could not be created or {@code file} is {@code null}
     * @since 1.0
     */
    public static boolean mkdirs(File file) {
        return file != null && mkdirs(file.toPath());
    }

    /**
     * Creates the directory specified by the given {@code Path}, including any
     * nonexistent parent directories as necessary.
     *
     * @param path the {@code Path} object representing the directory to be created;
     *             if {@code null}, this method returns {@code false}
     * @return {@code true} if the directory was created successfully or already exists;
     * {@code false} if the directory could not be created or {@code path} is {@code null}
     * @since 1.0
     */
    public static boolean mkdirs(Path path) {
        if (path == null) {
            return false;
        }
        try {
            Files.createDirectories(path);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Creates the directory specified by the given path string, including any
     * nonexistent parent directories as necessary.
     *
     * @param path the path string representing the directory to be created;
     *             if {@code null} or blank, this method returns {@code false}
     * @return {@code true} if the directory was created successfully or already exists;
     * {@code false} if the directory could not be created or {@code path} is {@code null} or blank
     * @since 1.0
     */
    public static boolean mkdirs(String path) {
        try {
            return TextTools.isNotBlank(path) && mkdirs(Path.of(path));
        } catch (InvalidPathException | SecurityException e) {
            return false;
        }
    }

    /**
     * Checks if the specified file does not exist.
     *
     * @param file the file to check for non-existence
     * @return {@code true} if the file is {@code null} or does not exist;
     * {@code false} otherwise
     * @since 1.0
     */
    public static boolean notExists(File file) {
        return !exists(file);
    }

    /**
     * Checks if the specified path does not exist.
     *
     * @param path the path to check for non-existence
     * @return {@code true} if the path is {@code null} or does not exist;
     * {@code false} otherwise
     * @since 1.0
     */
    public static boolean notExists(Path path) {
        return !exists(path);
    }

    /**
     * Checks whether a file or directory does not exist at the specified path.
     *
     * @param path the file system path to check for non-existence
     * @return {@code true} if the path is {@code null} or no file or directory
     * exists at the specified path; {@code false} otherwise
     * @since 1.0
     */
    public static boolean notExists(String path) {
        return !exists(path);
    }

    /**
     * Resolves a file path by joining a base file with additional path segments.
     *
     * <p>This method constructs a file path by appending one or more path segments
     * to a base file. {@code null} segments are silently skipped. The resulting
     * {@link File} object represents the complete path but does not create an actual
     * file on the filesystem.</p>
     *
     * @param base     the base file path to start from; may be {@code null}
     * @param segments additional path segments to append, in order; may be {@code null},
     *                 and individual {@code null} segments are silently skipped
     * @return a {@link File} representing the resolved path
     * @since 1.0
     */
    public static File resolveFile(File base, String... segments) {
        var path = new File(base, "").toPath(); // handle null file
        if (segments != null) {
            for (var segment : segments) {
                if (segment != null) {
                    path = path.resolve(segment);
                }
            }
        }
        return path.toFile();
    }
}