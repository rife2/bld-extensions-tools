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
import java.util.stream.Collectors;

/**
 * Classpath Utilities and Tools.
 */
public final class ClassPathUtils {
    private ClassPathUtils() {
        // no-op
    }

    /**
     * Constructs a classpath string by concatenating non-blank paths using the system's
     * path separator. Blank or null paths are ignored.
     *
     * @param path An array of strings representing individual classpath entries. Null or
     *             blank strings are skipped in the final classpath
     * @return A string representing the concatenated classpath entries, separated by
     * the system's path separator. If no valid paths are provided, an empty
     * string is returned
     * @since 1.0
     */
    public static String buildClassPath(String... path) {
        var joiner = new java.util.StringJoiner(File.pathSeparator);
        for (var p : path) {
            if (TextUtils.isNotBlank(p)) {
                joiner.add(p);
            }
        }
        return joiner.toString();
    }

    /**
     * Joins a list of JAR file paths into a single classpath string using the system's path separator.
     * <p>
     * Each JAR file's absolute path is included in the resulting classpath string.
     *
     * @param jars A list of {@link File} objects representing the JAR files to include in the classpath.
     *             If the list is empty, an empty string is returned
     * @return A classpath string where the absolute paths of the provided JAR files are joined
     * by the system's path separator. If the input list is empty, an empty string is returned
     * @since 1.0
     */
    public static String joinClasspathJar(Collection<File> jars) {
        return jars.stream()
                .map(File::getAbsolutePath)
                .collect(Collectors.joining(File.pathSeparator));
    }
}