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

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import rife.bld.extension.testing.VisibleForTesting;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.IllegalFormatException;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.logging.Logger;

/**
 * Object Tools.
 *
 * <p>Unified utility methods for emptiness-checking objects, arrays,
 * collections, maps, and character sequences.</p>
 *
 * <p>Emptiness is defined for {@link CharSequence}, {@link Collection},
 * {@link Map}, and arrays. All other non-{@code null} objects are considered not empty.</p>
 *
 * <p>If the value is an array, {@link Collection}, or {@link Map},
 * predicates and validators apply to all elements/entries recursively.</p>
 *
 * <p><b>Map key checking:</b> For {@link Map} containers, both keys and values
 * are evaluated by emptiness predicates. Keys of non-container, non-{@link CharSequence}
 * types (e.g. {@link Integer}, enum constants) are never considered empty.
 * This means a map with such keys will never report all keys as empty, which
 * can affect the result of {@link #allEmpty(Object)} and {@link #anyEmpty(Object)}.</p>
 *
 * <p><b>Validation order:</b> All {@code require*} methods throw {@link NullPointerException}
 * first if the value or any nested element is {@code null}, then {@link IllegalArgumentException}
 * if the value is empty or blank.</p>
 *
 * <p><b>Logging:</b> This class uses {@code java.util.logging} (JUL) to warn
 * about malformed format strings. In applications that bridge JUL to another
 * framework (e.g. SLF4J, Log4j2), warnings will route through that bridge.</p>
 *
 * @author <a href="https://erik.thauvin.net/">Erik C. Thauvin</a>
 * @since 1.0
 */
public final class ObjectTools {

    @VisibleForTesting
    static final Predicate<Object> isEmptyPredicate = ObjectTools::isEmpty;
    @VisibleForTesting
    static final Predicate<Object> isNotEmptyPredicate = ObjectTools::isNotEmpty;

    private static final Logger logger = Logger.getLogger(ObjectTools.class.getName());

    private ObjectTools() {
    }

    /**
     * Returns {@code true} if the value and all elements/entries are empty.
     *
     * <p>For non-containers, equivalent to {@link #isEmpty(Object)}.
     * For arrays, {@link Collection}, or {@link Map}, all elements/entries
     * must be {@code null} or empty.</p>
     *
     * <p><b>Note:</b> An empty container (size 0) vacuously satisfies this
     * condition and returns {@code true}. This is the logical complement of
     * requiring <em>any</em> element to be non-empty, not the inverse of
     * {@link #allNotEmpty(Object)} for empty containers.</p>
     *
     * <p>For a {@link Map}, both keys and values are checked. See the class-level
     * note on map key checking.</p>
     *
     * <p>Example with an empty list:</p>
     * <pre>{@code
     * List<String> empty = List.of();
     * allEmpty(empty) // true (vacuously: no non-empty elements)
     * allNotEmpty(empty) // false (no non-empty elements to satisfy the condition)
     * }</pre>
     *
     * @param value the value to inspect; may be {@code null}
     * @return {@code true} if the value and all elements are empty
     * @since 1.3
     */
    public static boolean allEmpty(@Nullable Object value) {
        if (value == null) {
            return true;
        }
        return isContainer(value)
                ? checkAll(value, ObjectTools::isEmpty)
                : isEmpty(value);
    }

    /**
     * Returns {@code true} if the value and all elements/entries are not empty.
     *
     * <p>For non-containers, equivalent to {@link #isNotEmpty(Object)}.
     * For arrays, {@link Collection}, or {@link Map}, the container must be
     * not empty and all elements/entries must be not {@code null} and not empty.</p>
     *
     * <p><b>Note:</b> An empty container returns {@code false} because there
     * are no non-empty elements to satisfy the condition. This is intentionally
     * asymmetric with {@link #allEmpty(Object)} for empty containers.</p>
     *
     * <p>For a {@link Map}, both keys and values are checked. See the class-level
     * note on map key checking.</p>
     *
     * <p>Example with a non-empty list:</p>
     * <pre>{@code
     * List<String> items = List.of("foo", "bar");
     * allNotEmpty(items) // true (all elements are non-null and non-empty)
     * allEmpty(items) // false (elements are present and non-empty)
     * }</pre>
     *
     * @param value the value to inspect; may be {@code null}
     * @return {@code true} if the value and all elements are not empty
     * @since 1.3
     */
    public static boolean allNotEmpty(@Nullable Object value) {
        if (value == null) {
            return false;
        }
        return isContainer(value)
                ? isNotEmpty(value) && checkAll(value, ObjectTools::isNotEmpty)
                : isNotEmpty(value);
    }

    /**
     * Returns {@code true} if the value or any element/entry is empty.
     *
     * <p>For non-containers, equivalent to {@link #isEmpty(Object)}.
     * For arrays, {@link Collection}, or {@link Map}, returns {@code true}
     * if the container is empty or any element/entry is {@code null} or empty.</p>
     *
     * <p>For a {@link Map}, both keys and values are checked. See the class-level
     * note on map key checking.</p>
     *
     * @param value the value to inspect; may be {@code null}
     * @return {@code true} if the value or any element is empty
     * @since 1.3
     */
    public static boolean anyEmpty(@Nullable Object value) {
        if (value == null) {
            return true;
        }
        return isContainer(value)
                ? isEmpty(value) || checkAny(value, ObjectTools::isEmpty)
                : isEmpty(value);
    }

    /**
     * Returns {@code true} if the value or any element/entry is not empty.
     *
     * <p>For non-containers, equivalent to {@link #isNotEmpty(Object)}.
     * For arrays, {@link Collection}, or {@link Map}, returns {@code true}
     * if any element/entry is not {@code null} and not empty.</p>
     *
     * <p>For a {@link Map}, both keys and values are checked. See the class-level
     * note on map key checking.</p>
     *
     * @param value the value to inspect; may be {@code null}
     * @return {@code true} if the value or any element is not empty
     * @since 1.3
     */
    public static boolean anyNotEmpty(@Nullable Object value) {
        if (value == null) {
            return false;
        }
        return isContainer(value)
                ? checkAny(value, ObjectTools::isNotEmpty)
                : isNotEmpty(value);
    }

    /**
     * Iterates {@code container} and returns {@code true} if {@code predicate} holds for
     * every element (or key/value pair for {@link Map}s). Returns {@code true} vacuously
     * for containers with no elements.
     */
    private static boolean checkAll(Object container, Predicate<Object> predicate) {
        return forEachElement(container, predicate, true);
    }

    /**
     * Iterates {@code container} and returns {@code true} if {@code predicate} holds for
     * at least one element (or key/value for {@link Map}s). Returns {@code false} for
     * containers with no elements.
     */
    private static boolean checkAny(Object container, Predicate<Object> predicate) {
        return forEachElement(container, predicate, false);
    }

    private static boolean forEachBoxedPrimitiveArray(Object container, Predicate<Object> predicate, boolean allMode) {
        int len = Array.getLength(container);
        for (int i = 0; i < len; i++) {
            if (predicate.test(Array.get(container, i)) != allMode) {
                return !allMode;
            }
        }
        return allMode;
    }

    private static boolean forEachCollection(Collection<?> c, Predicate<Object> predicate, boolean allMode) {
        for (Object v : c) {
            if (predicate.test(v) != allMode) {
                return !allMode;
            }
        }
        return allMode;
    }

    /**
     * Shared iteration logic for {@link #checkAll} and {@link #checkAny}.
     *
     * @param container the container to iterate; must satisfy {@link #isContainer}
     * @param predicate the test to apply to each element/key/value
     * @param allMode   if {@code true}, returns {@code false} on the first predicate failure
     *                  (all-match semantics); if {@code false}, returns {@code true} on the
     *                  first predicate success (any-match semantics)
     * @return the result of the all-match or any-match traversal
     * @implNote The fast path for primitive arrays using reference-identity comparison
     * ({@code predicate == isEmptyPredicate}) relies on callers passing the cached field
     * references directly. This assumption holds for all internal callers. Custom predicates
     * passed from outside always take the slow (boxing) path. For the built-in predicates,
     * primitives are never considered empty, so the length alone determines the result without
     * reading elements.
     */
    private static boolean forEachElement(Object container, Predicate<Object> predicate, boolean allMode) {
        if (container instanceof Object[] arr) {
            return forEachObjectArray(arr, predicate, allMode);
        }
        if (container instanceof Collection<?> c) {
            return forEachCollection(c, predicate, allMode);
        }
        if (container instanceof Map<?, ?> m) {
            return forEachMap(m, predicate, allMode);
        }
        if (container.getClass().isArray()) {
            return forEachPrimitiveArray(container, predicate, allMode);
        }
        throw new AssertionError("forEachElement called on non-container: " + container.getClass());
    }

    private static boolean forEachMap(Map<?, ?> m, Predicate<Object> predicate, boolean allMode) {
        for (var e : m.entrySet()) {
            if (predicate.test(e.getKey()) != allMode) {
                return !allMode;
            }
            if (predicate.test(e.getValue()) != allMode) {
                return !allMode;
            }
        }
        return allMode;
    }

    private static boolean forEachObjectArray(Object[] arr, Predicate<Object> predicate, boolean allMode) {
        for (Object v : arr) {
            if (predicate.test(v) != allMode) {
                return !allMode;
            }
        }
        return allMode;
    }

    private static boolean forEachPrimitiveArray(Object container, Predicate<Object> predicate, boolean allMode) {
        if (isPrimitiveFastPath(predicate)) {
            return handlePrimitiveFastPath(predicate, container, allMode);
        }
        return forEachBoxedPrimitiveArray(container, predicate, allMode);
    }

    /**
     * Formats a message with optional args. Falls back to the raw message if formatting
     * fails, logging a warning so callers are aware of the mismatched format string.
     */
    private static String formatMessage(String message, Object... args) {
        if (args == null || args.length == 0) {
            return message;
        }
        try {
            return String.format(message, args);
        } catch (IllegalFormatException e) {
            logger.warning(() -> "ObjectTools: message formatting failed for pattern \""
                    + message + "\": " + e.getMessage());
            return message;
        }
    }

    @VisibleForTesting
    static boolean handlePrimitiveFastPath(Predicate<Object> predicate, Object container, boolean allMode) {
        int len = Array.getLength(container);
        if (predicate.equals(isEmptyPredicate)) {
            return allMode ? len == 0 : len > 0;
        }
        // isNotEmptyPredicate
        return allMode ? len > 0 : len == 0;
    }

    /**
     * Returns {@code true} if the value and all nested elements are non-{@code null}.
     *
     * <p><b>Note:</b> This method is recursive. Pathologically deep container nesting
     * may cause {@link StackOverflowError}.</p>
     */
    private static boolean isAllNonNull(Object value) {
        return value != null && (!isContainer(value) || checkAll(value, ObjectTools::isAllNonNull));
    }

    /**
     * Returns {@code true} if {@code value} is an array, {@link Collection}, or {@link Map}.
     * Covers both object arrays and primitive arrays. Returns {@code false} if {@code value} is {@code null}.
     */
    private static boolean isContainer(Object value) {
        if (value == null) {
            return false;
        }
        return value.getClass().isArray()
                || value instanceof Collection<?>
                || value instanceof Map<?, ?>;
    }

    /**
     * Determines whether the given {@code value} is {@code null} or empty.
     *
     * <p>For arrays, {@link Collection}, or {@link Map}, returns {@code true}
     * only if the container itself is empty. To check elements as well, use
     * {@link #allEmpty(Object)}.</p>
     *
     * @param value the value to inspect; may be {@code null}
     * @return {@code true} if the {@code value} is {@code null} or empty
     */
    public static boolean isEmpty(@Nullable Object value) {
        if (value == null) {
            return true;
        }
        if (value instanceof CharSequence cs) {
            return cs.isEmpty();
        }
        if (value instanceof Collection<?> c) {
            return c.isEmpty();
        }
        if (value instanceof Map<?, ?> m) {
            return m.isEmpty();
        }
        return value.getClass().isArray() && Array.getLength(value) == 0;
    }

    /**
     * Determines whether the given {@code value} is not {@code null} and not empty.
     *
     * @param value the value to inspect; may be {@code null}
     * @return {@code true} if the {@code value} is not {@code null} and not empty
     */
    public static boolean isNotEmpty(@Nullable Object value) {
        return !isEmpty(value);
    }

    private static boolean isPrimitiveFastPath(Predicate<Object> predicate) {
        return predicate.equals(isEmptyPredicate) || predicate.equals(isNotEmptyPredicate);
    }

    /**
     * Requires the value to be empty.
     *
     * <p>If {@code value} is an array, {@link Collection}, or {@link Map},
     * all elements/entries must be empty. Throws {@link NullPointerException}
     * if the value or any element is {@code null}.</p>
     *
     * @param value   the value to validate and return; must not be {@code null}
     * @param message the exception message; must not be {@code null}, empty, or blank
     * @param <T>     the value type
     * @return the validated value
     * @throws NullPointerException     if the value or any element is {@code null}
     * @throws IllegalArgumentException if the value is not empty
     * @throws IllegalArgumentException if {@code message} is {@code null}, empty, or blank
     * @since 1.3
     */
    public static <T> T requireEmpty(@NonNull T value, @NonNull String message) {
        requireNonBlankMessage(message);
        validateNullsFirst(value, message);
        if (!allEmpty(value)) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    /**
     * Requires the value to be empty.
     *
     * <p>If {@code value} is an array, {@link Collection}, or {@link Map},
     * all elements/entries must be empty. Throws {@link NullPointerException}
     * if the value or any element is {@code null}. The message may contain
     * {@link String#format(String, Object...)} placeholders resolved using the supplied
     * {@code args}. If formatting fails, the raw message is used and a warning is logged.</p>
     *
     * @param value   the value to validate and return; must not be {@code null}
     * @param message the exception message or format string; must not be {@code null}, empty, or blank
     * @param args    optional arguments used to format the {@code message}
     * @param <T>     the value type
     * @return the validated value
     * @throws NullPointerException     if the value or any element is {@code null}
     * @throws IllegalArgumentException if the value is not empty
     * @throws IllegalArgumentException if {@code message} is {@code null}, empty, or blank
     * @since 1.3
     */
    public static <T> T requireEmpty(@NonNull T value, @NonNull String message, @Nullable Object... args) {
        return requireEmpty(value, formatMessage(message, args));
    }

    /**
     * Validates that a message string is not {@code null}, empty, or blank (whitespace-only).
     */
    private static void requireNonBlankMessage(String message) {
        if (TextTools.isBlank(message)) {
            throw new IllegalArgumentException("message must not be null, empty, or blank");
        }
    }

    /**
     * Requires the value to be not {@code null}, and all elements/entries to be not {@code null}.
     *
     * <p>If {@code value} is an array, {@link Collection}, or {@link Map},
     * the container must be not {@code null} and all elements/entries must be
     * not {@code null}. Unlike {@link #requireNotEmpty(Object, String)}, this method
     * allows empty containers as long as they contain no {@code null} elements.</p>
     *
     * <p>Exception message is constructed from {@code context} as
     * {@code "{context} must not be null"}.</p>
     *
     * <p><b>Note:</b> This method is recursive. For nested containers, all elements
     * at all depths are checked. Pathologically deep nesting may cause
     * {@link StackOverflowError}.</p>
     *
     * @param value   the value to validate and return; must not be {@code null}
     * @param context the context string used in exception message; must not be {@code null}, empty, or blank
     * @param <T>     the value type
     * @return the validated value, never {@code null}
     * @throws NullPointerException     if the {@code value} is {@code null} or contains {@code null} elements
     * @throws IllegalArgumentException if {@code context} is {@code null}, empty, or blank
     * @since 1.3
     */
    public static <T> T requireNonNull(@NonNull T value, @NonNull String context) {
        requireNonBlankMessage(context);
        return requireNonNull(value, context + " must not be null", new Object[0]);
    }

    /**
     * Requires the value to be not {@code null}, and all elements/entries to be not {@code null}.
     *
     * <p>If {@code value} is an array, {@link Collection}, or {@link Map},
     * the container must be not {@code null} and all elements/entries must be
     * not {@code null}. Unlike {@link #requireNotEmpty(Object, String, String, Object...)},
     * this method allows empty containers as long as they contain no {@code null} elements.</p>
     *
     * <p>The message may contain {@link String#format(String, Object...)} placeholders
     * resolved using the supplied {@code args}. If formatting fails, the raw message is used
     * and a warning is logged.</p>
     *
     * <p><b>Note:</b> This method is recursive. For nested containers, all elements
     * at all depths are checked. Pathologically deep nesting may cause
     * {@link StackOverflowError}.</p>
     *
     * @param value   the value to validate and return; must not be {@code null}
     * @param message the message for {@code NullPointerException}; must not be {@code null}, empty, or blank
     * @param args    optional arguments used to format the {@code message}
     * @param <T>     the value type
     * @return the validated value, never {@code null}
     * @throws NullPointerException     if the {@code value} is {@code null} or contains {@code null} elements
     * @throws IllegalArgumentException if {@code message} is {@code null}, empty, or blank
     * @since 1.3
     */
    public static <T> T requireNonNull(@NonNull T value, @NonNull String message, @Nullable Object... args) {
        requireNonBlankMessage(message);
        var formatted = formatMessage(message, args);
        validateNullsFirst(value, formatted);
        return value;
    }

    /**
     * Requires the value to be not {@code null} and not empty.
     *
     * <p>If {@code value} is an array, {@link Collection}, or {@link Map},
     * the container must be not empty and all elements/entries must be
     * not {@code null} and not empty.</p>
     *
     * <p>Exception messages are constructed from {@code context} as
     * {@code "{context} must not be null"} and {@code "{context} must not be empty"}.</p>
     *
     * @param value   the value to validate and return; must not be {@code null} or empty
     * @param context the context string used in exception messages; must not be {@code null}, empty, or blank
     * @param <T>     the value type
     * @return the validated value, never {@code null} or empty
     * @throws NullPointerException     if the {@code value} or any element is {@code null}
     * @throws IllegalArgumentException if the {@code value} is empty
     * @throws IllegalArgumentException if {@code context} is {@code null}, empty, or blank
     * @since 1.3
     */
    public static <T> T requireNotEmpty(@NonNull T value, @NonNull String context) {
        requireNonBlankMessage(context);
        return requireNotEmpty(value,
                context + " must not be null",
                context + " must not be empty");
    }

    /**
     * Requires the value to be not {@code null} and not empty.
     *
     * <p>If {@code value} is an array, {@link Collection}, or {@link Map},
     * the container must be not empty and all elements/entries must be
     * not {@code null} and not empty. Throws {@link NullPointerException}
     * if the value or any element is {@code null}.</p>
     *
     * @param value        the value to validate and return; must not be {@code null} or empty
     * @param nullMessage  the message for {@code NullPointerException}; must not be {@code null}, empty, or blank
     * @param emptyMessage the message for {@code IllegalArgumentException}; must not be {@code null}, empty, or blank
     * @param <T>          the value type
     * @return the validated value, never {@code null} or empty
     * @throws NullPointerException     if the {@code value} or any element is {@code null}
     * @throws IllegalArgumentException if the {@code value} is empty
     * @throws IllegalArgumentException if either message is {@code null}, empty, or blank
     * @since 1.3
     */
    public static <T> T requireNotEmpty(@NonNull T value, @NonNull String nullMessage, @NonNull String emptyMessage) {
        requireNonBlankMessage(nullMessage);
        requireNonBlankMessage(emptyMessage);
        validateNullsFirst(value, nullMessage);
        if (!allNotEmpty(value)) {
            throw new IllegalArgumentException(emptyMessage);
        }
        return value;
    }

    /**
     * Requires the value to be not {@code null} and not empty.
     *
     * <p>If {@code value} is an array, {@link Collection}, or {@link Map},
     * the container must be not empty and all elements/entries must be
     * not {@code null} and not empty. Throws {@link NullPointerException}
     * if the value or any element is {@code null}. Both messages may contain
     * {@link String#format(String, Object...)} placeholders resolved using the supplied
     * {@code args}. If formatting fails, the raw message is used and a warning is logged.</p>
     *
     * @param value        the value to validate and return; must not be {@code null} or empty
     * @param nullMessage  the message for {@code NullPointerException}; must not be {@code null}, empty, or blank
     * @param emptyMessage the message for {@code IllegalArgumentException}; must not be {@code null}, empty, or blank
     * @param args         optional arguments used to format the messages
     * @param <T>          the value type
     * @return the validated value, never {@code null} or empty
     * @throws NullPointerException     if the {@code value} or any element is {@code null}
     * @throws IllegalArgumentException if the {@code value} is empty
     * @throws IllegalArgumentException if either message is {@code null}, empty, or blank
     * @since 1.3
     */
    public static <T> T requireNotEmpty(@NonNull T value,
                                        @NonNull String nullMessage,
                                        @NonNull String emptyMessage,
                                        @Nullable Object... args) {
        return requireNotEmpty(value, formatMessage(nullMessage, args), formatMessage(emptyMessage, args));
    }

    /**
     * Validates that the value and all nested elements are non-{@code null}.
     * Throws {@link NullPointerException} on first null encountered.
     *
     * <p><b>Note:</b> This method is recursive. Pathologically deep container nesting
     * may cause {@link StackOverflowError}.</p>
     */
    @SuppressWarnings("PMD.AvoidThrowingNullPointerException")
    @SuppressFBWarnings("NP_PARAMETER_MUST_BE_NONNULL_BUT_MARKED_AS_NULLABLE")
    private static void validateNullsFirst(@Nullable Object value, String message) {
        Objects.requireNonNull(value, message);
        if (isContainer(value) && !isAllNonNull(value)) {
            throw new NullPointerException(message);
        }
    }
}