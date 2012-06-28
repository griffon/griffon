/*
 * Copyright 2010-2012 the original author or authors.
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

package griffon.core.controller;

import griffon.core.GriffonController;

/**
 * @author Andres Almiray
 * @since 1.1.0
 */
public interface GriffonControllerAction {
    String KEY_NAME = "name";
    String KEY_SHORT_DESCRIPTION = "shortDescription";
    String KEY_LONG_DESCRIPTION = "longDescription";
    String KEY_SMALL_ICON = "smallIcon";
    String KEY_LARGE_ICON = "largeIcon";
    String KEY_ENABLED = "enabled";
    String KEY_SELECTED = "selected";
    String KEY_ACCELERATOR = "accelerator";
    String KEY_MNEMONIC = "mnemonic";

    String getName();

    String getShortDescription();

    String getLongDescription();

    String getSmallIcon();

    String getLargeIcon();

    String getAccelerator();

    String getMnemonic();

    boolean isEnabled();

    boolean isSelected();

    GriffonController getController();

    Object getToolkitAction();

    void execute(Object... args);

    void setAccelerator(String accelerator);

    void setEnabled(boolean enabled);

    void setLargeIcon(String largeIcon);

    void setLongDescription(String longDescription);

    void setMnemonic(String mnemonic);

    void setName(String name);

    void setSelected(boolean selected);

    void setShortDescription(String shortDescription);

    void setSmallIcon(String smallIcon);
}
