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

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.IllegalFormatException;
import java.util.Map;

/**
 * Object Tools.
 *
 * <p>Unified utility methods for emptiness-checking objects, arrays,
 * collections, maps, and character sequences.</p>
 *
 * <p>Emptiness is defined only for types where the concept is meaningful:
 * {@link CharSequence}, {@link Collection}, {@link Map}, and arrays.
 * All other non-null objects are considered not empty.</p>
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
     * Returns {@code true} if all provided values are empty.
     *
     * @param values the values to inspect; may be {@code null}
     * @return {@code true} if all values are empty
     * @since 1.3
     */
    public static boolean allEmpty(Object... values) {
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
     * Returns {@code true} if all provided values are not empty.
     *
     * @param values the values to inspect; may be {@code null}
     * @return {@code true} if all values are not empty
     * @since 1.3
     */
    public static boolean allNotEmpty(Object... values) {
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
     * Returns {@code true} if at least one of the provided values is not empty.
     *
     * @param values the values to inspect; may be {@code null}
     * @return {@code true} if any value is not empty
     * @since 1.3
     */
    public static boolean anyNotEmpty(Object... values) {
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
     * Formats a message with optional arguments.
     *
     * @param message the message or format string
     * @param args    optional arguments used to format the message
     * @return the formatted message, or the raw message if formatting fails
     */
    private static String formatMessage(String message, Object... args) {
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
     * Determines whether the given value is {@code null} or empty.
     *
     * <p>Emptiness is defined for {@link CharSequence}, {@link Collection},
     * {@link Map}, and arrays. All other non-null objects are considered
     * not empty.</p>
     *
     * @param value the value to inspect; may be {@code null}
     * @return {@code true} if the value is {@code null} or empty
     */
    public static boolean isEmpty(Object value) {
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
        // Array handling: use Array.getLength() for safe reflection-based length check
        return value.getClass().isArray() && Array.getLength(value) == 0;
    }

    /**
     * Determines whether the given value is not {@code null} and not empty.
     *
     * @param value the value to inspect; may be {@code null}
     * @return {@code true} if the value is not {@code null} and not empty
     */
    public static boolean isNotEmpty(Object value) {
        return !isEmpty(value);
    }

    /**
     * Requires all provided values to be empty.
     *
     * <p>The array itself must not be null, but may be empty (all elements are empty).
     * All elements must be null or empty.</p>
     *
     * @param values  the values to inspect; must not be null
     * @param message the exception message; must not be null or empty
     * @throws IllegalArgumentException if any value is not empty
     * @throws IllegalArgumentException if values array is null
     * @throws IllegalArgumentException if message is null or empty
     * @since 1.3
     */
    public static void requireAllEmpty(Object[] values, String message) {
        requireValidMessage(message);
        if (values == null) {
            throw new IllegalArgumentException(message);
        }
        for (Object v : values) {
            if (isNotEmpty(v)) {
                throw new IllegalArgumentException(message);
            }
        }
    }

    /**
     * Requires all provided values to be empty.
     *
     * <p>The array itself must not be null, but may be empty (all elements are empty).
     * All elements must be null or empty. The message may contain
     * {@link String#format(String, Object...)} placeholders, which are resolved
     * using the supplied {@code args}. If formatting fails, the raw message is used.</p>
     *
     * @param values  the values to inspect; must not be null
     * @param message the exception message or format string; must not be null or empty
     * @param args    optional arguments used to format the message
     * @throws IllegalArgumentException if any value is not empty
     * @throws IllegalArgumentException if values array is null
     * @throws IllegalArgumentException if message is null or empty
     * @since 1.3
     */
    public static void requireAllEmpty(Object[] values, String message, Object... args) {
        requireValidMessage(message);
        if (values == null) {
            throw new IllegalArgumentException(formatMessage(message, args));
        }
        for (Object v : values) {
            if (isNotEmpty(v)) {
                throw new IllegalArgumentException(formatMessage(message, args));
            }
        }
    }

    /**
     * Requires all values in the provided map to be empty.
     *
     * <p>The map itself may be {@code null} or empty. If it is non-empty,
     * all values must be {@code null} or empty as defined by {@link #isEmpty(Object)}.
     * If any value is not empty, an {@link IllegalArgumentException} is thrown.</p>
     *
     * @since 1.3
     */
    public static void requireAllEmpty(Map<?, ?> map, String message) {
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
     * Requires all values in the provided map to be empty.
     *
     * <p>The map itself may be {@code null} or empty. If it is non-empty,
     * all values must be {@code null} or empty as defined by {@link #isEmpty(Object)}.
     * The message may contain {@link String#format(String, Object...)} placeholders,
     * which are resolved using the supplied {@code args}. If formatting fails,
     * the raw message is used.</p>
     *
     * @since 1.3
     */
    public static void requireAllEmpty(Map<?, ?> map, String message, Object... args) {
        requireValidMessage(message);
        if (map == null) {
            return;
        }
        for (Object v : map.values()) {
            if (isNotEmpty(v)) {
                throw new IllegalArgumentException(formatMessage(message, args));
            }
        }
    }

    /**
     * Requires all provided values to be not empty.
     *
     * <p>The array itself must not be null or empty, and none of its elements
     * may be null or empty.</p>
     *
     * @param values  the values to inspect; must not be null or empty
     * @param message the exception message; must not be null or empty
     * @throws IllegalArgumentException if values array is null or empty
     * @throws IllegalArgumentException if any value is null or empty
     * @throws IllegalArgumentException if message is null or empty
     * @since 1.3
     */
    public static void requireAllNotEmpty(Object[] values, String message) {
        requireValidMessage(message);
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException(message);
        }
        for (Object v : values) {
            if (isEmpty(v)) {
                throw new IllegalArgumentException(message);
            }
        }
    }

    /**
     * Requires all provided values to be not empty.
     *
     * <p>The array itself must not be null or empty, and none of its elements
     * may be null or empty. The message may contain {@link String#format(String, Object...)}
     * placeholders, which are resolved using the supplied {@code args}. If formatting fails,
     * the raw message is used.</p>
     *
     * @param values  the values to inspect; must not be null or empty
     * @param message the exception message or format string; must not be null or empty
     * @param args    optional arguments used to format the message
     * @throws IllegalArgumentException if values array is null or empty
     * @throws IllegalArgumentException if any value is null or empty
     * @throws IllegalArgumentException if message is null or empty
     * @since 1.3
     */
    public static void requireAllNotEmpty(Object[] values, String message, Object... args) {
        requireValidMessage(message);
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException(formatMessage(message, args));
        }
        for (Object v : values) {
            if (isEmpty(v)) {
                throw new IllegalArgumentException(formatMessage(message, args));
            }
        }
    }

    /**
     * Requires the provided map to be not empty, and all of its values to be not empty.
     *
     * <p>The map itself must not be {@code null} or empty, and none of its values
     * may be {@code null} or empty as defined by {@link #isEmpty(Object)}.
     * If the map is {@code null}, empty, or contains any empty value,
     * an {@link IllegalArgumentException} is thrown.</p>
     *
     * @since 1.3
     */
    public static void requireAllNotEmpty(Map<?, ?> map, String message) {
        requireValidMessage(message);
        if (map == null || map.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
        for (Object v : map.values()) {
            if (isEmpty(v)) {
                throw new IllegalArgumentException(message);
            }
        }
    }

    /**
     * Requires the provided map to be not empty, and all of its values to be not empty.
     *
     * <p>The map itself must not be {@code null} or empty, and none of its values
     * may be {@code null} or empty as defined by {@link #isEmpty(Object)}.
     * The message may contain {@link String#format(String, Object...)} placeholders,
     * which are resolved using the supplied {@code args}. If formatting fails,
     * the raw message is used.</p>
     *
     * @since 1.3
     */
    public static void requireAllNotEmpty(Map<?, ?> map, String message, Object... args) {
        requireValidMessage(message);
        if (map == null || map.isEmpty()) {
            throw new IllegalArgumentException(formatMessage(message, args));
        }
        for (Object v : map.values()) {
            if (isEmpty(v)) {
                throw new IllegalArgumentException(formatMessage(message, args));
            }
        }
    }

    /**
     * Requires the given value to be empty.
     *
     * <p>Emptiness is defined for {@link CharSequence}, {@link Collection},
     * {@link Map}, and arrays. All other non-null objects are considered
     * not empty. If the value is not empty, an {@link IllegalArgumentException}
     * is thrown. Otherwise, the original value is returned unchanged.</p>
     *
     * @param value   the value to inspect; may be {@code null}
     * @param message the exception message; must not be null or empty
     * @param <T>     the value type
     * @return the original value if it is empty
     * @throws IllegalArgumentException if the value is not empty
     * @throws IllegalArgumentException if message is null or empty
     * @since 1.3
     */
    public static <T> T requireEmpty(T value, String message) {
        requireValidMessage(message);
        if (isNotEmpty(value)) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    /**
     * Requires the given value to be empty.
     *
     * <p>Emptiness is defined for {@link CharSequence}, {@link Collection},
     * {@link Map}, and arrays. All other non-null objects are considered
     * not empty. If the value is not empty, an {@link IllegalArgumentException}
     * is thrown. The message may contain {@link String#format(String, Object...)}
     * placeholders, which are resolved using the supplied {@code args}. If
     * formatting fails, the raw message is used. Otherwise, the original value
     * is returned unchanged.</p>
     *
     * @param value   the value to inspect; may be {@code null}
     * @param message the exception message or format string; must not be null or empty
     * @param args    optional arguments used to format the message
     * @param <T>     the value type
     * @return the original value if it is empty
     * @throws IllegalArgumentException if the value is not empty
     * @throws IllegalArgumentException if message is null or empty
     * @since 1.3
     */
    public static <T> T requireEmpty(T value, String message, Object... args) {
        requireValidMessage(message);
        if (isNotEmpty(value)) {
            throw new IllegalArgumentException(formatMessage(message, args));
        }
        return value;
    }

    /**
     * Requires the given value to be not empty.
     *
     * <p>Emptiness is defined for {@link CharSequence}, {@link Collection},
     * {@link Map}, and arrays. All other non-null objects are considered
     * not empty. If the value is empty, an {@link IllegalArgumentException}
     * is thrown. Otherwise, the original value is returned unchanged.</p>
     *
     * @param value   the value to inspect; may be {@code null}
     * @param message the exception message; must not be null or empty
     * @param <T>     the value type
     * @return the original value if it is not empty
     * @throws IllegalArgumentException if the value is null or empty
     * @throws IllegalArgumentException if message is null or empty
     * @since 1.3
     */
    public static <T> T requireNotEmpty(T value, String message) {
        requireValidMessage(message);
        if (isEmpty(value)) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    /**
     * Requires the given value to be not empty.
     *
     * <p>Emptiness is defined for {@link CharSequence}, {@link Collection},
     * {@link Map}, and arrays. All other non-null objects are considered
     * not empty. If the value is empty, an {@link IllegalArgumentException}
     * is thrown. The message may contain {@link String#format(String, Object...)}
     * placeholders, which are resolved using the supplied {@code args}. If
     * formatting fails, the raw message is used. Otherwise, the original value
     * is returned unchanged.</p>
     *
     * @param value   the value to inspect; may be {@code null}
     * @param message the exception message or format string; must not be null or empty
     * @param args    optional arguments used to format the message
     * @param <T>     the value type
     * @return the original value if it is not empty
     * @throws IllegalArgumentException if the value is null or empty
     * @throws IllegalArgumentException if message is null or empty
     * @since 1.3
     */
    public static <T> T requireNotEmpty(T value, String message, Object... args) {
        requireValidMessage(message);
        if (isEmpty(value)) {
            throw new IllegalArgumentException(formatMessage(message, args));
        }
        return value;
    }

    private static void requireValidMessage(String message) {
        if (TextTools.isBlank(message)) {
            throw new IllegalArgumentException("message must not be null or empty");
        }
    }
}