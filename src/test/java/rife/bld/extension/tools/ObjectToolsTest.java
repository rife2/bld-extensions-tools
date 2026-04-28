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
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ObjectToolsTest {

    @Nested
    @DisplayName("allEmpty Tests")
    class AllEmptyTests {

        @Test
        @DisplayName("all empty → true")
        void allEmpty() {
            assertTrue(ObjectTools.allEmpty("", List.of(), Map.of()));
        }

        @Test
        @DisplayName("arbitrary object makes result false")
        void arbitraryObject() {
            assertFalse(ObjectTools.allEmpty(new Object()));
        }

        @Test
        @DisplayName("multiple empty values → true")
        void multipleEmptyValues() {
            assertTrue(ObjectTools.allEmpty("", List.of(), new Object[]{}));
        }

        @Test
        @DisplayName("empty array with null elements → true")
        void nullElements() {
            var array = new Object[]{null, null};
            assertTrue(ObjectTools.allEmpty(array));
        }

        @Test
        @DisplayName("null varargs → true")
        void nullVarargs() {
            assertTrue(ObjectTools.allEmpty((Object[]) null));
        }

        @Test
        @DisplayName("one non-empty → false")
        void oneNonEmpty() {
            assertFalse(ObjectTools.allEmpty("", List.of("x"), Map.of()));
        }

        @Test
        @DisplayName("single empty value → true")
        void singleEmptyValue() {
            assertTrue(ObjectTools.allEmpty(""));
        }

        @Test
        @DisplayName("single non-empty value → false")
        void singleNonEmptyValue() {
            assertFalse(ObjectTools.allEmpty("x"));
        }
    }

    @Nested
    @DisplayName("allNotEmpty Tests")
    class AllNotEmptyTests {

        @Test
        @DisplayName("all empty → false")
        void allEmpty() {
            assertFalse(ObjectTools.allNotEmpty("", List.of(), Map.of()));
        }

        @Test
        @DisplayName("empty array with all non-empty elements → true")
        void allNonEmptyElements() {
            assertTrue(ObjectTools.allNotEmpty("x", "y", "z"));
        }

        @Test
        @DisplayName("all non-empty → true")
        void allNotEmpty() {
            assertTrue(ObjectTools.allNotEmpty("x", List.of("y"), Map.of("k", "v")));
        }

        @Test
        @DisplayName("arbitrary objects are non-empty")
        void arbitraryObjects() {
            assertTrue(ObjectTools.allNotEmpty(new Object(), new Object()));
        }

        @Test
        @DisplayName("multiple non-empty values → true")
        void multipleNonEmptyValues() {
            assertTrue(ObjectTools.allNotEmpty("a", "b", "c"));
        }

        @Test
        @DisplayName("null value → false")
        void nullValue() {
            assertFalse(ObjectTools.allNotEmpty("x", null, Map.of("k", "v")));
        }

        @Test
        @DisplayName("null varargs → false")
        void nullVarargs() {
            assertFalse(ObjectTools.allNotEmpty((Object[]) null));
        }

        @Test
        @DisplayName("one empty → false")
        void oneEmpty() {
            assertFalse(ObjectTools.allNotEmpty("x", List.of(), Map.of("k", "v")));
        }

        @Test
        @DisplayName("single empty value → false")
        void singleEmptyValue() {
            assertFalse(ObjectTools.allNotEmpty(""));
        }

        @Test
        @DisplayName("single non-empty value → true")
        void singleNonEmptyValue() {
            assertTrue(ObjectTools.allNotEmpty("x"));
        }
    }

    @Nested
    @DisplayName("anyNotEmpty Tests")
    class AnyNotEmptyTests {

        @Test
        @DisplayName("all empty → false")
        void allEmpty() {
            assertFalse(ObjectTools.anyNotEmpty("", List.of(), Map.of()));
        }

        @Test
        @DisplayName("arbitrary object counts as non-empty")
        void arbitraryObject() {
            assertTrue(ObjectTools.anyNotEmpty(new Object()));
        }

        @Test
        @DisplayName("multiple non-empty values → true")
        void multipleNonEmptyValues() {
            assertTrue(ObjectTools.anyNotEmpty("a", "b", "c"));
        }

        @Test
        @DisplayName("null varargs → false")
        void nullVarargs() {
            assertFalse(ObjectTools.anyNotEmpty((Object[]) null));
        }

        @Test
        @DisplayName("one non-empty → true")
        void oneNonEmpty() {
            assertTrue(ObjectTools.anyNotEmpty("", List.of("x"), Map.of()));
        }

        @Test
        @DisplayName("single empty value → false")
        void singleEmptyValue() {
            assertFalse(ObjectTools.anyNotEmpty(""));
        }

        @Test
        @DisplayName("single non-empty value → true")
        void singleNonEmptyValue() {
            assertTrue(ObjectTools.anyNotEmpty("x"));
        }
    }

    @Nested
    @DisplayName("isEmpty Tests")
    class IsEmptyTests {

        @Test
        @DisplayName("Arbitrary object is never empty")
        void arbitraryObject() {
            assertFalse(ObjectTools.isEmpty(new Object()));
        }

        @Test
        @DisplayName("Array empty and non-empty")
        void arrayTests() {
            assertTrue(ObjectTools.isEmpty(new Object[]{}));
            assertFalse(ObjectTools.isEmpty(new Object[]{"x"}));
        }

        @Test
        @DisplayName("Boolean false is not empty")
        void booleanFalse() {
            assertFalse(ObjectTools.isEmpty(false));
        }

        @Test
        @DisplayName("CharSequence empty and non-empty")
        void charSequenceTests() {
            assertTrue(ObjectTools.isEmpty(""));
            assertFalse(ObjectTools.isEmpty("x"));
        }

        @Test
        @DisplayName("Collection empty and non-empty")
        void collectionTests() {
            assertTrue(ObjectTools.isEmpty(List.of()));
            assertFalse(ObjectTools.isEmpty(List.of("x")));
        }

        @Test
        @DisplayName("Map empty and non-empty")
        void mapTests() {
            assertTrue(ObjectTools.isEmpty(Map.of()));
            assertFalse(ObjectTools.isEmpty(Map.of("k", "v")));
        }

        @Test
        @DisplayName("null is empty")
        void nullIsEmpty() {
            assertTrue(ObjectTools.isEmpty(null));
        }

        @Test
        @DisplayName("Primitive array empty and non-empty")
        void primitiveArrayTests() {
            assertTrue(ObjectTools.isEmpty(new int[]{}));
            assertFalse(ObjectTools.isEmpty(new int[]{1}));
        }

        @Test
        @DisplayName("Whitespace-only string is not empty")
        void whitespaceString() {
            assertFalse(ObjectTools.isEmpty("   "));
            assertFalse(ObjectTools.isEmpty("\t"));
            assertFalse(ObjectTools.isEmpty("\n"));
        }

        @Test
        @DisplayName("Zero is not empty")
        void zero() {
            assertFalse(ObjectTools.isEmpty(0));
            assertFalse(ObjectTools.isEmpty(0.0));
        }
    }

    @Nested
    @DisplayName("isNotEmpty Tests")
    class IsNotEmptyTests {

        @Test
        @DisplayName("Arbitrary object is always not empty")
        void arbitraryObject() {
            assertTrue(ObjectTools.isNotEmpty(new Object()));
        }

        @Test
        @DisplayName("Array empty and non-empty")
        void arrayTests() {
            assertFalse(ObjectTools.isNotEmpty(new Object[]{}));
            assertTrue(ObjectTools.isNotEmpty(new Object[]{"x"}));
        }

        @Test
        @DisplayName("CharSequence empty and non-empty")
        void charSequenceTests() {
            assertFalse(ObjectTools.isNotEmpty(""));
            assertTrue(ObjectTools.isNotEmpty("x"));
        }

        @Test
        @DisplayName("Collection empty and non-empty")
        void collectionTests() {
            assertFalse(ObjectTools.isNotEmpty(List.of()));
            assertTrue(ObjectTools.isNotEmpty(List.of("x")));
        }

        @Test
        @DisplayName("Map empty and non-empty")
        void mapTests() {
            assertFalse(ObjectTools.isNotEmpty(Map.of()));
            assertTrue(ObjectTools.isNotEmpty(Map.of("k", "v")));
        }

        @Test
        @DisplayName("null is not empty → false")
        void nullIsNotEmpty() {
            assertFalse(ObjectTools.isNotEmpty(null));
        }

        @Test
        @DisplayName("Primitive array empty and non-empty")
        void primitiveArrayTests() {
            assertFalse(ObjectTools.isNotEmpty(new int[]{}));
            assertTrue(ObjectTools.isNotEmpty(new int[]{1}));
        }

        @Test
        @DisplayName("Whitespace-only string is not empty")
        void whitespaceString() {
            assertTrue(ObjectTools.isNotEmpty("   "));
        }
    }

    @Nested
    @DisplayName("requireAllEmpty Tests")
    class RequireAllEmptyTests {

        @Test
        @DisplayName("all null elements do not throw")
        void allNullElements() {
            assertDoesNotThrow(() -> ObjectTools.requireAllEmpty(
                    new Object[]{null, null}, "msg"));
        }

        @Test
        @DisplayName("throws with correct message for non-empty element")
        void correctMessageForNonEmptyElement() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> ObjectTools.requireAllEmpty(new Object[]{"x"}, "All values must be empty")
            );
            assertEquals("All values must be empty", ex.getMessage());
        }

        @Test
        @DisplayName("throws with correct message for null array")
        void correctMessageForNullArray() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> ObjectTools.requireAllEmpty((Object[]) null, "All values must be empty")
            );
            assertEquals("All values must be empty", ex.getMessage());
        }

        @Test
        @DisplayName("does not throw for all empty values")
        void doesNotThrow() {
            assertDoesNotThrow(() -> ObjectTools.requireAllEmpty(
                    new Object[]{"", List.of(), Map.of()}, "msg"));
        }

        @Test
        @DisplayName("does not throw for empty array")
        void doesNotThrowForEmptyArray() {
            assertDoesNotThrow(() -> ObjectTools.requireAllEmpty(new Object[]{}, "msg"));
        }

        @Test
        @DisplayName("formatted message is applied when element is not empty")
        void formattedMessageForNonEmptyElement() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> ObjectTools.requireAllEmpty(
                            new Object[]{"x"}, "Values %s must be empty", "element")
            );
            assertEquals("Values element must be empty", ex.getMessage());
        }

        @Test
        @DisplayName("formatted message is applied when array is null")
        void formattedMessageForNullArray() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> ObjectTools.requireAllEmpty((Object[]) null, "Values %s must be empty", "array")
            );
            assertEquals("Values array must be empty", ex.getMessage());
        }

        @Test
        @DisplayName("formatted message with no args behaves like plain message")
        void formattedMessageNoArgs() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> ObjectTools.requireAllEmpty(new Object[]{"x"}, "Simple message")
            );
            assertEquals("Simple message", ex.getMessage());
        }

        @Test
        @DisplayName("formatting failure falls back to raw message")
        void formattingFailureFallsBack() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> ObjectTools.requireAllEmpty(
                            new Object[]{"x"}, "%s %s %s", "onlyOneArg")
            );
            assertEquals("%s %s %s", ex.getMessage());
        }

        @Test
        @DisplayName("multiple empty elements do not throw")
        void multipleEmptyElements() {
            assertDoesNotThrow(() -> ObjectTools.requireAllEmpty(
                    new Object[]{"", List.of(), Map.of()}, "msg"));
        }

        @Test
        @DisplayName("multiple format args are applied correctly")
        void multipleFormatArgs() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> ObjectTools.requireAllEmpty(
                            new Object[]{"x"}, "%s:%s:%s", "a", "b", "c")
            );
            assertEquals("a:b:c", ex.getMessage());
        }

        @Test
        @DisplayName("non-empty element at end throws")
        void nonEmptyElementAtEnd() {
            assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireAllEmpty(
                            new Object[]{"", List.of(), new Object()}, "msg"));
        }

        @Test
        @DisplayName("non-empty element at start throws")
        void nonEmptyElementAtStart() {
            assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireAllEmpty(
                            new Object[]{"x", "", List.of()}, "msg"));
        }

        @Test
        @DisplayName("non-empty element in middle throws")
        void nonEmptyElementInMiddle() {
            assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireAllEmpty(
                            new Object[]{"", "x", List.of()}, "msg"));
        }

        @Test
        @DisplayName("single empty element does not throw")
        void singleEmptyElement() {
            assertDoesNotThrow(() -> ObjectTools.requireAllEmpty(
                    new Object[]{""}, "msg"));
        }

        @Test
        @DisplayName("single non-empty element throws")
        void singleNonEmptyElement() {
            assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireAllEmpty(
                            new Object[]{"x"}, "msg"));
        }

        @Test
        @DisplayName("throws when any value is not empty")
        void throwsForAnyNotEmpty() {
            assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireAllEmpty(
                            new Object[]{"", "x", List.of()}, "msg"));
            assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireAllEmpty(
                            new Object[]{"", List.of("y"), Map.of()}, "msg"));
            assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireAllEmpty(
                            new Object[]{"", Map.of(), new Object()}, "msg"));
        }

        @Test
        @DisplayName("throws when array is null")
        void throwsForNullArray() {
            assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireAllEmpty((Object[]) null, "msg"));
        }

        @Test
        @DisplayName("throws when message is null or empty")
        void throwsForNullOrEmptyMessage() {
            assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireAllEmpty(new Object[]{}, null));
            assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireAllEmpty(new Object[]{}, ""));
            assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireAllEmpty(new Object[]{}, "   "));
        }

        @Nested
        @DisplayName("requireAllEmpty(Map) Tests")
        class RequireAllEmptyMapTests {

            @Test
            @DisplayName("does not throw when all values are empty")
            void allValuesEmpty() {
                Map<String, Object> map = Map.of(
                        "a", "",
                        "b", List.of(),
                        "c", Map.of()
                );
                assertDoesNotThrow(() ->
                        ObjectTools.requireAllEmpty(map, "msg"));
            }

            @Test
            @DisplayName("does not throw for empty map")
            void emptyMapAllowed() {
                assertDoesNotThrow(() ->
                        ObjectTools.requireAllEmpty(Map.of(), "msg"));
            }

            @Test
            @DisplayName("formatted message is applied")
            void formattedMessageApplied() {
                Map<String, Object> map = Map.of("a", "x");
                IllegalArgumentException ex = assertThrows(
                        IllegalArgumentException.class,
                        () -> ObjectTools.requireAllEmpty(map, "Value %s invalid", "A")
                );
                assertEquals("Value A invalid", ex.getMessage());
            }

            @Test
            @DisplayName("formatting failure falls back to raw message")
            void formattingFailureFallsBack() {
                Map<String, Object> map = Map.of("a", "x");
                IllegalArgumentException ex = assertThrows(
                        IllegalArgumentException.class,
                        () -> ObjectTools.requireAllEmpty(map, "%s %s", "onlyOne")
                );
                assertEquals("%s %s", ex.getMessage());
            }

            @Test
            @DisplayName("does not throw for null map")
            void nullMapAllowed() {
                assertDoesNotThrow(() ->
                        ObjectTools.requireAllEmpty((Map<String, Object>) null, "msg"));
            }

            @Test
            @DisplayName("throws when message is null or empty")
            void throwsForInvalidMessage() {
                Map<String, Object> map = Map.of();
                assertThrows(IllegalArgumentException.class,
                        () -> ObjectTools.requireAllEmpty(map, null));
                assertThrows(IllegalArgumentException.class,
                        () -> ObjectTools.requireAllEmpty(map, ""));
                assertThrows(IllegalArgumentException.class,
                        () -> ObjectTools.requireAllEmpty(map, "   "));
            }

            @Test
            @DisplayName("throws when any value is not empty")
            void throwsWhenAnyValueNotEmpty() {
                Map<String, Object> map = Map.of(
                        "a", "",
                        "b", "x"
                );
                assertThrows(IllegalArgumentException.class, () ->
                        ObjectTools.requireAllEmpty(map, "msg"));
            }
        }

    }

    @Nested
    @DisplayName("requireAllNotEmpty Tests")
    class RequireAllNotEmptyTests {

        @Test
        @DisplayName("throws with correct message for empty array")
        void correctMessageForEmptyArray() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> ObjectTools.requireAllNotEmpty(new Object[]{}, "Values required")
            );
            assertEquals("Values required", ex.getMessage());
        }

        @Test
        @DisplayName("throws with correct message for empty element")
        void correctMessageForEmptyElement() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> ObjectTools.requireAllNotEmpty(new Object[]{"x", ""}, "Values required")
            );
            assertEquals("Values required", ex.getMessage());
        }

        @Test
        @DisplayName("throws with correct message for null array")
        void correctMessageForNullArray() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> ObjectTools.requireAllNotEmpty((Object[]) null, "Values required")
            );
            assertEquals("Values required", ex.getMessage());
        }

        @Test
        @DisplayName("does not throw for all non-empty values")
        void doesNotThrow() {
            assertDoesNotThrow(() -> ObjectTools.requireAllNotEmpty(
                    new Object[]{"x", List.of("y"), Map.of("k", "v")}, "msg"));
        }

        @Test
        @DisplayName("empty element at end throws")
        void emptyElementAtEnd() {
            assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireAllNotEmpty(
                            new Object[]{"x", "y", ""}, "msg"));
        }

        @Test
        @DisplayName("empty element at start throws")
        void emptyElementAtStart() {
            assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireAllNotEmpty(
                            new Object[]{"", "x", "y"}, "msg"));
        }

        @Test
        @DisplayName("empty element in middle throws")
        void emptyElementInMiddle() {
            assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireAllNotEmpty(
                            new Object[]{"x", "", "y"}, "msg"));
        }

        @Test
        @DisplayName("formatted message is applied when array is null or empty")
        void formattedMessageForEmptyArray() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> ObjectTools.requireAllNotEmpty((Object[]) null,
                            "Value %s must not be empty", "array")
            );
            assertEquals("Value array must not be empty", ex.getMessage());
        }

        @Test
        @DisplayName("formatted message is applied when element is empty")
        void formattedMessageForEmptyElement() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> ObjectTools.requireAllNotEmpty(
                            new Object[]{"x", ""}, "Value %s must not be empty", "element")
            );
            assertEquals("Value element must not be empty", ex.getMessage());
        }

        @Test
        @DisplayName("formatted message with no args behaves like plain message")
        void formattedMessageNoArgs() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> ObjectTools.requireAllNotEmpty(new Object[]{}, "Simple message")
            );
            assertEquals("Simple message", ex.getMessage());
        }

        @Test
        @DisplayName("formatting failure falls back to raw message")
        void formattingFailureFallsBack() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> ObjectTools.requireAllNotEmpty(
                            new Object[]{}, "%s %s %s", "onlyOneArg")
            );
            assertEquals("%s %s %s", ex.getMessage());
        }

        @Test
        @DisplayName("multiple format args are applied correctly")
        void multipleFormatArgs() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> ObjectTools.requireAllNotEmpty(
                            new Object[]{"x", ""}, "%s:%s:%s", "a", "b", "c")
            );
            assertEquals("a:b:c", ex.getMessage());
        }

        @Test
        @DisplayName("multiple non-empty elements do not throw")
        void multipleNonEmptyElements() {
            assertDoesNotThrow(() -> ObjectTools.requireAllNotEmpty(
                    new Object[]{"a", "b", "c"}, "msg"));
        }

        @Test
        @DisplayName("single empty element throws")
        void singleEmptyElement() {
            assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireAllNotEmpty(
                            new Object[]{""}, "msg"));
        }

        @Test
        @DisplayName("single non-empty element does not throw")
        void singleNonEmptyElement() {
            assertDoesNotThrow(() -> ObjectTools.requireAllNotEmpty(
                    new Object[]{"x"}, "msg"));
        }

        @Test
        @DisplayName("throws when any value is empty")
        void throwsForAnyEmpty() {
            assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireAllNotEmpty(
                            new Object[]{"x", "", List.of("y")}, "msg"));
            assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireAllNotEmpty(
                            new Object[]{"x", List.of(), "y"}, "msg"));
            assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireAllNotEmpty(
                            new Object[]{null, "x", "y"}, "msg"));
        }

        @Test
        @DisplayName("throws when array is empty")
        void throwsForEmptyArray() {
            assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireAllNotEmpty(new Object[]{}, "msg"));
        }

        @Test
        @DisplayName("throws when array is null")
        void throwsForNullArray() {
            assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireAllNotEmpty((Object[]) null, "msg"));
        }

        @Test
        @DisplayName("throws when message is null or empty")
        void throwsForNullOrEmptyMessage() {
            assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireAllNotEmpty(new Object[]{"x"}, null));
            assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireAllNotEmpty(new Object[]{"x"}, ""));
            assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireAllNotEmpty(new Object[]{"x"}, "   "));
        }

        @Nested
        @DisplayName("requireAllNotEmpty(Map) Tests")
        class RequireAllNotEmptyMapTests {

            @Test
            @DisplayName("does not throw when all values are not empty")
            void allValuesNotEmpty() {
                Map<String, Object> map = Map.of(
                        "a", "x",
                        "b", List.of("y"),
                        "c", Map.of("k", "v")
                );
                assertDoesNotThrow(() ->
                        ObjectTools.requireAllNotEmpty(map, "msg"));
            }

            @Test
            @DisplayName("throws for empty map")
            void emptyMapThrows() {
                assertThrows(IllegalArgumentException.class,
                        () -> ObjectTools.requireAllNotEmpty(Map.of(), "msg"));
            }

            @Test
            @DisplayName("formatted message is applied")
            void formattedMessageApplied() {
                Map<String, Object> map = Map.of("a", "");
                IllegalArgumentException ex = assertThrows(
                        IllegalArgumentException.class,
                        () -> ObjectTools.requireAllNotEmpty(map, "Bad %s", "Value")
                );
                assertEquals("Bad Value", ex.getMessage());
            }

            @Test
            @DisplayName("formatting failure falls back to raw message")
            void formattingFailureFallsBack() {
                Map<String, Object> map = Map.of("a", "");
                IllegalArgumentException ex = assertThrows(
                        IllegalArgumentException.class,
                        () -> ObjectTools.requireAllNotEmpty(map, "%s %s", "onlyOne")
                );
                assertEquals("%s %s", ex.getMessage());
            }

            @Test
            @DisplayName("throws for null map")
            void nullMapThrows() {
                assertThrows(IllegalArgumentException.class,
                        () -> ObjectTools.requireAllNotEmpty((Map<String, Object>) null, "msg"));
            }

            @Test
            @DisplayName("throws when message is null or empty")
            void throwsForInvalidMessage() {
                Map<String, Object> map = Map.of("a", "x");
                assertThrows(IllegalArgumentException.class,
                        () -> ObjectTools.requireAllNotEmpty(map, null));
                assertThrows(IllegalArgumentException.class,
                        () -> ObjectTools.requireAllNotEmpty(map, ""));
                assertThrows(IllegalArgumentException.class,
                        () -> ObjectTools.requireAllNotEmpty(map, "   "));
            }

            @Test
            @DisplayName("throws when any value is empty")
            void throwsWhenAnyValueEmpty() {
                Map<String, Object> map = Map.of(
                        "a", "x",
                        "b", ""
                );
                assertThrows(IllegalArgumentException.class, () ->
                        ObjectTools.requireAllNotEmpty(map, "msg"));
            }
        }

    }

    @Nested
    @DisplayName("requireEmpty Tests")
    class RequireEmptyTests {

        @Test
        @DisplayName("does not throw for empty values")
        void doesNotThrow() {
            assertDoesNotThrow(() -> ObjectTools.requireEmpty("", "msg"));
            assertDoesNotThrow(() -> ObjectTools.requireEmpty(List.of(), "msg"));
            assertDoesNotThrow(() -> ObjectTools.requireEmpty(Map.of(), "msg"));
            assertDoesNotThrow(() -> ObjectTools.requireEmpty(new Object[]{}, "msg"));
            assertDoesNotThrow(() -> ObjectTools.requireEmpty(new int[]{}, "msg"));
            assertDoesNotThrow(() -> ObjectTools.requireEmpty(null, "msg"));
        }

        @Test
        @DisplayName("formatted message is applied when non-empty")
        void formattedMessage() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> ObjectTools.requireEmpty("x", "Value %s must be empty", "name")
            );
            assertEquals("Value name must be empty", ex.getMessage());
        }

        @Test
        @DisplayName("formatted message with no args behaves like plain message")
        void formattedMessageNoArgs() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> ObjectTools.requireEmpty("x", "Simple message")
            );
            assertEquals("Simple message", ex.getMessage());
        }

        @Test
        @DisplayName("formatting failure falls back to raw message")
        void formattingFailureFallsBack() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> ObjectTools.requireEmpty("x", "%s %s %s", "onlyOneArg")
            );
            assertEquals("%s %s %s", ex.getMessage());
        }

        @Test
        @DisplayName("exception message is correct for non-empty string")
        void messageForNonEmptyString() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> ObjectTools.requireEmpty("x", "String must be empty")
            );
            assertEquals("String must be empty", ex.getMessage());
        }

        @Test
        @DisplayName("returns original value for empty inputs")
        void returnsOriginalValue() {
            assertEquals("", ObjectTools.requireEmpty("", "msg"));

            var emptyList = List.of();
            assertSame(emptyList, ObjectTools.requireEmpty(emptyList, "msg"));

            var emptyMap = Map.of();
            assertSame(emptyMap, ObjectTools.requireEmpty(emptyMap, "msg"));

            var emptyObjArray = new Object[]{};
            assertSame(emptyObjArray, ObjectTools.requireEmpty(emptyObjArray, "msg"));

            int[] emptyIntArray = new int[]{};
            assertSame(emptyIntArray, ObjectTools.requireEmpty(emptyIntArray, "msg"));

            // null is considered empty
            assertNull(ObjectTools.requireEmpty(null, "msg"));

            //noinspection ConstantValue
            assertTrue(ObjectTools.requireEmpty("", "msg").isEmpty());

            //noinspection ConstantValue
            assertTrue(ObjectTools.requireEmpty("", "%s", "bar").isEmpty());

        }

        @Test
        @DisplayName("throws for non-empty values")
        void throwsForNonEmpty() {
            assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireEmpty("x", "msg"));
            assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireEmpty(List.of("x"), "msg"));
            assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireEmpty(Map.of("k", "v"), "msg"));
            assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireEmpty(new Object[]{"x"}, "msg"));
            assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireEmpty(new int[]{1}, "msg"));
            assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireEmpty(new Object(), "msg"));
        }

        @Test
        @DisplayName("throws when message is null or empty")
        void throwsForNullOrEmptyMessage() {
            assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireEmpty("", null));
            assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireEmpty("", ""));
            assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireEmpty("", "   "));
        }

        @Nested
        @DisplayName("requireAllEmpty(Collection) Tests")
        class RequireAllEmptyCollectionTests {

            @Test
            @DisplayName("does not throw for null or empty collections")
            void doesNotThrow() {
                assertDoesNotThrow(() -> ObjectTools.requireAllEmpty(List.of(), "msg"));
                assertDoesNotThrow(() -> ObjectTools.requireAllEmpty(Collections.emptyList(), "msg"));
            }

            @Test
            @DisplayName("formatted message is applied when non-empty element found")
            void formattedMessage() {
                IllegalArgumentException ex = assertThrows(
                        IllegalArgumentException.class,
                        () -> ObjectTools.requireAllEmpty(List.of("x"), "Value %s must be empty", "name")
                );
                assertEquals("Value name must be empty", ex.getMessage());
            }

            @Test
            @DisplayName("formatted message with no args behaves like plain message")
            void formattedMessageNoArgs() {
                IllegalArgumentException ex = assertThrows(
                        IllegalArgumentException.class,
                        () -> ObjectTools.requireAllEmpty(List.of("x"), "Simple message")
                );
                assertEquals("Simple message", ex.getMessage());
            }

            @Test
            @DisplayName("formatting failure falls back to raw message")
            void formattingFailureFallsBack() {
                IllegalArgumentException ex = assertThrows(
                        IllegalArgumentException.class,
                        () -> ObjectTools.requireAllEmpty(List.of("x"), "%s %s %s", "onlyOneArg")
                );
                assertEquals("%s %s %s", ex.getMessage());
            }

            @Test
            @DisplayName("throws for non-empty elements")
            void throwsForNonEmptyElements() {
                assertThrows(IllegalArgumentException.class,
                        () -> ObjectTools.requireAllEmpty(List.of("x"), "msg"));
                assertThrows(IllegalArgumentException.class,
                        () -> ObjectTools.requireAllEmpty(List.of(List.of("x")), "msg"));
                assertThrows(IllegalArgumentException.class,
                        () -> ObjectTools.requireAllEmpty(List.of(Map.of("k", "v")), "msg"));
                assertThrows(IllegalArgumentException.class,
                        () -> ObjectTools.requireAllEmpty(List.of(new Object[]{"x"}), "msg"));
            }

            @Test
            @DisplayName("throws for null collection")
            void throwsForNullCollection() {
                assertThrows(IllegalArgumentException.class,
                        () -> ObjectTools.requireAllEmpty((Collection<?>) null, "msg"));
            }

            @Test
            @DisplayName("throws when message is null or empty")
            void throwsForNullOrEmptyMessage() {
                assertThrows(IllegalArgumentException.class,
                        () -> ObjectTools.requireAllEmpty(List.of(), null));
                assertThrows(IllegalArgumentException.class,
                        () -> ObjectTools.requireAllEmpty(List.of(), ""));
                assertThrows(IllegalArgumentException.class,
                        () -> ObjectTools.requireAllEmpty(List.of(), "   "));
            }
        }
    }

    @Nested
    @DisplayName("requireNotEmpty Tests")
    class RequireNotEmptyTests {

        @Test
        @DisplayName("does not throw for non-empty values")
        void doesNotThrow() {
            assertDoesNotThrow(() -> ObjectTools.requireNotEmpty("x", "msg"));
            assertDoesNotThrow(() -> ObjectTools.requireNotEmpty(List.of("x"), "msg"));
            assertDoesNotThrow(() -> ObjectTools.requireNotEmpty(Map.of("k", "v"), "msg"));
            assertDoesNotThrow(() -> ObjectTools.requireNotEmpty(new int[]{1}, "msg"));
            assertDoesNotThrow(() -> ObjectTools.requireNotEmpty(new Object[]{"x"}, "msg"));
            assertDoesNotThrow(() -> ObjectTools.requireNotEmpty(new Object(), "msg"));
        }

        @Test
        @DisplayName("formatted message is applied when empty")
        void formattedMessage() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> ObjectTools.requireNotEmpty("", "Value %s must not be empty", "name")
            );
            assertEquals("Value name must not be empty", ex.getMessage());
        }

        @Test
        @DisplayName("formatted message with no args behaves like plain message")
        void formattedMessageNoArgs() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> ObjectTools.requireNotEmpty("", "Simple message")
            );
            assertEquals("Simple message", ex.getMessage());
        }

        @Test
        @DisplayName("formatting failure falls back to raw message")
        void formattingFailureFallsBack() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> ObjectTools.requireNotEmpty("", "%s %s %s", "onlyOneArg")
            );
            assertEquals("%s %s %s", ex.getMessage());
        }

        @Test
        @DisplayName("exception message is correct for empty string")
        void messageForEmptyString() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> ObjectTools.requireNotEmpty("", "String cannot be empty")
            );
            assertEquals("String cannot be empty", ex.getMessage());
        }

        @Test
        @DisplayName("exception message is correct for null value")
        void messageForNullValue() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> ObjectTools.requireNotEmpty(null, "Custom error message")
            );
            assertEquals("Custom error message", ex.getMessage());
        }

        @Test
        @DisplayName("multiple format args are applied correctly")
        void multipleFormatArgs() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> ObjectTools.requireNotEmpty("", "%s:%s:%s", "a", "b", "c")
            );
            assertEquals("a:b:c", ex.getMessage());
        }

        @Test
        @DisplayName("returns original value for non-empty inputs")
        void returnsOriginalValue() {
            assertEquals("x", ObjectTools.requireNotEmpty("x", "msg"));

            var list = List.of("x");
            assertSame(list, ObjectTools.requireNotEmpty(list, "msg"));

            var map = Map.of("k", "v");
            assertSame(map, ObjectTools.requireNotEmpty(map, "msg"));

            int[] arr = new int[]{1};
            assertSame(arr, ObjectTools.requireNotEmpty(arr, "msg"));

            var objArr = new Object[]{"x"};
            assertSame(objArr, ObjectTools.requireNotEmpty(objArr, "msg"));

            var o = new Object();
            assertSame(o, ObjectTools.requireNotEmpty(o, "msg"));

            //noinspection SimplifiableAssertion, ConstantValue
            assertTrue(ObjectTools.requireNotEmpty("x", "msg").equals("x"));

            //noinspection SimplifiableAssertion, ConstantValue
            assertTrue(ObjectTools.requireNotEmpty("x", "%s", "bar").equals("x"));

        }

        @Test
        @DisplayName("throws for null or empty values")
        void throwsForEmpty() {
            assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireNotEmpty(null, "msg"));
            assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireNotEmpty("", "msg"));
            assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireNotEmpty(List.of(), "msg"));
            assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireNotEmpty(Map.of(), "msg"));
            assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireNotEmpty(new Object[]{}, "msg"));
            assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireNotEmpty(new int[]{}, "msg"));
        }

        @Test
        @DisplayName("throws when message is null or empty")
        void throwsForNullOrEmptyMessage() {
            assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireNotEmpty("x", null));
            assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireNotEmpty("x", ""));
            assertThrows(IllegalArgumentException.class,
                    () -> ObjectTools.requireNotEmpty("x", "   "));
        }

        @Nested
        @DisplayName("requireAllNotEmpty(Collection) Tests")
        class RequireAllNotEmptyCollectionTests {

            @Test
            @DisplayName("does not throw for collections with all non-empty elements")
            void doesNotThrow() {
                assertDoesNotThrow(() -> ObjectTools.requireAllNotEmpty(List.of("x"), "msg"));
                assertDoesNotThrow(() -> ObjectTools.requireAllNotEmpty(List.of(List.of("x")), "msg"));
                assertDoesNotThrow(() -> ObjectTools.requireAllNotEmpty(List.of(Map.of("k", "v")), "msg"));
                assertDoesNotThrow(() -> ObjectTools.requireAllNotEmpty(List.of(new Object[]{"x"}), "msg"));
            }

            @Test
            @DisplayName("formatted message is applied when empty element found")
            void formattedMessage() {
                IllegalArgumentException ex = assertThrows(
                        IllegalArgumentException.class,
                        () -> ObjectTools.requireAllNotEmpty(List.of(""), "Value %s must not be empty", "name")
                );
                assertEquals("Value name must not be empty", ex.getMessage());
            }

            @Test
            @DisplayName("formatted message with no args behaves like plain message")
            void formattedMessageNoArgs() {
                IllegalArgumentException ex = assertThrows(
                        IllegalArgumentException.class,
                        () -> ObjectTools.requireAllNotEmpty(List.of(""), "Simple message")
                );
                assertEquals("Simple message", ex.getMessage());
            }

            @Test
            @DisplayName("formatting failure falls back to raw message")
            void formattingFailureFallsBack() {
                IllegalArgumentException ex = assertThrows(
                        IllegalArgumentException.class,
                        () -> ObjectTools.requireAllNotEmpty(List.of(""), "%s %s %s", "onlyOneArg")
                );
                assertEquals("%s %s %s", ex.getMessage());
            }

            @Test
            @DisplayName("throws for empty elements")
            void throwsForEmptyElements() {
                assertThrows(IllegalArgumentException.class,
                        () -> ObjectTools.requireAllNotEmpty(List.of(""), "msg"));
                assertThrows(IllegalArgumentException.class,
                        () -> ObjectTools.requireAllNotEmpty(List.of(List.of()), "msg"));
                assertThrows(IllegalArgumentException.class,
                        () -> ObjectTools.requireAllNotEmpty(List.of(Map.of()), "msg"));
                assertThrows(IllegalArgumentException.class,
                        () -> ObjectTools.requireAllNotEmpty(List.of(new Object[]{}), "msg"));
                assertThrows(IllegalArgumentException.class,
                        () -> ObjectTools.requireAllNotEmpty(Collections.singletonList(null), "msg"));
            }

            @Test
            @DisplayName("throws for null or empty collection")
            void throwsForNullOrEmptyCollection() {
                assertThrows(IllegalArgumentException.class,
                        () -> ObjectTools.requireAllNotEmpty((Collection<?>) null, "msg"));
                assertThrows(IllegalArgumentException.class,
                        () -> ObjectTools.requireAllNotEmpty(List.of(), "msg"));
            }

            @Test
            @DisplayName("throws when message is null or empty")
            void throwsForNullOrEmptyMessage() {
                assertThrows(IllegalArgumentException.class,
                        () -> ObjectTools.requireAllNotEmpty(List.of("x"), null));
                assertThrows(IllegalArgumentException.class,
                        () -> ObjectTools.requireAllNotEmpty(List.of("x"), ""));
                assertThrows(IllegalArgumentException.class,
                        () -> ObjectTools.requireAllNotEmpty(List.of("x"), "   "));
            }
        }
    }
}