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
 * Text Tools.
 */
public final class TextTools {

    private TextTools() {
        // no-op
    }

    /**
     * Checks if a character sequence is {@code null}, empty, or contains only whitespace characters.
     *
     * @param str The character sequence to check
     * @return {@code true} if the character sequence is {@code null}, empty, or whitespace-only;
     * {@code false} otherwise.
     * @since 1.0
     */
    public static boolean isBlank(CharSequence str) {
        if (str == null) {
            return true;
        }
        // Optimize for String instances
        if (str instanceof String s) {
            return s.isBlank();
        }
        int len = str.length();
        if (len == 0) {
            return true;
        }
        for (int i = 0; i < len; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if all character sequences are {@code null}, empty, or contain only whitespace characters.
     *
     * @param strings The character sequences to check
     * @return {@code true} if all character sequences are {@code null}, empty, or whitespace-only;
     * {@code false} otherwise
     * @since 1.0
     */
    public static boolean isBlank(CharSequence... strings) {
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
     * Checks if all string objects are {@code null}, empty, or contain only whitespace characters.
     * <p>
     * If an object is not {@code null}, it will be converted to its string representation
     * for the check.
     *
     * @param strings The string objects to check
     * @return {@code true} if all string objects are {@code null}, their string representations are empty,
     * or their string representations are whitespace-only; {@code false} otherwise
     * @since 1.0
     */
    public static boolean isBlank(Object... strings) {
        if (strings == null) {
            return true;
        }
        for (var obj : strings) {
            if (obj == null) {
                continue;
            }
            // Avoid toString() call for CharSequence instances
            if (obj instanceof CharSequence cs) {
                if (!isBlank(cs)) {
                    return false;
                }
            } else if (!isBlank(obj.toString())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if all string objects are {@code null} or their string representations are empty.
     *
     * @param strings The string objects to check
     * @return {@code true} if all objects are {@code null} or their string representations are empty;
     * {@code false} otherwise
     * @since 1.0
     */
    public static boolean isEmpty(Object... strings) {
        if (strings == null) {
            return true;
        }
        for (var obj : strings) {
            if (obj == null) {
                continue;
            }
            // Avoid toString() call for CharSequence instances
            if (obj instanceof CharSequence cs) {
                if (!isEmpty(cs)) {
                    return false;
                }
            } else if (!isEmpty(obj.toString())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if a character sequence is {@code null} or empty.
     *
     * @param str The character sequence to check
     * @return {@code true} if the character sequence is {@code null} or empty; {@code false} otherwise
     * @since 1.0
     */
    public static boolean isEmpty(CharSequence str) {
        if (str == null) {
            return true;
        }
        // Optimize for String instances
        if (str instanceof String s) {
            return s.isEmpty();
        }
        return str.isEmpty();
    }

    /**
     * Checks if all character sequences are {@code null} or empty.
     *
     * @param strings The character sequences to check
     * @return {@code true} if all character sequences are {@code null} or empty; {@code false} otherwise
     * @since 1.0
     */
    public static boolean isEmpty(CharSequence... strings) {
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
     * Checks if all character sequences are not {@code null}, not empty, and not whitespace-only.
     *
     * @param strings The character sequences to check
     * @return {@code true} if all character sequences are not {@code null}, not empty, and not whitespace-only;
     * {@code false} otherwise
     * @since 1.0
     */
    public static boolean isNotBlank(CharSequence... strings) {
        if (strings == null || strings.length == 0) {
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
     * Checks if all string objects are not {@code null}, not empty, and not whitespace-only.
     * <p>
     * If an object is not {@code null}, it will be converted to its string representation
     * for the check.
     *
     * @param strings The string objects to check
     * @return {@code true} if all objects are not {@code null}, their string representations
     * are not empty, and their string representations are not whitespace-only;
     * {@code false} otherwise
     * @since 1.0
     */
    public static boolean isNotBlank(Object... strings) {
        if (strings == null || strings.length == 0) {
            return false;
        }
        for (var obj : strings) {
            if (obj == null) {
                return false;
            }
            // Avoid toString() call for CharSequence instances
            if (obj instanceof CharSequence cs) {
                if (isBlank(cs)) {
                    return false;
                }
            } else if (isBlank(obj.toString())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if a character sequence is not {@code null}, not empty, and not whitespace-only.
     *
     * @param str The character sequence to check
     * @return {@code true} if the character sequence is not {@code null}, not empty, and not whitespace-only;
     * {@code false} otherwise
     * @since 1.0
     */
    public static boolean isNotBlank(CharSequence str) {
        return !isBlank(str);
    }

    /**
     * Checks if all character sequences are not {@code null} and not empty.
     *
     * @param strings The character sequences to check
     * @return {@code true} if all character sequences are not {@code null} and not empty; {@code false} otherwise
     * @since 1.0
     */
    public static boolean isNotEmpty(CharSequence... strings) {
        if (strings == null || strings.length == 0) {
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
     * Checks if a character sequence is not {@code null} and not empty.
     *
     * @param str The character sequence to check
     * @return {@code true} if the character sequence is not {@code null} and not empty; {@code false} otherwise
     * @since 1.0
     */
    public static boolean isNotEmpty(CharSequence str) {
        return !isEmpty(str);
    }

    /**
     * Checks if all string objects are not {@code null} and their string representations are not empty.
     *
     * @param strings The string objects to check
     * @return {@code true} if all string objects are not {@code null} and their string representations are not empty;
     * {@code false} otherwise
     * @since 1.0
     */
    public static boolean isNotEmpty(Object... strings) {
        if (strings == null || strings.length == 0) {
            return false;
        }
        for (var obj : strings) {
            if (obj == null) {
                return false;
            }
            // Avoid toString() call for CharSequence instances
            if (obj instanceof CharSequence cs) {
                if (isEmpty(cs)) {
                    return false;
                }
            } else if (isEmpty(obj.toString())) {
                return false;
            }
        }
        return true;
    }
}