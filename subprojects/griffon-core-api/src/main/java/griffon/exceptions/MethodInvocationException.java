/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package griffon.exceptions;

import griffon.annotations.core.Nonnull;
import griffon.annotations.core.Nullable;

import static griffon.util.StringUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public abstract class MethodInvocationException extends GriffonException {
    private static final long serialVersionUID = -4458948337239674524L;

    private static final String MESSAGE = "message";

    public MethodInvocationException(@Nonnull String message) {
        super(requireNonBlank(message, MESSAGE));
    }

    public MethodInvocationException(@Nonnull String message, @Nonnull Throwable cause) {
        super(requireNonBlank(message, MESSAGE), requireNonNull(cause));
    }

    @Nonnull
    protected static Class<?>[] convertToTypeArray(@Nullable Object[] args) {
        if (args == null) {
            return new Class<?>[0];
        }
        int s = args.length;
        Class<?>[] ans = new Class<?>[s];
        for (int i = 0; i < s; i++) {
            Object o = args[i];
            ans[i] = o != null ? o.getClass() : null;
        }
        return ans;
    }
}
