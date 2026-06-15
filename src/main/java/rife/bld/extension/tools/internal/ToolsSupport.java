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

package rife.bld.extension.tools.internal;

import rife.bld.extension.tools.TextTools;

import java.util.Objects;

/**
 * Internal support utilities shared across tool classes.
 *
 * <p>This class is not part of the public API and may change without notice.</p>
 *
 * @author <a href="https://erik.thauvin.net/">Erik C. Thauvin</a>
 * @since 1.0
 */
public final class ToolsSupport {

    private ToolsSupport() {
    }

    /**
     * Validates that a context string is not {@code null}, empty, or whitespace-only.
     *
     * @param context the context string to validate
     * @throws NullPointerException     if {@code context} is {@code null}
     * @throws IllegalArgumentException if {@code context} is empty, or whitespace-only
     */
    public static void requireContext(String context) {
        Objects.requireNonNull(context, "context must not be null");
        if (TextTools.isBlank(context)) {
            throw new IllegalArgumentException("context must not be blank");
        }
    }

    /**
     * Validates that a message string is not {@code null}, empty, or whitespace-only.
     *
     * @param message the message string to validate
     * @throws NullPointerException     if {@code message} is {@code null}
     * @throws IllegalArgumentException if {@code message} is empty, or whitespace-only
     */
    public static void requireMessage(String message) {
        Objects.requireNonNull(message, "message must not be null");
        if (TextTools.isBlank(message)) {
            throw new IllegalArgumentException("message must not be empty, or blank");
        }
    }
}