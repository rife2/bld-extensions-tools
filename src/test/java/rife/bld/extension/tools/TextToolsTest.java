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
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Text Tools Tests")
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
class TextToolsTest {

    @Nested
    @DisplayName("equalsIgnoreWhitespace Tests")
    class EqualsIgnoreWhitespaceTests {

        @Test
        @DisplayName("should return false for empty varargs array")
        void shouldReturnFalseForEmptyVarargsArray() {
            assertFalse(TextTools.equalsIgnoreWhitespace());
        }

        @Test
        @DisplayName("should return false for null array")
        void shouldReturnFalseForNullArray() {
            assertFalse(TextTools.equalsIgnoreWhitespace((CharSequence[]) null));
        }

        @Test
        @DisplayName("should return false for single argument")
        void shouldReturnFalseForSingleArgument() {
            assertFalse(TextTools.equalsIgnoreWhitespace("text"));
        }

        @Test
        @DisplayName("should return false when strings differ in content")
        void shouldReturnFalseWhenContentDiffers() {
            var result = TextTools.equalsIgnoreWhitespace("hello", "world");
            assertFalse(result);
        }

        @Test
        @DisplayName("should return false when strings differ in content despite same whitespace")
        void shouldReturnFalseWhenContentDiffersWithSameWhitespace() {
            var result = TextTools.equalsIgnoreWhitespace("hello world", "hello earth");
            assertFalse(result);
        }

        @Test
        @DisplayName("should return false when first string is null")
        void shouldReturnFalseWhenFirstIsNull() {
            assertFalse(TextTools.equalsIgnoreWhitespace(null, "text"));
        }

        @Test
        @DisplayName("should return false when one of multiple strings differs")
        void shouldReturnFalseWhenOneOfMultipleDiffers() {
            assertFalse(TextTools.equalsIgnoreWhitespace("hello world", "helloworld", "hello earth"));
        }

        @Test
        @DisplayName("should return false when second string is null")
        void shouldReturnFalseWhenSecondIsNull() {
            assertFalse(TextTools.equalsIgnoreWhitespace("text", null));
        }

        @Test
        @DisplayName("should return true for empty and whitespace-only strings")
        void shouldReturnTrueForEmptyAndWhitespaceOnly() {
            var result = TextTools.equalsIgnoreWhitespace("", " ");
            assertTrue(result);
        }

        @Test
        @DisplayName("should return true when all strings are equal ignoring whitespace")
        void shouldReturnTrueWhenAllEqualIgnoringWhitespace() {
            assertTrue(TextTools.equalsIgnoreWhitespace("hello world", "hello world", "helloworld"));
        }

        @Test
        @DisplayName("should return true when all strings are null or whitespace-only")
        void shouldReturnTrueWhenAllNullOrWhitespace() {
            assertTrue(TextTools.equalsIgnoreWhitespace(null, " ", "\t\n"));
        }

        @Test
        @DisplayName("should return true when both strings are empty")
        void shouldReturnTrueWhenBothEmpty() {
            var result = TextTools.equalsIgnoreWhitespace("", "");
            assertTrue(result);
        }

        @Test
        @DisplayName("should return true when both strings are null")
        void shouldReturnTrueWhenBothNull() {
            assertTrue(TextTools.equalsIgnoreWhitespace(null, null));
        }

        @Test
        @DisplayName("should return true when both strings are whitespace-only")
        void shouldReturnTrueWhenBothWhitespaceOnly() {
            var result = TextTools.equalsIgnoreWhitespace(" ", "\t\n\r");
            assertTrue(result);
        }

        @Test
        @DisplayName("should return true when strings differ only in line separators")
        void shouldReturnTrueWhenDifferingOnlyInLineSeparators() {
            var result = TextTools.equalsIgnoreWhitespace("hello\nworld", "hello\r\nworld");
            assertTrue(result);
        }

        @Test
        @DisplayName("should return true when strings differ only in tabs and spaces")
        void shouldReturnTrueWhenDifferingOnlyInTabsAndSpaces() {
            var result = TextTools.equalsIgnoreWhitespace("hello\tworld", "hello world");
            assertTrue(result);
        }

        @Test
        @DisplayName("should return true when strings are equal ignoring whitespace")
        void shouldReturnTrueWhenEqualIgnoringWhitespace() {
            var result = TextTools.equalsIgnoreWhitespace("hello world", "helloworld");
            assertTrue(result);
        }

        @ParameterizedTest
        @ValueSource(strings = {"abc", "hello world", "foo\tbar", "line1\nline2"})
        @DisplayName("should return true when strings are identical")
        void shouldReturnTrueWhenIdentical(String input) {
            var result = TextTools.equalsIgnoreWhitespace(input, input);
            assertTrue(result);
        }

        @Test
        @DisplayName("should work with mixed CharSequence types")
        void shouldWorkWithMixedCharSequenceTypes() {
            var sb = new StringBuilder("hello world");
            var buff = new StringBuffer("helloworld");
            var result = TextTools.equalsIgnoreWhitespace(sb, buff);
            assertTrue(result);
        }

        @Test
        @DisplayName("should work with StringBuilder instances")
        void shouldWorkWithStringBuilder() {
            var sb1 = new StringBuilder("hello world");
            var sb2 = new StringBuilder("helloworld");
            var result = TextTools.equalsIgnoreWhitespace(sb1, sb2);
            assertTrue(result);
        }
    }

    @Nested
    @DisplayName("isBlank(Object...) Tests")
    class IsBlankObjectTests {

        @Test
        @DisplayName("isBlank(Object...) should continue when CharSequence is blank")
        @SuppressWarnings("PMD.UnnecessaryVarargsArrayCreation")
        void isBlankObjectArrayShouldContinueWhenCharSequenceIsBlank() {
            CharSequence cs = new StringBuffer(" ");
            var result = TextTools.isBlank(new Object[]{cs, "", " "});
            assertTrue(result);
        }

        @Test
        @DisplayName("isBlank(Object...) should continue when toString is blank")
        void isBlankObjectArrayShouldContinueWhenToStringIsBlank() {
            var obj = new Object() {
                @Override
                public String toString() {
                    return " ";
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
        @ValueSource(strings = {"a", "text", " text ", " text ", "\ttext\n", "123"})
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
        @ValueSource(strings = {"a", "text", " text ", " text ", "\ttext\n", "123"})
        @DisplayName("should return false when at least one object is not blank")
        void shouldReturnFalseWhenAtLeastOneObjectIsNotBlank(String input) {
            var result = TextTools.isBlank("", " ", input, "\t");
            assertFalse(result);
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " ", " ", "\t", "\n", "\r", " \t\n\r "})
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
        @ValueSource(strings = {"", " ", " ", "\t", "\n", "\r", " \t\n\r "})
        @DisplayName("should return true when all objects are blank")
        void shouldReturnTrueWhenAllObjectsAreBlank(String input) {
            var result = TextTools.isBlank(input, "", " ", "\t");
            assertTrue(result);
        }
    }

    @Nested
    @DisplayName("isBlank(CharSequence) Tests")
    class IsBlankTests {

        @Test
        @DisplayName("isBlank(CharSequence) should handle non-String blank CharSequence")
        void isBlankShouldHandleNonStringBlankCharSequence() {
            CharSequence cs = new StringBuffer(" ");
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
            String str = " ";
            var result = TextTools.isBlank(str);
            assertTrue(result);
        }

        @ParameterizedTest
        @ValueSource(strings = {"a", "text", " text ", " text ", "\ttext\n", "123"})
        @DisplayName("should return false for non-blank strings")
        void shouldReturnFalseForNonBlankStrings(String input) {
            var result = TextTools.isBlank(input);
            assertFalse(result);
        }

        @ParameterizedTest
        @NullSource
        @ValueSource(strings = {"", " ", " ", "\t", "\n", "\r", " \t\n\r "})
        @DisplayName("should return true for null, empty, or whitespace-only strings")
        void shouldReturnTrueForBlankStrings(String input) {
            var result = TextTools.isBlank(input);
            assertTrue(result);
        }

        @Test
        @DisplayName("should work with blank StringBuilder")
        void shouldWorkWithBlankStringBuilder() {
            var sb = new StringBuilder(" ");
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
        @ValueSource(strings = {"a", "text", " text ", " text ", "\ttext\n", "123"})
        @DisplayName("should return false when all strings are not blank")
        void shouldReturnFalseWhenAllStringsAreNotBlank(String input) {
            var result = TextTools.isBlank(input, "text", "more");
            assertFalse(result);
        }

        @ParameterizedTest
        @ValueSource(strings = {"a", "text", " text ", " text ", "\ttext\n", "123"})
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
        @ValueSource(strings = {"", " ", " ", "\t", "\n", "\r", " \t\n\r "})
        @DisplayName("should return true when all strings are blank")
        void shouldReturnTrueWhenAllStringsAreBlank(String input) {
            var result = TextTools.isBlank(input, "", " ", "\t");
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
        @ValueSource(strings = {" ", " ", "\t", "\n", "text", " text ", "a"})
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
        @ValueSource(strings = {" ", " ", "\t", "\n", "text", " text ", "a"})
        @DisplayName("should return false when all objects are not empty")
        void shouldReturnFalseWhenAllObjectsAreNotEmpty(String input) {
            var result = TextTools.isEmpty(input, "text", " ");
            assertFalse(result);
        }

        @ParameterizedTest
        @ValueSource(strings = {" ", " ", "\t", "\n", "text", " text ", "a"})
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
        @ValueSource(strings = {" ", " ", "\t", "\n", "text", " text ", "a"})
        @DisplayName("should return false for non-empty strings")
        void shouldReturnFalseForNonEmptyStrings(String input) {
            var result = TextTools.isEmpty(input);
            assertFalse(result);
        }

        @ParameterizedTest
        @NullAndEmptySource
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
        @ValueSource(strings = {" ", " ", "\t", "\n", "text", " text ", "a"})
        @DisplayName("should return false when all strings are not empty")
        void shouldReturnFalseWhenAllStringsAreNotEmpty(String input) {
            var result = TextTools.isEmpty(input, "text", " ");
            assertFalse(result);
        }

        @ParameterizedTest
        @ValueSource(strings = {" ", " ", "\t", "\n", "text", " text ", "a"})
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
            CharSequence cs = new StringBuffer(" ");
            var result = TextTools.isNotBlank(new Object[]{"text", cs});
            assertFalse(result);
        }

        @Test
        @DisplayName("isNotBlank(Object...) should return false when toString is blank")
        void isNotBlankObjectArrayShouldReturnFalseForBlankToString() {
            var obj = new Object() {
                @Override
                public String toString() {
                    return " ";
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
        @ValueSource(strings = {"", " ", " ", "\t", "\n", "\r", " \t\n\r "})
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
        @ValueSource(strings = {"", " ", " ", "\t", "\n", "\r", " \t\n\r "})
        @DisplayName("should return false when all objects are blank")
        void shouldReturnFalseWhenAllObjectsAreBlank(String input) {
            var result = TextTools.isNotBlank(input, "", " ", "\t");
            assertFalse(result);
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " ", " ", "\t", "\n", "\r", " \t\n\r "})
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
        @ValueSource(strings = {"a", "text", " text ", " text ", "\ttext\n", "123"})
        @DisplayName("should return true for non-blank string objects")
        void shouldReturnTrueForNonBlankStringObjects(String input) {
            var result = TextTools.isNotBlank((Object) input);
            assertTrue(result);
        }

        @ParameterizedTest
        @ValueSource(strings = {"a", "text", " text ", " text ", "\ttext\n", "123"})
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
        @ValueSource(strings = {"", " ", " ", "\t", "\n", "\r", " \t\n\r "})
        @DisplayName("should return false for null, empty, or whitespace-only strings")
        void shouldReturnFalseForBlankStrings(String input) {
            var result = TextTools.isNotBlank(input);
            assertFalse(result);
        }

        @ParameterizedTest
        @ValueSource(strings = {"a", "text", " text ", " text ", "\ttext\n", "123"})
        @DisplayName("should return true for non-blank strings")
        void shouldReturnTrueForNonBlankStrings(String input) {
            var result = TextTools.isNotBlank(input);
            assertTrue(result);
        }

        @Test
        @DisplayName("should work with blank StringBuilder")
        void shouldWorkWithBlankStringBuilder() {
            var sb = new StringBuilder(" ");
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
        @ValueSource(strings = {"", " ", " ", "\t", "\n", "\r", " \t\n\r "})
        @DisplayName("should return false when all strings are blank")
        void shouldReturnFalseWhenAllStringsAreBlank(String input) {
            var result = TextTools.isNotBlank(input, "", " ", "\t");
            assertFalse(result);
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " ", " ", "\t", "\n", "\r", " \t\n\r "})
        @DisplayName("should return false when at least one string is blank")
        void shouldReturnFalseWhenAtLeastOneStringIsBlank(String input) {
            var result = TextTools.isNotBlank("text", "more", input, "data");
            assertFalse(result);
        }

        @ParameterizedTest
        @ValueSource(strings = {"a", "text", " text ", " text ", "\ttext\n", "123"})
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
        @ValueSource(strings = {" ", " ", "\t", "\n", "text", " text ", "a"})
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
        @ValueSource(strings = {" ", " ", "\t", "\n", "text", " text ", "a"})
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
        @NullAndEmptySource
        @DisplayName("should return false for null or empty strings")
        void shouldReturnFalseForEmptyStrings(String input) {
            var result = TextTools.isNotEmpty(input);
            assertFalse(result);
        }

        @ParameterizedTest
        @ValueSource(strings = {" ", " ", "\t", "\n", "text", " text ", "a"})
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
        @ValueSource(strings = {" ", " ", "\t", "\n", "text", " text ", "a"})
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

    @Nested
    @DisplayName("requireNotBlank(Collection<? extends CharSequence>, String) Tests")
    class RequireNotBlankCollectionContextTests {

        @Test
        @DisplayName("should fail fast on first blank element")
        void shouldFailFastOnFirstBlank() {
            var list = List.of(" ", "hello");
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> TextTools.requireNotBlank(list, "values"));
            assertEquals("values must not be empty or contain blank elements", ex.getMessage());
        }

        @Test
        @DisplayName("should fail fast on first null element")
        void shouldFailFastOnFirstNull() {
            var list = Arrays.asList(null, " ", "world");
            var ex = assertThrows(NullPointerException.class,
                    () -> TextTools.requireNotBlank(list, "values"));
            assertEquals("values must not be null", ex.getMessage());
        }

        @Test
        @DisplayName("should return the original collection when all elements valid")
        void shouldReturnOriginalCollection() {
            var list = List.of("hello", "world", "test");
            assertSame(list, TextTools.requireNotBlank(list, "values"));
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " ", " ", "\t"})
        @DisplayName("should throw IllegalArgumentException for blank context")
        void shouldThrowIllegalArgumentExceptionForBlankContext(String context) {
            var list = List.of("hello");
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> TextTools.requireNotBlank(list, context));
            assertEquals("context must not be blank", ex.getMessage());
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " ", " ", "\t", "\n", "\r", " \t\n\r "})
        @DisplayName("should throw IllegalArgumentException when collection contains blank element")
        void shouldThrowIllegalArgumentExceptionForBlankElement(String blank) {
            var list = List.of("hello", blank, "world");
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> TextTools.requireNotBlank(list, "values"));
            assertEquals("values must not be empty or contain blank elements", ex.getMessage());
        }

        @Test
        @DisplayName("should throw IllegalArgumentException for empty collection")
        void shouldThrowIllegalArgumentExceptionForEmptyCollection() {
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> TextTools.requireNotBlank(List.of(), "values"));
            assertEquals("values must not be empty or contain blank elements", ex.getMessage());
        }

        @Test
        @DisplayName("should throw NullPointerException for null collection")
        void shouldThrowNullPointerExceptionForNullCollection() {
            var ex = assertThrows(NullPointerException.class,
                    () -> TextTools.requireNotBlank((Collection<String>) null, "values"));
            assertEquals("values must not be null", ex.getMessage());
        }

        @Test
        @DisplayName("should throw NullPointerException for null context")
        @SuppressWarnings("DataFlowIssue")
        void shouldThrowNullPointerExceptionForNullContext() {
            var list = List.of("hello");
            var ex = assertThrows(NullPointerException.class,
                    () -> TextTools.requireNotBlank(list, null));
            assertEquals("context must not be null", ex.getMessage());
        }

        @Test
        @DisplayName("should throw NullPointerException when collection contains null element")
        void shouldThrowNullPointerExceptionForNullElement() {
            var list = Arrays.asList("hello", null, "world");
            var ex = assertThrows(NullPointerException.class,
                    () -> TextTools.requireNotBlank(list, "values"));
            assertEquals("values must not be null", ex.getMessage());
        }

        @Test
        @DisplayName("should work with Set and other Collection types")
        void shouldWorkWithSet() {
            var set = Set.of("alpha", "beta", "gamma");
            assertSame(set, TextTools.requireNotBlank(set, "values"));
        }

        @Test
        @DisplayName("should work with StringBuilder and other CharSequence types")
        void shouldWorkWithStringBuilder() {
            var list = List.of(new StringBuilder("hello"), new StringBuffer("world"));
            var result = TextTools.requireNotBlank(list, "values");
            assertSame(list, result);
        }
    }

    @Nested
    @DisplayName("requireNotBlank(Collection, Supplier<String>) Tests")
    class RequireNotBlankCollectionSupplierTests {

        @Test
        @DisplayName("should return the original collection when all elements valid")
        void shouldReturnOriginalCollection() {
            var list = List.of("hello", "world");
            var result = TextTools.requireNotBlank(list, () -> "null", () -> "empty");
            assertSame(list, result);
        }

        @Test
        @DisplayName("should throw IllegalArgumentException with supplier message for blank element")
        void shouldThrowIllegalArgumentExceptionForBlankElementWithSupplierMessage() {
            var list = List.of("hello", " ");
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> TextTools.requireNotBlank(list, () -> "null", () -> "custom blank msg"));
            assertEquals("custom blank msg", ex.getMessage());
        }

        @Test
        @DisplayName("should throw IllegalArgumentException with supplier message for empty collection")
        void shouldThrowIllegalArgumentExceptionWithSupplierMessage() {
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> TextTools.requireNotBlank(List.of(), () -> "null", () -> "custom empty msg"));
            assertEquals("custom empty msg", ex.getMessage());
        }

        @Test
        @DisplayName("should throw NullPointerException with supplier message for null element")
        void shouldThrowNullPointerExceptionForNullElementWithSupplierMessage() {
            var list = Arrays.asList("hello", null);
            var ex = assertThrows(NullPointerException.class,
                    () -> TextTools.requireNotBlank(list, () -> "custom null elem msg", () -> "blank"));
            assertEquals("custom null elem msg", ex.getMessage());
        }

        @Test
        @DisplayName("should throw NullPointerException with supplier message for null collection")
        void shouldThrowNullPointerExceptionWithSupplierMessage() {
            var ex = assertThrows(NullPointerException.class,
                    () -> TextTools.requireNotBlank((Collection<String>) null, () -> "custom null msg", () -> "blank"));
            assertEquals("custom null msg", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("requireNotBlank(CharSequence, String) Tests")
    class RequireNotBlankContextTests {

        @Test
        @DisplayName("should return the original CharSequence type")
        void shouldReturnOriginalCharSequenceType() {
            var sb = new StringBuilder("hello");
            assertSame(TextTools.requireNotBlank(sb, "value"), sb);
        }

        @Test
        @DisplayName("should return the string when not blank")
        void shouldReturnStringWhenNotBlank() {
            assertEquals("hello", TextTools.requireNotBlank("hello", "value"));
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " ", " ", "\t", "\n", "\r", " \t\n\r "})
        @DisplayName("should throw IllegalArgumentException for blank string")
        void shouldThrowIllegalArgumentExceptionForBlank(String input) {
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> TextTools.requireNotBlank(input, "value"));
            assertEquals("value must not be blank", ex.getMessage());
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " ", " ", "\t"})
        @DisplayName("should throw IllegalArgumentException for blank context")
        void shouldThrowIllegalArgumentExceptionForBlankContext(String context) {
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> TextTools.requireNotBlank("hello", context));
            assertEquals("context must not be blank", ex.getMessage());
        }

        @Test
        @DisplayName("should throw NullPointerException for null string")
        void shouldThrowNullPointerExceptionForNull() {
            var ex = assertThrows(NullPointerException.class,
                    () -> TextTools.requireNotBlank((String) null, "value"));
            assertEquals("value must not be null", ex.getMessage());
        }

        @Test
        @DisplayName("should throw NullPointerException for null context")
        @SuppressWarnings("DataFlowIssue")
        void shouldThrowNullPointerExceptionForNullContext() {
            var ex = assertThrows(NullPointerException.class,
                    () -> TextTools.requireNotBlank("hello", (String) null));
            assertEquals("context must not be null", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("requireNotBlank(CharSequence, Supplier<String>) Tests")
    class RequireNotBlankSupplierTests {

        @Test
        @DisplayName("should return the original CharSequence type")
        void shouldReturnOriginalCharSequenceType() {
            var sb = new StringBuilder("hello");
            assertSame(TextTools.requireNotBlank(sb, () -> "null", () -> "blank"), sb);
        }

        @Test
        @DisplayName("should return the string when not blank")
        void shouldReturnStringWhenNotBlank() {
            assertEquals("hello", TextTools.requireNotBlank("hello", () -> "null", () -> "blank"));
        }

        @Test
        @DisplayName("should throw IllegalArgumentException with supplier message for blank string")
        void shouldThrowIllegalArgumentExceptionWithSupplierMessage() {
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> TextTools.requireNotBlank(" ", () -> "null", () -> "custom blank msg"));
            assertEquals("custom blank msg", ex.getMessage());
        }

        @Test
        @DisplayName("should throw NullPointerException with supplier message for null string")
        void shouldThrowNullPointerExceptionWithSupplierMessage() {
            var ex = assertThrows(NullPointerException.class,
                    () -> TextTools.requireNotBlank((String) null, () -> "custom null msg", () -> "blank"));
            assertEquals("custom null msg", ex.getMessage());
        }

        @Test
        @DisplayName("should not call supplier on success")
        void supplierIsLazy() {
            var called = new boolean[]{false};
            TextTools.requireNotBlank("hello",
                    () -> {
                        called[0] = true;
                        return "null";
                    },
                    () -> {
                        called[0] = true;
                        return "blank";
                    });
            assertFalse(called[0]);
        }
    }

    @Nested
    @DisplayName("requireNotBlank(String, T...) Tests")
    class RequireNotBlankVarargsContextTests {

        @Test
        @DisplayName("should fail fast on first blank element")
        void shouldFailFastOnFirstBlank() {
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> TextTools.requireNotBlank("values", " ", "hello"));
            assertEquals("values must not be empty or contain blank elements", ex.getMessage());
        }

        @Test
        @DisplayName("should fail fast on first null element")
        void shouldFailFastOnFirstNull() {
            var ex = assertThrows(NullPointerException.class,
                    () -> TextTools.requireNotBlank("values", null, " ", "world"));
            assertEquals("values must not be null", ex.getMessage());
        }

        @Test
        @DisplayName("should return the original array when all elements valid")
        void shouldReturnOriginalArray() {
            var elements = new String[]{"hello", "world", "test"};
            var result = TextTools.requireNotBlank("values", elements);
            assertSame(elements, result);
        }

        @Test
        @DisplayName("should throw IllegalArgumentException for blank context")
        void shouldThrowIllegalArgumentExceptionForBlankContext() {
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> TextTools.requireNotBlank(" ", "hello"));
            assertEquals("hello must not be blank", ex.getMessage());
        }

        @Test
        @DisplayName("should throw IllegalArgumentException when array contains blank element")
        void shouldThrowIllegalArgumentExceptionForBlankElement() {
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> TextTools.requireNotBlank("values", "hello", " "));
            assertEquals("values must not be empty or contain blank elements", ex.getMessage());
        }

        @Test
        @DisplayName("should throw IllegalArgumentException for empty array")
        void shouldThrowIllegalArgumentExceptionForEmptyArray() {
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> TextTools.requireNotBlank("values"));
            assertEquals("values must not be empty or contain blank elements", ex.getMessage());
        }

        @Test
        @DisplayName("should throw NullPointerException for null array")
        void shouldThrowNullPointerExceptionForNullArray() {
            var ex = assertThrows(NullPointerException.class,
                    () -> TextTools.requireNotBlank("values", (String[]) null));
            assertEquals("values must not be null", ex.getMessage());
        }

        @Test
        @DisplayName("should throw IAE for null context")
        void shouldThrowNullPointerExceptionForNullContext() {
            var ex = assertThrows(NullPointerException.class,
                    () -> TextTools.requireNotBlank((String) null, "hello"));
            assertEquals("hello must not be null", ex.getMessage());
        }

        @Test
        @DisplayName("should throw NullPointerException when array contains null element")
        void shouldThrowNullPointerExceptionForNullElement() {
            var ex = assertThrows(NullPointerException.class,
                    () -> TextTools.requireNotBlank("values", "hello", null));
            assertEquals("values must not be null", ex.getMessage());
        }

        @Test
        @DisplayName("should work with StringBuilder and other CharSequence types")
        void shouldWorkWithStringBuilder() {
            var elements = new CharSequence[]{new StringBuilder("hello"), new StringBuffer("world")};
            var result = TextTools.requireNotBlank("values", elements);
            assertSame(elements, result);
        }
    }

    @Nested
    @DisplayName("requireNotBlank(Supplier<String>, T...) Tests")
    class RequireNotBlankVarargsSupplierTests {

        @Test
        @DisplayName("should fail fast on first blank element")
        void shouldFailFastOnFirstBlank() {
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> TextTools.requireNotBlank(() -> "null", () -> "blank", " ", "hello"));
            assertEquals("blank", ex.getMessage());
        }

        @Test
        @DisplayName("should fail fast on first null element")
        void shouldFailFastOnFirstNull() {
            var ex = assertThrows(NullPointerException.class,
                    () -> TextTools.requireNotBlank(() -> "null", () -> "blank", null, " ", "world"));
            assertEquals("null", ex.getMessage());
        }

        @Test
        @DisplayName("should return the original array when all elements valid")
        void shouldReturnOriginalArray() {
            var elements = new String[]{"hello", "world", "test"};
            var result = TextTools.requireNotBlank(() -> "null", () -> "blank", elements);
            assertSame(elements, result);
        }

        @Test
        @DisplayName("should throw IllegalArgumentException with supplier message for blank element")
        void shouldThrowIllegalArgumentExceptionForBlankElementWithSupplierMessage() {
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> TextTools.requireNotBlank(() -> "null", () -> "custom blank msg", "hello", " "));
            assertEquals("custom blank msg", ex.getMessage());
        }

        @Test
        @DisplayName("should throw IllegalArgumentException with supplier message for empty array")
        void shouldThrowIllegalArgumentExceptionWithSupplierMessage() {
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> TextTools.requireNotBlank(() -> "null", () -> "custom empty msg"));
            assertEquals("custom empty msg", ex.getMessage());
        }

        @Test
        @DisplayName("should throw NullPointerException with supplier message for null element")
        void shouldThrowNullPointerExceptionForNullElementWithSupplierMessage() {
            var ex = assertThrows(NullPointerException.class,
                    () -> TextTools.requireNotBlank(() -> "custom null elem msg", () -> "blank", "hello", null));
            assertEquals("custom null elem msg", ex.getMessage());
        }

        @Test
        @DisplayName("should throw NullPointerException with supplier message for null array")
        void shouldThrowNullPointerExceptionWithSupplierMessage() {
            var ex = assertThrows(NullPointerException.class,
                    () -> TextTools.requireNotBlank(() -> "custom null msg", () -> "blank", (String[]) null));
            assertEquals("custom null msg", ex.getMessage());
        }

        @Test
        @DisplayName("should work with StringBuilder and other CharSequence types")
        void shouldWorkWithStringBuilder() {
            var elements = new CharSequence[]{new StringBuilder("hello"), new StringBuffer("world")};
            var result = TextTools.requireNotBlank(() -> "null", () -> "blank", elements);
            assertSame(elements, result);
        }
    }

    @Nested
    @DisplayName("requireNotEmpty(Collection<? extends CharSequence>, String) Tests")
    class RequireNotEmptyCollectionContextTests {

        @Test
        @DisplayName("should allow whitespace-only elements")
        void shouldAllowWhitespaceOnlyElements() {
            var list = List.of("hello", " ", "\t", "world");
            assertSame(list, TextTools.requireNotEmpty(list, "values"));
        }

        @Test
        @DisplayName("should fail fast on first empty element")
        void shouldFailFastOnFirstEmpty() {
            var list = List.of("", "hello");
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> TextTools.requireNotEmpty(list, "values"));
            assertEquals("values must not be empty or contain empty elements", ex.getMessage());
        }

        @Test
        @DisplayName("should fail fast on first null element")
        void shouldFailFastOnFirstNull() {
            var list = Arrays.asList(null, "", "world");
            var ex = assertThrows(NullPointerException.class,
                    () -> TextTools.requireNotEmpty(list, "values"));
            assertEquals("values must not be null", ex.getMessage());
        }

        @Test
        @DisplayName("should return the original collection when all elements valid")
        void shouldReturnOriginalCollection() {
            var list = List.of("hello", " ", "world");
            assertSame(list, TextTools.requireNotEmpty(list, "values"));
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " ", " ", "\t"})
        @DisplayName("should throw IllegalArgumentException for blank context")
        void shouldThrowIllegalArgumentExceptionForBlankContext(String context) {
            var list = List.of("hello");
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> TextTools.requireNotEmpty(list, context));
            assertEquals("context must not be blank", ex.getMessage());
        }

        @Test
        @DisplayName("should throw IllegalArgumentException for empty collection")
        void shouldThrowIllegalArgumentExceptionForEmptyCollection() {
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> TextTools.requireNotEmpty(List.of(), "values"));
            assertEquals("values must not be empty or contain empty elements", ex.getMessage());
        }

        @Test
        @DisplayName("should throw IllegalArgumentException when collection contains empty element")
        void shouldThrowIllegalArgumentExceptionForEmptyElement() {
            var list = List.of("hello", "", "world");
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> TextTools.requireNotEmpty(list, "values"));
            assertEquals("values must not be empty or contain empty elements", ex.getMessage());
        }

        @Test
        @DisplayName("should throw NullPointerException for null collection")
        void shouldThrowNullPointerExceptionForNullCollection() {
            var ex = assertThrows(NullPointerException.class,
                    () -> TextTools.requireNotEmpty((Collection<String>) null, "values"));
            assertEquals("values must not be null", ex.getMessage());
        }

        @Test
        @DisplayName("should throw NullPointerException for null context")
        @SuppressWarnings("DataFlowIssue")
        void shouldThrowNullPointerExceptionForNullContext() {
            var list = List.of("hello");
            var ex = assertThrows(NullPointerException.class,
                    () -> TextTools.requireNotEmpty(list, null));
            assertEquals("context must not be null", ex.getMessage());
        }

        @Test
        @DisplayName("should throw NullPointerException when collection contains null element")
        void shouldThrowNullPointerExceptionForNullElement() {
            var list = Arrays.asList("hello", null, "world");
            var ex = assertThrows(NullPointerException.class,
                    () -> TextTools.requireNotEmpty(list, "values"));
            assertEquals("values must not be null", ex.getMessage());
        }

        @Test
        @DisplayName("should work with Set and other Collection types")
        void shouldWorkWithSet() {
            var set = Set.of("alpha", "beta", "gamma");
            assertSame(set, TextTools.requireNotEmpty(set, "values"));
        }

        @Test
        @DisplayName("should work with StringBuilder and other CharSequence types")
        void shouldWorkWithStringBuilder() {
            var list = List.of(new StringBuilder("hello"), new StringBuffer(" "));
            var result = TextTools.requireNotEmpty(list, "values");
            assertSame(list, result);
        }
    }

    @Nested
    @DisplayName("requireNotEmpty(Collection, Supplier<String>) Tests")
    class RequireNotEmptyCollectionSupplierTests {

        @Test
        @DisplayName("should return the original collection when all elements valid")
        void shouldReturnOriginalCollection() {
            var list = List.of("hello", " ");
            var result = TextTools.requireNotEmpty(list, () -> "null", () -> "empty");
            assertSame(list, result);
        }

        @Test
        @DisplayName("should throw IllegalArgumentException with supplier message for empty element")
        void shouldThrowIllegalArgumentExceptionForEmptyElementWithSupplierMessage() {
            var list = List.of("hello", "");
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> TextTools.requireNotEmpty(list, () -> "null", () -> "custom empty elem msg"));
            assertEquals("custom empty elem msg", ex.getMessage());
        }

        @Test
        @DisplayName("should throw IllegalArgumentException with supplier message for empty collection")
        void shouldThrowIllegalArgumentExceptionWithSupplierMessage() {
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> TextTools.requireNotEmpty(List.of(), () -> "null", () -> "custom empty msg"));
            assertEquals("custom empty msg", ex.getMessage());
        }

        @Test
        @DisplayName("should throw NullPointerException with supplier message for null element")
        void shouldThrowNullPointerExceptionForNullElementWithSupplierMessage() {
            var list = Arrays.asList("hello", null);
            var ex = assertThrows(NullPointerException.class,
                    () -> TextTools.requireNotEmpty(list, () -> "custom null elem msg", () -> "empty"));
            assertEquals("custom null elem msg", ex.getMessage());
        }

        @Test
        @DisplayName("should throw NullPointerException with supplier message for null collection")
        void shouldThrowNullPointerExceptionWithSupplierMessage() {
            var ex = assertThrows(NullPointerException.class,
                    () -> TextTools.requireNotEmpty((Collection<String>) null, () -> "custom null msg", () -> "empty"));
            assertEquals("custom null msg", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("requireNotEmpty(CharSequence, String) Tests")
    class RequireNotEmptyContextTests {

        @Test
        @DisplayName("should return the original CharSequence type")
        void shouldReturnOriginalCharSequenceType() {
            var sb = new StringBuilder("hello");
            assertSame(TextTools.requireNotEmpty(sb, "value"), sb);
        }

        @Test
        @DisplayName("should return the string when not empty")
        void shouldReturnStringWhenNotEmpty() {
            assertEquals("hello", TextTools.requireNotEmpty("hello", "value"));
        }

        @Test
        @DisplayName("should return non-empty whitespace-only string")
        void shouldReturnWhitespaceOnlyString() {
            assertEquals(" ", TextTools.requireNotEmpty(" ", "value"));
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " ", " ", "\t"})
        @DisplayName("should throw IllegalArgumentException for blank context")
        void shouldThrowIllegalArgumentExceptionForBlankContext(String context) {
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> TextTools.requireNotEmpty("hello", context));
            assertEquals("context must not be blank", ex.getMessage());
        }

        @Test
        @DisplayName("should throw IllegalArgumentException for empty string")
        void shouldThrowIllegalArgumentExceptionForEmpty() {
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> TextTools.requireNotEmpty("", "value"));
            assertEquals("value must not be empty", ex.getMessage());
        }

        @Test
        @DisplayName("should throw NullPointerException for null string")
        void shouldThrowNullPointerExceptionForNull() {
            var ex = assertThrows(NullPointerException.class,
                    () -> TextTools.requireNotEmpty((String) null, "value"));
            assertEquals("value must not be null", ex.getMessage());
        }

        @Test
        @DisplayName("should throw NullPointerException for null context")
        @SuppressWarnings("DataFlowIssue")
        void shouldThrowNullPointerExceptionForNullContext() {
            var ex = assertThrows(NullPointerException.class,
                    () -> TextTools.requireNotEmpty("hello", (String) null));
            assertEquals("context must not be null", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("requireNotEmpty(CharSequence, Supplier<String>) Tests")
    class RequireNotEmptySupplierTests {

        @Test
        @DisplayName("should return the original CharSequence type")
        void shouldReturnOriginalCharSequenceType() {
            var sb = new StringBuilder("hello");
            assertSame(TextTools.requireNotEmpty(sb, () -> "null", () -> "empty"), sb);
        }

        @Test
        @DisplayName("should return the string when not empty")
        void shouldReturnStringWhenNotEmpty() {
            assertEquals("hello", TextTools.requireNotEmpty("hello", () -> "null", () -> "empty"));
        }

        @Test
        @DisplayName("should throw IllegalArgumentException with supplier message for empty string")
        void shouldThrowIllegalArgumentExceptionWithSupplierMessage() {
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> TextTools.requireNotEmpty("", () -> "null", () -> "custom empty msg"));
            assertEquals("custom empty msg", ex.getMessage());
        }

        @Test
        @DisplayName("should throw NullPointerException with supplier message for null string")
        void shouldThrowNullPointerExceptionWithSupplierMessage() {
            var ex = assertThrows(NullPointerException.class,
                    () -> TextTools.requireNotEmpty((String) null, () -> "custom null msg", () -> "empty"));
            assertEquals("custom null msg", ex.getMessage());
        }

        @Test
        @DisplayName("should not call supplier on success")
        void supplierIsLazy() {
            var called = new boolean[]{false};
            TextTools.requireNotEmpty("hello",
                    () -> {
                        called[0] = true;
                        return "null";
                    },
                    () -> {
                        called[0] = true;
                        return "empty";
                    });
            assertFalse(called[0]);
        }
    }

    @Nested
    @DisplayName("requireNotEmpty(String, T...) Tests")
    class RequireNotEmptyVarargsContextTests {

        @Test
        @DisplayName("should allow whitespace-only elements")
        void shouldAllowWhitespaceOnlyElements() {
            var elements = new String[]{"hello", " ", "\t", "world"};
            assertSame(elements, TextTools.requireNotEmpty("values", elements));
        }

        @Test
        @DisplayName("should fail fast on first empty element")
        void shouldFailFastOnFirstEmpty() {
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> TextTools.requireNotEmpty("values", "", "hello"));
            assertEquals("values must not be empty or contain empty elements", ex.getMessage());
        }

        @Test
        @DisplayName("should fail fast on first null element")
        void shouldFailFastOnFirstNull() {
            var ex = assertThrows(NullPointerException.class,
                    () -> TextTools.requireNotEmpty("values", null, "", "world"));
            assertEquals("values must not be null", ex.getMessage());
        }

        @Test
        @DisplayName("should return the original array when all elements valid")
        void shouldReturnOriginalArray() {
            var elements = new String[]{"hello", " ", "world"};
            var result = TextTools.requireNotEmpty("values", elements);
            assertSame(elements, result);
        }

        @Test
        @DisplayName("should throw IllegalArgumentException for blank context")
        void shouldThrowIllegalArgumentExceptionForBlankContext() {
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> TextTools.requireNotEmpty(" ", " "));
            assertEquals("context must not be blank", ex.getMessage());
        }

        @Test
        @DisplayName("should throw IllegalArgumentException for empty array")
        void shouldThrowIllegalArgumentExceptionForEmptyArray() {
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> TextTools.requireNotEmpty("values"));
            assertEquals("values must not be empty or contain empty elements", ex.getMessage());
        }

        @Test
        @DisplayName("should throw IllegalArgumentException when array contains empty element")
        void shouldThrowIllegalArgumentExceptionForEmptyElement() {
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> TextTools.requireNotEmpty("values", "hello", ""));
            assertEquals("values must not be empty or contain empty elements", ex.getMessage());
        }

        @Test
        @DisplayName("should throw NullPointerException for null arequireNotEmptyrray")
        void shouldThrowNullPointerExceptionForNullArray() {
            var ex = assertThrows(NullPointerException.class,
                    () -> TextTools.requireNotEmpty("values", (String[]) null));
            assertEquals("values must not be null", ex.getMessage());
        }

        @Test
        @DisplayName("should throw NullPointerException for null context")
        @SuppressWarnings("DataFlowIssue")
        void shouldThrowNullPointerExceptionForNullContext() {
            var ex = assertThrows(NullPointerException.class,
                    () -> TextTools.requireNotEmpty("hello", (String) null));
            assertEquals("context must not be null", ex.getMessage());
        }

        @Test
        @DisplayName("should throw NullPointerException when array contains null element")
        void shouldThrowNullPointerExceptionForNullElement() {
            var ex = assertThrows(NullPointerException.class,
                    () -> TextTools.requireNotEmpty("values", "hello", null));
            assertEquals("values must not be null", ex.getMessage());
        }

        @Test
        @DisplayName("should work with StringBuilder and other CharSequence types")
        void shouldWorkWithStringBuilder() {
            var elements = new CharSequence[]{new StringBuilder("hello"), new StringBuffer(" ")};
            var result = TextTools.requireNotEmpty("values", elements);
            assertSame(elements, result);
        }
    }

    @Nested
    @DisplayName("requireNotEmpty(Supplier<String>, T...) Tests")
    class RequireNotEmptyVarargsSupplierTests {

        @Test
        @DisplayName("should allow whitespace-only elements")
        void shouldAllowWhitespaceOnlyElements() {
            var elements = new String[]{"hello", " ", "\t", "world"};
            assertSame(elements, TextTools.requireNotEmpty(() -> "null", () -> "empty", elements));
        }

        @Test
        @DisplayName("should fail fast on first empty element")
        void shouldFailFastOnFirstEmpty() {
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> TextTools.requireNotEmpty(() -> "null", () -> "empty", "", "hello"));
            assertEquals("empty", ex.getMessage());
        }

        @Test
        @DisplayName("should fail fast on first null element")
        void shouldFailFastOnFirstNull() {
            var ex = assertThrows(NullPointerException.class,
                    () -> TextTools.requireNotEmpty(() -> "null", () -> "empty", null, "", "world"));
            assertEquals("null", ex.getMessage());
        }

        @Test
        @DisplayName("should return the original array when all elements valid")
        void shouldReturnOriginalArray() {
            var elements = new String[]{"hello", " ", "world"};
            var result = TextTools.requireNotEmpty(() -> "null", () -> "empty", elements);
            assertSame(elements, result);
        }

        @Test
        @DisplayName("should throw IllegalArgumentException with supplier message for empty element")
        void shouldThrowIllegalArgumentExceptionForEmptyElementWithSupplierMessage() {
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> TextTools.requireNotEmpty(() -> "null", () -> "custom empty elem msg", "hello", ""));
            assertEquals("custom empty elem msg", ex.getMessage());
        }

        @Test
        @DisplayName("should throw IllegalArgumentException with supplier message for empty array")
        void shouldThrowIllegalArgumentExceptionWithSupplierMessage() {
            var ex = assertThrows(IllegalArgumentException.class,
                    () -> TextTools.requireNotEmpty(() -> "null", () -> "custom empty msg"));
            assertEquals("custom empty msg", ex.getMessage());
        }

        @Test
        @DisplayName("should throw NullPointerException with supplier message for null element")
        void shouldThrowNullPointerExceptionForNullElementWithSupplierMessage() {
            var ex = assertThrows(NullPointerException.class,
                    () -> TextTools.requireNotEmpty(
                            () -> "custom null elem msg",
                            () -> "empty",
                            "hello", null));
            assertEquals("custom null elem msg", ex.getMessage());
        }

        @Test
        @DisplayName("should throw NullPointerException with supplier message for null array")
        void shouldThrowNullPointerExceptionWithSupplierMessage() {
            var ex = assertThrows(NullPointerException.class,
                    () -> TextTools.requireNotEmpty(() -> "custom null msg", () -> "null", (String[]) null));
            assertEquals("custom null msg", ex.getMessage());
        }

        @Test
        @DisplayName("should work with StringBuilder and other CharSequence types")
        void shouldWorkWithStringBuilder() {
            var elements = new CharSequence[]{new StringBuilder("hello"), new StringBuffer(" ")};
            var result = TextTools.requireNotEmpty(() -> "null", () -> "empty", elements);
            assertSame(elements, result);
        }
    }
}