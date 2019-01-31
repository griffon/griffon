/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2019 the original author or authors.
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
package griffon.lanterna.widgets;

import com.googlecode.lanterna.gui.Action;
import com.googlecode.lanterna.gui.component.Button;
import griffon.exceptions.GriffonException;
import griffon.lanterna.support.LanternaAction;

import java.lang.reflect.Field;

import static griffon.util.GriffonNameUtils.isNotBlank;

/**
 * @author Andres Almiray
 */
public class MutableButton extends Button {
    private LanternaAction lanternaAction;

    public MutableButton() {
        this("", new LanternaAction());
    }

    public MutableButton(Action action) {
        this("", action instanceof LanternaAction ? action : new LanternaAction(action));
    }

    public MutableButton(String text) {
        this(text, new LanternaAction());
    }

    public MutableButton(String text, Action action) {
        super(text, action instanceof LanternaAction ? action : new LanternaAction(action));
        try {
            Field field = getClass().getSuperclass().getDeclaredField("onPressEvent");
            field.setAccessible(true);
            lanternaAction = (LanternaAction) field.get(this);

            lanternaAction.addPropertyChangeListener(LanternaAction.NAME, event -> setText(event.getNewValue().toString()));
            if (isNotBlank(lanternaAction.getName())) {
                setText(lanternaAction.getName());
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new GriffonException(e);
        }
    }

    public void setAction(Runnable runnable) {
        lanternaAction.setRunnable(runnable);
    }

    public LanternaAction getAction() {
        return lanternaAction;
    }
}
