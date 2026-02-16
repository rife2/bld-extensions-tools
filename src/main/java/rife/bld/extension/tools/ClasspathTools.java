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
import java.util.Collection;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Classpath Tools.
 */
public final class ClasspathTools {

    private ClasspathTools() {
        // no-op
    }

    /**
     * Join string paths into a single classpath by concatenating non-blank
     * paths using the system's path separator.
     * <p>
     * Blank or {@code null} paths are ignored.
     *
     * @param paths An array of strings representing individual classpath entries.
     *              {@code null} or blank strings are skipped in the final classpath
     * @return A string representing the concatenated classpath entries, separated by
     * the system's path separator. If no valid paths are provided, an empty
     * string is returned
     * @since 1.0
     */
    public static String joinClasspath(String... paths) {
        var joiner = new StringJoiner(File.pathSeparator);
        for (var p : paths) {
            if (TextTools.isNotBlank(p)) {
                joiner.add(p);
            }
        }
        return joiner.toString();
    }

    /**
     * Joins multiple collections of file paths into a single classpath string
     * using the system's path separator.
     * <p>
     * Each file's absolute path from all provided collections is included in the resulting
     * classpath string. {@code null} collections are safely ignored.
     *
     * @param files Variable number of {@link Collection}s of {@link File} objects representing the
     *              files to include in the classpath. {@code null} collections are skipped. If all
     *              collections are empty or {@code null}, an empty string is returned
     * @return A classpath string where the absolute paths of all provided files are joined by the
     * system's path separator. If no valid files are provided, an empty string is returned
     * @since 1.0
     */
    @SafeVarargs
    public static String joinClasspath(Collection<File>... files) {
        return Stream.of(files)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .map(File::getAbsolutePath)
                .collect(Collectors.joining(File.pathSeparator));
    }
}