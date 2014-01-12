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

import static org.codehaus.griffon.core.compile.MethodDescriptor.args;
import static org.codehaus.griffon.core.compile.MethodDescriptor.type;
import static org.codehaus.griffon.core.compile.MethodDescriptor.typeWithParams;

/**
 * @author Andres Almiray
 */
public interface EventPublisherConstants extends BaseConstants {
    String EVENT_ROUTER_PROPERTY = "eventRouter";
    String EVENT_ROUTER_TYPE = "griffon.core.event.EventRouter";
    String EVENT_PUBLISHER_TYPE = "griffon.core.event.EventPublisher";
    String EVENT_PUBLISHER_FIELD_NAME = "this$eventPublisher";
    String EVENT_PUBLISHER_FIELD_TYPE = "org.codehaus.griffon.runtime.core.event.DefaultEventPublisher";
    String EVENT_TYPE = "griffon.core.event.Event";

    String METHOD_SET_EVENT_ROUTER = "setEventRouter";
    String METHOD_ADD_EVENT_LISTENER = "addEventListener";
    String METHOD_REMOVE_EVENT_LISTENER = "removeEventListener";
    String METHOD_PUBLISH_EVENT = "publishEvent";
    String METHOD_PUBLISH_EVENT_OUTSIDE_UI = "publishEventOutsideUI";
    String METHOD_PUBLISH_EVENT_ASYNC = "publishEventAsync";
    String METHOD_IS_EVENT_PUBLISHING_ENABLED = "isEventPublishingEnabled";
    String METHOD_SET_EVENT_PUBLISHING_ENABLED = "setEventPublishingEnabled";

    MethodDescriptor[] METHODS = new MethodDescriptor[]{
        MethodDescriptor.method(
            type(VOID),
            METHOD_ADD_EVENT_LISTENER,
            args(type(JAVA_LANG_OBJECT))
        ),
        MethodDescriptor.method(
            type(VOID),
            METHOD_ADD_EVENT_LISTENER,
            args(type(JAVA_LANG_STRING), type(GRIFFON_CORE_CALLABLEWITHARGS, WILDCARD))
        ),
        MethodDescriptor.method(
            type(VOID),
            METHOD_ADD_EVENT_LISTENER,
            args(typeWithParams(JAVA_UTIL_MAP, type(JAVA_LANG_STRING), type(GRIFFON_CORE_CALLABLEWITHARGS, WILDCARD)))
        ),
        MethodDescriptor.method(
            type(VOID),
            METHOD_ADD_EVENT_LISTENER,
            args(type(JAVA_LANG_CLASS, EVENT_TYPE), type(GRIFFON_CORE_CALLABLEWITHARGS, WILDCARD))
        ),

        MethodDescriptor.method(
            type(VOID),
            METHOD_REMOVE_EVENT_LISTENER,
            args(type(JAVA_LANG_OBJECT))
        ),
        MethodDescriptor.method(
            type(VOID),
            METHOD_REMOVE_EVENT_LISTENER,
            args(type(JAVA_LANG_STRING), type(GRIFFON_CORE_CALLABLEWITHARGS, WILDCARD))
        ),
        MethodDescriptor.method(
            type(VOID),
            METHOD_REMOVE_EVENT_LISTENER,
            args(typeWithParams(JAVA_UTIL_MAP, type(JAVA_LANG_STRING), type(GRIFFON_CORE_CALLABLEWITHARGS, WILDCARD)))
        ),
        MethodDescriptor.method(
            type(VOID),
            METHOD_REMOVE_EVENT_LISTENER,
            args(type(JAVA_LANG_CLASS, EVENT_TYPE), type(GRIFFON_CORE_CALLABLEWITHARGS, WILDCARD))
        ),

        MethodDescriptor.method(
            type(VOID),
            METHOD_PUBLISH_EVENT,
            args(type(JAVA_LANG_STRING))
        ),
        MethodDescriptor.method(
            type(VOID),
            METHOD_PUBLISH_EVENT,
            args(type(JAVA_LANG_STRING), type(JAVA_UTIL_LIST, WILDCARD))
        ),
        MethodDescriptor.method(
            type(VOID),
            METHOD_PUBLISH_EVENT,
            args(type(EVENT_TYPE))
        ),

        MethodDescriptor.method(
            type(VOID),
            METHOD_PUBLISH_EVENT_OUTSIDE_UI,
            args(type(JAVA_LANG_STRING))
        ),
        MethodDescriptor.method(
            type(VOID),
            METHOD_PUBLISH_EVENT_OUTSIDE_UI,
            args(type(JAVA_LANG_STRING), type(JAVA_UTIL_LIST, WILDCARD))
        ),
        MethodDescriptor.method(
            type(VOID),
            METHOD_PUBLISH_EVENT_OUTSIDE_UI,
            args(type(EVENT_TYPE))
        ),

        MethodDescriptor.method(
            type(VOID),
            METHOD_PUBLISH_EVENT_ASYNC,
            args(type(JAVA_LANG_STRING))
        ),
        MethodDescriptor.method(
            type(VOID),
            METHOD_PUBLISH_EVENT_ASYNC,
            args(type(JAVA_LANG_STRING), type(JAVA_UTIL_LIST, WILDCARD))
        ),
        MethodDescriptor.method(
            type(VOID),
            METHOD_PUBLISH_EVENT_ASYNC,
            args(type(EVENT_TYPE))
        ),

        MethodDescriptor.method(
            type(BOOLEAN),
            METHOD_IS_EVENT_PUBLISHING_ENABLED
        ),
        MethodDescriptor.method(
            type(VOID),
            METHOD_SET_EVENT_PUBLISHING_ENABLED,
            args(type(BOOLEAN))
        )
    };
}
