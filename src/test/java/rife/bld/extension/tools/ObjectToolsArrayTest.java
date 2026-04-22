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

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ObjectToolsArrayTest {

    static Stream<Arguments> nonEmptyArrays() {
        return Stream.of(
                Arguments.of((Object) new Object[]{"x"}),
                Arguments.of((Object) new String[]{"a", "b"}),
                Arguments.of((Object) new Integer[]{1}),
                Arguments.of((Object) new Object[]{null}),
                Arguments.of((Object) new Object[]{null, null}),
                Arguments.of((Object) new Object[]{1, null, "z"})
        );
    }

    @Nested
    @DisplayName("isEmpty(T[])")
    class IsEmptyArray {

        @ParameterizedTest
        @MethodSource("rife.bld.extension.tools.ObjectToolsArrayTest#nonEmptyArrays")
        @DisplayName("returns false for non-empty arrays")
        void returnsFalseForNonEmpty(Object[] array) {
            assertFalse(ObjectTools.isEmpty(array));
        }

        @Test
        @DisplayName("returns true for empty array")
        void returnsTrueForEmpty() {
            assertTrue(ObjectTools.isEmpty(new Object[]{}));
        }

        @Test
        @DisplayName("returns true for null array")
        void returnsTrueForNull() {
            assertTrue(ObjectTools.isEmpty((Object[]) null));
        }
    }

    @Nested
    @DisplayName("isNotEmpty(T[])")
    class IsNotEmptyArray {

        @Test
        @DisplayName("returns false for empty array")
        void returnsFalseForEmpty() {
            assertFalse(ObjectTools.isNotEmpty(new Object[]{}));
        }

        @Test
        @DisplayName("returns false for null array")
        void returnsFalseForNull() {
            assertFalse(ObjectTools.isNotEmpty((Object[]) null));
        }

        @ParameterizedTest
        @MethodSource("rife.bld.extension.tools.ObjectToolsArrayTest#nonEmptyArrays")
        @DisplayName("returns true for non-empty arrays")
        void returnsTrueForNonEmpty(Object[] array) {
            assertTrue(ObjectTools.isNotEmpty(array));
        }
    }
}
