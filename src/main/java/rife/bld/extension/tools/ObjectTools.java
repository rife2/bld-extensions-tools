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

/**
 * Object Tools.
 */
public final class ObjectTools {

    private ObjectTools() {
        // no-op
    }

    /**
     * Checks if any of the provided objects are {@code null}.
     *
     * @param objects the objects to check
     * @return {@code true} if any object is {@code null}, {@code false} otherwise
     * @since 1.0
     */
    @SafeVarargs
    public static <T> boolean isAnyNull(T... objects) {
        if (objects == null) {
            return true;
        }
        for (T obj : objects) {
            if (obj == null) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if any element in the collection is {@code null}.
     *
     * @param collection the collection to check
     * @return {@code true} if any element is {@code null}, {@code false} otherwise
     * @since 1.0
     */
    public static boolean isAnyNull(Collection<?> collection) {
        if (collection == null) {
            return true;
        }
        for (var obj : collection) {
            if (obj == null) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the provided array is empty or {@code null}.
     *
     * @param array The array to check; can be {@code null}
     * @return {@code true} if the array is {@code null} or empty; {@code false} otherwise
     * @since 1.0
     */
    @SuppressWarnings("PMD.UseVarargs")
    public static <T> boolean isEmpty(T[] array) {
        return array == null || array.length == 0;
    }

    /**
     * Checks if the provided {@link Map} is empty or {@code null}.
     *
     * @param map The map to check; can be {@code null}
     * @return {@code true} if the map is {@code null} or empty; {@code false} otherwise
     * @since 1.0
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    /**
     * Checks if all provided arrays are empty or {@code null}.
     *
     * @param arrays The arrays to check; can be {@code null} or contain {@code null} elements
     * @return {@code true} if all arrays are {@code null} or empty; {@code false} if any array is not empty
     * @since 1.0
     */
    @SafeVarargs
    public static <T> boolean isEmpty(T[]... arrays) {
        return arrays == null || Arrays.stream(arrays).allMatch(a -> a == null || a.length == 0);
    }

    /**
     * Checks if the provided collection is empty or {@code null}.
     *
     * @param collection The collection to check; can be {@code null}
     * @return {@code true} if the collection is {@code null} or empty; {@code false} otherwise
     * @since 1.0
     */
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * Checks if all provided collections are empty or {@code null}.
     *
     * @param collections The collections to check; can be {@code null} or contain {@code null} elements
     * @return {@code true} if all collections are {@code null} or empty; {@code false} if any collection is not empty
     * @since 1.0
     */
    @SafeVarargs
    public static <T extends Collection<?>> boolean isEmpty(T... collections) {
        return collections == null || Arrays.stream(collections).allMatch(c -> c == null || c.isEmpty());
    }

    /**
     * Checks if the provided array is not {@code null} and not empty.
     *
     * @param array The array to check; can be {@code null}
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
     * @param map The map to check; can be {@code null}
     * @return {@code true} if the map is not {@code null} and not empty; {@code false} otherwise
     * @since 1.0
     */
    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }

    /**
     * Checks if any of the provided arrays are not {@code null} and not empty.
     *
     * @param arrays The arrays to check; can be {@code null} or contain {@code null} elements
     * @return {@code true} if any array is not {@code null} and not empty; {@code false} if all are {@code null}
     * or empty
     * @since 1.0
     */
    @SafeVarargs
    public static <T> boolean isNotEmpty(T[]... arrays) {
        return !isEmpty(arrays);
    }

    /**
     * Checks if the provided collection is not {@code null} and not empty.
     *
     * @param collection The collection to check; can be {@code null}
     * @return {@code true} if the collection is not {@code null} and not empty; {@code false} otherwise
     * @since 1.0
     */
    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    /**
     * Checks if any of the provided collections are not {@code null} and not empty.
     *
     * @param collections The collections to check; can be {@code null} or contain {@code null} elements
     * @return {@code true} if any collection is not {@code null} and not empty; {@code false} if all are {@code null}
     * or empty
     * @since 1.0
     */
    @SafeVarargs
    public static <T extends Collection<?>> boolean isNotEmpty(T... collections) {
        return !isEmpty(collections);
    }

    /**
     * Checks if all provided objects are non-{@code null}.
     *
     * @param objects the objects to check
     * @return {@code true} if all objects are non-{@code null}, {@code false} otherwise
     * @since 1.0
     */
    @SafeVarargs
    public static <T> boolean isNotNull(T... objects) {
        if (objects == null) {
            return false;
        }
        for (T obj : objects) {
            if (obj == null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if all elements in the collection are non-{@code null}.
     *
     * @param collection the collection to check
     * @return {@code true} if all elements are non-{@code null}, {@code false} otherwise
     * @since 1.0
     */
    public static boolean isNotNull(Collection<?> collection) {
        if (collection == null) {
            return false;
        }
        for (var obj : collection) {
            if (obj == null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if all provided objects are {@code null}.
     *
     * @param objects the objects to check
     * @return {@code true} if all objects are {@code null}, {@code false} otherwise
     * @since 1.0
     */
    @SafeVarargs
    public static <T> boolean isNull(T... objects) {
        if (objects == null) {
            return true;
        }
        for (T obj : objects) {
            if (obj != null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if all elements in the collection are {@code null}.
     *
     * @param collection the collection to check
     * @return {@code true} if all elements are {@code null}, {@code false} otherwise
     * @since 1.0
     */
    public static boolean isNull(Collection<?> collection) {
        if (collection == null) {
            return true;
        }
        for (var obj : collection) {
            if (obj != null) {
                return false;
            }
        }
        return true;
    }
}