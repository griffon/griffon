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
package org.codehaus.griffon.converter;

import griffon.annotations.core.Nonnull;
import griffon.annotations.core.Nullable;
import griffon.converter.ConversionException;

import java.io.File;
import java.nio.file.Path;

/**
 * @author Andres Almiray
 * @since 3.0.0
 */
public class FileConverter extends AbstractConverter<File> {
    @Nullable
    @Override
    public File fromObject(@Nullable Object value) throws ConversionException {
        try {
            if (null == value) {
                return null;
            } else if (value instanceof CharSequence) {
                return convertFromString(String.valueOf(value).trim());
            } else if (value instanceof File) {
                return (File) value;
            } else if (value instanceof Path) {
                return handeAsPath((Path) value);
            }
        } catch (Exception e) {
            throw illegalValue(value, File.class, e);

        }
        throw illegalValue(value, File.class);
    }

    @Nullable
    protected File handeAsPath(@Nonnull Path value) {
        return value.toFile();
    }

    @Nullable
    protected File convertFromString(@Nonnull String str) {
        if (isBlank(str)) {
            return null;
        }
        return new File(str);
    }
}
