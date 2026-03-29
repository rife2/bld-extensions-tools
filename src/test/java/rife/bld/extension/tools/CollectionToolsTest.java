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

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("PMD.AvoidDuplicateLiterals")
class CollectionToolsTest {

    @Nested
    @DisplayName("combine()")
    class Combine {

        @Test
        @DisplayName("returns empty list when all collections are null")
        void allNullCollectionsReturnsEmpty() {
            var result = CollectionTools.combine((Collection<String>) null, null);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("returns empty list when all collections are empty")
        void emptyCollectionsReturnEmpty() {
            var result = CollectionTools.combine(List.of(), List.of());
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("merges multiple collections in order")
        void multipleCollections() {
            var result = CollectionTools.combine(List.of("a", "b"), List.of("c", "d"));
            assertEquals(List.of("a", "b", "c", "d"), result);
        }

        @Test
        @DisplayName("returns empty list when called with null array")
        void nullArrayReturnsEmpty() {
            var result = CollectionTools.combine((Collection<String>[]) null);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("skips null collections")
        void nullCollectionSkipped() {
            var result = CollectionTools.combine(List.of("a"), null, List.of("b"));
            assertEquals(List.of("a", "b"), result);
        }

        @Test
        @DisplayName("skips null elements within a collection")
        void nullElementsSkipped() {
            var withNulls = new ArrayList<String>();
            withNulls.add("a");
            withNulls.add(null);
            withNulls.add("b");
            var result = CollectionTools.combine(withNulls);
            assertEquals(List.of("a", "b"), result);
        }

        @Test
        @DisplayName("returns unmodifiable list")
        void returnsUnmodifiableList() {
            var result = CollectionTools.combine(List.of("a"));
            assertThrows(UnsupportedOperationException.class, () -> result.add("b"));
        }

        @Test
        @DisplayName("works with Set as input")
        void setInput() {
            var result = CollectionTools.combine(Set.of("x"));
            assertEquals(1, result.size());
            assertTrue(result.contains("x"));
        }

        @Test
        @DisplayName("returns all elements from a single collection")
        void singleCollection() {
            var result = CollectionTools.combine(List.of("a", "b", "c"));
            assertEquals(List.of("a", "b", "c"), result);
        }
    }

    @Nested
    @DisplayName("combineAndMap()")
    class CombineAndMap {

        @Test
        @DisplayName("applies mapper function to each element")
        @SuppressWarnings("unchecked")
        void mapperApplied() {
            var collections = new Collection[]{List.of("a", "bb", "ccc")};
            var result = CollectionTools.combineAndMap(collections, String::length);
            assertEquals(List.of(1, 2, 3), result);
        }

        @Test
        @DisplayName("returns empty list when array is null")
        void nullArrayReturnsEmpty() {
            var result = CollectionTools.combineAndMap(null, String::length);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("skips null collections")
        @SuppressWarnings("unchecked")
        void nullCollectionSkipped() {
            var collections = new Collection[]{List.of("a"), null, List.of("b")};
            var result = CollectionTools.<String, String>combineAndMap(collections, String::toUpperCase);
            assertEquals(List.of("A", "B"), result);
        }

        @Test
        @DisplayName("skips null elements within collections")
        @SuppressWarnings("unchecked")
        void nullElementsSkipped() {
            var list = new ArrayList<String>();
            list.add("a");
            list.add(null);
            list.add("b");
            var collections = new Collection[]{list};
            var result = CollectionTools.<String, String>combineAndMap(collections, String::toUpperCase);
            assertEquals(List.of("A", "B"), result);
        }
    }

    @Nested
    @DisplayName("combineAndMapVarargs()")
    class CombineAndMapVarargs {

        @Test
        @DisplayName("returns empty list when array is empty")
        void emptyArrayReturnsEmpty() {
            var elements = new String[]{};
            var result = CollectionTools.combineAndMapVarargs(elements, String::toUpperCase);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("applies mapper function to each element")
        void mapperApplied() {
            var elements = new String[]{"a", "bb", "ccc"};
            var result = CollectionTools.combineAndMapVarargs(elements, String::length);
            assertEquals(List.of(1, 2, 3), result);
        }

        @Test
        @DisplayName("returns empty list when array is null")
        void nullArrayReturnsEmpty() {
            var result = CollectionTools.combineAndMapVarargs(null, String::length);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("skips null elements")
        void nullElementsSkipped() {
            var elements = new String[]{"a", null, "b"};
            var result = CollectionTools.combineAndMapVarargs(elements, String::toUpperCase);
            assertEquals(List.of("A", "B"), result);
        }
    }

    @Nested
    @DisplayName("combineFilesToPaths()")
    class CombineFilesToPaths {

        @Test
        @DisplayName("returns empty list for empty collections")
        void emptyCollectionsReturnEmpty() {
            var result = CollectionTools.combineFilesToPaths(List.of());
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("returns Path objects for each file")
        void filesToPaths() {
            var file1 = new File("/tmp/a.txt");
            var file2 = new File("/tmp/b.txt");
            var result = CollectionTools.combineFilesToPaths(List.of(file1, file2));
            assertEquals(List.of(Path.of("/tmp/a.txt"), Path.of("/tmp/b.txt")), result);
        }

        @Test
        @DisplayName("merges multiple File collections in order")
        void multipleCollections() {
            var file1 = new File("/tmp/a.txt");
            var file2 = new File("/tmp/b.txt");
            var file3 = new File("/tmp/c.txt");
            var result = CollectionTools.combineFilesToPaths(
                    List.of(file1, file2), List.of(file3));
            assertEquals(List.of(
                    Path.of("/tmp/a.txt"),
                    Path.of("/tmp/b.txt"),
                    Path.of("/tmp/c.txt")), result);
        }

        @Test
        @DisplayName("returns empty list when called with null array")
        void nullArrayReturnsEmpty() {
            var result = CollectionTools.combineFilesToPaths((Collection<File>[]) null);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("skips null collections")
        void nullCollectionSkipped() {
            var file = new File("/tmp/a.txt");
            var result = CollectionTools.combineFilesToPaths(List.of(file), null);
            assertEquals(List.of(Path.of("/tmp/a.txt")), result);
        }

        @Test
        @DisplayName("skips null elements within a collection")
        void nullElementsSkipped() {
            var list = new ArrayList<File>();
            list.add(new File("/tmp/a.txt"));
            list.add(null);
            list.add(new File("/tmp/b.txt"));
            var result = CollectionTools.combineFilesToPaths(list);
            assertEquals(2, result.size());
        }

        @Test
        @DisplayName("round-trips with combinePathsToFiles")
        void roundTrip() {
            var files = List.of(new File("/tmp/a.txt"), new File("/tmp/b.txt"));
            var paths = CollectionTools.combineFilesToPaths(files);
            var result = CollectionTools.combinePathsToFiles(paths);
            assertEquals(files, result);
        }
    }

    @Nested
    @DisplayName("combineFilesToPaths() varargs")
    class CombineFilesToPathsVarargs {

        @Test
        @DisplayName("returns empty list when all elements are null")
        void allNullElementsReturnEmpty() {
            var result = CollectionTools.combineFilesToPaths((File) null, null);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("returns Path objects for each file")
        void filesToPaths() {
            var file1 = new File("/tmp/a.txt");
            var file2 = new File("/tmp/b.txt");
            var result = CollectionTools.combineFilesToPaths(file1, file2);
            assertEquals(List.of(Path.of("/tmp/a.txt"), Path.of("/tmp/b.txt")), result);
        }

        @Test
        @DisplayName("returns empty list when called with no arguments")
        void noArgumentsReturnEmpty() {
            var result = CollectionTools.combineFilesToPaths(new File[0]);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("returns empty list when called with null array")
        void nullArrayReturnsEmpty() {
            var result = CollectionTools.combineFilesToPaths((File[]) null);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("skips null elements")
        void nullElementsSkipped() {
            var file1 = new File("/tmp/a.txt");
            var file2 = new File("/tmp/b.txt");
            var result = CollectionTools.combineFilesToPaths(file1, null, file2);
            assertEquals(List.of(Path.of("/tmp/a.txt"), Path.of("/tmp/b.txt")), result);
        }

        @Test
        @DisplayName("round-trips with combinePathsToFiles varargs")
        void roundTrip() {
            var file1 = new File("/tmp/a.txt");
            var file2 = new File("/tmp/b.txt");
            var paths = CollectionTools.combineFilesToPaths(file1, file2);
            var result = CollectionTools.combinePathsToFiles(paths.toArray(new Path[0]));
            assertEquals(List.of(file1, file2), result);
        }

        @Test
        @DisplayName("works with a single file")
        void singleFile() {
            var file = new File("/tmp/a.txt");
            var result = CollectionTools.combineFilesToPaths(file);
            assertEquals(List.of(Path.of("/tmp/a.txt")), result);
        }
    }

    @Nested
    @DisplayName("combineFilesToStrings()")
    class CombineFilesToStrings {

        @Test
        @DisplayName("returns absolute paths for all files")
        void absolutePaths() {
            var file1 = new File("foo/bar.txt");
            var file2 = new File("baz/qux.txt");
            var result = CollectionTools.combineFilesToStrings(List.of(file1, file2));
            assertEquals(List.of(file1.getAbsolutePath(), file2.getAbsolutePath()), result);
        }

        @Test
        @DisplayName("returns empty list for empty collections")
        void emptyCollectionsReturnEmpty() {
            var result = CollectionTools.combineFilesToStrings(List.of());
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("merges multiple File collections in order")
        void multipleCollections() {
            var file1 = new File("/tmp/a.txt");
            var file2 = new File("/tmp/b.txt");
            var file3 = new File("/tmp/c.txt");
            var result = CollectionTools.combineFilesToStrings(
                    List.of(file1, file2), List.of(file3));
            assertEquals(List.of(
                    file1.getAbsolutePath(),
                    file2.getAbsolutePath(),
                    file3.getAbsolutePath()), result);
        }

        @Test
        @DisplayName("returns empty list when called with null array")
        void nullArrayReturnsEmpty() {
            var result = CollectionTools.combineFilesToStrings((Collection<File>[]) null);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("skips null collections")
        void nullCollectionSkipped() {
            var file = new File("/tmp/a.txt");
            var result = CollectionTools.combineFilesToStrings(List.of(file), null);
            assertEquals(List.of(file.getAbsolutePath()), result);
        }

        @Test
        @DisplayName("skips null elements within a collection")
        void nullElementsSkipped() {
            var list = new ArrayList<File>();
            list.add(new File("/tmp/a.txt"));
            list.add(null);
            list.add(new File("/tmp/b.txt"));
            var result = CollectionTools.combineFilesToStrings(list);
            assertEquals(2, result.size());
        }

        @Test
        @DisplayName("resolves relative paths to absolute using working directory")
        void relativePathBecomesAbsolute() {
            var file = new File("relative/path.txt");
            var result = CollectionTools.combineFilesToStrings(List.of(file));
            assertTrue(new File(result.get(0)).isAbsolute());
        }
    }

    @Nested
    @DisplayName("combineFilesToStrings() varargs")
    class CombineFilesToStringsVarargs {

        @Test
        @DisplayName("returns absolute paths for all files")
        void absolutePaths() {
            var file1 = new File("/tmp/a.txt");
            var file2 = new File("/tmp/b.txt");
            var result = CollectionTools.combineFilesToStrings(file1, file2);
            assertEquals(List.of(file1.getAbsolutePath(), file2.getAbsolutePath()), result);
        }

        @Test
        @DisplayName("returns empty list when all elements are null")
        void allNullElementsReturnEmpty() {
            var result = CollectionTools.combineFilesToStrings((File) null, null);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("returns empty list when called with no arguments")
        void noArgumentsReturnEmpty() {
            var result = CollectionTools.combineFilesToStrings(new File[0]);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("returns empty list when called with null array")
        void nullArrayReturnsEmpty() {
            var result = CollectionTools.combineFilesToStrings((File[]) null);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("skips null elements")
        void nullElementsSkipped() {
            var file1 = new File("/tmp/a.txt");
            var file2 = new File("/tmp/b.txt");
            var result = CollectionTools.combineFilesToStrings(file1, null, file2);
            assertEquals(List.of(file1.getAbsolutePath(), file2.getAbsolutePath()), result);
        }

        @Test
        @DisplayName("resolves relative paths to absolute using working directory")
        void relativePathBecomesAbsolute() {
            var file = new File("relative/path.txt");
            var result = CollectionTools.combineFilesToStrings(file);
            assertTrue(new File(result.get(0)).isAbsolute());
        }

        @Test
        @DisplayName("works with a single file")
        void singleFile() {
            var file = new File("/tmp/a.txt");
            var result = CollectionTools.combineFilesToStrings(file);
            assertEquals(List.of(file.getAbsolutePath()), result);
        }
    }

    @Nested
    @DisplayName("combinePathsToFiles()")
    class CombinePathsToFiles {

        @Test
        @DisplayName("returns empty list for empty collections")
        void emptyCollectionsReturnEmpty() {
            var result = CollectionTools.combinePathsToFiles(List.of());
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("merges multiple Path collections in order")
        void multipleCollections() {
            var result = CollectionTools.combinePathsToFiles(
                    List.of(Path.of("/tmp/a.txt")), List.of(Path.of("/tmp/b.txt"), Path.of("/tmp/c.txt")));
            assertEquals(List.of(
                    new File("/tmp/a.txt"),
                    new File("/tmp/b.txt"),
                    new File("/tmp/c.txt")), result);
        }

        @Test
        @DisplayName("returns empty list when called with null array")
        void nullArrayReturnsEmpty() {
            var result = CollectionTools.combinePathsToFiles((Collection<Path>[]) null);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("skips null collections")
        void nullCollectionSkipped() {
            var result = CollectionTools.combinePathsToFiles(
                    List.of(Path.of("/tmp/a.txt")), null);
            assertEquals(List.of(new File("/tmp/a.txt")), result);
        }

        @Test
        @DisplayName("skips null elements within a collection")
        void nullElementsSkipped() {
            var list = new ArrayList<Path>();
            list.add(Path.of("/tmp/a.txt"));
            list.add(null);
            list.add(Path.of("/tmp/b.txt"));
            var result = CollectionTools.combinePathsToFiles(list);
            assertEquals(2, result.size());
        }

        @Test
        @DisplayName("returns File objects for each path")
        void pathsToFiles() {
            var path1 = Path.of("/tmp/a.txt");
            var path2 = Path.of("/tmp/b.txt");
            var result = CollectionTools.combinePathsToFiles(List.of(path1, path2));
            assertEquals(List.of(new File("/tmp/a.txt"), new File("/tmp/b.txt")), result);
        }

        @Test
        @DisplayName("round-trips with combineFilesToStrings")
        void roundTrip() {
            var paths = List.of(Path.of("/tmp/a.txt"), Path.of("/tmp/b.txt"));
            var files = CollectionTools.combinePathsToFiles(paths);
            var strings = CollectionTools.combineFilesToStrings(files);
            assertEquals(
                    paths.stream().map(p -> p.toFile().getAbsolutePath()).toList(),
                    strings);
        }
    }

    @Nested
    @DisplayName("combinePathsToFiles() varargs")
    class CombinePathsToFilesVarargs {

        @Test
        @DisplayName("returns empty list when all elements are null")
        void allNullElementsReturnEmpty() {
            var result = CollectionTools.combinePathsToFiles((Path) null, null);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("returns empty list when called with no arguments")
        void noArgumentsReturnEmpty() {
            var result = CollectionTools.combinePathsToFiles(new Path[0]);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("returns empty list when called with null array")
        void nullArrayReturnsEmpty() {
            var result = CollectionTools.combinePathsToFiles((Path[]) null);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("skips null elements")
        void nullElementsSkipped() {
            var path1 = Path.of("/tmp/a.txt");
            var path2 = Path.of("/tmp/b.txt");
            var result = CollectionTools.combinePathsToFiles(path1, null, path2);
            assertEquals(List.of(new File("/tmp/a.txt"), new File("/tmp/b.txt")), result);
        }

        @Test
        @DisplayName("returns File objects for each path")
        void pathsToFiles() {
            var path1 = Path.of("/tmp/a.txt");
            var path2 = Path.of("/tmp/b.txt");
            var result = CollectionTools.combinePathsToFiles(path1, path2);
            assertEquals(List.of(new File("/tmp/a.txt"), new File("/tmp/b.txt")), result);
        }

        @Test
        @DisplayName("round-trips with combineFilesToStrings varargs")
        void roundTrip() {
            var path1 = Path.of("/tmp/a.txt");
            var path2 = Path.of("/tmp/b.txt");
            var files = CollectionTools.combinePathsToFiles(path1, path2);
            var strings = CollectionTools.combineFilesToStrings(files.toArray(new File[0]));
            assertEquals(
                    List.of(path1.toFile().getAbsolutePath(), path2.toFile().getAbsolutePath()),
                    strings);
        }

        @Test
        @DisplayName("works with a single path")
        void singlePath() {
            var path = Path.of("/tmp/a.txt");
            var result = CollectionTools.combinePathsToFiles(path);
            assertEquals(List.of(new File("/tmp/a.txt")), result);
        }
    }

    @Nested
    @DisplayName("combinePathsToStrings()")
    class CombinePathsToStrings {

        @Test
        @DisplayName("returns empty list for empty collections")
        void emptyCollectionsReturnEmpty() {
            var result = CollectionTools.combinePathsToStrings(List.of());
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("merges multiple Path collections in order")
        void multipleCollections() {
            var result = CollectionTools.combinePathsToStrings(
                    List.of(Path.of("/a")), List.of(Path.of("/b"), Path.of("/c")));
            assertEquals(List.of("/a", "/b", "/c"), result);
        }

        @Test
        @DisplayName("returns empty list when called with null array")
        void nullArrayReturnsEmpty() {
            var result = CollectionTools.combinePathsToStrings((Collection<Path>[]) null);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("skips null collections")
        void nullCollectionSkipped() {
            var result = CollectionTools.combinePathsToStrings(
                    List.of(Path.of("/a")), null);
            assertEquals(List.of("/a"), result);
        }

        @Test
        @DisplayName("skips null elements within a collection")
        void nullElementsSkipped() {
            var list = new ArrayList<Path>();
            list.add(Path.of("/a"));
            list.add(null);
            list.add(Path.of("/b"));
            var result = CollectionTools.combinePathsToStrings(list);
            assertEquals(List.of("/a", "/b"), result);
        }

        @Test
        @DisplayName("returns string representation of each path")
        void pathToString() {
            var path1 = Path.of("/tmp/a.txt");
            var path2 = Path.of("/tmp/b.txt");
            var result = CollectionTools.combinePathsToStrings(List.of(path1, path2));
            assertEquals(List.of("/tmp/a.txt", "/tmp/b.txt"), result);
        }

        @Test
        @DisplayName("preserves relative path strings without resolving them")
        void relativePathPreserved() {
            var result = CollectionTools.combinePathsToStrings(
                    List.of(Path.of("relative/path.txt")));
            assertEquals("relative/path.txt", result.get(0));
        }
    }

    @Nested
    @DisplayName("combinePathsToStrings() varargs")
    class CombinePathsToStringsVarargs {

        @Test
        @DisplayName("returns empty list when all elements are null")
        void allNullElementsReturnEmpty() {
            var result = CollectionTools.combinePathsToStrings((Path) null, null);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("returns empty list when called with no arguments")
        void noArgumentsReturnEmpty() {
            var result = CollectionTools.combinePathsToStrings(new Path[0]);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("returns empty list when called with null array")
        void nullArrayReturnsEmpty() {
            var result = CollectionTools.combinePathsToStrings((Path[]) null);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("skips null elements")
        void nullElementsSkipped() {
            var path1 = Path.of("/a");
            var path2 = Path.of("/b");
            var result = CollectionTools.combinePathsToStrings(path1, null, path2);
            assertEquals(List.of("/a", "/b"), result);
        }

        @Test
        @DisplayName("returns string representation of each path")
        void pathToString() {
            var path1 = Path.of("/tmp/a.txt");
            var path2 = Path.of("/tmp/b.txt");
            var result = CollectionTools.combinePathsToStrings(path1, path2);
            assertEquals(List.of("/tmp/a.txt", "/tmp/b.txt"), result);
        }

        @Test
        @DisplayName("preserves relative path strings without resolving them")
        void relativePathPreserved() {
            var path = Path.of("relative/path.txt");
            var result = CollectionTools.combinePathsToStrings(path);
            assertEquals("relative/path.txt", result.get(0));
        }

        @Test
        @DisplayName("works with a single path")
        void singlePath() {
            var path = Path.of("/tmp/a.txt");
            var result = CollectionTools.combinePathsToStrings(path);
            assertEquals(List.of("/tmp/a.txt"), result);
        }
    }

    @Nested
    @DisplayName("combineStringsToFiles()")
    class CombineStringsToFiles {

        @Test
        @DisplayName("returns empty list for empty collections")
        void emptyCollectionsReturnEmpty() {
            var result = CollectionTools.combineStringsToFiles(List.of());
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("merges multiple string collections in order")
        void multipleCollections() {
            var result = CollectionTools.combineStringsToFiles(
                    List.of("/tmp/a.txt"), List.of("/tmp/b.txt", "/tmp/c.txt"));
            assertEquals(List.of(
                    new File("/tmp/a.txt"),
                    new File("/tmp/b.txt"),
                    new File("/tmp/c.txt")), result);
        }

        @Test
        @DisplayName("returns empty list when called with null array")
        void nullArrayReturnsEmpty() {
            var result = CollectionTools.combineStringsToFiles((Collection<String>[]) null);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("skips null collections")
        void nullCollectionSkipped() {
            var result = CollectionTools.combineStringsToFiles(
                    List.of("/tmp/a.txt"), null);
            assertEquals(List.of(new File("/tmp/a.txt")), result);
        }

        @Test
        @DisplayName("skips null elements within a collection")
        void nullElementsSkipped() {
            var list = new ArrayList<String>();
            list.add("/tmp/a.txt");
            list.add(null);
            list.add("/tmp/b.txt");
            var result = CollectionTools.combineStringsToFiles(list);
            assertEquals(2, result.size());
        }

        @Test
        @DisplayName("round-trips with combineFilesToStrings")
        void roundTrip() {
            var paths = List.of("/tmp/a.txt", "/tmp/b.txt");
            var files = CollectionTools.combineStringsToFiles(paths);
            var strings = CollectionTools.combineFilesToStrings(files);
            assertEquals(
                    paths.stream().map(s -> new File(s).getAbsolutePath()).toList(),
                    strings);
        }

        @Test
        @DisplayName("returns File objects for each string")
        void stringsToFiles() {
            var result = CollectionTools.combineStringsToFiles(
                    List.of("/tmp/a.txt", "/tmp/b.txt"));
            assertEquals(List.of(new File("/tmp/a.txt"), new File("/tmp/b.txt")), result);
        }
    }

    @Nested
    @DisplayName("combineStringsToFiles() varargs")
    class CombineStringsToFilesVarargs {

        @Test
        @DisplayName("returns empty list when all elements are null")
        void allNullElementsReturnEmpty() {
            var result = CollectionTools.combineStringsToFiles((String) null, null);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("returns empty list when called with no arguments")
        void noArgumentsReturnEmpty() {
            var result = CollectionTools.combineStringsToFiles(new String[0]);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("returns empty list when called with null array")
        void nullArrayReturnsEmpty() {
            var result = CollectionTools.combineStringsToFiles((String[]) null);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("skips null elements")
        void nullElementsSkipped() {
            var result = CollectionTools.combineStringsToFiles("/tmp/a.txt", null, "/tmp/b.txt");
            assertEquals(List.of(new File("/tmp/a.txt"), new File("/tmp/b.txt")), result);
        }

        @Test
        @DisplayName("round-trips with combineFilesToStrings varargs")
        void roundTrip() {
            var file1 = new File("/tmp/a.txt");
            var file2 = new File("/tmp/b.txt");
            var files = CollectionTools.combineStringsToFiles(
                    file1.getAbsolutePath(), file2.getAbsolutePath());
            var strings = CollectionTools.combineFilesToStrings(files.toArray(new File[0]));
            assertEquals(List.of(file1.getAbsolutePath(), file2.getAbsolutePath()), strings);
        }

        @Test
        @DisplayName("works with a single string")
        void singleString() {
            var result = CollectionTools.combineStringsToFiles("/tmp/a.txt");
            assertEquals(List.of(new File("/tmp/a.txt")), result);
        }

        @Test
        @DisplayName("returns File objects for each string")
        void stringsToFiles() {
            var result = CollectionTools.combineStringsToFiles("/tmp/a.txt", "/tmp/b.txt");
            assertEquals(List.of(new File("/tmp/a.txt"), new File("/tmp/b.txt")), result);
        }
    }

    @Nested
    @DisplayName("combineStringsToPaths()")
    class CombineStringsToPaths {

        @Test
        @DisplayName("returns empty list for empty collections")
        void emptyCollectionsReturnEmpty() {
            var result = CollectionTools.combineStringsToPaths(List.of());
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("merges multiple string collections in order")
        void multipleCollections() {
            var result = CollectionTools.combineStringsToPaths(
                    List.of("/tmp/a.txt"), List.of("/tmp/b.txt", "/tmp/c.txt"));
            assertEquals(List.of(
                    Path.of("/tmp/a.txt"),
                    Path.of("/tmp/b.txt"),
                    Path.of("/tmp/c.txt")), result);
        }

        @Test
        @DisplayName("returns empty list when called with null array")
        void nullArrayReturnsEmpty() {
            var result = CollectionTools.combineStringsToPaths((Collection<String>[]) null);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("skips null collections")
        void nullCollectionSkipped() {
            var result = CollectionTools.combineStringsToPaths(
                    List.of("/tmp/a.txt"), null);
            assertEquals(List.of(Path.of("/tmp/a.txt")), result);
        }

        @Test
        @DisplayName("skips null elements within a collection")
        void nullElementsSkipped() {
            var list = new ArrayList<String>();
            list.add("/tmp/a.txt");
            list.add(null);
            list.add("/tmp/b.txt");
            var result = CollectionTools.combineStringsToPaths(list);
            assertEquals(2, result.size());
        }

        @Test
        @DisplayName("preserves relative path strings without resolving them")
        void relativePathPreserved() {
            var result = CollectionTools.combineStringsToPaths(
                    List.of("relative/path.txt"));
            assertEquals(Path.of("relative/path.txt"), result.get(0));
        }

        @Test
        @DisplayName("round-trips with combinePathsToStrings")
        void roundTrip() {
            var strings = List.of("/tmp/a.txt", "/tmp/b.txt");
            var paths = CollectionTools.combineStringsToPaths(strings);
            var result = CollectionTools.combinePathsToStrings(paths);
            assertEquals(strings, result);
        }

        @Test
        @DisplayName("returns Path objects for each string")
        void stringsToPaths() {
            var result = CollectionTools.combineStringsToPaths(
                    List.of("/tmp/a.txt", "/tmp/b.txt"));
            assertEquals(List.of(Path.of("/tmp/a.txt"), Path.of("/tmp/b.txt")), result);
        }
    }

    @Nested
    @DisplayName("combineStringsToPaths() varargs")
    class CombineStringsToPathsVarargs {

        @Test
        @DisplayName("returns empty list when all elements are null")
        void allNullElementsReturnEmpty() {
            var result = CollectionTools.combineStringsToPaths((String) null, null);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("returns empty list when called with no arguments")
        void noArgumentsReturnEmpty() {
            var result = CollectionTools.combineStringsToPaths(new String[0]);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("returns empty list when called with null array")
        void nullArrayReturnsEmpty() {
            var result = CollectionTools.combineStringsToPaths((String[]) null);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("skips null elements")
        void nullElementsSkipped() {
            var result = CollectionTools.combineStringsToPaths("/tmp/a.txt", null, "/tmp/b.txt");
            assertEquals(List.of(Path.of("/tmp/a.txt"), Path.of("/tmp/b.txt")), result);
        }

        @Test
        @DisplayName("preserves relative path strings without resolving them")
        void relativePathPreserved() {
            var result = CollectionTools.combineStringsToPaths("relative/path.txt");
            assertEquals(Path.of("relative/path.txt"), result.get(0));
        }

        @Test
        @DisplayName("round-trips with combinePathsToStrings varargs")
        void roundTrip() {
            var paths = CollectionTools.combineStringsToPaths("/tmp/a.txt", "/tmp/b.txt");
            var result = CollectionTools.combinePathsToStrings(paths.toArray(new Path[0]));
            assertEquals(List.of("/tmp/a.txt", "/tmp/b.txt"), result);
        }

        @Test
        @DisplayName("works with a single string")
        void singleString() {
            var result = CollectionTools.combineStringsToPaths("/tmp/a.txt");
            assertEquals(List.of(Path.of("/tmp/a.txt")), result);
        }

        @Test
        @DisplayName("returns Path objects for each string")
        void stringsToPaths() {
            var result = CollectionTools.combineStringsToPaths("/tmp/a.txt", "/tmp/b.txt");
            assertEquals(List.of(Path.of("/tmp/a.txt"), Path.of("/tmp/b.txt")), result);
        }
    }

    @Nested
    @DisplayName("combine() varargs")
    class CombineVarargs {

        @Test
        @DisplayName("returns empty list when all elements are null")
        void allNullElementsReturnEmpty() {
            var result = CollectionTools.combine((String) null, null);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("returns empty list when called with no arguments")
        void noArgumentsReturnEmpty() {
            var result = CollectionTools.combine(new String[0]);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("returns empty list when called with null array")
        void nullArrayReturnsEmpty() {
            var result = CollectionTools.combine((String[]) null);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("skips null elements")
        void nullElementsSkipped() {
            var result = CollectionTools.combine("a", null, "b");
            assertEquals(List.of("a", "b"), result);
        }

        @Test
        @DisplayName("returns unmodifiable list")
        void returnsUnmodifiableList() {
            var result = CollectionTools.combine("a");
            assertThrows(UnsupportedOperationException.class, () -> result.add("b"));
        }

        @Test
        @DisplayName("works with a single element")
        void singleElement() {
            var result = CollectionTools.combine("only");
            assertEquals(List.of("only"), result);
        }

        @Test
        @DisplayName("returns all elements from a single vararg call")
        void singleVararg() {
            var result = CollectionTools.combine("a", "b", "c");
            assertEquals(List.of("a", "b", "c"), result);
        }
    }
}