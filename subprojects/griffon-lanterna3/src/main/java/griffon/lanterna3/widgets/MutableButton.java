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
package griffon.lanterna3.widgets;

import com.googlecode.lanterna.gui2.Button;
import griffon.lanterna3.support.LanternaAction;

import static griffon.util.GriffonNameUtils.isNotBlank;

/**
 * @author Andres Almiray
 * @since 3.0.0
 */
public class MutableButton extends Button {
    private LanternaAction lanternaAction;

    public MutableButton() {
        this("", new LanternaAction(""));
    }

    public MutableButton(LanternaAction action) {
        this(action.getName(), action);
    }

    public MutableButton(String text) {
        this(text, new LanternaAction(""));
    }

    public MutableButton(String text, LanternaAction action) {
        super(text, action != null ? action.getRunnable() : null);
        lanternaAction = action != null ? action : new LanternaAction(() -> {});

        lanternaAction.addPropertyChangeListener(LanternaAction.NAME, event -> setLabel(event.getNewValue().toString()));
        if (isNotBlank(lanternaAction.getName())) {
            setLabel(lanternaAction.getName());
        }
    }

    public void setAction(Runnable runnable) {
        lanternaAction.setRunnable(runnable);
    }

    public LanternaAction getAction() {
        return lanternaAction;
    }
}
