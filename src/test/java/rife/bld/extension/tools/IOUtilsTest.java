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

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("IO Utils Tests")
@SuppressWarnings({"PMD.AvoidDuplicateLiterals", "ConstantValue", "PMD.TestClassWithoutTestCases"})
class IOUtilsTest {

    @Nested
    @DisplayName("canExecute(...) Tests")
    class CanExecuteTests {

        @Nested
        @DisplayName("canExecute(File)")
        class CanExecuteFileTest {

            @Test
            @DisplayName("should return false for directory")
            void shouldReturnFalseForDirectory(@TempDir Path tempDir) {
                var dir = tempDir.toFile();
                assertFalse(IOUtils.canExecute(dir));
            }

            @Test
            @DisplayName("should return false for non-executable file")
            @DisabledOnOs(OS.WINDOWS)
            void shouldReturnFalseForNonExecutableFile(@TempDir Path tempDir) throws IOException {
                var file = tempDir.resolve("test.txt").toFile();
                assertTrue(file.createNewFile());
                assertTrue(file.setExecutable(false));
                assertFalse(IOUtils.canExecute(file));
            }

            @Test
            @DisplayName("should return false for non-existent file")
            void shouldReturnFalseForNonExistentFile() {
                var file = new File("non-existent-file-12345.txt");
                assertFalse(IOUtils.canExecute(file));
            }

            @ParameterizedTest
            @NullSource
            @DisplayName("should return false for null file")
            void shouldReturnFalseForNullFile(File file) {
                assertFalse(IOUtils.canExecute(file));
            }

            @Test
            @DisplayName("should return true for executable file")
            void shouldReturnTrueForExecutableFile(@TempDir Path tempDir) throws IOException {
                var file = tempDir.resolve("test.sh").toFile();
                assertTrue(file.createNewFile());
                assertTrue(file.setExecutable(true));
                assertTrue(IOUtils.canExecute(file));
            }

            @Test
            @DisplayName("should return true for executable file with POSIX permissions")
            void shouldReturnTrueForExecutableFileWithPosixPermissions(@TempDir Path tempDir) throws IOException {
                var filePath = tempDir.resolve("script.sh");
                Files.createFile(filePath);

                try {
                    var permissions = Set.of(
                            PosixFilePermission.OWNER_READ,
                            PosixFilePermission.OWNER_WRITE,
                            PosixFilePermission.OWNER_EXECUTE
                    );
                    Files.setPosixFilePermissions(filePath, permissions);
                    assertTrue(IOUtils.canExecute(filePath.toFile()));
                } catch (UnsupportedOperationException e) {
                    // POSIX not supported on this file system (e.g., Windows)
                    var file = filePath.toFile();
                    assertTrue(file.setExecutable(true));
                    assertTrue(IOUtils.canExecute(file));
                }
            }
        }

        @Nested
        @DisplayName("canExecute(Path)")
        class CanExecutePathTest {

            @Test
            @DisplayName("should handle symbolic links to executable files")
            void shouldHandleSymbolicLinksToExecutableFiles(@TempDir Path tempDir) throws IOException {
                var targetFile = tempDir.resolve("target.sh");
                Files.createFile(targetFile);

                try {
                    var permissions = Set.of(
                            PosixFilePermission.OWNER_READ,
                            PosixFilePermission.OWNER_WRITE,
                            PosixFilePermission.OWNER_EXECUTE
                    );
                    Files.setPosixFilePermissions(targetFile, permissions);

                    var symlink = tempDir.resolve("link.sh");
                    Files.createSymbolicLink(symlink, targetFile);

                    assertTrue(IOUtils.canExecute(symlink));
                } catch (UnsupportedOperationException e) {
                    // Symbolic links or POSIX not supported on this file system
                    var ignored = targetFile.toFile().setExecutable(true);
                }
            }

            @Test
            @DisplayName("should return false for directory")
            void shouldReturnFalseForDirectory(@TempDir Path tempDir) {
                assertFalse(IOUtils.canExecute(tempDir));
            }

            @Test
            @DisplayName("should return false for non-executable file")
            @DisabledOnOs(OS.WINDOWS)
            void shouldReturnFalseForNonExecutableFile(@TempDir Path tempDir) throws IOException {
                var path = tempDir.resolve("test.txt");
                Files.createFile(path);

                try {
                    var permissions = Set.of(
                            PosixFilePermission.OWNER_READ,
                            PosixFilePermission.OWNER_WRITE
                    );
                    Files.setPosixFilePermissions(path, permissions);
                } catch (UnsupportedOperationException e) {
                    // POSIX not supported, use File API
                    var ignored = path.toFile().setExecutable(false);
                }

                assertFalse(IOUtils.canExecute(path));
            }

            @Test
            @DisplayName("should return false for non-existent path")
            void shouldReturnFalseForNonExistentPath() {
                var path = Path.of("non-existent-file-12345.txt");
                assertFalse(IOUtils.canExecute(path));
            }

            @ParameterizedTest
            @NullSource
            @DisplayName("should return false for null path")
            void shouldReturnFalseForNullPath(Path path) {
                assertFalse(IOUtils.canExecute(path));
            }

            @Test
            @DisplayName("should return true for executable file")
            void shouldReturnTrueForExecutableFile(@TempDir Path tempDir) throws IOException {
                var path = tempDir.resolve("test.sh");
                Files.createFile(path);

                try {
                    var permissions = Set.of(
                            PosixFilePermission.OWNER_READ,
                            PosixFilePermission.OWNER_WRITE,
                            PosixFilePermission.OWNER_EXECUTE
                    );
                    Files.setPosixFilePermissions(path, permissions);
                } catch (UnsupportedOperationException e) {
                    // POSIX not supported, use File API
                    var ignored = path.toFile().setExecutable(true);
                }

                assertTrue(IOUtils.canExecute(path));
            }

            @Test
            @DisplayName("should return true for system executable")
            void shouldReturnTrueForSystemExecutable() {
                // Test with a system executable that should exist on most systems
                var executable = System.getProperty("os.name").toLowerCase().contains("win")
                        ? "C:\\Windows\\System32\\cmd.exe"
                        : "/bin/sh";

                var path = Path.of(executable);
                if (Files.exists(path)) {
                    assertTrue(IOUtils.canExecute(path));
                }
            }
        }

        @Nested
        @DisplayName("canExecute(String)")
        class CanExecuteStringTest {

            @Test
            @DisplayName("should handle invalid char in path")
            void shouldHandleInvalidCharInPath() {
                assertFalse(IOUtils.canExecute("foo\0bar"));
            }

            @Test
            @DisplayName("should return false for blank string")
            void shouldReturnFalseForBlankString() {
                assertFalse(IOUtils.canExecute(""));
                assertFalse(IOUtils.canExecute("   "));
            }

            @Test
            @DisplayName("should return false for directory")
            void shouldReturnFalseForDirectory(@TempDir Path tempDir) {
                assertFalse(IOUtils.canExecute(tempDir.toString()));
            }

            @Test
            @DisplayName("should return false for non-executable file")
            @DisabledOnOs(OS.WINDOWS)
            void shouldReturnFalseForNonExecutableFile(@TempDir Path tempDir) throws IOException {
                var path = tempDir.resolve("test.txt");
                Files.createFile(path);

                try {
                    var permissions = Set.of(
                            PosixFilePermission.OWNER_READ,
                            PosixFilePermission.OWNER_WRITE
                    );
                    Files.setPosixFilePermissions(path, permissions);
                } catch (UnsupportedOperationException e) {
                    // POSIX not supported, use File API
                    var ignored = path.toFile().setExecutable(false);
                }

                assertFalse(IOUtils.canExecute(path.toString()));
            }

            @Test
            @DisplayName("should return false for non-existent path")
            void shouldReturnFalseForNonExistentPath() {
                assertFalse(IOUtils.canExecute("non-existent-file-12345.txt"));
            }

            @ParameterizedTest
            @NullSource
            @DisplayName("should return false for null string")
            void shouldReturnFalseForNullString(String path) {
                assertFalse(IOUtils.canExecute(path));
            }

            @Test
            @DisplayName("should return true for executable file")
            void shouldReturnTrueForExecutableFile(@TempDir Path tempDir) throws IOException {
                var path = tempDir.resolve("test.sh");
                Files.createFile(path);

                try {
                    var permissions = Set.of(
                            PosixFilePermission.OWNER_READ,
                            PosixFilePermission.OWNER_WRITE,
                            PosixFilePermission.OWNER_EXECUTE
                    );
                    Files.setPosixFilePermissions(path, permissions);
                } catch (UnsupportedOperationException e) {
                    // POSIX not supported, use File API
                    var ignored = path.toFile().setExecutable(true);
                }

                assertTrue(IOUtils.canExecute(path.toString()));
            }

            @Test
            @DisplayName("should return true for system executable")
            void shouldReturnTrueForSystemExecutable() {
                // Test with a system executable that should exist on most systems
                String executable = System.getProperty("os.name").toLowerCase().contains("win")
                        ? "C:\\Windows\\System32\\cmd.exe"
                        : "/bin/sh";

                if (Files.exists(Path.of(executable))) {
                    assertTrue(IOUtils.canExecute(executable));
                }
            }
        }
    }

    @Nested
    @DisplayName("exists(...) Tests")
    class ExistsTests {


        @Nested
        @DisplayName("exists edge cases")
        class ExistsEdgeCaseTests {

            @ParameterizedTest
            @NullSource
            @DisplayName("exists(File) should handle null input")
            void existsFileShouldHandleNull(File file) {
                var result = IOUtils.exists(file);
                assertFalse(result);
            }

            @ParameterizedTest
            @NullSource
            @DisplayName("exists(Path) should handle null input")
            void existsPathShouldHandleNull(Path path) {
                var result = IOUtils.exists(path);
                assertFalse(result);
            }

            @ParameterizedTest
            @ValueSource(strings = {"", " ", "   "})
            @DisplayName("exists(String) should handle empty and whitespace strings")
            void existsStringShouldHandleEmptyStrings(String path) {
                var result = IOUtils.exists(path);
                assertFalse(result);
            }

            @ParameterizedTest
            @NullSource
            @DisplayName("exists(String) should handle null input")
            void existsStringShouldHandleNull(String path) {
                var result = IOUtils.exists(path);
                assertFalse(result);
            }
        }

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
                var result = IOUtils.exists(nonExistentFile);
                assertFalse(result);
            }

            @Test
            @DisplayName("should return false when file is null")
            void shouldReturnFalseWhenFileIsNull() {
                var result = IOUtils.exists((File) null);
                assertFalse(result);
            }

            @Test
            @DisplayName("should return true when file exists")
            void shouldReturnTrueWhenFileExists() {
                var existingFile = tempFile.toFile();
                var result = IOUtils.exists(existingFile);
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
                var result = IOUtils.exists(nonExistentPath);
                assertFalse(result);
            }

            @Test
            @DisplayName("should return false when path is null")
            void shouldReturnFalseWhenPathIsNull() {
                var result = IOUtils.exists((Path) null);
                assertFalse(result);
            }

            @Test
            @DisplayName("should return true when path exists")
            void shouldReturnTrueWhenPathExists() {
                var result = IOUtils.exists(existingPath);
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
            @DisplayName("should handle invalid char in path")
            void shouldHandleInvalidCharInPath() {
                assertFalse(IOUtils.exists("foo\0bar"));
            }

            @Test
            @DisplayName("should return false when string path does not exist")
            void shouldReturnFalseWhenStringPathDoesNotExist() {
                var nonExistentPath = tempDir.resolve("nonexistent.txt").toString();
                var result = IOUtils.exists(nonExistentPath);
                assertFalse(result);
            }

            @Test
            @DisplayName("should return false when string path is null")
            void shouldReturnFalseWhenStringPathIsNull() {
                var result = IOUtils.exists((String) null);
                assertFalse(result);
            }

            @Test
            @DisplayName("should return true when string path exists")
            void shouldReturnTrueWhenStringPathExists() {
                var result = IOUtils.exists(existingPath.toString());
                assertTrue(result);
            }
        }
    }

    @Nested
    @DisplayName("IsDirectory Tests")
    @SuppressWarnings("PMD.UseUtilityClass")
    class IsDirectoryTest {

        private static Path nonExistentPath;
        private static Path tempDir;
        private static Path tempFile;

        @BeforeAll
        static void setUpAll() throws IOException {
            var baseDir = Files.createTempDirectory("IOUtils-test");
            tempDir = Files.createDirectory(baseDir.resolve("test-directory"));
            tempFile = Files.createFile(baseDir.resolve("test-file.txt"));
            nonExistentPath = baseDir.resolve("non-existent");
        }

        @AfterAll
        static void tearDownAll() throws IOException {
            if (tempFile != null && Files.exists(tempFile)) {
                Files.delete(tempFile);
            }
            if (tempDir != null && Files.exists(tempDir)) {
                Files.delete(tempDir);
            }
            if (tempDir != null && tempDir.getParent() != null) {
                Files.delete(tempDir.getParent());
            }
        }

        @Nested
        @DisplayName("Cross-type consistency tests")
        class ConsistencyTests {

            @Test
            @DisplayName("File, Path, and String should return same result for directory")
            void testDirectoryConsistency() {
                var file = tempDir.toFile();
                var path = tempDir;
                var string = tempDir.toString();

                boolean fileResult = IOUtils.isDirectory(file);
                boolean pathResult = IOUtils.isDirectory(path);
                boolean stringResult = IOUtils.isDirectory(string);

                assertTrue(fileResult);
                assertTrue(pathResult);
                assertTrue(stringResult);
                assertEquals(fileResult, pathResult);
                assertEquals(pathResult, stringResult);
            }

            @Test
            @DisplayName("File, Path, and String should return same result for regular file")
            void testFileConsistency() {
                var file = tempFile.toFile();
                var path = tempFile;
                var string = tempFile.toString();

                boolean fileResult = IOUtils.isDirectory(file);
                boolean pathResult = IOUtils.isDirectory(path);
                boolean stringResult = IOUtils.isDirectory(string);

                assertFalse(fileResult);
                assertFalse(pathResult);
                assertFalse(stringResult);
                assertEquals(fileResult, pathResult);
                assertEquals(pathResult, stringResult);
            }

            @Test
            @DisplayName("File, Path, and String should return same result for non-existent")
            void testNonExistentConsistency() {
                var file = nonExistentPath.toFile();
                var path = nonExistentPath;
                var string = nonExistentPath.toString();

                boolean fileResult = IOUtils.isDirectory(file);
                boolean pathResult = IOUtils.isDirectory(path);
                boolean stringResult = IOUtils.isDirectory(string);

                assertFalse(fileResult);
                assertFalse(pathResult);
                assertFalse(stringResult);
                assertEquals(fileResult, pathResult);
                assertEquals(pathResult, stringResult);
            }
        }

        @Nested
        @DisplayName("isDirectory(File) tests")
        class FileTests {

            @Test
            @DisplayName("should return true for existing directory")
            void testExistingDirectory() {
                var dir = tempDir.toFile();
                assertTrue(IOUtils.isDirectory(dir));
            }

            @Test
            @DisplayName("should return true for nested directory")
            void testNestedDirectory() throws IOException {
                var nested = Files.createDirectory(tempDir.resolve("nested"));
                try {
                    assertTrue(IOUtils.isDirectory(nested.toFile()));
                } finally {
                    Files.delete(nested);
                }
            }

            @Test
            @DisplayName("should return false for non-existent file")
            void testNonExistentFile() {
                var file = nonExistentPath.toFile();
                assertFalse(IOUtils.isDirectory(file));
            }

            @Test
            @DisplayName("should return false when file is null")
            void testNullFile() {
                assertFalse(IOUtils.isDirectory((File) null));
            }

            @Test
            @DisplayName("should return false for regular file")
            void testRegularFile() {
                var file = tempFile.toFile();
                assertFalse(IOUtils.isDirectory(file));
            }

            @ParameterizedTest
            @DisplayName("should handle system directories")
            @ValueSource(strings = {".", ".."})
            void testSystemDirectories(String path) {
                var file = new File(path);
                assertTrue(IOUtils.isDirectory(file));
            }
        }

        @Nested
        @DisplayName("isDirectory(Path) tests")
        class PathTests {

            @Test
            @DisplayName("should return true for existing directory")
            void testExistingDirectory() {
                assertTrue(IOUtils.isDirectory(tempDir));
            }

            @Test
            @DisplayName("should return true for nested directory")
            void testNestedDirectory() throws IOException {
                var nested = Files.createDirectory(tempDir.resolve("nested-path"));
                try {
                    assertTrue(IOUtils.isDirectory(nested));
                } finally {
                    Files.delete(nested);
                }
            }

            @Test
            @DisplayName("should return false for non-existent path")
            void testNonExistentPath() {
                assertFalse(IOUtils.isDirectory(nonExistentPath));
            }

            @Test
            @DisplayName("should return false when path is null")
            void testNullPath() {
                assertFalse(IOUtils.isDirectory((Path) null));
            }

            @Test
            @DisplayName("should return false for regular file")
            void testRegularFile() {
                assertFalse(IOUtils.isDirectory(tempFile));
            }

            @Test
            @DisplayName("should handle symbolic link to directory")
            @DisabledOnOs(OS.WINDOWS)
            void testSymbolicLinkToDirectory() throws IOException {
                var link = tempDir.getParent().resolve("link-to-dir");
                try {
                    Files.createSymbolicLink(link, tempDir);
                    assertTrue(IOUtils.isDirectory(link));
                } finally {
                    if (Files.exists(link)) {
                        Files.delete(link);
                    }
                }
            }

            @ParameterizedTest
            @DisplayName("should handle system paths")
            @ValueSource(strings = {".", ".."})
            void testSystemPaths(String pathStr) {
                var path = Path.of(pathStr);
                assertTrue(IOUtils.isDirectory(path));
            }
        }

        @Nested
        @DisplayName("isDirectory(String) tests")
        class StringTests {

            static Stream<String> provideInvalidPaths() {
                return Stream.of(
                        "/non/existent/path",
                        "invalid-directory-name-12345",
                        tempFile.toString() + "/subpath"
                );
            }

            @ParameterizedTest
            @DisplayName("should return false for blank strings")
            @NullAndEmptySource
            @ValueSource(strings = {"  ", "\t", "\n", "   \t\n  "})
            void testBlankStrings(String path) {
                assertFalse(IOUtils.isDirectory(path));
            }

            @Test
            @DisplayName("should return true for existing directory")
            void testExistingDirectory() {
                assertTrue(IOUtils.isDirectory(tempDir.toString()));
            }

            @Test
            @DisplayName("should handle invalid character in path")
            @EnabledOnOs({OS.MAC, OS.LINUX})
            void testInvalidCharInPath() {
                assertFalse(IOUtils.isDirectory("foo\0bar"));
            }

            @ParameterizedTest
            @DisplayName("should return false for paths with invalid characters")
            @ValueSource(strings = {
                    "path/with\u0000null",
                    "path<with>invalid",
                    "path|with|pipes",
                    "path\"with\"quotes"
            })
            @EnabledOnOs(OS.WINDOWS)
            void testInvalidCharactersWindows(String invalidPath) {
                // Windows-specific invalid characters should return false
                assertFalse(IOUtils.isDirectory(invalidPath));
            }

            @ParameterizedTest
            @DisplayName("should return false for invalid path strings")
            @MethodSource("provideInvalidPaths")
            void testInvalidPaths(String path) {
                assertFalse(IOUtils.isDirectory(path));
            }

            @Test
            @DisplayName("should return true for nested directory")
            void testNestedDirectory() throws IOException {
                var nested = Files.createDirectory(tempDir.resolve("nested-string"));
                try {
                    assertTrue(IOUtils.isDirectory(nested.toString()));
                } finally {
                    Files.delete(nested);
                }
            }

            @Test
            @DisplayName("should return false for non-existent path")
            void testNonExistentPath() {
                assertFalse(IOUtils.isDirectory(nonExistentPath.toString()));
            }

            @Test
            @DisplayName("should return false when string is null")
            void testNullString() {
                assertFalse(IOUtils.isDirectory((String) null));
            }

            @Test
            @DisplayName("should return false for path with leading spaces")
            void testPathWithLeadingSpaces() {
                // The method should NOT trim, so paths with leading spaces should fail
                var pathWithLeadingSpaces = " " + tempDir.toString();
                assertFalse(IOUtils.isDirectory(pathWithLeadingSpaces));
            }

            @Test
            @DisplayName("should handle path with special characters")
            void testPathWithSpecialCharacters() throws IOException {
                var specialDir = Files.createDirectory(tempDir.resolve("test-dir_123"));
                try {
                    assertTrue(IOUtils.isDirectory(specialDir.toString()));
                } finally {
                    Files.delete(specialDir);
                }
            }

            @Test
            @DisplayName("should return false for path with trailing spaces")
            void testPathWithTrailingSpaces() {
                // InvalidPathException is caught and returns false
                var pathWithTrailingSpaces = tempDir.toString() + " ";
                assertFalse(IOUtils.isDirectory(pathWithTrailingSpaces));
            }

            @Test
            @DisplayName("should return false for regular file")
            void testRegularFile() {
                assertFalse(IOUtils.isDirectory(tempFile.toString()));
            }

            @ParameterizedTest
            @DisplayName("should handle system path strings")
            @ValueSource(strings = {".", ".."})
            void testSystemPathStrings(String path) {
                assertTrue(IOUtils.isDirectory(path));
            }
        }
    }

    @Nested
    @DisplayName("Make Directory Tests")
    class MakeDirectoryTests {

        @TempDir
        Path tempDir;

        @Test
        @DisplayName("mkdirs File and Path versions have consistent behavior")
        void mkdirsFileAndPathVersionsAreConsistent() {
            var path = tempDir.resolve("consistent/test/dir");
            var file = path.toFile();

            var pathResult = IOUtils.mkdirs(path);
            var fileResult = IOUtils.mkdirs(file);

            assertEquals(pathResult, fileResult);
            assertTrue(Files.exists(path));
            assertTrue(file.exists());
        }

        @Test
        @DisplayName("mkdirs(File) with existing directory returns true")
        void mkdirsFileWithExistingDirectoryReturnsTrue() {
            var existing = tempDir.toFile();
            assertTrue(IOUtils.mkdirs(existing));
        }

        @Test
        @DisplayName("mkdirs(File) with existing file returns false")
        void mkdirsFileWithExistingFileReturnsFalse() throws IOException {
            var file = new File(tempDir.toFile(), "existing-file.txt");
            assertTrue(file.createNewFile());

            assertFalse(IOUtils.mkdirs(file));
        }

        @Test
        @DisplayName("mkdirs(File) with non-existing directory creates and returns true")
        void mkdirsFileWithNonExistingDirectoryCreatesAndReturnsTrue() {
            var newDir = new File(tempDir.toFile(), "new/nested/directory");
            assertFalse(newDir.exists());

            assertTrue(IOUtils.mkdirs(newDir));
            assertTrue(newDir.exists());
            assertTrue(newDir.isDirectory());
        }

        @ParameterizedTest
        @NullSource
        @DisplayName("mkdirs(File) with null returns false")
        void mkdirsFileWithNullReturnsFalse(File file) {
            assertFalse(IOUtils.mkdirs(file));
        }

        @Test
        @DisplayName("mkdirs(Path) with existing directory returns true")
        void mkdirsPathWithExistingDirectoryReturnsTrue() {
            assertTrue(IOUtils.mkdirs(tempDir));
        }

        @Test
        @DisplayName("mkdirs(Path) with existing file returns false")
        void mkdirsPathWithExistingFileReturnsFalse() throws IOException {
            var file = tempDir.resolve("existing-file.txt");
            Files.createFile(file);

            assertFalse(IOUtils.mkdirs(file));
        }

        @Test
        @DisplayName("mkdirs(Path) with non-existing directory creates and returns true")
        void mkdirsPathWithNonExistingDirectoryCreatesAndReturnsTrue() {
            var newDir = tempDir.resolve("new/nested/directory");
            assertFalse(Files.exists(newDir));

            assertTrue(IOUtils.mkdirs(newDir));
            assertTrue(Files.exists(newDir));
            assertTrue(Files.isDirectory(newDir));
        }

        @ParameterizedTest
        @NullSource
        @DisplayName("mkdirs(Path) with null returns false")
        void mkdirsPathWithNullReturnsFalse(Path path) {
            assertFalse(IOUtils.mkdirs(path));
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

                    assertTrue(IOUtils.mkdirs(absolute));
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

                    assertFalse(IOUtils.mkdirs(filePath.toString()));
                }

            }

            @Nested
            @DisplayName("When creating nested directories")
            class NestedDirectories {

                @Test
                @DisplayName("Should create deeply nested directories")
                void shouldCreateDeeplyNestedDirectories(@TempDir Path tempDir) {
                    var deepPath = tempDir.resolve("a/b/c/d/e/f/g").toString();

                    assertTrue(IOUtils.mkdirs(deepPath));
                    assertTrue(Files.exists(Path.of(deepPath)));
                }

                @Test
                @DisplayName("Should create all parent directories")
                void shouldCreateParentDirectories(@TempDir Path tempDir) {
                    var grandchild = tempDir.resolve("parent/child/grandchild").toString();

                    assertTrue(IOUtils.mkdirs(grandchild));
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
                    assertFalse(IOUtils.mkdirs(path));
                }

            }

            @Nested
            @DisplayName("When creating single directory")
            class SingleDirectory {

                @Test
                @DisplayName("Should create directory and return true")
                void shouldCreateDirectory(@TempDir Path tempDir) {
                    var newDir = tempDir.resolve("newdir").toString();

                    assertTrue(IOUtils.mkdirs(newDir));
                    var newDirPath = Path.of(newDir);
                    assertTrue(Files.exists(newDirPath));
                    assertTrue(Files.isDirectory(newDirPath));
                }

                @Test
                @DisplayName("Should return true when directory already exists")
                void shouldReturnTrueWhenExists(@TempDir Path tempDir) throws IOException {
                    var existingDir = tempDir.resolve("existing");
                    Files.createDirectory(existingDir);

                    assertTrue(IOUtils.mkdirs(existingDir.toString()));
                    assertTrue(Files.exists(existingDir));
                }

            }

            @Nested
            @DisplayName("When path contains special characters")
            class SpecialCharacters {

                @Test
                @DisplayName("should handle invalid char in path")
                void shouldHandleInvalidCharInPath() {
                    assertFalse(IOUtils.mkdirs("foo\0bar"));
                }

                @ParameterizedTest
                @ValueSource(strings = {"dir-with-dash", "dirWith_underscore", "dir.with.dots"})
                @DisplayName("Should handle special characters in directory names")
                void shouldHandleSpecialCharacters(String dirName, @TempDir Path tempDir) {
                    var dirPath = tempDir.resolve(dirName).toString();

                    assertTrue(IOUtils.mkdirs(dirPath));
                    assertTrue(Files.exists(Path.of(dirPath)));
                }
            }
        }

    }

    @Nested
    @DisplayName("notExists(...) Tests")
    class NotExistsTests {

        @Nested
        @DisplayName("notExists edge cases")
        class NotExistsEdgeCaseTests {

            @ParameterizedTest
            @NullSource
            @DisplayName("notExists(File) should handle null input")
            void notExistsFileShouldHandleNull(File file) {
                var result = IOUtils.notExists(file);
                assertTrue(result);
            }

            @ParameterizedTest
            @NullSource
            @DisplayName("notExists(Path) should handle null input")
            void notExistsPathShouldHandleNull(Path path) {
                var result = IOUtils.notExists(path);
                assertTrue(result);
            }

            @ParameterizedTest
            @ValueSource(strings = {"", " ", "   "})
            @DisplayName("notExists(String) should handle empty and whitespace strings")
            void notExistsStringShouldHandleEmptyStrings(String path) {
                var result = IOUtils.notExists(path);
                assertTrue(result);
            }

            @ParameterizedTest
            @NullSource
            @DisplayName("notExists(String) should handle null input")
            void notExistsStringShouldHandleNull(String path) {
                var result = IOUtils.notExists(path);
                assertTrue(result);
            }
        }

        @Nested
        @DisplayName("notExists(File) tests")
        class NotExistsFileTests {

            @TempDir
            Path tempDir;

            @TempDir
            Path tempFile;

            @Test
            @DisplayName("should return false when file exists")
            void shouldReturnFalseWhenFileExists() {
                var existingFile = tempFile.toFile();
                var result = IOUtils.notExists(existingFile);
                assertFalse(result);
            }

            @Test
            @DisplayName("should return true when file does not exist")
            void shouldReturnTrueWhenFileDoesNotExist() {
                var nonExistentFile = new File(tempDir.toFile(), "nonexistent.txt");
                var result = IOUtils.notExists(nonExistentFile);
                assertTrue(result);
            }

            @Test
            @DisplayName("should return true when file is null")
            void shouldReturnTrueWhenFileIsNull() {
                var result = IOUtils.notExists((File) null);
                assertTrue(result);
            }

        }

        @Nested
        @DisplayName("notExists(Path) tests")
        class NotExistsPathTests {

            @TempDir
            Path existingPath;

            @TempDir
            Path tempDir;

            @Test
            @DisplayName("should return false when path exists")
            void shouldReturnFalseWhenPathExists() {
                var result = IOUtils.notExists(existingPath);
                assertFalse(result);
            }

            @Test
            @DisplayName("should return true when path does not exist")
            void shouldReturnTrueWhenPathDoesNotExist() {
                var nonExistentPath = tempDir.resolve("nonexistent.txt");
                var result = IOUtils.notExists(nonExistentPath);
                assertTrue(result);
            }

            @Test
            @DisplayName("should return true when path is null")
            void shouldReturnTrueWhenPathIsNull() {
                var result = IOUtils.notExists((Path) null);
                assertTrue(result);
            }

        }

        @Nested
        @DisplayName("notExists(String) tests")
        class NotExistsStringTests {

            @TempDir
            Path existingPath;

            @TempDir
            Path tempDir;

            @Test
            @DisplayName("should return false when string path exists")
            void shouldReturnFalseWhenStringPathExists() {
                var result = IOUtils.notExists(existingPath.toString());
                assertFalse(result);
            }

            @Test
            @DisplayName("should return true when string path does not exist")
            void shouldReturnTrueWhenStringPathDoesNotExist() {
                var nonExistentPath = tempDir.resolve("nonexistent.txt").toString();
                var result = IOUtils.notExists(nonExistentPath);
                assertTrue(result);
            }

            @Test
            @DisplayName("should return true when string path is null")
            void shouldReturnTrueWhenStringPathIsNull() {
                var result = IOUtils.notExists((String) null);
                assertTrue(result);
            }
        }
    }

    @Nested
    @DisplayName("Resolve File Tests")
    class ResolveFileTests {

        @Nested
        @DisplayName("When comparing with File constructor behavior")
        class BehaviorConsistencyTests {

            @Test
            @DisplayName("should match behavior of nested File constructors")
            void shouldMatchNestedFileConstructors() {
                var base = new File("home", "user");
                var result = IOUtils.resolveFile(base, "docs", "work", "file.txt");

                var expected = new File(new File(new File(base, "docs"), "work"), "file.txt");
                assertEquals(expected.getPath(), result.getPath());
            }

            @Test
            @DisplayName("should not create actual file on filesystem")
            void shouldNotCreateActualFile() {
                var base = new File(System.getProperty("java.io.tmpdir"));
                var uniqueName = "test-file-" + System.currentTimeMillis();
                var result = IOUtils.resolveFile(base, uniqueName);

                assertFalse(result.exists(), "File should not exist on filesystem");
            }
        }

        @Nested
        @DisplayName("When resolving paths with edge case segments")
        class EdgeCaseSegmentsTests {

            @Test
            @DisplayName("should handle segments with dots for relative navigation")
            void shouldHandleDotsInSegments() {
                var base = new File("home", "user");
                var result = IOUtils.resolveFile(base, "..", "other", "file.txt");

                assertTrue(result.getPath().contains(".."));
                assertTrue(result.getPath().contains("other"));
            }

            @Test
            @DisplayName("should handle segments with special characters")
            void shouldHandleSpecialCharacters() {
                var base = new File("home", "user");
                var result = IOUtils.resolveFile(base, "my-file", "name_with.dots", "file@2024");

                assertTrue(result.getPath().contains("my-file"));
                assertTrue(result.getPath().contains("name_with.dots"));
                assertTrue(result.getPath().contains("file@2024"));
            }

            @Test
            @DisplayName("should handle very long segment names")
            void shouldHandleVeryLongSegmentNames() {
                var base = new File("/home");
                var longName = "a".repeat(255);
                var result = IOUtils.resolveFile(base, longName);

                assertTrue(result.getPath().contains(longName));
            }

            @ParameterizedTest
            @ValueSource(strings = {"", "  ", "\t", "\n"})
            @DisplayName("should handle whitespace and empty segments")
            void shouldHandleWhitespaceSegments(String segment) {
                var base = new File("home", "user");
                var result = IOUtils.resolveFile(base, "before", segment, "after");

                assertNotNull(result);
                assertTrue(result.getPath().contains("before"));
                assertTrue(result.getPath().contains("after"));
            }
        }

        @Nested
        @DisplayName("When resolving paths with multiple segments")
        class MultipleSegmentsTests {

            static Stream<Arguments> multipleSegmentProvider() {
                return Stream.of(
                        Arguments.of(new File("home", "user"),
                                new String[]{"documents", "work", "report.txt"}, 5),
                        Arguments.of(new File("project"),
                                new String[]{"src", "main", "java", "Main.java"}, 5),
                        Arguments.of(Path.of("base", "path").toFile(),
                                new String[]{"App", "config", "settings.xml"}, 5)
                );
            }

            @ParameterizedTest(name = "should resolve {1} segments correctly")
            @MethodSource("multipleSegmentProvider")
            @DisplayName("should append multiple segments in correct order")
            void shouldAppendMultipleSegments(File basePath, String[] segments, int expectedDepth) {
                var result = IOUtils.resolveFile(basePath, segments);

                // Verify all segments appear in the path
                for (String segment : segments) {
                    assertTrue(result.getPath().contains(segment),
                            () -> "Expected path to contain: " + segment + ", but was: " + result.getPath());
                }

                // Verify path depth
                var separatorPattern = "\\".equals(File.separator) ? "\\\\" : File.separator;
                var pathParts = result.getPath().split(separatorPattern);
                assertTrue(pathParts.length >= expectedDepth,
                        () -> "Expected at least " + expectedDepth + " path segments, but got: " + pathParts.length);
            }

            @Test
            @DisplayName("should create deeply nested path structure")
            void shouldCreateDeeplyNestedPath() {
                var base = new File("root");
                var result = IOUtils.resolveFile(base, "a", "b", "c", "d", "e", "f");

                var separatorPattern = "\\".equals(File.separator) ? "\\\\" : File.separator;
                var pathParts = result.getPath().split(separatorPattern);
                assertTrue(pathParts.length >= 7, "Path should have at least 7 segments");
            }
        }

        @Nested
        @DisplayName("When resolving paths with no segments")
        class NoSegmentsTests {

            @Test
            @DisplayName("should return equivalent path with empty varargs")
            void shouldHandleEmptyVarargs() {
                var base = new File("home", "user");
                var result = IOUtils.resolveFile(base);

                // When joining an empty array, should get base + empty string
                var expected = new File(base, "");
                assertEquals(expected.getPath(), result.getPath());
            }

            @Test
            @DisplayName("should handle explicit empty array")
            void shouldHandleExplicitEmptyArray() {
                var base = new File("home", "user");
                var emptySegments = new String[]{};
                var result = IOUtils.resolveFile(base, emptySegments);

                var expected = new File(base, "");
                assertEquals(expected.getPath(), result.getPath());
            }
        }

        @Nested
        @DisplayName("When handling null inputs")
        class NullInputTests {

            @Test
            void shouldHandleNullBase() {
                assertEquals(new File("foo"), IOUtils.resolveFile(null, "foo"));
            }

            @ParameterizedTest
            @NullSource
            @DisplayName("should throw NullPointerException when segments array is null")
            void shouldThrowWhenSegmentsIsNull(String... segments) {
                var base = new File("home", "user");

                assertThrows(NullPointerException.class, () -> IOUtils.resolveFile(base, segments));
            }
        }

        @Nested
        @DisplayName("When verifying platform independence")
        class PlatformIndependenceTests {

            @Test
            @DisplayName("should handle both Unix and Windows base paths gracefully")
            void shouldHandleBothPathStyles() {
                var unixBase = Path.of("usr", "local", "bin").toFile();
                var unixResult = IOUtils.resolveFile(unixBase, "app");
                assertNotNull(unixResult);

                var windowsBase = new File("C:", "Program Files");
                var windowsResult = IOUtils.resolveFile(windowsBase, "App");
                assertNotNull(windowsResult);
            }

            @Test
            @DisplayName("should use platform-specific file separator")
            void shouldUsePlatformSpecificSeparator() {
                var base = new File("home", "user");
                var result = IOUtils.resolveFile(base, "docs", "file.txt");

                var separator = File.separator;
                assertTrue(result.getPath().contains(separator),
                        "Path should contain platform separator: " + separator);
            }
        }

        @Nested
        @DisplayName("When resolving paths with single segment")
        class SingleSegmentTests {

            @Test
            @DisplayName("should append single segment to base path")
            void shouldAppendSingleSegment() {
                var base = new File("home", "user");
                var result = IOUtils.resolveFile(base, "documents");

                var expected = new File(base, "documents").getPath();
                assertEquals(expected, result.getPath());
            }

            @Test
            @DisplayName("should handle Windows-style base path")
            void shouldHandleWindowsStylePath() {
                var base = new File("C:\\Users\\John");
                var result = IOUtils.resolveFile(base, "Desktop");

                assertTrue(result.getPath().endsWith("Desktop"));
                assertTrue(result.getPath().contains("John"));
            }

            @Test
            @DisplayName("should work with relative base path")
            void shouldWorkWithRelativeBase() {
                var base = new File("project");
                var result = IOUtils.resolveFile(base, "src");

                var expected = new File(base, "src").getPath();
                assertEquals(expected, result.getPath());
            }
        }
    }
}