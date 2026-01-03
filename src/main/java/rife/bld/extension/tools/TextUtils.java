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
     * Checks if an object is {@code null}, empty, or contains only whitespace characters.
     * <p>
     * If the object is not {@code null}, it will be converted to its string representation
     * for the check.
     *
     * @param obj The object to check
     * @return {@code true} if the object is {@code null}, its string representation is empty,
     * or its string representation is whitespace-only; {@code false} otherwise
     * @since 1.0
     */
    public static boolean isBlank(Object obj) {
        return obj == null || isBlank(obj.toString());
    }

    /**
     * Checks if an object is {@code null} or its string representation is empty.
     *
     * @param obj The object to check
     * @return {@code true} if the object is {@code null} or its string representation is empty; {@code false} otherwise
     * @since 1.0
     */
    public static boolean isEmpty(Object obj) {
        return obj == null || isEmpty(obj.toString());
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
     * Checks if an object is not {@code null}, not empty, and not whitespace-only.
     * <p>
     * If the object is not {@code null}, it will be converted to its string representation
     * for the check.
     *
     * @param obj The object to check
     * @return {@code true} if the object is not {@code null}, its string representation
     * is not empty, and its string representation is not whitespace-only;
     * {@code false} otherwise
     * @since 1.0
     */
    public static boolean isNotBlank(Object obj) {
        return !isBlank(obj);
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
     * Checks if an object is not {@code null} and its string representation is not empty.
     *
     * @param obj The object to check
     * @return {@code true} if the object is not {@code null} and its string representation is not empty;
     * {@code false} otherwise
     * @since 1.0
     */
    public static boolean isNotEmpty(Object obj) {
        return !isEmpty(obj);
    }
}