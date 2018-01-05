/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2018 the original author or authors.
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
package org.codehaus.griffon.compile.core;

import java.lang.reflect.Modifier;

import static org.codehaus.griffon.compile.core.MethodDescriptor.annotatedMethod;
import static org.codehaus.griffon.compile.core.MethodDescriptor.annotatedType;
import static org.codehaus.griffon.compile.core.MethodDescriptor.annotations;
import static org.codehaus.griffon.compile.core.MethodDescriptor.args;
import static org.codehaus.griffon.compile.core.MethodDescriptor.method;
import static org.codehaus.griffon.compile.core.MethodDescriptor.throwing;
import static org.codehaus.griffon.compile.core.MethodDescriptor.type;

/**
 * @author Andres Almiray
 */
public interface VetoableConstants extends ObservableConstants {
    String VETOABLE_TYPE = "griffon.core.Vetoable";
    String VETOABLE_CHANGE_LISTENER_TYPE = "java.beans.VetoableChangeListener";
    String PROPERTY_VETO_EXCEPTION_TYPE = "java.beans.PropertyVetoException";
    String VETOABLE_CHANGE_SUPPORT_TYPE = "java.beans.VetoableChangeSupport";
    String VETOABLE_CHANGE_SUPPORT_FIELD_NAME = "this$vetoableChangeSupport";
    String VETOABLE_CHANGE_SUPPORT_FIELD_NAME_LOCK = "this$vetoableChangeSupportLock";

    String METHOD_GET_VETOABLE_CHANGE_SUPPORT = "getVetoableChangeSupport";
    String METHOD_ADD_VETOABLE_CHANGE_LISTENER = "addVetoableChangeListener";
    String METHOD_REMOVE_VETOABLE_CHANGE_LISTENER = "removeVetoableChangeListener";
    String METHOD_GET_VETOABLE_CHANGE_LISTENERS = "getVetoableChangeListeners";
    String METHOD_FIRE_VETOABLE_CHANGE = "fireVetoableChange";

    MethodDescriptor[] VETOABLE_METHODS = new MethodDescriptor[]{
        method(
            type(VOID),
            METHOD_ADD_VETOABLE_CHANGE_LISTENER,
            args(annotatedType(annotations(JAVAX_ANNOTATION_NULLABLE), VETOABLE_CHANGE_LISTENER_TYPE))
        ),
        method(
            type(VOID),
            METHOD_ADD_VETOABLE_CHANGE_LISTENER,
            args(
                annotatedType(annotations(JAVAX_ANNOTATION_NULLABLE), JAVA_LANG_STRING),
                annotatedType(annotations(JAVAX_ANNOTATION_NULLABLE), VETOABLE_CHANGE_LISTENER_TYPE))
        ),
        method(
            type(VOID),
            METHOD_REMOVE_VETOABLE_CHANGE_LISTENER,
            args(annotatedType(annotations(JAVAX_ANNOTATION_NULLABLE), VETOABLE_CHANGE_LISTENER_TYPE))
        ),
        method(
            type(VOID),
            METHOD_REMOVE_VETOABLE_CHANGE_LISTENER,
            args(
                annotatedType(annotations(JAVAX_ANNOTATION_NULLABLE), JAVA_LANG_STRING),
                annotatedType(annotations(JAVAX_ANNOTATION_NULLABLE), VETOABLE_CHANGE_LISTENER_TYPE))
        ),
        annotatedMethod(
            annotations(JAVAX_ANNOTATION_NONNULL),
            type(VETOABLE_CHANGE_LISTENER_TYPE, 1),
            METHOD_GET_VETOABLE_CHANGE_LISTENERS
        ),
        annotatedMethod(
            annotations(JAVAX_ANNOTATION_NONNULL),
            type(VETOABLE_CHANGE_LISTENER_TYPE, 1),
            METHOD_GET_VETOABLE_CHANGE_LISTENERS,
            args(annotatedType(annotations(JAVAX_ANNOTATION_NULLABLE), JAVA_LANG_STRING))
        )
    };

    MethodDescriptor[] VETOABLE_FIRE_METHODS = new MethodDescriptor[]{
        method(
            Modifier.PROTECTED,
            type(VOID),
            METHOD_FIRE_VETOABLE_CHANGE,
            args(annotatedType(annotations(JAVAX_ANNOTATION_NONNULL), PROPERTY_CHANGE_EVENT_TYPE)),
            throwing(type(PROPERTY_VETO_EXCEPTION_TYPE))
        ),
        method(
            Modifier.PROTECTED,
            type(VOID),
            METHOD_FIRE_VETOABLE_CHANGE,
            args(
                annotatedType(annotations(JAVAX_ANNOTATION_NONNULL), JAVA_LANG_STRING),
                annotatedType(annotations(JAVAX_ANNOTATION_NULLABLE), JAVA_LANG_OBJECT),
                annotatedType(annotations(JAVAX_ANNOTATION_NULLABLE), JAVA_LANG_OBJECT)),
            throwing(type(PROPERTY_VETO_EXCEPTION_TYPE))
        )
    };
}
