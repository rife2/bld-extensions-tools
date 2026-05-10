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

    static Stream<Arguments> allEmptyContainers() {
        return Stream.of(
                Arguments.of((Object) new Object[]{"", List.of(), Map.of(), new Object[]{}, new int[]{}}),
                Arguments.of(List.of("", List.of(), Map.of())),
                Arguments.of(Collections.singletonMap("", "")), // key and value empty
                Arguments.of((Object) new String[]{""}),
                Arguments.of((Object) new Object[]{null, null}),
                Arguments.of(Map.of()), // empty map
                Arguments.of(List.of()), // empty list
                Arguments.of("") // empty string
        );
    }

    static Stream<Arguments> allNotEmptyContainers() {
        return Stream.of(
                Arguments.of((Object) new Object[]{"x", "y"}),
                Arguments.of(List.of("x", List.of("y"), Map.of("k", "v"))),
                Arguments.of(Map.of("a", "x", "b", "y")),
                Arguments.of((Object) new int[]{1, 2}),
                Arguments.of((Object) new String[]{"x"})
        );
    }

    static Stream<Arguments> containersWithEmptyElement() {
        return Stream.of(
                Arguments.of((Object) new Object[]{"x", ""}),
                Arguments.of(List.of("x", "")),
                Arguments.of(Map.of("a", "x", "b", "")),
                Arguments.of((Object) new Object[]{List.of()}),
                Arguments.of((Object) new String[]{"", "x"})
        );
    }

    static Stream<Arguments> containersWithNonEmptyElement() {
        return Stream.of(
                Arguments.of((Object) new Object[]{"", "x"}),
                Arguments.of(List.of("", "x")),
                Arguments.of(Collections.singletonMap("", "x")), // empty key, non-empty value
                Arguments.of((Object) new Object[]{List.of(), "x"})
        );
    }

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
    @DisplayName("Predicate: allEmpty")
    class AllEmptyTest {

        @ParameterizedTest(name = "allEmpty({0}) → false")
        @MethodSource("rife.bld.extension.tools.ObjectToolsTest#containersWithNonEmptyElement")
        void allEmptyFalse(Object value) {
            assertFalse(ObjectTools.allEmpty(value));
        }

        @ParameterizedTest(name = "allEmpty({0}) → true")
        @MethodSource("rife.bld.extension.tools.ObjectToolsTest#allEmptyContainers")
        void allEmptyTrue(Object value) {
            assertTrue(ObjectTools.allEmpty(value));
        }

        @ParameterizedTest(name = "allEmpty({0}) → true")
        @MethodSource("rife.bld.extension.tools.ObjectToolsTest#emptyContainers")
        void allEmptyTrueForEmptyContainers(Object value) {
            assertTrue(ObjectTools.allEmpty(value));
        }

        @Test
        @DisplayName("allEmpty(\"x\") → false")
        void nonEmptyString() {
            assertFalse(ObjectTools.allEmpty("x"));
        }

        @Test
        @DisplayName("allEmpty(null) → true")
        void nullValue() {
            assertTrue(ObjectTools.allEmpty(null));
        }
    }

    @Nested
    @DisplayName("Predicate: allNotEmpty")
    class AllNotEmptyTest {

        @ParameterizedTest(name = "allNotEmpty({0}) → false")
        @MethodSource("rife.bld.extension.tools.ObjectToolsTest#containersWithEmptyElement")
        void allNotEmptyFalse(Object value) {
            assertFalse(ObjectTools.allNotEmpty(value));
        }

        @ParameterizedTest(name = "allNotEmpty({0}) → false")
        @MethodSource("rife.bld.extension.tools.ObjectToolsTest#emptyContainers")
        void allNotEmptyFalseForEmptyContainers(Object value) {
            assertFalse(ObjectTools.allNotEmpty(value));
        }

        @ParameterizedTest(name = "allNotEmpty({0}) → true")
        @MethodSource("rife.bld.extension.tools.ObjectToolsTest#allNotEmptyContainers")
        void allNotEmptyTrue(Object value) {
            assertTrue(ObjectTools.allNotEmpty(value));
        }

        @Test
        @DisplayName("allNotEmpty(\"\") → false")
        void emptyString() {
            assertFalse(ObjectTools.allNotEmpty(""));
        }

        @Test
        @DisplayName("allNotEmpty(\"x\") → true")
        void nonEmptyString() {
            assertTrue(ObjectTools.allNotEmpty("x"));
        }

        @Test
        @DisplayName("allNotEmpty(null) → false")
        void nullValue() {
            assertFalse(ObjectTools.allNotEmpty(null));
        }
    }

    @Nested
    @DisplayName("Predicate: anyEmpty")
    class AnyEmptyTest {

        @ParameterizedTest(name = "anyEmpty({0}) → false")
        @MethodSource("rife.bld.extension.tools.ObjectToolsTest#allNotEmptyContainers")
        void anyEmptyFalse(Object value) {
            assertFalse(ObjectTools.anyEmpty(value));
        }

        @ParameterizedTest(name = "anyEmpty({0}) → true")
        @MethodSource("rife.bld.extension.tools.ObjectToolsTest#containersWithEmptyElement")
        void anyEmptyTrue(Object value) {
            assertTrue(ObjectTools.anyEmpty(value));
        }

        @ParameterizedTest(name = "anyEmpty({0}) → true")
        @MethodSource("rife.bld.extension.tools.ObjectToolsTest#emptyContainers")
        void anyEmptyTrueForEmptyContainers(Object value) {
            assertTrue(ObjectTools.anyEmpty(value));
        }

        @Test
        @DisplayName("anyEmpty(null) → true")
        void nullValue() {
            assertTrue(ObjectTools.anyEmpty(null));
        }
    }

    @Nested
    @DisplayName("Predicate: anyNotEmpty")
    class AnyNotEmptyTest {

        @ParameterizedTest(name = "anyNotEmpty({0}) → false")
        @MethodSource("rife.bld.extension.tools.ObjectToolsTest#allEmptyContainers")
        void anyNotEmptyFalse(Object value) {
            assertFalse(ObjectTools.anyNotEmpty(value));
        }

        @ParameterizedTest(name = "anyNotEmpty({0}) → false")
        @MethodSource("rife.bld.extension.tools.ObjectToolsTest#emptyContainers")
        void anyNotEmptyFalseForEmptyContainers(Object value) {
            assertFalse(ObjectTools.anyNotEmpty(value));
        }

        @ParameterizedTest(name = "anyNotEmpty({0}) → true")
        @MethodSource("rife.bld.extension.tools.ObjectToolsTest#containersWithNonEmptyElement")
        void anyNotEmptyTrue(Object value) {
            assertTrue(ObjectTools.anyNotEmpty(value));
        }

        @Test
        @DisplayName("anyNotEmpty(null) → false")
        void nullValue() {
            assertFalse(ObjectTools.anyNotEmpty(null));
        }
    }

    @Nested
    @DisplayName("Predicate: isEmpty")
    class IsEmptyTest {

        @Test
        @DisplayName("isEmpty(false) → false")
        void isEmptyBooleanFalse() {
            assertFalse(ObjectTools.isEmpty(false));
        }

        @Test
        @DisplayName("isEmpty(new Object[]{}) → true")
        void isEmptyEmptyArray() {
            assertTrue(ObjectTools.isEmpty(new Object[]{}));
        }

        @Test
        @DisplayName("isEmpty(List.of()) → true")
        void isEmptyEmptyList() {
            assertTrue(ObjectTools.isEmpty(List.of()));
        }

        @Test
        @DisplayName("isEmpty(Map.of()) → true")
        void isEmptyEmptyMap() {
            assertTrue(ObjectTools.isEmpty(Map.of()));
        }

        @Test
        @DisplayName("isEmpty(\"\") → true")
        void isEmptyEmptyString() {
            assertTrue(ObjectTools.isEmpty(""));
        }

        @Test
        @DisplayName("isEmpty(new Object[]{\"x\"}) → false")
        void isEmptyNonEmptyArray() {
            assertFalse(ObjectTools.isEmpty(new Object[]{"x"}));
        }

        @Test
        @DisplayName("isEmpty(List.of(\"x\")) → false")
        void isEmptyNonEmptyList() {
            assertFalse(ObjectTools.isEmpty(List.of("x")));
        }

        @Test
        @DisplayName("isEmpty(null) → true")
        void isEmptyNull() {
            assertTrue(ObjectTools.isEmpty(null));
        }
    }

    @Nested
    @DisplayName("Predicate: isNotEmpty")
    class IsNotEmptyTest {

        @Test
        @DisplayName("isNotEmpty(false) → true")
        void isNotEmptyBooleanFalse() {
            assertTrue(ObjectTools.isNotEmpty(false));
        }

        @Test
        @DisplayName("isNotEmpty(new Object[]{}) → false")
        void isNotEmptyEmptyArray() {
            assertFalse(ObjectTools.isNotEmpty(new Object[]{}));
        }

        @Test
        @DisplayName("isNotEmpty(List.of()) → false")
        void isNotEmptyEmptyList() {
            assertFalse(ObjectTools.isNotEmpty(List.of()));
        }

        @Test
        @DisplayName("isNotEmpty(\"\") → false")
        void isNotEmptyEmptyString() {
            assertFalse(ObjectTools.isNotEmpty(""));
        }

        @Test
        @DisplayName("isNotEmpty(\"x\") → true")
        void isNotEmptyNonEmptyString() {
            assertTrue(ObjectTools.isNotEmpty("x"));
        }

        @Test
        @DisplayName("isNotEmpty(null) → false")
        void isNotEmptyNull() {
            assertFalse(ObjectTools.isNotEmpty(null));
        }
    }

    @Nested
    @DisplayName("Validator: requireEmpty")
    class RequireEmptyTest {

        @ParameterizedTest(name = "allows {0}")
        @MethodSource("rife.bld.extension.tools.ObjectToolsTest#allEmptyContainers")
        void allowsContainerWithAllEmptyElements(Object value) {
            assertSame(value, ObjectTools.requireEmpty(value, "ctx"));
        }

        @ParameterizedTest(name = "allows {0}")
        @MethodSource("rife.bld.extension.tools.ObjectToolsTest#emptyContainers")
        void allowsEmptyContainers(Object value) {
            assertSame(value, ObjectTools.requireEmpty(value, "ctx"));
        }

        @Test
        @DisplayName("allows null")
        void allowsNull() {
            assertNull(ObjectTools.requireEmpty(null, "ctx"));
        }

        @Test
        @DisplayName("formatted message with args")
        void formattedMessage() {
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireEmpty("x", "Value %s must be empty", "foo"));
            assertEquals("Value foo must be empty", ex.getMessage());
        }

        @Test
        @DisplayName("throws when array has non-empty element")
        void throwsForArrayWithNonEmptyElement() {
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireEmpty(new Object[]{"x"}, "ctx"));
            assertEquals("ctx", ex.getMessage());
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = "   ")
        @DisplayName("throws when message is null/empty/blank")
        void throwsForInvalidMessage(String message) {
            assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireEmpty("", message));
        }

        @Test
        @DisplayName("throws when non-empty string")
        void throwsForNonEmptyString() {
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireEmpty("x", "ctx"));
            assertEquals("ctx", ex.getMessage());
        }

        @Test
        @DisplayName("with messages")
        void withMessages() {
            var x = "";
            assertSame(x, ObjectTools.requireEmpty(x, "%s must not be null",
                    "%s must be empty", x));
        }
    }

    @Nested
    @DisplayName("requireNonNull")
    class RequireNonNullTest {

        @Test
        @DisplayName("checks nested containers recursively")
        void checksNestedContainersRecursively() {
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
        @DisplayName("passes for collection with non-null elements")
        void passesForCollectionWithNonNullElements() {
            var list = List.of("a", "b", "");
            var result = ObjectTools.requireNonNull(list, "list");
            assertEquals(List.of("a", "b", ""), result);
        }

        @Test
        @DisplayName("passes for empty array")
        void passesForEmptyArray() {
            var arr = new String[0];
            var result = ObjectTools.requireNonNull(arr, "array");
            assertArrayEquals(new String[0], result);
        }

        @Test
        @DisplayName("passes for empty collection")
        void passesForEmptyCollection() {
            var list = List.of();
            var result = ObjectTools.requireNonNull(list, "list");
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("passes for empty String")
        void passesForEmptyString() {
            var result = ObjectTools.requireNonNull("", "value");
            assertEquals("", result);
        }

        @Test
        @DisplayName("passes for nested containers with no nulls")
        void passesForNestedContainersWithNoNulls() {
            var nested = List.of(Set.of("a", ""), Set.of());
            var result = ObjectTools.requireNonNull(nested, "nested");
            assertEquals(2, result.size());
        }

        @Test
        @DisplayName("passes for non-null non-container")
        void passesForNonNullValue() {
            var result = ObjectTools.requireNonNull("test", "value");
            assertEquals("test", result);
        }

        @Test
        @DisplayName("throws IAE for blank context")
        void throwsIaeForBlankContext() {
            assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireNonNull("test", " "));
        }

        @Test
        @DisplayName("throws IAE for blank message")
        void throwsIaeForBlankMessage() {
            assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireNonNull("test", "", "arg"));
        }

        @Test
        @DisplayName("throws NPE for array containing null")
        void throwsNpeForArrayContainingNull() {
            String[] arr = {"a", null, "c"};
            NullPointerException ex = assertThrows(NullPointerException.class,
                    () -> ObjectTools.requireNonNull(arr, "array"));
            assertEquals("array must not be null", ex.getMessage());
        }

        @Test
        @DisplayName("throws NPE for collection containing null")
        void throwsNpeForCollectionContainingNull() {
            List<String> list = Arrays.asList("a", null);
            NullPointerException ex = assertThrows(NullPointerException.class,
                    () -> ObjectTools.requireNonNull(list, "list"));
            assertEquals("list must not be null", ex.getMessage());
        }

        @Test
        @DisplayName("throws NPE for map with null key")
        void throwsNpeForMapWithNullKey() {
            Map<String, String> map = new java.util.HashMap<>();
            map.put(null, "value");
            assertThrows(NullPointerException.class,
                    () -> ObjectTools.requireNonNull(map, "map"));
        }

        @Test
        @DisplayName("throws NPE for map with null value")
        void throwsNpeForMapWithNullValue() {
            Map<String, String> map = new HashMap<>();
            map.put("key", null);
            NullPointerException ex = assertThrows(NullPointerException.class,
                    () -> ObjectTools.requireNonNull(map, "map"));
            assertEquals("map must not be null", ex.getMessage());
        }

        @Test
        @DisplayName("throws NPE for null value with context")
        @SuppressWarnings("DataFlowIssue")
        void throwsNpeForNullValueWithContext() {
            NullPointerException ex = assertThrows(NullPointerException.class,
                    () -> ObjectTools.requireNonNull(null, "userList"));
            assertEquals("userList must not be null", ex.getMessage());
        }

        @Test
        @DisplayName("throws NPE for null value with formatted message")
        @SuppressWarnings("DataFlowIssue")
        void throwsNpeForNullValueWithMessage() {
            NullPointerException ex = assertThrows(NullPointerException.class,
                    () -> ObjectTools.requireNonNull(null, "user %s must not be null", "admin"));
            assertEquals("user admin must not be null", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("Validator: requireNotEmpty")
    class RequireNotEmptyTest {

        @Test
        @DisplayName("allows false boolean")
        void allowsBooleanFalse() {
            assertEquals(false, ObjectTools.requireNotEmpty(false, "ctx"));
        }

        @Test
        @DisplayName("allows container with all non-empty elements")
        void allowsContainerWithAllNonEmptyElements() {
            var arr = new Object[]{"x", "y"};
            assertSame(arr, ObjectTools.requireNotEmpty(arr, "ctx"));
        }

        @Test
        @DisplayName("allows non-empty array")
        void allowsNonEmptyArray() {
            Object[] arr = new Object[]{"x"};
            assertSame(arr, ObjectTools.requireNotEmpty(arr, "ctx"));
        }

        @Test
        @DisplayName("allows non-empty collection")
        void allowsNonEmptyCollection() {
            List<String> list = List.of("x");
            assertSame(list, ObjectTools.requireNotEmpty(list, "ctx"));
        }

        @Test
        @DisplayName("allows non-empty map")
        void allowsNonEmptyMap() {
            Map<String, String> map = Map.of("k", "v");
            assertSame(map, ObjectTools.requireNotEmpty(map, "ctx"));
        }

        @Test
        @DisplayName("allows non-empty string")
        void allowsNonEmptyString() {
            assertEquals("x", ObjectTools.requireNotEmpty("x", "ctx"));
        }

        @Test
        @DisplayName("allows 0")
        void allowsZero() {
            assertEquals(0, ObjectTools.requireNotEmpty(0, "ctx"));
        }

        @Test
        @DisplayName("formatted message with args")
        void formattedMessage() {
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireNotEmpty("", "%s is null", "%s is empty", "foo"));
            assertEquals("foo is empty", ex.getMessage());
        }

        @Test
        @DisplayName("formatting failure falls back to raw message")
        void formattingFailureFallsBack() {
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireNotEmpty("", "%s is null", "%s %s is empty", "onlyOne"));
            assertEquals("%s %s is empty", ex.getMessage());
        }

        @Test
        @DisplayName("throws when array has empty element")
        void throwsForArrayWithEmptyElement() {
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireNotEmpty(new Object[]{""}, "ctx"));
            assertEquals("ctx must not be empty", ex.getMessage());
        }

        @Test
        @DisplayName("throws when array has null element")
        void throwsForArrayWithNullElement() {
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireNotEmpty(new Object[]{null}, "ctx"));
            assertEquals("ctx must not be empty", ex.getMessage());
        }

        @Test
        @DisplayName("throws when collection has empty element")
        void throwsForCollectionWithEmptyElement() {
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireNotEmpty(List.of(""), "ctx"));
            assertEquals("ctx must not be empty", ex.getMessage());
        }

        @Test
        @DisplayName("throws when array is empty")
        void throwsForEmptyArray() {
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireNotEmpty(new Object[]{}, "ctx"));
            assertEquals("ctx must not be empty", ex.getMessage());
        }

        @Test
        @DisplayName("throws when collection is empty")
        void throwsForEmptyCollection() {
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireNotEmpty(List.of(), "ctx"));
            assertEquals("ctx must not be empty", ex.getMessage());
        }

        @Test
        @DisplayName("throws when map is empty")
        void throwsForEmptyMap() {
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireNotEmpty(Map.of(), "ctx"));
            assertEquals("ctx must not be empty", ex.getMessage());
        }

        @Test
        @DisplayName("throws when string is empty")
        void throwsForEmptyString() {
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireNotEmpty("", "ctx"));
            assertEquals("ctx must not be empty", ex.getMessage());
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = "   ")
        @DisplayName("throws when context is null/empty/blank")
        void throwsForInvalidContext(String context) {
            assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireNotEmpty("x", context));
        }

        @Test
        @DisplayName("throws when map has empty value")
        void throwsForMapWithEmptyValue() {
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireNotEmpty(Map.of("a", ""), "ctx"));
            assertEquals("ctx must not be empty", ex.getMessage());
        }

        @Test
        @DisplayName("throws when null")
        @SuppressWarnings("DataFlowIssue")
        void throwsForNull() {
            var ex = assertThrows(NullPointerException.class,
                    () -> ObjectTools.requireNotEmpty(null, "ctx"));
            assertEquals("ctx must not be null", ex.getMessage());
        }

        @Test
        @DisplayName("with messages")
        void withMessages() {
            var x = "x";
            assertSame(x, ObjectTools.requireNotEmpty(x, "%s must not be null",
                    "%s must not be empty", x));
        }
    }

}