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

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Collection Tools.
 * <p>
 * All public methods accept {@code null} varargs arrays or {@code null} collection references
 * and return an empty unmodifiable list in those cases. Individual {@code null} elements within
 * collections or varargs are silently ignored. Empty collections are also skipped.
 * <p>
 * The internal methods {@code combineAndMap} and {@code combineAndMapVarargs} are implementation
 * details and not part of the public API.
 * <p>
 * Conversion methods may propagate {@link java.nio.file.InvalidPathException}
 * (malformed path strings), {@link UnsupportedOperationException} (paths that
 * cannot be converted to a {@link File}), or {@link java.io.IOError} (when
 * resolving an absolute path).
 *
 * @author <a href="https://erik.thauvin.net/">Erik C. Thauvin</a>
 * @since 1.0
 */
@NullMarked
@SuppressWarnings("PMD.CouplingBetweenObjects") // Multiple conversions between File/Path/String are intentional
public final class CollectionTools {

    private static final Logger logger = Logger.getLogger(CollectionTools.class.getName());

    private CollectionTools() {
        // no-op
    }

    /**
     * Combines multiple collections into a single list, ignoring any {@code null}
     * collections, empty collections, or {@code null} elements.
     * <p>
     * Returns an unmodifiable list. Returns an empty list if the input array is {@code null}.
     *
     * @param collections the collections to combine, may be {@code null}
     * @param <T>         the element type
     * @return an unmodifiable list containing all non-null elements
     * @since 1.0
     */
    @SafeVarargs // Safe because we don't store the array or expose it to untrusted code
    public static <T> List<T> combine(@Nullable Collection<T> @Nullable ... collections) {
        return combineAndMap(collections, t -> t);
    }

    /**
     * Combines varargs elements into a single list, ignoring any {@code null} elements.
     * <p>
     * Returns an unmodifiable list. Returns an empty list if the input array is {@code null}.
     *
     * @param elements the elements to combine, may be {@code null}
     * @param <T>      the element type
     * @return an unmodifiable list containing all non-null elements
     * @since 1.0
     */
    @SafeVarargs // Safe because we don't store the array or expose it to untrusted code
    public static <T> List<T> combine(@Nullable T @Nullable ... elements) {
        return combineAndMapVarargs(elements, t -> t);
    }

    /**
     * Internal implementation for combining collections and applying a mapper.
     * <p>
     * Tracks whether any null collections, null elements, empty collections, or null
     * mapper results were ignored and logs a single debug message summarizing the
     * dropped inputs. The input array reference itself may also be null, in which
     * case no collections are processed.
     * <p>
     * Returns an unmodifiable list. Returns an empty list if the input array
     * reference is {@code null}. Mapper results that are {@code null} are ignored,
     * so the returned list never contains {@code null} elements.
     *
     * @since 1.0
     */
    static <T, R> List<R> combineAndMap(@Nullable Collection<T> @Nullable [] collections,
                                        Function<T, @Nullable R> mapper) {
        if (collections == null) {
            logger.fine("Ignored null collections array");
            return List.of();
        }

        List<R> result = new ArrayList<>();
        Set<DropReason> dropped = EnumSet.noneOf(DropReason.class);

        for (@Nullable Collection<@Nullable T> c : collections) {
            if (c == null) {
                dropped.add(DropReason.NULL_COLLECTION);
                continue;
            }
            if (c.isEmpty()) {
                dropped.add(DropReason.EMPTY_COLLECTION);
                continue;
            }
            //noinspection NullableProblems
            for (@Nullable T e : c) {
                if (e == null) {
                    dropped.add(DropReason.NULL_ELEMENT);
                    continue;
                }
                R mapped = mapper.apply(e);
                if (mapped == null) {
                    dropped.add(DropReason.NULL_MAPPED);
                    continue;
                }
                result.add(mapped);
            }
        }

        logDropped(dropped);
        return List.copyOf(result);
    }

    /**
     * Internal implementation for combining varargs and applying a mapper.
     * <p>
     * Tracks whether the varargs array reference was null, any null elements were
     * ignored, or any null mapper results were ignored and logs a single debug
     * message summarizing the dropped inputs. The input array reference itself may
     * be null, in which case no elements are processed. Note: empty collections
     * are not applicable for varargs.
     * <p>
     * Returns an unmodifiable list. Returns an empty list if the input array
     * reference is {@code null}. Mapper results that are {@code null} are ignored,
     * so the returned list never contains {@code null} elements.
     *
     * @since 1.0
     */
    static <T, R> List<R> combineAndMapVarargs(@Nullable T @Nullable [] elements,
                                               Function<T, @Nullable R> mapper) {
        if (elements == null) {
            logger.fine("Ignored null varargs array");
            return List.of();
        }

        List<R> result = new ArrayList<>();
        Set<DropReason> dropped = EnumSet.noneOf(DropReason.class);

        for (@Nullable T e : elements) {
            if (e == null) {
                dropped.add(DropReason.NULL_ELEMENT);
                continue;
            }
            R mapped = mapper.apply(e);
            if (mapped == null) {
                dropped.add(DropReason.NULL_MAPPED);
                continue;
            }
            result.add(mapped);
        }

        logDropped(dropped);
        return List.copyOf(result);
    }

    /**
     * Combines multiple {@link File} collections into a list of {@link Path} objects.
     * <p>
     * Null collections, empty collections, and null elements are ignored.
     * Returns an unmodifiable list.
     *
     * @param collections the file collections to combine
     * @return an unmodifiable list of paths
     * @since 1.0
     */
    @SafeVarargs // Safe because we don't store the array or expose it to untrusted code
    public static List<Path> combineFilesToPaths(@Nullable Collection<File> @Nullable ... collections) {
        return combineAndMap(collections, CollectionTools::toPath);
    }

    /**
     * Combines varargs {@link File} elements into a list of {@link Path} objects.
     * <p>
     * Null elements are ignored. Returns an unmodifiable list.
     *
     * @param files the files to combine
     * @return an unmodifiable list of paths
     * @since 1.0
     */
    public static List<Path> combineFilesToPaths(@Nullable File @Nullable ... files) {
        return combineAndMapVarargs(files, CollectionTools::toPath);
    }

    /**
     * Returns an empty unmodifiable list, disambiguating the no-argument call.
     *
     * @return an empty unmodifiable list
     * @since 1.0
     */
    public static List<Path> combineFilesToPaths() {
        return List.of();
    }

    /**
     * Combines multiple {@link File} collections into a list of normalized absolute path strings.
     * <p>
     * Uses {@link Path#toAbsolutePath()} and {@link Path#normalize()}.
     * Returns an unmodifiable list.
     *
     * @param collections the file collections to combine
     * @return an unmodifiable list of normalized path strings
     * @since 1.0
     */
    @SafeVarargs // Safe because we don't store the array or expose it to untrusted code
    public static List<String> combineFilesToStrings(@Nullable Collection<File> @Nullable ... collections) {
        return combineAndMap(collections, CollectionTools::toNormalizedString);
    }

    /**
     * Combines varargs {@link File} elements into a list of normalized absolute path strings.
     * <p>
     * Uses {@link Path#toAbsolutePath()} and {@link Path#normalize()}.
     * Returns an unmodifiable list.
     *
     * @param files the files to combine
     * @return an unmodifiable list of normalized path strings
     * @since 1.0
     */
    public static List<String> combineFilesToStrings(@Nullable File @Nullable ... files) {
        return combineAndMapVarargs(files, CollectionTools::toNormalizedString);
    }

    /**
     * Returns an empty unmodifiable list, disambiguating the no-argument call.
     *
     * @return an empty unmodifiable list
     * @since 1.0
     */
    public static List<String> combineFilesToStrings() {
        return List.of();
    }

    /**
     * Combines multiple {@link Path} collections into a list of {@link File} objects.
     * <p>
     * Null collections, empty collections, and null elements are ignored.
     * Returns an unmodifiable list.
     *
     * @param collections the path collections to combine
     * @return an unmodifiable list of files
     * @since 1.0
     */
    @SafeVarargs // Safe because we don't store the array or expose it to untrusted code
    public static List<File> combinePathsToFiles(@Nullable Collection<Path> @Nullable ... collections) {
        return combineAndMap(collections, CollectionTools::toFile);
    }

    /**
     * Combines varargs {@link Path} elements into a list of {@link File} objects.
     * <p>
     * Null elements are ignored. Returns an unmodifiable list.
     *
     * @param paths the paths to combine
     * @return an unmodifiable list of files
     * @since 1.0
     */
    public static List<File> combinePathsToFiles(@Nullable Path @Nullable ... paths) {
        return combineAndMapVarargs(paths, CollectionTools::toFile);
    }

    /**
     * Returns an empty unmodifiable list, disambiguating the no-argument call.
     *
     * @return an empty unmodifiable list
     * @since 1.0
     */
    public static List<File> combinePathsToFiles() {
        return List.of();
    }

    /**
     * Combines multiple {@link Path} collections into a list of normalized absolute path strings.
     * <p>
     * Uses {@link Path#toAbsolutePath()} and {@link Path#normalize()}.
     * Returns an unmodifiable list.
     *
     * @param collections the path collections to combine
     * @return an unmodifiable list of normalized path strings
     * @since 1.0
     */
    @SafeVarargs // Safe because we don't store the array or expose it to untrusted code
    public static List<String> combinePathsToStrings(@Nullable Collection<Path> @Nullable ... collections) {
        return combineAndMap(collections, CollectionTools::toNormalizedString);
    }

    /**
     * Combines varargs {@link Path} elements into a list of normalized absolute path strings.
     * <p>
     * Uses {@link Path#toAbsolutePath()} and {@link Path#normalize()}.
     * Returns an unmodifiable list.
     *
     * @param paths the paths to combine
     * @return an unmodifiable list of normalized path strings
     * @since 1.0
     */
    public static List<String> combinePathsToStrings(@Nullable Path @Nullable ... paths) {
        return combineAndMapVarargs(paths, CollectionTools::toNormalizedString);
    }

    /**
     * Returns an empty unmodifiable list, disambiguating the no-argument call.
     *
     * @return an empty unmodifiable list
     * @since 1.0
     */
    public static List<String> combinePathsToStrings() {
        return List.of();
    }

    /**
     * Combines multiple string collections into a list of {@link File} objects.
     * <p>
     * Null collections, empty collections, and null elements are ignored.
     * Returns an unmodifiable list.
     *
     * @param collections the string collections to combine
     * @return an unmodifiable list of files
     * @since 1.0
     */
    @SafeVarargs // Safe because we don't store the array or expose it to untrusted code
    public static List<File> combineStringsToFiles(@Nullable Collection<String> @Nullable ... collections) {
        return combineAndMap(collections, CollectionTools::toFile);
    }

    /**
     * Combines varargs string elements into a list of {@link File} objects.
     * <p>
     * Null elements are ignored. Returns an unmodifiable list.
     *
     * @param strings the strings to combine
     * @return an unmodifiable list of files
     * @since 1.0
     */
    public static List<File> combineStringsToFiles(@Nullable String @Nullable ... strings) {
        return combineAndMapVarargs(strings, CollectionTools::toFile);
    }

    /**
     * Returns an empty unmodifiable list, disambiguating the no-argument call.
     *
     * @return an empty unmodifiable list
     * @since 1.0
     */
    public static List<File> combineStringsToFiles() {
        return List.of();
    }

    /**
     *
     * Combines multiple string collections into a list of {@link Path} objects.
     * <p>
     * Null collections, empty collections, and null elements are ignored.
     * Returns an unmodifiable list.
     *
     * @param collections the string collections to combine
     * @return an unmodifiable list of paths
     * @since 1.0
     */
    @SafeVarargs // Safe because we don't store the array or expose it to untrusted code
    public static List<Path> combineStringsToPaths(@Nullable Collection<String> @Nullable ... collections) {
        return combineAndMap(collections, CollectionTools::toPath);
    }

    /**
     * Combines varargs string elements into a list of {@link Path} objects.
     * <p>
     * Null elements are ignored. Returns an unmodifiable list.
     *
     * @param strings the strings to combine
     * @return an unmodifiable list of paths
     * @since 1.0
     */
    public static List<Path> combineStringsToPaths(@Nullable String @Nullable ... strings) {
        return combineAndMapVarargs(strings, CollectionTools::toPath);
    }

    /**
     * Returns an empty unmodifiable list, disambiguating the no-argument call.
     *
     * @return an empty unmodifiable list
     * @since 1.0
     */
    public static List<Path> combineStringsToPaths() {
        return List.of();
    }

    /**
     * Logs a single FINE message summarizing the categories of inputs that were
     * dropped during a combine operation.
     * <p>
     * The message is only constructed when FINE logging is enabled, and categories
     * are reported in a stable order.
     *
     * @param reasons the set of drop reasons, may be empty
     */
    private static void logDropped(Set<DropReason> reasons) {
        if (reasons.isEmpty() || !logger.isLoggable(Level.FINE)) {
            return;
        }
        var messages = new ArrayList<String>(reasons.size());
        for (DropReason reason : reasons) {
            messages.add(reason.message());
        }
        logger.fine("Dropped " + String.join(", ", messages));
    }

    private static File toFile(Path p) {
        return p.toFile();
    }

    private static File toFile(String s) {
        return new File(s);
    }

    private static String toNormalizedString(Path p) {
        return p.toAbsolutePath().normalize().toString();
    }

    private static String toNormalizedString(File f) {
        return toNormalizedString(f.toPath());
    }

    private static Path toPath(File f) {
        return f.toPath();
    }

    private static Path toPath(String s) {
        return Path.of(s);
    }

    /**
     * Categories of inputs that a combine operation may drop.
     */
    private enum DropReason {
        NULL_COLLECTION("null collections"),
        NULL_ELEMENT("null elements"),
        NULL_MAPPED("null mapper results"),
        EMPTY_COLLECTION("empty collections");

        private final String message;

        DropReason(String message) {
            this.message = message;
        }

        String message() {
            return message;
        }
    }
}