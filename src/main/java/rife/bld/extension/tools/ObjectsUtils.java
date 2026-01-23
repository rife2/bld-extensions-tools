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

import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;

/**
 * Objects Utilities and Tools.
 */
@SuppressWarnings({"PMD.LooseCoupling", "PMD.ReplaceHashtableWithMap"})
public final class ObjectsUtils {

    private ObjectsUtils() {
        // no-op
    }

    /**
     * Checks if any of the provided objects are {@code null}.
     *
     * @param objects the objects to check
     * @return {@code true} if any object is {@code null}, {@code false} otherwise
     * @since 1.0
     */
    public static boolean isAnyNull(Object... objects) {
        if (objects == null) {
            return true;
        }
        for (var obj : objects) {
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
    public static boolean isEmpty(Object[] array) {
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
     * Checks if the provided {@link Hashtable} is empty or {@code null}.
     *
     * @param table The hashtable to check; can be {@code null}
     * @return {@code true} if the hashtable is {@code null} or empty; {@code false} otherwise
     * @since 1.0
     */
    public static boolean isEmpty(Hashtable<?, ?> table) {
        return table == null || table.isEmpty();
    }

    /**
     * Checks if all provided arrays are empty or {@code null}.
     *
     * @param arrays The arrays to check; can be {@code null} or contain {@code null} elements
     * @return {@code true} if all arrays are {@code null} or empty; {@code false} if any array is not empty
     * @since 1.0
     */
    public static boolean isEmpty(Object[]... arrays) {
        if (arrays == null) {
            return true;
        }
        for (Object[] array : arrays) {
            if (array != null && array.length > 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the provided array is not {@code null} and not empty.
     *
     * @param array The array to check; can be {@code null}
     * @return {@code true} if the array is not {@code null} and not empty; {@code false} otherwise
     * @since 1.0
     */
    @SuppressWarnings("PMD.UseVarargs")
    public static boolean isNotEmpty(Object[] array) {
        return array != null && array.length > 0;
    }

    /**
     * Checks if the provided {@link Map} is not {@code null} and not empty.
     *
     * @param map The map to check; can be {@code null}
     * @return {@code true} if the map is not {@code null} and not empty; {@code false} otherwise
     * @since 1.0
     */
    public static boolean isNotEmpty(Map<?, ?> map) {
        return map != null && !map.isEmpty();
    }

    /**
     * Checks if the provided {@link Hashtable} is not {@code null} and not empty.
     *
     * @param table The hashtable to check; can be {@code null}
     * @return {@code true} if the hashtable is not {@code null} and not empty; {@code false} otherwise
     * @since 1.0
     */
    public static boolean isNotEmpty(Hashtable<?, ?> table) {
        return table != null && !table.isEmpty();
    }

    /**
     * Checks if any of the provided arrays are not {@code null} and not empty.
     *
     * @param arrays The arrays to check; can be {@code null} or contain {@code null} elements
     * @return {@code true} if any array is not {@code null} and not empty; {@code false} if all are {@code null} or empty
     * @since 1.0
     */
    public static boolean isNotEmpty(Object[]... arrays) {
        if (arrays == null) {
            return false;
        }
        for (Object[] array : arrays) {
            if (array != null && array.length > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if all provided objects are non-{@code null}.
     *
     * @param objects the objects to check
     * @return {@code true} if all objects are non-{@code null}, {@code false} otherwise
     * @since 1.0
     */
    public static boolean isNotNull(Object... objects) {
        if (objects == null) {
            return false;
        }
        for (var obj : objects) {
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
    public static boolean isNull(Object... objects) {
        if (objects == null) {
            return true;
        }
        for (var obj : objects) {
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