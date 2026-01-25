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
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Text Tools Tests")
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
class TextToolsTest {

    @Nested
    @DisplayName("isBlank(Object...) Tests")
    class IsBlankObjectTests {

        @Test
        @DisplayName("isBlank(Object...) should continue when CharSequence is blank")
        @SuppressWarnings("PMD.UnnecessaryVarargsArrayCreation")
        void isBlankObjectArrayShouldContinueWhenCharSequenceIsBlank() {
            CharSequence cs = new StringBuffer("  ");
            var result = TextTools.isBlank(new Object[]{cs, "", " "});
            assertTrue(result);
        }

        @Test
        @DisplayName("isBlank(Object...) should continue when toString is blank")
        void isBlankObjectArrayShouldContinueWhenToStringIsBlank() {
            var obj = new Object() {
                @Override
                public String toString() {
                    return "  ";
                }
            };
            var result = TextTools.isBlank(obj, "", " ");
            assertTrue(result);
        }

        @Test
        @DisplayName("should avoid toString for CharSequence objects")
        void shouldAvoidToStringForCharSequenceObjects() {
            var sb = new StringBuilder("text");
            var result = TextTools.isBlank(sb);
            assertFalse(result);
        }

        @Test
        @DisplayName("should handle mixed CharSequence and non-CharSequence objects")
        void shouldHandleMixedObjects() {
            var sb = new StringBuilder("data");
            var result = TextTools.isBlank(sb, 42, "text");
            assertFalse(result);
        }

        @ParameterizedTest
        @ValueSource(ints = {0, 1, 42, -1})
        @DisplayName("should return false for non-null non-blank objects")
        void shouldReturnFalseForNonBlankObjects(Integer input) {
            var result = TextTools.isBlank(input);
            assertFalse(result);
        }

        @ParameterizedTest
        @ValueSource(strings = {"a", "text", " text ", "  text  ", "\ttext\n", "123"})
        @DisplayName("should return false for non-blank string objects")
        void shouldReturnFalseForNonBlankStringObjects(String input) {
            var result = TextTools.isBlank((Object) input);
            assertFalse(result);
        }

        @ParameterizedTest
        @ValueSource(ints = {0, 1, 42, -1})
        @DisplayName("should return false when all objects are not blank")
        void shouldReturnFalseWhenAllObjectsAreNotBlank(Integer input) {
            var result = TextTools.isBlank(input, 123, "text");
            assertFalse(result);
        }

        @ParameterizedTest
        @ValueSource(strings = {"a", "text", " text ", "  text  ", "\ttext\n", "123"})
        @DisplayName("should return false when at least one object is not blank")
        void shouldReturnFalseWhenAtLeastOneObjectIsNotBlank(String input) {
            var result = TextTools.isBlank("", " ", input, "\t");
            assertFalse(result);
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " ", "  ", "\t", "\n", "\r", " \t\n\r "})
        @DisplayName("should return true for empty or whitespace-only string objects")
        void shouldReturnTrueForBlankStringObjects(String input) {
            var result = TextTools.isBlank((Object) input);
            assertTrue(result);
        }

        @Test
        @DisplayName("should return true for empty varargs array")
        void shouldReturnTrueForEmptyVarargsArray() {
            var result = TextTools.isBlank(new Object[0]);
            assertTrue(result);
        }

        @ParameterizedTest
        @NullSource
        @DisplayName("should return true for null array")
        @SuppressWarnings("PMD.UseVarargs")
        void shouldReturnTrueForNullArray(Object[] input) {
            var result = TextTools.isBlank(input);
            assertTrue(result);
        }

        @ParameterizedTest
        @NullSource
        @DisplayName("should return true for null object")
        void shouldReturnTrueForNullObject(Object input) {
            var result = TextTools.isBlank(input);
            assertTrue(result);
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " ", "  ", "\t", "\n", "\r", " \t\n\r "})
        @DisplayName("should return true when all objects are blank")
        void shouldReturnTrueWhenAllObjectsAreBlank(String input) {
            var result = TextTools.isBlank(input, "", "  ", "\t");
            assertTrue(result);
        }
    }

    @Nested
    @DisplayName("isBlank(CharSequence) Tests")
    class IsBlankTests {

        @Test
        @DisplayName("isBlank(CharSequence) should handle non-String blank CharSequence")
        void isBlankShouldHandleNonStringBlankCharSequence() {
            CharSequence cs = new StringBuffer("   ");
            var result = TextTools.isBlank(cs);
            assertTrue(result);
        }

        @Test
        @DisplayName("isBlank(CharSequence) should handle non-String CharSequence with content")
        void isBlankShouldHandleNonStringCharSequenceWithContent() {
            CharSequence cs = new StringBuffer("text");
            var result = TextTools.isBlank(cs);
            assertFalse(result);
        }

        @Test
        @DisplayName("isBlank(CharSequence) should handle non-String empty CharSequence")
        void isBlankShouldHandleNonStringEmptyCharSequence() {
            CharSequence cs = new StringBuffer();
            var result = TextTools.isBlank(cs);
            assertTrue(result);
        }

        @Test
        @DisplayName("should optimize for String instances")
        void shouldOptimizeForStringInstances() {
            String str = "  ";
            var result = TextTools.isBlank(str);
            assertTrue(result);
        }

        @ParameterizedTest
        @ValueSource(strings = {"a", "text", " text ", "  text  ", "\ttext\n", "123"})
        @DisplayName("should return false for non-blank strings")
        void shouldReturnFalseForNonBlankStrings(String input) {
            var result = TextTools.isBlank(input);
            assertFalse(result);
        }

        @ParameterizedTest
        @NullSource
        @ValueSource(strings = {"", " ", "  ", "\t", "\n", "\r", " \t\n\r "})
        @DisplayName("should return true for null, empty, or whitespace-only strings")
        void shouldReturnTrueForBlankStrings(String input) {
            var result = TextTools.isBlank(input);
            assertTrue(result);
        }

        @Test
        @DisplayName("should work with blank StringBuilder")
        void shouldWorkWithBlankStringBuilder() {
            var sb = new StringBuilder("   ");
            var result = TextTools.isBlank(sb);
            assertTrue(result);
        }

        @Test
        @DisplayName("should work with StringBuilder")
        void shouldWorkWithStringBuilder() {
            var sb = new StringBuilder("text");
            var result = TextTools.isBlank(sb);
            assertFalse(result);
        }
    }

    @Nested
    @DisplayName("isBlank(CharSequence...) Tests")
    class IsBlankVarargsTests {

        @ParameterizedTest
        @ValueSource(strings = {"a", "text", " text ", "  text  ", "\ttext\n", "123"})
        @DisplayName("should return false when all strings are not blank")
        void shouldReturnFalseWhenAllStringsAreNotBlank(String input) {
            var result = TextTools.isBlank(input, "text", "more");
            assertFalse(result);
        }

        @ParameterizedTest
        @ValueSource(strings = {"a", "text", " text ", "  text  ", "\ttext\n", "123"})
        @DisplayName("should return false when at least one string is not blank")
        void shouldReturnFalseWhenAtLeastOneStringIsNotBlank(String input) {
            var result = TextTools.isBlank("", " ", input, "\t");
            assertFalse(result);
        }

        @Test
        @DisplayName("should return true for empty varargs array")
        void shouldReturnTrueForEmptyVarargsArray() {
            var result = TextTools.isBlank();
            assertTrue(result);
        }

        @ParameterizedTest
        @NullSource
        @DisplayName("should return true for null array")
        @SuppressWarnings({"PMD.UseVarargs"})
        void shouldReturnTrueForNullArray(CharSequence[] input) {
            var result = TextTools.isBlank(input);
            assertTrue(result);
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " ", "  ", "\t", "\n", "\r", " \t\n\r "})
        @DisplayName("should return true when all strings are blank")
        void shouldReturnTrueWhenAllStringsAreBlank(String input) {
            var result = TextTools.isBlank(input, "", "  ", "\t");
            assertTrue(result);
        }

        @Test
        @DisplayName("should work with mixed CharSequence types")
        void shouldWorkWithMixedCharSequenceTypes() {
            var sb = new StringBuilder("text");
            var buff = new StringBuffer("data");
            var result = TextTools.isBlank(sb, buff, "more");
            assertFalse(result);
        }
    }

    @Nested
    @DisplayName("isEmpty(Object...) Tests")
    class IsEmptyObjectTests {

        @Test
        @DisplayName("isEmpty(Object...) should continue when CharSequence is empty")
        @SuppressWarnings("PMD.UnnecessaryVarargsArrayCreation")
        void isEmptyObjectArrayShouldContinueWhenCharSequenceIsEmpty() {
            CharSequence cs = new StringBuffer();
            var result = TextTools.isEmpty(new Object[]{cs, "", null});
            assertTrue(result);
        }

        @Test
        @DisplayName("isEmpty(Object...) should continue when toString is empty")
        void isEmptyObjectArrayShouldContinueWhenToStringIsEmpty() {
            var obj = new Object() {
                @Override
                public String toString() {
                    return "";
                }
            };
            var result = TextTools.isEmpty(obj, null);
            assertTrue(result);
        }

        @Test
        @DisplayName("should avoid toString for CharSequence objects")
        void shouldAvoidToStringForCharSequenceObjects() {
            var sb = new StringBuilder("text");
            var result = TextTools.isEmpty((Object) sb);
            assertFalse(result);
        }

        @Test
        @DisplayName("should handle mixed CharSequence and non-CharSequence objects")
        void shouldHandleMixedObjects() {
            var sb = new StringBuilder();
            var result = TextTools.isEmpty(sb, null);
            assertTrue(result);
        }

        @ParameterizedTest
        @ValueSource(strings = {" ", "  ", "\t", "\n", "text", " text ", "a"})
        @DisplayName("should return false for non-empty string objects")
        void shouldReturnFalseForNonEmptyStringObjects(String input) {
            var result = TextTools.isEmpty((Object) input);
            assertFalse(result);
        }

        @ParameterizedTest
        @ValueSource(ints = {0, 1, 42, -1})
        @DisplayName("should return false for non-null objects")
        void shouldReturnFalseForNonNullObjects(Integer input) {
            var result = TextTools.isEmpty(input);
            assertFalse(result);
        }

        @ParameterizedTest
        @ValueSource(ints = {0, 1, 42, -1})
        @DisplayName("should return false when all objects are not empty")
        void shouldReturnFalseWhenAllObjectsAreNotEmpty(Integer input) {
            var result = TextTools.isEmpty(input, 123, " ");
            assertFalse(result);
        }

        @ParameterizedTest
        @ValueSource(strings = {" ", "  ", "\t", "\n", "text", " text ", "a"})
        @DisplayName("should return false when at least one object is not empty")
        void shouldReturnFalseWhenAtLeastOneObjectIsNotEmpty(String input) {
            var result = TextTools.isEmpty("", null, input);
            assertFalse(result);
        }

        @ParameterizedTest
        @ValueSource(strings = {""})
        @DisplayName("should return true for empty string object")
        void shouldReturnTrueForEmptyStringObject(String input) {
            var result = TextTools.isEmpty((Object) input);
            assertTrue(result);
        }

        @Test
        @DisplayName("should return true for empty varargs array")
        void shouldReturnTrueForEmptyVarargsArray() {
            var result = TextTools.isEmpty(new Object[0]);
            assertTrue(result);
        }

        @ParameterizedTest
        @NullSource
        @DisplayName("should return true for null array")
        @SuppressWarnings("PMD.UseVarargs")
        void shouldReturnTrueForNullArray(Object[] input) {
            var result = TextTools.isEmpty(input);
            assertTrue(result);
        }

        @ParameterizedTest
        @NullSource
        @DisplayName("should return true for null object")
        void shouldReturnTrueForNullObject(Object input) {
            var result = TextTools.isEmpty(input);
            assertTrue(result);
        }

        @ParameterizedTest
        @ValueSource(strings = {""})
        @DisplayName("should return true when all objects are empty or null")
        void shouldReturnTrueWhenAllObjectsAreEmpty(String input) {
            var result = TextTools.isEmpty(input, "", null);
            assertTrue(result);
        }
    }

    @Nested
    @DisplayName("isEmpty(CharSequence) Tests")
    class IsEmptyTests {

        @Test
        @DisplayName("should optimize for String instances")
        void shouldOptimizeForStringInstances() {
            String str = "";
            var result = TextTools.isEmpty(str);
            assertTrue(result);
        }

        @ParameterizedTest
        @ValueSource(strings = {" ", "  ", "\t", "\n", "text", " text ", "a"})
        @DisplayName("should return false for non-empty strings")
        void shouldReturnFalseForNonEmptyStrings(String input) {
            var result = TextTools.isEmpty(input);
            assertFalse(result);
        }

        @ParameterizedTest
        @NullSource
        @ValueSource(strings = {""})
        @DisplayName("should return true for null or empty strings")
        void shouldReturnTrueForEmptyStrings(String input) {
            var result = TextTools.isEmpty(input);
            assertTrue(result);
        }

        @Test
        @DisplayName("should work with empty StringBuilder")
        void shouldWorkWithEmptyStringBuilder() {
            var sb = new StringBuilder();
            var result = TextTools.isEmpty(sb);
            assertTrue(result);
        }

        @Test
        @DisplayName("should work with StringBuilder")
        void shouldWorkWithStringBuilder() {
            var sb = new StringBuilder("text");
            var result = TextTools.isEmpty(sb);
            assertFalse(result);
        }
    }

    @Nested
    @DisplayName("isEmpty(CharSequence...) Tests")
    class IsEmptyVarargsTests {

        @ParameterizedTest
        @ValueSource(strings = {" ", "  ", "\t", "\n", "text", " text ", "a"})
        @DisplayName("should return false when all strings are not empty")
        void shouldReturnFalseWhenAllStringsAreNotEmpty(String input) {
            var result = TextTools.isEmpty(input, "text", " ");
            assertFalse(result);
        }

        @ParameterizedTest
        @ValueSource(strings = {" ", "  ", "\t", "\n", "text", " text ", "a"})
        @DisplayName("should return false when at least one string is not empty")
        void shouldReturnFalseWhenAtLeastOneStringIsNotEmpty(String input) {
            var result = TextTools.isEmpty("", null, input);
            assertFalse(result);
        }

        @Test
        @DisplayName("should return true for empty varargs array")
        void shouldReturnTrueForEmptyVarargsArray() {
            var result = TextTools.isEmpty();
            assertTrue(result);
        }

        @ParameterizedTest
        @NullSource
        @DisplayName("should return true for null array")
        @SuppressWarnings({"PMD.UseVarargs"})
        void shouldReturnTrueForNullArray(CharSequence[] input) {
            var result = TextTools.isEmpty(input);
            assertTrue(result);
        }

        @ParameterizedTest
        @ValueSource(strings = {""})
        @DisplayName("should return true when all strings are empty or null")
        void shouldReturnTrueWhenAllStringsAreEmpty(String input) {
            var result = TextTools.isEmpty(input, "", null);
            assertTrue(result);
        }

        @Test
        @DisplayName("should work with mixed CharSequence types")
        void shouldWorkWithMixedCharSequenceTypes() {
            var sb = new StringBuilder();
            var buff = new StringBuffer();
            var result = TextTools.isEmpty(sb, buff, "");
            assertTrue(result);
        }
    }

    @Nested
    @DisplayName("isNotBlank(Object...) Tests")
    class IsNotBlankObjectTests {

        @Test
        @DisplayName("isNotBlank(Object...) should return false when CharSequence is blank")
        @SuppressWarnings("PMD.UnnecessaryVarargsArrayCreation")
        void isNotBlankObjectArrayShouldReturnFalseForBlankCharSequence() {
            CharSequence cs = new StringBuffer("  ");
            var result = TextTools.isNotBlank(new Object[]{"text", cs});
            assertFalse(result);
        }

        @Test
        @DisplayName("isNotBlank(Object...) should return false when toString is blank")
        void isNotBlankObjectArrayShouldReturnFalseForBlankToString() {
            var obj = new Object() {
                @Override
                public String toString() {
                    return "  ";
                }
            };
            var result = TextTools.isNotBlank("text", obj);
            assertFalse(result);
        }

        @Test
        @DisplayName("should avoid toString for CharSequence objects")
        void shouldAvoidToStringForCharSequenceObjects() {
            var sb = new StringBuilder("text");
            var result = TextTools.isNotBlank(sb);
            assertTrue(result);
        }

        @Test
        @DisplayName("should handle mixed CharSequence and non-CharSequence objects")
        void shouldHandleMixedObjects() {
            var sb = new StringBuilder("data");
            var result = TextTools.isNotBlank(sb, 42, "text");
            assertTrue(result);
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " ", "  ", "\t", "\n", "\r", " \t\n\r "})
        @DisplayName("should return false for empty or whitespace-only string objects")
        void shouldReturnFalseForBlankStringObjects(String input) {
            var result = TextTools.isNotBlank((Object) input);
            assertFalse(result);
        }

        @Test
        @DisplayName("should return false for empty varargs array")
        void shouldReturnFalseForEmptyVarargsArray() {
            var result = TextTools.isNotBlank(new Object[0]);
            assertFalse(result);
        }

        @ParameterizedTest
        @NullSource
        @DisplayName("should return false for null array")
        @SuppressWarnings("PMD.UseVarargs")
        void shouldReturnFalseForNullArray(Object[] input) {
            var result = TextTools.isNotBlank(input);
            assertFalse(result);
        }

        @ParameterizedTest
        @NullSource
        @DisplayName("should return false for null object")
        void shouldReturnFalseForNullObject(Object input) {
            var result = TextTools.isNotBlank(input);
            assertFalse(result);
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " ", "  ", "\t", "\n", "\r", " \t\n\r "})
        @DisplayName("should return false when all objects are blank")
        void shouldReturnFalseWhenAllObjectsAreBlank(String input) {
            var result = TextTools.isNotBlank(input, "", "  ", "\t");
            assertFalse(result);
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " ", "  ", "\t", "\n", "\r", " \t\n\r "})
        @DisplayName("should return false when at least one object is blank")
        void shouldReturnFalseWhenAtLeastOneObjectIsBlank(String input) {
            var result = TextTools.isNotBlank(123, "text", input, "data");
            assertFalse(result);
        }

        @ParameterizedTest
        @ValueSource(ints = {0, 1, 42, -1})
        @DisplayName("should return true for non-null non-blank objects")
        void shouldReturnTrueForNonBlankObjects(Integer input) {
            var result = TextTools.isNotBlank(input);
            assertTrue(result);
        }

        @ParameterizedTest
        @ValueSource(strings = {"a", "text", " text ", "  text  ", "\ttext\n", "123"})
        @DisplayName("should return true for non-blank string objects")
        void shouldReturnTrueForNonBlankStringObjects(String input) {
            var result = TextTools.isNotBlank((Object) input);
            assertTrue(result);
        }

        @ParameterizedTest
        @ValueSource(strings = {"a", "text", " text ", "  text  ", "\ttext\n", "123"})
        @DisplayName("should return true when all objects are not blank")
        void shouldReturnTrueWhenAllObjectsAreNotBlank(String input) {
            var result = TextTools.isNotBlank(input, "text", 42);
            assertTrue(result);
        }
    }

    @Nested
    @DisplayName("isNotBlank(CharSequence) Tests")
    class IsNotBlankTests {

        @ParameterizedTest
        @NullSource
        @ValueSource(strings = {"", " ", "  ", "\t", "\n", "\r", " \t\n\r "})
        @DisplayName("should return false for null, empty, or whitespace-only strings")
        void shouldReturnFalseForBlankStrings(String input) {
            var result = TextTools.isNotBlank(input);
            assertFalse(result);
        }

        @ParameterizedTest
        @ValueSource(strings = {"a", "text", " text ", "  text  ", "\ttext\n", "123"})
        @DisplayName("should return true for non-blank strings")
        void shouldReturnTrueForNonBlankStrings(String input) {
            var result = TextTools.isNotBlank(input);
            assertTrue(result);
        }

        @Test
        @DisplayName("should work with blank StringBuilder")
        void shouldWorkWithBlankStringBuilder() {
            var sb = new StringBuilder("   ");
            var result = TextTools.isNotBlank(sb);
            assertFalse(result);
        }

        @Test
        @DisplayName("should work with StringBuilder")
        void shouldWorkWithStringBuilder() {
            var sb = new StringBuilder("text");
            var result = TextTools.isNotBlank(sb);
            assertTrue(result);
        }
    }

    @Nested
    @DisplayName("isNotBlank(CharSequence...) Tests")
    class IsNotBlankVarargsTests {

        @Test
        @DisplayName("should return false for empty varargs array")
        void shouldReturnFalseForEmptyVarargsArray() {
            var result = TextTools.isNotBlank();
            assertFalse(result);
        }

        @ParameterizedTest
        @NullSource
        @DisplayName("should return false for null array")
        @SuppressWarnings({"PMD.UseVarargs"})
        void shouldReturnFalseForNullArray(CharSequence[] input) {
            var result = TextTools.isNotBlank(input);
            assertFalse(result);
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " ", "  ", "\t", "\n", "\r", " \t\n\r "})
        @DisplayName("should return false when all strings are blank")
        void shouldReturnFalseWhenAllStringsAreBlank(String input) {
            var result = TextTools.isNotBlank(input, "", "  ", "\t");
            assertFalse(result);
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " ", "  ", "\t", "\n", "\r", " \t\n\r "})
        @DisplayName("should return false when at least one string is blank")
        void shouldReturnFalseWhenAtLeastOneStringIsBlank(String input) {
            var result = TextTools.isNotBlank("text", "more", input, "data");
            assertFalse(result);
        }

        @ParameterizedTest
        @ValueSource(strings = {"a", "text", " text ", "  text  ", "\ttext\n", "123"})
        @DisplayName("should return true when all strings are not blank")
        void shouldReturnTrueWhenAllStringsAreNotBlank(String input) {
            var result = TextTools.isNotBlank(input, "text", "more");
            assertTrue(result);
        }

        @Test
        @DisplayName("should work with mixed CharSequence types")
        void shouldWorkWithMixedCharSequenceTypes() {
            var sb = new StringBuilder("text");
            var buff = new StringBuffer("data");
            var result = TextTools.isNotBlank(sb, buff, "more");
            assertTrue(result);
        }
    }

    @Nested
    @DisplayName("isNotEmpty(Object...) Tests")
    class IsNotEmptyObjectTests {

        @Test
        @DisplayName("isNotEmpty(Object...) should return false when CharSequence is empty")
        @SuppressWarnings("PMD.UnnecessaryVarargsArrayCreation")
        void isNotEmptyObjectArrayShouldReturnFalseForEmptyCharSequence() {
            CharSequence cs = new StringBuffer();
            var result = TextTools.isNotEmpty(new Object[]{"text", cs});
            assertFalse(result);
        }

        @Test
        @DisplayName("isNotEmpty(Object...) should return false when toString is empty")
        void isNotEmptyObjectArrayShouldReturnFalseForEmptyToString() {
            var obj = new Object() {
                @Override
                public String toString() {
                    return "";
                }
            };
            var result = TextTools.isNotEmpty("text", obj);
            assertFalse(result);
        }

        @Test
        @DisplayName("should avoid toString for CharSequence objects")
        void shouldAvoidToStringForCharSequenceObjects() {
            var sb = new StringBuilder("text");
            var result = TextTools.isNotEmpty(sb);
            assertTrue(result);
        }

        @Test
        @DisplayName("should handle mixed CharSequence and non-CharSequence objects")
        void shouldHandleMixedObjects() {
            var sb = new StringBuilder("data");
            var result = TextTools.isNotEmpty(sb, 42, " ");
            assertTrue(result);
        }

        @ParameterizedTest
        @ValueSource(strings = {""})
        @DisplayName("should return false for empty string object")
        void shouldReturnFalseForEmptyStringObject(String input) {
            var result = TextTools.isNotEmpty((Object) input);
            assertFalse(result);
        }

        @Test
        @DisplayName("should return false for empty varargs array")
        void shouldReturnFalseForEmptyVarargsArray() {
            var result = TextTools.isNotEmpty(new Object[0]);
            assertFalse(result);
        }

        @ParameterizedTest
        @NullSource
        @DisplayName("should return false for null array")
        @SuppressWarnings("PMD.UseVarargs")
        void shouldReturnFalseForNullArray(Object[] input) {
            var result = TextTools.isNotEmpty(input);
            assertFalse(result);
        }

        @ParameterizedTest
        @NullSource
        @DisplayName("should return false for null object")
        void shouldReturnFalseForNullObject(Object input) {
            var result = TextTools.isNotEmpty(input);
            assertFalse(result);
        }

        @ParameterizedTest
        @ValueSource(strings = {""})
        @DisplayName("should return false when all objects are empty or null")
        void shouldReturnFalseWhenAllObjectsAreEmpty(String input) {
            var result = TextTools.isNotEmpty(input, "", null);
            assertFalse(result);
        }

        @ParameterizedTest
        @ValueSource(strings = {""})
        @DisplayName("should return false when at least one object is empty or null")
        void shouldReturnFalseWhenAtLeastOneObjectIsEmpty(String input) {
            var result = TextTools.isNotEmpty("text", " ", input, "data");
            assertFalse(result);
        }

        @ParameterizedTest
        @ValueSource(strings = {" ", "  ", "\t", "\n", "text", " text ", "a"})
        @DisplayName("should return true for non-empty string objects")
        void shouldReturnTrueForNonEmptyStringObjects(String input) {
            var result = TextTools.isNotEmpty((Object) input);
            assertTrue(result);
        }

        @ParameterizedTest
        @ValueSource(ints = {0, 1, 42, -1})
        @DisplayName("should return true for non-null objects")
        void shouldReturnTrueForNonNullObjects(Integer input) {
            var result = TextTools.isNotEmpty(input);
            assertTrue(result);
        }

        @ParameterizedTest
        @ValueSource(strings = {" ", "  ", "\t", "\n", "text", " text ", "a"})
        @DisplayName("should return true when all objects are not empty")
        void shouldReturnTrueWhenAllObjectsAreNotEmpty(String input) {
            var result = TextTools.isNotEmpty(input, "text", " ");
            assertTrue(result);
        }
    }

    @Nested
    @DisplayName("isNotEmpty(CharSequence) Tests")
    class IsNotEmptyTests {

        @ParameterizedTest
        @NullSource
        @ValueSource(strings = {""})
        @DisplayName("should return false for null or empty strings")
        void shouldReturnFalseForEmptyStrings(String input) {
            var result = TextTools.isNotEmpty(input);
            assertFalse(result);
        }

        @ParameterizedTest
        @ValueSource(strings = {" ", "  ", "\t", "\n", "text", " text ", "a"})
        @DisplayName("should return true for non-empty strings")
        void shouldReturnTrueForNonEmptyStrings(String input) {
            var result = TextTools.isNotEmpty(input);
            assertTrue(result);
        }

        @Test
        @DisplayName("should work with empty StringBuilder")
        void shouldWorkWithEmptyStringBuilder() {
            var sb = new StringBuilder();
            var result = TextTools.isNotEmpty(sb);
            assertFalse(result);
        }

        @Test
        @DisplayName("should work with StringBuilder")
        void shouldWorkWithStringBuilder() {
            var sb = new StringBuilder("text");
            var result = TextTools.isNotEmpty(sb);
            assertTrue(result);
        }
    }

    @Nested
    @DisplayName("isNotEmpty(CharSequence...) Tests")
    class IsNotEmptyVarargsTests {

        @Test
        @DisplayName("should return false for empty varargs array")
        void shouldReturnFalseForEmptyVarargsArray() {
            var result = TextTools.isNotEmpty();
            assertFalse(result);
        }

        @ParameterizedTest
        @NullSource
        @DisplayName("should return false for null array")
        @SuppressWarnings({"PMD.UseVarargs"})
        void shouldReturnFalseForNullArray(CharSequence[] input) {
            var result = TextTools.isNotEmpty(input);
            assertFalse(result);
        }

        @ParameterizedTest
        @ValueSource(strings = {""})
        @DisplayName("should return false when all strings are empty or null")
        void shouldReturnFalseWhenAllStringsAreEmpty(String input) {
            var result = TextTools.isNotEmpty(input, "", null);
            assertFalse(result);
        }

        @ParameterizedTest
        @ValueSource(strings = {""})
        @DisplayName("should return false when at least one string is empty or null")
        void shouldReturnFalseWhenAtLeastOneStringIsEmpty(String input) {
            var result = TextTools.isNotEmpty("text", " ", input, "data");
            assertFalse(result);
        }

        @ParameterizedTest
        @ValueSource(strings = {" ", "  ", "\t", "\n", "text", " text ", "a"})
        @DisplayName("should return true when all strings are not empty")
        void shouldReturnTrueWhenAllStringsAreNotEmpty(String input) {
            var result = TextTools.isNotEmpty(input, "text", " ");
            assertTrue(result);
        }

        @Test
        @DisplayName("should work with mixed CharSequence types")
        void shouldWorkWithMixedCharSequenceTypes() {
            var sb = new StringBuilder("text");
            var buff = new StringBuffer(" ");
            var result = TextTools.isNotEmpty(sb, buff, "data");
            assertTrue(result);
        }
    }

}