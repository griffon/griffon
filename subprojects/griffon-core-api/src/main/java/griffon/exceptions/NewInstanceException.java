/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2021 the original author or authors.
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

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class NewInstanceException extends GriffonException {
    private static final long serialVersionUID = -2311364344106926056L;

    public NewInstanceException(@Nonnull Class<?> klass) {
        super(format(klass));
    }

    public NewInstanceException(@Nonnull Class<?> klass, @Nonnull Throwable cause) {
        super(format(klass), requireNonNull(cause, "cause"));
    }

    private static String format(Class<?> klass) {
        requireNonNull(klass, "class");
        return "Cannot create a new instance of type " + klass.getName();
    }
}
