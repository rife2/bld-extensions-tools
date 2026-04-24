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

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.logging.Logger;

/**
 * Collection Tools.
 * <p>
 * All public methods accept {@code null} varargs arrays or {@code null} collection references
 * and return an empty list in those cases. Individual {@code null} elements within collections
 * or varargs are silently ignored.
 * <p>
 * The internal methods {@code combineAndMap} and {@code combineAndMapVarargs} are implementation
 * details and not part of the public API.
 *
 * @author <a href="https://erik.thauvin.net/">Erik C. Thauvin</a>
 * @since 1.0
 */
@SuppressWarnings("PMD.CouplingBetweenObjects")
public final class CollectionTools {

    private static final Logger logger = Logger.getLogger(CollectionTools.class.getName());

    private CollectionTools() {
        // no-op
    }

    /**
     * Combines multiple collections into a single list, ignoring any {@code null}
     * collections or {@code null} elements.
     * <p>
     * Returns an empty list if the input array is {@code null}.
     *
     * @since 1.0
     */
    @SafeVarargs
    public static <T> List<T> combine(@Nullable Collection<T>... collections) {
        return combineAndMap(collections, t -> t);
    }

    /**
     * Combines varargs elements into a single list, ignoring any {@code null} elements.
     * <p>
     * Returns an empty list if the input array is {@code null}.
     *
     * @since 1.0
     */
    @SafeVarargs
    public static <T> List<T> combine(@Nullable T... elements) {
        return combineAndMapVarargs(elements, t -> t);
    }

    /**
     * Internal implementation for combining collections and applying a mapper.
     * <p>
     * Tracks whether any null collections, null elements, or empty collections were
     * ignored and logs a single warning summarizing the dropped inputs. The input
     * array reference itself may also be null, in which case no collections are
     * processed.
     * <p>
     * Returns an unmodifiable list. Returns an empty list if the input array
     * reference is {@code null}.
     *
     * @since 1.0
     */
    static <T, R> List<R> combineAndMap(@Nullable Collection<T>[] collections,
                                        Function<T, R> mapper) {
        if (collections == null) {
            logger.warning("Ignored null collections array");
            return List.of();
        }

        List<R> result = new ArrayList<>();
        boolean droppedNull = false;
        boolean droppedEmpty = false;

        for (Collection<T> c : collections) {
            if (c == null) {
                droppedNull = true;
                continue;
            }
            if (c.isEmpty()) {
                droppedEmpty = true;
                continue;
            }
            for (T e : c) {
                if (e == null) {
                    droppedNull = true;
                    continue;
                }
                result.add(mapper.apply(e));
            }
        }

        if (droppedNull && droppedEmpty) {
            logger.warning("Dropped one or more null elements or collections and one or more empty collections");
        } else if (droppedNull) {
            logger.warning("Dropped one or more null elements or collections");
        } else if (droppedEmpty) {
            logger.warning("Dropped one or more empty collections");
        }

        return List.copyOf(result);
    }

    /**
     * Internal implementation for combining varargs and applying a mapper.
     * <p>
     * Tracks whether the varargs array reference was null or any null elements were
     * ignored and logs a single warning summarizing the dropped inputs. The input
     * array reference itself may be null, in which case no elements are processed.
     * <p>
     * Returns an unmodifiable list. Returns an empty list if the input array
     * reference is {@code null}.
     *
     * @since 1.0
     */
    static <T, R> List<R> combineAndMapVarargs(@Nullable T[] elements,
                                               Function<T, R> mapper) {
        if (elements == null) {
            logger.warning("Ignored null varargs array");
            return List.of();
        }

        List<R> result = new ArrayList<>();
        boolean droppedNull = false;

        for (T e : elements) {
            if (e == null) {
                droppedNull = true;
                continue;
            }
            result.add(mapper.apply(e));
        }

        if (droppedNull) {
            logger.warning("Dropped one or more null elements");
        }

        return List.copyOf(result);
    }

    /**
     * Combines multiple {@link File} collections into a list of {@link Path} objects.
     * <p>
     * Null collections and null elements are ignored.
     *
     * @since 1.0
     */
    @SafeVarargs
    public static List<Path> combineFilesToPaths(@Nullable Collection<File>... collections) {
        return combineAndMap(collections, f -> Objects.requireNonNull(f).toPath());
    }

    /**
     * Combines varargs {@link File} elements into a list of {@link Path} objects.
     * <p>
     * Null elements are ignored.
     *
     * @since 1.0
     */
    public static List<Path> combineFilesToPaths(@Nullable File... files) {
        return combineAndMapVarargs(files, f -> Objects.requireNonNull(f).toPath());
    }

    /**
     * Combines multiple {@link File} collections into a list of normalized absolute path strings.
     * <p>
     * Uses {@link Path#toAbsolutePath()} and {@link Path#normalize()}.
     *
     * @since 1.0
     */
    @SafeVarargs
    public static List<String> combineFilesToStrings(@Nullable Collection<File>... collections) {
        return combineAndMap(collections,
                f -> Objects.requireNonNull(f).toPath().toAbsolutePath().normalize().toString());
    }

    /**
     * Combines varargs {@link File} elements into a list of normalized absolute path strings.
     * <p>
     * Uses {@link Path#toAbsolutePath()} and {@link Path#normalize()}.
     *
     * @since 1.0
     */
    public static List<String> combineFilesToStrings(@Nullable File... files) {
        return combineAndMapVarargs(files,
                f -> Objects.requireNonNull(f).toPath().toAbsolutePath().normalize().toString());
    }

    /**
     * Combines multiple {@link Path} collections into a list of {@link File} objects.
     * <p>
     * Null collections and null elements are ignored.
     *
     * @since 1.0
     */
    @SafeVarargs
    public static List<File> combinePathsToFiles(@Nullable Collection<Path>... collections) {
        return combineAndMap(collections, p -> Objects.requireNonNull(p).toFile());
    }

    /**
     * Combines varargs {@link Path} elements into a list of {@link File} objects.
     * <p>
     * Null elements are ignored.
     *
     * @since 1.0
     */
    public static List<File> combinePathsToFiles(@Nullable Path... paths) {
        return combineAndMapVarargs(paths, p -> Objects.requireNonNull(p).toFile());
    }

    /**
     * Combines multiple {@link Path} collections into a list of absolute path strings.
     * <p>
     * Null collections and null elements are ignored.
     *
     * @since 1.0
     */
    @SafeVarargs
    public static List<String> combinePathsToStrings(@Nullable Collection<Path>... collections) {
        return combineAndMap(collections,
                p -> Objects.requireNonNull(p).toAbsolutePath().toString());
    }

    /**
     * Combines varargs {@link Path} elements into a list of absolute path strings.
     * <p>
     * Null elements are ignored.
     *
     * @since 1.0
     */
    public static List<String> combinePathsToStrings(@Nullable Path... paths) {
        return combineAndMapVarargs(paths,
                p -> Objects.requireNonNull(p).toAbsolutePath().toString());
    }

    /**
     * Combines multiple string collections into a list of {@link File} objects.
     * <p>
     * Null collections and null elements are ignored.
     *
     * @since 1.0
     */
    @SafeVarargs
    public static List<File> combineStringsToFiles(@Nullable Collection<String>... collections) {
        return combineAndMap(collections, s -> new File(Objects.requireNonNull(s)));
    }

    /**
     * Combines varargs string elements into a list of {@link File} objects.
     * <p>
     * Null elements are ignored.
     *
     * @since 1.0
     */
    public static List<File> combineStringsToFiles(@Nullable String... strings) {
        return combineAndMapVarargs(strings, s -> new File(Objects.requireNonNull(s)));
    }

    /**
     * Combines multiple string collections into a list of {@link Path} objects.
     * <p>
     * Null collections and null elements are ignored.
     *
     * @since 1.0
     */
    @SafeVarargs
    public static List<Path> combineStringsToPaths(@Nullable Collection<String>... collections) {
        return combineAndMap(collections, s -> Path.of(Objects.requireNonNull(s)));
    }

    /**
     * Combines varargs string elements into a list of {@link Path} objects.
     * <p>
     * Null elements are ignored.
     *
     * @since 1.0
     */
    public static List<Path> combineStringsToPaths(@Nullable String... strings) {
        return combineAndMapVarargs(strings, s -> Path.of(Objects.requireNonNull(s)));
    }
}