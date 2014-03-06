/*
 * Copyright 2008-2014 the original author or authors.
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
package org.codehaus.griffon.compile.domain;

import org.codehaus.griffon.compile.core.BaseConstants;
import org.codehaus.griffon.compile.core.MethodDescriptor;

import static java.lang.reflect.Modifier.PUBLIC;
import static java.lang.reflect.Modifier.STATIC;
import static org.codehaus.griffon.compile.core.MethodDescriptor.*;

/**
 * @author Andres Almiray
 */
public interface DomainConstants extends BaseConstants {
    String GRIFFON_DOMAIN_TYPE = "griffon.plugins.domain.GriffonDomain";
    String GRIFFON_DOMAIN_HANDLER_REGISTRY_TYPE = "org.codehaus.griffon.runtime.domain.GriffonDomainHandlerRegistry";
    String METHOD_RESOLVE_FOR = "resolveFor";
    String CRITERION_TYPE = "griffon.plugins.domain.orm.Criterion";

    String METHOD_COUNT_BY = "countBy";
    String METHOD_COUNT = "count";
    String METHOD_CREATE = "create";
    String METHOD_DELETE = "delete";
    String METHOD_EXISTS = "exists";
    String METHOD_FIND_ALL_BY = "findAllBy";
    String METHOD_FIND_ALL = "findAll";
    String METHOD_FIND_ALL_WHERE = "findAllWhere";
    String METHOD_FIND_BY = "findBy";
    String METHOD_FIND = "find";
    String METHOD_FIND_OR_CREATE_BY = "findOrCreateBy";
    String METHOD_FIND_OR_CREATE_WHERE = "findOrCreateWhere";
    String METHOD_FIND_OR_SAVE_BY = "findOrSaveBy";
    String METHOD_FIND_OR_SAVE_WHERE = "findOrSaveWhere";
    String METHOD_FIND_WHERE = "findWhere";
    String METHOD_FIRST = "first";
    String METHOD_GET = "get";
    String METHOD_GET_ALL = "getAll";
    String METHOD_LAST = "last";
    String METHOD_LIST = "list";
    String METHOD_LIST_ORDER_BY = "listOrderBy";
    String METHOD_SAVE = "save";
    String METHOD_WITH_CRITERIA = "withCriteria";

    String CLAZZ = "clazz";
    String INSTANCE = "instance";
    String CLAUSE = "clause";
    String ARGUMENTS = "arguments";
    String PROPERTY_NAME = "propertyName";
    String EXAMPLE = "example";
    String PARAMS = "params";
    String OPTIONS = "options";
    String CRITERION = "criterion";

    MethodDescriptor[] METHODS = new MethodDescriptor[]{
        method(
            STATIC | PUBLIC,
            type(INT),
            METHOD_COUNT_BY,
            args(
                annotatedType(annotations(JAVAX_ANNOTATION_NONNULL), JAVA_LANG_STRING),
                annotatedType(annotations(JAVAX_ANNOTATION_NONNULL), JAVA_LANG_OBJECT, 1))
        ),
        method(
            STATIC | PUBLIC,
            type(INT),
            METHOD_COUNT_BY,
            args(
                annotatedType(annotations(JAVAX_ANNOTATION_NONNULL), JAVA_LANG_STRING),
                annotatedType(annotations(JAVAX_ANNOTATION_NONNULL), JAVA_UTIL_LIST))
        ),

        method(
            STATIC | PUBLIC,
            type(INT),
            METHOD_COUNT
        ),

        annotatedMethod(
            annotations(JAVAX_ANNOTATION_NONNULL),
            STATIC | PUBLIC,
            type(T),
            METHOD_CREATE
        ),
        annotatedMethod(
            annotations(JAVAX_ANNOTATION_NONNULL),
            STATIC | PUBLIC,
            type(T),
            METHOD_CREATE,
            args(annotatedType(annotations(JAVAX_ANNOTATION_NONNULL), JAVA_UTIL_MAP, JAVA_LANG_STRING, JAVA_LANG_OBJECT))
        ),

        annotatedMethod(
            annotations(JAVAX_ANNOTATION_NONNULL),
            type(T),
            METHOD_DELETE
        ),
        annotatedMethod(
            annotations(JAVAX_ANNOTATION_NONNULL),
            type(T),
            METHOD_DELETE,
            args(annotatedType(annotations(JAVAX_ANNOTATION_NONNULL), JAVA_UTIL_MAP, JAVA_LANG_STRING, JAVA_LANG_OBJECT))
        ),

        method(
            STATIC | PUBLIC,
            type(BOOLEAN),
            METHOD_EXISTS,
            args(annotatedType(annotations(JAVAX_ANNOTATION_NONNULL), JAVA_LANG_OBJECT))
        ),

        annotatedMethod(
            annotations(JAVAX_ANNOTATION_NONNULL),
            STATIC | PUBLIC,
            type(JAVA_UTIL_COLLECTION, T),
            METHOD_FIND_ALL_BY,
            args(
                annotatedType(annotations(JAVAX_ANNOTATION_NONNULL), JAVA_LANG_STRING),
                annotatedType(annotations(JAVAX_ANNOTATION_NONNULL), JAVA_LANG_OBJECT, 1))
        ),
        annotatedMethod(
            annotations(JAVAX_ANNOTATION_NONNULL),
            STATIC | PUBLIC,
            type(JAVA_UTIL_COLLECTION, T),
            METHOD_FIND_ALL_BY,
            args(
                annotatedType(annotations(JAVAX_ANNOTATION_NONNULL), JAVA_LANG_STRING),
                annotatedType(annotations(JAVAX_ANNOTATION_NONNULL), JAVA_UTIL_LIST))
        ),
        annotatedMethod(
            annotations(JAVAX_ANNOTATION_NONNULL),
            STATIC | PUBLIC,
            type(JAVA_UTIL_COLLECTION, T),
            METHOD_FIND_ALL_BY,
            args(
                annotatedType(annotations(JAVAX_ANNOTATION_NONNULL), JAVA_LANG_STRING),
                annotatedType(annotations(JAVAX_ANNOTATION_NONNULL), JAVA_LANG_OBJECT, 1),
                annotatedType(annotations(JAVAX_ANNOTATION_NONNULL), JAVA_UTIL_MAP, JAVA_LANG_STRING, JAVA_LANG_OBJECT))
        ),
        annotatedMethod(
            annotations(JAVAX_ANNOTATION_NONNULL),
            STATIC | PUBLIC,
            type(JAVA_UTIL_COLLECTION, T),
            METHOD_FIND_ALL_BY,
            args(
                annotatedType(annotations(JAVAX_ANNOTATION_NONNULL), JAVA_LANG_STRING),
                annotatedType(annotations(JAVAX_ANNOTATION_NONNULL), JAVA_UTIL_LIST),
                annotatedType(annotations(JAVAX_ANNOTATION_NONNULL), JAVA_UTIL_MAP, JAVA_LANG_STRING, JAVA_LANG_OBJECT))
        ),

        annotatedMethod(
            annotations(JAVAX_ANNOTATION_NONNULL),
            STATIC | PUBLIC,
            type(JAVA_UTIL_COLLECTION, T),
            METHOD_FIND_ALL,
            args(
                annotatedType(annotations(JAVAX_ANNOTATION_NONNULL), JAVA_LANG_OBJECT))
        ),
        annotatedMethod(
            annotations(JAVAX_ANNOTATION_NONNULL),
            STATIC | PUBLIC,
            type(JAVA_UTIL_COLLECTION, T),
            METHOD_FIND_ALL,
            args(
                annotatedType(annotations(JAVAX_ANNOTATION_NONNULL), JAVA_LANG_OBJECT),
                annotatedType(annotations(JAVAX_ANNOTATION_NONNULL), JAVA_UTIL_MAP, JAVA_LANG_STRING, JAVA_LANG_OBJECT))
        ),
        annotatedMethod(
            annotations(JAVAX_ANNOTATION_NONNULL),
            STATIC | PUBLIC,
            type(JAVA_UTIL_COLLECTION, T),
            METHOD_FIND_ALL,
            args(
                annotatedType(annotations(JAVAX_ANNOTATION_NONNULL), JAVA_UTIL_MAP, JAVA_LANG_STRING, JAVA_LANG_OBJECT))
        ),
        annotatedMethod(
            annotations(JAVAX_ANNOTATION_NONNULL),
            STATIC | PUBLIC,
            type(JAVA_UTIL_COLLECTION, T),
            METHOD_FIND_ALL,
            args(
                annotatedType(annotations(JAVAX_ANNOTATION_NONNULL), JAVA_UTIL_MAP, JAVA_LANG_STRING, JAVA_LANG_OBJECT),
                annotatedType(annotations(JAVAX_ANNOTATION_NONNULL), JAVA_UTIL_MAP, JAVA_LANG_STRING, JAVA_LANG_OBJECT))
        ),
        annotatedMethod(
            annotations(JAVAX_ANNOTATION_NONNULL),
            STATIC | PUBLIC,
            type(JAVA_UTIL_COLLECTION, T),
            METHOD_FIND_ALL,
            args(
                annotatedType(annotations(JAVAX_ANNOTATION_NONNULL), CRITERION_TYPE))
        ),
        annotatedMethod(
            annotations(JAVAX_ANNOTATION_NONNULL),
            STATIC | PUBLIC,
            type(JAVA_UTIL_COLLECTION, T),
            METHOD_FIND_ALL,
            args(
                annotatedType(annotations(JAVAX_ANNOTATION_NONNULL), CRITERION_TYPE),
                annotatedType(annotations(JAVAX_ANNOTATION_NONNULL), CRITERION_TYPE, JAVA_LANG_STRING, JAVA_LANG_OBJECT))
        ),

        annotatedMethod(
            annotations(JAVAX_ANNOTATION_NONNULL),
            STATIC | PUBLIC,
            type(JAVA_UTIL_COLLECTION, T),
            METHOD_FIND_ALL_WHERE,
            args(
                annotatedType(annotations(JAVAX_ANNOTATION_NONNULL), JAVA_UTIL_MAP, JAVA_LANG_STRING, JAVA_LANG_OBJECT))
        ),
        annotatedMethod(
            annotations(JAVAX_ANNOTATION_NONNULL),
            STATIC | PUBLIC,
            type(JAVA_UTIL_COLLECTION, T),
            METHOD_FIND_ALL_WHERE,
            args(
                annotatedType(annotations(JAVAX_ANNOTATION_NONNULL), JAVA_UTIL_MAP, JAVA_LANG_STRING, JAVA_LANG_OBJECT),
                annotatedType(annotations(JAVAX_ANNOTATION_NONNULL), JAVA_UTIL_MAP, JAVA_LANG_STRING, JAVA_LANG_OBJECT))
        ),

        annotatedMethod(
            annotations(JAVAX_ANNOTATION_NULLABLE),
            STATIC | PUBLIC,
            type(T),
            METHOD_FIND_BY,
            args(
                annotatedType(annotations(JAVAX_ANNOTATION_NONNULL), JAVA_LANG_STRING),
                annotatedType(annotations(JAVAX_ANNOTATION_NONNULL), JAVA_LANG_OBJECT, 1))
        ),
        annotatedMethod(
            annotations(JAVAX_ANNOTATION_NULLABLE),
            STATIC | PUBLIC,
            type(T),
            METHOD_FIND_BY,
            args(
                annotatedType(annotations(JAVAX_ANNOTATION_NONNULL), JAVA_LANG_STRING),
                annotatedType(annotations(JAVAX_ANNOTATION_NONNULL), JAVA_UTIL_LIST))
        ),

        annotatedMethod(
            annotations(JAVAX_ANNOTATION_NULLABLE),
            STATIC | PUBLIC,
            type(T),
            METHOD_FIND,
            args(
                annotatedType(annotations(JAVAX_ANNOTATION_NONNULL), JAVA_LANG_OBJECT))
        ),
        annotatedMethod(
            annotations(JAVAX_ANNOTATION_NULLABLE),
            STATIC | PUBLIC,
            type(T),
            METHOD_FIND,
            args(
                annotatedType(annotations(JAVAX_ANNOTATION_NONNULL), JAVA_UTIL_MAP, JAVA_LANG_STRING, JAVA_LANG_OBJECT))
        ),
        annotatedMethod(
            annotations(JAVAX_ANNOTATION_NULLABLE),
            STATIC | PUBLIC,
            type(T),
            METHOD_FIND,
            args(
                annotatedType(annotations(JAVAX_ANNOTATION_NONNULL), CRITERION_TYPE))
        ),

        annotatedMethod(
            annotations(JAVAX_ANNOTATION_NULLABLE),
            STATIC | PUBLIC,
            type(T),
            METHOD_FIND_OR_CREATE_BY,
            args(
                annotatedType(annotations(JAVAX_ANNOTATION_NONNULL), JAVA_LANG_STRING),
                annotatedType(annotations(JAVAX_ANNOTATION_NONNULL), JAVA_LANG_OBJECT, 1))
        ),
        annotatedMethod(
            annotations(JAVAX_ANNOTATION_NULLABLE),
            STATIC | PUBLIC,
            type(T),
            METHOD_FIND_OR_CREATE_BY,
            args(
                annotatedType(annotations(JAVAX_ANNOTATION_NONNULL), JAVA_LANG_STRING),
                annotatedType(annotations(JAVAX_ANNOTATION_NONNULL), JAVA_UTIL_LIST))
        ),

        annotatedMethod(
            annotations(JAVAX_ANNOTATION_NULLABLE),
            STATIC | PUBLIC,
            type(T),
            METHOD_FIND_OR_CREATE_WHERE,
            args(
                annotatedType(annotations(JAVAX_ANNOTATION_NONNULL), JAVA_UTIL_MAP, JAVA_LANG_STRING, JAVA_LANG_OBJECT))
        ),

        annotatedMethod(
            annotations(JAVAX_ANNOTATION_NULLABLE),
            STATIC | PUBLIC,
            type(T),
            METHOD_FIND_OR_SAVE_BY,
            args(
                annotatedType(annotations(JAVAX_ANNOTATION_NONNULL), JAVA_LANG_STRING),
                annotatedType(annotations(JAVAX_ANNOTATION_NONNULL), JAVA_LANG_OBJECT, 1))
        ),
        annotatedMethod(
            annotations(JAVAX_ANNOTATION_NULLABLE),
            STATIC | PUBLIC,
            type(T),
            METHOD_FIND_OR_SAVE_BY,
            args(
                annotatedType(annotations(JAVAX_ANNOTATION_NONNULL), JAVA_LANG_STRING),
                annotatedType(annotations(JAVAX_ANNOTATION_NONNULL), JAVA_UTIL_LIST))
        ),
        annotatedMethod(
            annotations(JAVAX_ANNOTATION_NULLABLE),
            STATIC | PUBLIC,
            type(T),
            METHOD_FIND_OR_SAVE_BY,
            args(
                annotatedType(annotations(JAVAX_ANNOTATION_NONNULL), JAVA_LANG_STRING),
                annotatedType(annotations(JAVAX_ANNOTATION_NONNULL), JAVA_LANG_OBJECT, 1),
                annotatedType(annotations(JAVAX_ANNOTATION_NONNULL), JAVA_UTIL_MAP, JAVA_LANG_STRING, JAVA_LANG_OBJECT))
        ),
        annotatedMethod(
            annotations(JAVAX_ANNOTATION_NULLABLE),
            STATIC | PUBLIC,
            type(T),
            METHOD_FIND_OR_SAVE_BY,
            args(
                annotatedType(annotations(JAVAX_ANNOTATION_NONNULL), JAVA_LANG_STRING),
                annotatedType(annotations(JAVAX_ANNOTATION_NONNULL), JAVA_UTIL_LIST),
                annotatedType(annotations(JAVAX_ANNOTATION_NONNULL), JAVA_UTIL_MAP, JAVA_LANG_STRING, JAVA_LANG_OBJECT))
        ),

        annotatedMethod(
            annotations(JAVAX_ANNOTATION_NULLABLE),
            STATIC | PUBLIC,
            type(T),
            METHOD_FIND_OR_SAVE_WHERE,
            args(
                annotatedType(annotations(JAVAX_ANNOTATION_NONNULL), JAVA_UTIL_MAP, JAVA_LANG_STRING, JAVA_LANG_OBJECT))
        ),
        annotatedMethod(
            annotations(JAVAX_ANNOTATION_NULLABLE),
            STATIC | PUBLIC,
            type(T),
            METHOD_FIND_OR_SAVE_WHERE,
            args(
                annotatedType(annotations(JAVAX_ANNOTATION_NONNULL), JAVA_UTIL_MAP, JAVA_LANG_STRING, JAVA_LANG_OBJECT),
                annotatedType(annotations(JAVAX_ANNOTATION_NONNULL), JAVA_UTIL_MAP, JAVA_LANG_STRING, JAVA_LANG_OBJECT))
        ),

        annotatedMethod(
            annotations(JAVAX_ANNOTATION_NULLABLE),
            STATIC | PUBLIC,
            type(T),
            METHOD_FIND_WHERE,
            args(
                annotatedType(annotations(JAVAX_ANNOTATION_NONNULL), JAVA_UTIL_MAP, JAVA_LANG_STRING, JAVA_LANG_OBJECT))
        ),

        annotatedMethod(
            annotations(JAVAX_ANNOTATION_NULLABLE),
            STATIC | PUBLIC,
            type(T),
            METHOD_FIRST
        ),
        annotatedMethod(
            annotations(JAVAX_ANNOTATION_NULLABLE),
            STATIC | PUBLIC,
            type(T),
            METHOD_FIRST,
            args(
                annotatedType(annotations(JAVAX_ANNOTATION_NONNULL), JAVA_LANG_STRING))
        ),
        annotatedMethod(
            annotations(JAVAX_ANNOTATION_NULLABLE),
            STATIC | PUBLIC,
            type(T),
            METHOD_FIRST,
            args(
                annotatedType(annotations(JAVAX_ANNOTATION_NONNULL), JAVA_UTIL_MAP, JAVA_LANG_STRING, JAVA_LANG_OBJECT))
        ),

        annotatedMethod(
            annotations(JAVAX_ANNOTATION_NONNULL),
            STATIC | PUBLIC,
            type(JAVA_UTIL_COLLECTION, T),
            METHOD_GET_ALL
        ),
        annotatedMethod(
            annotations(JAVAX_ANNOTATION_NONNULL),
            STATIC | PUBLIC,
            type(JAVA_UTIL_COLLECTION, T),
            METHOD_GET_ALL,
            args(
                annotatedType(annotations(JAVAX_ANNOTATION_NONNULL), JAVA_LANG_OBJECT, 1))
        ),
        annotatedMethod(
            annotations(JAVAX_ANNOTATION_NONNULL),
            STATIC | PUBLIC,
            type(JAVA_UTIL_COLLECTION, T),
            METHOD_GET_ALL,
            args(
                annotatedType(annotations(JAVAX_ANNOTATION_NONNULL), JAVA_UTIL_LIST))
        ),

        annotatedMethod(
            annotations(JAVAX_ANNOTATION_NULLABLE),
            STATIC | PUBLIC,
            type(T),
            METHOD_GET,
            args(
                annotatedType(annotations(JAVAX_ANNOTATION_NONNULL), JAVA_LANG_OBJECT))
        ),

        annotatedMethod(
            annotations(JAVAX_ANNOTATION_NULLABLE),
            STATIC | PUBLIC,
            type(T),
            METHOD_LAST
        ),
        annotatedMethod(
            annotations(JAVAX_ANNOTATION_NULLABLE),
            STATIC | PUBLIC,
            type(T),
            METHOD_LAST,
            args(
                annotatedType(annotations(JAVAX_ANNOTATION_NONNULL), JAVA_LANG_STRING))
        ),
        annotatedMethod(
            annotations(JAVAX_ANNOTATION_NULLABLE),
            STATIC | PUBLIC,
            type(T),
            METHOD_LAST,
            args(
                annotatedType(annotations(JAVAX_ANNOTATION_NONNULL), JAVA_UTIL_MAP, JAVA_LANG_STRING, JAVA_LANG_OBJECT))
        ),

        annotatedMethod(
            annotations(JAVAX_ANNOTATION_NONNULL),
            STATIC | PUBLIC,
            type(JAVA_UTIL_COLLECTION, T),
            METHOD_LIST
        ),
        annotatedMethod(
            annotations(JAVAX_ANNOTATION_NONNULL),
            STATIC | PUBLIC,
            type(JAVA_UTIL_COLLECTION, T),
            METHOD_LIST,
            args(
                annotatedType(annotations(JAVAX_ANNOTATION_NONNULL), JAVA_UTIL_MAP, JAVA_LANG_STRING, JAVA_LANG_OBJECT))
        ),

        annotatedMethod(
            annotations(JAVAX_ANNOTATION_NONNULL),
            STATIC | PUBLIC,
            type(JAVA_UTIL_COLLECTION, T),
            METHOD_LIST_ORDER_BY,
            args(
                annotatedType(annotations(JAVAX_ANNOTATION_NONNULL), JAVA_LANG_STRING))
        ),
        annotatedMethod(
            annotations(JAVAX_ANNOTATION_NONNULL),
            STATIC | PUBLIC,
            type(JAVA_UTIL_COLLECTION, T),
            METHOD_LIST_ORDER_BY,
            args(
                annotatedType(annotations(JAVAX_ANNOTATION_NONNULL), JAVA_LANG_STRING),
                annotatedType(annotations(JAVAX_ANNOTATION_NONNULL), JAVA_UTIL_MAP, JAVA_LANG_STRING, JAVA_LANG_OBJECT))
        ),

        annotatedMethod(
            annotations(JAVAX_ANNOTATION_NONNULL),
            type(T),
            METHOD_SAVE
        ),
        annotatedMethod(
            annotations(JAVAX_ANNOTATION_NONNULL),
            type(T),
            METHOD_SAVE,
            args(annotatedType(annotations(JAVAX_ANNOTATION_NONNULL), JAVA_UTIL_MAP, JAVA_LANG_STRING, JAVA_LANG_OBJECT))
        ),

        annotatedMethod(
            annotations(JAVAX_ANNOTATION_NONNULL),
            STATIC | PUBLIC,
            type(JAVA_UTIL_COLLECTION, T),
            METHOD_WITH_CRITERIA,
            args(
                annotatedType(annotations(JAVAX_ANNOTATION_NONNULL), CRITERION_TYPE))
        ),
        annotatedMethod(
            annotations(JAVAX_ANNOTATION_NONNULL),
            STATIC | PUBLIC,
            type(JAVA_UTIL_COLLECTION, T),
            METHOD_WITH_CRITERIA,
            args(
                annotatedType(annotations(JAVAX_ANNOTATION_NONNULL), CRITERION_TYPE),
                annotatedType(annotations(JAVAX_ANNOTATION_NONNULL), JAVA_UTIL_MAP, JAVA_LANG_STRING, JAVA_LANG_OBJECT))
        )
    };
}
