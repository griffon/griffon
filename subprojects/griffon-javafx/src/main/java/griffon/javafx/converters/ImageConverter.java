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
package griffon.javafx.converters;

import griffon.converter.ConversionException;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.codehaus.griffon.converter.AbstractConverter;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

/**
 * @author Andres Almiray
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
        } else if (value instanceof Image) {
            return (Image) value;
        } else {
            throw illegalValue(value, Image.class);
        }
    }

    protected Image handleAsString(String str) {
        if (isBlank(str)) {
            return null;
        } else if (str.contains("|")) {
            return handleAsClassWithArg(str);
        } else {
            return handleAsURL(getClass().getClassLoader().getResource(str));
        }
    }

    protected Image handleAsFile(File file) {
        try {
            return handleAsURL(file.toURI().toURL());
        } catch (MalformedURLException e) {
            throw illegalValue(file, URL.class, e);
        }
    }

    protected Image handleAsURL(URL url) {
        try {
            return new Image(url.toString());
        } catch (Exception e) {
            throw illegalValue(url, URL.class, e);
        }
    }

    protected Image handleAsURI(URI uri) {
        try {
            return handleAsURL(uri.toURL());
        } catch (MalformedURLException e) {
            throw illegalValue(uri, URL.class, e);
        }
    }

    protected Image handleAsInputStream(InputStream stream) {
        try {
            return new Image(stream);
        } catch (Exception e) {
            throw illegalValue(stream, URL.class, e);
        }
    }

    protected Image handleAsClassWithArg(String str) {
        String[] args = str.split("\\|");
        if (args.length == 2) {
            Class<?> iconClass = null;
            try {
                iconClass = ImageConverter.class.getClassLoader().loadClass(args[0]);
            } catch (ClassNotFoundException e) {
                throw illegalValue(str, Image.class, e);
            }

            Constructor<?> constructor = null;
            try {
                constructor = iconClass.getConstructor(String.class);
            } catch (NoSuchMethodException e) {
                throw illegalValue(str, Image.class, e);
            }

            try {
                Object o = constructor.newInstance(args[1]);
                if (o instanceof Image) {
                    return (Image) o;
                } else if (o instanceof ImageView) {
                    return ((ImageView) o).getImage();
                } else {
                    throw illegalValue(str, Image.class);
                }
            } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
                throw illegalValue(str, Image.class, e);
            }
        } else {
            throw illegalValue(str, Image.class);
        }
    }
}
