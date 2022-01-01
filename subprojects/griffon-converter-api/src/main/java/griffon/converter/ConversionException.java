/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2022 the original author or authors.
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
package griffon.converter;

import griffon.annotations.core.Nonnull;
import griffon.annotations.core.Nullable;

/*
 * @author Andres Almiray
 * @since 3.0.0
 */
public class ConversionException extends RuntimeException {
    private static final long serialVersionUID = 2737009409197478002L;

    private final transient Object value;
    private Class<?> type;

    public ConversionException(@Nullable Object value) {
        this(value, (Exception) null);
    }

    public ConversionException(@Nullable Object value, @Nonnull Class<?> type) {
        this(value, type, null);
    }

    public ConversionException(@Nullable Object value, @Nonnull Class<?> type, @Nullable Exception cause) {
        super("Can't convert '" + value + "' into " + type.getName(), cause);
        this.value = value;
        this.type = type;
    }

    public ConversionException(@Nullable Object value, @Nullable Exception cause) {
        super("Can't convert '" + value + "'", cause);
        this.value = value;
        if (this.value != null) this.type = this.value.getClass();
    }

    @Nullable
    public Object getValue() {
        return value;
    }

    @Nullable
    public Class<?> getType() {
        return type;
    }
}
