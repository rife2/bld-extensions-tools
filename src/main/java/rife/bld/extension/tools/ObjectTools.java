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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.NullUnmarked;
import org.jspecify.annotations.Nullable;
import rife.bld.extension.tools.internal.ToolsSupport;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Object Tools.
 *
 * <p>Unified utility methods for validating objects: emptiness-checking (arrays,
 * collections, maps, and character sequences) and null/sign/range checks for
 * {@link Comparable} values, including all {@code require*} guard methods.</p>
 *
 * <p><b>Emptiness.</b> Emptiness is defined for {@link CharSequence}, {@link Collection},
 * {@link Map}, and arrays. All other non-{@code null} objects are considered not empty.
 * {@link #allEmpty(Object)}, {@link #allNotEmpty(Object)}, and the container-checking
 * {@code require*} methods check containers recursively, up to {@value #MAX_NESTING_DEPTH}
 * levels deep per branch (see {@link #MAX_NESTING_DEPTH}). Exceeding this limit causes an
 * {@link IllegalStateException} to be thrown, rather than returning a potentially
 * unreliable result. {@link #anyEmpty(Object)} and {@link #anyNotEmpty(Object)} check only
 * the container's direct elements/entries — they do not descend into nested containers.</p>
 *
 * <p><b>Map key checking:</b> both keys and values are evaluated by emptiness predicates.
 * Keys of non-container, non-{@link CharSequence} types (e.g. {@link Integer}, enum
 * constants) are never considered empty, so a map with such keys never reports all keys
 * as empty — this affects {@link #allEmpty(Object)} and {@link #anyEmpty(Object)}.</p>
 *
 * <p><b>Sign and range checks.</b> {@link #requirePositive(Object, String)},
 * {@link #requireNegative(Object, String)}, and {@link #requireNonNegative(Object, String)}
 * work with any {@link Comparable} type that has a natural zero: {@link Integer},
 * {@link Long}, {@link Double}, {@link Float}, {@link Short}, {@link Byte},
 * {@link java.math.BigInteger}, and {@link java.math.BigDecimal}. For {@link Double} and
 * {@link Float}, {@code NaN} is rejected by all three, and {@code -0.0} is treated as equal
 * to zero rather than negative.</p>
 *
 * <p><b>Validation order:</b> all {@code require*} methods throw {@link NullPointerException}
 * first if the value (or, for container-checking overloads, any nested element) is
 * {@code null}, then {@link IllegalArgumentException} if the value fails its specific check
 * (empty, wrong sign, etc.).</p>
 *
 * <p><b>Supplier overloads.</b> Every {@code Supplier<String>} overload in this class
 * invokes its supplier lazily — at most once, and only when validation actually fails.
 * The supplier's output is used as-is and is not itself validated.</p>
 *
 * <p>For text-specific operations like blank-checking, see {@link TextTools}.</p>
 *
 * @author <a href="https://erik.thauvin.net/">Erik C. Thauvin</a>
 * @since 1.0
 */
@NullMarked
@SuppressWarnings("PMD.CouplingBetweenObjects")
@SuppressFBWarnings("NP_PARAMETER_MUST_BE_NONNULL_BUT_MARKED_AS_NULLABLE")
public final class ObjectTools {

    /**
     * Maximum nesting depth checked during null/emptiness validation.
     *
     * <p>Depth is tracked per traversal branch, not the total number of containers visited
     * across the whole structure — a wide structure with many sibling containers at the
     * same level does not count against this limit.</p>
     *
     * <p>Traversal is iterative, so {@link StackOverflowError} is not the concern; the limit
     * exists to bound work and memory on cyclic or pathologically deep structures. Containers
     * nested deeper than this limit along a given branch cause an {@link IllegalStateException}
     * to be thrown, rather than silently truncating the traversal and returning a potentially
     * unreliable result.</p>
     */
    static final int MAX_NESTING_DEPTH = 128;

    private static final String MESSAGE_SUPPLIER = "messageSupplier";
    private static final String MUST_NOT_BE_NULL = " must not be null";

    /**
     * Zero values keyed by boxed numeric type, used by {@link #zeroOf(Object)}.
     *
     * <p>{@link Double} and {@link Float} are intentionally absent: the sign-check methods
     * compare them via primitive operators so that {@code NaN} and {@code -0.0} are handled
     * correctly, which {@link Comparable#compareTo} does not do.</p>
     */
    private static final Map<Class<?>, Object> ZERO_BY_TYPE = Map.of(
            Integer.class, 0,
            Long.class, 0L,
            Short.class, (short) 0,
            Byte.class, (byte) 0,
            BigInteger.class, BigInteger.ZERO,
            BigDecimal.class, BigDecimal.ZERO
    );

    private static final Predicate<@Nullable Object> isEmptyPredicate = ObjectTools::isEmpty;
    private static final Predicate<@Nullable Object> isNotEmptyPredicate = ObjectTools::isNotEmpty;

    private ObjectTools() {
    }

    /**
     * Returns {@code true} if the value and all nested elements/entries are empty.
     *
     * <p>For non-containers, equivalent to {@link #isEmpty(Object)}. For containers,
     * returns {@code true} only if the container itself is empty, or every nested
     * element/entry (checked recursively, see class docs) is {@code null} or empty.</p>
     *
     * <pre>{@code
     * allEmpty(List.of());                           // true - vacuously
     * allEmpty(List.of(List.of("", ""), List.of())); // true
     * allEmpty(List.of("foo", "bar"));               // false
     * }</pre>
     *
     * @param value the value to inspect; may be {@code null}
     * @return {@code true} if the value and all nested elements are {@code null} or empty
     * @throws IllegalStateException if nesting exceeds {@value #MAX_NESTING_DEPTH} levels
     * @apiNote An empty container vacuously satisfies this and returns {@code true} — the
     * logical complement of requiring <em>any</em> element non-empty, not the inverse of
     * {@link #allNotEmpty(Object)} for empty containers. A {@code null} element is treated
     * as empty.
     * @since 1.3
     */
    @SuppressFBWarnings("ITC_INHERITANCE_TYPE_CHECKING")
    public static boolean allEmpty(@Nullable Object value) {
        if (value == null) {
            return true;
        }
        if (!isContainer(value)) {
            return isEmpty(value);
        }
        if (isEmpty(value)) {
            return true;
        }

        var stack = new DepthStack();
        stack.pushRoot(value);

        DepthEntry entry;
        while ((entry = stack.pop()) != null) {
            var current = entry.value();
            if (isEmpty(current)) {
                continue;
            }

            if (current.getClass().isArray() && !(current instanceof Object[])) {
                return false; // non-empty primitive array
            }

            if (current instanceof @Nullable Object[] arr) {
                for (var o : arr) {
                    if (o == null) {
                        continue;
                    }
                    if (isContainer(o)) {
                        stack.tryPush(o, entry.depth());
                    } else if (isNotEmpty(o)) {
                        return false;
                    }
                }
            } else if (current instanceof Collection<?> c) {
                for (var o : c) {
                    if (o == null) {
                        continue;
                    }
                    if (isContainer(o)) {
                        stack.tryPush(o, entry.depth());
                    } else if (isNotEmpty(o)) {
                        return false;
                    }
                }
            } else if (current instanceof Map<?, ?> m) {
                for (var e : m.entrySet()) {
                    var k = e.getKey();
                    var v = e.getValue();
                    if (k != null) {
                        if (isContainer(k)) {
                            stack.tryPush(k, entry.depth());
                        } else if (isNotEmpty(k)) {
                            return false;
                        }
                    }
                    if (v != null) {
                        if (isContainer(v)) {
                            stack.tryPush(v, entry.depth());
                        } else if (isNotEmpty(v)) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * Returns {@code true} if the value and all nested elements/entries are not empty.
     *
     * <p>For non-containers, equivalent to {@link #isNotEmpty(Object)}. For containers,
     * the container itself and every nested element/entry (checked recursively, see class
     * docs) must be not {@code null} and not empty.</p>
     *
     * <pre>{@code
     * allNotEmpty(List.of("foo", "bar"));   // true
     * allNotEmpty(List.of(List.of("")));    // false - nested element is empty
     * allNotEmpty(List.of());               // false - empty container
     * }</pre>
     *
     * @param value the value to inspect; may be {@code null}
     * @return {@code true} if the value and all nested elements are not {@code null} and not empty
     * @throws IllegalStateException if nesting exceeds {@value #MAX_NESTING_DEPTH} levels
     * @apiNote An empty container returns {@code false} — there are no non-empty elements
     * to satisfy the condition. This is intentionally asymmetric with {@link #allEmpty(Object)}
     * for empty containers. A {@code null} element is treated as empty and causes this method
     * to return {@code false}.
     * @since 1.3
     */
    public static boolean allNotEmpty(@Nullable Object value) {
        if (value == null || isEmpty(value)) {
            return false;
        }
        if (!isContainer(value)) {
            return true;
        }

        var stack = new DepthStack();
        stack.pushRoot(value);

        DepthEntry entry;
        while ((entry = stack.pop()) != null) {
            var current = entry.value();
            if (isEmpty(current)) {
                return false;
            }
            if (!isContainer(current)) {
                continue; // non-empty, non-container leaf satisfies allNotEmpty
            }
            if (stack.pushAllOrNull(current, entry.depth())) {
                return false; // a null child means this container fails
            }
        }
        return true;
    }

    /**
     * Returns {@code true} if the value, or any of its direct elements/entries, is empty.
     *
     * <p>For non-containers, equivalent to {@link #isEmpty(Object)}. For containers, only
     * the direct elements/entries are checked — nested containers are not descended into
     * (unlike {@link #allEmpty(Object)}). See the class-level note on map key checking.</p>
     *
     * @param value the value to inspect; may be {@code null}
     * @return {@code true} if the value or any direct element is empty
     * @since 1.3
     */
    public static boolean anyEmpty(@Nullable Object value) {
        if (value == null) {
            return true;
        }
        return isContainer(value)
                ? isEmpty(value) || forEachElement(value, isEmptyPredicate)
                : isEmpty(value);
    }

    /**
     * Returns {@code true} if the value, or any of its direct elements/entries, is not empty.
     *
     * <p>For non-containers, equivalent to {@link #isNotEmpty(Object)}. For containers, only
     * the direct elements/entries are checked — nested containers are not descended into
     * (unlike {@link #allNotEmpty(Object)}).</p>
     *
     * @param value the value to inspect; may be {@code null}
     * @return {@code true} if the value or any direct element is not empty
     * @since 1.3
     */
    public static boolean anyNotEmpty(@Nullable Object value) {
        if (value == null) {
            return false;
        }
        return isContainer(value)
                ? forEachElement(value, isNotEmptyPredicate)
                : isNotEmpty(value);
    }

    /**
     * Throws {@link NullPointerException} if {@code value} is a container containing a
     * {@code null} element.
     */
    @SuppressWarnings("PMD.AvoidThrowingNullPointerException")
    private static void checkForNulls(Object value, String message) {
        if (isContainer(value) && hasNullElement(value)) {
            throw new NullPointerException(message);
        }
    }

    /**
     * Throws {@link NullPointerException} if {@code value} is a container containing a
     * {@code null} element. Lazy variant: {@code messageSupplier} is only invoked when a
     * {@code null} element is actually found.
     */
    @SuppressWarnings("PMD.AvoidThrowingNullPointerException")
    private static void checkForNulls(Object value, Supplier<String> messageSupplier) {
        if (isContainer(value) && hasNullElement(value)) {
            throw new NullPointerException(messageSupplier.get());
        }
    }

    private static boolean forEachBoxedPrimitive(Object container, Predicate<@Nullable Object> predicate) {
        int len = Array.getLength(container);
        for (int i = 0; i < len; i++) {
            if (predicate.test(Array.get(container, i))) {
                return true;
            }
        }
        return false;
    }

    private static boolean forEachCollection(Collection<? extends @Nullable Object> c,
                                             Predicate<@Nullable Object> predicate) {
        for (@Nullable Object v : c) {
            if (predicate.test(v)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Shared any-match iteration for {@link #anyEmpty} and {@link #anyNotEmpty}:
     * {@code true} as soon as {@code predicate} holds for one element/key/value,
     * {@code false} otherwise (including empty containers).
     *
     * @implNote The primitive-array fast path compares {@code predicate} by reference
     * identity against the cached {@link #isEmptyPredicate}/{@link #isNotEmptyPredicate}
     * fields. A newly-created lambda or method reference with equivalent behavior would
     * not match and would silently fall back to the slower boxing path.
     */
    private static boolean forEachElement(Object container, Predicate<@Nullable Object> predicate) {
        if (container instanceof @Nullable Object[] arr) {
            return forEachObjectArray(arr, predicate);
        }
        if (container instanceof Collection<?> c) {
            return forEachCollection(c, predicate);
        }
        if (container instanceof Map<?, ?> m) {
            return forEachMap(m, predicate);
        }
        if (container.getClass().isArray()) {
            return forEachPrimitiveArray(container, predicate);
        }
        // Should never be reached: callers guard with isContainer(), which covers exactly
        // the four branches above. Update this method if isContainer() gains new types.
        throw new IllegalStateException("forEachElement called on unhandled container type: "
                + container.getClass().getName());
    }

    private static boolean forEachMap(Map<? extends @Nullable Object, ? extends @Nullable Object> m,
                                      Predicate<@Nullable Object> predicate) {
        for (var e : m.entrySet()) {
            if (predicate.test(e.getKey())) {
                return true;
            }
            if (predicate.test(e.getValue())) {
                return true;
            }
        }
        return false;
    }

    private static boolean forEachObjectArray(@Nullable Object[] arr, Predicate<@Nullable Object> predicate) {
        for (@Nullable Object v : arr) {
            if (predicate.test(v)) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("PMD.CompareObjectsWithEquals")
    private static boolean forEachPrimitiveArray(Object container, Predicate<@Nullable Object> predicate) {
        if (isPrimitiveFastPath(predicate)) {
            // primitives are never empty -> isEmpty never matches, isNotEmpty matches if length > 0
            return predicate == isNotEmptyPredicate && Array.getLength(container) > 0;
        }
        return forEachBoxedPrimitive(container, predicate);
    }

    /**
     * Returns {@code true} if {@code value} is a container holding a {@code null} element/entry,
     * checked recursively up to {@value #MAX_NESTING_DEPTH} levels deep per branch (see
     * {@link #MAX_NESTING_DEPTH}).
     *
     * @throws IllegalStateException if nesting exceeds {@value #MAX_NESTING_DEPTH} levels
     */
    private static boolean hasNullElement(Object value) {
        if (!isContainer(value)) {
            return false;
        }

        var stack = new DepthStack();
        stack.pushRoot(value);
        DepthEntry entry;
        while ((entry = stack.pop()) != null) {
            if (!isContainer(entry.value())) {
                continue;
            }
            if (stack.pushAllOrNull(entry.value(), entry.depth())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns {@code true} if {@code value} is an array, {@link Collection}, or {@link Map}.
     * Covers both object arrays and primitive arrays. {@link CharSequence} is not a
     * container; use {@link TextTools}.
     */
    private static boolean isContainer(@Nullable Object value) {
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
     * @since 1.0
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
     * Returns {@code true} if {@code value} is greater than or equal to zero.
     *
     * <p>For {@link Double} and {@link Float}, uses primitive comparison so that
     * {@code NaN} is never non-negative and {@code -0.0} is treated as zero.</p>
     */
    private static <T extends Comparable<T>> boolean isNonNegative(T value) {
        if (value instanceof Double d) {
            return d >= 0.0d;
        }
        if (value instanceof Float f) {
            return f >= 0.0f;
        }
        return value.compareTo(zeroOf(value)) >= 0;
    }

    /**
     * Determines whether the given {@code value} is not {@code null} and not empty.
     *
     * @param value the value to inspect; may be {@code null}
     * @return {@code true} if the {@code value} is not {@code null} and not empty
     * @since 1.0
     */
    public static boolean isNotEmpty(@Nullable Object value) {
        return !isEmpty(value);
    }

    @SuppressWarnings("PMD.CompareObjectsWithEquals")
    private static boolean isPrimitiveFastPath(Predicate<@Nullable Object> predicate) {
        // Reference-identity check: these are static final fields; == is intentional.
        return predicate == isEmptyPredicate || predicate == isNotEmptyPredicate;
    }

    /**
     * Returns {@code true} if {@code value} is strictly negative.
     *
     * <p>For {@link Double} and {@link Float}, uses primitive comparison so that
     * {@code NaN} is never negative and {@code -0.0} is not treated as negative.</p>
     */
    private static <T extends Comparable<T>> boolean isStrictlyNegative(T value) {
        if (value instanceof Double d) {
            return d < 0.0d;
        }
        if (value instanceof Float f) {
            return f < 0.0f;
        }
        return value.compareTo(zeroOf(value)) < 0;
    }

    /**
     * Returns {@code true} if {@code value} is strictly positive.
     *
     * <p>For {@link Double} and {@link Float}, uses primitive comparison so that
     * {@code NaN} is never positive and {@code -0.0}/{@code 0.0} are not positive.</p>
     */
    private static <T extends Comparable<T>> boolean isStrictlyPositive(T value) {
        if (value instanceof Double d) {
            return d > 0.0d;
        }
        if (value instanceof Float f) {
            return f > 0.0f;
        }
        return value.compareTo(zeroOf(value)) > 0;
    }

    /**
     * Requires the value to be empty. For containers, every element/entry must also be
     * empty, and {@code null} elements are rejected with {@link NullPointerException}
     * (note: {@link #allEmpty(Object)} treats {@code null} as empty, but this method does
     * not — consistent with all other {@code require*} methods).
     *
     * @param value   the value to validate and return; must not be {@code null}
     * @param message the exception message; must not be {@code null}, empty, or blank
     * @param <T>     the value type
     * @return the validated value
     * @throws NullPointerException     if the value or any element is {@code null}
     * @throws IllegalArgumentException if the value is not empty
     * @throws IllegalArgumentException if {@code message} is {@code null}, empty, or blank
     * @throws IllegalStateException    if nesting exceeds {@value #MAX_NESTING_DEPTH} levels
     * @since 1.3
     */
    @Contract("null, _ -> fail; _, _ -> param1")
    @NullUnmarked
    public static <T extends @Nullable Object> @NonNull T requireEmpty(@Nullable T value, @NonNull String message) {
        ToolsSupport.requireMessage(message);
        Objects.requireNonNull(value, message);
        checkForNulls(value, message);
        if (!allEmpty(value)) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    /**
     * Requires the value to be empty. See {@link #requireEmpty(Object, String)}.
     *
     * <p>{@code messageSupplier} is invoked lazily — at most once, and only when validation
     * actually fails. Its output is used as-is and is not itself validated.</p>
     *
     * @param <T>             the value type
     * @param value           the value to validate and return; must not be {@code null}
     * @param messageSupplier the supplier of the exception message; must not be {@code null}
     * @return the validated value
     * @throws NullPointerException     if the value or any element is {@code null}
     * @throws IllegalArgumentException if the value is not empty
     * @throws IllegalStateException    if nesting exceeds {@value #MAX_NESTING_DEPTH} levels
     * @since 1.3
     */
    @Contract("null, _ -> fail; _, _ -> param1")
    @NullUnmarked
    public static <T extends @Nullable Object> @NonNull T requireEmpty(@Nullable T value,
                                                                       @NonNull Supplier<String> messageSupplier) {
        Objects.requireNonNull(messageSupplier, MESSAGE_SUPPLIER + MUST_NOT_BE_NULL);
        Objects.requireNonNull(value, messageSupplier);
        checkForNulls(value, messageSupplier);
        if (!allEmpty(value)) {
            throw new IllegalArgumentException(messageSupplier.get());
        }
        return value;
    }

    /**
     * Checks that the specified value is strictly negative. Works with any
     * {@link Comparable} type that has a natural zero value: {@link Integer}, {@link Long},
     * {@link Double}, {@link Float}, {@link Byte}, {@link Short}, {@link BigInteger}, and
     * {@link BigDecimal}.
     *
     * <p>For {@link Double} and {@link Float}, {@code NaN} is rejected by all three
     * {@code require*} sign-check methods, and {@code -0.0} is treated as equal to zero
     * rather than negative.</p>
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
    @Contract("null, _ -> fail; _, _ -> param1")
    @NullUnmarked
    public static <T extends @Nullable Object & Comparable<T>> @NonNull T requireNegative(@Nullable T value,
                                                                                          @NonNull String context) {
        ToolsSupport.requireContext(context);
        Objects.requireNonNull(value, () -> context + MUST_NOT_BE_NULL);
        return requireNegative(value, () -> context + " must be negative, got: " + value);
    }

    /**
     * Checks that the specified value is strictly negative. See {@link #requireNegative(Object, String)}.
     *
     * @param <T>             the type of the value, must implement {@link Comparable}
     * @param value           the value to check for negativity; must not be {@code null}
     * @param messageSupplier the supplier of the exception message; must not be {@code null}
     * @return the validated value if it is less than zero
     * @throws NullPointerException     if {@code value} or {@code messageSupplier} is {@code null}
     * @throws IllegalArgumentException if {@code value} is zero or positive
     * @throws IllegalArgumentException if {@code value} is of an unsupported type
     * @since 1.3
     */
    @Contract("null, _ -> fail; _, _ -> param1")
    @NullUnmarked
    public static <T extends @Nullable Object & Comparable<T>> @NonNull T requireNegative(@Nullable T value,
                                                                                          @NonNull Supplier<String> messageSupplier) {
        Objects.requireNonNull(messageSupplier, MESSAGE_SUPPLIER + MUST_NOT_BE_NULL);
        Objects.requireNonNull(value, "value" + MUST_NOT_BE_NULL);

        if (!isStrictlyNegative(value)) {
            throw new IllegalArgumentException(messageSupplier.get());
        }
        return value;
    }

    /**
     * Checks that the specified value is non-negative. See {@link #requireNegative(Object, String)}
     * for supported types.
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
    @Contract("null, _ -> fail; _, _ -> param1")
    @NullUnmarked
    public static <T extends @Nullable Object & Comparable<T>> @NonNull T requireNonNegative(@Nullable T value,
                                                                                             @NonNull String context) {
        ToolsSupport.requireContext(context);
        Objects.requireNonNull(value, () -> context + MUST_NOT_BE_NULL);
        return requireNonNegative(value, () -> context + " must be non-negative, got: " + value);
    }

    /**
     * Checks that the specified value is non-negative. See {@link #requireNonNegative(Object, String)}.
     *
     * @param <T>             the type of the value, must implement {@link Comparable}
     * @param value           the value to check for non-negativity; must not be {@code null}
     * @param messageSupplier the supplier of the exception message; must not be {@code null}
     * @return the validated value if it is greater than or equal to zero
     * @throws NullPointerException     if {@code value} or {@code messageSupplier} is {@code null}
     * @throws IllegalArgumentException if {@code value} is negative
     * @throws IllegalArgumentException if {@code value} is of an unsupported type
     * @since 1.3
     */
    @Contract("null, _ -> fail; _, _ -> param1")
    @NullUnmarked
    public static <T extends @Nullable Object & Comparable<T>> @NonNull T requireNonNegative(@Nullable T value,
                                                                                             @NonNull Supplier<String> messageSupplier) {
        Objects.requireNonNull(messageSupplier, MESSAGE_SUPPLIER + MUST_NOT_BE_NULL);
        Objects.requireNonNull(value, "value" + MUST_NOT_BE_NULL);

        if (!isNonNegative(value)) {
            throw new IllegalArgumentException(messageSupplier.get());
        }
        return value;
    }

    /**
     * Requires the value to be not {@code null}, and all elements/entries to be not
     * {@code null}. Unlike {@link #requireNotEmpty(Object, String)}, empty containers
     * are allowed as long as they contain no {@code null} elements. Nested containers
     * are checked recursively (see class docs).
     *
     * @param value   the value to validate and return; must not be {@code null}
     * @param context the context string used in exception message; must not be {@code null}, empty, or blank
     * @param <T>     the value type
     * @return the validated value, never {@code null}
     * @throws NullPointerException     if the {@code value} is {@code null} or contains {@code null} elements
     * @throws NullPointerException     if {@code context} is {@code null}
     * @throws IllegalArgumentException if {@code context} is empty, or blank
     * @throws IllegalStateException    if nesting exceeds {@value #MAX_NESTING_DEPTH} levels
     * @since 1.3
     */
    @Contract("null, _ -> fail; _, _ -> param1")
    @NullUnmarked
    public static <T extends @Nullable Object> @NonNull T requireNonNull(@Nullable T value, @NonNull String context) {
        ToolsSupport.requireContext(context);
        Objects.requireNonNull(value, () -> context + MUST_NOT_BE_NULL);
        checkForNulls(value, () -> context + " must not contain null elements");
        return value;
    }

    /**
     * Requires the value to be not {@code null}, and all elements/entries to be not
     * {@code null}. See {@link #requireNonNull(Object, String)}.
     *
     * <p>{@code messageSupplier} is invoked lazily — at most once, and only when validation
     * actually fails. Its output is used as-is and is not itself validated.</p>
     *
     * @param value           the value to validate and return; must not be {@code null}
     * @param messageSupplier the supplier of the exception message; must not be {@code null}
     * @param <T>             the value type
     * @return the validated value, never {@code null}
     * @throws NullPointerException  if the {@code value} is {@code null} or contains {@code null} elements
     * @throws NullPointerException  if {@code messageSupplier} is {@code null}
     * @throws IllegalStateException if nesting exceeds {@value #MAX_NESTING_DEPTH} levels
     * @since 1.3
     */
    @Contract("null, _ -> fail; _, _ -> param1")
    @NullUnmarked
    public static <T extends @Nullable Object> @NonNull T requireNonNull(@Nullable T value,
                                                                         @NonNull Supplier<String> messageSupplier) {
        Objects.requireNonNull(messageSupplier, MESSAGE_SUPPLIER + MUST_NOT_BE_NULL);
        Objects.requireNonNull(value, messageSupplier);   // lazy — only if value is null
        checkForNulls(value, messageSupplier);            // lazy — only if nulls found
        return value;
    }

    /**
     * Requires the value to be not {@code null} and not empty (and, for containers, every
     * element/entry to be not {@code null} and not empty).
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
     * @throws IllegalStateException    if nesting exceeds {@value #MAX_NESTING_DEPTH} levels
     * @since 1.3
     */
    @Contract("null, _ -> fail; _, _ -> param1")
    @NullUnmarked
    public static <T extends @Nullable Object> @NonNull T requireNotEmpty(@Nullable T value, @NonNull String context) {
        ToolsSupport.requireContext(context);
        Objects.requireNonNull(value, () -> context + MUST_NOT_BE_NULL);
        checkForNulls(value, () -> context + " must not contain null elements");
        if (isEmpty(value)) {
            throw new IllegalArgumentException(context + " must not be empty");
        }
        if (!allNotEmpty(value)) {
            throw new IllegalArgumentException(context + " must not contain empty elements");
        }
        return value;
    }

    /**
     * Requires the value to be not {@code null} and not empty. See
     * {@link #requireNotEmpty(Object, String)}.
     *
     * @param value        the value to validate and return; must not be {@code null} or empty
     * @param nullMessage  the message for {@code NullPointerException}; must not be {@code null}, empty, or blank
     * @param emptyMessage the message for {@code IllegalArgumentException}; must not be {@code null}, empty, or blank
     * @param <T>          the value type
     * @return the validated value, never {@code null} or empty
     * @throws NullPointerException     if the {@code value} or any element is {@code null}
     * @throws IllegalArgumentException if the {@code value} is empty
     * @throws IllegalArgumentException if either message is {@code null}, empty, or blank
     * @throws IllegalStateException    if nesting exceeds {@value #MAX_NESTING_DEPTH} levels
     * @since 1.3
     */
    @Contract("null, _, _ -> fail; _, _, _ -> param1")
    @NullUnmarked
    public static <T extends @Nullable Object> @NonNull T requireNotEmpty(@Nullable T value,
                                                                          @NonNull String nullMessage,
                                                                          @NonNull String emptyMessage) {
        ToolsSupport.requireMessage(nullMessage);
        ToolsSupport.requireMessage(emptyMessage);
        Objects.requireNonNull(value, nullMessage);
        checkForNulls(value, nullMessage);
        if (!allNotEmpty(value)) {
            throw new IllegalArgumentException(emptyMessage);
        }
        return value;
    }

    /**
     * Checks that the specified value is strictly positive. See {@link #requireNegative(Object, String)}
     * for supported types.
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
    @Contract("null, _ -> fail; _, _ -> param1")
    @NullUnmarked
    public static <T extends @Nullable Object & Comparable<T>> @NonNull T requirePositive(@Nullable T value,
                                                                                          @NonNull String context) {
        ToolsSupport.requireContext(context);
        Objects.requireNonNull(value, () -> context + MUST_NOT_BE_NULL);
        return requirePositive(value, () -> context + " must be positive, got: " + value);
    }

    /**
     * Checks that the specified value is strictly positive. See {@link #requirePositive(Object, String)}.
     *
     * @param <T>             the type of the value, must implement {@link Comparable}
     * @param value           the value to check for positivity; must not be {@code null}
     * @param messageSupplier the supplier of the exception message; must not be {@code null}
     * @return the validated value if it is greater than zero
     * @throws NullPointerException     if {@code value} or {@code messageSupplier} is {@code null}
     * @throws IllegalArgumentException if {@code value} is zero or negative
     * @throws IllegalArgumentException if {@code value} is of an unsupported type
     * @since 1.3
     */
    @Contract("null, _ -> fail; _, _ -> param1")
    @NullUnmarked
    public static <T extends @Nullable Object & Comparable<T>> @NonNull T requirePositive(@Nullable T value,
                                                                                          @NonNull Supplier<String> messageSupplier) {
        Objects.requireNonNull(messageSupplier, MESSAGE_SUPPLIER + MUST_NOT_BE_NULL);
        Objects.requireNonNull(value, "value" + MUST_NOT_BE_NULL);

        if (!isStrictlyPositive(value)) {
            throw new IllegalArgumentException(messageSupplier.get());
        }
        return value;
    }

    /**
     * Returns the zero value for the given type.
     *
     * <p>Walks the runtime class's superclass chain so that subclasses of non-final
     * supported types (notably {@link BigInteger} and {@link BigDecimal}) are resolved to
     * their mapped ancestor rather than being rejected as unsupported.</p>
     *
     * @param <T>   the type of the value
     * @param value instance used to determine the type
     * @return the zero value corresponding to {@code value}'s type
     * @throws IllegalArgumentException if the type is not supported
     */
    @SuppressWarnings("unchecked")
    private static <T> T zeroOf(T value) {
        for (Class<?> cls = value.getClass(); cls != null; cls = cls.getSuperclass()) {
            var zero = ZERO_BY_TYPE.get(cls);
            if (zero != null) {
                return (T) zero;
            }
        }
        throw new IllegalArgumentException("Unsupported type: " + value.getClass().getName());
    }

    /**
     * A container value paired with its nesting depth (number of containers enclosing it,
     * counted along the traversal branch from the root — not the total number of containers
     * visited across the whole structure).
     */
    private record DepthEntry(Object value, int depth) {

    }

    /**
     * Iterative depth-tracked stack shared by {@link #allEmpty}, {@link #allNotEmpty}, and
     * {@link #hasNullElement} to traverse nested containers without recursion. Pushing past
     * {@link #MAX_NESTING_DEPTH} throws {@link IllegalStateException} rather than silently
     * truncating the traversal.
     */
    private static final class DepthStack {

        private final Deque<DepthEntry> deque = new ArrayDeque<>();

        @Nullable DepthEntry pop() {
            return deque.isEmpty() ? null : deque.pop();
        }

        /**
         * Pushes every element/entry of {@code container} at {@code parentDepth + 1},
         * stopping and returning {@code true} as soon as a {@code null} is found (used by
         * {@link #hasNullElement} and {@link #allNotEmpty}, where a null child already fails
         * the check the caller is performing).
         *
         * @throws IllegalStateException if a child would exceed {@link #MAX_NESTING_DEPTH}
         */
        @SuppressFBWarnings("ITC_INHERITANCE_TYPE_CHECKING")
        boolean pushAllOrNull(Object container, int parentDepth) {
            if (container instanceof @Nullable Object[] arr) {
                for (var o : arr) {
                    if (o == null) {
                        return true;
                    }
                    tryPush(o, parentDepth);
                }
            } else if (container instanceof Collection<?> c) {
                for (var o : c) {
                    if (o == null) {
                        return true;
                    }
                    tryPush(o, parentDepth);
                }
            } else if (container instanceof Map<?, ?> m) {
                for (var e : m.entrySet()) {
                    if (e.getKey() == null || e.getValue() == null) {
                        return true;
                    }
                    tryPush(e.getKey(), parentDepth);
                    tryPush(e.getValue(), parentDepth);
                }
            }
            return false;
        }

        void pushRoot(Object value) {
            deque.push(new DepthEntry(value, 0));
        }

        /**
         * Pushes {@code child} at {@code parentDepth + 1}.
         *
         * @throws IllegalStateException if that depth exceeds {@link #MAX_NESTING_DEPTH}
         */
        void tryPush(Object child, int parentDepth) {
            int childDepth = parentDepth + 1;
            if (childDepth > MAX_NESTING_DEPTH) {
                throw new IllegalStateException(
                        "Maximum validation nesting depth of " + MAX_NESTING_DEPTH + " exceeded");
            }
            deque.push(new DepthEntry(child, childDepth));
        }
    }
}