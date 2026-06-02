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

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
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
 *
 * @author <a href="https://erik.thauvin.net/">Erik C. Thauvin</a>
 * @since 1.0
 */
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
    public static <T> List<T> combine(@Nullable Collection<T>... collections) {
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
    public static <T> List<T> combine(@Nullable T... elements) {
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
     * reference is {@code null}. Mapper results that are {@code null} are ignored.
     *
     * @since 1.0
     */
    static <T, R> List<R> combineAndMap(@Nullable Collection<T>[] collections,
                                        Function<T, R> mapper) {
        if (collections == null) {
            logger.fine("Ignored null collections array");
            return List.of();
        }

        List<R> result = new ArrayList<>();
        boolean droppedNullCollection = false;
        boolean droppedNullElement = false;
        boolean droppedEmpty = false;
        boolean droppedNullMapped = false;

        for (Collection<T> c : collections) {
            if (c == null) {
                droppedNullCollection = true;
                continue;
            }
            if (c.isEmpty()) {
                droppedEmpty = true;
                continue;
            }
            for (T e : c) {
                if (e == null) {
                    droppedNullElement = true;
                    continue;
                }
                R mapped = mapper.apply(e);
                if (mapped == null) {
                    droppedNullMapped = true;
                    continue;
                }
                result.add(mapped);
            }
        }

        if (droppedNullCollection || droppedNullElement || droppedEmpty || droppedNullMapped) {
            List<String> reasons = new ArrayList<>();
            if (droppedNullCollection) {
                reasons.add("null collections");
            }
            if (droppedNullElement) {
                reasons.add("null elements");
            }
            if (droppedNullMapped) {
                reasons.add("null mapper results");
            }
            if (droppedEmpty) {
                reasons.add("empty collections");
            }
            logger.fine("Dropped " + String.join(", ", reasons));
        }

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
     * reference is {@code null}. Mapper results that are {@code null} are ignored.
     *
     * @since 1.0
     */
    static <T, R> List<R> combineAndMapVarargs(@Nullable T[] elements,
                                               Function<T, R> mapper) {
        if (elements == null) {
            logger.fine("Ignored null varargs array");
            return List.of();
        }

        List<R> result = new ArrayList<>();
        boolean droppedNullElement = false;
        boolean droppedNullMapped = false;

        for (T e : elements) {
            if (e == null) {
                droppedNullElement = true;
                continue;
            }
            R mapped = mapper.apply(e);
            if (mapped == null) {
                droppedNullMapped = true;
                continue;
            }
            result.add(mapped);
        }

        if (droppedNullElement || droppedNullMapped) {
            String reason = droppedNullElement && droppedNullMapped
                    ? "null elements or null mapper results"
                    : droppedNullElement ? "null elements" : "null mapper results";
            logger.fine("Dropped " + reason);
        }

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
    public static List<Path> combineFilesToPaths(@Nullable Collection<File>... collections) {
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
    public static List<Path> combineFilesToPaths(@Nullable File... files) {
        return combineAndMapVarargs(files, CollectionTools::toPath);
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
    public static List<String> combineFilesToStrings(@Nullable Collection<File>... collections) {
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
    public static List<String> combineFilesToStrings(@Nullable File... files) {
        return combineAndMapVarargs(files, CollectionTools::toNormalizedString);
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
    public static List<File> combinePathsToFiles(@Nullable Collection<Path>... collections) {
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
    public static List<File> combinePathsToFiles(@Nullable Path... paths) {
        return combineAndMapVarargs(paths, CollectionTools::toFile);
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
    public static List<String> combinePathsToStrings(@Nullable Collection<Path>... collections) {
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
    public static List<String> combinePathsToStrings(@Nullable Path... paths) {
        return combineAndMapVarargs(paths, CollectionTools::toNormalizedString);
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
    public static List<File> combineStringsToFiles(@Nullable Collection<String>... collections) {
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
    public static List<File> combineStringsToFiles(@Nullable String... strings) {
        return combineAndMapVarargs(strings, CollectionTools::toFile);
    }

    /**
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
    public static List<Path> combineStringsToPaths(@Nullable Collection<String>... collections) {
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
    public static List<Path> combineStringsToPaths(@Nullable String... strings) {
        return combineAndMapVarargs(strings, CollectionTools::toPath);
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
}