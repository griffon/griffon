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

import static org.codehaus.griffon.core.compile.MethodDescriptor.*;

/**
 * @author Andres Almiray
 */
public interface ResourceResolverAwareConstants extends BaseConstants {
    String RESOURCE_RESOLVER_PROPERTY = "resourceResolver";
    String NO_SUCH_RESOURCE_EXCEPTION_TYPE = "griffon.core.resources.NoSuchResourceException";
    String RESOURCE_RESOLVER_TYPE = "griffon.core.resources.ResourceResolver";
    String JAVA_UTIL_LOCALE = "java.util.Locale";
    String METHOD_RESOLVE_RESOURCE = "resolveResource";
    String METHOD_RESOLVE_RESOURCE_VALUE = "resolveResourceValue";
    String METHOD_FORMAT_RESOURCE = "formatResource";

    MethodDescriptor[] METHODS = new MethodDescriptor[]{
        annotatedMethod(
            types(type(JAVAX_ANNOTATION_NONNULL)),
            type(JAVA_LANG_OBJECT),
            METHOD_RESOLVE_RESOURCE_VALUE,
            args(
                type(types(type(JAVAX_ANNOTATION_NONNULL)), JAVA_LANG_STRING),
                type(types(type(JAVAX_ANNOTATION_NONNULL)), JAVA_UTIL_LOCALE)),
            throwing(type(NO_SUCH_RESOURCE_EXCEPTION_TYPE))
        ),

        annotatedMethod(
            types(type(JAVAX_ANNOTATION_NONNULL)),
            type(JAVA_LANG_OBJECT),
            METHOD_RESOLVE_RESOURCE,
            args(type(types(type(JAVAX_ANNOTATION_NONNULL)), JAVA_LANG_STRING)),
            throwing(type(NO_SUCH_RESOURCE_EXCEPTION_TYPE))
        ),
        annotatedMethod(
            types(type(JAVAX_ANNOTATION_NONNULL)),
            type(JAVA_LANG_OBJECT),
            METHOD_RESOLVE_RESOURCE,
            args(
                type(types(type(JAVAX_ANNOTATION_NONNULL)), JAVA_LANG_STRING),
                type(types(type(JAVAX_ANNOTATION_NONNULL)), JAVA_UTIL_LOCALE)),
            throwing(type(NO_SUCH_RESOURCE_EXCEPTION_TYPE))
        ),
        annotatedMethod(
            types(type(JAVAX_ANNOTATION_NONNULL)),
            type(JAVA_LANG_OBJECT),
            METHOD_RESOLVE_RESOURCE,
            args(
                type(types(type(JAVAX_ANNOTATION_NONNULL)), JAVA_LANG_STRING),
                type(types(type(JAVAX_ANNOTATION_NONNULL)), JAVA_LANG_OBJECT, 1)),
            throwing(type(NO_SUCH_RESOURCE_EXCEPTION_TYPE))
        ),
        annotatedMethod(
            types(type(JAVAX_ANNOTATION_NONNULL)),
            type(JAVA_LANG_OBJECT),
            METHOD_RESOLVE_RESOURCE,
            args(
                type(types(type(JAVAX_ANNOTATION_NONNULL)), JAVA_LANG_STRING),
                type(types(type(JAVAX_ANNOTATION_NONNULL)), JAVA_LANG_OBJECT, 1),
                type(types(type(JAVAX_ANNOTATION_NONNULL)), JAVA_UTIL_LOCALE)),
            throwing(type(NO_SUCH_RESOURCE_EXCEPTION_TYPE))
        ),
        annotatedMethod(
            types(type(JAVAX_ANNOTATION_NONNULL)),
            type(JAVA_LANG_OBJECT),
            METHOD_RESOLVE_RESOURCE,
            args(
                type(types(type(JAVAX_ANNOTATION_NONNULL)), JAVA_LANG_STRING),
                type(types(type(JAVAX_ANNOTATION_NONNULL)), JAVA_UTIL_LIST, WILDCARD)),
            throwing(type(NO_SUCH_RESOURCE_EXCEPTION_TYPE))
        ),
        annotatedMethod(
            types(type(JAVAX_ANNOTATION_NONNULL)),
            type(JAVA_LANG_OBJECT),
            METHOD_RESOLVE_RESOURCE,
            args(
                type(types(type(JAVAX_ANNOTATION_NONNULL)), JAVA_LANG_STRING),
                type(types(type(JAVAX_ANNOTATION_NONNULL)), JAVA_UTIL_LIST, WILDCARD),
                type(types(type(JAVAX_ANNOTATION_NONNULL)), JAVA_UTIL_LOCALE)),
            throwing(type(NO_SUCH_RESOURCE_EXCEPTION_TYPE))
        ),
        annotatedMethod(
            types(type(JAVAX_ANNOTATION_NONNULL)),
            type(JAVA_LANG_OBJECT),
            METHOD_RESOLVE_RESOURCE,
            args(
                type(types(type(JAVAX_ANNOTATION_NONNULL)), JAVA_LANG_STRING),
                type(types(type(JAVAX_ANNOTATION_NONNULL)), JAVA_UTIL_MAP, JAVA_LANG_STRING, JAVA_LANG_OBJECT)),
            throwing(type(NO_SUCH_RESOURCE_EXCEPTION_TYPE))
        ),
        annotatedMethod(
            types(type(JAVAX_ANNOTATION_NONNULL)),
            type(JAVA_LANG_OBJECT),
            METHOD_RESOLVE_RESOURCE,
            args(
                type(types(type(JAVAX_ANNOTATION_NONNULL)), JAVA_LANG_STRING),
                type(types(type(JAVAX_ANNOTATION_NONNULL)), JAVA_UTIL_MAP, JAVA_LANG_STRING, JAVA_LANG_OBJECT),
                type(types(type(JAVAX_ANNOTATION_NONNULL)), JAVA_UTIL_LOCALE)),
            throwing(type(NO_SUCH_RESOURCE_EXCEPTION_TYPE))
        ),

        annotatedMethod(
            types(type(JAVAX_ANNOTATION_NULLABLE)),
            type(JAVA_LANG_OBJECT),
            METHOD_RESOLVE_RESOURCE,
            args(
                type(types(type(JAVAX_ANNOTATION_NONNULL)), JAVA_LANG_STRING),
                type(types(type(JAVAX_ANNOTATION_NULLABLE)), JAVA_LANG_OBJECT))
        ),
        annotatedMethod(
            types(type(JAVAX_ANNOTATION_NULLABLE)),
            type(JAVA_LANG_OBJECT),
            METHOD_RESOLVE_RESOURCE,
            args(
                type(types(type(JAVAX_ANNOTATION_NONNULL)), JAVA_LANG_STRING),
                type(types(type(JAVAX_ANNOTATION_NONNULL)), JAVA_UTIL_LOCALE),
                type(types(type(JAVAX_ANNOTATION_NULLABLE)), JAVA_LANG_OBJECT))
        ),
        annotatedMethod(
            types(type(JAVAX_ANNOTATION_NULLABLE)),
            type(JAVA_LANG_OBJECT),
            METHOD_RESOLVE_RESOURCE,
            args(
                type(types(type(JAVAX_ANNOTATION_NONNULL)), JAVA_LANG_STRING),
                type(types(type(JAVAX_ANNOTATION_NONNULL)), JAVA_LANG_OBJECT, 1),
                type(types(type(JAVAX_ANNOTATION_NULLABLE)), JAVA_LANG_OBJECT))
        ),
        annotatedMethod(
            types(type(JAVAX_ANNOTATION_NULLABLE)),
            type(JAVA_LANG_OBJECT),
            METHOD_RESOLVE_RESOURCE,
            args(
                type(types(type(JAVAX_ANNOTATION_NONNULL)), JAVA_LANG_STRING),
                type(types(type(JAVAX_ANNOTATION_NONNULL)), JAVA_LANG_OBJECT, 1),
                type(types(type(JAVAX_ANNOTATION_NONNULL)), JAVA_UTIL_LOCALE),
                type(types(type(JAVAX_ANNOTATION_NULLABLE)), JAVA_LANG_OBJECT))
        ),
        annotatedMethod(
            types(type(JAVAX_ANNOTATION_NULLABLE)),
            type(JAVA_LANG_OBJECT),
            METHOD_RESOLVE_RESOURCE,
            args(
                type(types(type(JAVAX_ANNOTATION_NONNULL)), JAVA_LANG_STRING),
                type(types(type(JAVAX_ANNOTATION_NONNULL)), JAVA_UTIL_LIST, WILDCARD),
                type(types(type(JAVAX_ANNOTATION_NULLABLE)), JAVA_LANG_OBJECT))
        ),
        annotatedMethod(
            types(type(JAVAX_ANNOTATION_NULLABLE)),
            type(JAVA_LANG_OBJECT),
            METHOD_RESOLVE_RESOURCE,
            args(
                type(types(type(JAVAX_ANNOTATION_NONNULL)), JAVA_LANG_STRING),
                type(types(type(JAVAX_ANNOTATION_NONNULL)), JAVA_UTIL_LIST, WILDCARD),
                type(types(type(JAVAX_ANNOTATION_NONNULL)), JAVA_UTIL_LOCALE),
                type(types(type(JAVAX_ANNOTATION_NULLABLE)), JAVA_LANG_OBJECT))
        ),
        annotatedMethod(
            types(type(JAVAX_ANNOTATION_NULLABLE)),
            type(JAVA_LANG_OBJECT),
            METHOD_RESOLVE_RESOURCE,
            args(
                type(types(type(JAVAX_ANNOTATION_NONNULL)), JAVA_LANG_STRING),
                type(types(type(JAVAX_ANNOTATION_NONNULL)), JAVA_UTIL_MAP, JAVA_LANG_STRING, JAVA_LANG_OBJECT),
                type(types(type(JAVAX_ANNOTATION_NULLABLE)), JAVA_LANG_OBJECT))
        ),
        annotatedMethod(
            types(type(JAVAX_ANNOTATION_NULLABLE)),
            type(JAVA_LANG_OBJECT),
            METHOD_RESOLVE_RESOURCE,
            args(
                type(types(type(JAVAX_ANNOTATION_NONNULL)), JAVA_LANG_STRING),
                type(types(type(JAVAX_ANNOTATION_NONNULL)), JAVA_UTIL_MAP, JAVA_LANG_STRING, JAVA_LANG_OBJECT),
                type(types(type(JAVAX_ANNOTATION_NONNULL)), JAVA_UTIL_LOCALE),
                type(types(type(JAVAX_ANNOTATION_NULLABLE)), JAVA_LANG_OBJECT))
        ),

        annotatedMethod(
            types(type(JAVAX_ANNOTATION_NONNULL)),
            type(JAVA_LANG_STRING),
            METHOD_FORMAT_RESOURCE,
            args(
                type(types(type(JAVAX_ANNOTATION_NONNULL)), JAVA_LANG_OBJECT, 1),
                type(types(type(JAVAX_ANNOTATION_NONNULL)), JAVA_UTIL_MAP, JAVA_LANG_STRING, JAVA_LANG_OBJECT))
        ),
        annotatedMethod(
            types(type(JAVAX_ANNOTATION_NONNULL)),
            type(JAVA_LANG_STRING),
            METHOD_FORMAT_RESOURCE,
            args(
                type(types(type(JAVAX_ANNOTATION_NONNULL)), JAVA_LANG_STRING),
                type(types(type(JAVAX_ANNOTATION_NONNULL)), JAVA_UTIL_LIST, WILDCARD))
        ),
        annotatedMethod(
            types(type(JAVAX_ANNOTATION_NONNULL)),
            type(JAVA_LANG_STRING),
            METHOD_FORMAT_RESOURCE,
            args(
                type(types(type(JAVAX_ANNOTATION_NONNULL)), JAVA_LANG_STRING),
                type(types(type(JAVAX_ANNOTATION_NONNULL)), JAVA_UTIL_MAP, JAVA_LANG_STRING, JAVA_LANG_OBJECT))
        )
    };
}
