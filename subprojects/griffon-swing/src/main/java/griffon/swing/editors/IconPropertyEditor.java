/*
 * Copyright 2010-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package griffon.swing.editors;

import griffon.core.resources.editors.AbstractPropertyEditor;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

/**
 * @author Andres Almiray
 * @since 1.1.0
 */
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

    private void handleAsString(String str) {
        handleAsURL(getClass().getClassLoader().getResource(str));
    }

    private void handleAsFile(File file) {
        try {
            handleAsImage(ImageIO.read(file));
        } catch (IOException e) {
            throw illegalValue(file, Icon.class);
        }
    }

    private void handleAsURL(URL url) {
        try {
            handleAsImage(ImageIO.read(url));
        } catch (IOException e) {
            throw illegalValue(url, Icon.class);
        }
    }

    private void handleAsURI(URI uri) {
        try {
            handleAsURL(uri.toURL());
        } catch (MalformedURLException e) {
            throw illegalValue(uri, Icon.class);
        }
    }

    private void handleAsInputStream(InputStream stream) {
        try {
            handleAsImage(ImageIO.read(stream));
        } catch (IOException e) {
            throw illegalValue(stream, Icon.class);
        }
    }

    private void handleAsImageInputStream(ImageInputStream stream) {
        try {
            handleAsImage(ImageIO.read(stream));
        } catch (IOException e) {
            throw illegalValue(stream, Icon.class);
        }
    }

    private void handleAsByteArray(byte[] bytes) {
        super.setValueInternal(new ImageIcon(bytes));
    }

    private void handleAsImage(Image img) {
        super.setValueInternal(new ImageIcon(img));
    }
}
