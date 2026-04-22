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

import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ObjectToolsRequireTest {

    @Test
    @DisplayName("requireAnyNotEmpty(array) throws for null or empty")
    void requireArrayThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> ObjectTools.requireAnyNotEmpty((Object[]) null, "msg"));
        assertThrows(IllegalArgumentException.class,
                () -> ObjectTools.requireAnyNotEmpty(new Object[]{}, "msg"));
    }

    @Test
    @DisplayName("requireAnyNotEmpty(array) does not throw for non-empty")
    void requireArrayDoesNotThrow() {
        assertDoesNotThrow(() -> ObjectTools.requireAnyNotEmpty(new Object[]{"x"}, "msg"));
    }

    @Test
    @DisplayName("requireAnyNotEmpty(map) throws for null or empty")
    void requireMapThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> ObjectTools.requireAnyNotEmpty((Map<?, ?>) null, "msg"));
        assertThrows(IllegalArgumentException.class,
                () -> ObjectTools.requireAnyNotEmpty(Map.of(), "msg"));
    }

    @Test
    @DisplayName("requireAnyNotEmpty(map) does not throw for non-empty")
    void requireMapDoesNotThrow() {
        assertDoesNotThrow(() -> ObjectTools.requireAnyNotEmpty(Map.of("k", "v"), "msg"));
    }

    @Test
    @DisplayName("requireAnyNotEmpty(collection) throws for null or empty")
    void requireCollectionThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> ObjectTools.requireAnyNotEmpty((Collection<?>) null, "msg"));
        assertThrows(IllegalArgumentException.class,
                () -> ObjectTools.requireAnyNotEmpty(List.of(), "msg"));
    }

    @Test
    @DisplayName("requireAnyNotEmpty(collection) does not throw for non-empty")
    void requireCollectionDoesNotThrow() {
        assertDoesNotThrow(() -> ObjectTools.requireAnyNotEmpty(List.of("x"), "msg"));
    }

    @Test
    @DisplayName("requireAnyNotEmpty(varargs collections) throws when all empty or null")
    void requireVarargsThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> ObjectTools.requireAnyNotEmpty("msg",
                        (List<?>) null, List.of(), List.of()));
    }

    @Test
    @DisplayName("requireAnyNotEmpty(varargs collections) does not throw when any non-empty")
    void requireVarargsDoesNotThrow() {
        assertDoesNotThrow(() -> ObjectTools.requireAnyNotEmpty("msg",
                List.of(), List.of("x"), null));
    }
}