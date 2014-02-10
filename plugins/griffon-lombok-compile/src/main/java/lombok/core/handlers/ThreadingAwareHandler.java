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
package lombok.core.handlers;

import lombok.ast.IMethod;
import lombok.ast.IType;
import org.codehaus.griffon.compile.core.ThreadingAwareConstants;

import static lombok.ast.AST.*;

/**
 * @author Andres Almiray
 */
public abstract class ThreadingAwareHandler<TYPE_TYPE extends IType<? extends IMethod<?, ?, ?, ?>, ?, ?, ?, ?, ?>> extends AbstractHandler<TYPE_TYPE> implements ThreadingAwareConstants {
    public void addThreadingHandlingSupport(final TYPE_TYPE type) {
        type.editor().injectField(
            FieldDecl(Type(THREADING_HANDLER_TYPE), UITHREAD_MANAGER_PROPERTY)
                .makePrivate()
                .withAnnotation(Annotation(Type(JAVAX_INJECT_INJECT)))
        );

        delegateMethodsTo(type, METHODS, Field(UITHREAD_MANAGER_PROPERTY));
    }
}
