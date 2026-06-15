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
import rife.bld.extension.tools.internal.ToolsSupport;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.logging.Level;
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
 * <p>For text-specific operations like blank-checking, see {@link TextTools}.</p>
 *
 * @author <a href="https://erik.thauvin.net/">Erik C. Thauvin</a>
 * @since 1.0
 */
public final class ObjectTools {

    /**
     * Maximum nesting depth checked during null validation. Containers nested
     * deeper than this limit are not traversed, preventing {@link StackOverflowError}
     * on pathologically deep structures.
     */
    private static final int MAX_NESTING_DEPTH = 128;
    private static final String MESSAGE_SUPPLIER = "messageSupplier";
    private static final String MUST_NOT_BE_NULL = " must not be null";

    /**
     * Zero values keyed by boxed numeric type, used by {@link #zeroOf(Object)}.
     */
    private static final Map<Class<?>, Object> ZERO_BY_TYPE = Map.of(
            Integer.class, 0,
            Long.class, 0L,
            Double.class, 0.0d,
            Float.class, 0.0f,
            Short.class, (short) 0,
            Byte.class, (byte) 0,
            BigInteger.class, BigInteger.ZERO,
            BigDecimal.class, BigDecimal.ZERO
    );

    private static final Predicate<Object> isEmptyPredicate = ObjectTools::isEmpty;
    private static final Predicate<Object> isNotEmptyPredicate = ObjectTools::isNotEmpty;
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
     * @apiNote An empty container (size 0) vacuously satisfies this
     * condition and returns {@code true}. This is the logical complement of
     * requiring <em>any</em> element to be non-empty, not the inverse of
     * {@link #allNotEmpty(Object)} for empty containers.
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
     * @apiNote An empty container returns {@code false} because there
     * are no non-empty elements to satisfy the condition. This is intentionally
     * asymmetric with {@link #allEmpty(Object)} for empty containers.</p>
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

    /**
     * Validates that the value and all nested elements are non-{@code null}.
     * Throws {@link NullPointerException} on first null encountered.
     *
     * <p>Uses an iterative traversal (not recursive) up to {@value #MAX_NESTING_DEPTH}
     * levels deep to prevent {@link StackOverflowError}.</p>
     */
    @SuppressWarnings("PMD.AvoidThrowingNullPointerException")
    @SuppressFBWarnings(value = "NP_PARAMETER_MUST_BE_NONNULL_BUT_MARKED_AS_NULLABLE",
            justification = "intentionally nullable — we perform the null check and throw NPE ourselves")
    private static void checkForNulls(@Nullable Object value, String message) {
        Objects.requireNonNull(value, message);
        if (isContainer(value) && !isAllNonNull(value)) {
            throw new NullPointerException(message);
        }
    }

    private static boolean forEachBoxedPrimitive(Object container, Predicate<Object> predicate, boolean allMode) {
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
     * @implNote The fast path for primitive arrays uses reference-identity comparison
     * ({@code predicate == isEmptyPredicate} / {@code predicate == isNotEmptyPredicate})
     * and relies on callers passing the cached static field references directly.
     * This assumption holds for all internal callers. Custom predicates passed from
     * outside always take the slow (boxing) path. For the built-in predicates,
     * primitives are never considered empty, so the length alone determines the
     * result without reading elements.
     */
    private static boolean forEachElement(Object container, Predicate<Object> predicate, boolean allMode) {
        if (container == null) {
            return predicate.test(null) == allMode;
        }

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
        // Should never be reached: forEachElement is only called from checkAll/checkAny,
        // which are only called when isContainer() returned true. If isContainer() is
        // extended to new types, this method must be updated in tandem.
        throw new IllegalStateException("forEachElement called on unhandled container type: "
                + container.getClass().getName()
                + ". Update forEachElement to handle this type.");
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

    @SuppressWarnings("PMD.CompareObjectsWithEquals")
    private static boolean forEachPrimitiveArray(Object container, Predicate<Object> predicate, boolean allMode) {
        if (isPrimitiveFastPath(predicate)) {
            int len = Array.getLength(container);
            // Primitives: emptiness depends only on length, not allMode
            // allEmpty & anyEmpty: len == 0
            // allNotEmpty & anyNotEmpty: len > 0
            return predicate == isEmptyPredicate ? len == 0 : len > 0;
        }
        return forEachBoxedPrimitive(container, predicate, allMode);
    }

    /**
     * Requires the value to be not {@code null}, and all elements/entries to be not {@code null}.
     *
     * <p>Nested containers are checked iteratively up to {@value #MAX_NESTING_DEPTH}
     * levels deep. If the depth limit is reached, validation stops and a warning is
     * logged; deeper elements are assumed non-null. This prevents {@link StackOverflowError}
     * on pathologically deep structures while allowing operations to proceed.
     */
    private static boolean isAllNonNull(Object value) {
        if (value == null) {
            return false;
        }
        if (!isContainer(value)) {
            return true;
        }

        // Iterative DFS to avoid StackOverflowError on deep nesting.
        // ArrayDeque does not permit null elements, so nulls are detected
        // in pushContainerElements before any push is attempted.
        Deque<Object> stack = new ArrayDeque<>();
        stack.push(value);
        int depth = 0;

        while (!stack.isEmpty()) {
            Object current = stack.pop();
            // current is always non-null here: pushContainerElements returns false
            // immediately on null elements rather than pushing them.
            if (!isContainer(current)) {
                continue;
            }

            if (++depth > MAX_NESTING_DEPTH) { // depth counts container nesting levels, not total elements
                if (logger.isLoggable(Level.WARNING)) {
                    logger.warning("Null validation stopped at depth " + MAX_NESTING_DEPTH +
                            " to prevent StackOverflowError. Deeper elements not checked.");
                }
                break; // assume remaining are non-null
            }

            if (!pushContainerElements(current, stack)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns {@code true} if {@code value} is an array, {@link Collection}, or {@link Map}.
     * Covers both object arrays and primitive arrays. Returns {@code false} if {@code value} is {@code null}.
     * <p>
     * {@link CharSequence} is not a container; use {@link TextTools}.
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

    @SuppressWarnings("PMD.CompareObjectsWithEquals")
    private static boolean isPrimitiveFastPath(Predicate<Object> predicate) {
        // Reference-identity check: these are static final fields; == is intentional.
        return predicate == isEmptyPredicate || predicate == isNotEmptyPredicate;
    }

    /**
     * Pushes all elements of {@code container} onto {@code stack} for iterative null-checking.
     *
     * @return {@code false} immediately if any element is {@code null} (since a null element
     * means the container fails the all-non-null check, and {@link ArrayDeque} does
     * not permit null values); {@code true} if all elements were pushed successfully
     */
    private static boolean pushContainerElements(Object container, Deque<Object> stack) {
        if (container instanceof Object[] arr) {
            for (Object o : arr) {
                if (o == null) {
                    return false;
                }
                stack.push(o);
            }
        } else if (container instanceof Collection<?> c) {
            for (Object o : c) {
                if (o == null) {
                    return false;
                }
                stack.push(o);
            }
        } else if (container instanceof Map<?, ?> m) {
            for (var e : m.entrySet()) {
                if (e.getKey() == null || e.getValue() == null) {
                    return false;
                }
                stack.push(e.getKey());
                stack.push(e.getValue());
            }
        } else if (container.getClass().isArray()) {
            int len = Array.getLength(container);
            Class<?> ct = container.getClass().getComponentType();
            if (!ct.isPrimitive()) {
                for (int i = 0; i < len; i++) {
                    Object o = Array.get(container, i);
                    if (o == null) {
                        return false;
                    }
                    stack.push(o);
                }
            } // primitives: nothing to push, they can't be null or containers
        }

        return true;
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
        ToolsSupport.requireMessage(message);
        checkForNulls(value, message);
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
     * if the value or any element is {@code null}.</p>
     *
     * @param <T>             the value type
     * @param value           the value to validate and return; must not be {@code null}
     * @param messageSupplier the supplier of the exception message; must not be {@code null}
     * @return the validated value
     * @throws NullPointerException     if the value or any element is {@code null}
     * @throws IllegalArgumentException if the value is not empty
     * @throws IllegalArgumentException if {@code messageSupplier} is {@code null}, empty, or blank
     * @since 1.3
     */
    public static <T> T requireEmpty(@NonNull T value, @NonNull Supplier<String> messageSupplier) {
        Objects.requireNonNull(messageSupplier, MESSAGE_SUPPLIER + MUST_NOT_BE_NULL);
        return requireEmpty(value, messageSupplier.get());
    }

    /**
     * Checks that the specified value is strictly negative.
     *
     * <p>This method is generic and works with any {@link Comparable} type that has a natural
     * zero value, including {@link Integer}, {@link Long}, {@link Double}, {@link Float},
     * {@link Byte}, {@link Short}, {@link BigInteger}, and {@link BigDecimal}.
     *
     * @param <T>     the type of the value, must implement {@link Comparable}
     * @param value   the value to check for negativity; must not be {@code null}
     * @param context the context string used in exception messages; must not be {@code null}, empty, or blank
     * @return the validated value if it is less than zero
     * @throws NullPointerException     if {@code value} or {@code context} is {@code null}
     * @throws IllegalArgumentException if {@code context} is empty, or blank
     * @throws IllegalArgumentException if {@code value} is zero or positive
     * @throws IllegalArgumentException if {@code value} is of an unsupported type
     * @since 1.3
     */
    public static <T extends Comparable<T>> T requireNegative(@NonNull T value, @NonNull String context) {
        ToolsSupport.requireContext(context);
        return requireNegative(value, () -> context + " must be negative, got: " + value);
    }

    /**
     * Checks that the specified value is strictly negative.
     *
     * @param <T>             the type of the value, must implement {@link Comparable}
     * @param value           the value to check for negativity; must not be {@code null}
     * @param messageSupplier the supplier of the exception message; must not be {@code null}
     * @return the validated value if it is less than zero
     * @throws NullPointerException     if {@code value} or {@code messageSupplier} is {@code null}
     * @throws IllegalArgumentException if {@code value} is zero or positive
     * @throws IllegalArgumentException if {@code messageSupplier} is empty, or blank
     * @throws IllegalArgumentException if {@code value} is of an unsupported type
     * @since 1.3
     */
    public static <T extends Comparable<T>> T requireNegative(@NonNull T value,
                                                              @NonNull Supplier<String> messageSupplier) {
        Objects.requireNonNull(messageSupplier, MESSAGE_SUPPLIER + MUST_NOT_BE_NULL);
        requireNonNull(value, "value");

        if (value.compareTo(zeroOf(value)) >= 0) {
            throw new IllegalArgumentException(messageSupplier.get());
        }
        return value;
    }

    /**
     * Checks that the specified value is non-negative.
     *
     * <p>This method is generic and works with any {@link Comparable} type that has a natural
     * zero value, including {@link Integer}, {@link Long}, {@link Double}, {@link Float},
     * {@link Byte}, {@link Short}, {@link BigInteger}, and {@link BigDecimal}.
     *
     * @param <T>     the type of the value, must implement {@link Comparable}
     * @param value   the value to check for non-negativity; must not be {@code null}
     * @param context the context string used in exception messages; must not be {@code null}, empty, or blank
     * @return the validated value if it is greater than or equal to zero
     * @throws NullPointerException     if {@code value} or {@code context} is {@code null}
     * @throws IllegalArgumentException if {@code context} is empty, or blank
     * @throws IllegalArgumentException if {@code value} is negative
     * @throws IllegalArgumentException if {@code value} is of an unsupported type
     * @since 1.3
     */
    public static <T extends Comparable<T>> T requireNonNegative(@NonNull T value, @NonNull String context) {
        ToolsSupport.requireContext(context);
        return requireNonNegative(value, () -> context + " must be non-negative, got: " + value);
    }

    /**
     * Checks that the specified value is non-negative.
     *
     * @param <T>             the type of the value, must implement {@link Comparable}
     * @param value           the value to check for non-negativity; must not be {@code null}
     * @param messageSupplier the supplier of the exception message; must not be {@code null}
     * @return the validated value if it is greater than or equal to zero
     * @throws NullPointerException     if {@code value} or {@code messageSupplier} is {@code null}
     * @throws IllegalArgumentException if {@code value} is negative
     * @throws IllegalArgumentException if {@code messageSupplier} is empty, or blank
     * @throws IllegalArgumentException if {@code value} is of an unsupported type
     * @since 1.3
     */
    public static <T extends Comparable<T>> T requireNonNegative(@NonNull T value,
                                                                 @NonNull Supplier<String> messageSupplier) {
        Objects.requireNonNull(messageSupplier, MESSAGE_SUPPLIER + MUST_NOT_BE_NULL);
        requireNonNull(value, "value");

        if (value.compareTo(zeroOf(value)) < 0) {
            throw new IllegalArgumentException(messageSupplier.get());
        }
        return value;
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
     * <p><b>Note:</b> Nested containers are checked iteratively up to
     * {@value #MAX_NESTING_DEPTH} levels deep to prevent {@link StackOverflowError}.</p>
     *
     * @param value   the value to validate and return; must not be {@code null}
     * @param context the context string used in exception message; must not be {@code null}, empty, or blank
     * @param <T>     the value type
     * @return the validated value, never {@code null}
     * @throws NullPointerException     if the {@code value} is {@code null} or contains {@code null} elements
     * @throws NullPointerException     if {@code context} is {@code null}
     * @throws IllegalArgumentException if {@code context} is empty, or blank
     * @since 1.3
     */
    public static <T> T requireNonNull(@NonNull T value, @NonNull String context) {
        ToolsSupport.requireContext(context);
        Objects.requireNonNull(value, context + MUST_NOT_BE_NULL);
        checkForNulls(value, context + " must not contain null elements");
        return value;
    }

    /**
     * Requires the value to be not {@code null}, and all elements/entries to be not {@code null}.
     *
     * <p>If {@code value} is an array, {@link Collection}, or {@link Map},
     * the container must be not {@code null} and all elements/entries must be
     * not {@code null}. Unlike {@link #requireNotEmpty(Object, String, String)},
     * this method allows empty containers as long as they contain no {@code null} elements.</p>
     *
     * <p><b>Note:</b> Nested containers are checked iteratively up to
     * {@value #MAX_NESTING_DEPTH} levels deep to prevent {@link StackOverflowError}.</p>
     *
     * @param value           the value to validate and return; must not be {@code null}
     * @param messageSupplier the supplier of the exception message; must not be {@code null}
     * @param <T>             the value type
     * @return the validated value, never {@code null}
     * @throws NullPointerException     if the {@code value} is {@code null} or contains {@code null} elements
     * @throws NullPointerException     if {@code messageSupplier} is {@code null}
     * @throws IllegalArgumentException if {@code messageSupplier} is empty, or blank
     * @since 1.3
     */
    public static <T> T requireNonNull(@NonNull T value, @NonNull Supplier<String> messageSupplier) {
        Objects.requireNonNull(messageSupplier, MESSAGE_SUPPLIER + MUST_NOT_BE_NULL);
        var message = messageSupplier.get();
        Objects.requireNonNull(value, message);
        checkForNulls(value, message);
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
     * @throws NullPointerException     if {@code context} is {@code null}
     * @throws IllegalArgumentException if {@code context} is empty, or blank
     * @since 1.3
     */
    public static <T> T requireNotEmpty(@NonNull T value, @NonNull String context) {
        ToolsSupport.requireContext(context);
        return requireNotEmpty(value,
                context + MUST_NOT_BE_NULL,
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
        ToolsSupport.requireMessage(nullMessage);
        ToolsSupport.requireMessage(emptyMessage);
        checkForNulls(value, nullMessage);
        if (!allNotEmpty(value)) {
            throw new IllegalArgumentException(emptyMessage);
        }
        return value;
    }

    /**
     * Checks that the specified value is strictly positive.
     *
     * <p>This method is generic and works with any {@link Comparable} type that has a natural
     * zero value, including {@link Integer}, {@link Long}, {@link Double}, {@link Float},
     * {@link Byte}, {@link Short}, {@link BigInteger}, and {@link BigDecimal}.
     *
     * @param <T>     the type of the value, must implement {@link Comparable}
     * @param value   the value to check for positivity; must not be {@code null}
     * @param context the context string used in exception messages; must not be {@code null}, empty, or blank
     * @return the validated value if it is greater than zero
     * @throws NullPointerException     if {@code value} or {@code context} is {@code null}
     * @throws IllegalArgumentException if {@code context} is empty, or blank
     * @throws IllegalArgumentException if {@code value} is zero or negative
     * @throws IllegalArgumentException if {@code value} is of an unsupported type
     * @since 1.3
     */
    public static <T extends Comparable<T>> T requirePositive(@NonNull T value, @NonNull String context) {
        ToolsSupport.requireContext(context);
        return requirePositive(value, () -> context + " must be positive, got: " + value);
    }

    /**
     * Checks that the specified value is strictly positive.
     *
     * @param <T>             the type of the value, must implement {@link Comparable}
     * @param value           the value to check for positivity; must not be {@code null}
     * @param messageSupplier the supplier of the exception message; must not be {@code null}
     * @return the validated value if it is greater than zero
     * @throws NullPointerException     if {@code value} or {@code messageSupplier} is {@code null}
     * @throws IllegalArgumentException if {@code value} is zero or negative
     * @throws IllegalArgumentException if {@code messageSupplier} is empty, or blank
     * @throws IllegalArgumentException if {@code value} is of an unsupported type
     * @since 1.3
     */
    public static <T extends Comparable<T>> T requirePositive(@NonNull T value,
                                                              @NonNull Supplier<String> messageSupplier) {
        Objects.requireNonNull(messageSupplier, MESSAGE_SUPPLIER + MUST_NOT_BE_NULL);
        requireNonNull(value, "value");

        if (value.compareTo(zeroOf(value)) <= 0) {
            throw new IllegalArgumentException(messageSupplier.get());
        }
        return value;
    }

    /**
     * Returns the zero value for the given type.
     *
     * @param <T>   the type of the value
     * @param value instance used to determine the type
     * @return the zero value corresponding to {@code value}'s type
     * @throws IllegalArgumentException if the type is not supported
     */
    @SuppressWarnings("unchecked")
    private static <T> T zeroOf(T value) {
        var zero = ZERO_BY_TYPE.get(value.getClass());
        if (zero == null) {
            throw new IllegalArgumentException("Unsupported type: " + value.getClass().getName());
        }
        return (T) zero;
    }
}