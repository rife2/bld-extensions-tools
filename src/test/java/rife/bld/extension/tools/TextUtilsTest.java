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
    @DisplayName("isBlank() Tests")
    class IsBlankTests {
        @ParameterizedTest
        @NullSource
        @ValueSource(strings = {"", " ", "  ", "\t", "\n", "\r", " \t\n\r "})
        @DisplayName("should return true for null, empty, or whitespace-only strings")
        void shouldReturnTrueForBlankStrings(String input) {
            var result = TextUtils.isBlank(input);
            assertTrue(result);
        }

        @ParameterizedTest
        @ValueSource(strings = {"a", "text", " text ", "  text  ", "\ttext\n", "123"})
        @DisplayName("should return false for non-blank strings")
        void shouldReturnFalseForNonBlankStrings(String input) {
            var result = TextUtils.isBlank(input);
            assertFalse(result);
        }
    }

    @Nested
    @DisplayName("isEmpty() Tests")
    class IsEmptyTests {
        @ParameterizedTest
        @NullSource
        @ValueSource(strings = {""})
        @DisplayName("should return true for null or empty strings")
        void shouldReturnTrueForEmptyStrings(String input) {
            var result = TextUtils.isEmpty(input);
            assertTrue(result);
        }

        @ParameterizedTest
        @ValueSource(strings = {" ", "  ", "\t", "\n", "text", " text ", "a"})
        @DisplayName("should return false for non-empty strings")
        void shouldReturnFalseForNonEmptyStrings(String input) {
            var result = TextUtils.isEmpty(input);
            assertFalse(result);
        }
    }

    @Nested
    @DisplayName("isNotBlank() Tests")
    class IsNotBlankTests {
        @ParameterizedTest
        @ValueSource(strings = {"a", "text", " text ", "  text  ", "\ttext\n", "123"})
        @DisplayName("should return true for non-blank strings")
        void shouldReturnTrueForNonBlankStrings(String input) {
            var result = TextUtils.isNotBlank(input);
            assertTrue(result);
        }

        @ParameterizedTest
        @NullSource
        @ValueSource(strings = {"", " ", "  ", "\t", "\n", "\r", " \t\n\r "})
        @DisplayName("should return false for null, empty, or whitespace-only strings")
        void shouldReturnFalseForBlankStrings(String input) {
            var result = TextUtils.isNotBlank(input);
            assertFalse(result);
        }
    }

    @Nested
    @DisplayName("isNotEmpty() Tests")
    class IsNotEmptyTests {
        @ParameterizedTest
        @ValueSource(strings = {" ", "  ", "\t", "\n", "text", " text ", "a"})
        @DisplayName("should return true for non-empty strings")
        void shouldReturnTrueForNonEmptyStrings(String input) {
            var result = TextUtils.isNotEmpty(input);
            assertTrue(result);
        }

        @ParameterizedTest
        @NullSource
        @ValueSource(strings = {""})
        @DisplayName("should return false for null or empty strings")
        void shouldReturnFalseForEmptyStrings(String input) {
            var result = TextUtils.isNotEmpty(input);
            assertFalse(result);
        }
    }
}