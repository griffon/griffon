/*
 * Copyright 2008-2015 the original author or authors.
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
package org.codehaus.griffon.compile.core

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import java.lang.reflect.Modifier

import static org.codehaus.griffon.compile.core.MethodDescriptor.*

@Unroll
class MethodDescriptorSpec extends Specification implements BaseConstants {
    void "Can build a type descriptor with #args"() {
        expect:
        new MethodDescriptor.Type(* args).signature() == signature

        where:
        args << TYPE_ARGUMENTS
        signature << TYPE_SIGNATURES
    }

    void "Can build a wildcard descriptor with #args"() {
        given:
        MethodDescriptor.Wildcard wildcard = new MethodDescriptor.Wildcard(* args)

        expect:

        wildcard.signature() == signature
        wildcard.extends == isextends
        wildcard.super == issuper

        where:
        args                                            | signature                  | isextends | issuper
        []                                              | '?'                        | false     | false
        [types(type(JAVA_UTIL_LIST))]                   | '? extends java.util.List' | true      | false
        [Wildcard.EXTENDS, types(type(JAVA_UTIL_LIST))] | '? extends java.util.List' | true      | false
        [Wildcard.SUPER, types(type(JAVA_UTIL_LIST))]   | '? super java.util.List'   | false     | true
    }

    void "Can build a method descriptor with #args"() {
        expect:
        MethodDescriptor.method(* args).signature == signature

        where:
        args << METHOD_ARGUMENTS
        signature << METHOD_SIGNATURES
    }

    void "Can build an annotated method descriptor with #args"() {
        expect:
        MethodDescriptor.annotatedMethod(* args).signature == signature

        where:
        args << ANNOTATED_METHOD_ARGUMENTS
        signature << ANNOTATED_METHOD_SIGNATURES
    }

    @Shared
    private List TYPE_ARGUMENTS = [
        [
            JAVA_LANG_STRING
        ],
        [
            JAVA_LANG_STRING,
            1
        ],
        [
            JAVA_UTIL_LIST,
            0
        ],
        [
            JAVA_UTIL_LIST,
            1,
            type(T)
        ],
        [
            types(type(JAVAX_ANNOTATION_NONNULL)),
            JAVA_LANG_STRING
        ],
        [
            types(type(JAVAX_ANNOTATION_NONNULL)),
            JAVA_LANG_STRING,
            1
        ],
        [
            types(type(JAVAX_ANNOTATION_NONNULL)),
            JAVA_UTIL_LIST,
            0
        ],
        [
            types(type(JAVAX_ANNOTATION_NONNULL)),
            JAVA_UTIL_LIST,
            1,
            type(T)
        ],
        [
            types(type(JAVAX_ANNOTATION_NONNULL)),
            JAVA_UTIL_LIST,
            type(T)
        ],
        [
            null,
            JAVA_UTIL_LIST,
            1,
            null
        ],
        [
            JAVA_UTIL_MAP,
            0,
            types("K", "V")
        ],
    ]

    @Shared
    private List TYPE_SIGNATURES = [
        "java.lang.String",
        "java.lang.String[]",
        "java.util.List",
        "java.util.List<T>[]",
        "@javax.annotation.Nonnull java.lang.String",
        "@javax.annotation.Nonnull java.lang.String[]",
        "@javax.annotation.Nonnull java.util.List",
        "@javax.annotation.Nonnull java.util.List<T>[]",
        "@javax.annotation.Nonnull java.util.List<T>",
        "java.util.List[]",
        "java.util.Map<K, V>"
    ]

    @Shared
    private String METHOD_NAME = 'doSomething'

    @Shared
    private String JAVA_LANG_EXCEPTION = 'java.lang.Exception'

    @Shared
    private List METHOD_ARGUMENTS = [
        [
            type(JAVA_LANG_STRING),
            METHOD_NAME
        ],
        [
            type(JAVA_LANG_STRING),
            METHOD_NAME,
            types(type(JAVA_LANG_STRING))
        ],
        [
            type(T),
            typeParams(T),
            METHOD_NAME
        ],
        [
            type(T),
            typeParams(T),
            METHOD_NAME,
            types(type(JAVA_LANG_STRING))
        ],
        [
            Modifier.PRIVATE,
            type(JAVA_LANG_STRING),
            METHOD_NAME
        ],
        [
            Modifier.PRIVATE,
            type(JAVA_LANG_STRING),
            METHOD_NAME,
            types(type(JAVA_LANG_STRING))
        ],
        [
            Modifier.PRIVATE,
            type(T),
            typeParams(T),
            METHOD_NAME
        ],
        [
            Modifier.PRIVATE,
            type(T),
            typeParams(T),
            METHOD_NAME,
            types(type(JAVA_LANG_STRING))
        ],
        [
            type(JAVA_LANG_STRING),
            METHOD_NAME,
            types(type(JAVA_LANG_STRING)),
            throwing(type(JAVA_LANG_EXCEPTION))
        ],
        [
            type(T),
            typeParams(T),
            METHOD_NAME,
            types(type(JAVA_LANG_STRING)),
            throwing(type(JAVA_LANG_EXCEPTION))
        ],
        [
            Modifier.PRIVATE,
            type(JAVA_LANG_STRING),
            METHOD_NAME,
            types(type(JAVA_LANG_STRING)),
            throwing(type(JAVA_LANG_EXCEPTION))
        ],
        [
            Modifier.PRIVATE,
            type(T),
            typeParams(T),
            METHOD_NAME,
            types(type(JAVA_LANG_STRING)),
            throwing(type(JAVA_LANG_EXCEPTION))
        ],
    ]

    @Shared
    private List METHOD_SIGNATURES = [
        'public java.lang.String doSomething()',
        'public java.lang.String doSomething(java.lang.String arg0)',
        'public <T> T doSomething()',
        'public <T> T doSomething(java.lang.String arg0)',
        'private java.lang.String doSomething()',
        'private java.lang.String doSomething(java.lang.String arg0)',
        'private <T> T doSomething()',
        'private <T> T doSomething(java.lang.String arg0)',
        'public java.lang.String doSomething(java.lang.String arg0) throws java.lang.Exception',
        'public <T> T doSomething(java.lang.String arg0) throws java.lang.Exception',
        'private java.lang.String doSomething(java.lang.String arg0) throws java.lang.Exception',
        'private <T> T doSomething(java.lang.String arg0) throws java.lang.Exception'
    ]

    @Shared
    private List ANNOTATED_METHOD_ARGUMENTS = [
        [
            types(type(JAVAX_ANNOTATION_NONNULL)),
            type(JAVA_LANG_STRING),
            METHOD_NAME
        ],
        [
            types(type(JAVAX_ANNOTATION_NONNULL)),
            type(JAVA_LANG_STRING),
            METHOD_NAME,
            types(type(JAVA_LANG_STRING))
        ],
        [
            types(type(JAVAX_ANNOTATION_NONNULL)),
            type(T),
            typeParams(T),
            METHOD_NAME
        ],
        [
            types(type(JAVAX_ANNOTATION_NONNULL)),
            type(T),
            typeParams(T),
            METHOD_NAME,
            types(type(JAVA_LANG_STRING))
        ],
        [
            types(type(JAVAX_ANNOTATION_NONNULL)),
            Modifier.PRIVATE,
            type(JAVA_LANG_STRING),
            METHOD_NAME
        ],
        [
            types(type(JAVAX_ANNOTATION_NONNULL)),
            Modifier.PRIVATE,
            type(JAVA_LANG_STRING),
            METHOD_NAME,
            types(type(JAVA_LANG_STRING))
        ],
        [
            types(type(JAVAX_ANNOTATION_NONNULL)),
            Modifier.PRIVATE,
            type(T),
            typeParams(T),
            METHOD_NAME
        ],
        [
            types(type(JAVAX_ANNOTATION_NONNULL)),
            Modifier.PRIVATE,
            type(T),
            typeParams(T),
            METHOD_NAME,
            types(type(JAVA_LANG_STRING))
        ],
        [
            types(type(JAVAX_ANNOTATION_NONNULL)),
            type(JAVA_LANG_STRING),
            METHOD_NAME,
            types(type(JAVA_LANG_STRING)),
            throwing(type(JAVA_LANG_EXCEPTION))
        ],
        [
            types(type(JAVAX_ANNOTATION_NONNULL)),
            type(T),
            typeParams(T),
            METHOD_NAME,
            types(type(JAVA_LANG_STRING)),
            throwing(type(JAVA_LANG_EXCEPTION))
        ],
        [
            types(type(JAVAX_ANNOTATION_NONNULL)),
            Modifier.PRIVATE,
            type(JAVA_LANG_STRING),
            METHOD_NAME,
            types(type(JAVA_LANG_STRING)),
            throwing(type(JAVA_LANG_EXCEPTION))
        ],
        [
            types(type(JAVAX_ANNOTATION_NONNULL)),
            Modifier.PRIVATE,
            type(T),
            typeParams(T),
            METHOD_NAME,
            types(type(JAVA_LANG_STRING)),
            throwing(type(JAVA_LANG_EXCEPTION))
        ],
    ]

    @Shared
    private List ANNOTATED_METHOD_SIGNATURES = [
        '@javax.annotation.Nonnull public java.lang.String doSomething()',
        '@javax.annotation.Nonnull public java.lang.String doSomething(java.lang.String arg0)',
        '@javax.annotation.Nonnull public <T> T doSomething()',
        '@javax.annotation.Nonnull public <T> T doSomething(java.lang.String arg0)',
        '@javax.annotation.Nonnull private java.lang.String doSomething()',
        '@javax.annotation.Nonnull private java.lang.String doSomething(java.lang.String arg0)',
        '@javax.annotation.Nonnull private <T> T doSomething()',
        '@javax.annotation.Nonnull private <T> T doSomething(java.lang.String arg0)',
        '@javax.annotation.Nonnull public java.lang.String doSomething(java.lang.String arg0) throws java.lang.Exception',
        '@javax.annotation.Nonnull public <T> T doSomething(java.lang.String arg0) throws java.lang.Exception',
        '@javax.annotation.Nonnull private java.lang.String doSomething(java.lang.String arg0) throws java.lang.Exception',
        '@javax.annotation.Nonnull private <T> T doSomething(java.lang.String arg0) throws java.lang.Exception'
    ]
}
