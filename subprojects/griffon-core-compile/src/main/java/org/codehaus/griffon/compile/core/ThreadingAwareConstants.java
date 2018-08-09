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
import static org.codehaus.griffon.compile.core.MethodDescriptor.typeParams;

/**
 * @author Andres Almiray
 */
public interface ThreadingAwareConstants extends BaseConstants {
    String THREADING_HANDLER_TYPE = "griffon.core.threading.ThreadingHandler";
    String UITHREAD_MANAGER_PROPERTY = "uiThreadManager";

    String METHOD_IS_UITHREAD = "isUIThread";
    String METHOD_EXECUTE_INSIDE_UI_ASYNC = "executeInsideUIAsync";
    String METHOD_EXECUTE_INSIDE_UI_SYNC = "executeInsideUISync";
    String METHOD_EXECUTE_OUTSIDE_UI = "executeOutsideUI";
    String METHOD_EXECUTE_OUTSIDE_UI_ASYNC = "executeOutsideUIAsync";
    String METHOD_EXECUTE_FUTURE = "executeFuture";

    String JAVA_UTIL_CONCURRENT_FUTURE = "java.util.concurrent.Future";
    String JAVA_UTIL_CONCURRENT_CALLABLE = "java.util.concurrent.Callable";
    String JAVA_UTIL_CONCURRENT_EXECUTOR_SERVICE = "java.util.concurrent.ExecutorService";

    MethodDescriptor[] METHODS = new MethodDescriptor[]{
        method(
            type(BOOLEAN),
            METHOD_IS_UITHREAD
        ),
        method(
            type(VOID),
            METHOD_EXECUTE_INSIDE_UI_ASYNC,
            args(annotatedType(annotations(ANNOTATION_NONNULL), JAVA_LANG_RUNNABLE))
        ),
        method(
            type(VOID),
            METHOD_EXECUTE_INSIDE_UI_SYNC,
            args(annotatedType(annotations(ANNOTATION_NONNULL), JAVA_LANG_RUNNABLE))
        ),
        method(
            type(VOID),
            METHOD_EXECUTE_OUTSIDE_UI,
            args(annotatedType(annotations(ANNOTATION_NONNULL), JAVA_LANG_RUNNABLE))
        ),
        method(
            type(VOID),
            METHOD_EXECUTE_OUTSIDE_UI_ASYNC,
            args(annotatedType(annotations(ANNOTATION_NONNULL), JAVA_LANG_RUNNABLE))
        ),
        annotatedMethod(
            annotations(ANNOTATION_NONNULL),
            type(JAVA_UTIL_CONCURRENT_FUTURE, R),
            typeParams(R),
            METHOD_EXECUTE_FUTURE,
            args(
                annotatedType(annotations(ANNOTATION_NONNULL), JAVA_UTIL_CONCURRENT_EXECUTOR_SERVICE),
                annotatedType(annotations(ANNOTATION_NONNULL), JAVA_UTIL_CONCURRENT_CALLABLE, R))
        ),
        annotatedMethod(
            annotations(ANNOTATION_NONNULL),
            type(JAVA_UTIL_CONCURRENT_FUTURE, R),
            typeParams(R),
            METHOD_EXECUTE_FUTURE,
            args(annotatedType(annotations(ANNOTATION_NONNULL), JAVA_UTIL_CONCURRENT_CALLABLE, R))
        ),
        annotatedMethod(
            annotations(ANNOTATION_NULLABLE),
            type(R),
            typeParams(R),
            METHOD_EXECUTE_INSIDE_UI_SYNC,
            args(
                annotatedType(annotations(ANNOTATION_NONNULL), JAVA_UTIL_CONCURRENT_CALLABLE, R))
        )
    };
}
