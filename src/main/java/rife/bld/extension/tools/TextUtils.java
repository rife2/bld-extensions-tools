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

/**
 * Text Utilities and Tools.
 */
public final class TextUtils {

    private TextUtils() {
        // no-op
    }

    /**
     * Checks if a string is {@code null}, empty, or contains only whitespace characters.
     *
     * @param str The string to check
     * @return {@code true} if the string is {@code null}, empty, or whitespace-only;
     * {@code false} otherwise.
     * @since 1.0
     */
    public static boolean isBlank(String str) {
        if (str == null || str.isEmpty()) {
            return true;
        }
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if all strings are {@code null}, empty, or contain only whitespace characters.
     *
     * @param strings The strings to check
     * @return {@code true} if all strings are {@code null}, empty, or whitespace-only;
     * {@code false} otherwise
     * @since 1.0
     */
    public static boolean isBlank(String... strings) {
        if (strings == null) {
            return true;
        }
        for (var str : strings) {
            if (!isBlank(str)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if all objects are {@code null}, empty, or contain only whitespace characters.
     * <p>
     * If an object is not {@code null}, it will be converted to its string representation
     * for the check.
     *
     * @param objects The objects to check
     * @return {@code true} if all objects are {@code null}, their string representations are empty,
     * or their string representations are whitespace-only; {@code false} otherwise
     * @since 1.0
     */
    public static boolean isBlank(Object... objects) {
        if (objects == null) {
            return true;
        }
        for (var obj : objects) {
            if (obj != null && !isBlank(obj.toString())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if all objects are {@code null} or their string representations are empty.
     *
     * @param objects The objects to check
     * @return {@code true} if all objects are {@code null} or their string representations are empty;
     * {@code false} otherwise
     * @since 1.0
     */
    public static boolean isEmpty(Object... objects) {
        if (objects == null) {
            return true;
        }
        for (var obj : objects) {
            if (obj != null && !isEmpty(obj.toString())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if a string is {@code null} or empty.
     *
     * @param str The string to check
     * @return {@code true} if the string is {@code null} or empty; {@code false} otherwise
     * @since 1.0
     */
    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    /**
     * Checks if all strings are {@code null} or empty.
     *
     * @param strings The strings to check
     * @return {@code true} if all strings are {@code null} or empty; {@code false} otherwise
     * @since 1.0
     */
    public static boolean isEmpty(String... strings) {
        if (strings == null) {
            return true;
        }
        for (var str : strings) {
            if (!isEmpty(str)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if all strings are not {@code null}, not empty, and not whitespace-only.
     *
     * @param strings The strings to check
     * @return {@code true} if all strings are not {@code null}, not empty, and not whitespace-only;
     * {@code false} otherwise
     * @since 1.0
     */
    public static boolean isNotBlank(String... strings) {
        if (strings == null) {
            return false;
        }
        for (var str : strings) {
            if (isBlank(str)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if all objects are not {@code null}, not empty, and not whitespace-only.
     * <p>
     * If an object is not {@code null}, it will be converted to its string representation
     * for the check.
     *
     * @param objects The objects to check
     * @return {@code true} if all objects are not {@code null}, their string representations
     * are not empty, and their string representations are not whitespace-only;
     * {@code false} otherwise
     * @since 1.0
     */
    public static boolean isNotBlank(Object... objects) {
        if (objects == null) {
            return false;
        }
        for (var obj : objects) {
            if (obj == null || isBlank(obj.toString())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if a string is not {@code null}, not empty, and not whitespace-only.
     *
     * @param str The string to check
     * @return {@code true} if the string is not {@code null}, not empty, and not whitespace-only;
     * {@code false} otherwise
     * @since 1.0
     */
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    /**
     * Checks if all strings are not {@code null} and not empty.
     *
     * @param strings The strings to check
     * @return {@code true} if all strings are not {@code null} and not empty; {@code false} otherwise
     * @since 1.0
     */
    public static boolean isNotEmpty(String... strings) {
        if (strings == null) {
            return false;
        }
        for (var str : strings) {
            if (isEmpty(str)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if a string is not {@code null} and not empty.
     *
     * @param str The string to check
     * @return {@code true} if the string is not {@code null} and not empty; {@code false} otherwise
     * @since 1.0
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * Checks if all objects are not {@code null} and their string representations are not empty.
     *
     * @param objects The objects to check
     * @return {@code true} if all objects are not {@code null} and their string representations are not empty;
     * {@code false} otherwise
     * @since 1.0
     */
    public static boolean isNotEmpty(Object... objects) {
        if (objects == null) {
            return false;
        }
        for (var obj : objects) {
            if (obj == null || isEmpty(obj.toString())) {
                return false;
            }
        }
        return true;
    }
}