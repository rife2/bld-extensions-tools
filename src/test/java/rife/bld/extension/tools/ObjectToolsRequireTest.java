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

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ObjectToolsRequireTest {

    @Nested
    @DisplayName("Require Any Not Empty Tests")
    class RequireAnyNotEmptyTests {

        @Test
        @DisplayName("requireAnyNotEmpty(array) does not throw for non-empty")
        void requireArrayDoesNotThrow() {
            assertDoesNotThrow(() -> ObjectTools.requireAnyNotEmpty(new Object[]{"x"}, "msg"));
        }

        @Test
        @DisplayName("requireAnyNotEmpty(array) throws for null or empty")
        void requireArrayThrows() {
            assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireAnyNotEmpty((Object[]) null, "msg"));
            assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireAnyNotEmpty(new Object[]{}, "msg"));
        }

        @Test
        @DisplayName("requireAnyNotEmpty(collection) does not throw for non-empty")
        void requireCollectionDoesNotThrow() {
            assertDoesNotThrow(() -> ObjectTools.requireAnyNotEmpty(List.of("x"), "msg"));
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
        @DisplayName("requireAnyNotEmpty(map) does not throw for non-empty")
        void requireMapDoesNotThrow() {
            assertDoesNotThrow(() -> ObjectTools.requireAnyNotEmpty(Map.of("k", "v"), "msg"));
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
        @DisplayName("requireAnyNotEmpty(varargs collections) does not throw when any non-empty")
        void requireVarargsDoesNotThrow() {
            assertDoesNotThrow(() -> ObjectTools.requireAnyNotEmpty("msg",
                    List.of(), List.of("x"), null));
        }

        @Test
        @DisplayName("requireAnyNotEmpty(varargs collections) throws when all empty or null")
        void requireVarargsThrows() {
            assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireAnyNotEmpty("msg",
                            (List<?>) null, List.of(), List.of()));
        }
    }

    @Nested
    @DisplayName("Require Not Empty Tests")
    class RequireNotEmptyTests {

        @Test
        @DisplayName("requireNotEmpty(array) does not throw for non-empty")
        void requireNotEmptyArrayDoesNotThrow() {
            assertDoesNotThrow(() ->
                    ObjectTools.requireNotEmpty(new Object[]{"x"}, "msg"));
        }

        @Test
        @DisplayName("requireNotEmpty(array) throws for null or empty")
        void requireNotEmptyArrayThrows() {
            assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireNotEmpty((Object[]) null, "msg"));
            assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireNotEmpty(new Object[]{}, "msg"));
        }

        @Test
        @DisplayName("requireNotEmpty(collection) does not throw for non-empty")
        void requireNotEmptyCollectionDoesNotThrow() {
            assertDoesNotThrow(() ->
                    ObjectTools.requireNotEmpty(List.of("x"), "msg"));
        }

        @Test
        @DisplayName("requireNotEmpty(collection) throws for null or empty")
        void requireNotEmptyCollectionThrows() {
            assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireNotEmpty((Collection<?>) null, "msg"));
            assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireNotEmpty(List.of(), "msg"));
        }

        @Test
        @DisplayName("requireNotEmpty(map) does not throw for non-empty")
        void requireNotEmptyMapDoesNotThrow() {
            assertDoesNotThrow(() ->
                    ObjectTools.requireNotEmpty(Map.of("k", "v"), "msg"));
        }

        @Test
        @DisplayName("requireNotEmpty(map) throws for null or empty")
        void requireNotEmptyMapThrows() {
            assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireNotEmpty((Map<?, ?>) null, "msg"));
            assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireNotEmpty(Map.of(), "msg"));
        }

        @Test
        @DisplayName("requireNotEmpty(varargs) does not throw when all non-empty")
        void requireNotEmptyVarargsDoesNotThrow() {
            assertDoesNotThrow(() ->
                    ObjectTools.requireNotEmpty("msg",
                            List.of("x"), List.of("y")));
        }

        @Test
        @DisplayName("requireNotEmpty(varargs) throws when any null or empty")
        void requireNotEmptyVarargsThrows() {
            assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireNotEmpty("msg",
                            List.of("x"), List.of(), List.of("y")));

            assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireNotEmpty("msg",
                            List.of("x"), null));
        }
    }
}