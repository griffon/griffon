/*
 * Copyright 2012-2014 the original author or authors.
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
import static org.codehaus.griffon.core.compile.MethodDescriptor.typeParams;

/**
 * @author Andres Almiray
 */
public interface ThreadingAwareConstants extends BaseConstants {
    String THREADING_HANDLER_TYPE = "griffon.core.threading.ThreadingHandler";
    String UITHREAD_MANAGER_PROPERTY = "uiThreadManager";

    String METHOD_IS_UITHREAD = "isUIThread";
    String METHOD_RUN_INSIDE_UI_ASYNC = "runInsideUIAsync";
    String METHOD_RUN_INSIDE_UI_SYNC = "runInsideUISync";
    String METHOD_RUN_OUTSIDE_UI = "runOutsideUI";
    String METHOD_RUN_FUTURE = "runFuture";

    String JAVA_UTIL_CONCURRENT_FUTURE = "java.util.concurrent.Future";
    String JAVA_UTIL_CONCURRENT_CALLABLE = "java.util.concurrent.Callable";
    String JAVA_UTIL_CONCURRENT_EXECUTOR_SERVICE = "java.util.concurrent.ExecutorService";

    MethodDescriptor[] METHODS = new MethodDescriptor[]{
        MethodDescriptor.method(
            type(BOOLEAN),
            METHOD_IS_UITHREAD
        ),
        MethodDescriptor.method(
            type(VOID),
            METHOD_RUN_INSIDE_UI_ASYNC,
            args(type(JAVA_LANG_RUNNABLE))
        ),
        MethodDescriptor.method(
            type(VOID),
            METHOD_RUN_INSIDE_UI_SYNC,
            args(type(JAVA_LANG_RUNNABLE))
        ),
        MethodDescriptor.method(
            type(VOID),
            METHOD_RUN_OUTSIDE_UI,
            args(type(JAVA_LANG_RUNNABLE))
        ),
        MethodDescriptor.method(
            type(JAVA_UTIL_CONCURRENT_FUTURE, R),
            typeParams(R),
            METHOD_RUN_FUTURE,
            args(type(JAVA_UTIL_CONCURRENT_EXECUTOR_SERVICE), type(JAVA_UTIL_CONCURRENT_CALLABLE, R))
        ),
        MethodDescriptor.method(
            type(JAVA_UTIL_CONCURRENT_FUTURE, R),
            typeParams(R),
            METHOD_RUN_FUTURE,
            args(type(JAVA_UTIL_CONCURRENT_CALLABLE, R))
        )
    };
}
