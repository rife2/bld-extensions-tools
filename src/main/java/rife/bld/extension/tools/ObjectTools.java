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

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * Object Tools.
 *
 * <p>Utility methods for null-checking and emptiness-checking objects, arrays,
 * collections, and maps.</p>
 *
 * <p><strong>Varargs semantics note:</strong> single-argument {@code isEmpty} overloads
 * return {@code true} if that one argument is null or empty. Multi-argument (varargs)
 * {@code isEmpty} overloads return {@code true} only if <em>all</em> arguments are null
 * or empty. Conversely, multi-argument {@code isNotEmpty} overloads return {@code true}
 * if <em>any</em> argument is not null and not empty.</p>
 *
 * <p>The same applies to {@code isNull}, {@code isAnyNull} and {@code isNotNull}.</p>
 *
 * <p>Callers should take care to use the correct overload for their intent.</p>
 *
 * @since 1.0
 */
public final class ObjectTools {

    private ObjectTools() {
        // no-op
    }

    /**
     * Checks if any of the provided objects are {@code null}.
     *
     * <p>Returns {@code true} if the varargs array itself is {@code null}.</p>
     *
     * @param objects the objects to check
     * @return {@code true} if any object is {@code null}; {@code false} otherwise
     * @since 1.0
     */
    @SafeVarargs
    public static <T> boolean isAnyNull(T... objects) {
        return objects == null || Arrays.stream(objects).anyMatch(Objects::isNull);
    }

    /**
     * Checks if any element in the collection is {@code null}.
     *
     * <p>Returns {@code true} if the collection itself is {@code null}.</p>
     *
     * @param collection the collection to check
     * @return {@code true} if any element is {@code null}; {@code false} otherwise
     * @since 1.0
     */
    public static boolean isAnyNull(Collection<?> collection) {
        return collection == null || collection.stream().anyMatch(Objects::isNull);
    }

    /**
     * Checks if the provided array is empty or {@code null}.
     *
     * <p>Note: this overload takes a plain array parameter. PMD's UseVarargs warning
     * is suppressed intentionally as converting to varargs would create an ambiguous
     * overload conflict with {@link #isEmpty(Object[]...)}.</p>
     *
     * @param array the array to check; can be {@code null}
     * @return {@code true} if the array is {@code null} or empty; {@code false} otherwise
     * @since 1.0
     */
    @SuppressWarnings({"PMD.UseVarargs"})
    public static <T> boolean isEmpty(T[] array) {
        return array == null || array.length == 0;
    }

    /**
     * Checks if the provided {@link Map} is empty or {@code null}.
     *
     * @param map the map to check; can be {@code null}
     * @return {@code true} if the map is {@code null} or empty; {@code false} otherwise
     * @since 1.0
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    /**
     * Checks if all provided arrays are empty or {@code null}.
     *
     * <p>Returns {@code true} if the varargs array itself is {@code null} or if every
     * individual array is {@code null} or empty. Returns {@code false} as soon as any
     * array contains at least one element. See the class-level note on varargs semantics.</p>
     *
     * @param arrays the arrays to check; can be {@code null} or contain {@code null} elements
     * @return {@code true} if all arrays are {@code null} or empty;
     * {@code false} if any array is not empty
     * @since 1.0
     */
    @SafeVarargs
    public static <T> boolean isEmpty(T[]... arrays) {
        return arrays == null || Arrays.stream(arrays).allMatch(a -> a == null || a.length == 0);
    }

    /**
     * Checks if the provided collection is empty or {@code null}.
     *
     * @param collection the collection to check; can be {@code null}
     * @return {@code true} if the collection is {@code null} or empty; {@code false} otherwise
     * @since 1.0
     */
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * Checks if all provided collections are empty or {@code null}.
     *
     * <p>Returns {@code true} if the varargs array itself is {@code null} or if every
     * individual collection is {@code null} or empty. Returns {@code false} as soon as
     * any collection contains at least one element. See the class-level note on varargs
     * semantics.</p>
     *
     * @param collections the collections to check; can be {@code null} or contain {@code null} elements
     * @return {@code true} if all collections are {@code null} or empty;
     * {@code false} if any collection is not empty
     * @since 1.0
     */
    @SafeVarargs
    public static <T extends Collection<?>> boolean isEmpty(T... collections) {
        return collections == null || Arrays.stream(collections).allMatch(c -> c == null || c.isEmpty());
    }

    /**
     * Checks if the provided array is not {@code null} and not empty.
     *
     * <p>Note: this overload takes a plain array parameter. PMD's UseVarargs warning
     * is suppressed intentionally as converting to varargs would create an ambiguous
     * overload conflict with {@link #isNotEmpty(Object[]...)}.</p>
     *
     * @param array the array to check; can be {@code null}
     * @return {@code true} if the array is not {@code null} and not empty; {@code false} otherwise
     * @since 1.0
     */
    @SuppressWarnings("PMD.UseVarargs")
    public static <T> boolean isNotEmpty(T[] array) {
        return !isEmpty(array);
    }

    /**
     * Checks if the provided {@link Map} is not {@code null} and not empty.
     *
     * @param map the map to check; can be {@code null}
     * @return {@code true} if the map is not {@code null} and not empty; {@code false} otherwise
     * @since 1.0
     */
    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }

    /**
     * Checks if any of the provided arrays are not {@code null} and not empty.
     *
     * <p>Logical negation of {@link #isEmpty(Object[]...)}. Returns {@code true} as soon
     * as any array contains at least one element. See the class-level note on varargs
     * semantics.</p>
     *
     * @param arrays the arrays to check; can be {@code null} or contain {@code null} elements
     * @return {@code true} if any array is not {@code null} and not empty;
     * {@code false} if all are {@code null} or empty
     * @since 1.0
     */
    @SafeVarargs
    public static <T> boolean isNotEmpty(T[]... arrays) {
        return !isEmpty(arrays);
    }

    /**
     * Checks if the provided collection is not {@code null} and not empty.
     *
     * @param collection the collection to check; can be {@code null}
     * @return {@code true} if the collection is not {@code null} and not empty; {@code false} otherwise
     * @since 1.0
     */
    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    /**
     * Checks if any of the provided collections are not {@code null} and not empty.
     *
     * <p>Logical negation of {@link #isEmpty(Collection[])}. Returns {@code true} as soon
     * as any collection contains at least one element. See the class-level note on varargs
     * semantics.</p>
     *
     * @param collections the collections to check; can be {@code null} or contain {@code null} elements
     * @return {@code true} if any collection is not {@code null} and not empty;
     * {@code false} if all are {@code null} or empty
     * @since 1.0
     */
    @SafeVarargs
    public static <T extends Collection<?>> boolean isNotEmpty(T... collections) {
        return !isEmpty(collections);
    }

    /**
     * Checks if all provided objects are non-{@code null}.
     *
     * <p>Returns {@code false} if the varargs array itself is {@code null}.</p>
     *
     * <p>Returns {@code true} for an empty argument list (vacuously true).</p>
     *
     * @param objects the objects to check
     * @return {@code true} if all objects are non-{@code null}; {@code false} otherwise
     * @since 1.0
     */
    @SafeVarargs
    public static <T> boolean isNotNull(T... objects) {
        return objects != null && Arrays.stream(objects).noneMatch(Objects::isNull);
    }

    /**
     * Checks if all elements in the collection are non-{@code null}.
     *
     * <p>Returns {@code false} if the collection itself is {@code null}.</p>
     *
     * @param collection the collection to check
     * @return {@code true} if all elements are non-{@code null}; {@code false} otherwise
     * @since 1.0
     */
    public static boolean isNotNull(Collection<?> collection) {
        return collection != null && collection.stream().noneMatch(Objects::isNull);
    }

    /**
     * Checks if all provided objects are {@code null}.
     *
     * <p>Returns {@code true} if the varargs array itself is {@code null}.</p>
     *
     * <p>Returns {@code true} for an empty argument list (vacuously true).</p>
     *
     * @param objects the objects to check
     * @return {@code true} if all objects are {@code null}; {@code false} otherwise
     * @since 1.0
     */
    @SafeVarargs
    public static <T> boolean isNull(T... objects) {
        return objects == null || Arrays.stream(objects).allMatch(Objects::isNull);
    }

    /**
     * Checks if all elements in the collection are {@code null}.
     *
     * <p>Returns {@code true} if the collection itself is {@code null}.</p>
     *
     * @param collection the collection to check
     * @return {@code true} if all elements are {@code null}; {@code false} otherwise
     * @since 1.0
     */
    public static boolean isNull(Collection<?> collection) {
        return collection == null || collection.stream().allMatch(Objects::isNull);
    }
}