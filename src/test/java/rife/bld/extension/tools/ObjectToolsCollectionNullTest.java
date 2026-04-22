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

import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ObjectToolsCollectionNullTest {

    static Stream<Arguments> allNullCollections() {
        return Stream.of(
                Arguments.of(Collections.singletonList(null)),
                Arguments.of(Arrays.asList(null, null)),
                Arguments.of(Arrays.asList(null, null, null))
        );
    }

    static Stream<Arguments> mixedCollections() {
        return Stream.of(
                Arguments.of(Arrays.asList(null, "x")),
                Arguments.of(Arrays.asList("x", null)),
                Arguments.of(Arrays.asList(null, "x", null))
        );
    }

    static Stream<Arguments> noNullCollections() {
        return Stream.of(
                Arguments.of(List.of("a")),
                Arguments.of(List.of("a", "b")),
                Arguments.of(Set.of(1, 2, 3)),
                Arguments.of(Arrays.asList(1, "x", 3))
        );
    }

    @Nested
    @DisplayName("isAnyNull(Collection<?>)")
    class IsAnyNullCollection {

        @ParameterizedTest
        @MethodSource("rife.bld.extension.tools.ObjectToolsCollectionNullTest#noNullCollections")
        @DisplayName("returns false when no elements are null")
        void returnsFalseWhenNoneNull(Collection<?> collection) {
            assertFalse(ObjectTools.isAnyNull(collection));
        }

        @ParameterizedTest
        @MethodSource("rife.bld.extension.tools.ObjectToolsCollectionNullTest#allNullCollections")
        @DisplayName("returns true when all elements are null")
        void returnsTrueWhenAllNull(Collection<?> collection) {
            assertTrue(ObjectTools.isAnyNull(collection));
        }

        @ParameterizedTest
        @MethodSource("rife.bld.extension.tools.ObjectToolsCollectionNullTest#mixedCollections")
        @DisplayName("returns true when any element is null")
        void returnsTrueWhenAnyNull(Collection<?> collection) {
            assertTrue(ObjectTools.isAnyNull(collection));
        }

        @Test
        @DisplayName("returns true when collection is null")
        void returnsTrueWhenCollectionNull() {
            assertTrue(ObjectTools.isAnyNull((Collection<?>) null));
        }
    }

    @Nested
    @DisplayName("isNotNull(Collection<?>)")
    class IsNotNullCollection {

        @ParameterizedTest
        @MethodSource("rife.bld.extension.tools.ObjectToolsCollectionNullTest#allNullCollections")
        @DisplayName("returns false when all elements are null")
        void returnsFalseWhenAllNull(Collection<?> collection) {
            assertFalse(ObjectTools.isNotNull(collection));
        }

        @ParameterizedTest
        @MethodSource("rife.bld.extension.tools.ObjectToolsCollectionNullTest#mixedCollections")
        @DisplayName("returns false when any element is null")
        void returnsFalseWhenAnyNull(Collection<?> collection) {
            assertFalse(ObjectTools.isNotNull(collection));
        }

        @Test
        @DisplayName("returns false when collection is null")
        void returnsFalseWhenCollectionNull() {
            assertFalse(ObjectTools.isNotNull((Collection<?>) null));
        }

        @ParameterizedTest
        @MethodSource("rife.bld.extension.tools.ObjectToolsCollectionNullTest#noNullCollections")
        @DisplayName("returns true when all elements are non-null")
        void returnsTrueWhenAllNonNull(Collection<?> collection) {
            assertTrue(ObjectTools.isNotNull(collection));
        }
    }

    @Nested
    @DisplayName("isNull(Collection<?>)")
    class IsNullCollection {

        @ParameterizedTest
        @MethodSource("rife.bld.extension.tools.ObjectToolsCollectionNullTest#noNullCollections")
        @DisplayName("returns false when all elements are non-null")
        void returnsFalseWhenAllNonNull(Collection<?> collection) {
            assertFalse(ObjectTools.isNull(collection));
        }

        @ParameterizedTest
        @MethodSource("rife.bld.extension.tools.ObjectToolsCollectionNullTest#mixedCollections")
        @DisplayName("returns false when collection contains mix of null and non-null")
        void returnsFalseWhenMixed(Collection<?> collection) {
            assertFalse(ObjectTools.isNull(collection));
        }

        @ParameterizedTest
        @MethodSource("rife.bld.extension.tools.ObjectToolsCollectionNullTest#allNullCollections")
        @DisplayName("returns true when all elements are null")
        void returnsTrueWhenAllNull(Collection<?> collection) {
            assertTrue(ObjectTools.isNull(collection));
        }

        @Test
        @DisplayName("returns true when collection is null")
        void returnsTrueWhenCollectionNull() {
            assertTrue(ObjectTools.isNull((Collection<?>) null));
        }
    }
}