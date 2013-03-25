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

package griffon.core.resources.editors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;

/**
 * @author Andres Almiray
 * @since 1.3.0
 */
public final class PropertyEditorResolver {
    private static final Logger LOG = LoggerFactory.getLogger(PropertyEditorResolver.class);

    private PropertyEditorResolver() {

    }

    public static PropertyEditor findEditor(Class<?> type) {
        if (type == null) return null;
        if (LOG.isTraceEnabled()) {
            LOG.trace("Searching PropertyEditor for " + type.getName());
        }

        PropertyEditor editor = null;
        if (Enum.class.isAssignableFrom(type)) {
            editor = new EnumPropertyEditor();
            ((EnumPropertyEditor) editor).setEnumType((Class<? extends Enum>) type);
        } else {
            editor = PropertyEditorManager.findEditor(type);
        }

        if (editor != null) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("PropertyEditor for " + type.getName() + " is " + editor.getClass().getName());
            }
        }
        return editor;
    }
}
