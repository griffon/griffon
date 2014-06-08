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

import griffon.transform.EventPublisher;
import lombok.ast.Argument;
import lombok.ast.IMethod;
import lombok.ast.IType;
import lombok.ast.MethodDecl;
import org.codehaus.griffon.compile.core.EventPublisherConstants;

import static java.util.Arrays.asList;
import static lombok.ast.AST.*;
import static lombok.ast.AST.String;

/**
 * @author Andres Almiray
 */
public abstract class EventPublisherHandler<TYPE_TYPE extends IType<? extends IMethod<?, ?, ?, ?>, ?, ?, ?, ?, ?>> extends AbstractHandler<TYPE_TYPE> implements EventPublisherConstants {
    public void addEventPublisherSupport(final TYPE_TYPE type, final EventPublisher annotation) {
        type.editor().injectField(
            FieldDecl(Type(EVENT_ROUTER_TYPE), EVENT_ROUTER_FIELD_NAME)
                .makePrivate()
        );

        String beanName = annotation.value();
        Argument arg = Arg(Type(EVENT_ROUTER_TYPE), EVENT_ROUTER_PROPERTY);
        if (beanName != null && beanName.trim().length() > 0) {
            arg = arg.withAnnotation(Annotation(Type(JAVAX_INJECT_NAMED)).withValue(String(beanName)));
        }

        MethodDecl methodDecl = MethodDecl(Type(VOID), getSetterName(EVENT_ROUTER_PROPERTY))
            .makePrivate()
            .withArguments(asList(arg))
            .withAnnotation(Annotation(Type(JAVAX_INJECT_INJECT)))
            .withStatement(Assign(Field(EVENT_ROUTER_FIELD_NAME), Name(EVENT_ROUTER_PROPERTY)));
        type.editor().injectMethod(methodDecl);

        delegateMethodsTo(type, METHODS, Field(EVENT_ROUTER_FIELD_NAME));
    }
}
