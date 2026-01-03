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
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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