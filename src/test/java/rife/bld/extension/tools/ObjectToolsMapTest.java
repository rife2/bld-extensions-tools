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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("ConstantValue")
class ObjectToolsMapTest {

    static Stream<Arguments> nonEmptyMaps() {
        var mapWithNullValue = new HashMap<Integer, String>();
        mapWithNullValue.put(1, null);

        return Stream.of(
                Arguments.of(Map.of("k", "v")),
                Arguments.of(Map.of("a", 1, "b", 2)),
                Arguments.of(new TreeMap<>(Map.of("x", "y"))),
                Arguments.of(new LinkedHashMap<>(Map.of("k", "v"))),
                Arguments.of(new ConcurrentHashMap<>(Map.of("test", "data"))),
                Arguments.of(mapWithNullValue)
        );
    }

    @ParameterizedTest
    @MethodSource("nonEmptyMaps")
    @DisplayName("isEmpty(map) returns false for non-empty maps")
    void isEmptyFalse(Map<?, ?> map) {
        assertFalse(ObjectTools.isEmpty(map));
    }

    @Test
    @DisplayName("isEmpty(map) returns true for empty map")
    void isEmptyTrueEmpty() {
        assertTrue(ObjectTools.isEmpty(new HashMap<>()));
    }

    @Test
    @DisplayName("isEmpty(map) returns true for null map")
    void isEmptyTrueNull() {
        assertTrue(ObjectTools.isEmpty((Map<?, ?>) null));
    }

    @Test
    @DisplayName("isNotEmpty(map) returns false for empty map")
    void isNotEmptyFalseEmpty() {
        assertFalse(ObjectTools.isNotEmpty(new HashMap<>()));
    }

    @Test
    @DisplayName("isNotEmpty(map) returns false for null map")
    void isNotEmptyFalseNull() {
        assertFalse(ObjectTools.isNotEmpty((Map<?, ?>) null));
    }

    @ParameterizedTest
    @MethodSource("nonEmptyMaps")
    @DisplayName("isNotEmpty(map) returns true for non-empty maps")
    void isNotEmptyTrue(Map<?, ?> map) {
        assertTrue(ObjectTools.isNotEmpty(map));
    }
}
