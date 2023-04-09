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

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class MembersInjectionException extends GriffonException {
    private static final long serialVersionUID = 5180926740378298528L;

    public MembersInjectionException(@Nonnull Object instance, @Nonnull Throwable cause) {
        super("An error occurred while injecting members into " + checkNonNull(instance, "instance"), checkNonNull(cause, "cause"));
    }
}
