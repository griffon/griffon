/*
 * Copyright 2008-2014 the original author or authors.
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

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class TypeConversionException extends GriffonException {
    private static final long serialVersionUID = 1157963177529544281L;

    public TypeConversionException(@Nonnull Object value, @Nonnull Class<?> targetType) {
        super(formatArgs(value, targetType));
    }

    public TypeConversionException(@Nonnull Object value, @Nonnull Class<?> targetType, @Nonnull Throwable cause) {
        super(formatArgs(value, targetType), cause);
    }

    private static String formatArgs(@Nonnull Object value, @Nonnull Class<?> targetType) {
        checkNonNull(value, "value");
        checkNonNull(targetType, "targetType");
        return "Cannot convert an instance of " + value.getClass() + " into " + targetType + ". " + value;
    }
}
