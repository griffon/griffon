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
package griffon.swing.converters;

import org.kordamp.jsr377.converter.AbstractConverter;

import javax.application.converter.ConversionException;
import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

/**
 * @author Andres Almiray
 * @since 3.0.0
 */
public class ImageConverter extends AbstractConverter<Image> {
    @Override
    public Image fromObject(Object value) throws ConversionException {
        if (null == value) {
            return null;
        } else if (value instanceof CharSequence) {
            return handleAsString(String.valueOf(value));
        } else if (value instanceof File) {
            return handleAsFile((File) value);
        } else if (value instanceof URL) {
            return handleAsURL((URL) value);
        } else if (value instanceof URI) {
            return handleAsURI((URI) value);
        } else if (value instanceof InputStream) {
            return handleAsInputStream((InputStream) value);
        } else if (value instanceof ImageInputStream) {
            return handleAsImageInputStream((ImageInputStream) value);
        } else if (value instanceof byte[]) {
            return handleAsByteArray((byte[]) value);
        } else if (value instanceof Image) {
            return (Image) value;
        } else {
            throw illegalValue(value, Image.class);
        }
    }

    protected Image handleAsString(String str) {
        if (isBlank(str)) {
            return null;
        }
        return handleAsURL(getClass().getClassLoader().getResource(str));
    }

    protected Image handleAsFile(File file) {
        try {
            return ImageIO.read(file);
        } catch (Exception e) {
            throw illegalValue(file, Image.class, e);
        }
    }

    protected Image handleAsURL(URL url) {
        try {
            return ImageIO.read(url);
        } catch (Exception e) {
            throw illegalValue(url, Image.class, e);
        }
    }

    protected Image handleAsURI(URI uri) {
        try {
            return handleAsURL(uri.toURL());
        } catch (Exception e) {
            throw illegalValue(uri, Image.class, e);
        }
    }

    protected Image handleAsInputStream(InputStream stream) {
        try {
            return ImageIO.read(stream);
        } catch (Exception e) {
            throw illegalValue(stream, Image.class, e);
        }
    }

    protected Image handleAsImageInputStream(ImageInputStream stream) {
        try {
            return ImageIO.read(stream);
        } catch (Exception e) {
            throw illegalValue(stream, Image.class, e);
        }
    }

    protected Image handleAsByteArray(byte[] bytes) {
        return Toolkit.getDefaultToolkit().createImage(bytes);
    }
}
