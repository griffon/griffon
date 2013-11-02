/*
 * Copyright 2010-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
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
public class ArtifactNotFoundException extends GriffonException {
    public ArtifactNotFoundException(@Nonnull Class<?> clazz, @Nonnull String type) {
        super(format(clazz, type));
    }

    public ArtifactNotFoundException(@Nonnull Class<?> clazz, @Nonnull String type, @Nonnull Throwable cause) {
        super(format(clazz, type), checkNonNull(cause, "cause"));
    }

    private static String format(Class<?> clazz, String type) {
        return "Could not find artifact with class " + checkNonNull(clazz, "clazz").getName() +" and type "+checkNonNull(type, "type");
    }
}
