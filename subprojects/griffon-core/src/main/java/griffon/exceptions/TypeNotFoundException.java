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

import javax.annotation.Nonnull;
import java.lang.annotation.Annotation;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class TypeNotFoundException extends GriffonException {
    private static final long serialVersionUID = -1200238376357824754L;
    private static final String TYPE = "type";
    private static final String CAUSE = "cause";

    public TypeNotFoundException(@Nonnull String type) {
        super("Could not find type " + checkNonBlank(type, TYPE));
    }

    public TypeNotFoundException(@Nonnull String type, @Nonnull Throwable cause) {
        super("Could not find type " + checkNonBlank(type, TYPE), checkNonNull(cause, CAUSE));
    }

    public TypeNotFoundException(@Nonnull String type, @Nonnull Annotation qualifier, @Nonnull Throwable cause) {
        super("Could not find type " + checkNonBlank(type, TYPE) + " with qualifier " + checkNonNull(qualifier, "qualifier"), checkNonNull(cause, CAUSE));
    }
}
