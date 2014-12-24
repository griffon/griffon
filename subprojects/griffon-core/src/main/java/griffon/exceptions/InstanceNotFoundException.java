/*
 * Copyright 2008-2015 the original author or authors.
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

    public InstanceNotFoundException(@Nonnull Class<?> type) {
        super("Could not find an instance of type " + checkNonNull(type, "type").getName());
    }

    public InstanceNotFoundException(@Nonnull Class<?> type, @Nonnull Throwable cause) {
        super("Could not find an instance of type " + checkNonNull(type, "type").getName(), checkNonNull(cause, "cause"));
    }

    public InstanceNotFoundException(@Nonnull Class<?> type, @Nonnull Annotation qualifier) {
        super("Could not find an instance of type " + checkNonNull(type, "type").getName() + " with qualifier " + checkNonNull(qualifier, "qualifier"));
    }

    public InstanceNotFoundException(@Nonnull Class<?> type, @Nonnull Annotation qualifier, @Nonnull Throwable cause) {
        super("Could not find an instance of type " + checkNonNull(type, "type").getName() + " with qualifier " + checkNonNull(qualifier, "qualifier"), checkNonNull(cause, "cause"));
    }
}
