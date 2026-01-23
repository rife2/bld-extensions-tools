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

@DisplayName("Objects Utils Tests")
@SuppressWarnings({"PMD.UseVarargs", "ConstantValue", "PMD.AvoidDuplicateLiterals",
        "PMD.LooseCoupling", "PMD.ReplaceHashtableWithMap"})
class ObjectsUtilsTest {

    @Nested
    @DisplayName("Array Tests")
    @SuppressWarnings({"ConstantValue", "PMD.AvoidDuplicateLiterals"})
    class ArrayTests {

        @Nested
        @DisplayName("Edge Cases and Special Scenarios")
        class EdgeCasesTests {

            @Test
            @DisplayName("should handle array containing only null elements as non-empty")
            void shouldHandleArrayContainingOnlyNullElements() {
                var array = new Object[]{null, null, null};
                assertTrue(ObjectsUtils.isNotEmpty(array));
                assertFalse(ObjectsUtils.isEmpty(array));
            }

            @Test
            @DisplayName("should handle arrays of different types")
            void shouldHandleArraysOfDifferentTypes() {
                var stringArray = new String[]{"test"};
                var integerArray = new Integer[]{1, 2};
                var objectArray = new Object[]{new Object()};

                assertTrue(ObjectsUtils.isNotEmpty(stringArray));
                assertTrue(ObjectsUtils.isNotEmpty(integerArray));
                assertTrue(ObjectsUtils.isNotEmpty(objectArray));
            }

            @Test
            @DisplayName("should handle large arrays efficiently")
            void shouldHandleLargeArraysEfficiently() {
                var largeArray = new Object[10000];
                for (int i = 0; i < largeArray.length; i++) {
                    largeArray[i] = i;
                }

                assertTrue(ObjectsUtils.isNotEmpty(largeArray));
                assertFalse(ObjectsUtils.isEmpty(largeArray));
            }

            @Test
            @DisplayName("should handle varargs with single element")
            void shouldHandleVarargsWithSingleElement() {
                var result1 = ObjectsUtils.isEmpty(new Object[]{"element"});
                var result2 = ObjectsUtils.isNotEmpty(new Object[]{"element"});

                assertFalse(result1);
                assertTrue(result2);
            }
        }

        @Nested
        @DisplayName("isEmpty(Object[]...) Tests")
        class IsEmptyMultipleArraysTests {

            static Stream<Arguments> arraysWithAtLeastOneNonEmpty() {
                return Stream.of(
                        Arguments.of((Object) new Object[][]{new Object[]{"element"}}),
                        Arguments.of((Object) new Object[][]{new Object[]{}, new String[]{"a"}}),
                        Arguments.of((Object) new Object[][]{null, new String[]{"b"}, new Object[]{}}),
                        Arguments.of((Object) new Object[][]{new Integer[]{1}, new Object[]{}, null}),
                        Arguments.of((Object) new Object[][]{new Object[]{"a"}, new Object[]{"b"}})
                );
            }

            @ParameterizedTest
            @MethodSource("arraysWithAtLeastOneNonEmpty")
            @DisplayName("should return false when at least one array is not empty")
            @SuppressWarnings("PMD.UseVarargs")
            void shouldReturnFalseWhenAtLeastOneArrayIsNotEmpty(Object[][] arrays) {
                var result = ObjectsUtils.isEmpty(arrays);
                assertFalse(result);
            }

            @Test
            @DisplayName("should return true for mix of null and empty arrays")
            void shouldReturnTrueForMixOfNullAndEmptyArrays() {
                var result = ObjectsUtils.isEmpty(null, new Object[]{}, null, new String[]{});
                assertTrue(result);
            }

            @Test
            @DisplayName("should return true for null varargs")
            void shouldReturnTrueForNullVarargs() {
                var result = ObjectsUtils.isEmpty((Object[][]) null);
                assertTrue(result);
            }

            @Test
            @DisplayName("should return true when all arrays are empty")
            void shouldReturnTrueWhenAllArraysAreEmpty() {
                var result = ObjectsUtils.isEmpty(new Object[]{}, new String[]{}, new Integer[]{});
                assertTrue(result);
            }

            @Test
            @DisplayName("should return true when all arrays are null")
            void shouldReturnTrueWhenAllArraysAreNull() {
                var result = ObjectsUtils.isEmpty(null, null, null);
                assertTrue(result);
            }
        }

        @Nested
        @DisplayName("isEmpty(Object[]) Tests")
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
            @SuppressWarnings("PMD.UseVarargs")
            void shouldReturnFalseForNonEmptyArrays(Object[] array) {
                var result = ObjectsUtils.isEmpty(array);
                assertFalse(result);
            }

            @Test
            @DisplayName("should return true for empty array")
            void shouldReturnTrueForEmptyArray() {
                var array = new Object[]{};
                var result = ObjectsUtils.isEmpty(array);
                assertTrue(result);
            }

            @Test
            @DisplayName("should return true for null array")
            void shouldReturnTrueForNullArray() {
                var result = ObjectsUtils.isEmpty((Object[]) null);
                assertTrue(result);
            }
        }

        @Nested
        @DisplayName("isNotEmpty(Object[]...) Tests")
        class IsNotEmptyMultipleArraysTests {

            static Stream<Arguments> arraysWithAtLeastOneNonEmpty() {
                return Stream.of(
                        Arguments.of((Object) new Object[][]{new Object[]{"element"}}),
                        Arguments.of((Object) new Object[][]{new Object[]{}, new String[]{"a"}}),
                        Arguments.of((Object) new Object[][]{null, new String[]{"b"}, new Object[]{}}),
                        Arguments.of((Object) new Object[][]{new Integer[]{1}, new Object[]{}, null}),
                        Arguments.of((Object) new Object[][]{new Object[]{"a"}, new Object[]{"b"}})
                );
            }

            @Test
            @DisplayName("should return false for mix of null and empty arrays")
            void shouldReturnFalseForMixOfNullAndEmptyArrays() {
                var result = ObjectsUtils.isNotEmpty(null, new Object[]{}, null, new String[]{});
                assertFalse(result);
            }

            @Test
            @DisplayName("should return false for null varargs")
            void shouldReturnFalseForNullVarargs() {
                var result = ObjectsUtils.isNotEmpty((Object[][]) null);
                assertFalse(result);
            }

            @Test
            @DisplayName("should return false when all arrays are empty")
            void shouldReturnFalseWhenAllArraysAreEmpty() {
                var result = ObjectsUtils.isNotEmpty(new Object[]{}, new String[]{}, new Integer[]{});
                assertFalse(result);
            }

            @Test
            @DisplayName("should return false when all arrays are null")
            void shouldReturnFalseWhenAllArraysAreNull() {
                var result = ObjectsUtils.isNotEmpty(null, null, null);
                assertFalse(result);
            }

            @ParameterizedTest
            @MethodSource("arraysWithAtLeastOneNonEmpty")
            @DisplayName("should return true when at least one array is not empty")
            @SuppressWarnings("PMD.UseVarargs")
            void shouldReturnTrueWhenAtLeastOneArrayIsNotEmpty(Object[][] arrays) {
                var result = ObjectsUtils.isNotEmpty(arrays);
                assertTrue(result);
            }
        }

        @Nested
        @DisplayName("isNotEmpty(Object[]) Tests")
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
                var result = ObjectsUtils.isNotEmpty(array);
                assertFalse(result);
            }

            @Test
            @DisplayName("should return false for null array")
            void shouldReturnFalseForNullArray() {
                var result = ObjectsUtils.isNotEmpty((Object[]) null);
                assertFalse(result);
            }

            @ParameterizedTest
            @MethodSource("nonEmptyArrays")
            @SuppressWarnings("PMD.UseVarargs")
            @DisplayName("should return true for non-empty arrays")
            void shouldReturnTrueForNonEmptyArrays(Object[] array) {
                var result = ObjectsUtils.isNotEmpty(array);
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
            void shouldHandleDifferentMapImplementations() {
                var hashMap = new HashMap<>(Map.of("key", "value"));
                var treeMap = new TreeMap<>(Map.of("key", "value"));
                var linkedHashMap = new LinkedHashMap<>(Map.of("key", "value"));
                var concurrentHashMap = new ConcurrentHashMap<>(Map.of("key", "value"));

                assertTrue(ObjectsUtils.isNotEmpty(hashMap));
                assertTrue(ObjectsUtils.isNotEmpty(treeMap));
                assertTrue(ObjectsUtils.isNotEmpty(linkedHashMap));
                assertTrue(ObjectsUtils.isNotEmpty(concurrentHashMap));
                assertFalse(ObjectsUtils.isEmpty(hashMap));
                assertFalse(ObjectsUtils.isEmpty(treeMap));
                assertFalse(ObjectsUtils.isEmpty(linkedHashMap));
                assertFalse(ObjectsUtils.isEmpty(concurrentHashMap));
            }

            @Test
            @DisplayName("should handle hashtables with multiple entries")
            void shouldHandleHashtablesWithMultipleEntries() {
                var table = new Hashtable<String, Integer>();
                table.put("one", 1);
                table.put("two", 2);
                table.put("three", 3);

                assertTrue(ObjectsUtils.isNotEmpty(table));
                assertFalse(ObjectsUtils.isEmpty(table));
            }

            @Test
            @DisplayName("should handle large hashtables efficiently")
            void shouldHandleLargeHashtablesEfficiently() {
                var largeTable = new Hashtable<Integer, String>();
                for (int i = 0; i < 10000; i++) {
                    largeTable.put(i, "value" + i);
                }

                assertTrue(ObjectsUtils.isNotEmpty(largeTable));
                assertFalse(ObjectsUtils.isEmpty(largeTable));
            }

            @Test
            @DisplayName("should handle large maps efficiently")
            void shouldHandleLargeMapsEfficiently() {
                var largeMap = new HashMap<Integer, String>();
                for (int i = 0; i < 10000; i++) {
                    largeMap.put(i, "value" + i);
                }

                assertTrue(ObjectsUtils.isNotEmpty(largeMap));
                assertFalse(ObjectsUtils.isEmpty(largeMap));
            }

            @Test
            @DisplayName("should handle maps with null values as non-empty")
            void shouldHandleMapsWithNullValuesAsNonEmpty() {
                var map = new HashMap<String, String>();
                map.put("key1", null);
                map.put("key2", null);

                assertTrue(ObjectsUtils.isNotEmpty(map));
                assertFalse(ObjectsUtils.isEmpty(map));
            }
        }

        @Nested
        @DisplayName("isEmpty(Map<?, ?>) Tests")
        class IsEmptyMapTests {

            static Stream<Arguments> nonEmptyMaps() {
                var mapWithNull1 = new HashMap<Integer, String>();
                mapWithNull1.put(1, null);

                var mapWithNull2 = new HashMap<String, String>();
                mapWithNull2.put("key", "value");
                mapWithNull2.put("key2", null);

                return Stream.of(
                        Arguments.of(new HashMap<>(Map.of("key1", "value1"))),
                        Arguments.of(new HashMap<>(Map.of("a", 1, "b", 2, "c", 3))),
                        Arguments.of(new TreeMap<>(Map.of("x", "y"))),
                        Arguments.of(new LinkedHashMap<>(Map.of("k", "v"))),
                        Arguments.of(new ConcurrentHashMap<>(Map.of("test", "data"))),
                        Arguments.of(mapWithNull1),
                        Arguments.of(mapWithNull2)
                );
            }

            @ParameterizedTest
            @MethodSource("nonEmptyMaps")
            @DisplayName("should return false for non-empty maps")
            void shouldReturnFalseForNonEmptyMaps(Map<?, ?> map) {
                var result = ObjectsUtils.isEmpty(map);
                assertFalse(result);
            }

            @Test
            @DisplayName("should return true for empty map")
            @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
            void shouldReturnTrueForEmptyMap() {
                var map = new HashMap<>();
                var result = ObjectsUtils.isEmpty(map);
                assertTrue(result);
            }

            @Test
            @DisplayName("should return true for null map")
            void shouldReturnTrueForNullMap() {
                var result = ObjectsUtils.isEmpty((Map<?, ?>) null);
                assertTrue(result);
            }
        }

        @Nested
        @DisplayName("isEmpty(Hashtable<?, ?>) Tests")
        class IsEmptyHashtableTests {

            static Stream<Arguments> nonEmptyHashtables() {
                var ht1 = new Hashtable<String, String>();
                ht1.put("key1", "value1");

                var ht2 = new Hashtable<String, Integer>();
                ht2.put("a", 1);
                ht2.put("b", 2);
                ht2.put("c", 3);

                var ht3 = new Hashtable<Integer, String>();
                ht3.put(1, "one");

                return Stream.of(
                        Arguments.of(ht1),
                        Arguments.of(ht2),
                        Arguments.of(ht3)
                );
            }

            @ParameterizedTest
            @MethodSource("nonEmptyHashtables")
            @DisplayName("should return false for non-empty hashtables")
            void shouldReturnFalseForNonEmptyHashtables(Hashtable<?, ?> table) {
                var result = ObjectsUtils.isEmpty(table);
                assertFalse(result);
            }

            @Test
            @DisplayName("should return true for empty hashtable")
            @SuppressWarnings({"MismatchedQueryAndUpdateOfCollection"})
            void shouldReturnTrueForEmptyHashtable() {
                var table = new Hashtable<>();
                var result = ObjectsUtils.isEmpty(table);
                assertTrue(result);
            }

            @Test
            @DisplayName("should return true for null hashtable")
            void shouldReturnTrueForNullHashtable() {
                var result = ObjectsUtils.isEmpty((Hashtable<?, ?>) null);
                assertTrue(result);
            }
        }

        @Nested
        @DisplayName("isNotEmpty(Map<?, ?>) Tests")
        class IsNotEmptyMapTests {

            static Stream<Arguments> nonEmptyMaps() {
                var mapWithNull1 = new HashMap<Integer, String>();
                mapWithNull1.put(1, null);

                var mapWithNull2 = new HashMap<String, String>();
                mapWithNull2.put("key", "value");
                mapWithNull2.put("key2", null);

                return Stream.of(
                        Arguments.of(new HashMap<>(Map.of("key1", "value1"))),
                        Arguments.of(new HashMap<>(Map.of("a", 1, "b", 2, "c", 3))),
                        Arguments.of(new TreeMap<>(Map.of("x", "y"))),
                        Arguments.of(new LinkedHashMap<>(Map.of("k", "v"))),
                        Arguments.of(new ConcurrentHashMap<>(Map.of("test", "data"))),
                        Arguments.of(mapWithNull1),
                        Arguments.of(mapWithNull2)
                );
            }

            @Test
            @DisplayName("should return false for empty map")
            @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
            void shouldReturnFalseForEmptyMap() {
                var map = new HashMap<>();
                var result = ObjectsUtils.isNotEmpty(map);
                assertFalse(result);
            }

            @Test
            @DisplayName("should return false for null map")
            void shouldReturnFalseForNullMap() {
                var result = ObjectsUtils.isNotEmpty((Map<?, ?>) null);
                assertFalse(result);
            }

            @ParameterizedTest
            @MethodSource("nonEmptyMaps")
            @DisplayName("should return true for non-empty maps")
            void shouldReturnTrueForNonEmptyMaps(Map<?, ?> map) {
                var result = ObjectsUtils.isNotEmpty(map);
                assertTrue(result);
            }
        }

        @Nested
        @DisplayName("isNotEmpty(Hashtable<?, ?>) Tests")
        class IsNotEmptyHashtableTests {

            static Stream<Arguments> nonEmptyHashtables() {
                var ht1 = new Hashtable<String, String>();
                ht1.put("key1", "value1");

                var ht2 = new Hashtable<String, Integer>();
                ht2.put("a", 1);
                ht2.put("b", 2);
                ht2.put("c", 3);

                var ht3 = new Hashtable<Integer, String>();
                ht3.put(1, "one");

                return Stream.of(
                        Arguments.of(ht1),
                        Arguments.of(ht2),
                        Arguments.of(ht3)
                );
            }

            @Test
            @DisplayName("should return false for empty hashtable")
            @SuppressWarnings({"MismatchedQueryAndUpdateOfCollection"})
            void shouldReturnFalseForEmptyHashtable() {
                var table = new Hashtable<>();
                var result = ObjectsUtils.isNotEmpty(table);
                assertFalse(result);
            }

            @Test
            @DisplayName("should return false for null hashtable")
            void shouldReturnFalseForNullHashtable() {
                var result = ObjectsUtils.isNotEmpty((Hashtable<?, ?>) null);
                assertFalse(result);
            }

            @ParameterizedTest
            @MethodSource("nonEmptyHashtables")
            @DisplayName("should return true for non-empty hashtables")
            void shouldReturnTrueForNonEmptyHashtables(Hashtable<?, ?> table) {
                var result = ObjectsUtils.isNotEmpty(table);
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
                assertFalse(ObjectsUtils.isAnyNull(collection));
            }

            @Test
            @DisplayName("should return false when collection is empty")
            void shouldReturnFalseWhenCollectionIsEmpty() {
                assertFalse(ObjectsUtils.isAnyNull(Collections.emptyList()));
            }

            @ParameterizedTest
            @MethodSource("provideNullCollections")
            @DisplayName("should return true when any element is null")
            void shouldReturnTrueWhenAnyElementIsNull(Collection<?> collection) {
                assertTrue(ObjectsUtils.isAnyNull(collection));
            }

            @Test
            @DisplayName("should return true when collection is null")
            void shouldReturnTrueWhenCollectionIsNull() {
                assertTrue(ObjectsUtils.isAnyNull((Collection<?>) null));
            }
        }

        @Nested
        @DisplayName("isAnyNull(Object...) tests")
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
                assertFalse(ObjectsUtils.isAnyNull(objects));
            }

            @Test
            @DisplayName("should return false when empty array is provided")
            void shouldReturnFalseWhenEmptyArrayIsProvided() {
                assertFalse(ObjectsUtils.isAnyNull());
            }

            @ParameterizedTest
            @MethodSource("provideNullCases")
            @DisplayName("should return true when any object is null")
            void shouldReturnTrueWhenAnyObjectIsNull(Object[] objects) {
                assertTrue(ObjectsUtils.isAnyNull(objects));
            }

            @Test
            @DisplayName("should return true when varargs parameter is null")
            void shouldReturnTrueWhenVarargsParameterIsNull() {
                assertTrue(ObjectsUtils.isAnyNull((Object[]) null));
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
                assertFalse(ObjectsUtils.isNotNull(collection));
            }

            @Test
            @DisplayName("should return false when collection is null")
            void shouldReturnFalseWhenCollectionIsNull() {
                assertFalse(ObjectsUtils.isNotNull((Collection<?>) null));
            }

            @ParameterizedTest
            @MethodSource("provideNonNullCollections")
            @DisplayName("should return true when all elements are not null")
            void shouldReturnTrueWhenAllElementsAreNotNull(Collection<?> collection) {
                assertTrue(ObjectsUtils.isNotNull(collection));
            }

            @Test
            @DisplayName("should return true when collection is empty")
            void shouldReturnTrueWhenCollectionIsEmpty() {
                assertTrue(ObjectsUtils.isNotNull(Collections.emptyList()));
            }
        }

        @Nested
        @DisplayName("isNotNull(Object...) tests")
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
                assertFalse(ObjectsUtils.isNotNull(objects));
            }

            @Test
            @DisplayName("should return false when varargs parameter is null")
            void shouldReturnFalseWhenVarargsParameterIsNull() {
                assertFalse(ObjectsUtils.isNotNull((Object[]) null));
            }

            @ParameterizedTest
            @MethodSource("provideNonNullCases")
            @DisplayName("should return true when all objects are not null")
            void shouldReturnTrueWhenAllObjectsAreNotNull(Object[] objects) {
                assertTrue(ObjectsUtils.isNotNull(objects));
            }

            @Test
            @DisplayName("should return true when empty array is provided")
            void shouldReturnTrueWhenEmptyArrayIsProvided() {
                assertTrue(ObjectsUtils.isNotNull());
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
                assertFalse(ObjectsUtils.isNull(collection));
            }

            @ParameterizedTest
            @MethodSource("provideNullCollections")
            @DisplayName("should return true when all elements are null")
            void shouldReturnTrueWhenAllElementsAreNull(Collection<?> collection) {
                assertTrue(ObjectsUtils.isNull(collection));
            }

            @Test
            @DisplayName("should return true when collection is empty")
            void shouldReturnTrueWhenCollectionIsEmpty() {
                assertTrue(ObjectsUtils.isNull(Collections.emptyList()));
            }

            @Test
            @DisplayName("should return true when collection is null")
            void shouldReturnTrueWhenCollectionIsNull() {
                assertTrue(ObjectsUtils.isNull((Collection<?>) null));
            }
        }

        @Nested
        @DisplayName("isNull(Object...) tests")
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
                assertFalse(ObjectsUtils.isNull(objects));
            }

            @ParameterizedTest
            @MethodSource("provideNullCases")
            @DisplayName("should return true when all objects are null")
            void shouldReturnTrueWhenAllObjectsAreNull(Object[] objects) {
                assertTrue(ObjectsUtils.isNull(objects));
            }

            @Test
            @DisplayName("should return true when empty array is provided")
            void shouldReturnTrueWhenEmptyArrayIsProvided() {
                assertTrue(ObjectsUtils.isNull());
            }

            @Test
            @DisplayName("should return true when varargs parameter is null")
            void shouldReturnTrueWhenVarargsParameterIsNull() {
                assertTrue(ObjectsUtils.isNull((Object[]) null));
            }
        }
    }
}