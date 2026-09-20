/*
 * Copyright 2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
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
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Path Tools.
 * <p>
 * Utility methods to facilitate paths and arguments handling.
 *
 * @author <a href="https://erik.thauvin.net/">Erik C. Thauvin</a>
 * @since 1.0
 */
@NullMarked
public final class PathTools {

    private PathTools() {
        // no-op
    }

    private static String escapePosix(@Nullable String s) {
        if (s == null) {
            return "''";
        }
        if (s.isEmpty()) {
            return "''";
        }
        // Safe chars that don't need quoting: alphanum + - _. / : =, @
        if (s.matches("[a-zA-Z0-9._/:@,=-]+")) {
            return s;
        }
        // Single-quote and escape embedded single quotes: ' -> '\''
        return "'" + s.replace("'", "'\\''") + "'";
    }

    /**
     * Formats a collection of command-line arguments into a single string suitable for
     * logging and safe copy/paste into a POSIX-compatible shell.
     *
     * @param args the collection of command-line arguments to format; may be {@code null}
     * or empty, and may contain {@code null} or empty elements, which are rendered as {@code ''}
     * @return a space-separated string with arguments quoted and escaped for POSIX shells;
     * an empty string if the collection is {@code null} or empty
     * @since 1.0
     */
    public static String formatCommandLine(@Nullable Collection<@Nullable String> args) {
        if (args == null || args.isEmpty()) {
            return "";
        }
        return args.stream()
                .map(PathTools::escapePosix)
                .collect(Collectors.joining(" "));
    }

    /**
     * Returns an empty classpath string. This overload exists to disambiguate zero-argument
     * calls between the various {@code joinClasspath} varargs overloads.
     *
     * @return an empty string
     * @since 1.0
     */
    public static String joinClasspath() {
        return "";
    }

    /**
     * Joins multiple collections of files into a single classpath string
     * using the system's path separator.
     *
     * <p>Each file's normalized absolute path from all provided collections is included
     * in the resulting classpath string. {@code null} collections and {@code null}
     * individual file elements are silently ignored.</p>
     *
     * <p>Uses {@link java.nio.file.Path#toAbsolutePath()} followed by
     * {@link java.nio.file.Path#normalize()} to resolve relative paths and eliminate
     * redundant {@code ..} and {@code .} segments.</p>
     *
     * @param files variable number of {@link Collection}s of {@link File} objects
     * representing the files to include in the classpath; may be
     * {@code null}, and {@code null} collections or elements are skipped
     * @return a classpath string where the normalized absolute paths of all provided
     * files are joined by the system's path separator; an empty string if no valid
     * files are provided
     * @since 1.0
     */
    @SafeVarargs
    public static String joinClasspath(@Nullable Collection<File> @Nullable... files) {
        if (files == null) {
            return "";
        }
        return Stream.of(files)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .map(f -> Objects.requireNonNull(f).toPath().toAbsolutePath().normalize().toString())
                .collect(Collectors.joining(File.pathSeparator));
    }

    /**
     * Joins multiple files into a single classpath string
     * using the system's path separator.
     *
     * <p>Each file's normalized absolute path is included in the resulting
     * classpath string. {@code null} individual file elements are silently ignored.</p>
     *
     * <p>Uses {@link java.nio.file.Path#toAbsolutePath()} followed by
     * {@link java.nio.file.Path#normalize()} to resolve relative paths and eliminate
     * redundant {@code ..} and {@code .} segments.</p>
     *
     * @param files variable number of {@link File} objects representing the files
     * to include in the classpath; may be {@code null}, and {@code null}
     * elements are skipped
     * @return a classpath string where the normalized absolute paths of all provided
     * files are joined by the system's path separator; an empty string if no valid
     * files are provided
     * @since 1.0
     */
    public static String joinClasspath(@Nullable File @Nullable... files) {
        if (files == null) {
            return "";
        }
        return Arrays.stream(files)
                .filter(Objects::nonNull)
                .map(f -> f.toPath().toAbsolutePath().normalize().toString())
                .collect(Collectors.joining(File.pathSeparator));
    }

    /**
     * Joins string paths into a single classpath by concatenating non-blank
     * paths using the system's path separator.
     *
     * <p>Blank or {@code null} paths are ignored.</p>
     *
     * @param paths an array of strings representing individual classpath entries;
     * may be {@code null}, and individual {@code null} or blank strings
     * are silently skipped
     * @return a string representing the concatenated classpath entries, separated by
     * the system's path separator; an empty string if no valid paths are provided
     * @since 1.0
     */
    public static String joinClasspath(@Nullable String @Nullable... paths) {
        if (paths == null) {
            return "";
        }
        return Arrays.stream(paths)
                .filter(TextTools::isNotBlank)
                .collect(Collectors.joining(File.pathSeparator));
    }

    /**
     * Joins a collection of paths into a single classpath string
     * using the system's path separator.
     *
     * <p>Each path's normalized absolute path from the provided collection is included
     * in the resulting classpath string. {@code null} individual path elements are
     * silently ignored.</p>
     *
     * <p>Uses {@link Path#toAbsolutePath()} followed by {@link Path#normalize()}
     * to resolve relative paths and eliminate redundant {@code ..} and {@code .} segments.</p>
     *
     * @param paths a {@link Collection} of {@link Path} objects representing the paths
     * to include in the classpath; may be {@code null}, and {@code null}
     * elements are skipped
     * @return a classpath string where the normalized absolute paths of all provided
     * paths are joined by the system's path separator; an empty string if the collection
     * is {@code null} or contains no valid paths
     * @since 1.0
     */
    public static String joinClasspath(@Nullable Collection<@Nullable Path> paths) {
        if (paths == null) {
            return "";
        }
        return paths.stream()
                .filter(Objects::nonNull)
                .map(p -> p.toAbsolutePath().normalize().toString())
                .collect(Collectors.joining(File.pathSeparator));
    }

    /**
     * Joins multiple paths into a single classpath string
     * using the system's path separator.
     *
     * <p>Each path's normalized absolute path is included in the resulting
     * classpath string. {@code null} individual path elements are silently ignored.</p>
     *
     * <p>Uses {@link Path#toAbsolutePath()} followed by {@link Path#normalize()}
     * to resolve relative paths and eliminate redundant {@code ..} and {@code .} segments.</p>
     *
     * @param paths variable number of {@link Path} objects representing the paths
     * to include in the classpath; may be {@code null}, and {@code null}
     * elements are skipped
     * @return a classpath string where the normalized absolute paths of all provided
     * paths are joined by the system's path separator; an empty string if no valid
     * paths are provided
     * @since 1.0
     */
    public static String joinClasspath(@Nullable Path @Nullable... paths) {
        if (paths == null) {
            return "";
        }
        return Arrays.stream(paths)
                .filter(Objects::nonNull)
                .map(p -> p.toAbsolutePath().normalize().toString())
                .collect(Collectors.joining(File.pathSeparator));
    }
}