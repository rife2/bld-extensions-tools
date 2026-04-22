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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ObjectToolsNullTest {

    @Nested
    @DisplayName("isAnyNull")
    class IsAnyNull {

        @Test
        @DisplayName("returns false when none are null")
        void falseWhenNoneNull() {
            assertFalse(ObjectTools.isAnyNull("a", "b", "c"));
        }

        @Test
        @DisplayName("returns true when any element is null")
        void trueWhenAnyNull() {
            assertTrue(ObjectTools.isAnyNull("x", null, "y"));
        }

        @Test
        @DisplayName("returns true when varargs is null")
        void trueWhenVarargsNull() {
            assertTrue(ObjectTools.isAnyNull((Object[]) null));
        }
    }

    @Nested
    @DisplayName("isNotNull")
    class IsNotNull {

        @Test
        @DisplayName("returns false when any element is null")
        void falseWhenAnyNull() {
            assertFalse(ObjectTools.isNotNull("a", null, "c"));
        }

        @Test
        @DisplayName("returns false when varargs is null")
        void falseWhenVarargsNull() {
            assertFalse(ObjectTools.isNotNull((Object[]) null));
        }

        @Test
        @DisplayName("returns true when all elements are non-null")
        void trueWhenAllNonNull() {
            assertTrue(ObjectTools.isNotNull("a", "b", "c"));
        }
    }

    @Nested
    @DisplayName("isNull")
    class IsNull {

        @Test
        @DisplayName("returns false when any element is non-null")
        void falseWhenAnyNonNull() {
            assertFalse(ObjectTools.isNull(null, "x", null));
        }

        @Test
        @DisplayName("returns true when all elements are null")
        void trueWhenAllNull() {
            assertTrue(ObjectTools.isNull(null, null, null));
        }

        @Test
        @DisplayName("returns true when varargs is null")
        void trueWhenVarargsNull() {
            assertTrue(ObjectTools.isNull((Object[]) null));
        }
    }
}