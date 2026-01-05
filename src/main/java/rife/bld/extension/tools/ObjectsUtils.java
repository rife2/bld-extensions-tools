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

/**
 * Objects Utilities and Tools.
 */
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