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

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Pattern;

/**
 * Utility methods for working with text and character sequences.
 * <p>
 * Provides null-safe checks for blankness and emptiness across {@link CharSequence},
 * {@link CharSequence} varargs arrays, and {@link Iterable}s of {@link CharSequence},
 * along with a whitespace-insensitive equality comparison. The {@code require*} guard
 * methods accept {@link Collection}s of {@link CharSequence} (so the validated collection
 * can be returned) and {@link CharSequence} varargs.
 * <p>
 * <b>Scope.</b> This class operates on {@link CharSequence} and on containers whose
 * elements are {@link CharSequence}s. Object- and container-level checks on arbitrary
 * objects (primitive arrays, maps, non-text collections) live in {@link ObjectTools};
 * use that class for non-text inputs. The container predicates here inspect the
 * <em>elements</em> rather than the container itself: on an {@link Iterable} of
 * {@code CharSequence}s, {@code isEmpty} means every element is {@code null} or empty —
 * the same semantics as {@link ObjectTools#allEmpty(Object)} for {@code CharSequence}
 * elements ({@code allEmpty} additionally recurses into nested containers, which cannot
 * occur here) — whereas {@link ObjectTools#isEmpty(Object)} reports container-level
 * emptiness (a non-empty container of empty strings is {@code false} there but
 * {@code true} here). Element-level blankness is defined only here.
 * <p>
 * <b>Vacuous truth.</b> The varargs and {@link Iterable} predicate overloads treat a
 * {@code null} or empty input as satisfying "all" conditions and failing "none"
 * conditions: the all-conditions forms ({@code isBlank}, {@code isEmpty}) return
 * {@code true}, while the none-conditions forms ({@code isNotBlank}, {@code isNotEmpty})
 * return {@code false}. {@link #equalsIgnoreWhitespace(CharSequence...)} returns
 * {@code false} for fewer than two arguments.
 * <p>
 * <b>Whitespace definition.</b> "Whitespace" means {@link Character#isWhitespace(char)},
 * matching {@link String#isBlank()}. The same definition drives both the blankness
 * predicates and {@link #equalsIgnoreWhitespace(CharSequence...)} stripping, so the two
 * agree on non-ASCII whitespace such as EM SPACE (U+2003).
 *
 * @author <a href="https://erik.thauvin.net/">Erik C. Thauvin</a>
 * @since 1.0
 */
@NullMarked
@SuppressFBWarnings(
        value = "FCCD_FIND_CLASS_CIRCULAR_DEPENDENCY",
        justification = "ToolsSupport is shared support used by all tools, including TextTools."
)
public final class TextTools {

    private static final String BLANK_MESSAGE = "blankMessage";
    private static final String EMPTY_MESSAGE = "emptyMessage";
    private static final String MUST_NOT_BE_BLANK = " must not be blank";
    private static final String MUST_NOT_BE_EMPTY = " must not be empty";
    private static final String MUST_NOT_BE_NULL = " must not be null";
    private static final String MUST_NOT_CONTAIN_NULL_ELEMENTS = " must not contain null elements";
    private static final String NULL_ELEMENT_MESSAGE = "nullElementMessage";
    private static final String NULL_MESSAGE = "nullMessage";
    /**
     * Matches characters recognized by {@link Character#isWhitespace(char)}, so
     * {@link #equalsIgnoreWhitespace(CharSequence...)} and {@link #isBlank(CharSequence)}
     * agree on what counts as whitespace (including non-ASCII, e.g. EM SPACE, U+2003).
     */
    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\p{javaWhitespace}+");

    private TextTools() {
        // no-op
    }

    private static boolean allMatch(@Nullable CharSequence @Nullable [] values,
                                    Predicate<@Nullable CharSequence> predicate) {
        return values == null || values.length == 0 || Arrays.stream(values).allMatch(predicate);
    }

    private static boolean allMatch(@Nullable Iterable<? extends @Nullable CharSequence> values,
                                    Predicate<@Nullable CharSequence> predicate) {
        if (values == null) {
            return true;
        }
        for (var v : values) {
            if (!predicate.test(v)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Compares two or more character sequences by removing all whitespace.
     *
     * @param strings the character sequences to compare
     * @return {@code true} if all character sequences are equivalent when whitespace is ignored,
     * {@code false} otherwise. Returns {@code false} if {@code null} or fewer than 2 elements
     * @implNote {@code null} elements within the array are treated as empty strings, so
     * {@code equalsIgnoreWhitespace(null, "")} returns {@code true}. Because a single
     * argument is insufficient for comparison, the method is not reflexive for one-element
     * invocations.
     * @since 1.0
     */
    public static boolean equalsIgnoreWhitespace(@Nullable CharSequence @Nullable ... strings) {
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
     * @implNote Delegates to {@link String#isBlank()}, so an exotic {@link CharSequence} is
     * first materialized via {@link CharSequence#toString()}.
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
    public static boolean isBlank(@Nullable CharSequence @Nullable ... strings) {
        return allMatch(strings, TextTools::isBlank);
    }

    /**
     * Checks if all character sequences in the iterable are {@code null}, empty, or
     * contain only whitespace characters.
     *
     * @param strings the character sequences to check; may be {@code null}
     * @return {@code true} if all elements are blank; {@code false} otherwise.
     * Returns {@code true} for a {@code null} or empty iterable (vacuous truth)
     * @since 1.0
     */
    public static boolean isBlank(@Nullable Iterable<? extends @Nullable CharSequence> strings) {
        return allMatch(strings, TextTools::isBlank);
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
    public static boolean isEmpty(@Nullable CharSequence @Nullable ... strings) {
        return allMatch(strings, TextTools::isEmpty);
    }

    /**
     * Checks if all character sequences in the iterable are {@code null} or empty.
     *
     * <p>For a {@code null} or empty iterable this returns {@code true} (vacuous truth),
     * matching {@link ObjectTools#allEmpty(Object)} on the same collection; both treat
     * {@code null} elements as empty. The two are equivalent here only because
     * {@code CharSequence} elements cannot nest: {@code allEmpty} additionally recurses
     * into nested containers up to its maximum nesting depth, which cannot occur for
     * {@code CharSequence} elements. Note that this differs from
     * {@link ObjectTools#isEmpty(Object)}, which reports container-level emptiness (a
     * non-empty container of empty strings is {@code false} there but {@code true}
     * here).</p>
     *
     * @param strings the character sequences to check; may be {@code null}
     * @return {@code true} if all elements are empty; {@code false} otherwise.
     * Returns {@code true} for a {@code null} or empty iterable
     * @since 1.0
     */
    public static boolean isEmpty(@Nullable Iterable<? extends @Nullable CharSequence> strings) {
        return allMatch(strings, TextTools::isEmpty);
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
    public static boolean isNotBlank(@Nullable CharSequence @Nullable ... strings) {
        return noneMatch(strings, TextTools::isBlank);
    }

    /**
     * Checks if none of the character sequences in the iterable are {@code null},
     * empty, or whitespace-only.
     *
     * @param strings the character sequences to check; may be {@code null}
     * @return {@code true} if every element is not blank; {@code false} otherwise.
     * Returns {@code false} for a {@code null} or empty iterable
     * @since 1.0
     */
    public static boolean isNotBlank(@Nullable Iterable<? extends @Nullable CharSequence> strings) {
        return noneMatch(strings, TextTools::isBlank);
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
    public static boolean isNotEmpty(@Nullable CharSequence @Nullable ... strings) {
        return noneMatch(strings, TextTools::isEmpty);
    }

    /**
     * Checks if none of the character sequences in the iterable are {@code null} or empty.
     *
     * @param strings the character sequences to check; may be {@code null}
     * @return {@code true} if every element is not empty; {@code false} otherwise.
     * Returns {@code false} for a {@code null} or empty iterable
     * @since 1.0
     */
    public static boolean isNotEmpty(@Nullable Iterable<? extends @Nullable CharSequence> strings) {
        return noneMatch(strings, TextTools::isEmpty);
    }

    private static boolean noneMatch(@Nullable CharSequence @Nullable [] values,
                                     Predicate<@Nullable CharSequence> predicate) {
        return values != null && values.length > 0 && Arrays.stream(values).noneMatch(predicate);
    }

    private static boolean noneMatch(@Nullable Iterable<? extends @Nullable CharSequence> values,
                                     Predicate<@Nullable CharSequence> predicate) {
        if (values == null) {
            return false;
        }
        var it = values.iterator();
        if (!it.hasNext()) {
            return false;
        }
        do {
            if (predicate.test(it.next())) {
                return false;
            }
        } while (it.hasNext());
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
     * @throws NullPointerException     if {@code str} or either message supplier is {@code null}
     * @throws IllegalArgumentException if {@code str} is empty or whitespace-only
     * @since 1.0
     */
    @Contract("null, _, _ -> fail; !null, _, _ -> !null")
    @NullUnmarked
    @SuppressWarnings("PMD.AvoidThrowingNullPointerException")
    public static <T extends @Nullable Object & CharSequence> @NonNull T requireNotBlank(@Nullable T str,
                                                                                         @NonNull Supplier<String> nullMessage,
                                                                                         @NonNull Supplier<String> blankMessage) {
        Objects.requireNonNull(nullMessage, NULL_MESSAGE);
        Objects.requireNonNull(blankMessage, BLANK_MESSAGE);

        if (str == null) {
            throw new NullPointerException(nullMessage.get());
        }
        if (str.toString().isBlank()) {
            throw new IllegalArgumentException(blankMessage.get());
        }
        return str;
    }

    /**
     * Checks that the specified character sequence is not {@code null}, not empty, and not whitespace-only.
     *
     * @param str     the character sequence to check
     * @param context the context description used in the exception messages; must not be {@code null}
     * @param <T>     the type of the character sequence
     * @return {@code str} if not blank
     * @throws NullPointerException     if {@code str} or {@code context} is {@code null}
     * @throws IllegalArgumentException if {@code str} is empty or whitespace-only
     * @since 1.0
     */
    @Contract("null, _ -> fail; !null, _ -> !null")
    @NullUnmarked
    public static <T extends @Nullable Object & CharSequence> @NonNull T requireNotBlank(@Nullable T str,
                                                                                         @NonNull String context) {
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
     * @throws NullPointerException     if {@code coll} is {@code null}, contains {@code null} elements,
     *                                  or either message supplier is {@code null}
     * @throws IllegalArgumentException if {@code coll} is empty or contains blank elements
     * @since 1.0
     */
    @Contract("null, _, _ -> fail; !null, _, _ -> !null")
    @NullUnmarked
    public static <T extends CharSequence> @NonNull Collection<@NonNull T> requireNotBlank(@Nullable Collection<@Nullable T> coll,
                                                                                           @NonNull Supplier<String> nullMessage,
                                                                                           @NonNull Supplier<String> blankMessage) {
        return requireNotBlankCollection(coll, nullMessage, nullMessage, blankMessage);
    }

    /**
     * Checks that the specified collection of character sequences is not {@code null}, not empty,
     * and contains no {@code null}, empty, or whitespace-only elements.
     *
     * @param coll    the collection to check
     * @param context the context description used in the exception messages; must not be {@code null}
     * @param <T>     the type of the character sequence
     * @return {@code coll} if valid
     * @throws NullPointerException     if {@code coll} or {@code context} is {@code null}, or {@code coll}
     *                                  contains {@code null} elements
     * @throws IllegalArgumentException if {@code coll} is empty or contains blank elements
     * @since 1.0
     */
    @Contract("null, _ -> fail; !null, _ -> !null")
    @NullUnmarked
    public static <T extends CharSequence> @NonNull Collection<@NonNull T> requireNotBlank(@Nullable Collection<@Nullable T> coll,
                                                                                           @NonNull String context) {
        ToolsSupport.requireContext(context);
        return requireNotBlankCollection(coll,
                () -> context + MUST_NOT_BE_NULL,
                () -> context + MUST_NOT_CONTAIN_NULL_ELEMENTS,
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
     * @throws NullPointerException     if {@code elements} is {@code null}, contains {@code null} elements,
     *                                  or either message supplier is {@code null}
     * @throws IllegalArgumentException if {@code elements} is empty or contains blank elements
     * @since 1.0
     */
    @Contract("_, _, null -> fail; _, _, !null -> !null")
    @NullUnmarked
    @SafeVarargs
    public static <T extends CharSequence> @NonNull T @NonNull [] requireNotBlank(@NonNull Supplier<String> nullMessage,
                                                                                  @NonNull Supplier<String> blankMessage,
                                                                                  @Nullable T @Nullable ... elements) {
        return requireNotBlankArray(nullMessage, nullMessage, blankMessage, elements);
    }

    /**
     * Checks that the specified varargs array of character sequences is not {@code null}, not empty,
     * and contains no {@code null}, empty, or whitespace-only elements.
     *
     * <p>When {@code elements} is {@code null}, a {@link NullPointerException} with the
     * message {@code context + " must not be null"} is thrown. When {@code elements} is
     * empty, the {@code context}-derived blank message is used. When an element is
     * {@code null}, the message is {@code context + " must not contain null elements"}.</p>
     *
     * <p><b>Overload resolution.</b> A one-element invocation such as
     * {@code requireNotBlank("ctx", "a")} resolves to the fixed-arity
     * {@code (T str, String context)} overload in the non-varargs phase, not to this
     * varargs form, and so validates {@code "ctx"} using {@code "a"} as the context.
     * Use this form with two or more elements, or pass an explicit array.</p>
     *
     * @param context  the context description used in the exception messages; must not be {@code null}
     * @param elements the elements to check
     * @param <T>      the type of the character sequence
     * @return {@code elements} if valid
     * @throws NullPointerException     if {@code elements} or {@code context} is {@code null}, or
     *                                  {@code elements} contains {@code null} elements
     * @throws IllegalArgumentException if {@code elements} is empty or contains blank elements
     * @since 1.0
     */
    @Contract("_, null -> fail; _, !null -> !null")
    @NullUnmarked
    @SafeVarargs
    public static <T extends CharSequence> @NonNull T @NonNull [] requireNotBlank(@NonNull String context,
                                                                                  @Nullable T @Nullable ... elements) {
        ToolsSupport.requireContext(context);
        return requireNotBlankArray(
                () -> context + MUST_NOT_BE_NULL,
                () -> context + MUST_NOT_CONTAIN_NULL_ELEMENTS,
                () -> context + " must not be empty or contain blank elements",
                elements);
    }

    @SafeVarargs
    @SuppressWarnings({"PMD.UnnecessaryCast", "RedundantCast", "PMD.AvoidThrowingNullPointerException"})
    private static <T extends CharSequence> T[] requireNotBlankArray(
            Supplier<String> nullMessage,
            Supplier<String> nullElementMessage,
            Supplier<String> blankMessage,
            @Nullable T @Nullable ... elements) {
        Objects.requireNonNull(nullMessage, NULL_MESSAGE);
        Objects.requireNonNull(nullElementMessage, NULL_ELEMENT_MESSAGE);
        Objects.requireNonNull(blankMessage, BLANK_MESSAGE);

        if (elements == null) {
            throw new NullPointerException(nullMessage.get());
        }
        if (elements.length == 0) {
            throw new IllegalArgumentException(blankMessage.get());
        }
        for (var element : elements) {
            if (element == null) {
                throw new NullPointerException(nullElementMessage.get());
            }
            if (element.toString().isBlank()) {
                throw new IllegalArgumentException(blankMessage.get());
            }
        }
        return (T @NonNull []) elements;
    }

    @SuppressWarnings({"PMD.UnnecessaryCast", "RedundantCast", "PMD.AvoidThrowingNullPointerException"})
    private static <T extends CharSequence> Collection<T> requireNotBlankCollection(
            @Nullable Collection<@Nullable T> coll,
            Supplier<String> nullMessage,
            Supplier<String> nullElementMessage,
            Supplier<String> blankMessage) {
        Objects.requireNonNull(nullMessage, NULL_MESSAGE);
        Objects.requireNonNull(nullElementMessage, NULL_ELEMENT_MESSAGE);
        Objects.requireNonNull(blankMessage, BLANK_MESSAGE);

        if (coll == null) {
            throw new NullPointerException(nullMessage.get());
        }
        if (coll.isEmpty()) {
            throw new IllegalArgumentException(blankMessage.get());
        }
        for (var element : coll) {
            if (element == null) {
                throw new NullPointerException(nullElementMessage.get());
            }
            if (element.toString().isBlank()) {
                throw new IllegalArgumentException(blankMessage.get());
            }
        }
        return (Collection<T>) coll;
    }

    /**
     * Checks that the specified character sequence is not {@code null} and not empty.
     *
     * @param str          the character sequence to check
     * @param <T>          the type of the character sequence
     * @param nullMessage  the supplier of the {@link NullPointerException} message; must not be {@code null}
     * @param emptyMessage the supplier of the {@link IllegalArgumentException} message; must not be {@code null}
     * @return {@code str} if not empty
     * @throws NullPointerException     if {@code str} or either message supplier is {@code null}
     * @throws IllegalArgumentException if {@code str} is empty
     * @since 1.0
     */
    @Contract("null, _, _ -> fail; !null, _, _ -> !null")
    @NullUnmarked
    @SuppressWarnings("PMD.AvoidThrowingNullPointerException")
    public static <T extends @Nullable Object & CharSequence> @NonNull T requireNotEmpty(@Nullable T str,
                                                                                         @NonNull Supplier<String> nullMessage,
                                                                                         @NonNull Supplier<String> emptyMessage) {
        Objects.requireNonNull(nullMessage, NULL_MESSAGE);
        Objects.requireNonNull(emptyMessage, EMPTY_MESSAGE);

        if (str == null) {
            throw new NullPointerException(nullMessage.get());
        }
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
     * @param context the context description used in the exception messages; must not be {@code null}
     * @return {@code str} if not empty
     * @throws NullPointerException     if {@code str} or {@code context} is {@code null}
     * @throws IllegalArgumentException if {@code str} is empty
     * @since 1.0
     */
    @Contract("null, _ -> fail; !null, _ -> !null")
    @NullUnmarked
    public static <T extends @Nullable Object & CharSequence> @NonNull T requireNotEmpty(@Nullable T str,
                                                                                         @NonNull String context) {
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
     * @throws NullPointerException     if {@code coll} is {@code null}, contains {@code null} elements,
     *                                  or either message supplier is {@code null}
     * @throws IllegalArgumentException if {@code coll} is empty or contains empty elements
     * @since 1.0
     */
    @Contract("null, _, _ -> fail; !null, _, _ -> !null")
    @NullUnmarked
    public static <T extends CharSequence> @NonNull Collection<@NonNull T> requireNotEmpty(@Nullable Collection<@Nullable T> coll,
                                                                                           @NonNull Supplier<String> nullMessage,
                                                                                           @NonNull Supplier<String> emptyMessage) {
        return requireNotEmptyCollection(coll, nullMessage, nullMessage, emptyMessage);
    }

    /**
     * Checks that the specified collection of character sequences is not {@code null}, not empty,
     * and contains no {@code null} or empty elements.
     *
     * @param coll    the collection to check
     * @param context the context description used in the exception messages; must not be {@code null}
     * @param <T>     the type of the character sequence
     * @return {@code coll} if valid
     * @throws NullPointerException     if {@code coll} or {@code context} is {@code null}, or {@code coll}
     *                                  contains {@code null} elements
     * @throws IllegalArgumentException if {@code coll} is empty or contains empty elements
     * @since 1.0
     */
    @Contract("null, _ -> fail; !null, _ -> !null")
    @NullUnmarked
    public static <T extends CharSequence> @NonNull Collection<@NonNull T> requireNotEmpty(@Nullable Collection<@Nullable T> coll,
                                                                                           @NonNull String context) {
        ToolsSupport.requireContext(context);
        return requireNotEmptyCollection(coll,
                () -> context + MUST_NOT_BE_NULL,
                () -> context + MUST_NOT_CONTAIN_NULL_ELEMENTS,
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
     * @throws NullPointerException     if {@code elements} is {@code null}, contains {@code null} elements,
     *                                  or either message supplier is {@code null}
     * @throws IllegalArgumentException if {@code elements} is empty or contains empty elements
     * @since 1.0
     */
    @Contract("_, _, null -> fail; _, _, !null -> !null")
    @NullUnmarked
    @SafeVarargs
    public static <T extends CharSequence> @NonNull T @NonNull [] requireNotEmpty(@NonNull Supplier<String> nullMessage,
                                                                                  @NonNull Supplier<String> emptyMessage,
                                                                                  @Nullable T @Nullable ... elements) {
        return requireNotEmptyArray(nullMessage, nullMessage, emptyMessage, elements);
    }

    /**
     * Checks that the specified varargs array of character sequences is not {@code null}, not empty,
     * and contains no {@code null} or empty elements.
     *
     * <p>When {@code elements} is {@code null}, a {@link NullPointerException} with the
     * message {@code context + " must not be null"} is thrown. When {@code elements} is
     * empty, the {@code context}-derived empty message is used. When an element is
     * {@code null}, the message is {@code context + " must not contain null elements"}.</p>
     *
     * <p><b>Overload resolution.</b> A one-element invocation such as
     * {@code requireNotEmpty("ctx", "a")} resolves to the fixed-arity
     * {@code (T str, String context)} overload in the non-varargs phase, not to this
     * varargs form, and so validates {@code "ctx"} using {@code "a"} as the context.
     * Use this form with two or more elements, or pass an explicit array.</p>
     *
     * @param context  the context description used in the exception messages; must not be {@code null}
     * @param elements the elements to check
     * @param <T>      the type of the character sequence
     * @return {@code elements} if valid
     * @throws NullPointerException     if {@code elements} or {@code context} is {@code null}, or
     *                                  {@code elements} contains {@code null} elements
     * @throws IllegalArgumentException if {@code elements} is empty or contains empty elements
     * @since 1.0
     */
    @Contract("_, null -> fail; _, !null -> !null")
    @NullUnmarked
    @SafeVarargs
    public static <T extends CharSequence> @NonNull T @NonNull [] requireNotEmpty(@NonNull String context,
                                                                                  @Nullable T @Nullable ... elements) {
        ToolsSupport.requireContext(context);
        return requireNotEmptyArray(
                () -> context + MUST_NOT_BE_NULL,
                () -> context + MUST_NOT_CONTAIN_NULL_ELEMENTS,
                () -> context + " must not be empty or contain empty elements",
                elements);
    }

    @SafeVarargs
    @SuppressWarnings({"PMD.UnnecessaryCast", "RedundantCast", "PMD.AvoidThrowingNullPointerException"})
    private static <T extends CharSequence> T[] requireNotEmptyArray(
            Supplier<String> nullMessage,
            Supplier<String> nullElementMessage,
            Supplier<String> emptyMessage,
            @Nullable T @Nullable ... elements) {
        Objects.requireNonNull(nullMessage, NULL_MESSAGE);
        Objects.requireNonNull(nullElementMessage, NULL_ELEMENT_MESSAGE);
        Objects.requireNonNull(emptyMessage, EMPTY_MESSAGE);

        if (elements == null) {
            throw new NullPointerException(nullMessage.get());
        }
        if (elements.length == 0) {
            throw new IllegalArgumentException(emptyMessage.get());
        }
        for (var element : elements) {
            if (element == null) {
                throw new NullPointerException(nullElementMessage.get());
            }
            if (element.isEmpty()) {
                throw new IllegalArgumentException(emptyMessage.get());
            }
        }
        return (T @NonNull []) elements;
    }

    @SuppressWarnings({"PMD.UnnecessaryCast", "RedundantCast", "PMD.AvoidThrowingNullPointerException"})
    private static <T extends CharSequence> Collection<T> requireNotEmptyCollection(
            @Nullable Collection<@Nullable T> coll,
            Supplier<String> nullMessage,
            Supplier<String> nullElementMessage,
            Supplier<String> emptyMessage) {
        Objects.requireNonNull(nullMessage, NULL_MESSAGE);
        Objects.requireNonNull(nullElementMessage, NULL_ELEMENT_MESSAGE);
        Objects.requireNonNull(emptyMessage, EMPTY_MESSAGE);

        if (coll == null) {
            throw new NullPointerException(nullMessage.get());
        }
        if (coll.isEmpty()) {
            throw new IllegalArgumentException(emptyMessage.get());
        }
        for (var element : coll) {
            if (element == null) {
                throw new NullPointerException(nullElementMessage.get());
            }
            if (element.isEmpty()) {
                throw new IllegalArgumentException(emptyMessage.get());
            }
        }
        return (Collection<T>) coll;
    }
}