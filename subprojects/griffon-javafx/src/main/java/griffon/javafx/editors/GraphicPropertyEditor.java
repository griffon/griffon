/*
 * Copyright 2008-2015 the original author or authors.
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
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static griffon.util.GriffonNameUtils.isBlank;

/**
 * @author Andres Almiray
 * @since 2.3.0
 */
@PropertyEditorFor(Node.class)
public class GraphicPropertyEditor extends AbstractPropertyEditor {
    protected final ImagePropertyEditor imagePropertyEditor = new ImagePropertyEditor();

    protected void setValueInternal(Object value) {
        if (null == value) {
            super.setValueInternal(null);
        } else if (value instanceof CharSequence) {
            handleAsString(String.valueOf(value));
        } else {
            handleWithImagePropertyEditor(value);
        }
    }

    protected void handleAsString(String str) {
        if (isBlank(str)) {
            super.setValueInternal(null);
        } else if (str.contains("|")) {
            handleAsClassWithArg(str);
        } else {
            handleWithImagePropertyEditor(str);
        }
    }

    protected void handleWithImagePropertyEditor(Object value) {
        try {
            imagePropertyEditor.setValueInternal(value);
            Image image = (Image) imagePropertyEditor.getValue();
            if (image != null) {
                super.setValueInternal(new ImageView(image));
            } else {
                super.setValueInternal(null);
            }
        } catch (IllegalArgumentException iae) {
            throw illegalValue(value, Node.class, iae);
        }
    }

    protected void handleAsClassWithArg(String str) {
        String[] args = str.split("\\|");
        if (args.length == 2) {
            Class<?> iconClass = null;
            try {
                iconClass = GraphicPropertyEditor.class.getClassLoader().loadClass(args[0]);
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
                    super.setValueInternal(o);
                } else if (o instanceof Image) {
                    super.setValueInternal(new ImageView((Image) o));
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
