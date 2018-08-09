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
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

/**
 * @author Andres Almiray
 * @since 3.0.0
 */
public class BufferedImageConverter extends AbstractConverter<BufferedImage> {
    @Override
    public BufferedImage fromObject(Object value) throws ConversionException {
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
        } else if (value instanceof BufferedImage) {
            return (BufferedImage) value;
        } else {
            throw illegalValue(value, BufferedImage.class);
        }
    }

    protected BufferedImage handleAsString(String str) {
        if (isBlank(str)) {
            return null;
        } else {
            return handleAsURL(getClass().getClassLoader().getResource(str));
        }
    }

    protected BufferedImage handleAsFile(File file) {
        try {
            return ImageIO.read(file);
        } catch (Exception e) {
            throw illegalValue(file, BufferedImage.class, e);
        }
    }

    protected BufferedImage handleAsURL(URL url) {
        try {
            return ImageIO.read(url);
        } catch (Exception e) {
            throw illegalValue(url, BufferedImage.class, e);
        }
    }

    protected BufferedImage handleAsURI(URI uri) {
        try {
            return handleAsURL(uri.toURL());
        } catch (Exception e) {
            throw illegalValue(uri, BufferedImage.class, e);
        }
    }

    protected BufferedImage handleAsInputStream(InputStream stream) {
        try {
            return ImageIO.read(stream);
        } catch (Exception e) {
            throw illegalValue(stream, BufferedImage.class, e);
        }
    }

    protected BufferedImage handleAsImageInputStream(ImageInputStream stream) {
        try {
            return ImageIO.read(stream);
        } catch (Exception e) {
            throw illegalValue(stream, BufferedImage.class, e);
        }
    }
}
