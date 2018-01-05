/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2018 the original author or authors.
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
public class InstanceNotFoundException extends GriffonException {
    private static final long serialVersionUID = 4999935012183988413L;
    private static final String COULD_NOT_FIND_AN_INSTANCE_OF_TYPE = "Could not find an instance of type ";
    private static final String TYPE = "type";
    private static final String CAUSE = "cause";
    private static final String WITH_QUALIFIER = " with qualifier ";
    private static final String QUALIFIER = "qualifier";

    public InstanceNotFoundException(@Nonnull Class<?> type) {
        super(COULD_NOT_FIND_AN_INSTANCE_OF_TYPE + checkNonNull(type, TYPE).getName());
    }

    public InstanceNotFoundException(@Nonnull Class<?> type, @Nonnull Throwable cause) {
        super(COULD_NOT_FIND_AN_INSTANCE_OF_TYPE + checkNonNull(type, TYPE).getName(), checkNonNull(cause, CAUSE));
    }

    public InstanceNotFoundException(@Nonnull Class<?> type, @Nonnull Annotation qualifier) {
        super(COULD_NOT_FIND_AN_INSTANCE_OF_TYPE + checkNonNull(type, TYPE).getName() + WITH_QUALIFIER + checkNonNull(qualifier, QUALIFIER));
    }

    public InstanceNotFoundException(@Nonnull Class<?> type, @Nonnull Annotation qualifier, @Nonnull Throwable cause) {
        super(COULD_NOT_FIND_AN_INSTANCE_OF_TYPE + checkNonNull(type, TYPE).getName() + WITH_QUALIFIER + checkNonNull(qualifier, QUALIFIER), checkNonNull(cause, CAUSE));
    }
}
