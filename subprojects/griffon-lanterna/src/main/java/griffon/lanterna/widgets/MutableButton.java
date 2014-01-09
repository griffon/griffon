/*
 * Copyright 20012-2014 the original author or authors.
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
package griffon.lanterna.widgets;

import com.googlecode.lanterna.gui.Action;
import com.googlecode.lanterna.gui.component.Button;
import griffon.exceptions.GriffonException;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Field;

import static griffon.util.GriffonNameUtils.isBlank;

/**
 * @author Andres Almiray
 */
public class MutableButton extends Button {
    private MutableAction mutableAction;

    public MutableButton() {
        this("", new MutableAction());
    }

    public MutableButton(Action action) {
        this("", action instanceof MutableAction ? action : new MutableAction(action));
    }

    public MutableButton(String text) {
        this(text, new MutableAction());
    }

    public MutableButton(String text, Action action) {
        super(text, action instanceof MutableAction ? action : new MutableAction(action));
        try {
            Field field = getClass().getSuperclass().getDeclaredField("onPressEvent");
            field.setAccessible(true);
            mutableAction = (MutableAction) field.get(this);

            mutableAction.addPropertyChangeListener(MutableAction.NAME, new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent event) {
                    setText(event.getNewValue().toString());
                }
            });
            if (!isBlank(mutableAction.getName())) {
                setText(mutableAction.getName());
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new GriffonException(e);
        }
    }

    public void setAction(Runnable runnable) {
        mutableAction.setRunnable(runnable);
    }

    public MutableAction getAction() {
        return mutableAction;
    }
}
