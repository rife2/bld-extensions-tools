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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("TextUtils Tests")
class TextUtilsTest {

    @Nested
    @DisplayName("isBlank(Object) Tests")
    class IsBlankObjectTests {
        @ParameterizedTest
        @ValueSource(ints = {0, 1, 42, -1})
        @DisplayName("should return false for non-null non-blank objects")
        void shouldReturnFalseForNonBlankObjects(Integer input) {
            var result = TextUtils.isBlank(input);
            assertFalse(result);
        }

        @ParameterizedTest
        @ValueSource(strings = {"a", "text", " text ", "  text  ", "\ttext\n", "123"})
        @DisplayName("should return false for non-blank string objects")
        void shouldReturnFalseForNonBlankStringObjects(String input) {
            var result = TextUtils.isBlank((Object) input);
            assertFalse(result);
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " ", "  ", "\t", "\n", "\r", " \t\n\r "})
        @DisplayName("should return true for empty or whitespace-only string objects")
        void shouldReturnTrueForBlankStringObjects(String input) {
            var result = TextUtils.isBlank((Object) input);
            assertTrue(result);
        }

        @ParameterizedTest
        @NullSource
        @DisplayName("should return true for null object")
        void shouldReturnTrueForNullObject(Object input) {
            var result = TextUtils.isBlank(input);
            assertTrue(result);
        }
    }

    @Nested
    @DisplayName("isBlank() Tests")
    class IsBlankTests {
        @ParameterizedTest
        @ValueSource(strings = {"a", "text", " text ", "  text  ", "\ttext\n", "123"})
        @DisplayName("should return false for non-blank strings")
        void shouldReturnFalseForNonBlankStrings(String input) {
            var result = TextUtils.isBlank(input);
            assertFalse(result);
        }

        @ParameterizedTest
        @NullSource
        @ValueSource(strings = {"", " ", "  ", "\t", "\n", "\r", " \t\n\r "})
        @DisplayName("should return true for null, empty, or whitespace-only strings")
        void shouldReturnTrueForBlankStrings(String input) {
            var result = TextUtils.isBlank(input);
            assertTrue(result);
        }
    }

    @Nested
    @DisplayName("isEmpty(Object) Tests")
    class IsEmptyObjectTests {
        @ParameterizedTest
        @ValueSource(strings = {" ", "  ", "\t", "\n", "text", " text ", "a"})
        @DisplayName("should return false for non-empty string objects")
        void shouldReturnFalseForNonEmptyStringObjects(String input) {
            var result = TextUtils.isEmpty((Object) input);
            assertFalse(result);
        }

        @ParameterizedTest
        @ValueSource(ints = {0, 1, 42, -1})
        @DisplayName("should return false for non-null objects")
        void shouldReturnFalseForNonNullObjects(Integer input) {
            var result = TextUtils.isEmpty(input);
            assertFalse(result);
        }

        @ParameterizedTest
        @ValueSource(strings = {""})
        @DisplayName("should return true for empty string object")
        void shouldReturnTrueForEmptyStringObject(String input) {
            var result = TextUtils.isEmpty((Object) input);
            assertTrue(result);
        }

        @ParameterizedTest
        @NullSource
        @DisplayName("should return true for null object")
        void shouldReturnTrueForNullObject(Object input) {
            var result = TextUtils.isEmpty(input);
            assertTrue(result);
        }
    }

    @Nested
    @DisplayName("isEmpty() Tests")
    class IsEmptyTests {
        @ParameterizedTest
        @ValueSource(strings = {" ", "  ", "\t", "\n", "text", " text ", "a"})
        @DisplayName("should return false for non-empty strings")
        void shouldReturnFalseForNonEmptyStrings(String input) {
            var result = TextUtils.isEmpty(input);
            assertFalse(result);
        }

        @ParameterizedTest
        @NullSource
        @ValueSource(strings = {""})
        @DisplayName("should return true for null or empty strings")
        void shouldReturnTrueForEmptyStrings(String input) {
            var result = TextUtils.isEmpty(input);
            assertTrue(result);
        }
    }

    @Nested
    @DisplayName("isNotBlank(Object) Tests")
    class IsNotBlankObjectTests {
        @ParameterizedTest
        @ValueSource(strings = {"", " ", "  ", "\t", "\n", "\r", " \t\n\r "})
        @DisplayName("should return false for empty or whitespace-only string objects")
        void shouldReturnFalseForBlankStringObjects(String input) {
            var result = TextUtils.isNotBlank((Object) input);
            assertFalse(result);
        }

        @ParameterizedTest
        @NullSource
        @DisplayName("should return false for null object")
        void shouldReturnFalseForNullObject(Object input) {
            var result = TextUtils.isNotBlank(input);
            assertFalse(result);
        }

        @ParameterizedTest
        @ValueSource(ints = {0, 1, 42, -1})
        @DisplayName("should return true for non-null non-blank objects")
        void shouldReturnTrueForNonBlankObjects(Integer input) {
            var result = TextUtils.isNotBlank(input);
            assertTrue(result);
        }

        @ParameterizedTest
        @ValueSource(strings = {"a", "text", " text ", "  text  ", "\ttext\n", "123"})
        @DisplayName("should return true for non-blank string objects")
        void shouldReturnTrueForNonBlankStringObjects(String input) {
            var result = TextUtils.isNotBlank((Object) input);
            assertTrue(result);
        }
    }

    @Nested
    @DisplayName("isNotBlank() Tests")
    class IsNotBlankTests {
        @ParameterizedTest
        @NullSource
        @ValueSource(strings = {"", " ", "  ", "\t", "\n", "\r", " \t\n\r "})
        @DisplayName("should return false for null, empty, or whitespace-only strings")
        void shouldReturnFalseForBlankStrings(String input) {
            var result = TextUtils.isNotBlank(input);
            assertFalse(result);
        }

        @ParameterizedTest
        @ValueSource(strings = {"a", "text", " text ", "  text  ", "\ttext\n", "123"})
        @DisplayName("should return true for non-blank strings")
        void shouldReturnTrueForNonBlankStrings(String input) {
            var result = TextUtils.isNotBlank(input);
            assertTrue(result);
        }
    }

    @Nested
    @DisplayName("isNotEmpty(Object) Tests")
    class IsNotEmptyObjectTests {
        @ParameterizedTest
        @ValueSource(strings = {""})
        @DisplayName("should return false for empty string object")
        void shouldReturnFalseForEmptyStringObject(String input) {
            var result = TextUtils.isNotEmpty((Object) input);
            assertFalse(result);
        }

        @ParameterizedTest
        @NullSource
        @DisplayName("should return false for null object")
        void shouldReturnFalseForNullObject(Object input) {
            var result = TextUtils.isNotEmpty(input);
            assertFalse(result);
        }

        @ParameterizedTest
        @ValueSource(strings = {" ", "  ", "\t", "\n", "text", " text ", "a"})
        @DisplayName("should return true for non-empty string objects")
        void shouldReturnTrueForNonEmptyStringObjects(String input) {
            var result = TextUtils.isNotEmpty((Object) input);
            assertTrue(result);
        }

        @ParameterizedTest
        @ValueSource(ints = {0, 1, 42, -1})
        @DisplayName("should return true for non-null objects")
        void shouldReturnTrueForNonNullObjects(Integer input) {
            var result = TextUtils.isNotEmpty(input);
            assertTrue(result);
        }
    }

    @Nested
    @DisplayName("isNotEmpty() Tests")
    class IsNotEmptyTests {
        @ParameterizedTest
        @NullSource
        @ValueSource(strings = {""})
        @DisplayName("should return false for null or empty strings")
        void shouldReturnFalseForEmptyStrings(String input) {
            var result = TextUtils.isNotEmpty(input);
            assertFalse(result);
        }

        @ParameterizedTest
        @ValueSource(strings = {" ", "  ", "\t", "\n", "text", " text ", "a"})
        @DisplayName("should return true for non-empty strings")
        void shouldReturnTrueForNonEmptyStrings(String input) {
            var result = TextUtils.isNotEmpty(input);
            assertTrue(result);
        }
    }
}