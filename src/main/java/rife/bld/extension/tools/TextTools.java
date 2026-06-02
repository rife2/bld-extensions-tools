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

import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * Utility methods for working with text and character sequences.
 * <p>
 * Provides null‑safe checks for blankness and emptiness across {@link CharSequence}
 * and general {@link Object} inputs, along with a whitespace‑insensitive equality
 * comparison.
 *
 * @author <a href="https://erik.thauvin.net/">Erik C. Thauvin</a>
 * @since 1.0
 */
public final class TextTools {

    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s+");

    private TextTools() {
        // no-op
    }

    /**
     * Compares two or more character sequences by removing all whitespace.
     *
     * @param strings the character sequences to compare
     * @return {@code true} if all character sequences are equivalent when whitespace is ignored,
     * {@code false} otherwise. Returns {@code false} if {@code null} or fewer than 2 elements
     * @implNote {@code null} elements within the array are treated as empty strings, so
     * {@code equalsIgnoreWhitespace(null, "")} returns {@code true}.
     * @since 1.0
     */
    public static boolean equalsIgnoreWhitespace(@Nullable CharSequence... strings) {
        if (strings == null || strings.length < 2) {
            return false;
        }
        var first = removeWhitespace(strings[0]);
        return Arrays.stream(strings, 1, strings.length)
                .map(TextTools::removeWhitespace)
                .allMatch(first::equals);
    }

    /**
     * Checks if a character sequence is {@code null}, empty, or contains only whitespace characters.
     *
     * @param str the character sequence to check
     * @return {@code true} if the character sequence is {@code null}, empty, or whitespace-only;
     * {@code false} otherwise
     * @since 1.0
     */
    public static boolean isBlank(@Nullable CharSequence str) {
        return str == null || str.toString().isBlank();
    }

    /**
     * Checks if all character sequences are {@code null}, empty, or contain only whitespace characters.
     *
     * @param strings the character sequences to check
     * @return {@code true} if all character sequences are {@code null}, empty, or whitespace-only;
     * {@code false} otherwise. Returns {@code true} for {@code null} or empty array (vacuous truth)
     * @since 1.0
     */
    public static boolean isBlank(@Nullable CharSequence... strings) {
        return strings == null || strings.length == 0
                || Arrays.stream(strings).allMatch(TextTools::isBlank);
    }

    /**
     * Checks if all objects are {@code null}, empty, or contain only whitespace characters.
     * <p>
     * {@link CharSequence} instances are checked directly; all other objects are checked via
     * their {@link Object#toString()} representation. This varargs overload exists to support
     * mixed-type arrays — prefer the {@code CharSequence...} overload for homogeneous inputs.
     *
     * @param objects the objects to check
     * @return {@code true} if all objects are {@code null}, their string representations are empty,
     * or their string representations are whitespace-only; {@code false} otherwise
     * @since 1.0
     */
    public static boolean isBlank(@Nullable Object... objects) {
        if (objects == null) {
            return true;
        }
        for (var obj : objects) {
            if (obj instanceof CharSequence cs) {
                if (!isBlank(cs)) {
                    return false;
                }
            } else if (obj != null && !isBlank(obj.toString())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if a character sequence is {@code null} or empty.
     *
     * @param str the character sequence to check
     * @return {@code true} if the character sequence is {@code null} or empty; {@code false} otherwise
     * @since 1.0
     */
    public static boolean isEmpty(@Nullable CharSequence str) {
        return str == null || str.isEmpty(); // CharSequence.isEmpty() since Java 15
    }

    /**
     * Checks if all character sequences are {@code null} or empty.
     *
     * @param strings the character sequences to check
     * @return {@code true} if all character sequences are {@code null} or empty; {@code false} otherwise.
     * Returns {@code true} for {@code null} or empty array (vacuous truth)
     * @since 1.0
     */
    public static boolean isEmpty(@Nullable CharSequence... strings) {
        return strings == null || strings.length == 0
                || Arrays.stream(strings).allMatch(TextTools::isEmpty);
    }

    /**
     * Checks if all objects are {@code null} or their string representations are empty.
     * <p>
     * {@link CharSequence} instances are checked directly; all other objects are checked via
     * their {@link Object#toString()} representation. This varargs overload exists to support
     * mixed-type arrays — prefer the {@code CharSequence...} overload for homogeneous inputs.
     *
     * @param objects the objects to check
     * @return {@code true} if all objects are {@code null} or their string representations are empty;
     * {@code false} otherwise
     * @since 1.0
     */
    public static boolean isEmpty(@Nullable Object... objects) {
        if (objects == null) {
            return true;
        }
        for (var obj : objects) {
            if (obj instanceof CharSequence cs) {
                if (!isEmpty(cs)) {
                    return false;
                }
            } else if (obj != null && !isEmpty(obj.toString())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if a character sequence is not {@code null}, not empty, and not whitespace-only.
     *
     * @param str the character sequence to check
     * @return {@code true} if the character sequence is not {@code null}, not empty, and not whitespace-only;
     * {@code false} otherwise
     * @since 1.0
     */
    public static boolean isNotBlank(@Nullable CharSequence str) {
        return !isBlank(str);
    }

    /**
     * Checks if all character sequences are not {@code null}, not empty, and not whitespace-only.
     *
     * @param strings the character sequences to check
     * @return {@code true} if all character sequences are not {@code null}, not empty, and not whitespace-only;
     * {@code false} otherwise. Returns {@code false} for {@code null} or empty array
     * @since 1.0
     */
    public static boolean isNotBlank(@Nullable CharSequence... strings) {
        return strings != null && strings.length > 0
                && Arrays.stream(strings).noneMatch(TextTools::isBlank);
    }

    /**
     * Checks if all objects are not {@code null}, not empty, and not whitespace-only.
     * <p>
     * {@link CharSequence} instances are checked directly; all other objects are checked via
     * their {@link Object#toString()} representation. This varargs overload exists to support
     * mixed-type arrays — prefer the {@code CharSequence...} overload for homogeneous inputs.
     *
     * @param objects the objects to check
     * @return {@code true} if all objects are not {@code null}, their string representations
     * are not empty, and their string representations are not whitespace-only;
     * {@code false} otherwise
     * @since 1.0
     */
    public static boolean isNotBlank(@Nullable Object... objects) {
        if (objects == null || objects.length == 0) {
            return false;
        }
        for (var obj : objects) {
            if (obj == null) {
                return false;
            }
            if (obj instanceof CharSequence cs) {
                if (isBlank(cs)) {
                    return false;
                }
            } else if (isBlank(obj.toString())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if a character sequence is not {@code null} and not empty.
     *
     * @param str the character sequence to check
     * @return {@code true} if the character sequence is not {@code null} and not empty; {@code false} otherwise
     * @since 1.0
     */
    public static boolean isNotEmpty(@Nullable CharSequence str) {
        return !isEmpty(str);
    }

    /**
     * Checks if all character sequences are not {@code null} and not empty.
     *
     * @param strings the character sequences to check
     * @return {@code true} if all character sequences are not {@code null} and not empty; {@code false} otherwise.
     * Returns {@code false} for {@code null} or empty array
     * @since 1.0
     */
    public static boolean isNotEmpty(@Nullable CharSequence... strings) {
        return strings != null && strings.length > 0
                && Arrays.stream(strings).noneMatch(TextTools::isEmpty);
    }

    /**
     * Checks if all objects are not {@code null} and their string representations are not empty.
     * <p>
     * {@link CharSequence} instances are checked directly; all other objects are checked via
     * their {@link Object#toString()} representation. This varargs overload exists to support
     * mixed-type arrays — prefer the {@code CharSequence...} overload for homogeneous inputs.
     *
     * @param objects the objects to check
     * @return {@code true} if all objects are not {@code null} and their string representations are not empty;
     * {@code false} otherwise
     * @since 1.0
     */
    public static boolean isNotEmpty(@Nullable Object... objects) {
        if (objects == null || objects.length == 0) {
            return false;
        }
        for (var obj : objects) {
            if (obj == null) {
                return false;
            }
            if (obj instanceof CharSequence cs) {
                if (isEmpty(cs)) {
                    return false;
                }
            } else if (isEmpty(obj.toString())) {
                return false;
            }
        }
        return true;
    }

    private static String removeWhitespace(@Nullable CharSequence cs) {
        return cs == null ? "" : WHITESPACE_PATTERN.matcher(cs).replaceAll("");
    }
}