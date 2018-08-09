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

import static org.codehaus.griffon.compile.core.MethodDescriptor.annotatedMethod;
import static org.codehaus.griffon.compile.core.MethodDescriptor.annotatedType;
import static org.codehaus.griffon.compile.core.MethodDescriptor.annotations;
import static org.codehaus.griffon.compile.core.MethodDescriptor.args;
import static org.codehaus.griffon.compile.core.MethodDescriptor.method;
import static org.codehaus.griffon.compile.core.MethodDescriptor.type;
import static org.codehaus.griffon.compile.core.MethodDescriptor.typeParam;
import static org.codehaus.griffon.compile.core.MethodDescriptor.typeParams;
import static org.codehaus.griffon.compile.core.MethodDescriptor.typeWithParams;

/**
 * @author Andres Almiray
 */
public interface EventPublisherConstants extends BaseConstants {
    String EVENT_ROUTER_PROPERTY = "eventRouter";
    String EVENT_ROUTER_TYPE = "griffon.core.event.EventRouter";
    String EVENT_PUBLISHER_TYPE = "griffon.core.event.EventPublisher";
    String EVENT_PUBLISHER_FIELD_NAME = "this$eventPublisher";
    String EVENT_TYPE = "griffon.core.event.Event";

    String METHOD_SET_EVENT_ROUTER = "setEventRouter";
    String METHOD_ADD_EVENT_LISTENER = "addEventListener";
    String METHOD_REMOVE_EVENT_LISTENER = "removeEventListener";
    String METHOD_PUBLISH_EVENT = "publishEvent";
    String METHOD_PUBLISH_EVENT_OUTSIDE_UI = "publishEventOutsideUI";
    String METHOD_PUBLISH_EVENT_ASYNC = "publishEventAsync";
    String METHOD_IS_EVENT_PUBLISHING_ENABLED = "isEventPublishingEnabled";
    String METHOD_SET_EVENT_PUBLISHING_ENABLED = "setEventPublishingEnabled";
    String METHOD_GET_EVENT_LISTENERS = "getEventListeners";

    String E = "E";

    MethodDescriptor[] METHODS = new MethodDescriptor[]{
        method(
            type(VOID),
            METHOD_ADD_EVENT_LISTENER,
            args(annotatedType(annotations(ANNOTATION_NONNULL), JAVA_LANG_OBJECT))
        ),
        method(
            type(VOID),
            METHOD_ADD_EVENT_LISTENER,
            args(
                annotatedType(annotations(ANNOTATION_NONNULL), JAVA_LANG_STRING),
                annotatedType(annotations(ANNOTATION_NONNULL), GRIFFON_CORE_RUNNABLEWITHARGS, WILDCARD))
        ),
        method(
            type(VOID),
            METHOD_ADD_EVENT_LISTENER,
            args(typeWithParams(annotations(ANNOTATION_NONNULL), JAVA_UTIL_MAP, type(JAVA_LANG_STRING), type(JAVA_LANG_OBJECT)))
        ),
        method(
            type(VOID),
            typeParams(
                typeParam(E, EVENT_TYPE)
            ),
            METHOD_ADD_EVENT_LISTENER,
            args(
                annotatedType(annotations(ANNOTATION_NONNULL), JAVA_LANG_CLASS, E),
                annotatedType(annotations(ANNOTATION_NONNULL), GRIFFON_CORE_RUNNABLEWITHARGS, WILDCARD))
        ),

        method(
            type(VOID),
            METHOD_REMOVE_EVENT_LISTENER,
            args(annotatedType(annotations(ANNOTATION_NONNULL), JAVA_LANG_OBJECT))
        ),
        method(
            type(VOID),
            METHOD_REMOVE_EVENT_LISTENER,
            args(
                annotatedType(annotations(ANNOTATION_NONNULL), JAVA_LANG_STRING),
                annotatedType(annotations(ANNOTATION_NONNULL), GRIFFON_CORE_RUNNABLEWITHARGS, WILDCARD))
        ),
        method(
            type(VOID),
            METHOD_REMOVE_EVENT_LISTENER,
            args(typeWithParams(annotations(ANNOTATION_NONNULL), JAVA_UTIL_MAP, type(JAVA_LANG_STRING), type(JAVA_LANG_OBJECT)))
        ),
        method(
            type(VOID),
            typeParams(
                typeParam(E, EVENT_TYPE)
            ),
            METHOD_REMOVE_EVENT_LISTENER,
            args(
                annotatedType(annotations(ANNOTATION_NONNULL), JAVA_LANG_CLASS, E),
                annotatedType(annotations(ANNOTATION_NONNULL), GRIFFON_CORE_RUNNABLEWITHARGS, WILDCARD))
        ),

        method(
            type(VOID),
            METHOD_PUBLISH_EVENT,
            args(annotatedType(annotations(ANNOTATION_NONNULL), JAVA_LANG_STRING))
        ),
        method(
            type(VOID),
            METHOD_PUBLISH_EVENT,
            args(
                annotatedType(annotations(ANNOTATION_NONNULL), JAVA_LANG_STRING),
                annotatedType(annotations(ANNOTATION_NONNULL), JAVA_UTIL_LIST, WILDCARD))
        ),
        method(
            type(VOID),
            METHOD_PUBLISH_EVENT,
            args(annotatedType(annotations(ANNOTATION_NONNULL), EVENT_TYPE))
        ),

        method(
            type(VOID),
            METHOD_PUBLISH_EVENT_OUTSIDE_UI,
            args(annotatedType(annotations(ANNOTATION_NONNULL), JAVA_LANG_STRING))
        ),
        method(
            type(VOID),
            METHOD_PUBLISH_EVENT_OUTSIDE_UI,
            args(
                annotatedType(annotations(ANNOTATION_NONNULL), JAVA_LANG_STRING),
                annotatedType(annotations(ANNOTATION_NONNULL), JAVA_UTIL_LIST, WILDCARD))
        ),
        method(
            type(VOID),
            METHOD_PUBLISH_EVENT_OUTSIDE_UI,
            args(annotatedType(annotations(ANNOTATION_NONNULL), EVENT_TYPE))
        ),

        method(
            type(VOID),
            METHOD_PUBLISH_EVENT_ASYNC,
            args(annotatedType(annotations(ANNOTATION_NONNULL), JAVA_LANG_STRING))
        ),
        method(
            type(VOID),
            METHOD_PUBLISH_EVENT_ASYNC,
            args(
                annotatedType(annotations(ANNOTATION_NONNULL), JAVA_LANG_STRING),
                annotatedType(annotations(ANNOTATION_NONNULL), JAVA_UTIL_LIST, WILDCARD))
        ),
        method(
            type(VOID),
            METHOD_PUBLISH_EVENT_ASYNC,
            args(annotatedType(annotations(ANNOTATION_NONNULL), EVENT_TYPE))
        ),

        method(
            type(BOOLEAN),
            METHOD_IS_EVENT_PUBLISHING_ENABLED
        ),
        method(
            type(VOID),
            METHOD_SET_EVENT_PUBLISHING_ENABLED,
            args(type(BOOLEAN))
        ),

        annotatedMethod(
            annotations(ANNOTATION_NONNULL),
            typeWithParams(JAVA_UTIL_COLLECTION, type(JAVA_LANG_OBJECT)),
            METHOD_GET_EVENT_LISTENERS
        ),
        annotatedMethod(
            annotations(ANNOTATION_NONNULL),
            typeWithParams(JAVA_UTIL_COLLECTION, type(JAVA_LANG_OBJECT)),
            METHOD_GET_EVENT_LISTENERS,
            args(annotatedType(annotations(ANNOTATION_NONNULL), JAVA_LANG_STRING))
        )
    };
}
