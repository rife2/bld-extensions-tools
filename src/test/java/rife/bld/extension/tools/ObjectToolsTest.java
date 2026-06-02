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

import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ObjectToolsTest {

    /**
     * Containers / values where every element (and the container itself) is empty and non-null.
     */
    static Stream<Arguments> allEmptyContainers() {
        return Stream.of(
                Arguments.of((Object) new Object[]{"", List.of(), Map.of(), new Object[]{}, new int[]{}}),
                Arguments.of(List.of("", List.of(), Map.of())),
                Arguments.of(Collections.singletonMap("", "")),
                Arguments.of((Object) new String[]{""}),
                Arguments.of(Map.of()),
                Arguments.of(List.of()),
                Arguments.of("")
        );
    }

    /**
     * Containers where every element is empty but at least one element is {@code null}.
     */
    static Stream<Arguments> allEmptyContainersWithNullElements() {
        return Stream.of(
                Arguments.of((Object) new Object[]{null, null}),
                Arguments.of((Object) new Object[]{null, ""}),
                Arguments.of(Collections.singletonList(null))
        );
    }

    /**
     * Containers where every element is non-empty.
     */
    static Stream<Arguments> allNotEmptyContainers() {
        return Stream.of(
                Arguments.of((Object) new Object[]{"x", "y"}),
                Arguments.of(List.of("x", List.of("y"), Map.of("k", "v"))),
                Arguments.of(Map.of("a", "x", "b", "y")),
                Arguments.of((Object) new int[]{1, 2}),
                Arguments.of((Object) new String[]{"x"})
        );
    }

    /**
     * Containers that have at least one empty element alongside non-empty ones.
     */
    static Stream<Arguments> containersWithEmptyElement() {
        return Stream.of(
                Arguments.of((Object) new Object[]{"x", ""}),
                Arguments.of(List.of("x", "")),
                Arguments.of(Map.of("a", "x", "b", "")),
                Arguments.of((Object) new Object[]{List.of()}),
                Arguments.of((Object) new String[]{"", "x"})
        );
    }

    /**
     * Containers that have at least one non-empty element alongside empty ones.
     */
    static Stream<Arguments> containersWithNonEmptyElement() {
        return Stream.of(
                Arguments.of((Object) new Object[]{"", "x"}),
                Arguments.of(List.of("", "x")),
                Arguments.of(Collections.singletonMap("", "x")),
                Arguments.of((Object) new Object[]{List.of(), "x"})
        );
    }

    /**
     * Empty containers (zero elements) and empty string.
     */
    static Stream<Arguments> emptyContainers() {
        return Stream.of(
                Arguments.of((Object) new Object[]{}),
                Arguments.of(List.of()),
                Arguments.of(Map.of()),
                Arguments.of((Object) new int[]{}),
                Arguments.of("")
        );
    }

    @Nested
    @DisplayName("allEmpty")
    class AllEmptyTest {

        @ParameterizedTest(name = "→ false : {0}")
        @MethodSource("rife.bld.extension.tools.ObjectToolsTest#containersWithNonEmptyElement")
        void falseWhenAnyNonEmpty(Object value) {
            assertFalse(ObjectTools.allEmpty(value));
        }

        @Test
        void nonEmptyStringFalse() {
            assertFalse(ObjectTools.allEmpty("x"));
        }

        @Test
        void nullIsEmpty() {
            assertTrue(ObjectTools.allEmpty(null));
        }

        @Test
        void primitiveArrays_empty() {
            assertTrue(ObjectTools.allEmpty(new int[]{}));
            assertTrue(ObjectTools.allEmpty(new long[]{}));
            assertTrue(ObjectTools.allEmpty(new double[]{}));
            assertTrue(ObjectTools.allEmpty(new float[]{}));
            assertTrue(ObjectTools.allEmpty(new boolean[]{}));
            assertTrue(ObjectTools.allEmpty(new byte[]{}));
            assertTrue(ObjectTools.allEmpty(new char[]{}));
            assertTrue(ObjectTools.allEmpty(new short[]{}));
        }

        @Test
        void primitiveArrays_nonEmpty() {
            assertFalse(ObjectTools.allEmpty(new int[]{1}));
        }

        @ParameterizedTest(name = "→ true : {0}")
        @MethodSource({
                "rife.bld.extension.tools.ObjectToolsTest#allEmptyContainers",
                "rife.bld.extension.tools.ObjectToolsTest#allEmptyContainersWithNullElements",
                "rife.bld.extension.tools.ObjectToolsTest#emptyContainers"
        })
        void trueForEmptyContent(Object value) {
            assertTrue(ObjectTools.allEmpty(value));
        }
    }

    @Nested
    @DisplayName("allNotEmpty")
    class AllNotEmptyTest {

        @Test
        void emptyStringFalse() {
            assertFalse(ObjectTools.allNotEmpty(""));
        }

        @ParameterizedTest(name = "→ false : {0}")
        @MethodSource({
                "rife.bld.extension.tools.ObjectToolsTest#containersWithEmptyElement",
                "rife.bld.extension.tools.ObjectToolsTest#emptyContainers"
        })
        void falseWhenAnyEmpty(Object value) {
            assertFalse(ObjectTools.allNotEmpty(value));
        }

        @Test
        void nonEmptyStringTrue() {
            assertTrue(ObjectTools.allNotEmpty("x"));
        }

        @Test
        void nullIsFalse() {
            assertFalse(ObjectTools.allNotEmpty(null));
        }

        @Test
        void primitiveArrays_empty() {
            assertFalse(ObjectTools.allNotEmpty(new int[0]));
        }

        @Test
        void primitiveArrays_nonEmpty() {
            assertTrue(ObjectTools.allNotEmpty(new int[]{1}));
            assertTrue(ObjectTools.allNotEmpty(new long[]{1L}));
            assertTrue(ObjectTools.allNotEmpty(new double[]{1.0}));
            assertTrue(ObjectTools.allNotEmpty(new float[]{1.0f}));
            assertTrue(ObjectTools.allNotEmpty(new boolean[]{true}));
            assertTrue(ObjectTools.allNotEmpty(new byte[]{1}));
            assertTrue(ObjectTools.allNotEmpty(new char[]{'a'}));
            assertTrue(ObjectTools.allNotEmpty(new short[]{1}));
        }

        @ParameterizedTest(name = "→ true : {0}")
        @MethodSource("rife.bld.extension.tools.ObjectToolsTest#allNotEmptyContainers")
        void trueWhenAllNonEmpty(Object value) {
            assertTrue(ObjectTools.allNotEmpty(value));
        }
    }

    @Nested
    @DisplayName("anyEmpty")
    class AnyEmptyTest {

        @ParameterizedTest(name = "→ false : {0}")
        @MethodSource("rife.bld.extension.tools.ObjectToolsTest#allNotEmptyContainers")
        void falseWhenAllNonEmpty(Object value) {
            assertFalse(ObjectTools.anyEmpty(value));
        }

        @Test
        void nullIsTrue() {
            assertTrue(ObjectTools.anyEmpty(null));
        }

        @Test
        void primitiveArrayNonEmpty_notEmpty() {
            assertFalse(ObjectTools.anyEmpty(new int[]{1}));
            assertFalse(ObjectTools.anyEmpty(new boolean[]{false}));
        }

        @ParameterizedTest(name = "→ true : {0}")
        @MethodSource({
                "rife.bld.extension.tools.ObjectToolsTest#containersWithEmptyElement",
                "rife.bld.extension.tools.ObjectToolsTest#emptyContainers"
        })
        void trueWhenAnyEmpty(Object value) {
            assertTrue(ObjectTools.anyEmpty(value));
        }
    }

    @Nested
    @DisplayName("anyNotEmpty")
    class AnyNotEmptyTest {

        @ParameterizedTest(name = "→ false : {0}")
        @MethodSource({
                "rife.bld.extension.tools.ObjectToolsTest#allEmptyContainers",
                "rife.bld.extension.tools.ObjectToolsTest#emptyContainers"
        })
        void falseWhenAllEmpty(Object value) {
            assertFalse(ObjectTools.anyNotEmpty(value));
        }

        @Test
        void nullIsFalse() {
            assertFalse(ObjectTools.anyNotEmpty(null));
        }

        @ParameterizedTest(name = "→ true : {0}")
        @MethodSource("rife.bld.extension.tools.ObjectToolsTest#containersWithNonEmptyElement")
        void trueWhenAnyNonEmpty(Object value) {
            assertTrue(ObjectTools.anyNotEmpty(value));
        }
    }

    @Nested
    @DisplayName("handlePrimitiveFastPath")
    class HandlePrimitiveFastPathTest {

        @Test
        void isEmptyPredicate_allMode_emptyArray() {
            assertTrue(ObjectTools.handlePrimitiveFastPath(ObjectTools.isEmptyPredicate, new int[0], true));
        }

        @Test
        void isEmptyPredicate_allMode_nonEmptyArray() {
            assertFalse(ObjectTools.handlePrimitiveFastPath(ObjectTools.isEmptyPredicate, new int[]{1}, true));
        }

        @Test
        void isEmptyPredicate_anyMode_emptyArray() {
            assertFalse(ObjectTools.handlePrimitiveFastPath(ObjectTools.isEmptyPredicate, new int[0], false));
        }

        @Test
        void isEmptyPredicate_anyMode_nonEmptyArray() {
            assertTrue(ObjectTools.handlePrimitiveFastPath(ObjectTools.isEmptyPredicate, new int[]{1}, false));
        }

        @Test
        void isNotEmptyPredicate_allMode_emptyArray() {
            assertFalse(ObjectTools.handlePrimitiveFastPath(ObjectTools.isNotEmptyPredicate, new int[0], true));
        }

        @Test
        void isNotEmptyPredicate_allMode_nonEmptyArray() {
            assertTrue(ObjectTools.handlePrimitiveFastPath(ObjectTools.isNotEmptyPredicate, new int[]{1}, true));
        }

        @Test
        void isNotEmptyPredicate_anyMode_emptyArray() {
            assertTrue(ObjectTools.handlePrimitiveFastPath(ObjectTools.isNotEmptyPredicate, new int[0], false));
        }

        @Test
        void isNotEmptyPredicate_anyMode_nonEmptyArray() {
            assertFalse(ObjectTools.handlePrimitiveFastPath(ObjectTools.isNotEmptyPredicate, new int[]{1}, false));
        }
    }

    @Nested
    @DisplayName("isEmpty")
    class IsEmptyTest {

        @Test
        void booleanFalseFalse() {
            assertFalse(ObjectTools.isEmpty(false));
        }

        @Test
        void emptyArrayTrue() {
            assertTrue(ObjectTools.isEmpty(new Object[]{}));
        }

        @Test
        void emptyListTrue() {
            assertTrue(ObjectTools.isEmpty(List.of()));
        }

        @Test
        void emptyMapTrue() {
            assertTrue(ObjectTools.isEmpty(Map.of()));
        }

        @Test
        void emptyStringTrue() {
            assertTrue(ObjectTools.isEmpty(""));
        }

        @Test
        void nonEmptyArrayFalse() {
            assertFalse(ObjectTools.isEmpty(new Object[]{"x"}));
        }

        @Test
        void nonEmptyListFalse() {
            assertFalse(ObjectTools.isEmpty(List.of("x")));
        }

        @Test
        void nullTrue() {
            assertTrue(ObjectTools.isEmpty(null));
        }
    }

    @Nested
    @DisplayName("isNotEmpty")
    class IsNotEmptyTest {

        @Test
        void booleanFalseTrue() {
            assertTrue(ObjectTools.isNotEmpty(false));
        }

        @Test
        void emptyArrayFalse() {
            assertFalse(ObjectTools.isNotEmpty(new Object[]{}));
        }

        @Test
        void emptyListFalse() {
            assertFalse(ObjectTools.isNotEmpty(List.of()));
        }

        @Test
        void emptyStringFalse() {
            assertFalse(ObjectTools.isNotEmpty(""));
        }

        @Test
        void nonEmptyStringTrue() {
            assertTrue(ObjectTools.isNotEmpty("x"));
        }

        @Test
        void nullFalse() {
            assertFalse(ObjectTools.isNotEmpty(null));
        }
    }

    @Nested
    @DisplayName("requireEmpty")
    class RequireEmptyTest {

        @ParameterizedTest(name = "allows {0}")
        @MethodSource({
                "rife.bld.extension.tools.ObjectToolsTest#allEmptyContainers",
                "rife.bld.extension.tools.ObjectToolsTest#emptyContainers"
        })
        void allowsEmptyContent(Object value) {
            assertSame(value, ObjectTools.requireEmpty(value, "ctx"));
        }

        @Test
        void formattedMessage() {
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireEmpty("x", "Value %s must be empty", "foo"));
            assertEquals("Value foo must be empty", ex.getMessage());
        }

        @Test
        void noFormatArgs() {
            assertEquals("must be empty",
                    assertThrows(IllegalArgumentException.class,
                            () -> ObjectTools.requireEmpty("x", "must be empty")).getMessage());
        }

        @Test
        void npeBeforeIae_nullElementInMixedContainer() {
            assertThrows(NullPointerException.class,
                    () -> ObjectTools.requireEmpty(new Object[]{null, "x"}, "ctx"));
        }

        @Test
        void throwsForArrayWithNonEmptyElement() {
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireEmpty(new Object[]{"x"}, "ctx"));
            assertEquals("ctx", ex.getMessage());
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = " ")
        void throwsForBlankMessage(String message) {
            assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireEmpty("", message));
        }

        @Test
        void throwsForNonEmptyString() {
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireEmpty("x", "ctx"));
            assertEquals("ctx", ex.getMessage());
        }

        @Test
        @SuppressWarnings("DataFlowIssue")
        void throwsNpeForNull() {
            var ex = assertThrows(NullPointerException.class,
                    () -> ObjectTools.requireEmpty(null, "ctx"));
            assertEquals("ctx", ex.getMessage());
        }

        @ParameterizedTest(name = "throws NPE for null element: {0}")
        @MethodSource("rife.bld.extension.tools.ObjectToolsTest#allEmptyContainersWithNullElements")
        void throwsNpeForNullElement(Object value) {
            assertThrows(NullPointerException.class,
                    () -> ObjectTools.requireEmpty(value, "ctx"));
        }

        @Test
        void throwsNpeMessageMatchesProvidedMessage() {
            var ex = assertThrows(NullPointerException.class,
                    () -> ObjectTools.requireEmpty(new Object[]{null}, "ctx"));
            assertEquals("ctx", ex.getMessage());
        }

        @Test
        void varArgsOverloadFormatsAndPasses() {
            var x = "";
            assertSame(x, ObjectTools.requireEmpty(x, "value '%s' must be empty", "input"));
        }

        @Test
        void varArgsOverloadFormatsAndThrows() {
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireEmpty("x", "value '%s' must be empty", "input"));
            assertEquals("value 'input' must be empty", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("requireNonNull")
    class RequireNonNullTest {

        @Test
        void passesForCollectionWithNonNullElements() {
            assertEquals(List.of("a", "b", ""), ObjectTools.requireNonNull(List.of("a", "b", ""), "list"));
        }

        @Test
        void passesForEmptyArray() {
            assertArrayEquals(new String[0], ObjectTools.requireNonNull(new String[0], "array"));
        }

        @Test
        void passesForEmptyCollection() {
            assertTrue(ObjectTools.requireNonNull(List.of(), "list").isEmpty());
        }

        @Test
        void passesForEmptyString() {
            assertEquals("", ObjectTools.requireNonNull("", "value"));
        }

        @Test
        void passesForNestedContainersNoNulls() {
            var nested = List.of(Set.of("a", ""), Set.of());
            assertEquals(2, ObjectTools.requireNonNull(nested, "nested").size());
        }

        @Test
        void passesForNonNullScalar() {
            assertEquals("test", ObjectTools.requireNonNull("test", "value"));
        }

        @Test
        @DisplayName("survives reasonable nesting depth without StackOverflowError")
        void survivesReasonableNestingDepth() {
            List<Object> nested = new ArrayList<>();
            List<Object> current = nested;
            for (int i = 0; i < 100; i++) {
                List<Object> next = new ArrayList<>();
                current.add(next);
                current = next;
            }
            current.add("leaf");
            assertDoesNotThrow(() -> ObjectTools.requireNonNull(nested, "nested"));
        }

        @Test
        void throwsIaeForBlankContext() {
            assertThrows(IllegalArgumentException.class, () -> ObjectTools.requireNonNull("x", " "));
        }

        @Test
        void throwsIaeForBlankMessage() {
            assertThrows(IllegalArgumentException.class, () -> ObjectTools.requireNonNull("x", "", "arg"));
        }

        @Test
        void throwsNpeForArrayContainingNull() {
            String[] arr = {"a", null, "c"};
            var ex = assertThrows(NullPointerException.class,
                    () -> ObjectTools.requireNonNull(arr, "array"));
            assertEquals("array must not be null", ex.getMessage());
        }

        @Test
        void throwsNpeForCollectionContainingNull() {
            List<String> list = Arrays.asList("a", null);
            var ex = assertThrows(NullPointerException.class,
                    () -> ObjectTools.requireNonNull(list, "list"));
            assertEquals("list must not be null", ex.getMessage());
        }

        @Test
        void throwsNpeForDeeplyNestedNull() {
            List<List<String>> nested = new ArrayList<>();
            nested.add(List.of("a"));
            List<String> inner = new ArrayList<>();
            inner.add("b");
            inner.add(null);
            nested.add(inner);
            assertThrows(NullPointerException.class,
                    () -> ObjectTools.requireNonNull(nested, "nested"));
        }

        @Test
        void throwsNpeForMapWithNullKey() {
            Map<String, String> map = new HashMap<>();
            map.put(null, "value");
            assertThrows(NullPointerException.class,
                    () -> ObjectTools.requireNonNull(map, "map"));
        }

        @Test
        void throwsNpeForMapWithNullValue() {
            Map<String, String> map = new HashMap<>();
            map.put("key", null);
            var ex = assertThrows(NullPointerException.class,
                    () -> ObjectTools.requireNonNull(map, "map"));
            assertEquals("map must not be null", ex.getMessage());
        }

        @Test
        @SuppressWarnings("DataFlowIssue")
        void throwsNpeForNullValue() {
            var ex = assertThrows(NullPointerException.class,
                    () -> ObjectTools.requireNonNull(null, "userList"));
            assertEquals("userList must not be null", ex.getMessage());
        }

        @Test
        @SuppressWarnings("DataFlowIssue")
        void throwsNpeWithFormattedMessage() {
            var ex = assertThrows(NullPointerException.class,
                    () -> ObjectTools.requireNonNull(null, "user %s must not be null", "admin"));
            assertEquals("user admin must not be null", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("requireNotEmpty")
    class RequireNotEmptyTest {

        @Test
        void allowsBooleanFalse() {
            assertEquals(false, ObjectTools.requireNotEmpty(false, "ctx"));
        }

        @Test
        void allowsContainerWithAllNonEmptyElements() {
            var arr = new Object[]{"x", "y"};
            assertSame(arr, ObjectTools.requireNotEmpty(arr, "ctx"));
        }

        @Test
        void allowsNonEmptyArray() {
            Object[] arr = {"x"};
            assertSame(arr, ObjectTools.requireNotEmpty(arr, "ctx"));
        }

        @Test
        void allowsNonEmptyList() {
            var list = List.of("x");
            assertSame(list, ObjectTools.requireNotEmpty(list, "ctx"));
        }

        @Test
        void allowsNonEmptyMap() {
            var map = Map.of("k", "v");
            assertSame(map, ObjectTools.requireNotEmpty(map, "ctx"));
        }

        @Test
        void allowsNonEmptyString() {
            assertEquals("x", ObjectTools.requireNotEmpty("x", "ctx"));
        }

        @Test
        void allowsZero() {
            assertEquals(0, ObjectTools.requireNotEmpty(0, "ctx"));
        }

        @Test
        void formattedMessage() {
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireNotEmpty("", "%s is null", "%s is empty", "foo"));
            assertEquals("foo is empty", ex.getMessage());
        }

        @Test
        void formattingFailureFallsBack() {
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireNotEmpty("", "%s is null", "%s %s is empty", "onlyOne"));
            assertEquals("%s %s is empty", ex.getMessage());
        }

        @Test
        void throwsForArrayWithEmptyElement() {
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireNotEmpty(new Object[]{""}, "ctx"));
            assertEquals("ctx must not be empty", ex.getMessage());
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = " ")
        void throwsForBlankContext(String context) {
            assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireNotEmpty("x", context));
        }

        @Test
        void throwsForCollectionWithEmptyElement() {
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireNotEmpty(List.of(""), "ctx"));
            assertEquals("ctx must not be empty", ex.getMessage());
        }

        @Test
        void throwsForEmptyArray() {
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireNotEmpty(new Object[]{}, "ctx"));
            assertEquals("ctx must not be empty", ex.getMessage());
        }

        @Test
        void throwsForEmptyCollection() {
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireNotEmpty(List.of(), "ctx"));
            assertEquals("ctx must not be empty", ex.getMessage());
        }

        @Test
        void throwsForEmptyMap() {
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireNotEmpty(Map.of(), "ctx"));
            assertEquals("ctx must not be empty", ex.getMessage());
        }

        @Test
        void throwsForEmptyString() {
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireNotEmpty("", "ctx"));
            assertEquals("ctx must not be empty", ex.getMessage());
        }

        @Test
        void throwsForMapWithEmptyValue() {
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireNotEmpty(Map.of("a", ""), "ctx"));
            assertEquals("ctx must not be empty", ex.getMessage());
        }

        @Test
        void throwsNpeForArrayWithNullElement() {
            var ex = assertThrows(NullPointerException.class,
                    () -> ObjectTools.requireNotEmpty(new Object[]{null}, "ctx"));
            assertEquals("ctx must not be null", ex.getMessage());
        }

        @Test
        void throwsNpeForCollectionWithNullElement() {
            var ex = assertThrows(NullPointerException.class,
                    () -> ObjectTools.requireNotEmpty(Arrays.asList("x", null), "ctx"));
            assertEquals("ctx must not be null", ex.getMessage());
        }

        @Test
        void throwsNpeForMapWithNullValue() {
            Map<String, String> map = new HashMap<>();
            map.put("a", null);
            var ex = assertThrows(NullPointerException.class,
                    () -> ObjectTools.requireNotEmpty(map, "ctx"));
            assertEquals("ctx must not be null", ex.getMessage());
        }

        @Test
        @SuppressWarnings("DataFlowIssue")
        void throwsNpeForNull() {
            var ex = assertThrows(NullPointerException.class,
                    () -> ObjectTools.requireNotEmpty(null, "ctx"));
            assertEquals("ctx must not be null", ex.getMessage());
        }

        @Test
        void varArgsOverloadPassesThrough() {
            var x = "x";
            assertSame(x, ObjectTools.requireNotEmpty(x, "%s must not be null", "%s must not be empty", x));
        }
    }
}