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
package griffon.javafx.editors;

import griffon.core.editors.AbstractPropertyEditor;
import griffon.metadata.PropertyEditorFor;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import static griffon.util.GriffonNameUtils.isBlank;

/**
 * @author Andres Almiray
 */
@PropertyEditorFor(Image.class)
public class ImagePropertyEditor extends AbstractPropertyEditor {
    @Override
    protected void setValueInternal(Object value) {
        if (null == value) {
            super.setValueInternal(null);
        } else if (value instanceof CharSequence) {
            handleAsString(String.valueOf(value));
        } else if (value instanceof File) {
            handleAsFile((File) value);
        } else if (value instanceof URL) {
            handleAsURL((URL) value);
        } else if (value instanceof URI) {
            handleAsURI((URI) value);
        } else if (value instanceof InputStream) {
            handleAsInputStream((InputStream) value);
        } else if (value instanceof Image) {
            super.setValueInternal(value);
        } else {
            throw illegalValue(value, Image.class);
        }
    }

    protected void handleAsString(String str) {
        if (isBlank(str)) {
            super.setValueInternal(null);
        } else if (str.contains("|")) {
            handleAsClassWithArg(str);
        } else {
            handleAsURL(getClass().getClassLoader().getResource(str));
        }
    }

    protected void handleAsFile(File file) {
        try {
            handleAsURL(file.toURI().toURL());
        } catch (MalformedURLException e) {
            throw illegalValue(file, URL.class, e);
        }
    }

    protected void handleAsURL(URL url) {
        try {
            super.setValueInternal(new Image(url.toString()));
        } catch (Exception e) {
            throw illegalValue(url, URL.class, e);
        }
    }

    protected void handleAsURI(URI uri) {
        try {
            handleAsURL(uri.toURL());
        } catch (MalformedURLException e) {
            throw illegalValue(uri, URL.class, e);
        }
    }

    protected void handleAsInputStream(InputStream stream) {
        try {
            super.setValueInternal(new Image(stream));
        } catch (Exception e) {
            throw illegalValue(stream, URL.class, e);
        }
    }

    protected void handleAsClassWithArg(String str) {
        String[] args = str.split("\\|");
        if (args.length == 2) {
            Class<?> iconClass = null;
            try {
                iconClass = ImagePropertyEditor.class.getClassLoader().loadClass(args[0]);
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
                    super.setValueInternal(o);
                } else if (o instanceof ImageView) {
                    super.setValueInternal(((ImageView) o).getImage());
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
