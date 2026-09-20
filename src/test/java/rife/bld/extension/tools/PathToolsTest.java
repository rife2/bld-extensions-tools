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
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Classpath Tools Tests")
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
class PathToolsTest {
    @Nested
    @DisplayName("Command Line Formatting Tests")
    class FormatCommandLineTest {

        @Test
        @DisplayName("escapes embedded double quotes with single quotes")
        void embeddedQuotes() {
            List<String> args = List.of("echo", "he said \"hello\"");
            assertEquals("echo 'he said \"hello\"'", PathTools.formatCommandLine(args));
        }

        @Test
        @DisplayName("escapes single quotes")
        void embeddedSingleQuote() {
            List<String> args = List.of("echo", "can't");
            assertEquals("echo 'can'\\''t'", PathTools.formatCommandLine(args));
        }

        @Test
        @DisplayName("returns empty string for empty list")
        void emptyList() {
            assertEquals("", PathTools.formatCommandLine(List.of()));
        }

        @Test
        @DisplayName("preserves empty string args as ''")
        void emptyStringArg() {
            List<String> args = List.of("echo", "");
            assertEquals("echo ''", PathTools.formatCommandLine(args));
        }

        @Test
        @DisplayName("handles multiple spaced args")
        void multipleSpacedArgs() {
            List<String> args = List.of("cp", "my documents", "backup folder");
            assertEquals("cp 'my documents' 'backup folder'", PathTools.formatCommandLine(args));
        }

        @Test
        @DisplayName("joins args without spaces as-is")
        void noSpaces() {
            List<String> args = List.of("java", "-jar", "app.jar");
            assertEquals("java -jar app.jar", PathTools.formatCommandLine(args));
        }

        @Test
        @DisplayName("return empty for null list")
        void nullList() {
            assertEquals("", PathTools.formatCommandLine(null));
        }

        @Test
        @DisplayName("prevents shell expansion")
        void shellMetaChars() {
            List<String> args = List.of("echo", "$HOME", "`rm -rf /`");
            assertEquals("echo '$HOME' '`rm -rf /`'", PathTools.formatCommandLine(args));
        }

        @Test
        @DisplayName("quotes args that contain spaces")
        void withSpaces() {
            List<String> args = List.of("myapp", "input file.txt", "--verbose");
            assertEquals("myapp 'input file.txt' --verbose", PathTools.formatCommandLine(args));
        }
    }

    @Nested
    @DisplayName("Classpath Joining Tests")
    class JoinClasspathTests {

        private static final String FILE_SEP = File.separator;
        private static final String PATH_SEP = File.pathSeparator;

        @Nested
        @DisplayName("joinClasspath() - no args")
        class NoArgsTests {

            @Test
            @DisplayName("should return empty string")
            void shouldReturnEmptyString() {
                assertEquals("", PathTools.joinClasspath());
                assertEquals("", PathTools.joinClasspath(new File[0]));
                assertEquals("", PathTools.joinClasspath(new Path[0]));
                assertEquals("", PathTools.joinClasspath(new String[0]));
            }
        }

        @Nested
        @DisplayName("joinClasspath(String...)")
        class JoinClasspathString {

            private static Stream<Arguments> providePathCombinations() {
                var path1 = "lib" + FILE_SEP + "a.jar";
                var path2 = "lib" + FILE_SEP + "b.jar";
                return Stream.of(
                        Arguments.of(new String[]{path1}, path1),
                        Arguments.of(new String[]{path1, path2}, path1 + PATH_SEP + path2),
                        Arguments.of(new String[]{"", "", ""}, ""),
                        Arguments.of(new String[]{null, null}, ""),
                        Arguments.of(new String[]{path1, "", path2}, path1 + PATH_SEP + path2)
                );
            }

            @ParameterizedTest
            @MethodSource("providePathCombinations")
            @DisplayName("should handle various path combinations")
            void handleVariousPathCombinations(String[] paths, String expected) {
                var result = PathTools.joinClasspath(paths);
                assertEquals(expected, result);
            }

            @ParameterizedTest
            @NullAndEmptySource
            @ValueSource(strings = {" ", "\t", "\n"})
            @DisplayName("should ignore blank and null paths")
            void ignoreBlankAndNullPaths(String blankPath) {
                var result = PathTools.joinClasspath(blankPath);
                assertEquals("", result);
            }

            @Test
            @DisplayName("should build classpath with single valid path")
            void singleValidPath() {
                var path = "path" + FILE_SEP + "to" + FILE_SEP + "jar.jar";
                var result = PathTools.joinClasspath(path);
                assertEquals(path, result);
            }

            @Test
            @DisplayName("should build classpath with multiple valid paths")
            void multipleValidPaths() {
                var path1 = "path" + FILE_SEP + "to" + FILE_SEP + "jar1.jar";
                var path2 = "path" + FILE_SEP + "to" + FILE_SEP + "jar2.jar";
                var path3 = "path" + FILE_SEP + "to" + FILE_SEP + "jar3.jar";

                var result = PathTools.joinClasspath(path1, path2, path3);
                var expected = String.join(PATH_SEP, path1, path2, path3);
                assertEquals(expected, result);
            }

            @Test
            @DisplayName("should filter out blank paths from mixed input")
            void filterBlankPathsFromMixed() {
                var path1 = "path" + FILE_SEP + "to" + FILE_SEP + "jar1.jar";
                var path2 = "path" + FILE_SEP + "to" + FILE_SEP + "jar2.jar";
                var path3 = "path" + FILE_SEP + "to" + FILE_SEP + "jar3.jar";

                var result = PathTools.joinClasspath(path1, "", null, path2, " ", path3);
                var expected = String.join(PATH_SEP, path1, path2, path3);
                assertEquals(expected, result);
            }
        }

        @Nested
        @DisplayName("joinClasspath(File...)")
        class JoinClasspathFileVarargs {

            @TempDir
            Path tempDir;

            @Test
            @DisplayName("should return empty when varargs is null")
            void nullVarargs() {
                assertEquals("", PathTools.joinClasspath((File[]) null));
            }

            @Test
            @DisplayName("should join single file")
            void singleFile() {
                var file = new File("path" + FILE_SEP + "to" + FILE_SEP + "library.jar");
                var result = PathTools.joinClasspath(file);
                assertEquals(file.getAbsolutePath(), result);
            }

            @Test
            @DisplayName("should skip null elements")
            void skipNulls() throws IOException {
                var file = Files.createFile(tempDir.resolve("a.jar")).toFile();
                var result = PathTools.joinClasspath(file, null);
                assertEquals(file.getAbsolutePath(), result);
            }
        }

        @Nested
        @DisplayName("joinClasspath(Collection<File>)")
        class JoinClasspathFileCollection {

            private static Stream<Arguments> provideFileLists() {
                return Stream.of(
                        Arguments.of(List.of(), 0),
                        Arguments.of(List.of(new File("lib" + FILE_SEP + "a.jar")), 1),
                        Arguments.of(List.of(
                                new File("lib" + FILE_SEP + "a.jar"),
                                new File("lib" + FILE_SEP + "b.jar")
                        ), 2)
                );
            }

            @Test
            @DisplayName("should return empty for empty list")
            void emptyList() {
                var emptyList = new ArrayList<File>();
                assertEquals("", PathTools.joinClasspath(emptyList));
            }

            @ParameterizedTest
            @MethodSource("provideFileLists")
            @DisplayName("should handle various file lists")
            void variousLists(List<File> jars, int expectedParts) {
                var result = PathTools.joinClasspath(jars);
                if (expectedParts == 0) {
                    assertEquals("", result);
                } else {
                    var parts = result.split(File.pathSeparator);
                    assertEquals(expectedParts, parts.length);
                }
            }

            @Test
            @DisplayName("should join multiple files")
            void multipleFiles() {
                var file1 = new File("path" + FILE_SEP + "to" + FILE_SEP + "library1.jar");
                var file2 = new File("path" + FILE_SEP + "to" + FILE_SEP + "library2.jar");
                var file3 = new File("path" + FILE_SEP + "to" + FILE_SEP + "library3.jar");
                var jars = List.of(file1, file2, file3);

                var result = PathTools.joinClasspath(jars);
                var expected = String.join(PATH_SEP,
                        file1.getAbsolutePath(), file2.getAbsolutePath(), file3.getAbsolutePath());
                assertEquals(expected, result);
            }
        }

        @Nested
        @DisplayName("joinClasspath(Collection<File>...)")
        class JoinClasspathFileCollectionVarargs {

            static Stream<Arguments> provideMultipleCollectionCases() {
                var file1 = new File("path" + FILE_SEP + "to" + FILE_SEP + "lib1.jar");
                var file2 = new File("path" + FILE_SEP + "to" + FILE_SEP + "lib2.jar");
                var file3 = new File("path" + FILE_SEP + "to" + FILE_SEP + "lib3.jar");

                return Stream.of(
                        Arguments.of(new Collection[]{List.of(file1), List.of(file2)},
                                file1.getAbsolutePath() + PATH_SEP + file2.getAbsolutePath()),
                        Arguments.of(new Collection[]{List.of(file1, file2), List.of(file3)},
                                file1.getAbsolutePath() + PATH_SEP + file2.getAbsolutePath() + PATH_SEP + file3.getAbsolutePath())
                );
            }

            @ParameterizedTest
            @MethodSource("provideMultipleCollectionCases")
            @DisplayName("should join multiple collections")
            void multipleCollections(Collection<File>[] jars, String expected) {
                assertEquals(expected, PathTools.joinClasspath(jars));
            }

            @Test
            @DisplayName("should handle mixed empty and non-empty collections")
            void mixedEmptyAndNonEmpty() {
                var file1 = new File("path" + FILE_SEP + "to" + FILE_SEP + "lib1.jar");
                var file2 = new File("path" + FILE_SEP + "to" + FILE_SEP + "lib2.jar");
                var expected = file1.getAbsolutePath() + PATH_SEP + file2.getAbsolutePath();

                assertEquals(expected, PathTools.joinClasspath(
                        List.of(file1), Collections.emptyList(), List.of(file2)));
            }

            @Test
            @DisplayName("should skip null collections")
            void skipNullCollections() {
                var file1 = new File("path" + FILE_SEP + "to" + FILE_SEP + "lib1.jar");
                var file3 = new File("path" + FILE_SEP + "to" + FILE_SEP + "lib3.jar");
                var expected = file1.getAbsolutePath() + PATH_SEP + file3.getAbsolutePath();

                assertEquals(expected, PathTools.joinClasspath(List.of(file1), null, List.of(file3)));
            }

            @Test
            @DisplayName("should return empty when all collections are empty")
            void allEmpty() {
                assertEquals("", PathTools.joinClasspath(
                        Collections.emptyList(),
                        Collections.emptyList()));
            }
        }

        @Nested
        @DisplayName("joinClasspath(Collection<Path>)")
        class JoinClasspathPathCollection {

            @TempDir
            Path tempDir;

            @Test
            @DisplayName("should return empty when collection is null")
            void nullCollection() {
                assertEquals("", PathTools.joinClasspath((Collection<Path>) null));
            }

            @Test
            @DisplayName("should return empty when collection is empty")
            void emptyCollection() {
                assertEquals("", PathTools.joinClasspath(Collections.emptyList()));
            }

            @Test
            @DisplayName("should join normalized absolute paths")
            void normalizedPaths() throws IOException {
                var file = Files.createFile(tempDir.resolve("a.jar"));
                var sub = Files.createDirectory(tempDir.resolve("sub"));
                var file2 = Files.createFile(sub.resolve("b.jar"));

                var cp = PathTools.joinClasspath(List.of(file, file2));

                assertTrue(cp.contains(file.toAbsolutePath().normalize().toString()));
                assertTrue(cp.contains(file2.toAbsolutePath().normalize().toString()));
                assertTrue(cp.contains(File.pathSeparator));
            }

            @Test
            @DisplayName("should skip null elements")
            void skipNulls() throws IOException {
                var file = Files.createFile(tempDir.resolve("a.jar"));
                var withNull = Arrays.asList(file, null);

                var cp = PathTools.joinClasspath(withNull);
                assertEquals(file.toAbsolutePath().normalize().toString(), cp);
            }
        }

        @Nested
        @DisplayName("joinClasspath(Path...)")
        class JoinClasspathPathVarargs {

            @TempDir
            Path tempDir;

            @Test
            @DisplayName("should return empty when varargs is null")
            void nullVarargs() {
                assertEquals("", PathTools.joinClasspath((Path[]) null));
            }

            @Test
            @DisplayName("should join multiple paths")
            void multiplePaths() throws IOException {
                var a = Files.createFile(tempDir.resolve("a.jar"));
                var b = Files.createFile(tempDir.resolve("b.jar"));

                var cp = PathTools.joinClasspath(a, b);
                var expected = a.toAbsolutePath().normalize()
                        + File.pathSeparator
                        + b.toAbsolutePath().normalize();

                assertEquals(expected, cp);
            }

            @Test
            @DisplayName("should skip null varargs elements")
            void skipNulls() throws IOException {
                var a = Files.createFile(tempDir.resolve("a.jar"));
                var cp = PathTools.joinClasspath(a, null);
                assertEquals(a.toAbsolutePath().normalize().toString(), cp);
            }
        }

        @Nested
        @DisplayName("Integration")
        class IntegrationTests {

            @Test
            @DisplayName("should work together for building complete classpaths")
            void buildCompleteClasspath() {
                var jarFiles = List.of(
                        new File("lib" + FILE_SEP + "dependency1.jar"),
                        new File("lib" + FILE_SEP + "dependency2.jar")
                );
                var jarClasspath = PathTools.joinClasspath(jarFiles);

                var fullClasspath = PathTools.joinClasspath(
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
}