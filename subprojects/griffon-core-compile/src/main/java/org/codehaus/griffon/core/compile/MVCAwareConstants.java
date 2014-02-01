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
package org.codehaus.griffon.core.compile;

import static org.codehaus.griffon.core.compile.MethodDescriptor.*;

/**
 * @author Andres Almiray
 */
public interface MVCAwareConstants extends BaseConstants {
    String MVC_GROUP_MANAGER_PROPERTY = "mvcGroupManager";
    String MVC_CALLABLE_TYPE = "griffon.core.mvc.MVCCallable";
    String GRIFFON_MODEL_TYPE = "griffon.core.artifact.GriffonModel";
    String GRIFFON_VIEW_TYPE = "griffon.core.artifact.GriffonView";
    String GRIFFON_CONTROLLER_TYPE = "griffon.core.artifact.GriffonController";
    String MVC_GROUP = "griffon.core.mvc.MVCGroup";
    String MVC_HANDLER_TYPE = "griffon.core.mvc.MVCHandler";
    String MVC_GROUP_MANAGER_TYPE = "griffon.core.mvc.MVCGroupManager";
    String GRIFFON_MVC_ARTIFACT_TYPE = "griffon.core.artifact.GriffonMvcArtifact";

    String METHOD_CREATE_MVC_GROUP = "createMVCGroup";
    String METHOD_BUILD_MVC_GROUP = "buildMVCGroup";
    String METHOD_WITH_MVC_GROUP = "withMVCGroup";
    String METHOD_DESTROY_MVC_GROUP = "destroyMVCGroup";

    String M = "M";
    String V = "V";
    String C = "C";

    MethodDescriptor[] METHODS = new MethodDescriptor[]{
        method(
            type(VOID),
            METHOD_DESTROY_MVC_GROUP,
            args(type(types(type(JAVAX_ANNOTATION_NONNULL)), JAVA_LANG_STRING))
        ),
        annotatedMethod(
            types(type(JAVAX_ANNOTATION_NONNULL)),
            type(MVC_GROUP),
            METHOD_BUILD_MVC_GROUP,
            args(type(types(type(JAVAX_ANNOTATION_NONNULL)), JAVA_LANG_STRING))
        ),
        annotatedMethod(
            types(type(JAVAX_ANNOTATION_NONNULL)),
            type(MVC_GROUP),
            METHOD_BUILD_MVC_GROUP,
            args(type(types(type(JAVAX_ANNOTATION_NONNULL)), JAVA_LANG_STRING), type(JAVA_LANG_STRING))
        ),
        annotatedMethod(
            types(type(JAVAX_ANNOTATION_NONNULL)),
            type(MVC_GROUP),
            METHOD_BUILD_MVC_GROUP,
            args(
                type(types(type(JAVAX_ANNOTATION_NONNULL)), JAVA_UTIL_MAP, JAVA_LANG_STRING, JAVA_LANG_OBJECT),
                type(types(type(JAVAX_ANNOTATION_NONNULL)), JAVA_LANG_STRING))
        ),
        annotatedMethod(
            types(type(JAVAX_ANNOTATION_NONNULL)),
            type(MVC_GROUP),
            METHOD_BUILD_MVC_GROUP,
            args(
                type(types(type(JAVAX_ANNOTATION_NONNULL)), JAVA_LANG_STRING),
                type(types(type(JAVAX_ANNOTATION_NONNULL)), JAVA_UTIL_MAP, JAVA_LANG_STRING, JAVA_LANG_OBJECT))
        ),
        annotatedMethod(
            types(type(JAVAX_ANNOTATION_NONNULL)),
            type(MVC_GROUP),
            METHOD_BUILD_MVC_GROUP,
            args(
                type(types(type(JAVAX_ANNOTATION_NONNULL)), JAVA_UTIL_MAP, JAVA_LANG_STRING, JAVA_LANG_OBJECT),
                type(types(type(JAVAX_ANNOTATION_NONNULL)), JAVA_LANG_STRING),
                type(types(type(JAVAX_ANNOTATION_NONNULL)), JAVA_LANG_STRING))
        ),
        annotatedMethod(
            types(type(JAVAX_ANNOTATION_NONNULL)),
            type(MVC_GROUP),
            METHOD_BUILD_MVC_GROUP,
            args(
                type(types(type(JAVAX_ANNOTATION_NONNULL)), JAVA_LANG_STRING),
                type(types(type(JAVAX_ANNOTATION_NONNULL)), JAVA_LANG_STRING),
                type(types(type(JAVAX_ANNOTATION_NONNULL)), JAVA_UTIL_MAP, JAVA_LANG_STRING, JAVA_LANG_OBJECT))
        ),

        annotatedMethod(
            types(type(JAVAX_ANNOTATION_NONNULL)),
            typeWithParams(JAVA_UTIL_LIST, wildcard(GRIFFON_MVC_ARTIFACT_TYPE)),
            METHOD_CREATE_MVC_GROUP,
            args(type(types(type(JAVAX_ANNOTATION_NONNULL)), JAVA_LANG_STRING))
        ),
        annotatedMethod(
            types(type(JAVAX_ANNOTATION_NONNULL)),
            typeWithParams(JAVA_UTIL_LIST, wildcard(GRIFFON_MVC_ARTIFACT_TYPE)),
            METHOD_CREATE_MVC_GROUP,
            args(
                type(types(type(JAVAX_ANNOTATION_NONNULL)), JAVA_UTIL_MAP, JAVA_LANG_STRING, JAVA_LANG_OBJECT),
                type(types(type(JAVAX_ANNOTATION_NONNULL)), JAVA_LANG_STRING))
        ),
        annotatedMethod(
            types(type(JAVAX_ANNOTATION_NONNULL)),
            typeWithParams(JAVA_UTIL_LIST, wildcard(GRIFFON_MVC_ARTIFACT_TYPE)),
            METHOD_CREATE_MVC_GROUP,
            args(
                type(types(type(JAVAX_ANNOTATION_NONNULL)), JAVA_LANG_STRING),
                type(types(type(JAVAX_ANNOTATION_NONNULL)), JAVA_UTIL_MAP, JAVA_LANG_STRING, JAVA_LANG_OBJECT))
        ),
        annotatedMethod(
            types(type(JAVAX_ANNOTATION_NONNULL)),
            typeWithParams(JAVA_UTIL_LIST, wildcard(GRIFFON_MVC_ARTIFACT_TYPE)),
            METHOD_CREATE_MVC_GROUP,
            args(
                type(types(type(JAVAX_ANNOTATION_NONNULL)), JAVA_LANG_STRING),
                type(types(type(JAVAX_ANNOTATION_NONNULL)), JAVA_LANG_STRING))
        ),
        annotatedMethod(
            types(type(JAVAX_ANNOTATION_NONNULL)),
            typeWithParams(JAVA_UTIL_LIST, wildcard(GRIFFON_MVC_ARTIFACT_TYPE)),
            METHOD_CREATE_MVC_GROUP,
            args(
                type(types(type(JAVAX_ANNOTATION_NONNULL)), JAVA_UTIL_MAP, JAVA_LANG_STRING, JAVA_LANG_OBJECT),
                type(types(type(JAVAX_ANNOTATION_NONNULL)), JAVA_LANG_STRING),
                type(types(type(JAVAX_ANNOTATION_NONNULL)), JAVA_LANG_STRING))
        ),
        annotatedMethod(
            types(type(JAVAX_ANNOTATION_NONNULL)),
            typeWithParams(JAVA_UTIL_LIST, wildcard(GRIFFON_MVC_ARTIFACT_TYPE)),
            METHOD_CREATE_MVC_GROUP,
            args(
                type(types(type(JAVAX_ANNOTATION_NONNULL)), JAVA_LANG_STRING),
                type(types(type(JAVAX_ANNOTATION_NONNULL)), JAVA_LANG_STRING),
                type(types(type(JAVAX_ANNOTATION_NONNULL)), JAVA_UTIL_MAP, JAVA_LANG_STRING, JAVA_LANG_OBJECT))
        ),

        method(
            type(VOID),
            typeParams(
                typeParam(M, GRIFFON_MODEL_TYPE),
                typeParam(V, GRIFFON_VIEW_TYPE),
                typeParam(C, GRIFFON_CONTROLLER_TYPE)
            ),
            METHOD_WITH_MVC_GROUP,
            args(
                type(types(type(JAVAX_ANNOTATION_NONNULL)), JAVA_LANG_STRING),
                type(types(type(JAVAX_ANNOTATION_NONNULL)), MVC_CALLABLE_TYPE, M, V, C))
        ),
        method(
            type(VOID),
            typeParams(
                typeParam(M, GRIFFON_MODEL_TYPE),
                typeParam(V, GRIFFON_VIEW_TYPE),
                typeParam(C, GRIFFON_CONTROLLER_TYPE)
            ),
            METHOD_WITH_MVC_GROUP,
            args(
                type(types(type(JAVAX_ANNOTATION_NONNULL)), JAVA_LANG_STRING),
                type(types(type(JAVAX_ANNOTATION_NONNULL)), JAVA_LANG_STRING),
                type(types(type(JAVAX_ANNOTATION_NONNULL)), MVC_CALLABLE_TYPE, M, V, C))
        ),
        method(
            type(VOID),
            typeParams(
                typeParam(M, GRIFFON_MODEL_TYPE),
                typeParam(V, GRIFFON_VIEW_TYPE),
                typeParam(C, GRIFFON_CONTROLLER_TYPE)
            ),
            METHOD_WITH_MVC_GROUP,
            args(
                type(types(type(JAVAX_ANNOTATION_NONNULL)), JAVA_LANG_STRING),
                type(types(type(JAVAX_ANNOTATION_NONNULL)), JAVA_LANG_STRING),
                type(types(type(JAVAX_ANNOTATION_NONNULL)), JAVA_UTIL_MAP, JAVA_LANG_STRING, JAVA_LANG_OBJECT),
                type(types(type(JAVAX_ANNOTATION_NONNULL)), MVC_CALLABLE_TYPE, M, V, C))
        ),
        method(
            type(VOID),
            typeParams(
                typeParam(M, GRIFFON_MODEL_TYPE),
                typeParam(V, GRIFFON_VIEW_TYPE),
                typeParam(C, GRIFFON_CONTROLLER_TYPE)
            ),
            METHOD_WITH_MVC_GROUP,
            args(
                type(types(type(JAVAX_ANNOTATION_NONNULL)), JAVA_UTIL_MAP, JAVA_LANG_STRING, JAVA_LANG_OBJECT),
                type(types(type(JAVAX_ANNOTATION_NONNULL)), JAVA_LANG_STRING),
                type(types(type(JAVAX_ANNOTATION_NONNULL)), JAVA_LANG_STRING),
                type(types(type(JAVAX_ANNOTATION_NONNULL)), MVC_CALLABLE_TYPE, M, V, C))
        ),
        method(
            type(VOID),
            typeParams(
                typeParam(M, GRIFFON_MODEL_TYPE),
                typeParam(V, GRIFFON_VIEW_TYPE),
                typeParam(C, GRIFFON_CONTROLLER_TYPE)
            ),
            METHOD_WITH_MVC_GROUP,
            args(
                type(types(type(JAVAX_ANNOTATION_NONNULL)), JAVA_LANG_STRING),
                type(types(type(JAVAX_ANNOTATION_NONNULL)), JAVA_UTIL_MAP, JAVA_LANG_STRING, JAVA_LANG_OBJECT),
                type(types(type(JAVAX_ANNOTATION_NONNULL)), MVC_CALLABLE_TYPE, M, V, C))
        ),
        method(
            type(VOID),
            typeParams(
                typeParam(M, GRIFFON_MODEL_TYPE),
                typeParam(V, GRIFFON_VIEW_TYPE),
                typeParam(C, GRIFFON_CONTROLLER_TYPE)
            ),
            METHOD_WITH_MVC_GROUP,
            args(
                type(types(type(JAVAX_ANNOTATION_NONNULL)), JAVA_UTIL_MAP, JAVA_LANG_STRING, JAVA_LANG_OBJECT),
                type(types(type(JAVAX_ANNOTATION_NONNULL)), JAVA_LANG_STRING),
                type(types(type(JAVAX_ANNOTATION_NONNULL)), MVC_CALLABLE_TYPE, M, V, C))
        )
    };
}
