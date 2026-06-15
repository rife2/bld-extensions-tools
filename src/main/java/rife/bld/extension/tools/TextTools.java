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

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
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

    private static final String MUST_NOT_BE_BLANK = " must not be blank";
    private static final String MUST_NOT_BE_EMPTY = " must not be empty";
    private static final String MUST_NOT_BE_NULL = " must not be null";
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
        return strings == null || strings.length == 0 || Arrays.stream(strings).allMatch(TextTools::isEmpty);
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

    /**
     * Checks that the specified character sequence is not {@code null}, not empty, and not whitespace-only.
     *
     * @param str     the character sequence to check
     * @param context the context description used in the exception messages
     * @param <T>     the type of the character sequence
     * @return {@code str} if not blank
     * @throws NullPointerException     if {@code str} is {@code null}
     * @throws IllegalArgumentException if {@code str} is empty or whitespace-only
     * @since 1.0
     */
    public static <T extends CharSequence> T requireNotBlank(@Nullable T str, @NonNull String context) {
        ToolsSupport.requireContext(context);
        return requireNotBlank(str, context + MUST_NOT_BE_NULL, context + MUST_NOT_BE_BLANK);
    }

    /**
     * Checks that the specified character sequence is not {@code null}, not empty, and not whitespace-only,
     * and throws a customized exception if it is.
     *
     * @param str          the character sequence to check
     * @param nullMessage  detail message if {@code str} is {@code null}
     * @param blankMessage detail message if {@code str} is empty or whitespace-only
     * @param args         optional format arguments for the messages
     * @param <T>          the type of the character sequence
     * @return {@code str} if not blank
     * @throws NullPointerException     if {@code str} is {@code null}
     * @throws IllegalArgumentException if {@code str} is empty or whitespace-only
     * @since 1.0
     */
    @SuppressFBWarnings("NP_PARAMETER_MUST_BE_NONNULL_BUT_MARKED_AS_NULLABLE")
    public static <T extends CharSequence> T requireNotBlank(@Nullable T str,
                                                             @NonNull String nullMessage,
                                                             @NonNull String blankMessage,
                                                             @Nullable Object... args) {
        Objects.requireNonNull(str, ToolsSupport.formatMessage(nullMessage, args));

        if (str.toString().isBlank()) {
            throw new IllegalArgumentException(ToolsSupport.formatMessage(blankMessage, args));
        }
        return str;
    }

    /**
     * Checks that the specified collection of character sequences is not {@code null}, not empty,
     * and contains no {@code null}, empty, or whitespace-only elements.
     *
     * @param coll    the collection to check
     * @param context the context description used in the exception messages
     * @param <T>     the type of the character sequence
     * @return {@code coll} if valid
     * @throws NullPointerException     if {@code coll} is {@code null} or contains {@code null} elements
     * @throws IllegalArgumentException if {@code coll} is empty or contains blank elements
     * @since 1.0
     */
    public static <T extends CharSequence> Collection<T> requireNotBlank(
            @Nullable Collection<T> coll, @NonNull String context) {
        ToolsSupport.requireContext(context);
        return requireNotBlank(coll,
                context + MUST_NOT_BE_NULL,
                context + MUST_NOT_BE_EMPTY,
                context + " must not contain null elements",
                context + " must not contain blank elements");
    }

    /**
     * Checks that the specified collection of character sequences is not {@code null}, not empty,
     * and contains no {@code null}, empty, or whitespace-only elements,
     * and throws customized exceptions if validation fails.
     *
     * @param coll               the collection to check
     * @param nullMessage        detail message if {@code coll} is {@code null}
     * @param emptyMessage       detail message if {@code coll} is empty
     * @param nullElementMessage detail message if {@code coll} contains {@code null} elements
     * @param blankMessage       detail message if {@code coll} contains empty or whitespace-only elements
     * @param args               optional format arguments for the messages
     * @param <T>                the type of the character sequence
     * @return {@code coll} if valid
     * @throws NullPointerException     if {@code coll} is {@code null} or contains {@code null} elements
     * @throws IllegalArgumentException if {@code coll} is empty or contains blank elements
     * @since 1.0
     */
    @SuppressFBWarnings("NP_PARAMETER_MUST_BE_NONNULL_BUT_MARKED_AS_NULLABLE")
    public static <T extends CharSequence> Collection<T> requireNotBlank(
            @Nullable Collection<T> coll,
            @NonNull String nullMessage,
            @NonNull String emptyMessage,
            @NonNull String nullElementMessage,
            @NonNull String blankMessage,
            @Nullable Object... args) {

        Objects.requireNonNull(nullMessage, "nullMessage" + MUST_NOT_BE_NULL);
        Objects.requireNonNull(emptyMessage, "emptyMessage" + MUST_NOT_BE_NULL);
        Objects.requireNonNull(nullElementMessage, "nullElementMessage" + MUST_NOT_BE_NULL);
        Objects.requireNonNull(blankMessage, "blankMessage" + MUST_NOT_BE_NULL);

        Objects.requireNonNull(coll, () -> ToolsSupport.formatMessage(nullMessage, args));

        if (coll.isEmpty()) {
            throw new IllegalArgumentException(ToolsSupport.formatMessage(emptyMessage, args));
        }

        for (T element : coll) {
            Objects.requireNonNull(element, () -> ToolsSupport.formatMessage(nullElementMessage, args));
            if (element.toString().isBlank()) {
                throw new IllegalArgumentException(ToolsSupport.formatMessage(blankMessage, args));
            }
        }
        return coll;
    }

    /**
     * Checks that the specified character sequence is not {@code null} and not empty.
     *
     * @param str     the character sequence to check
     * @param context the context description used in the exception messages
     * @param <T>     the type of the character sequence
     * @return {@code str} if not empty
     * @throws NullPointerException     if {@code str} is {@code null}
     * @throws IllegalArgumentException if {@code str} is empty
     * @since 1.0
     */
    public static <T extends CharSequence> T requireNotEmpty(@Nullable T str, @NonNull String context) {
        ToolsSupport.requireContext(context);
        return requireNotEmpty(str, context + MUST_NOT_BE_NULL, context + MUST_NOT_BE_EMPTY);
    }

    /**
     * Checks that the specified character sequence is not {@code null} and not empty,
     * and throws a customized exception if it is.
     *
     * @param str          the character sequence to check
     * @param nullMessage  detail message if {@code str} is {@code null}
     * @param emptyMessage detail message if {@code str} is empty
     * @param args         optional format arguments for the messages
     * @param <T>          the type of the character sequence
     * @return {@code str} if not empty
     * @throws NullPointerException     if {@code str} is {@code null}
     * @throws IllegalArgumentException if {@code str} is empty
     * @since 1.0
     */
    @SuppressFBWarnings("NP_PARAMETER_MUST_BE_NONNULL_BUT_MARKED_AS_NULLABLE")
    public static <T extends CharSequence> T requireNotEmpty(@Nullable T str,
                                                             @NonNull String nullMessage,
                                                             @NonNull String emptyMessage,
                                                             @Nullable Object... args) {
        Objects.requireNonNull(str, ToolsSupport.formatMessage(nullMessage, args));

        if (str.isEmpty()) {
            throw new IllegalArgumentException(ToolsSupport.formatMessage(emptyMessage, args));
        }
        return str;
    }

    /**
     * Checks that the specified collection of character sequences is not {@code null}, not empty,
     * and contains no {@code null} or empty elements.
     *
     * @param coll    the collection to check
     * @param context the context description used in the exception messages
     * @param <T>     the type of the character sequence
     * @return {@code coll} if valid
     * @throws NullPointerException     if {@code coll} is {@code null} or contains {@code null} elements
     * @throws IllegalArgumentException if {@code coll} is empty or contains empty elements
     * @since 1.0
     */
    public static <T extends CharSequence> Collection<T> requireNotEmpty(
            @Nullable Collection<T> coll, @NonNull String context) {
        ToolsSupport.requireContext(context);
        return requireNotEmpty(coll,
                context + MUST_NOT_BE_NULL,
                context + MUST_NOT_BE_EMPTY,
                context + " must not contain null elements",
                context + " must not contain empty elements");
    }

    /**
     * Checks that the specified collection of character sequences is not {@code null}, not empty,
     * and contains no {@code null} or empty elements, and throws customized exceptions if validation fails.
     *
     * @param coll                the collection to check
     * @param nullMessage         detail message if {@code coll} is {@code null}
     * @param emptyMessage        detail message if {@code coll} is empty
     * @param nullElementMessage  detail message if {@code coll} contains {@code null} elements
     * @param emptyElementMessage detail message if {@code coll} contains empty elements
     * @param args                optional format arguments for the messages
     * @param <T>                 the type of the character sequence
     * @return {@code coll} if valid
     * @throws NullPointerException     if {@code coll} is {@code null} or contains {@code null} elements
     * @throws IllegalArgumentException if {@code coll} is empty or contains empty elements
     * @since 1.0
     */
    @SuppressFBWarnings("NP_PARAMETER_MUST_BE_NONNULL_BUT_MARKED_AS_NULLABLE")
    public static <T extends CharSequence> Collection<T> requireNotEmpty(
            @Nullable Collection<T> coll,
            @NonNull String nullMessage,
            @NonNull String emptyMessage,
            @NonNull String nullElementMessage,
            @NonNull String emptyElementMessage,
            @Nullable Object... args) {

        Objects.requireNonNull(nullMessage, "nullMessage" + MUST_NOT_BE_NULL);
        Objects.requireNonNull(emptyMessage, "emptyMessage" + MUST_NOT_BE_NULL);
        Objects.requireNonNull(nullElementMessage, "nullElementMessage" + MUST_NOT_BE_NULL);
        Objects.requireNonNull(emptyElementMessage, "emptyElementMessage" + MUST_NOT_BE_NULL);

        Objects.requireNonNull(coll, () -> ToolsSupport.formatMessage(nullMessage, args));

        if (coll.isEmpty()) {
            throw new IllegalArgumentException(ToolsSupport.formatMessage(emptyMessage, args));
        }

        for (T element : coll) {
            Objects.requireNonNull(element, () -> ToolsSupport.formatMessage(nullElementMessage, args));
            if (element.isEmpty()) {
                throw new IllegalArgumentException(ToolsSupport.formatMessage(emptyElementMessage, args));
            }
        }
        return coll;
    }
}