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

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Objects Tools Tests")
@SuppressWarnings({"ConstantValue", "PMD.AvoidDuplicateLiterals", "PMD.UseVarargs"})
class ObjectToolsTest {

    @Nested
    @DisplayName("Array Tests")
    class ArrayTests {

        @Nested
        @DisplayName("Edge Cases and Special Scenarios")
        class EdgeCasesTests {

            @Test
            @DisplayName("should handle array containing only null elements as non-empty")
            void shouldHandleArrayContainingOnlyNullElements() {
                var array = new String[]{null, null, null};
                assertTrue(ObjectTools.isNotEmpty(array));
                assertFalse(ObjectTools.isEmpty(array));
            }

            @Test
            @DisplayName("should handle arrays of different types")
            void shouldHandleArraysOfDifferentTypes() {
                var stringArray = new String[]{"test"};
                var integerArray = new Integer[]{1, 2};
                var objectArray = new Object[]{new Object()};

                assertTrue(ObjectTools.isNotEmpty(stringArray));
                assertTrue(ObjectTools.isNotEmpty(integerArray));
                assertTrue(ObjectTools.isNotEmpty(objectArray));
            }

            @Test
            @DisplayName("should handle large arrays efficiently")
            void shouldHandleLargeArraysEfficiently() {
                var largeArray = new Object[10000];
                for (int i = 0; i < largeArray.length; i++) {
                    largeArray[i] = i;
                }

                assertTrue(ObjectTools.isNotEmpty(largeArray));
                assertFalse(ObjectTools.isEmpty(largeArray));
            }

            @Test
            @DisplayName("should handle varargs with single element")
            void shouldHandleVarargsWithSingleElement() {
                boolean result1 = ObjectTools.isEmpty(new Object[]{"element"});
                boolean result2 = ObjectTools.isNotEmpty(new Object[]{"element"});

                assertFalse(result1);
                assertTrue(result2);
            }
        }

        @Nested
        @DisplayName("isEmpty(T[]...) Tests")
        class IsEmptyMultipleArraysTests {

            static Stream<Arguments> arraysWithAtLeastOneNonEmpty() {
                return Stream.of(
                        Arguments.of((Object) new String[][]{new String[]{"element"}}),
                        Arguments.of((Object) new Object[][]{new Object[]{}, new String[]{"a"}}),
                        Arguments.of((Object) new Object[][]{null, new String[]{"b"}, new Object[]{}}),
                        Arguments.of((Object) new Integer[][]{new Integer[]{1}, new Integer[]{}, null}),
                        Arguments.of((Object) new String[][]{new String[]{"a"}, new String[]{"b"}})
                );
            }

            @ParameterizedTest
            @MethodSource("arraysWithAtLeastOneNonEmpty")
            @DisplayName("should return false when at least one array is not empty")
            void shouldReturnFalseWhenAtLeastOneArrayIsNotEmpty(Object[][] arrays) {
                boolean result = ObjectTools.isEmpty(arrays);
                assertFalse(result);
            }

            @Test
            @DisplayName("should return true for mix of null and empty arrays")
            void shouldReturnTrueForMixOfNullAndEmptyArrays() {
                boolean result = ObjectTools.isEmpty(null, new Object[]{}, null, new String[]{});
                assertTrue(result);
            }

            @Test
            @DisplayName("should return true for null varargs")
            void shouldReturnTrueForNullVarargs() {
                boolean result = ObjectTools.isEmpty((Object[][]) null);
                assertTrue(result);
            }

            @Test
            @DisplayName("should return true when all arrays are empty")
            void shouldReturnTrueWhenAllArraysAreEmpty() {
                boolean result = ObjectTools.isEmpty(new Object[]{}, new String[]{}, new Integer[]{});
                assertTrue(result);
            }

            @Test
            @DisplayName("should return true when all arrays are null")
            void shouldReturnTrueWhenAllArraysAreNull() {
                boolean result = ObjectTools.isEmpty((Object[]) null, null, null);
                assertTrue(result);
            }
        }

        @Nested
        @DisplayName("isEmpty(T[]) Tests")
        class IsEmptySingleArrayTests {

            static Stream<Arguments> nonEmptyArrays() {
                return Stream.of(
                        Arguments.of((Object) new Object[]{"element"}),
                        Arguments.of((Object) new String[]{"a", "b", "c"}),
                        Arguments.of((Object) new Integer[]{1, 2, 3}),
                        Arguments.of((Object) new Object[]{null}),
                        Arguments.of((Object) new Object[]{null, null}),
                        Arguments.of((Object) new Object[]{1, null, "test"})
                );
            }

            @ParameterizedTest
            @MethodSource("nonEmptyArrays")
            @DisplayName("should return false for non-empty arrays")
            void shouldReturnFalseForNonEmptyArrays(Object[] array) {
                boolean result = ObjectTools.isEmpty(array);
                assertFalse(result);
            }

            @Test
            @DisplayName("should return true for empty array")
            void shouldReturnTrueForEmptyArray() {
                var array = new Object[]{};
                boolean result = ObjectTools.isEmpty(array);
                assertTrue(result);
            }

            @Test
            @DisplayName("should return true for null array")
            void shouldReturnTrueForNullArray() {
                boolean result = ObjectTools.isEmpty((Object[]) null);
                assertTrue(result);
            }
        }

        @Nested
        @DisplayName("isNotEmpty(T[]...) Tests")
        class IsNotEmptyMultipleArraysTests {

            static Stream<Arguments> arraysWithAtLeastOneNonEmpty() {
                return Stream.of(
                        Arguments.of((Object) new String[][]{new String[]{"element"}}),
                        Arguments.of((Object) new Object[][]{new Object[]{}, new String[]{"a"}}),
                        Arguments.of((Object) new Object[][]{null, new String[]{"b"}, new Object[]{}}),
                        Arguments.of((Object) new Integer[][]{new Integer[]{1}, new Integer[]{}, null}),
                        Arguments.of((Object) new String[][]{new String[]{"a"}, new String[]{"b"}})
                );
            }

            @Test
            @DisplayName("should return false for mix of null and empty arrays")
            void shouldReturnFalseForMixOfNullAndEmptyArrays() {
                boolean result = ObjectTools.isNotEmpty(null, new Object[]{}, null, new String[]{});
                assertFalse(result);
            }

            @Test
            @DisplayName("should return false for null varargs")
            void shouldReturnFalseForNullVarargs() {
                boolean result = ObjectTools.isNotEmpty((Object[][]) null);
                assertFalse(result);
            }

            @Test
            @DisplayName("should return false when all arrays are empty")
            void shouldReturnFalseWhenAllArraysAreEmpty() {
                boolean result = ObjectTools.isNotEmpty(new Object[]{}, new String[]{}, new Integer[]{});
                assertFalse(result);
            }

            @Test
            @DisplayName("should return false when all arrays are null")
            void shouldReturnFalseWhenAllArraysAreNull() {
                boolean result = ObjectTools.isNotEmpty((Object[]) null, null, null);
                assertFalse(result);
            }

            @ParameterizedTest
            @MethodSource("arraysWithAtLeastOneNonEmpty")
            @DisplayName("should return true when at least one array is not empty")
            void shouldReturnTrueWhenAtLeastOneArrayIsNotEmpty(Object[][] arrays) {
                boolean result = ObjectTools.isNotEmpty(arrays);
                assertTrue(result);
            }
        }

        @Nested
        @DisplayName("isNotEmpty(T[]) Tests")
        class IsNotEmptySingleArrayTests {

            static Stream<Arguments> nonEmptyArrays() {
                return Stream.of(
                        Arguments.of((Object) new Object[]{"element"}),
                        Arguments.of((Object) new String[]{"a", "b", "c"}),
                        Arguments.of((Object) new Integer[]{1, 2, 3}),
                        Arguments.of((Object) new Object[]{null}),
                        Arguments.of((Object) new Object[]{null, null}),
                        Arguments.of((Object) new Object[]{1, null, "test"})
                );
            }

            @Test
            @DisplayName("should return false for empty array")
            void shouldReturnFalseForEmptyArray() {
                var array = new Object[]{};
                boolean result = ObjectTools.isNotEmpty(array);
                assertFalse(result);
            }

            @Test
            @DisplayName("should return false for null array")
            void shouldReturnFalseForNullArray() {
                boolean result = ObjectTools.isNotEmpty((Object[]) null);
                assertFalse(result);
            }

            @ParameterizedTest
            @MethodSource("nonEmptyArrays")
            @DisplayName("should return true for non-empty arrays")
            void shouldReturnTrueForNonEmptyArrays(Object[] array) {
                boolean result = ObjectTools.isNotEmpty(array);
                assertTrue(result);
            }
        }
    }

    @Nested
    @DisplayName("Collection Tests")
    class CollectionTests {

        @Nested
        @DisplayName("Edge Cases and Special Scenarios")
        class EdgeCasesTests {

            @Test
            @DisplayName("should handle collection containing only null elements as non-empty")
            void shouldHandleCollectionContainingOnlyNullElements() {
                Collection<String> collection = Arrays.asList(null, null, null);
                assertTrue(ObjectTools.isNotEmpty(collection));
                assertFalse(ObjectTools.isEmpty(collection));
            }

            @Test
            @DisplayName("should handle collections of different types")
            void shouldHandleCollectionsOfDifferentTypes() {
                var arrayList = new ArrayList<>(List.of("test"));
                var hashSet = new HashSet<>(Set.of(1, 2));
                var linkedList = new LinkedList<>(List.of(new Object()));

                assertTrue(ObjectTools.isNotEmpty(arrayList));
                assertTrue(ObjectTools.isNotEmpty(hashSet));
                assertTrue(ObjectTools.isNotEmpty(linkedList));
            }

            @Test
            @DisplayName("should handle large collections efficiently")
            void shouldHandleLargeCollectionsEfficiently() {
                List<Integer> largeCollection = new ArrayList<>();
                for (int i = 0; i < 10000; i++) {
                    largeCollection.add(i);
                }

                assertTrue(ObjectTools.isNotEmpty(largeCollection));
                assertFalse(ObjectTools.isEmpty(largeCollection));
            }

            @Test
            @DisplayName("should handle single element collection")
            void shouldHandleSingleElementCollection() {
                boolean result1 = ObjectTools.isEmpty(List.of("element"));
                boolean result2 = ObjectTools.isNotEmpty(List.of("element"));

                assertFalse(result1);
                assertTrue(result2);
            }
        }

        @Nested
        @DisplayName("isEmpty(T...) Tests where T extends Collection<?>")
        class IsEmptyMultipleCollectionsTests {

            static Stream<Arguments> collectionsWithAtLeastOneNonEmpty() {
                return Stream.of(
                        Arguments.of((Object) new Collection<?>[]{List.of("element")}),
                        Arguments.of((Object) new Collection<?>[]{new ArrayList<>(), List.of("a")}),
                        Arguments.of((Object) new Collection<?>[]{null, List.of("b"), new HashSet<>()}),
                        Arguments.of((Object) new Collection<?>[]{Set.of(1), new ArrayList<>(), null}),
                        Arguments.of((Object) new Collection<?>[]{List.of("a"), Set.of("b")})
                );
            }

            @ParameterizedTest
            @MethodSource("collectionsWithAtLeastOneNonEmpty")
            @DisplayName("should return false when at least one collection is not empty")
            void shouldReturnFalseWhenAtLeastOneCollectionIsNotEmpty(Collection<?>[] collections) {
                boolean result = ObjectTools.isEmpty(collections);
                assertFalse(result);
            }

            @Test
            @DisplayName("should return true for mix of null and empty collections")
            void shouldReturnTrueForMixOfNullAndEmptyCollections() {
                boolean result = ObjectTools.isEmpty(null, new ArrayList<>(), null, new HashSet<>());
                assertTrue(result);
            }

            @Test
            @DisplayName("should return true for null varargs")
            void shouldReturnTrueForNullVarargs() {
                boolean result = ObjectTools.isEmpty((Collection<?>[]) null);
                assertTrue(result);
            }

            @Test
            @DisplayName("should return true when all collections are empty")
            void shouldReturnTrueWhenAllCollectionsAreEmpty() {
                boolean result = ObjectTools.isEmpty(new ArrayList<>(), new HashSet<>(), new LinkedList<>());
                assertTrue(result);
            }

            @Test
            @DisplayName("should return true when all collections are null")
            void shouldReturnTrueWhenAllCollectionsAreNull() {
                boolean result = ObjectTools.isEmpty((Collection<?>) null, null, null);
                assertTrue(result);
            }
        }

        @Nested
        @DisplayName("isEmpty(Collection<?>) Tests")
        class IsEmptySingleCollectionTests {

            static Stream<Arguments> nonEmptyCollections() {
                return Stream.of(
                        Arguments.of(List.of("element")),
                        Arguments.of(List.of("a", "b", "c")),
                        Arguments.of(Set.of(1, 2, 3)),
                        Arguments.of(Collections.singletonList(null)),
                        Arguments.of(Arrays.asList(null, null)),
                        Arguments.of(Arrays.asList(1, null, "test"))
                );
            }

            @ParameterizedTest
            @MethodSource("nonEmptyCollections")
            @DisplayName("should return false for non-empty collections")
            void shouldReturnFalseForNonEmptyCollections(Collection<?> collection) {
                boolean result = ObjectTools.isEmpty(collection);
                assertFalse(result);
            }

            @Test
            @DisplayName("should return true for empty collection")
            @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
            void shouldReturnTrueForEmptyCollection() {
                Collection<?> collection = new ArrayList<>();
                boolean result = ObjectTools.isEmpty(collection);
                assertTrue(result);
            }

            @Test
            @DisplayName("should return true for null collection")
            void shouldReturnTrueForNullCollection() {
                boolean result = ObjectTools.isEmpty((Collection<?>) null);
                assertTrue(result);
            }
        }

        @Nested
        @DisplayName("isNotEmpty(T...) Tests where T extends Collection<?>")
        class IsNotEmptyMultipleCollectionsTests {

            static Stream<Arguments> collectionsWithAtLeastOneNonEmpty() {
                return Stream.of(
                        Arguments.of((Object) new Collection<?>[]{List.of("element")}),
                        Arguments.of((Object) new Collection<?>[]{new ArrayList<>(), List.of("a")}),
                        Arguments.of((Object) new Collection<?>[]{null, List.of("b"), new HashSet<>()}),
                        Arguments.of((Object) new Collection<?>[]{Set.of(1), new ArrayList<>(), null}),
                        Arguments.of((Object) new Collection<?>[]{List.of("a"), Set.of("b")})
                );
            }

            @Test
            @DisplayName("should return false for mix of null and empty collections")
            void shouldReturnFalseForMixOfNullAndEmptyCollections() {
                boolean result = ObjectTools.isNotEmpty(null, new ArrayList<>(), null, new HashSet<>());
                assertFalse(result);
            }

            @Test
            @DisplayName("should return false for null varargs")
            void shouldReturnFalseForNullVarargs() {
                boolean result = ObjectTools.isNotEmpty((Collection<?>[]) null);
                assertFalse(result);
            }

            @Test
            @DisplayName("should return false when all collections are empty")
            void shouldReturnFalseWhenAllCollectionsAreEmpty() {
                boolean result = ObjectTools.isNotEmpty(new ArrayList<>(), new HashSet<>(), new LinkedList<>());
                assertFalse(result);
            }

            @Test
            @DisplayName("should return false when all collections are null")
            void shouldReturnFalseWhenAllCollectionsAreNull() {
                boolean result = ObjectTools.isNotEmpty((Collection<?>) null, null, null);
                assertFalse(result);
            }

            @ParameterizedTest
            @MethodSource("collectionsWithAtLeastOneNonEmpty")
            @DisplayName("should return true when at least one collection is not empty")
            void shouldReturnTrueWhenAtLeastOneCollectionIsNotEmpty(Collection<?>[] collections) {
                boolean result = ObjectTools.isNotEmpty(collections);
                assertTrue(result);
            }
        }

        @Nested
        @DisplayName("isNotEmpty(Collection<?>) Tests")
        class IsNotEmptySingleCollectionTests {

            static Stream<Arguments> nonEmptyCollections() {
                return Stream.of(
                        Arguments.of(List.of("element")),
                        Arguments.of(List.of("a", "b", "c")),
                        Arguments.of(Set.of(1, 2, 3)),
                        Arguments.of(Collections.singletonList(null)),
                        Arguments.of(Arrays.asList(null, null)),
                        Arguments.of(Arrays.asList(1, null, "test"))
                );
            }

            @Test
            @DisplayName("should return false for empty collection")
            @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
            void shouldReturnFalseForEmptyCollection() {
                Collection<?> collection = new ArrayList<>();
                boolean result = ObjectTools.isNotEmpty(collection);
                assertFalse(result);
            }

            @Test
            @DisplayName("should return false for null collection")
            void shouldReturnFalseForNullCollection() {
                boolean result = ObjectTools.isNotEmpty((Collection<?>) null);
                assertFalse(result);
            }

            @ParameterizedTest
            @MethodSource("nonEmptyCollections")
            @DisplayName("should return true for non-empty collections")
            void shouldReturnTrueForNonEmptyCollections(Collection<?> collection) {
                boolean result = ObjectTools.isNotEmpty(collection);
                assertTrue(result);
            }
        }
    }

    @Nested
    @DisplayName("Map Tests")
    class MapTests {

        @Nested
        @DisplayName("Edge Cases and Special Scenarios")
        class EdgeCasesTests {

            @Test
            @DisplayName("should handle different Map implementations")
            @SuppressWarnings("PMD.ReplaceHashtableWithMap")
            void shouldHandleDifferentMapImplementations() {
                var hashMap = new HashMap<>(Map.of("key", "value"));
                var treeMap = new TreeMap<>(Map.of("key", "value"));
                var linkedHashMap = new LinkedHashMap<>(Map.of("key", "value"));
                var concurrentHashMap = new ConcurrentHashMap<>(Map.of("key", "value"));
                var hashtable = new Hashtable<>(Map.of("key", "value"));

                assertTrue(ObjectTools.isNotEmpty(hashMap));
                assertTrue(ObjectTools.isNotEmpty(treeMap));
                assertTrue(ObjectTools.isNotEmpty(linkedHashMap));
                assertTrue(ObjectTools.isNotEmpty(concurrentHashMap));
                assertTrue(ObjectTools.isNotEmpty(hashtable));
                assertFalse(ObjectTools.isEmpty(hashMap));
                assertFalse(ObjectTools.isEmpty(treeMap));
                assertFalse(ObjectTools.isEmpty(linkedHashMap));
                assertFalse(ObjectTools.isEmpty(concurrentHashMap));
                assertFalse(ObjectTools.isEmpty(hashtable));
            }

            @Test
            @DisplayName("should handle hashtables with multiple entries")
            @SuppressWarnings("PMD.ReplaceHashtableWithMap")
            void shouldHandleHashtablesWithMultipleEntries() {
                var table = new Hashtable<String, Integer>();
                table.put("one", 1);
                table.put("two", 2);
                table.put("three", 3);

                assertTrue(ObjectTools.isNotEmpty(table));
                assertFalse(ObjectTools.isEmpty(table));
            }

            @Test
            @DisplayName("should handle large hashtables efficiently")
            @SuppressWarnings("PMD.ReplaceHashtableWithMap")
            void shouldHandleLargeHashtablesEfficiently() {
                var largeTable = new Hashtable<Integer, String>();
                for (int i = 0; i < 10000; i++) {
                    largeTable.put(i, "value" + i);
                }

                assertTrue(ObjectTools.isNotEmpty(largeTable));
                assertFalse(ObjectTools.isEmpty(largeTable));
            }

            @Test
            @DisplayName("should handle large maps efficiently")
            void shouldHandleLargeMapsEfficiently() {
                var largeMap = new HashMap<Integer, String>();
                for (int i = 0; i < 10000; i++) {
                    largeMap.put(i, "value" + i);
                }

                assertTrue(ObjectTools.isNotEmpty(largeMap));
                assertFalse(ObjectTools.isEmpty(largeMap));
            }

            @Test
            @DisplayName("should handle maps with null values as non-empty")
            void shouldHandleMapsWithNullValuesAsNonEmpty() {
                var map = new HashMap<String, String>();
                map.put("key1", null);
                map.put("key2", null);

                assertTrue(ObjectTools.isNotEmpty(map));
                assertFalse(ObjectTools.isEmpty(map));
            }
        }

        @Nested
        @DisplayName("isEmpty(Map<?, ?>) Tests")
        @SuppressWarnings("PMD.ReplaceHashtableWithMap")
        class IsEmptyMapTests {

            static Stream<Arguments> nonEmptyMaps() {
                var mapWithNull1 = new HashMap<Integer, String>();
                mapWithNull1.put(1, null);

                var mapWithNull2 = new HashMap<String, String>();
                mapWithNull2.put("key", "value");
                mapWithNull2.put("key2", null);

                var hashtable = new Hashtable<String, String>();
                hashtable.put("key1", "value1");

                return Stream.of(
                        Arguments.of(new HashMap<>(Map.of("key1", "value1"))),
                        Arguments.of(new HashMap<>(Map.of("a", 1, "b", 2, "c", 3))),
                        Arguments.of(new TreeMap<>(Map.of("x", "y"))),
                        Arguments.of(new LinkedHashMap<>(Map.of("k", "v"))),
                        Arguments.of(new ConcurrentHashMap<>(Map.of("test", "data"))),
                        Arguments.of(mapWithNull1),
                        Arguments.of(mapWithNull2),
                        Arguments.of(hashtable)
                );
            }

            @ParameterizedTest
            @MethodSource("nonEmptyMaps")
            @DisplayName("should return false for non-empty maps")
            void shouldReturnFalseForNonEmptyMaps(Map<?, ?> map) {
                boolean result = ObjectTools.isEmpty(map);
                assertFalse(result);
            }

            @Test
            @DisplayName("should return true for empty map")
            @SuppressWarnings({"MismatchedQueryAndUpdateOfCollection", "PMD.UseConcurrentHashMap"})
            void shouldReturnTrueForEmptyMap() {
                Map<?, ?> map = new HashMap<>();
                boolean result = ObjectTools.isEmpty(map);
                assertTrue(result);
            }

            @Test
            @DisplayName("should return true for null map")
            void shouldReturnTrueForNullMap() {
                boolean result = ObjectTools.isEmpty((Map<?, ?>) null);
                assertTrue(result);
            }
        }

        @Nested
        @DisplayName("isNotEmpty(Map<?, ?>) Tests")
        @SuppressWarnings("PMD.ReplaceHashtableWithMap")
        class IsNotEmptyMapTests {

            static Stream<Arguments> nonEmptyMaps() {
                var mapWithNull1 = new HashMap<Integer, String>();
                mapWithNull1.put(1, null);

                var mapWithNull2 = new HashMap<String, String>();
                mapWithNull2.put("key", "value");
                mapWithNull2.put("key2", null);

                var hashtable = new Hashtable<String, String>();
                hashtable.put("key1", "value1");

                return Stream.of(

                        Arguments.of(new HashMap<>(Map.of("key1", "value1"))),
                        Arguments.of(new HashMap<>(Map.of("a", 1, "b", 2, "c", 3))),
                        Arguments.of(new TreeMap<>(Map.of("x", "y"))),
                        Arguments.of(new LinkedHashMap<>(Map.of("k", "v"))),
                        Arguments.of(new ConcurrentHashMap<>(Map.of("test", "data"))),
                        Arguments.of(mapWithNull1),
                        Arguments.of(mapWithNull2),
                        Arguments.of(hashtable)
                );
            }

            @Test
            @DisplayName("should return false for empty map")
            @SuppressWarnings({"MismatchedQueryAndUpdateOfCollection", "PMD.UseConcurrentHashMap"})
            void shouldReturnFalseForEmptyMap() {
                Map<?, ?> map = new HashMap<>();
                boolean result = ObjectTools.isNotEmpty(map);
                assertFalse(result);
            }

            @Test
            @DisplayName("should return false for null map")
            void shouldReturnFalseForNullMap() {
                boolean result = ObjectTools.isNotEmpty((Map<?, ?>) null);
                assertFalse(result);
            }

            @ParameterizedTest
            @MethodSource("nonEmptyMaps")
            @DisplayName("should return true for non-empty maps")
            void shouldReturnTrueForNonEmptyMaps(Map<?, ?> map) {
                boolean result = ObjectTools.isNotEmpty(map);
                assertTrue(result);
            }
        }
    }

    @Nested
    @DisplayName("Null Tests")
    class NullTests {

        @Nested
        @DisplayName("isAnyNull(Collection<?>) tests")
        class IsAnyNullCollectionTests {

            static Stream<Arguments> provideNonNullCollections() {
                return Stream.of(
                        Arguments.of(List.of("test")),
                        Arguments.of(List.of(1, 2, 3)),
                        Arguments.of(List.of("a", "b", "c"))
                );
            }

            static Stream<Arguments> provideNullCollections() {
                return Stream.of(
                        Arguments.of(Collections.singletonList(null)),
                        Arguments.of(Arrays.asList(null, null)),
                        Arguments.of(Arrays.asList(null, "test")),
                        Arguments.of(Arrays.asList("test", null)),
                        Arguments.of(Arrays.asList(null, "test", null))
                );
            }

            @ParameterizedTest
            @MethodSource("provideNonNullCollections")
            @DisplayName("should return false when all elements are not null")
            void shouldReturnFalseWhenAllElementsAreNotNull(Collection<?> collection) {
                assertFalse(ObjectTools.isAnyNull(collection));
            }

            @Test
            @DisplayName("should return false when collection is empty")
            void shouldReturnFalseWhenCollectionIsEmpty() {
                assertFalse(ObjectTools.isAnyNull(Collections.emptyList()));
            }

            @ParameterizedTest
            @MethodSource("provideNullCollections")
            @DisplayName("should return true when any element is null")
            void shouldReturnTrueWhenAnyElementIsNull(Collection<?> collection) {
                assertTrue(ObjectTools.isAnyNull(collection));
            }

            @Test
            @DisplayName("should return true when collection is null")
            void shouldReturnTrueWhenCollectionIsNull() {
                assertTrue(ObjectTools.isAnyNull((Collection<?>) null));
            }
        }

        @Nested
        @DisplayName("isAnyNull(T...) tests")
        class IsAnyNullVarargsTests {

            static Stream<Arguments> provideNonNullCases() {
                return Stream.of(
                        Arguments.arguments((Object) new Object[]{"test"}),
                        Arguments.arguments((Object) new Object[]{1, 2, 3}),
                        Arguments.arguments((Object) new Object[]{"a", "b", "c"})
                );
            }

            static Stream<Arguments> provideNullCases() {
                return Stream.of(
                        Arguments.arguments((Object) new Object[]{null}),
                        Arguments.arguments((Object) new Object[]{null, null}),
                        Arguments.arguments((Object) new Object[]{null, "test"}),
                        Arguments.arguments((Object) new Object[]{"test", null}),
                        Arguments.arguments((Object) new Object[]{null, "test", null})
                );
            }

            @ParameterizedTest
            @MethodSource("provideNonNullCases")
            @DisplayName("should return false when all objects are not null")
            void shouldReturnFalseWhenAllObjectsAreNotNull(Object[] objects) {
                assertFalse(ObjectTools.isAnyNull(objects));
            }

            @Test
            @DisplayName("should return false when empty array is provided")
            void shouldReturnFalseWhenEmptyArrayIsProvided() {
                assertFalse(ObjectTools.isAnyNull());
            }

            @ParameterizedTest
            @MethodSource("provideNullCases")
            @DisplayName("should return true when any object is null")
            void shouldReturnTrueWhenAnyObjectIsNull(Object[] objects) {
                assertTrue(ObjectTools.isAnyNull(objects));
            }

            @Test
            @DisplayName("should return true when varargs parameter is null")
            void shouldReturnTrueWhenVarargsParameterIsNull() {
                assertTrue(ObjectTools.isAnyNull((Object[]) null));
            }
        }

        @Nested
        @DisplayName("isNotNull(Collection<?>) tests")
        class IsNotNullCollectionTests {

            static Stream<Arguments> provideNonNullCollections() {
                return Stream.of(
                        Arguments.of(List.of("test")),
                        Arguments.of(List.of(1, 2, 3)),
                        Arguments.of(List.of("a", "b", "c"))
                );
            }

            static Stream<Arguments> provideNullCollections() {
                return Stream.of(
                        Arguments.of(Collections.singletonList(null)),
                        Arguments.of(Arrays.asList(null, null)),
                        Arguments.of(Arrays.asList(null, "test")),
                        Arguments.of(Arrays.asList("test", null)),
                        Arguments.of(Arrays.asList(null, "test", null))
                );
            }

            @ParameterizedTest
            @MethodSource("provideNullCollections")
            @DisplayName("should return false when any element is null")
            void shouldReturnFalseWhenAnyElementIsNull(Collection<?> collection) {
                assertFalse(ObjectTools.isNotNull(collection));
            }

            @Test
            @DisplayName("should return false when collection is null")
            void shouldReturnFalseWhenCollectionIsNull() {
                assertFalse(ObjectTools.isNotNull((Collection<?>) null));
            }

            @ParameterizedTest
            @MethodSource("provideNonNullCollections")
            @DisplayName("should return true when all elements are not null")
            void shouldReturnTrueWhenAllElementsAreNotNull(Collection<?> collection) {
                assertTrue(ObjectTools.isNotNull(collection));
            }

            @Test
            @DisplayName("should return true when collection is empty")
            void shouldReturnTrueWhenCollectionIsEmpty() {
                assertTrue(ObjectTools.isNotNull(Collections.emptyList()));
            }
        }

        @Nested
        @DisplayName("isNotNull(T...) tests")
        class IsNotNullVarargsTests {

            static Stream<Arguments> provideNonNullCases() {
                return Stream.of(
                        Arguments.arguments((Object) new Object[]{"test"}),
                        Arguments.arguments((Object) new Object[]{1, 2, 3}),
                        Arguments.arguments((Object) new Object[]{"a", "b", "c"})
                );
            }

            static Stream<Arguments> provideNullCases() {
                return Stream.of(
                        Arguments.arguments((Object) new Object[]{null}),
                        Arguments.arguments((Object) new Object[]{null, null}),
                        Arguments.arguments((Object) new Object[]{null, "test"}),
                        Arguments.arguments((Object) new Object[]{"test", null}),
                        Arguments.arguments((Object) new Object[]{null, "test", null})
                );
            }

            @ParameterizedTest
            @MethodSource("provideNullCases")
            @DisplayName("should return false when any object is null")
            void shouldReturnFalseWhenAnyObjectIsNull(Object[] objects) {
                assertFalse(ObjectTools.isNotNull(objects));
            }

            @Test
            @DisplayName("should return false when varargs parameter is null")
            void shouldReturnFalseWhenVarargsParameterIsNull() {
                assertFalse(ObjectTools.isNotNull((Object[]) null));
            }

            @ParameterizedTest
            @MethodSource("provideNonNullCases")
            @DisplayName("should return true when all objects are not null")
            void shouldReturnTrueWhenAllObjectsAreNotNull(Object[] objects) {
                assertTrue(ObjectTools.isNotNull(objects));
            }

            @Test
            @DisplayName("should return true when empty array is provided")
            void shouldReturnTrueWhenEmptyArrayIsProvided() {
                assertTrue(ObjectTools.isNotNull());
            }
        }

        @Nested
        @DisplayName("isNull(Collection<?>) tests")
        class IsNullCollectionTests {

            static Stream<Arguments> provideNonNullCollections() {
                return Stream.of(
                        Arguments.of(List.of("test")),
                        Arguments.of(List.of(1, 2, 3)),
                        Arguments.of(Arrays.asList(null, "test")),
                        Arguments.of(Arrays.asList("test", null)),
                        Arguments.of(Arrays.asList(null, "test", null))
                );
            }

            static Stream<Arguments> provideNullCollections() {
                return Stream.of(
                        Arguments.of(Collections.singletonList(null)),
                        Arguments.of(Arrays.asList(null, null)),
                        Arguments.of(Arrays.asList(null, null, null))
                );
            }

            @ParameterizedTest
            @MethodSource("provideNonNullCollections")
            @DisplayName("should return false when any element is not null")
            void shouldReturnFalseWhenAnyElementIsNotNull(Collection<?> collection) {
                assertFalse(ObjectTools.isNull(collection));
            }

            @ParameterizedTest
            @MethodSource("provideNullCollections")
            @DisplayName("should return true when all elements are null")
            void shouldReturnTrueWhenAllElementsAreNull(Collection<?> collection) {
                assertTrue(ObjectTools.isNull(collection));
            }

            @Test
            @DisplayName("should return true when collection is empty")
            void shouldReturnTrueWhenCollectionIsEmpty() {
                assertTrue(ObjectTools.isNull(Collections.emptyList()));
            }

            @Test
            @DisplayName("should return true when collection is null")
            void shouldReturnTrueWhenCollectionIsNull() {
                assertTrue(ObjectTools.isNull((Collection<?>) null));
            }
        }

        @Nested
        @DisplayName("isNull(T...) tests")
        class IsNullVarargsTests {

            static Stream<Arguments> provideNonNullCases() {
                return Stream.of(
                        Arguments.arguments((Object) new Object[]{"test"}),
                        Arguments.arguments((Object) new Object[]{1, 2, 3}),
                        Arguments.arguments((Object) new Object[]{null, "test"}),
                        Arguments.arguments((Object) new Object[]{"test", null}),
                        Arguments.arguments((Object) new Object[]{null, "test", null})
                );
            }

            static Stream<Arguments> provideNullCases() {
                return Stream.of(
                        Arguments.arguments((Object) new Object[]{null}),
                        Arguments.arguments((Object) new Object[]{null, null}),
                        Arguments.arguments((Object) new Object[]{null, null, null})
                );
            }

            @ParameterizedTest
            @MethodSource("provideNonNullCases")
            @DisplayName("should return false when any object is not null")
            void shouldReturnFalseWhenAnyObjectIsNotNull(Object[] objects) {
                assertFalse(ObjectTools.isNull(objects));
            }

            @ParameterizedTest
            @MethodSource("provideNullCases")
            @DisplayName("should return true when all objects are null")
            void shouldReturnTrueWhenAllObjectsAreNull(Object[] objects) {
                assertTrue(ObjectTools.isNull(objects));
            }

            @Test
            @DisplayName("should return true when empty array is provided")
            void shouldReturnTrueWhenEmptyArrayIsProvided() {
                assertTrue(ObjectTools.isNull());
            }

            @Test
            @DisplayName("should return true when varargs parameter is null")
            void shouldReturnTrueWhenVarargsParameterIsNull() {
                assertTrue(ObjectTools.isNull((Object[]) null));
            }
        }
    }
}