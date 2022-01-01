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
package griffon.swing.converters;

import griffon.converter.ConversionException;
import org.codehaus.griffon.converter.AbstractConverter;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URL;

/**
 * @author Andres Almiray
 * @since 3.0.0
 */
public class IconConverter extends AbstractConverter<Icon> {
    @Override
    public Icon fromObject(Object value) throws ConversionException {
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
            return handleAsImage((Image) value);
        } else if (value instanceof Icon) {
            return (Icon) value;
        } else {
            throw illegalValue(value, Icon.class);
        }
    }

    protected Icon handleAsString(String str) {
        if (isBlank(str)) {
            return null;
        }
        if (str.contains("|")) {
            // assume classname|arg format
            return handleAsClassWithArg(str);
        } else {
            return handleAsURL(getClass().getClassLoader().getResource(str));
        }
    }

    @SuppressWarnings("unchecked")
    protected Icon handleAsClassWithArg(String str) {
        String[] args = str.split("\\|");
        if (args.length == 2) {
            Class<? extends Icon> iconClass = null;
            try {
                iconClass = (Class<? extends Icon>) IconConverter.class.getClassLoader().loadClass(args[0]);
            } catch (ClassNotFoundException e) {
                throw illegalValue(str, Icon.class, e);
            }

            Constructor<? extends Icon> constructor = null;
            try {
                constructor = iconClass.getConstructor(String.class);
            } catch (NoSuchMethodException e) {
                throw illegalValue(str, Icon.class, e);
            }

            try {
                return constructor.newInstance(args[1]);
            } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
                throw illegalValue(str, Icon.class, e);
            }
        } else {
            throw illegalValue(str, Icon.class);
        }
    }

    protected Icon handleAsFile(File file) {
        try {
            return handleAsImage(ImageIO.read(file));
        } catch (Exception e) {
            throw illegalValue(file, Icon.class, e);
        }
    }

    protected Icon handleAsURL(URL url) {
        try {
            return handleAsImage(ImageIO.read(url));
        } catch (Exception e) {
            throw illegalValue(url, Icon.class, e);
        }
    }

    protected Icon handleAsURI(URI uri) {
        try {
            return handleAsURL(uri.toURL());
        } catch (Exception e) {
            throw illegalValue(uri, Icon.class, e);
        }
    }

    protected Icon handleAsInputStream(InputStream stream) {
        try {
            return handleAsImage(ImageIO.read(stream));
        } catch (Exception e) {
            throw illegalValue(stream, Icon.class, e);
        }
    }

    protected Icon handleAsImageInputStream(ImageInputStream stream) {
        try {
            return handleAsImage(ImageIO.read(stream));
        } catch (Exception e) {
            throw illegalValue(stream, Icon.class, e);
        }
    }

    protected Icon handleAsByteArray(byte[] bytes) {
        return new ImageIcon(bytes);
    }

    protected Icon handleAsImage(Image img) {
        return new ImageIcon(img);
    }
}
