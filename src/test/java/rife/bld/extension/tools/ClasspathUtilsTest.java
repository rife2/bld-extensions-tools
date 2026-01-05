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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Classpath Utils Tests")
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
class ClasspathUtilsTest {

    private static final String FILE_SEP = File.separator;
    private static final String PATH_SEP = File.pathSeparator;

    @Nested
    @DisplayName("buildClasspath() method")
    class BuildClasspathTests {

        private static Stream<Arguments> providePathCombinations() {
            var path1 = "lib" + FILE_SEP + "a.jar";
            var path2 = "lib" + FILE_SEP + "b.jar";
            return Stream.of(
                    Arguments.of(
                            new String[]{path1},
                            path1
                    ),
                    Arguments.of(
                            new String[]{path1, path2},
                            path1 + PATH_SEP + path2
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
                            new String[]{path1, "", path2},
                            path1 + PATH_SEP + path2
                    )
            );
        }

        @Test
        @DisplayName("should return empty string when no paths provided")
        void emptyWhenNoPathsProvided() {
            var result = ClasspathUtils.buildClasspath();
            assertEquals("", result);
        }

        @Test
        @DisplayName("should filter out blank paths from mixed input")
        void filterBlankPathsFromMixed() {
            var path1 = "path" + FILE_SEP + "to" + FILE_SEP + "jar1.jar";
            var path2 = "path" + FILE_SEP + "to" + FILE_SEP + "jar2.jar";
            var path3 = "path" + FILE_SEP + "to" + FILE_SEP + "jar3.jar";

            var result = ClasspathUtils.buildClasspath(
                    path1,
                    "",
                    null,
                    path2,
                    "   ",
                    path3
            );
            var expected = String.join(PATH_SEP, path1, path2, path3);
            assertEquals(expected, result);
        }

        @ParameterizedTest
        @MethodSource("providePathCombinations")
        @DisplayName("should handle various path combinations")
        void handleVariousPathCombinations(String[] paths, String expected) {
            var result = ClasspathUtils.buildClasspath(paths);
            assertEquals(expected, result);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t", "\n"})
        @DisplayName("should ignore blank and null paths")
        void ignoreBlankAndNullPaths(String blankPath) {
            var result = ClasspathUtils.buildClasspath(blankPath);
            assertEquals("", result);
        }

        @Test
        @DisplayName("should build classpath with multiple valid paths")
        void multipleValidPaths() {
            var path1 = "path" + FILE_SEP + "to" + FILE_SEP + "jar1.jar";
            var path2 = "path" + FILE_SEP + "to" + FILE_SEP + "jar2.jar";
            var path3 = "path" + FILE_SEP + "to" + FILE_SEP + "jar3.jar";

            var result = ClasspathUtils.buildClasspath(path1, path2, path3);
            var expected = String.join(PATH_SEP, path1, path2, path3);
            assertEquals(expected, result);
        }

        @Test
        @DisplayName("should build classpath with single valid path")
        void singleValidPath() {
            var path = "path" + FILE_SEP + "to" + FILE_SEP + "jar.jar";
            var result = ClasspathUtils.buildClasspath(path);
            assertEquals(path, result);
        }

        @Nested
        @DisplayName("Integration Tests")
        class IntegrationTests {

            @Test
            @DisplayName("should work together for building complete classpaths")
            void buildCompleteClasspath() {
                var jarFiles = List.of(
                        new File("lib" + FILE_SEP + "dependency1.jar"),
                        new File("lib" + FILE_SEP + "dependency2.jar")
                );
                var jarClasspath = ClasspathUtils.joinClasspathJar(jarFiles);

                var fullClasspath = ClasspathUtils.buildClasspath(
                        "classes",
                        jarClasspath,
                        "resources"
                );

                assertFalse(fullClasspath.isEmpty());
                assertTrue(fullClasspath.contains("classes"));
                assertTrue(fullClasspath.contains("resources"));
                assertTrue(fullClasspath.contains("dependency1.jar"));
                assertTrue(fullClasspath.contains("dependency2.jar"));
            }
        }

    }

    @Nested
    @DisplayName("joinClasspathJar() method")
    class JoinClasspathJarTests {

        private static Stream<Arguments> provideJarFileLists() {
            return Stream.of(
                    Arguments.of(List.of(), 0),
                    Arguments.of(List.of(new File("lib" + FILE_SEP + "a.jar")), 1),
                    Arguments.of(List.of(
                            new File("lib" + FILE_SEP + "a.jar"),
                            new File("lib" + FILE_SEP + "b.jar")
                    ), 2),
                    Arguments.of(List.of(
                            new File("lib" + FILE_SEP + "a.jar"),
                            new File("lib" + FILE_SEP + "b.jar"),
                            new File("lib" + FILE_SEP + "c.jar")
                    ), 3)
            );
        }

        @Test
        @DisplayName("should return empty string for empty list")
        void emptyStringForEmptyList() {
            var emptyList = new ArrayList<File>();
            var result = ClasspathUtils.joinClasspathJar(emptyList);
            assertEquals("", result);
        }

        @Test
        @DisplayName("should handle relative and absolute paths")
        void handleRelativeAndAbsolutePaths() {
            var relativeFile = new File("relative" + FILE_SEP + "path" + FILE_SEP + "lib.jar");
            var absoluteFile = new File("absolute" + FILE_SEP + "path" + FILE_SEP + "lib.jar");
            var jars = List.of(relativeFile, absoluteFile);

            var result = ClasspathUtils.joinClasspathJar(jars);

            assertTrue(result.contains(relativeFile.getAbsolutePath()));
            assertTrue(result.contains(absoluteFile.getAbsolutePath()));
            assertTrue(result.contains(PATH_SEP));
        }

        @ParameterizedTest
        @MethodSource("provideJarFileLists")
        @DisplayName("should handle various jar file lists")
        void handleVariousJarFileLists(List<File> jars, int expectedParts) {
            var result = ClasspathUtils.joinClasspathJar(jars);

            if (expectedParts == 0) {
                assertEquals("", result);
            } else {
                var parts = result.split(File.pathSeparator);
                assertEquals(expectedParts, parts.length);
            }
        }

        @Test
        @DisplayName("should join multiple jar files")
        void joinMultipleJarFiles() {
            var file1 = new File("path" + FILE_SEP + "to" + FILE_SEP + "library1.jar");
            var file2 = new File("path" + FILE_SEP + "to" + FILE_SEP + "library2.jar");
            var file3 = new File("path" + FILE_SEP + "to" + FILE_SEP + "library3.jar");
            var jars = List.of(file1, file2, file3);

            var result = ClasspathUtils.joinClasspathJar(jars);

            var expected = String.join(PATH_SEP,
                    file1.getAbsolutePath(),
                    file2.getAbsolutePath(),
                    file3.getAbsolutePath()
            );
            assertEquals(expected, result);
        }

        @Test
        @DisplayName("should join single jar file")
        void joinSingleJarFile() {
            var file = new File("path" + FILE_SEP + "to" + FILE_SEP + "library.jar");
            var jars = List.of(file);
            var result = ClasspathUtils.joinClasspathJar(jars);
            assertEquals(file.getAbsolutePath(), result);
        }

        @Nested
        @DisplayName("joinClasspathJar(Collection<File>...) tests")
        class JoinClasspathJarVarargsTests {

            @SuppressWarnings("DuplicateExpressions")
            static Stream<Arguments> provideMultipleCollectionCases() {
                var file1 = new File("path" + FILE_SEP + "to" + FILE_SEP + "lib1.jar");
                var file2 = new File("path" + FILE_SEP + "to" + FILE_SEP + "lib2.jar");
                var file3 = new File("path" + FILE_SEP + "to" + FILE_SEP + "lib3.jar");

                return Stream.of(
                        Arguments.of(
                                new Collection[]{
                                        List.of(file1),
                                        List.of(file2)
                                },
                                file1.getAbsolutePath() + PATH_SEP + file2.getAbsolutePath()
                        ),
                        Arguments.of(
                                new Collection[]{
                                        List.of(file1, file2),
                                        List.of(file3)
                                },
                                file1.getAbsolutePath() + PATH_SEP + file2.getAbsolutePath() + PATH_SEP + file3.getAbsolutePath()
                        ),
                        Arguments.of(
                                new Collection[]{
                                        List.of(file1),
                                        List.of(file2),
                                        List.of(file3)
                                },
                                file1.getAbsolutePath() + PATH_SEP + file2.getAbsolutePath() + PATH_SEP + file3.getAbsolutePath()
                        )
                );
            }

            static Stream<Arguments> provideSingleCollectionCases() {
                var file1 = new File("path" + FILE_SEP + "to" + FILE_SEP + "lib1.jar");
                var file2 = new File("path" + FILE_SEP + "to" + FILE_SEP + "lib2.jar");
                var file3 = new File("path" + FILE_SEP + "to" + FILE_SEP + "lib3.jar");

                return Stream.of(
                        Arguments.of(
                                List.of(file1),
                                file1.getAbsolutePath()
                        ),
                        Arguments.of(
                                List.of(file1, file2),
                                file1.getAbsolutePath() + PATH_SEP + file2.getAbsolutePath()
                        ),
                        Arguments.of(
                                List.of(file1, file2, file3),
                                file1.getAbsolutePath() + PATH_SEP + file2.getAbsolutePath() + PATH_SEP + file3.getAbsolutePath()
                        )
                );
            }

            @Test
            @DisplayName("should handle mixed empty and non-empty collections")
            void shouldHandleMixedEmptyAndNonEmptyCollections() {
                var file1 = new File("path" + FILE_SEP + "to" + FILE_SEP + "lib1.jar");
                var file2 = new File("path" + FILE_SEP + "to" + FILE_SEP + "lib2.jar");
                var expected = file1.getAbsolutePath() + PATH_SEP + file2.getAbsolutePath();

                assertEquals(expected, ClasspathUtils.joinClasspathJar(
                        List.of(file1),
                        Collections.emptyList(),
                        List.of(file2)
                ));
            }

            @Test
            @DisplayName("should handle mixed null and non-null collections with empty collections")
            void shouldHandleMixedNullAndNonNullCollectionsWithEmptyCollections() {
                var file1 = new File("path" + FILE_SEP + "to" + FILE_SEP + "lib1.jar");
                var expected = file1.getAbsolutePath();

                assertEquals(expected, ClasspathUtils.joinClasspathJar(
                        null,
                        Collections.emptyList(),
                        List.of(file1),
                        null
                ));
            }

            @ParameterizedTest
            @MethodSource("provideMultipleCollectionCases")
            @DisplayName("should join multiple collections into classpath string")
            void shouldJoinMultipleCollectionsIntoClasspathString(Collection<File>[] jars, String expected) {
                assertEquals(expected, ClasspathUtils.joinClasspathJar(jars));
            }

            @ParameterizedTest
            @MethodSource("provideSingleCollectionCases")
            @DisplayName("should join single collection into classpath string")
            void shouldJoinSingleCollectionIntoClasspathString(Collection<File> jars, String expected) {
                assertEquals(expected, ClasspathUtils.joinClasspathJar(jars));
            }

            @Test
            @DisplayName("should return empty string when all collections are empty")
            void shouldReturnEmptyStringWhenAllCollectionsAreEmpty() {
                assertEquals("", ClasspathUtils.joinClasspathJar(
                        Collections.emptyList(),
                        Collections.emptyList()
                ));
            }

            @Test
            @DisplayName("should return empty string when all collections are null")
            void shouldReturnEmptyStringWhenAllCollectionsAreNull() {
                assertEquals("", ClasspathUtils.joinClasspathJar(null, null, null));
            }

            @Test
            @DisplayName("should return empty string when no collections are provided")
            void shouldReturnEmptyStringWhenNoCollectionsAreProvided() {
                var result = ClasspathUtils.joinClasspathJar();
                assertEquals("", result);
            }

            @Test
            @DisplayName("should skip null collections")
            void shouldSkipNullCollections() {
                var file1 = new File("path" + FILE_SEP + "to" + FILE_SEP + "lib1.jar");
                var file3 = new File("path" + FILE_SEP + "to" + FILE_SEP + "lib3.jar");
                var expected = file1.getAbsolutePath() + PATH_SEP + file3.getAbsolutePath();

                assertEquals(expected, ClasspathUtils.joinClasspathJar(
                        List.of(file1),
                        null,
                        List.of(file3)
                ));
            }
        }

    }
}