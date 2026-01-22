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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Objects Utils Tests")
@SuppressWarnings({"PMD.UseVarargs", "ConstantValue"})
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