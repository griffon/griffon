/*
 * Copyright 2008-2016 the original author or authors.
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
package griffon.swing.editors;

import griffon.core.editors.AbstractPropertyEditor;
import griffon.metadata.PropertyEditorFor;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import static griffon.util.GriffonNameUtils.isBlank;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
@PropertyEditorFor(Icon.class)
public class IconPropertyEditor extends AbstractPropertyEditor {
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
        } else if (value instanceof ImageInputStream) {
            handleAsImageInputStream((ImageInputStream) value);
        } else if (value instanceof byte[]) {
            handleAsByteArray((byte[]) value);
        } else if (value instanceof Image) {
            handleAsImage((Image) value);
        } else if (value instanceof Icon) {
            super.setValueInternal(value);
        } else {
            throw illegalValue(value, Icon.class);
        }
    }

    protected void handleAsString(String str) {
        if (isBlank(str)) {
            super.setValueInternal(null);
            return;
        }
        if (str.contains("|")) {
            // assume classname|arg format
            handleAsClassWithArg(str);
        } else {
            handleAsURL(getClass().getClassLoader().getResource(str));
        }
    }

    @SuppressWarnings("unchecked")
    protected void handleAsClassWithArg(String str) {
        String[] args = str.split("\\|");
        if (args.length == 2) {
            Class<? extends Icon> iconClass = null;
            try {
                iconClass = (Class<? extends Icon>) IconPropertyEditor.class.getClassLoader().loadClass(args[0]);
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
                super.setValueInternal(constructor.newInstance(args[1]));
            } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
                throw illegalValue(str, Icon.class, e);
            }
        } else {
            throw illegalValue(str, Icon.class);
        }
    }

    protected void handleAsFile(File file) {
        try {
            handleAsImage(ImageIO.read(file));
        } catch (IOException e) {
            throw illegalValue(file, Icon.class, e);
        }
    }

    protected void handleAsURL(URL url) {
        try {
            handleAsImage(ImageIO.read(url));
        } catch (IOException e) {
            throw illegalValue(url, Icon.class, e);
        }
    }

    protected void handleAsURI(URI uri) {
        try {
            handleAsURL(uri.toURL());
        } catch (MalformedURLException e) {
            throw illegalValue(uri, Icon.class, e);
        }
    }

    protected void handleAsInputStream(InputStream stream) {
        try {
            handleAsImage(ImageIO.read(stream));
        } catch (IOException e) {
            throw illegalValue(stream, Icon.class, e);
        }
    }

    protected void handleAsImageInputStream(ImageInputStream stream) {
        try {
            handleAsImage(ImageIO.read(stream));
        } catch (IOException e) {
            throw illegalValue(stream, Icon.class, e);
        }
    }

    protected void handleAsByteArray(byte[] bytes) {
        super.setValueInternal(new ImageIcon(bytes));
    }

    protected void handleAsImage(Image img) {
        super.setValueInternal(new ImageIcon(img));
    }
}
