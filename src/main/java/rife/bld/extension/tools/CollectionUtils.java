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
 * Collection Utilities and Tools.
 */
public final class CollectionUtils {
    private CollectionUtils() {
        // no-op
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
     * Checks if the provided collection is not {@code null} and not empty.
     *
     * @param collection The collection to check; can be {@code null}
     * @return {@code true} if the collection is not {@code null} and not empty; {@code false} otherwise
     * @since 1.0
     */
    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }
}