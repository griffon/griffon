/*
 * Copyright 2012-2013 the original author or authors.
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

package org.codehaus.griffon.core.compile;

import java.lang.reflect.Modifier;

import static org.codehaus.griffon.core.compile.MethodDescriptor.*;

/**
 * @author Andres Almiray
 */
public interface ObservableConstants extends BaseConstants {
    String OBSERVABLE_TYPE = "griffon.core.Observable";
    String PROPERTY_CHANGE_LISTENER_TYPE = "java.beans.PropertyChangeListener";
    String PROPERTY_CHANGE_EVENT_TYPE = "java.beans.PropertyChangeEvent";
    String PROPERTY_CHANGE_SUPPORT_TYPE = "java.beans.PropertyChangeSupport";
    String PROPERTY_CHANGE_SUPPORT_FIELD_NAME = "this$propertyChangeSupport";
    String PROPERTY_CHANGE_SUPPORT_FIELD_NAME_LOCK = "this$propertyChangeSupportLock";

    String METHOD_GET_PROPERTY_CHANGE_SUPPORT = "getPropertyChangeSupport";
    String METHOD_ADD_PROPERTY_CHANGE_LISTENER = "addPropertyChangeListener";
    String METHOD_REMOVE_PROPERTY_CHANGE_LISTENER = "removePropertyChangeListener";
    String METHOD_GET_PROPERTY_CHANGE_LISTENERS = "getPropertyChangeListeners";
    String METHOD_FIRE_PROPERTY_CHANGE = "firePropertyChange";
    String OLD_VALUE = "oldValue";
    String NEW_VALUE = "newValue";
    String EVENT = "event";

    MethodDescriptor[] OBSERVABLE_METHODS = new MethodDescriptor[]{
        method(
            type(VOID),
            METHOD_ADD_PROPERTY_CHANGE_LISTENER,
            args(type(types(type(JAVAX_ANNOTATION_NULLABLE)), PROPERTY_CHANGE_LISTENER_TYPE))
        ),
        method(
            type(VOID),
            METHOD_ADD_PROPERTY_CHANGE_LISTENER,
            args(
                type(types(type(JAVAX_ANNOTATION_NULLABLE)), JAVA_LANG_STRING),
                type(types(type(JAVAX_ANNOTATION_NULLABLE)), PROPERTY_CHANGE_LISTENER_TYPE))
        ),
        method(
            type(VOID),
            METHOD_REMOVE_PROPERTY_CHANGE_LISTENER,
            args(type(types(type(JAVAX_ANNOTATION_NULLABLE)), PROPERTY_CHANGE_LISTENER_TYPE))
        ),
        method(
            type(VOID),
            METHOD_REMOVE_PROPERTY_CHANGE_LISTENER,
            args(
                type(types(type(JAVAX_ANNOTATION_NULLABLE)), JAVA_LANG_STRING),
                type(types(type(JAVAX_ANNOTATION_NULLABLE)), PROPERTY_CHANGE_LISTENER_TYPE))
        ),
        annotatedMethod(
            types(type(JAVAX_ANNOTATION_NONNULL)),
            type(PROPERTY_CHANGE_LISTENER_TYPE, 1),
            METHOD_GET_PROPERTY_CHANGE_LISTENERS
        ),
        annotatedMethod(
            types(type(JAVAX_ANNOTATION_NONNULL)),
            type(PROPERTY_CHANGE_LISTENER_TYPE, 1),
            METHOD_GET_PROPERTY_CHANGE_LISTENERS,
            args(type(types(type(JAVAX_ANNOTATION_NULLABLE)), JAVA_LANG_STRING))
        )
    };

    MethodDescriptor[] OBSERVABLE_FIRE_METHODS = new MethodDescriptor[]{
        method(
            Modifier.PROTECTED,
            type(VOID),
            METHOD_FIRE_PROPERTY_CHANGE,
            args(type(types(type(JAVAX_ANNOTATION_NONNULL)), PROPERTY_CHANGE_EVENT_TYPE))
        ),
        method(
            Modifier.PROTECTED,
            type(VOID),
            METHOD_FIRE_PROPERTY_CHANGE,
            args(
                type(types(type(JAVAX_ANNOTATION_NONNULL)), JAVA_LANG_STRING),
                type(types(type(JAVAX_ANNOTATION_NULLABLE)), JAVA_LANG_OBJECT),
                type(types(type(JAVAX_ANNOTATION_NULLABLE)), JAVA_LANG_OBJECT))
        )
    };
}
