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
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings({"PMD.AvoidDuplicateLiterals", "ConstantValue"})
class FilesUtilsTest {
    @Nested
    @DisplayName("exists(File) tests")
    class ExistsFileTests {
        @TempDir
        Path tempDir;

        @TempDir
        Path tempFile;

        @Test
        @DisplayName("should return false when file does not exist")
        void shouldReturnFalseWhenFileDoesNotExist() {
            var nonExistentFile = new File(tempDir.toFile(), "nonexistent.txt");
            var result = FilesUtils.exists(nonExistentFile);
            assertFalse(result);
        }

        @Test
        @DisplayName("should return false when file is null")
        void shouldReturnFalseWhenFileIsNull() {
            var result = FilesUtils.exists((File) null);
            assertFalse(result);
        }

        @Test
        @DisplayName("should return true when file exists")
        void shouldReturnTrueWhenFileExists() {
            var existingFile = tempFile.toFile();
            var result = FilesUtils.exists(existingFile);
            assertTrue(result);
        }
    }

    @Nested
    @DisplayName("exists(Path) tests")
    class ExistsPathTests {
        @TempDir
        Path existingPath;
        @TempDir
        Path tempDir;

        @Test
        @DisplayName("should return false when path does not exist")
        void shouldReturnFalseWhenPathDoesNotExist() {
            var nonExistentPath = tempDir.resolve("nonexistent.txt");
            var result = FilesUtils.exists(nonExistentPath);
            assertFalse(result);
        }

        @Test
        @DisplayName("should return false when path is null")
        void shouldReturnFalseWhenPathIsNull() {
            var result = FilesUtils.exists((Path) null);
            assertFalse(result);
        }

        @Test
        @DisplayName("should return true when path exists")
        void shouldReturnTrueWhenPathExists() {
            var result = FilesUtils.exists(existingPath);
            assertTrue(result);
        }
    }

    @Nested
    @DisplayName("exists(String) tests")
    class ExistsStringTests {
        @TempDir
        Path existingPath;
        @TempDir
        Path tempDir;

        @Test
        @DisplayName("should return false when string path does not exist")
        void shouldReturnFalseWhenStringPathDoesNotExist() {
            var nonExistentPath = tempDir.resolve("nonexistent.txt").toString();
            var result = FilesUtils.exists(nonExistentPath);
            assertFalse(result);
        }

        @Test
        @DisplayName("should return false when string path is null")
        void shouldReturnFalseWhenStringPathIsNull() {
            var result = FilesUtils.exists((String) null);
            assertFalse(result);
        }

        @Test
        @DisplayName("should return true when string path exists")
        void shouldReturnTrueWhenStringPathExists() {
            var result = FilesUtils.exists(existingPath.toString());
            assertTrue(result);
        }
    }

    @Nested
    @DisplayName("Make Directory Tests")
    class MakeDirectoryTests {
        @TempDir
        Path tempDir;

        @Test
        @DisplayName("mkdirs(File) with existing directory returns true")
        void mkdirsFileWithExistingDirectoryReturnsTrue() {
            var existing = tempDir.toFile();
            assertTrue(FilesUtils.mkdirs(existing));
        }

        @Test
        @DisplayName("mkdirs(File) with existing file returns false")
        void mkdirsFileWithExistingFileReturnsFalse() throws IOException {
            var file = new File(tempDir.toFile(), "existing-file.txt");
            assertTrue(file.createNewFile());

            assertFalse(FilesUtils.mkdirs(file));
        }

        @Test
        @DisplayName("mkdirs(File) with non-existing directory creates and returns true")
        void mkdirsFileWithNonExistingDirectoryCreatesAndReturnsTrue() {
            var newDir = new File(tempDir.toFile(), "new/nested/directory");
            assertFalse(newDir.exists());

            assertTrue(FilesUtils.mkdirs(newDir));
            assertTrue(newDir.exists());
            assertTrue(newDir.isDirectory());
        }

        @ParameterizedTest
        @NullSource
        @DisplayName("mkdirs(File) with null returns false")
        void mkdirsFileWithNullReturnsFalse(File file) {
            assertFalse(FilesUtils.mkdirs(file));
        }

        @Test
        @DisplayName("mkdirs(Path) with existing directory returns true")
        void mkdirsPathWithExistingDirectoryReturnsTrue() {
            assertTrue(FilesUtils.mkdirs(tempDir));
        }

        @Test
        @DisplayName("mkdirs(Path) with existing file returns false")
        void mkdirsPathWithExistingFileReturnsFalse() throws IOException {
            var file = tempDir.resolve("existing-file.txt");
            Files.createFile(file);

            assertFalse(FilesUtils.mkdirs(file));
        }

        @Test
        @DisplayName("mkdirs(Path) with non-existing directory creates and returns true")
        void mkdirsPathWithNonExistingDirectoryCreatesAndReturnsTrue() {
            var newDir = tempDir.resolve("new/nested/directory");
            assertFalse(Files.exists(newDir));

            assertTrue(FilesUtils.mkdirs(newDir));
            assertTrue(Files.exists(newDir));
            assertTrue(Files.isDirectory(newDir));
        }

        @ParameterizedTest
        @NullSource
        @DisplayName("mkdirs(Path) with null returns false")
        void mkdirsPathWithNullReturnsFalse(Path path) {
            assertFalse(FilesUtils.mkdirs(path));
        }

        @Test
        @DisplayName("mkdirs File and Path versions have consistent behavior")
        void mkdirsFileAndPathVersionsAreConsistent() {
            var path = tempDir.resolve("consistent/test/dir");
            var file = path.toFile();

            var pathResult = FilesUtils.mkdirs(path);
            var fileResult = FilesUtils.mkdirs(file);

            assertEquals(pathResult, fileResult);
            assertTrue(Files.exists(path));
            assertTrue(file.exists());
        }

        @Nested
        @DisplayName("mkdirs(String) Tests")
        class MkdirsStringTest {
            @Nested
            @DisplayName("When using absolute paths")
            class AbsolutePaths {
                @Test
                @DisplayName("Should handle absolute path")
                void shouldHandleAbsolutePath(@TempDir Path tempDir) {
                    var absolute = tempDir.resolve("absolute/path/test").toAbsolutePath().toString();

                    assertTrue(FilesUtils.mkdirs(absolute));
                    var absolutePath = Path.of(absolute);
                    assertTrue(Files.exists(absolutePath));
                    assertTrue(Files.isDirectory(absolutePath));
                }
            }

            @Nested
            @DisplayName("When file exists at path")
            class FileExistsAtPath {
                @Test
                @DisplayName("Should return false when file exists at target path")
                void shouldReturnFalseWhenFileExists(@TempDir Path tempDir) throws IOException {
                    var filePath = tempDir.resolve("existingfile");
                    Files.createFile(filePath);

                    assertFalse(FilesUtils.mkdirs(filePath.toString()));
                }
            }

            @Nested
            @DisplayName("When creating nested directories")
            class NestedDirectories {
                @Test
                @DisplayName("Should create deeply nested directories")
                void shouldCreateDeeplyNestedDirectories(@TempDir Path tempDir) {
                    var deepPath = tempDir.resolve("a/b/c/d/e/f/g").toString();

                    assertTrue(FilesUtils.mkdirs(deepPath));
                    assertTrue(Files.exists(Path.of(deepPath)));
                }

                @Test
                @DisplayName("Should create all parent directories")
                void shouldCreateParentDirectories(@TempDir Path tempDir) {
                    var grandchild = tempDir.resolve("parent/child/grandchild").toString();

                    assertTrue(FilesUtils.mkdirs(grandchild));
                    var grandchildPath = Path.of(grandchild);
                    assertTrue(Files.exists(grandchildPath));
                    assertTrue(Files.isDirectory(grandchildPath));
                }
            }

            @Nested
            @DisplayName("When path is null or blank")
            class NullOrBlankPath {
                @ParameterizedTest
                @NullAndEmptySource
                @ValueSource(strings = {" ", "  ", "\t", "\n", " \t\n "})
                @DisplayName("Should return false")
                void shouldReturnFalse(String path) {
                    assertFalse(FilesUtils.mkdirs(path));
                }
            }

            @Nested
            @DisplayName("When creating single directory")
            class SingleDirectory {
                @Test
                @DisplayName("Should create directory and return true")
                void shouldCreateDirectory(@TempDir Path tempDir) {
                    var newDir = tempDir.resolve("newdir").toString();

                    assertTrue(FilesUtils.mkdirs(newDir));
                    var newDirPath = Path.of(newDir);
                    assertTrue(Files.exists(newDirPath));
                    assertTrue(Files.isDirectory(newDirPath));
                }

                @Test
                @DisplayName("Should return true when directory already exists")
                void shouldReturnTrueWhenExists(@TempDir Path tempDir) throws IOException {
                    var existingDir = tempDir.resolve("existing");
                    Files.createDirectory(existingDir);

                    assertTrue(FilesUtils.mkdirs(existingDir.toString()));
                    assertTrue(Files.exists(existingDir));
                }
            }

            @Nested
            @DisplayName("When path contains special characters")
            class SpecialCharacters {
                @ParameterizedTest
                @ValueSource(strings = {"dir-with-dash", "dirWith_underscore", "dir.with.dots"})
                @DisplayName("Should handle special characters in directory names")
                void shouldHandleSpecialCharacters(String dirName, @TempDir Path tempDir) {
                    var dirPath = tempDir.resolve(dirName).toString();

                    assertTrue(FilesUtils.mkdirs(dirPath));
                    assertTrue(Files.exists(Path.of(dirPath)));
                }
            }
        }
    }

    @Nested
    @DisplayName("Parameterized tests for edge cases")
    class ParameterizedEdgeCaseTests {
        @ParameterizedTest
        @NullSource
        @DisplayName("exists(File) should handle null input")
        void existsFileShouldHandleNull(File file) {
            var result = FilesUtils.exists(file);
            assertFalse(result);
        }

        @ParameterizedTest
        @NullSource
        @DisplayName("exists(Path) should handle null input")
        void existsPathShouldHandleNull(Path path) {
            var result = FilesUtils.exists(path);
            assertFalse(result);
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " ", "   "})
        @DisplayName("exists(String) should handle empty and whitespace strings")
        void existsStringShouldHandleEmptyStrings(String path) {
            var result = FilesUtils.exists(path);
            assertFalse(result);
        }

        @ParameterizedTest
        @NullSource
        @DisplayName("exists(String) should handle null input")
        void existsStringShouldHandleNull(String path) {
            var result = FilesUtils.exists(path);
            assertFalse(result);
        }

        @ParameterizedTest
        @NullSource
        @DisplayName("notExists(File) should handle null input")
        void notExistsFileShouldHandleNull(File file) {
            var result = FilesUtils.notExists(file);
            assertTrue(result);
        }

        @ParameterizedTest
        @NullSource
        @DisplayName("notExists(Path) should handle null input")
        void notExistsPathShouldHandleNull(Path path) {
            var result = FilesUtils.notExists(path);
            assertTrue(result);
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " ", "   "})
        @DisplayName("notExists(String) should handle empty and whitespace strings")
        void notExistsStringShouldHandleEmptyStrings(String path) {
            var result = FilesUtils.notExists(path);
            assertTrue(result);
        }

        @ParameterizedTest
        @NullSource
        @DisplayName("notExists(String) should handle null input")
        void notExistsStringShouldHandleNull(String path) {
            var result = FilesUtils.notExists(path);
            assertTrue(result);
        }
    }

    @Nested
    @DisplayName("notExists(File) tests")
    class notExistsFileTests {
        @TempDir
        Path tempDir;

        @TempDir
        Path tempFile;

        @Test
        @DisplayName("should return false when file exists")
        void shouldReturnFalseWhenFileExists() {
            var existingFile = tempFile.toFile();
            var result = FilesUtils.notExists(existingFile);
            assertFalse(result);
        }

        @Test
        @DisplayName("should return true when file does not exist")
        void shouldReturnTrueWhenFileDoesNotExist() {
            var nonExistentFile = new File(tempDir.toFile(), "nonexistent.txt");
            var result = FilesUtils.notExists(nonExistentFile);
            assertTrue(result);
        }

        @Test
        @DisplayName("should return true when file is null")
        void shouldReturnTrueWhenFileIsNull() {
            var result = FilesUtils.notExists((File) null);
            assertTrue(result);
        }
    }

    @Nested
    @DisplayName("notExists(Path) tests")
    class notExistsPathTests {
        @TempDir
        Path existingPath;
        @TempDir
        Path tempDir;

        @Test
        @DisplayName("should return false when path exists")
        void shouldReturnFalseWhenPathExists() {
            var result = FilesUtils.notExists(existingPath);
            assertFalse(result);
        }

        @Test
        @DisplayName("should return true when path does not exist")
        void shouldReturnTrueWhenPathDoesNotExist() {
            var nonExistentPath = tempDir.resolve("nonexistent.txt");
            var result = FilesUtils.notExists(nonExistentPath);
            assertTrue(result);
        }

        @Test
        @DisplayName("should return true when path is null")
        void shouldReturnTrueWhenPathIsNull() {
            var result = FilesUtils.notExists((Path) null);
            assertTrue(result);
        }
    }

    @Nested
    @DisplayName("notExists(String) tests")
    class notExistsStringTests {
        @TempDir
        Path existingPath;
        @TempDir
        Path tempDir;

        @Test
        @DisplayName("should return false when string path exists")
        void shouldReturnFalseWhenStringPathExists() {
            var result = FilesUtils.notExists(existingPath.toString());
            assertFalse(result);
        }

        @Test
        @DisplayName("should return true when string path does not exist")
        void shouldReturnTrueWhenStringPathDoesNotExist() {
            var nonExistentPath = tempDir.resolve("nonexistent.txt").toString();
            var result = FilesUtils.notExists(nonExistentPath);
            assertTrue(result);
        }

        @Test
        @DisplayName("should return true when string path is null")
        void shouldReturnTrueWhenStringPathIsNull() {
            var result = FilesUtils.notExists((String) null);
            assertTrue(result);
        }
    }
}