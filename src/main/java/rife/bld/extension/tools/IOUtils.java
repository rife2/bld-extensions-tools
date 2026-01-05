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
 * I/O Utilities and Tools.
 */
public final class IOUtils {

    private IOUtils() {
        // no-op
    }

    /**
     * Determines if the specified file exists, is a file, and is executable.
     *
     * @param file The file to be checked
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
     * @param path The path to be checked
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
     * @param path The path string to be checked
     * @return {@code true} if the path exists, is a regular file, and can be executed;
     * {@code false} otherwise
     * @since 1.0
     */
    public static boolean canExecute(String path) {
        try {
            return TextUtils.isNotBlank(path) && canExecute(Path.of(path));
        } catch (InvalidPathException | SecurityException e) {
            return false;
        }
    }

    /**
     * Checks if the specified file exists.
     *
     * @param file The file to check for existence
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
     * @param path The path to check for existence
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
     * @param path The file system path to check for existence.
     * @return {@code true} if the path is not {@code null} and a file or directory
     * exists at the specified path; {@code false} otherwise
     * @since 1.0
     */
    public static boolean exists(String path) {
        try {
            return TextUtils.isNotBlank(path) && Files.exists(Path.of(path));
        }
        catch (InvalidPathException | SecurityException e) {
            return false;
        }
    }

    /**
     * Determines if the specified {@code File} is a directory.
     *
     * @param file The {@code File} object to be checked. If {@code null}, this method
     *             will return {@code false}
     * @return {@code true} if the file exists and is a directory; {@code false} otherwise
     * @since 1.0
     */
    public static boolean isDirectory(File file) {
        return file != null && file.isDirectory();
    }

    /**
     * Determines if the specified {@code Path} represents an existing directory.
     *
     * @param path The {@code Path} object to be checked. If {@code null}, this method
     *             returns {@code false}
     * @return {@code true} if the path exists and is a directory; {@code false} otherwise
     * @since 1.0
     */
    public static boolean isDirectory(Path path) {
        return path != null && Files.isDirectory(path);
    }

    /**
     * Determines if the specified path string represents an existing directory.
     *
     * @param path The path string to be checked. If {@code null} or empty, this method
     *             will return {@code false}
     * @return {@code true} if the specified path exists and is a directory;
     * {@code false} otherwise (including when the path string is invalid)
     * @since 1.0
     */
    public static boolean isDirectory(String path) {
        if (TextUtils.isBlank(path)) {
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
     * @param file The {@code File} object representing the directory to be created.
     *             If {@code null}, this method returns {@code false}
     * @return {@code true} if the directory was created successfully, or if it
     * already exists; {@code false} if the directory could not be created
     * or if the provided {@code file} is {@code null}
     */
    public static boolean mkdirs(File file) {
        if (file == null) {
            return false;
        }
        if (file.exists() && !file.isDirectory()) {
            return false;
        }
        return file.exists() || file.mkdirs();
    }

    /**
     * Creates the directory specified by the given {@code Path}, including any
     * nonexistent parent directories as necessary.
     *
     * @param path The {@code Path} object representing the directory to be created.
     *             If {@code null}, this method will return {@code false}
     * @return {@code true} if the directory was created successfully or already
     * exists; {@code false} if the directory could not be created, or if
     * the provided {@code path} is {@code null}
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
     * @param path The path string representing the directory to be created.
     *             If {@code null} or blank, this method will return {@code false}
     * @return {@code true} if the directory was created successfully or already
     * exists; {@code false} if the directory could not be created, or if
     * the provided {@code path} is {@code null} or blank
     */
    public static boolean mkdirs(String path) {
        try {
            return TextUtils.isNotBlank(path) && mkdirs(Path.of(path));
        }
        catch (InvalidPathException | SecurityException e) {
            return false;
        }
    }

    /**
     * Checks whether a file or directory does not exist at the specified path.
     *
     * @param path The file system path to check for non-existence.
     * @return {@code true} if the path is {@code null} or no file or directory
     * exists at the specified path; {@code false} otherwise
     * @since 1.0
     */
    public static boolean notExists(String path) {
        return TextUtils.isBlank(path) || !new File(path).exists();
    }

    /**
     * Checks if the specified file does not exist.
     *
     * @param file The file to check for non-existence
     * @return {@code true} if the file is {@code null} or does not exist;
     * {@code false} otherwise
     * @since 1.0
     */
    public static boolean notExists(File file) {
        return file == null || !file.exists();
    }

    /**
     * Checks if the specified path does not exist.
     *
     * @param path The path to check for non-existence
     * @return {@code true} if the path is {@code null} or does not exist;
     * {@code false} otherwise
     * @since 1.0
     */
    public static boolean notExists(Path path) {
        return path == null || !Files.exists(path);
    }
}