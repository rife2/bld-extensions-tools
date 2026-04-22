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

@SuppressWarnings("ConstantValue")
class ObjectToolsCollectionTest {

    static Stream<Arguments> nonEmptyCollections() {
        return Stream.of(
                Arguments.of(List.of("x")),
                Arguments.of(List.of("a", "b")),
                Arguments.of(Set.of(1, 2)),
                Arguments.of(Collections.singletonList(null)),
                Arguments.of(Arrays.asList(null, null)),
                Arguments.of(Arrays.asList(1, null, "z"))
        );
    }

    @Nested
    @DisplayName("isEmpty(Collection<?>)")
    class IsEmptyCollection {

        @ParameterizedTest
        @MethodSource("rife.bld.extension.tools.ObjectToolsCollectionTest#nonEmptyCollections")
        @DisplayName("returns false for non-empty collections")
        void returnsFalseForNonEmpty(Collection<?> c) {
            assertFalse(ObjectTools.isEmpty(c));
        }

        @Test
        @DisplayName("returns true for empty collection")
        void returnsTrueForEmpty() {
            assertTrue(ObjectTools.isEmpty(new ArrayList<>()));
        }

        @Test
        @DisplayName("returns true for null collection")
        void returnsTrueForNull() {
            assertTrue(ObjectTools.isEmpty((Collection<?>) null));
        }
    }

    @Nested
    @DisplayName("isNotEmpty(Collection<?>)")
    class IsNotEmptyCollection {

        @Test
        @DisplayName("returns false for empty collection")
        void returnsFalseForEmpty() {
            assertFalse(ObjectTools.isNotEmpty(new ArrayList<>()));
        }

        @Test
        @DisplayName("returns false for null collection")
        void returnsFalseForNull() {
            assertFalse(ObjectTools.isNotEmpty((Collection<?>) null));
        }

        @ParameterizedTest
        @MethodSource("rife.bld.extension.tools.ObjectToolsCollectionTest#nonEmptyCollections")
        @DisplayName("returns true for non-empty collections")
        void returnsTrueForNonEmpty(Collection<?> c) {
            assertTrue(ObjectTools.isNotEmpty(c));
        }
    }

    @Nested
    @DisplayName("Varargs collections – isEmpty / isNotEmpty")
    class VarargsCollections {

        static Stream<Arguments> collectionsWithAtLeastOneNonEmpty() {
            return Stream.of(
                    Arguments.of((Object) new Collection<?>[]{List.of("x")}),
                    Arguments.of((Object) new Collection<?>[]{new ArrayList<>(), List.of("a")}),
                    Arguments.of((Object) new Collection<?>[]{null, List.of("b"), new HashSet<>()}),
                    Arguments.of((Object) new Collection<?>[]{Set.of(1), new ArrayList<>(), null}),
                    Arguments.of((Object) new Collection<?>[]{List.of("a"), Set.of("b")})
            );
        }

        @ParameterizedTest
        @MethodSource("collectionsWithAtLeastOneNonEmpty")
        @DisplayName("isEmpty(varargs) returns false when any collection is non-empty")
        void isEmptyFalse(Collection<?>[] collections) {
            assertFalse(ObjectTools.isEmpty(collections));
        }

        @Test
        @DisplayName("isEmpty(varargs) returns true when all are empty or null")
        void isEmptyTrue() {
            assertTrue(ObjectTools.isEmpty(new ArrayList<>(), new HashSet<>(), null));
        }

        @Test
        @DisplayName("isNotEmpty(varargs) returns false when all are empty or null")
        void isNotEmptyFalse() {
            assertFalse(ObjectTools.isNotEmpty(new ArrayList<>(), new HashSet<>(), null));
        }

        @ParameterizedTest
        @MethodSource("collectionsWithAtLeastOneNonEmpty")
        @DisplayName("isNotEmpty(varargs) returns true when any collection is non-empty")
        void isNotEmptyTrue(Collection<?>[] collections) {
            assertTrue(ObjectTools.isNotEmpty(collections));
        }
    }
}