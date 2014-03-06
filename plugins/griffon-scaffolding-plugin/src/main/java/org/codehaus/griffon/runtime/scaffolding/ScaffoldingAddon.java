/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codehaus.griffon.runtime.scaffolding;

import griffon.core.GriffonApplication;
import griffon.plugins.scaffolding.editors.CalendarPropertyEditor;
import griffon.plugins.scaffolding.editors.DatePropertyEditor;
import org.codehaus.griffon.runtime.core.addon.AbstractGriffonAddon;

import javax.annotation.Nonnull;
import javax.inject.Named;
import java.beans.PropertyEditorManager;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Andres Almiray
 */
@Named("scaffolding")
public class ScaffoldingAddon extends AbstractGriffonAddon {
    public void init(@Nonnull GriffonApplication application) {
        getLog().debug("Registering {} as editor for {}", Date.class.getName(), DatePropertyEditor.class.getName());
        PropertyEditorManager.registerEditor(Date.class, DatePropertyEditor.class);
        getLog().debug("Registering {} as editor for {}", Calendar.class.getName(), CalendarPropertyEditor.class.getName());
        PropertyEditorManager.registerEditor(Calendar.class, CalendarPropertyEditor.class);
    }
}
