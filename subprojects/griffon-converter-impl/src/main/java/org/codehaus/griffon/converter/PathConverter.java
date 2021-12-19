/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2015-2021 the original author or authors.
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
import java.net.URI;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Andres Almiray
 * @since 3.0.0
 */
public class PathConverter extends AbstractConverter<Path> {
    @Nullable
    @Override
    public Path fromObject(@Nullable Object value) throws ConversionException {
        try {
            if (null == value) {
                return null;
            } else if (value instanceof CharSequence) {
                return convertFromString(String.valueOf(value).trim());
            } else if (value instanceof File) {
                return convertFromFile((File) value);
            } else if (value instanceof URI) {
                return convertFromURI((URI) value);
            } else if (value instanceof Path) {
                return (Path) value;
            }
        } catch (Exception e) {
            throw illegalValue(value, Path.class, e);
        }
        throw illegalValue(value, Path.class);
    }

    @Nullable
    protected Path convertFromFile(@Nonnull File value) {
        return value.toPath();
    }

    @Nullable
    protected Path convertFromURI(@Nonnull URI value) {
        return Paths.get(value);
    }

    @Nullable
    protected Path convertFromString(@Nonnull String str) {
        if (isBlank(str)) {
            return null;
        }
        try {
            return Paths.get(str);
        } catch (InvalidPathException e) {
            throw illegalValue(str, Path.class, e);
        }
    }
}
