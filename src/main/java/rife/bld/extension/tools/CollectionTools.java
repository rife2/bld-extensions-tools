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
import java.util.*;
import java.util.function.Function;

/**
 * Collection Tools.
 *
 * <p>All public methods accept {@code null} varargs arrays or {@code null} collection references
 * and will return an empty list in those cases. Individual {@code null} elements within
 * collections or varargs are also silently ignored.</p>
 *
 * <p>{@link #combineAndMap(Collection[], Function)} and
 * {@link #combineAndMapVarargs(Object[], Function)} are intentionally package-private
 * implementation details and are not part of the public API.</p>
 *
 * @author <a href="https://erik.thauvin.net/">Erik C. Thauvin</a>
 * @since 1.0
 */
public final class CollectionTools {

    private CollectionTools() {
        // no-op
    }

    /**
     * Combines multiple collections into a single list, ignoring any
     * {@code null} collections or {@code null} elements.
     *
     * @param collections the collections to combine; may be {@code null}
     * @param <T>         the type of elements
     * @return a new list containing all non-null elements from all non-null collections,
     * or an empty list if {@code collections} is {@code null}
     * @since 1.0
     */
    @SafeVarargs
    public static <T> List<T> combine(@Nullable Collection<T>... collections) {
        return combineAndMap(collections, Function.identity());
    }

    /**
     * Combines varargs elements into a single list, ignoring any {@code null} elements.
     *
     * @param elements the elements to combine; may be {@code null}, and individual elements
     *                 may also be {@code null} (they are silently dropped)
     * @param <T>      the type of elements
     * @return a new list containing all non-null elements,
     * or an empty list if {@code elements} is {@code null}
     * @since 1.0
     */
    @SafeVarargs
    public static <T> List<T> combine(@Nullable T... elements) {
        return combineAndMapVarargs(elements, Function.identity());
    }

    /**
     * Combines multiple collections into a single list by applying the given mapper
     * function to each element, ignoring any {@code null} collections or {@code null}
     * elements.
     *
     * <p>This method is an intentionally package-private implementation detail.</p>
     *
     * @param collections the collections to combine; may be {@code null}
     * @param mapper      the function to apply to each element
     * @param <T>         the type of input elements
     * @param <R>         the type of output elements
     * @return a new list containing the mapped results of all non-null elements from
     * all non-null collections, or an empty list if {@code collections} is {@code null}
     * @since 1.0
     */
    static <T, R> List<R> combineAndMap(@Nullable Collection<T>[] collections, Function<T, R> mapper) {
        // collections itself can only be null if called as, e.g., combineStringsToFiles((Collection[]) null);
        // the guard is intentional, not dead code
        if (collections == null) {
            return Collections.emptyList();
        }
        return Arrays.stream(collections)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .map(mapper)
                .toList();
    }

    /**
     * Combines varargs elements into a single list by applying the given mapper
     * function to each element, ignoring any {@code null} elements.
     *
     * <p>This method is an intentionally package-private implementation detail.</p>
     *
     * @param elements the elements to combine; may be {@code null}, and individual elements
     *                 may also be {@code null} (they are silently dropped)
     * @param mapper   the function to apply to each element
     * @param <T>      the type of input elements
     * @param <R>      the type of output elements
     * @return a new list containing the mapped results of all non-null elements,
     * or an empty list if {@code elements} is {@code null}
     * @since 1.0
     */
    static <T, R> List<R> combineAndMapVarargs(@Nullable T[] elements, Function<T, R> mapper) {
        if (elements == null) {
            return Collections.emptyList();
        }
        return Arrays.stream(elements)
                .filter(Objects::nonNull)
                .map(mapper)
                .toList();
    }

    /**
     * Combines multiple {@link File} collections into a single list of {@link Path}
     * objects, ignoring any {@code null} collections or {@code null} elements.
     *
     * @param collections the collections of files to combine; may be {@code null}
     * @return a new list of {@link Path} objects for all non-null files from all
     * non-null collections, or an empty list if {@code collections} is {@code null}
     * @see File#toPath()
     * @since 1.0
     */
    @SafeVarargs
    public static List<Path> combineFilesToPaths(@Nullable Collection<File>... collections) {
        return combineAndMap(collections, f -> Objects.requireNonNull(f).toPath());
    }

    /**
     * Combines varargs {@link File} elements into a single list of {@link Path}
     * objects, ignoring any {@code null} elements.
     *
     * @param files the files to combine; may be {@code null}, and individual elements
     *              may also be {@code null} (they are silently dropped)
     * @return a new list of {@link Path} objects for all non-null files,
     * or an empty list if {@code files} is {@code null}
     * @see File#toPath()
     * @since 1.0
     */
    public static List<Path> combineFilesToPaths(@Nullable File... files) {
        return combineAndMapVarargs(files, f -> Objects.requireNonNull(f).toPath());
    }

    /**
     * Combines multiple {@link File} collections into a single list of normalized absolute
     * path strings, ignoring any {@code null} collections or {@code null} elements.
     *
     * <p>Uses {@link Path#toAbsolutePath()} followed by {@link Path#normalize()} to resolve
     * relative paths and eliminate redundant {@code ..} and {@code .} segments. This is
     * more robust than {@link File#getAbsolutePath()}, which does not normalize the path.</p>
     *
     * @param collections the collections of files to combine; may be {@code null}
     * @return a new list of normalized absolute path strings for all non-null files from all
     * non-null collections, or an empty list if {@code collections} is {@code null}
     * @see Path#toAbsolutePath()
     * @see Path#normalize()
     * @since 1.0
     */
    @SafeVarargs
    public static List<String> combineFilesToStrings(@Nullable Collection<File>... collections) {
        return combineAndMap(collections, f -> Objects.requireNonNull(f).toPath().toAbsolutePath().normalize().toString());
    }

    /**
     * Combines varargs {@link File} elements into a single list of normalized absolute path
     * strings, ignoring any {@code null} elements.
     *
     * <p>Uses {@link Path#toAbsolutePath()} followed by {@link Path#normalize()} to resolve
     * relative paths and eliminate redundant {@code ..} and {@code .} segments. This is
     * more robust than {@link File#getAbsolutePath()}, which does not normalize the path.</p>
     *
     * @param files the files to combine; may be {@code null}, and individual elements
     *              may also be {@code null} (they are silently dropped)
     * @return a new list of normalized absolute path strings for all non-null files,
     * or an empty list if {@code files} is {@code null}
     * @see Path#toAbsolutePath()
     * @see Path#normalize()
     * @since 1.0
     */
    public static List<String> combineFilesToStrings(@Nullable File... files) {
        return combineAndMapVarargs(files, f -> Objects.requireNonNull(f).toPath().toAbsolutePath().normalize().toString());
    }

    /**
     * Combines multiple {@link Path} collections into a single list of {@link File}
     * objects, ignoring any {@code null} collections or {@code null} elements.
     *
     * @param collections the collections of paths to combine; may be {@code null}
     * @return a new list of {@link File} objects for all non-null paths from all
     * non-null collections, or an empty list if {@code collections} is {@code null}
     * @see Path#toFile()
     * @since 1.0
     */
    @SafeVarargs
    public static List<File> combinePathsToFiles(@Nullable Collection<Path>... collections) {
        return combineAndMap(collections, p -> Objects.requireNonNull(p).toFile());
    }

    /**
     * Combines varargs {@link Path} elements into a single list of {@link File}
     * objects, ignoring any {@code null} elements.
     *
     * @param paths the paths to combine; may be {@code null}, and individual elements
     *              may also be {@code null} (they are silently dropped)
     * @return a new list of {@link File} objects for all non-null paths,
     * or an empty list if {@code paths} is {@code null}
     * @see Path#toFile()
     * @since 1.0
     */
    public static List<File> combinePathsToFiles(@Nullable Path... paths) {
        return combineAndMapVarargs(paths, p -> Objects.requireNonNull(p).toFile());
    }

    /**
     * Combines multiple {@link Path} collections into a single list of path
     * strings, ignoring any {@code null} collections or {@code null} elements.
     *
     * @param collections the collections of paths to combine; may be {@code null}
     * @return a new list of path strings for all non-null paths from all
     * non-null collections, or an empty list if {@code collections} is {@code null}
     * @see Path#toString()
     * @since 1.0
     */
    @SafeVarargs
    public static List<String> combinePathsToStrings(@Nullable Collection<Path>... collections) {
        return combineAndMap(collections, p -> Objects.requireNonNull(p).toString());
    }

    /**
     * Combines varargs {@link Path} elements into a single list of path strings,
     * ignoring any {@code null} elements.
     *
     * @param paths the paths to combine; may be {@code null}, and individual elements
     *              may also be {@code null} (they are silently dropped)
     * @return a new list of path strings for all non-null paths,
     * or an empty list if {@code paths} is {@code null}
     * @see Path#toString()
     * @since 1.0
     */
    public static List<String> combinePathsToStrings(@Nullable Path... paths) {
        return combineAndMapVarargs(paths, p -> Objects.requireNonNull(p).toString());
    }

    /**
     * Combines multiple string collections into a single list of {@link File}
     * objects, ignoring any {@code null} collections or {@code null} elements.
     *
     * @param collections the collections of strings to combine; may be {@code null}
     * @return a new list of {@link File} objects for all non-null strings from all
     * non-null collections, or an empty list if {@code collections} is {@code null}
     * @see File#File(String)
     * @since 1.0
     */
    @SafeVarargs
    public static List<File> combineStringsToFiles(@Nullable Collection<String>... collections) {
        return combineAndMap(collections, s -> new File(Objects.requireNonNull(s)));
    }

    /**
     * Combines varargs string elements into a single list of {@link File} objects,
     * ignoring any {@code null} elements.
     *
     * @param strings the strings to combine; may be {@code null}, and individual elements
     *                may also be {@code null} (they are silently dropped)
     * @return a new list of {@link File} objects for all non-null strings,
     * or an empty list if {@code strings} is {@code null}
     * @see File#File(String)
     * @since 1.0
     */
    public static List<File> combineStringsToFiles(@Nullable String... strings) {
        return combineAndMapVarargs(strings, s -> new File(Objects.requireNonNull(s)));
    }

    /**
     * Combines multiple string collections into a single list of {@link Path}
     * objects, ignoring any {@code null} collections or {@code null} elements.
     *
     * @param collections the collections of strings to combine; may be {@code null}
     * @return a new list of {@link Path} objects for all non-null strings from all
     * non-null collections, or an empty list if {@code collections} is {@code null}
     * @see Path#of(String, String...)
     * @since 1.0
     */
    @SafeVarargs
    public static List<Path> combineStringsToPaths(@Nullable Collection<String>... collections) {
        return combineAndMap(collections, s -> Path.of(Objects.requireNonNull(s)));
    }

    /**
     * Combines varargs string elements into a single list of {@link Path} objects,
     * ignoring any {@code null} elements.
     *
     * @param strings the strings to combine; may be {@code null}, and individual elements
     *                may also be {@code null} (they are silently dropped)
     * @return a new list of {@link Path} objects for all non-null strings,
     * or an empty list if {@code strings} is {@code null}
     * @see Path#of(String, String...)
     * @since 1.0
     */
    public static List<Path> combineStringsToPaths(@Nullable String... strings) {
        return combineAndMapVarargs(strings, s -> Path.of(Objects.requireNonNull(s)));
    }
}