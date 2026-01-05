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
@SuppressWarnings({"PMD.AvoidDuplicateLiterals", "PMD.UseVarargs", "ConstantValue"})
class ObjectsUtilsTest {

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