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
import org.junit.jupiter.params.provider.NullSource;
import rife.bld.extension.testing.BlankSource;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Duration;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Predicate;
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
    @DisplayName("isContainer null branch")
    class IsContainerNullTest {

        @Test
        @DisplayName("isContainer returns false for null")
        void isContainer_nullReturnsFalse() throws Exception {
            var method = ObjectTools.class.getDeclaredMethod("isContainer", Object.class);
            method.setAccessible(true);

            boolean result = (boolean) method.invoke(null, (Object) null);

            assertFalse(result); // hits: if (value == null) return false;
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
    @DisplayName("primitive array fast path")
    class PrimitiveArrayFastPathTest {

        @Test
        void allEmpty_emptyPrimitiveArray() {
            assertTrue(ObjectTools.allEmpty(new int[0]));
        }

        @Test
        void allEmpty_nonEmptyPrimitiveArray() {
            assertFalse(ObjectTools.allEmpty(new int[]{1}));
        }

        @Test
        void allNotEmpty_emptyPrimitiveArray() {
            assertFalse(ObjectTools.allNotEmpty(new int[0]));
        }

        @Test
        void allNotEmpty_nonEmptyPrimitiveArray() {
            assertTrue(ObjectTools.allNotEmpty(new int[]{1}));
        }

        @Test
        void anyEmpty_emptyPrimitiveArray() {
            assertTrue(ObjectTools.anyEmpty(new int[0]));
        }

        @Test
        void anyEmpty_nonEmptyPrimitiveArray() {
            assertFalse(ObjectTools.anyEmpty(new int[]{1}));
        }

        @Test
        void anyNotEmpty_emptyPrimitiveArray() {
            assertFalse(ObjectTools.anyNotEmpty(new int[0]));
        }

        @Test
        void anyNotEmpty_nonEmptyPrimitiveArray() {
            assertTrue(ObjectTools.anyNotEmpty(new int[]{1}));
        }

        @Test
        @SuppressWarnings("unchecked")
        void primitiveArrayBranches() throws Exception {
            var m = ObjectTools.class.getDeclaredMethod("forEachPrimitiveArray",
                    Object.class, Predicate.class, boolean.class);
            m.setAccessible(true);

            var f1 = ObjectTools.class.getDeclaredField("isEmptyPredicate");
            var f2 = ObjectTools.class.getDeclaredField("isNotEmptyPredicate");
            f1.setAccessible(true);
            f2.setAccessible(true);

            Predicate<Object> isEmpty = (Predicate<Object>) f1.get(null);
            Predicate<Object> isNotEmpty = (Predicate<Object>) f2.get(null);

            // new int[0] cases
            assertTrue((boolean) m.invoke(null, new int[0], isEmpty, true)); // allEmpty
            assertTrue((boolean) m.invoke(null, new int[0], isEmpty, false)); // anyEmpty
            assertFalse((boolean) m.invoke(null, new int[0], isNotEmpty, true)); // allNotEmpty
            assertFalse((boolean) m.invoke(null, new int[0], isNotEmpty, false)); // anyNotEmpty

            // new int[1] cases
            assertFalse((boolean) m.invoke(null, new int[1], isEmpty, true)); // allEmpty
            assertFalse((boolean) m.invoke(null, new int[1], isEmpty, false)); // anyEmpty
            assertTrue((boolean) m.invoke(null, new int[1], isNotEmpty, true)); // allNotEmpty
            assertTrue((boolean) m.invoke(null, new int[1], isNotEmpty, false)); // anyNotEmpty
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
                    () -> ObjectTools.requireEmpty("x", () -> String.format("Value %s must be empty", "foo")));
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
        @BlankSource
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

        @ParameterizedTest
        @NullSource
        void throwsForNullMessage(String message) {
            assertThrows(NullPointerException.class,
                    () -> ObjectTools.requireEmpty("", message));
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
            assertSame(x, ObjectTools.requireEmpty(x, () -> String.format("value '%s' must be empty", "input")));
        }

        @Test
        void varArgsOverloadFormatsAndThrows() {
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireEmpty("x", () -> String.format("value '%s' must be empty", "input")));
            assertEquals("value 'input' must be empty", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("requireNegative")
    class RequireNegativeTest {

        static Stream<Arguments> negativeNumbers() {
            return Stream.of(
                    Arguments.of(-1),
                    Arguments.of(-42L),
                    Arguments.of(-0.1d),
                    Arguments.of(-0.001f),
                    Arguments.of(BigInteger.valueOf(-1)),
                    Arguments.of(BigInteger.valueOf(Long.MIN_VALUE).subtract(BigInteger.ONE)),
                    Arguments.of(BigDecimal.valueOf(-0.01)),
                    Arguments.of(new BigDecimal("-9999999999999999999999999999.1"))
            );
        }

        static Stream<Arguments> unsupportedComparableTypes() {
            return Stream.of(
                    Arguments.of("string"),
                    Arguments.of(LocalDate.now()),
                    Arguments.of(UUID.randomUUID()),
                    Arguments.of(Duration.ofSeconds(1))
            );
        }

        static Stream<Arguments> zeroOrPositiveNumbers() {
            return Stream.of(
                    Arguments.of(0),
                    Arguments.of(1),
                    Arguments.of(0L),
                    Arguments.of(42L),
                    Arguments.of(0.0d),
                    Arguments.of(0.1d),
                    Arguments.of(0.0f),
                    Arguments.of(0.001f),
                    Arguments.of(BigInteger.ZERO),
                    Arguments.of(BigInteger.ONE),
                    Arguments.of(BigDecimal.ZERO),
                    Arguments.of(BigDecimal.valueOf(0.01)),
                    Arguments.of(new BigDecimal("9999999999999999.1"))
            );
        }

        @ParameterizedTest(name = "allows negative: {0}")
        @MethodSource("negativeNumbers")
        <T extends Comparable<T>> void allowsNegativeValues(T value) {
            assertSame(value, ObjectTools.requireNegative(value, "value"));
        }

        @Test
        void defaultMessageUsesLabelAndValue() {
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireNegative(5, "age"));
            assertEquals("age must be negative, got: 5", ex.getMessage());
        }

        @Test
        void returnsSameInstance() {
            var bd = new BigDecimal("-42.5");
            assertSame(bd, ObjectTools.requireNegative(bd, "bd"));
        }

        @SuppressWarnings({"rawtypes"})
        @ParameterizedTest(name = "throws IAE for unsupported type: {0}")
        @MethodSource("unsupportedComparableTypes")
        void throwsForUnsupportedComparableType(Comparable value) {
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireNegative(value, "value"));
            assertTrue(ex.getMessage().startsWith("Unsupported type: " + value.getClass().getName()));
        }

        @ParameterizedTest(name = "throws IAE for zero or positive: {0}")
        @MethodSource("zeroOrPositiveNumbers")
        <T extends Comparable<T>> void throwsForZeroOrPositive(T value) {
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireNegative(value, "value"));
            assertEquals("value must be negative, got: " + value, ex.getMessage());
        }

        @ParameterizedTest
        @BlankSource
        void throwsIaeForBlankMessage(String message) {
            assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireNegative(-1, message));
        }

        @ParameterizedTest
        @NullSource
        void throwsIaeForNullMessage(String message) {
            assertThrows(NullPointerException.class,
                    () -> ObjectTools.requireNegative(-1, message));
        }

        @Test
        @SuppressWarnings("DataFlowIssue")
        void throwsNpeForNullContext() {
            var ex = assertThrows(NullPointerException.class,
                    () -> ObjectTools.requireNegative(-1, (String) null));
            assertEquals("context must not be null", ex.getMessage());
        }

        @Test
        @SuppressWarnings("DataFlowIssue")
        void throwsNpeForNullValue() {
            var ex = assertThrows(NullPointerException.class,
                    () -> ObjectTools.requireNegative(null, "value"));
            assertEquals("value must not be null", ex.getMessage());
        }

        @Test
        void varArgsOverloadFormatsAndPasses() {
            var val = -5;
            assertSame(val, ObjectTools.requireNegative(val, String.format("%s %s must be < 0", "input", "value")));
        }

        @Test
        void varArgsOverloadFormatsAndThrows() {
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireNegative(1, String.format("%s %s must be < 0", "input", "value")));
            assertEquals("input value must be < 0 must be negative, got: 1", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("requireNonNegative")
    class RequireNonNegativeTest {

        static Stream<Arguments> negativeNumbers() {
            return Stream.of(
                    Arguments.of(-1),
                    Arguments.of(-42L),
                    Arguments.of(-0.1d),
                    Arguments.of(-0.001f),
                    Arguments.of(BigInteger.valueOf(-1)),
                    Arguments.of(BigDecimal.valueOf(-0.01)),
                    Arguments.of(new BigDecimal("-9999999999999999.1"))
            );
        }

        static Stream<Arguments> nonNegativeNumbers() {
            return Stream.of(
                    Arguments.of(0),
                    Arguments.of(1),
                    Arguments.of(0L),
                    Arguments.of(42L),
                    Arguments.of(0.0d),
                    Arguments.of(0.1d),
                    Arguments.of(0.0f),
                    Arguments.of(0.001f),
                    Arguments.of(BigInteger.ZERO),
                    Arguments.of(BigInteger.ONE),
                    Arguments.of(BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.ONE)),
                    Arguments.of(BigDecimal.ZERO),
                    Arguments.of(BigDecimal.valueOf(0.01)),
                    Arguments.of(new BigDecimal("9999999999999999999999999999.1"))
            );
        }

        static Stream<Arguments> unsupportedComparableTypes() {
            return Stream.of(
                    Arguments.of("string"),
                    Arguments.of(LocalDate.now()),
                    Arguments.of(UUID.randomUUID()),
                    Arguments.of(Duration.ofSeconds(1))
            );
        }

        @ParameterizedTest(name = "allows non-negative: {0}")
        @MethodSource("nonNegativeNumbers")
        <T extends Comparable<T>> void allowsNonNegativeValues(T value) {
            assertSame(value, ObjectTools.requireNonNegative(value, "value"));
        }

        @Test
        void defaultMessageUsesLabelAndValue() {
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireNonNegative(-5, "age"));
            assertEquals("age must be non-negative, got: -5", ex.getMessage());
        }

        @Test
        void returnsSameInstance() {
            var bd = BigDecimal.ZERO;
            assertSame(bd, ObjectTools.requireNonNegative(bd, "bd"));
        }

        @ParameterizedTest(name = "throws IAE for negative: {0}")
        @MethodSource("negativeNumbers")
        <T extends Comparable<T>> void throwsForNegative(T value) {
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireNonNegative(value, "value"));
            assertEquals("value must be non-negative, got: " + value, ex.getMessage());
        }

        @SuppressWarnings({"rawtypes"})
        @ParameterizedTest(name = "throws IAE for unsupported type: {0}")
        @MethodSource("unsupportedComparableTypes")
        void throwsForUnsupportedComparableType(Comparable value) {
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireNonNegative(value, "value"));
            System.out.println(ex.getMessage());
            assertTrue(ex.getMessage().startsWith("Unsupported type: " + value.getClass().getName()));
        }

        @ParameterizedTest
        @BlankSource
        void throwsIaeForBlankMessage(String message) {
            assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireNonNegative(0, message));
        }

        @ParameterizedTest
        @NullSource
        void throwsIaeForNullMessage(String message) {
            assertThrows(NullPointerException.class,
                    () -> ObjectTools.requireNonNegative(0, message));
        }

        @Test
        @SuppressWarnings("DataFlowIssue")
        void throwsNpeForNullContext() {
            var ex = assertThrows(NullPointerException.class,
                    () -> ObjectTools.requireNonNegative(0, (String) null));
            assertEquals("context must not be null", ex.getMessage());
        }

        @Test
        @SuppressWarnings("DataFlowIssue")
        void throwsNpeForNullValue() {
            var ex = assertThrows(NullPointerException.class,
                    () -> ObjectTools.requireNonNegative(null, "value"));
            assertEquals("value must not be null", ex.getMessage());
        }

        @Test
        void varArgsOverloadFormatsAndPasses() {
            var val = 0;
            assertSame(val, ObjectTools.requireNonNegative(val, () -> String.format("%s %s must be >= 0", "input", "value")));
        }

        @Test
        void varArgsOverloadFormatsAndThrows() {
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireNonNegative(-1, () -> String.format("%s %s must be >= 0", "input", "value")));
            assertEquals("input value must be >= 0", ex.getMessage());
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
        @SuppressWarnings("ConstantValue")
        void passesForEmptyCollection() {
            assertTrue(ObjectTools.requireNonNull(List.of(), "list").isEmpty());
        }

        @Test
        void passesForEmptyString() {
            assertEquals("", ObjectTools.requireNonNull("", () -> "value is empty, non-null"));
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
            assertThrows(IllegalArgumentException.class, () -> ObjectTools.requireNonNull("x", ""));
        }

        @Test
        void throwsNpeForArrayContainingNull() {
            String[] arr = {"a", null, "c"};
            var ex = assertThrows(NullPointerException.class,
                    () -> ObjectTools.requireNonNull(arr, "array"));
            assertEquals("array must not contain null elements", ex.getMessage());
        }

        @Test
        void throwsNpeForCollectionContainingNull() {
            List<String> list = Arrays.asList("a", null);
            var ex = assertThrows(NullPointerException.class,
                    () -> ObjectTools.requireNonNull(list, "list"));
            assertEquals("list must not contain null elements", ex.getMessage());
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
            assertEquals("map must not contain null elements", ex.getMessage());
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
                    () -> ObjectTools.requireNonNull(null, () -> String.format("user %s must not be null", "admin")));
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
        void throwsForArrayWithEmptyElement() {
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireNotEmpty(new Object[]{""}, "ctx"));
            assertEquals("ctx must not be empty", ex.getMessage());
        }

        @ParameterizedTest
        @BlankSource
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

        @ParameterizedTest
        @NullSource
        void throwsForNullContext(String context) {
            assertThrows(NullPointerException.class,
                    () -> ObjectTools.requireNotEmpty("x", context));
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
    }

    @Nested
    @DisplayName("requirePositive")
    class RequirePositiveTest {

        static Stream<Arguments> positiveNumbers() {
            return Stream.of(
                    Arguments.of(1),
                    Arguments.of(42L),
                    Arguments.of(0.1d),
                    Arguments.of(0.001f),
                    Arguments.of(BigInteger.ONE),
                    Arguments.of(BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.ONE)),
                    Arguments.of(BigDecimal.valueOf(0.01)),
                    Arguments.of(new BigDecimal("9999999999999999999999999999.1"))
            );
        }

        static Stream<Arguments> unsupportedComparableTypes() {
            return Stream.of(
                    Arguments.of("string"),
                    Arguments.of(LocalDate.now()),
                    Arguments.of(UUID.randomUUID()),
                    Arguments.of(Duration.ofSeconds(1))
            );
        }

        static Stream<Arguments> zeroOrNegativeNumbers() {
            return Stream.of(
                    Arguments.of(0),
                    Arguments.of(-1),
                    Arguments.of(0L),
                    Arguments.of(-42L),
                    Arguments.of(0.0d),
                    Arguments.of(-0.1d),
                    Arguments.of(0.0f),
                    Arguments.of(-0.001f),
                    Arguments.of(BigInteger.ZERO),
                    Arguments.of(BigInteger.valueOf(-1)),
                    Arguments.of(BigDecimal.ZERO),
                    Arguments.of(BigDecimal.valueOf(-0.01)),
                    Arguments.of(new BigDecimal("-9999999999999999.1"))
            );
        }

        @ParameterizedTest(name = "allows positive: {0}")
        @MethodSource("positiveNumbers")
        <T extends Comparable<T>> void allowsPositiveValues(T value) {
            assertSame(value, ObjectTools.requirePositive(value, "value"));
        }

        @Test
        void defaultMessageUsesLabelAndValue() {
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requirePositive(-5, "age"));
            assertEquals("age must be positive, got: -5", ex.getMessage());
        }

        @Test
        void returnsSameInstance() {
            var bd = new BigDecimal("42.5");
            assertSame(bd, ObjectTools.requirePositive(bd, "bd"));
        }

        @SuppressWarnings({"rawtypes"})
        @ParameterizedTest(name = "throws IAE for unsupported type: {0}")
        @MethodSource("unsupportedComparableTypes")
        void throwsForUnsupportedComparableType(Comparable value) {
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requirePositive(value, "value"));
            assertTrue(ex.getMessage().startsWith("Unsupported type: " + value.getClass().getName()));
        }

        @ParameterizedTest(name = "throws IAE for zero or negative: {0}")
        @MethodSource("zeroOrNegativeNumbers")
        <T extends Comparable<T>> void throwsForZeroOrNegative(T value) {
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requirePositive(value, "value"));
            assertEquals("value must be positive, got: " + value, ex.getMessage());
        }

        @ParameterizedTest
        @BlankSource
        void throwsIaeForBlankMessage(String message) {
            assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requirePositive(1, message));
        }

        @ParameterizedTest
        @NullSource
        void throwsIaeForNullMessage(String message) {
            assertThrows(NullPointerException.class,
                    () -> ObjectTools.requirePositive(1, message));
        }

        @Test
        @SuppressWarnings("DataFlowIssue")
        void throwsNpeForNullContext() {
            var ex = assertThrows(NullPointerException.class,
                    () -> ObjectTools.requirePositive(1, (String) null));
            assertEquals("context must not be null", ex.getMessage());
        }

        @Test
        @SuppressWarnings("DataFlowIssue")
        void throwsNpeForNullValue() {
            var ex = assertThrows(NullPointerException.class,
                    () -> ObjectTools.requirePositive(null, "value"));
            assertEquals("value must not be null", ex.getMessage());
        }

        @Test
        void varArgsOverloadFormatsAndPasses() {
            var val = 5;
            assertSame(val, ObjectTools.requirePositive(val, () -> String.format("%s %s must be > 0", "input", "value")));
        }

        @Test
        void varArgsOverloadFormatsAndThrows() {
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requirePositive(-1, () -> String.format("%s %s must be > 0", "input", "value")));
            assertEquals("input value must be > 0", ex.getMessage());
        }
    }

}
