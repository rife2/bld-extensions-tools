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
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Files and Paths Utilities and Tools.
 */
public final class FilesUtils {
    private FilesUtils() {
        // no-op
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
        return TextUtils.isNotBlank(path) && Files.exists(Path.of(path));
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
}