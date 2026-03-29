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

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Classpath Tools.
 *
 * <p>Utility methods for assembling classpath strings from file and string
 * path entries. {@code null} arrays, {@code null} collections, and {@code null}
 * or blank individual entries are silently ignored in all methods.</p>
 *
 * @since 1.0
 */
public final class ClasspathTools {

    private ClasspathTools() {
        // no-op
    }

    /**
     * Joins string paths into a single classpath by concatenating non-blank
     * paths using the system's path separator.
     *
     * <p>Blank or {@code null} paths are ignored.</p>
     *
     * @param paths an array of strings representing individual classpath entries;
     *              may be {@code null}, and individual {@code null} or blank strings
     *              are silently skipped
     * @return a string representing the concatenated classpath entries, separated by
     * the system's path separator; an empty string if no valid paths are provided
     * @since 1.0
     */
    public static String joinClasspath(@Nullable String... paths) {
        if (paths == null) {
            return "";
        }
        return Arrays.stream(paths)
                .filter(TextTools::isNotBlank)
                .collect(Collectors.joining(File.pathSeparator));
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
     *              representing the files to include in the classpath; may be
     *              {@code null}, and {@code null} collections or elements are skipped
     * @return a classpath string where the normalized absolute paths of all provided
     * files are joined by the system's path separator; an empty string if no valid
     * files are provided
     * @since 1.0
     */
    @SafeVarargs
    public static String joinClasspath(@Nullable Collection<File>... files) {
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
}