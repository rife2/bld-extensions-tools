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
import java.util.function.Supplier;
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

    private static final String BLANK_MESSAGE = "blankMessage";
    private static final String EMPTY_MESSAGE = "emptyMessage";
    private static final String MUST_NOT_BE_BLANK = " must not be blank";
    private static final String MUST_NOT_BE_EMPTY = " must not be empty";
    private static final String MUST_NOT_BE_NULL = " must not be null";
    private static final String NULL_MESSAGE = "nullMessage";
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
        return str == null || str.isEmpty();
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
     * @param str          the character sequence to check
     * @param nullMessage  the supplier of the {@link NullPointerException} message; must not be {@code null}
     * @param blankMessage the supplier of the {@link IllegalArgumentException} message; must not be {@code null}
     * @param <T>          the type of the character sequence
     * @return {@code str} if not blank
     * @throws NullPointerException     if {@code str} is {@code null}
     * @throws IllegalArgumentException if {@code str} is empty or whitespace-only
     * @since 1.0
     */
    @SuppressFBWarnings("NP_PARAMETER_MUST_BE_NONNULL_BUT_MARKED_AS_NULLABLE")
    public static <T extends CharSequence> T requireNotBlank(@Nullable T str,
                                                             @NonNull Supplier<String> nullMessage,
                                                             @NonNull Supplier<String> blankMessage) {
        Objects.requireNonNull(nullMessage, NULL_MESSAGE);
        Objects.requireNonNull(blankMessage, BLANK_MESSAGE);

        Objects.requireNonNull(str, nullMessage);
        if (str.toString().isBlank()) {
            throw new IllegalArgumentException(blankMessage.get());
        }
        return str;
    }

    /**
     * Checks that the specified character sequence is not {@code null}, not empty, and not whitespace-only.
     *
     * @param str     the character sequence to check
     * @param context the context description used in the exception messages
     * @param <T>     the type of the character sequence
     * @return {@code str} if not blank
     * @throws NullPointerException     if {@code str} or {@code context} is {@code null}
     * @throws IllegalArgumentException if {@code str} is empty or whitespace-only
     * @since 1.0
     */
    public static <T extends CharSequence> T requireNotBlank(@Nullable T str, @NonNull String context) {
        ToolsSupport.requireContext(context);
        return requireNotBlank(str, () -> context + MUST_NOT_BE_NULL, () -> context + MUST_NOT_BE_BLANK);
    }

    /**
     * Checks that the specified collection of character sequences is not {@code null}, not empty,
     * and contains no {@code null}, empty, or whitespace-only elements.
     *
     * @param coll         the collection to check
     * @param <T>          the type of the character sequence
     * @param nullMessage  the supplier of the {@link NullPointerException} message; must not be {@code null}
     * @param blankMessage the supplier of the {@link IllegalArgumentException} message; must not be {@code null}
     * @return {@code coll} if valid
     * @throws NullPointerException     if {@code coll} is {@code null} or contains {@code null} elements
     * @throws IllegalArgumentException if {@code coll} is empty or contains blank elements
     * @since 1.0
     */
    @SuppressFBWarnings("NP_PARAMETER_MUST_BE_NONNULL_BUT_MARKED_AS_NULLABLE")
    public static <T extends CharSequence> Collection<T> requireNotBlank(@Nullable Collection<T> coll,
                                                                         @NonNull Supplier<String> nullMessage,
                                                                         @NonNull Supplier<String> blankMessage) {

        Objects.requireNonNull(nullMessage, NULL_MESSAGE);
        Objects.requireNonNull(blankMessage, BLANK_MESSAGE);

        Objects.requireNonNull(coll, nullMessage);

        if (coll.isEmpty()) {
            throw new IllegalArgumentException(blankMessage.get());
        }

        for (T element : coll) {
            Objects.requireNonNull(element, nullMessage);
            if (element.toString().isBlank()) {
                throw new IllegalArgumentException(blankMessage.get());
            }
        }
        return coll;
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
    public static <T extends CharSequence> Collection<T> requireNotBlank(@Nullable Collection<T> coll,
                                                                         @NonNull String context) {
        ToolsSupport.requireContext(context);
        return requireNotBlank(coll,
                () -> context + MUST_NOT_BE_NULL,
                () -> context + " must not be empty or contain blank elements");
    }

    /**
     * Checks that the specified varargs array of character sequences is not {@code null}, not empty,
     * and contains no {@code null}, empty, or whitespace-only elements.
     *
     * @param nullMessage  the supplier of the {@link NullPointerException} message; must not be {@code null}
     * @param blankMessage the supplier of the {@link IllegalArgumentException} message; must not be {@code null}
     * @param elements     the elements to check
     * @param <T>          the type of the character sequence
     * @return {@code elements} if valid
     * @throws NullPointerException     if {@code elements} is {@code null} or contains {@code null} elements
     * @throws IllegalArgumentException if {@code elements} is empty or contains blank elements
     * @since 1.0
     */
    @SafeVarargs
    public static <T extends CharSequence> T[] requireNotBlank(@NonNull Supplier<String> nullMessage,
                                                               @NonNull Supplier<String> blankMessage,
                                                               @Nullable T... elements) {

        Objects.requireNonNull(nullMessage, NULL_MESSAGE);
        Objects.requireNonNull(blankMessage, BLANK_MESSAGE);

        Objects.requireNonNull(elements, nullMessage);

        if (elements.length == 0) {
            throw new IllegalArgumentException(blankMessage.get());
        }

        for (T element : elements) {
            Objects.requireNonNull(element, nullMessage);
            if (element.toString().isBlank()) {
                throw new IllegalArgumentException(blankMessage.get());
            }
        }
        return elements;
    }

    /**
     * Checks that the specified varargs array of character sequences is not {@code null}, not empty,
     * and contains no {@code null}, empty, or whitespace-only elements.
     *
     * @param context  the context description used in the exception messages
     * @param elements the elements to check
     * @param <T>      the type of the character sequence
     * @return {@code elements} if valid
     * @throws NullPointerException     if {@code elements} is {@code null} or contains {@code null} elements
     * @throws IllegalArgumentException if {@code elements} is empty or contains blank elements
     * @since 1.0
     */
    @SafeVarargs
    public static <T extends CharSequence> T[] requireNotBlank(
            @NonNull String context,
            @Nullable T... elements) {
        ToolsSupport.requireContext(context);
        return requireNotBlank(
                () -> context + MUST_NOT_BE_NULL,
                () -> context + " must not be empty or contain blank elements",
                elements);
    }

    /**
     * Checks that the specified character sequence is not {@code null} and not empty.
     *
     * @param str          the character sequence to check
     * @param <T>          the type of the character sequence
     * @param nullMessage  the supplier of the {@link NullPointerException} message; must not be {@code null}
     * @param emptyMessage the supplier of the {@link IllegalArgumentException} message; must not be {@code null}
     * @return {@code str} if not empty
     * @throws NullPointerException     if {@code str} is {@code null}
     * @throws IllegalArgumentException if {@code str} is empty
     * @since 1.0
     */
    @SuppressFBWarnings("NP_PARAMETER_MUST_BE_NONNULL_BUT_MARKED_AS_NULLABLE")
    public static <T extends CharSequence> T requireNotEmpty(@Nullable T str,
                                                             @NonNull Supplier<String> nullMessage,
                                                             @NonNull Supplier<String> emptyMessage) {
        Objects.requireNonNull(nullMessage, NULL_MESSAGE);
        Objects.requireNonNull(emptyMessage, EMPTY_MESSAGE);

        Objects.requireNonNull(str, nullMessage);

        if (str.isEmpty()) {
            throw new IllegalArgumentException(emptyMessage.get());
        }
        return str;
    }

    /**
     * Checks that the specified character sequence is not {@code null} and not empty.
     *
     * @param str     the character sequence to check
     * @param <T>     the type of the character sequence
     * @param context the context description used in the exception messages
     * @return {@code str} if not empty
     * @throws NullPointerException     if {@code str} is {@code null}
     * @throws IllegalArgumentException if {@code str} is empty
     * @since 1.0
     */
    public static <T extends CharSequence> T requireNotEmpty(@Nullable T str, @NonNull String context) {
        ToolsSupport.requireContext(context);
        return requireNotEmpty(str, () -> context + MUST_NOT_BE_NULL, () -> context + MUST_NOT_BE_EMPTY);
    }

    /**
     * Checks that the specified collection of character sequences is not {@code null}, not empty,
     * and contains no {@code null} or empty elements.
     *
     * @param coll         the collection to check
     * @param <T>          the type of the character sequence
     * @param nullMessage  the supplier of the {@link NullPointerException} message; must not be {@code null}
     * @param emptyMessage the supplier of the {@link IllegalArgumentException} message; must not be {@code null}
     * @return {@code coll} if valid
     * @throws NullPointerException     if {@code coll} is {@code null} or contains {@code null} elements
     * @throws IllegalArgumentException if {@code coll} is empty or contains empty elements
     * @since 1.0
     */
    @SuppressFBWarnings("NP_PARAMETER_MUST_BE_NONNULL_BUT_MARKED_AS_NULLABLE")
    public static <T extends CharSequence> Collection<T> requireNotEmpty(@Nullable Collection<T> coll,
                                                                         @NonNull Supplier<String> nullMessage,
                                                                         @NonNull Supplier<String> emptyMessage) {
        Objects.requireNonNull(nullMessage, NULL_MESSAGE);
        Objects.requireNonNull(emptyMessage, EMPTY_MESSAGE);

        Objects.requireNonNull(coll, nullMessage);

        if (coll.isEmpty()) {
            throw new IllegalArgumentException(emptyMessage.get());
        }

        for (T element : coll) {
            Objects.requireNonNull(element, nullMessage);
            if (element.isEmpty()) {
                throw new IllegalArgumentException(emptyMessage.get());
            }
        }
        return coll;
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
                () -> context + MUST_NOT_BE_NULL,
                () -> context + " must not be empty or contain empty elements");
    }

    /**
     * Checks that the specified varargs array of character sequences is not {@code null}, not empty,
     * and contains no {@code null} or empty elements.
     *
     * @param nullMessage  the supplier of the {@link NullPointerException} message; must not be {@code null}
     * @param emptyMessage the supplier of the {@link IllegalArgumentException} message; must not be {@code null}
     * @param elements     the elements to check
     * @param <T>          the type of the character sequence
     * @return {@code elements} if valid
     * @throws NullPointerException     if {@code elements} is {@code null} or contains {@code null} elements
     * @throws IllegalArgumentException if {@code elements} is empty or contains empty elements
     * @since 1.0
     */
    @SafeVarargs
    public static <T extends CharSequence> T[] requireNotEmpty(
            @NonNull Supplier<String> nullMessage,
            @NonNull Supplier<String> emptyMessage,
            @Nullable T... elements) {

        Objects.requireNonNull(nullMessage, NULL_MESSAGE);
        Objects.requireNonNull(emptyMessage, EMPTY_MESSAGE);

        Objects.requireNonNull(elements, nullMessage);

        if (elements.length == 0) {
            throw new IllegalArgumentException(emptyMessage.get());
        }

        for (T element : elements) {
            Objects.requireNonNull(element, nullMessage);
            if (element.isEmpty()) {
                throw new IllegalArgumentException(emptyMessage.get());
            }
        }
        return elements;
    }

    /**
     * Checks that the specified varargs array of character sequences is not {@code null}, not empty,
     * and contains no {@code null} or empty elements.
     *
     * @param context  the context description used in the exception messages
     * @param elements the elements to check
     * @param <T>      the type of the character sequence
     * @return {@code elements} if valid
     * @throws NullPointerException     if {@code elements} is {@code null} or contains {@code null} elements
     * @throws IllegalArgumentException if {@code elements} is empty or contains empty elements
     * @since 1.0
     */
    @SafeVarargs
    public static <T extends CharSequence> T[] requireNotEmpty(
            @NonNull String context,
            @Nullable T... elements) {
        ToolsSupport.requireContext(context);
        return requireNotEmpty(
                () -> context + MUST_NOT_BE_NULL,
                () -> context + " must not be empty or contain empty elements",
                elements);
    }
}