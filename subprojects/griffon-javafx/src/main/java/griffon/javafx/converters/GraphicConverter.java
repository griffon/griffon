/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2023 the original author or authors.
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
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.codehaus.griffon.converter.AbstractConverter;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Andres Almiray
 * @since 3.0.0
 */
public class GraphicConverter extends AbstractConverter<Node> {
    protected final ImageConverter imageConverter = new ImageConverter();

    @Override
    public Node fromObject(Object value) throws ConversionException {
        if (null == value) {
            return null;
        } else if (value instanceof CharSequence) {
            return handleAsString(String.valueOf(value));
        } else {
            return handleWithImageConverter(value);
        }
    }

    protected Node handleAsString(String str) {
        if (isBlank(str)) {
            return null;
        } else if (str.contains("|")) {
            return handleAsClassWithArg(str);
        } else {
            return handleWithImageConverter(str);
        }
    }

    protected Node handleWithImageConverter(Object value) {
        try {
            Image image = imageConverter.fromObject(value);
            if (image != null) {
                return new ImageView(image);
            } else {
                return null;
            }
        } catch (IllegalArgumentException iae) {
            throw illegalValue(value, Node.class, iae);
        }
    }

    protected Node handleAsClassWithArg(String str) {
        String[] args = str.split("\\|");
        if (args.length == 2) {
            Class<?> iconClass = null;
            try {
                iconClass = GraphicConverter.class.getClassLoader().loadClass(args[0]);
            } catch (ClassNotFoundException e) {
                throw illegalValue(str, Node.class, e);
            }

            Constructor<?> constructor = null;
            try {
                constructor = iconClass.getConstructor(String.class);
            } catch (NoSuchMethodException e) {
                throw illegalValue(str, Node.class, e);
            }

            try {
                Object o = constructor.newInstance(args[1]);
                if (o instanceof Node) {
                    return (Node) o;
                } else if (o instanceof Image) {
                    return new ImageView((Image) o);
                } else {
                    throw illegalValue(str, Node.class);
                }
            } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
                throw illegalValue(str, Node.class, e);
            }
        } else {
            throw illegalValue(str, Node.class);
        }
    }
}
