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

import edu.umd.cs.findbugs.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.IllegalFormatException;
import java.util.Map;
import java.util.Objects;

/**
 * Object Tools.
 *
 * <p>Unified utility methods for emptiness-checking objects, arrays,
 * collections, maps, and character sequences.</p>
 *
 * <p>Emptiness is defined only for types where the concept is meaningful:
 * {@link CharSequence}, {@link Collection}, {@link Map}, and arrays.
 * All other non-{@code null} objects are considered not empty.</p>
 *
 * <p>Multi-value helpers are provided as both predicates and validators.</p>
 *
 * @author <a href="https://erik.thauvin.net/">Erik C. Thauvin</a>
 * @since 1.0
 */
public final class ObjectTools {

    private ObjectTools() {
        // no-op
    }

    /**
     * Returns {@code true} if all provided {@code values} are empty.
     *
     * @param values the values to inspect; may be {@code null}
     * @return {@code true} if all values are empty
     * @since 1.3
     */
    public static boolean allEmpty(@Nullable Object... values) {
        if (values == null) {
            return true;
        }
        for (Object v : values) {
            if (isNotEmpty(v)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns {@code true} if all provided {@code values} are not empty.
     *
     * @param values the values to inspect; may be {@code null}
     * @return {@code true} if all values are not empty
     * @since 1.3
     */
    public static boolean allNotEmpty(@Nullable Object... values) {
        if (values == null) {
            return false;
        }
        for (Object v : values) {
            if (isEmpty(v)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns {@code true} if at least one of the provided {@code values} is not empty.
     *
     * @param values the values to inspect; may be {@code null}
     * @return {@code true} if any value is not empty
     * @since 1.3
     */
    public static boolean anyNotEmpty(@Nullable Object... values) {
        if (values == null) {
            return false;
        }
        for (Object v : values) {
            if (isNotEmpty(v)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Appends index context to a base message.
     *
     * @param base  the base message
     * @param index the index to append
     * @return the message with {@code " [index=" + index + "]"} appended
     */
    private static String formatIndex(String base, int index) {
        return base + " [index=" + index + "]";
    }

    /**
     * Appends key context to a base message.
     *
     * @param base the base message
     * @param key  the key to append
     * @return the message with {@code " [key=" + key + "]"} appended
     */
    private static String formatKey(String base, @Nullable Object key) {
        return base + " [key=" + key + "]";
    }

    /**
     * Formats a message with optional arguments.
     *
     * @param message the message or format string
     * @param args    optional arguments used to format the message
     * @return the formatted message, or the raw {@code message} if formatting fails
     */
    private static String formatMessage(String message, @Nullable Object... args) {
        if (args == null || args.length == 0) {
            return message;
        }
        try {
            return String.format(message, args);
        } catch (IllegalFormatException e) {
            return message;
        }
    }

    /**
     * Determines whether the given {@code value} is {@code null} or empty.
     *
     * <p>Emptiness is defined for {@link CharSequence}, {@link Collection},
     * {@link Map}, and arrays. All other non-{@code null} objects are considered
     * not empty.</p>
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

    /**
     * Requires all provided {@code values} to be empty.
     *
     * <p>The {@code values} array itself must not be {@code null}, but may be empty.
     * All elements must be {@code null} or empty.</p>
     *
     * @param values  the values to inspect; must not be {@code null}
     * @param message the exception message; must not be {@code null} or empty
     * @throws NullPointerException     if {@code values} is {@code null}
     * @throws IllegalArgumentException if any value is not empty
     * @throws IllegalArgumentException if {@code message} is {@code null} or empty
     * @since 1.3
     */
    public static void requireAllEmpty(Object[] values, String message) {
        requireValidMessage(message);
        Objects.requireNonNull(values, message);
        for (Object v : values) {
            if (isNotEmpty(v)) {
                throw new IllegalArgumentException(message);
            }
        }
    }

    /**
     * Requires all provided {@code values} to be empty.
     *
     * <p>The {@code values} array itself must not be {@code null}, but may be empty.
     * All elements must be {@code null} or empty. The {@code message} may contain
     * {@link String#format(String, Object...)} placeholders, which are resolved
     * using the supplied {@code args}. If formatting fails, the raw {@code message} is used.</p>
     *
     * @param values  the values to inspect; must not be {@code null}
     * @param message the exception message or format string; must not be {@code null} or empty
     * @param args    optional arguments used to format the {@code message}
     * @throws NullPointerException     if {@code values} is {@code null}
     * @throws IllegalArgumentException if any value is not empty
     * @throws IllegalArgumentException if {@code message} is {@code null} or empty
     * @since 1.3
     */
    public static void requireAllEmpty(Object[] values, String message, @Nullable Object... args) {
        requireValidMessage(message);
        Objects.requireNonNull(values, () -> formatMessage(message, args));
        for (int i = 0; i < values.length; i++) {
            Object v = values[i];
            if (isNotEmpty(v)) {
                throw new IllegalArgumentException(formatMessage(formatIndex(message, i), args));
            }
        }
    }

    /**
     * Requires all values in the provided {@code map} to be empty.
     *
     * <p>The {@code map} itself may be {@code null} or empty. If it is non-empty,
     * all values must be {@code null} or empty as defined by {@link #isEmpty(Object)}.
     * If any value is not empty, an {@link IllegalArgumentException} is thrown.</p>
     *
     * @param map     the map to inspect; may be {@code null}
     * @param message the exception message; must not be {@code null} or empty
     * @throws IllegalArgumentException if any value is not empty
     * @throws IllegalArgumentException if {@code message} is {@code null} or empty
     * @since 1.3
     */
    public static void requireAllEmpty(@Nullable Map<?, ?> map, String message) {
        requireValidMessage(message);
        if (map == null) {
            return;
        }
        for (Object v : map.values()) {
            if (isNotEmpty(v)) {
                throw new IllegalArgumentException(message);
            }
        }
    }

    /**
     * Requires all values in the provided {@code map} to be empty.
     *
     * <p>The {@code map} itself may be {@code null} or empty. If it is non-empty,
     * all values must be {@code null} or empty as defined by {@link #isEmpty(Object)}.
     * The {@code message} may contain {@link String#format(String, Object...)} placeholders,
     * which are resolved using the supplied {@code args}. If formatting fails,
     * the raw {@code message} is used.</p>
     *
     * @param map     the map to inspect; may be {@code null}
     * @param message the exception message or format string; must not be {@code null} or empty
     * @param args    optional arguments used to format the {@code message}
     * @throws IllegalArgumentException if any value is not empty
     * @throws IllegalArgumentException if {@code message} is {@code null} or empty
     * @since 1.3
     */
    public static void requireAllEmpty(@Nullable Map<?, ?> map, String message, @Nullable Object... args) {
        requireValidMessage(message);
        if (map == null) {
            return;
        }
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (isNotEmpty(entry.getValue())) {
                throw new IllegalArgumentException(
                        formatMessage(formatKey(message, entry.getKey()), args));
            }
        }
    }

    /**
     * Requires all provided {@code values} collection elements to be empty.
     *
     * <p>The {@code values} collection itself must not be {@code null}, but may be empty.
     * All elements must be {@code null} or empty as defined by {@link #isEmpty(Object)}.
     * If any element is not empty, an {@link IllegalArgumentException} is thrown.</p>
     *
     * @param values  the collection to inspect; must not be {@code null}
     * @param message the exception message; must not be {@code null} or empty
     * @throws NullPointerException     if {@code values} is {@code null}
     * @throws IllegalArgumentException if any element is not empty
     * @throws IllegalArgumentException if {@code message} is {@code null} or empty
     * @since 1.2
     */
    public static void requireAllEmpty(Collection<?> values, String message) {
        requireValidMessage(message);
        Objects.requireNonNull(values, message);
        for (Object v : values) {
            if (isNotEmpty(v)) {
                throw new IllegalArgumentException(message);
            }
        }
    }

    /**
     * Requires all provided {@code values} collection elements to be empty.
     *
     * <p>The {@code values} collection itself must not be {@code null}, but may be empty.
     * All elements must be {@code null} or empty as defined by {@link #isEmpty(Object)}.
     * The {@code message} may contain {@link String#format(String, Object...)} placeholders,
     * which are resolved using the supplied {@code args}. If formatting fails,
     * the raw {@code message} is used.</p>
     *
     * @param values  the collection to inspect; must not be {@code null}
     * @param message the exception message or format string; must not be {@code null} or empty
     * @param args    optional arguments used to format the {@code message}
     * @throws NullPointerException     if {@code values} is {@code null}
     * @throws IllegalArgumentException if any element is not empty
     * @throws IllegalArgumentException if {@code message} is {@code null} or empty
     * @since 1.2
     */
    public static void requireAllEmpty(Collection<?> values, String message, @Nullable Object... args) {
        requireValidMessage(message);
        Objects.requireNonNull(values, () -> formatMessage(message, args));
        int i = 0;
        for (Object v : values) {
            if (isNotEmpty(v)) {
                throw new IllegalArgumentException(formatMessage(formatIndex(message, i), args));
            }
            i++;
        }
    }

    /**
     * Requires all provided {@code values} to be not {@code null} and not empty.
     *
     * <p>The {@code values} array itself must not be {@code null} or empty, and none of its elements
     * may be {@code null} or empty.</p>
     *
     * @param values  the values to inspect; must not be {@code null} or empty
     * @param message the exception message; must not be {@code null} or empty
     * @throws NullPointerException     if {@code values} is {@code null} or any element is {@code null}
     * @throws IllegalArgumentException if {@code values} is empty or any element is empty
     * @throws IllegalArgumentException if {@code message} is {@code null} or empty
     * @since 1.3
     */
    public static void requireAllNotEmpty(Object[] values, String message) {
        requireValidMessage(message);
        Objects.requireNonNull(values, message);
        if (values.length == 0) {
            throw new IllegalArgumentException(message);
        }
        for (int i = 0; i < values.length; i++) {
            Object v = Objects.requireNonNull(values[i], formatIndex(message, i));
            if (isEmpty(v)) {
                throw new IllegalArgumentException(formatIndex(message, i));
            }
        }
    }

    /**
     * Requires all provided {@code values} to be not {@code null} and not empty.
     *
     * <p>The {@code values} array itself must not be {@code null} or empty, and none of its elements
     * may be {@code null} or empty. The {@code message} may contain {@link String#format(String, Object...)}
     * placeholders, which are resolved using the supplied {@code args}. If formatting fails,
     * the raw {@code message} is used.</p>
     *
     * @param values  the values to inspect; must not be {@code null} or empty
     * @param message the exception message or format string; must not be {@code null} or empty
     * @param args    optional arguments used to format the {@code message}
     * @throws NullPointerException     if {@code values} is {@code null} or any element is {@code null}
     * @throws IllegalArgumentException if {@code values} is empty or any element is empty
     * @throws IllegalArgumentException if {@code message} is {@code null} or empty
     * @since 1.3
     */
    public static void requireAllNotEmpty(Object[] values, String message, @Nullable Object... args) {
        requireValidMessage(message);
        Objects.requireNonNull(values, () -> formatMessage(message, args));
        if (values.length == 0) {
            throw new IllegalArgumentException(formatMessage(message, args));
        }
        for (int i = 0; i < values.length; i++) {
            int idx = i;
            Object v = Objects.requireNonNull(values[i], () -> formatMessage(formatIndex(message, idx), args));
            if (isEmpty(v)) {
                throw new IllegalArgumentException(formatMessage(formatIndex(message, i), args));
            }
        }
    }

    /**
     * Requires the provided {@code map} to be not {@code null}, not empty, and all of its values to be not {@code null} and not empty.
     *
     * <p>The {@code map} itself must not be {@code null} or empty, and none of its values
     * may be {@code null} or empty as defined by {@link #isEmpty(Object)}.</p>
     *
     * @param map     the map to inspect; must not be {@code null} or empty
     * @param message the exception message; must not be {@code null} or empty
     * @throws NullPointerException     if {@code map} is {@code null} or any value is {@code null}
     * @throws IllegalArgumentException if {@code map} is empty or any value is empty
     * @throws IllegalArgumentException if {@code message} is {@code null} or empty
     * @since 1.3
     */
    public static void requireAllNotEmpty(Map<?, ?> map, String message) {
        requireValidMessage(message);
        Objects.requireNonNull(map, message);
        if (map.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            Object k = entry.getKey();
            Object v = Objects.requireNonNull(entry.getValue(), formatKey(message, k));
            if (isEmpty(v)) {
                throw new IllegalArgumentException(formatKey(message, k));
            }
        }
    }

    /**
     * Requires the provided {@code map} to be not {@code null}, not empty, and all of its values to be not {@code null} and not empty.
     *
     * <p>The {@code map} itself must not be {@code null} or empty, and none of its values
     * may be {@code null} or empty as defined by {@link #isEmpty(Object)}.
     * The {@code message} may contain {@link String#format(String, Object...)} placeholders,
     * which are resolved using the supplied {@code args}. If formatting fails,
     * the raw {@code message} is used.</p>
     *
     * @param map     the map to inspect; must not be {@code null} or empty
     * @param message the exception message or format string; must not be {@code null} or empty
     * @param args    optional arguments used to format the {@code message}
     * @throws NullPointerException     if {@code map} is {@code null} or any value is {@code null}
     * @throws IllegalArgumentException if {@code map} is empty or any value is empty
     * @throws IllegalArgumentException if {@code message} is {@code null} or empty
     * @since 1.3
     */
    public static void requireAllNotEmpty(Map<?, ?> map, String message, @Nullable Object... args) {
        requireValidMessage(message);
        Objects.requireNonNull(map, () -> formatMessage(message, args));
        if (map.isEmpty()) {
            throw new IllegalArgumentException(formatMessage(message, args));
        }
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            Object k = entry.getKey();
            Object v = Objects.requireNonNull(entry.getValue(), () -> formatMessage(formatKey(message, k), args));
            if (isEmpty(v)) {
                throw new IllegalArgumentException(formatMessage(formatKey(message, k), args));
            }
        }
    }

    /**
     * Requires all provided {@code values} collection elements to be not {@code null} and not empty.
     *
     * <p>The {@code values} collection itself must not be {@code null} or empty, and none of its
     * elements may be {@code null} or empty as defined by {@link #isEmpty(Object)}.</p>
     *
     * @param values  the collection to inspect; must not be {@code null} or empty
     * @param message the exception message; must not be {@code null} or empty
     * @throws NullPointerException     if {@code values} is {@code null} or any element is {@code null}
     * @throws IllegalArgumentException if {@code values} is empty or any element is empty
     * @throws IllegalArgumentException if {@code message} is {@code null} or empty
     * @since 1.2
     */
    public static void requireAllNotEmpty(Collection<?> values, String message) {
        requireValidMessage(message);
        Objects.requireNonNull(values, message);
        if (values.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
        int i = 0;
        for (Object v : values) {
            Objects.requireNonNull(v, formatIndex(message, i));
            if (isEmpty(v)) {
                throw new IllegalArgumentException(formatIndex(message, i));
            }
            i++;
        }
    }

    /**
     * Requires all provided {@code values} collection elements to be not {@code null} and not empty.
     *
     * <p>The {@code values} collection itself must not be {@code null} or empty, and none of its
     * elements may be {@code null} or empty as defined by {@link #isEmpty(Object)}.
     * The {@code message} may contain {@link String#format(String, Object...)} placeholders,
     * which are resolved using the supplied {@code args}. If formatting fails,
     * the raw {@code message} is used.</p>
     *
     * @param values  the collection to inspect; must not be {@code null} or empty
     * @param message the exception message or format string; must not be {@code null} or empty
     * @param args    optional arguments used to format the {@code message}
     * @throws NullPointerException     if {@code values} is {@code null} or any element is {@code null}
     * @throws IllegalArgumentException if {@code values} is empty or any element is empty
     * @throws IllegalArgumentException if {@code message} is {@code null} or empty
     * @since 1.2
     */
    public static void requireAllNotEmpty(Collection<?> values, String message, @Nullable Object... args) {
        requireValidMessage(message);
        Objects.requireNonNull(values, () -> formatMessage(message, args));
        if (values.isEmpty()) {
            throw new IllegalArgumentException(formatMessage(message, args));
        }
        int i = 0;
        for (Object v : values) {
            int idx = i;
            Objects.requireNonNull(v, () -> formatMessage(formatIndex(message, idx), args));
            if (isEmpty(v)) {
                throw new IllegalArgumentException(formatMessage(formatIndex(message, i), args));
            }
            i++;
        }
    }

    /**
     * Requires the given {@code value} to be empty.
     *
     * <p>Emptiness is defined for {@link CharSequence}, {@link Collection},
     * {@link Map}, and arrays. All other non-{@code null} objects are considered
     * not empty. If the {@code value} is not empty, an {@link IllegalArgumentException}
     * is thrown. Otherwise, the original {@code value} is returned unchanged.</p>
     *
     * @param value   the value to inspect; may be {@code null}
     * @param message the exception message; must not be {@code null} or empty
     * @param <T>     the value type
     * @return the original {@code value} if it is empty
     * @throws IllegalArgumentException if the {@code value} is not empty
     * @throws IllegalArgumentException if {@code message} is {@code null} or empty
     * @since 1.3
     */
    public static <T> T requireEmpty(@Nullable T value, String message) {
        requireValidMessage(message);
        if (isNotEmpty(value)) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    /**
     * Requires the given {@code value} to be empty.
     *
     * <p>Emptiness is defined for {@link CharSequence}, {@link Collection},
     * {@link Map}, and arrays. All other non-{@code null} objects are considered
     * not empty. If the {@code value} is not empty, an {@link IllegalArgumentException}
     * is thrown. The {@code message} may contain {@link String#format(String, Object...)}
     * placeholders, which are resolved using the supplied {@code args}. If
     * formatting fails, the raw {@code message} is used. Otherwise, the original {@code value}
     * is returned unchanged.</p>
     *
     * @param value   the value to inspect; may be {@code null}
     * @param message the exception message or format string; must not be {@code null} or empty
     * @param args    optional arguments used to format the {@code message}
     * @param <T>     the value type
     * @return the original {@code value} if it is empty
     * @throws IllegalArgumentException if the {@code value} is not empty
     * @throws IllegalArgumentException if {@code message} is {@code null} or empty
     * @since 1.3
     */
    public static <T> T requireEmpty(@Nullable T value, String message, @Nullable Object... args) {
        requireValidMessage(message);
        if (isNotEmpty(value)) {
            throw new IllegalArgumentException(formatMessage(message, args));
        }
        return value;
    }

    /**
     * Requires the given {@code value} to be not {@code null} and not empty.
     *
     * <p>Emptiness is defined for {@link CharSequence}, {@link Collection},
     * {@link Map}, and arrays. All other non-{@code null} objects are considered
     * not empty. If the {@code value} is {@code null} or empty, an exception is thrown.
     * Otherwise, the original {@code value} is returned unchanged.</p>
     *
     * @param value   the value to inspect; must not be {@code null} or empty
     * @param message the exception message; must not be {@code null} or empty
     * @param <T>     the value type
     * @return the original {@code value} if it is not {@code null} and not empty
     * @throws NullPointerException     if the {@code value} is {@code null}
     * @throws IllegalArgumentException if the {@code value} is empty
     * @throws IllegalArgumentException if {@code message} is {@code null} or empty
     * @since 1.3
     */
    public static <T> T requireNotEmpty(T value, String message) {
        requireValidMessage(message);
        Objects.requireNonNull(value, message);
        if (isEmpty(value)) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    /**
     * Requires the given {@code value} to be not {@code null} and not empty.
     *
     * <p>Emptiness is defined for {@link CharSequence}, {@link Collection},
     * {@link Map}, and arrays. All other non-{@code null} objects are considered
     * not empty. If the {@code value} is {@code null} or empty, an exception is thrown.
     * The {@code message} may contain {@link String#format(String, Object...)}
     * placeholders, which are resolved using the supplied {@code args}. If
     * formatting fails, the raw {@code message} is used. Otherwise, the original {@code value}
     * is returned unchanged.</p>
     *
     * @param value   the value to inspect; must not be {@code null} or empty
     * @param message the exception message or format string; must not be {@code null} or empty
     * @param args    optional arguments used to format the {@code message}
     * @param <T>     the value type
     * @return the original {@code value} if it is not {@code null} and not empty
     * @throws NullPointerException     if the {@code value} is {@code null}
     * @throws IllegalArgumentException if the {@code value} is empty
     * @throws IllegalArgumentException if {@code message} is {@code null} or empty
     * @since 1.3
     */
    public static <T> T requireNotEmpty(T value, String message, @Nullable Object... args) {
        requireValidMessage(message);
        Objects.requireNonNull(value, () -> formatMessage(message, args));
        if (isEmpty(value)) {
            throw new IllegalArgumentException(formatMessage(message, args));
        }
        return value;
    }

    /**
     * Validates that the {@code message} is not blank.
     *
     * @param message the message to validate
     * @throws IllegalArgumentException if {@code message} is {@code null} or empty
     */
    private static void requireValidMessage(String message) {
        if (TextTools.isBlank(message)) {
            throw new IllegalArgumentException("message must not be null or empty");
        }
    }
}