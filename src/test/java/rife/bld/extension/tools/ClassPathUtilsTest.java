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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ClassPathUtils Tests")
@SuppressWarnings( "PMD.AvoidDuplicateLiterals")
class ClassPathUtilsTest {
    private static final String PATH_SEP = File.pathSeparator;

    @Nested
    @DisplayName("buildClassPath() method")
    class BuildClassPathTests {
        @Test
        @DisplayName("should return empty string when no paths provided")
        void emptyWhenNoPathsProvided() {
            var result = ClassPathUtils.buildClassPath();
            assertEquals("", result);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t", "\n"})
        @DisplayName("should ignore blank and null paths")
        void ignoreBlankAndNullPaths(String blankPath) {
            var result = ClassPathUtils.buildClassPath(blankPath);
            assertEquals("", result);
        }

        @Test
        @DisplayName("should build classpath with single valid path")
        void singleValidPath() {
            var result = ClassPathUtils.buildClassPath("/path/to/jar.jar");
            assertEquals("/path/to/jar.jar", result);
        }

        @Test
        @DisplayName("should build classpath with multiple valid paths")
        void multipleValidPaths() {
            var result = ClassPathUtils.buildClassPath(
                    "/path/to/jar1.jar",
                    "/path/to/jar2.jar",
                    "/path/to/jar3.jar"
            );
            var expected = String.join(PATH_SEP,
                    "/path/to/jar1.jar",
                    "/path/to/jar2.jar",
                    "/path/to/jar3.jar"
            );
            assertEquals(expected, result);
        }

        @Test
        @DisplayName("should filter out blank paths from mixed input")
        void filterBlankPathsFromMixed() {
            var result = ClassPathUtils.buildClassPath(
                    "/path/to/jar1.jar",
                    "",
                    null,
                    "/path/to/jar2.jar",
                    "   ",
                    "/path/to/jar3.jar"
            );
            var expected = String.join(PATH_SEP,
                    "/path/to/jar1.jar",
                    "/path/to/jar2.jar",
                    "/path/to/jar3.jar"
            );
            assertEquals(expected, result);
        }

        @ParameterizedTest
        @MethodSource("providePathCombinations")
        @DisplayName("should handle various path combinations")
        void handleVariousPathCombinations(String[] paths, String expected) {
            var result = ClassPathUtils.buildClassPath(paths);
            assertEquals(expected, result);
        }

        private static Stream<Arguments> providePathCombinations() {
            return Stream.of(
                    Arguments.of(
                            new String[]{"/lib/a.jar"},
                            "/lib/a.jar"
                    ),
                    Arguments.of(
                            new String[]{"/lib/a.jar", "/lib/b.jar"},
                            "/lib/a.jar" + PATH_SEP + "/lib/b.jar"
                    ),
                    Arguments.of(
                            new String[]{"", "", ""},
                            ""
                    ),
                    Arguments.of(
                            new String[]{null, null},
                            ""
                    ),
                    Arguments.of(
                            new String[]{"/lib/a.jar", "", "/lib/b.jar"},
                            "/lib/a.jar" + PATH_SEP + "/lib/b.jar"
                    )
            );
        }
    }

    @Nested
    @DisplayName("joinClasspathJar() method")
    class JoinClasspathJarTests {
        @Test
        @DisplayName("should return empty string for empty list")
        void emptyStringForEmptyList() {
            var emptyList = new ArrayList<File>();
            var result = ClassPathUtils.joinClasspathJar(emptyList);
            assertEquals("", result);
        }

        @Test
        @DisplayName("should join single jar file")
        void joinSingleJarFile() {
            var file = new File("/path/to/library.jar");
            var jars = List.of(file);
            var result = ClassPathUtils.joinClasspathJar(jars);
            assertEquals(file.getAbsolutePath(), result);
        }

        @Test
        @DisplayName("should join multiple jar files")
        void joinMultipleJarFiles() {
            var file1 = new File("/path/to/library1.jar");
            var file2 = new File("/path/to/library2.jar");
            var file3 = new File("/path/to/library3.jar");
            var jars = List.of(file1, file2, file3);

            var result = ClassPathUtils.joinClasspathJar(jars);

            var expected = String.join(PATH_SEP,
                    file1.getAbsolutePath(),
                    file2.getAbsolutePath(),
                    file3.getAbsolutePath()
            );
            assertEquals(expected, result);
        }

        @ParameterizedTest
        @MethodSource("provideJarFileLists")
        @DisplayName("should handle various jar file lists")
        void handleVariousJarFileLists(List<File> jars, int expectedParts) {
            var result = ClassPathUtils.joinClasspathJar(jars);

            if (expectedParts == 0) {
                assertEquals("", result);
            } else {
                var parts = result.split(File.pathSeparator);
                assertEquals(expectedParts, parts.length);
            }
        }

        private static Stream<Arguments> provideJarFileLists() {
            return Stream.of(
                    Arguments.of(List.of(), 0),
                    Arguments.of(List.of(new File("lib/a.jar")), 1),
                    Arguments.of(List.of(new File("lib/a.jar"), new File("lib/b.jar")), 2),
                    Arguments.of(List.of(
                            new File("lib/a.jar"),
                            new File("lib/b.jar"),
                            new File("lib/c.jar")
                    ), 3)
            );
        }

        @Test
        @DisplayName("should handle relative and absolute paths")
        void handleRelativeAndAbsolutePaths() {
            var relativeFile = new File("relative/path/lib.jar");
            var absoluteFile = new File("/absolute/path/lib.jar");
            var jars = List.of(relativeFile, absoluteFile);

            var result = ClassPathUtils.joinClasspathJar(jars);

            assertTrue(result.contains(relativeFile.getAbsolutePath()));
            assertTrue(result.contains(absoluteFile.getAbsolutePath()));
            assertTrue(result.contains(PATH_SEP));
        }
    }

    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {
        @Test
        @DisplayName("should work together for building complete classpaths")
        void buildCompleteClasspath() {
            var jarFiles = List.of(
                    new File("/lib/dependency1.jar"),
                    new File("/lib/dependency2.jar")
            );
            var jarClasspath = ClassPathUtils.joinClasspathJar(jarFiles);

            var fullClasspath = ClassPathUtils.buildClassPath(
                    "/classes",
                    jarClasspath,
                    "/resources"
            );

            assertFalse(fullClasspath.isEmpty());
            assertTrue(fullClasspath.contains("/classes"));
            assertTrue(fullClasspath.contains("/resources"));
            assertTrue(fullClasspath.contains("dependency1.jar"));
            assertTrue(fullClasspath.contains("dependency2.jar"));
        }
    }
}