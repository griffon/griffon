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
package lombok.javac.handlers;

import com.sun.tools.javac.tree.JCTree;
import griffon.transform.EventPublisher;
import lombok.core.AnnotationValues;
import lombok.core.handlers.EventPublisherHandler;
import lombok.javac.JavacAnnotationHandler;
import lombok.javac.JavacNode;
import lombok.javac.handlers.ast.JavacType;
import org.codehaus.griffon.compile.core.EventPublisherConstants;

import static lombok.core.util.ErrorMessages.canBeUsedOnClassAndEnumOnly;
import static lombok.javac.handlers.JavacHandlerUtil.createAnnotation;
import static lombok.javac.handlers.JavacHandlerUtil.deleteAnnotationIfNeccessary;

/**
 * @author Andres Almiray
 */
// @ServiceProviderFor(JavacAnnotationHandler.class)
public class HandleEventPublisher extends JavacAnnotationHandler<EventPublisher> {
    private final JavacEventPublisherHandler handler = new JavacEventPublisherHandler();

    @Override
    public void handle(AnnotationValues<EventPublisher> annotation, final JCTree.JCAnnotation source, final JavacNode annotationNode) {
        deleteAnnotationIfNeccessary(annotationNode, EventPublisher.class);

        JavacType type = JavacType.typeOf(annotationNode, source);
        if (type.isAnnotation() || type.isInterface()) {
            annotationNode.addError(canBeUsedOnClassAndEnumOnly(EventPublisher.class));
            return;
        }

        JavacNode eventPublisherNode = type.getAnnotation(EventPublisher.class);
        AnnotationValues<EventPublisher> eventPublisherAnnotation = createAnnotation(EventPublisher.class, eventPublisherNode);

        JavacUtil.addInterface(type.node(), EventPublisherConstants.EVENT_PUBLISHER_TYPE);
        handler.addEventPublisherSupport(type, eventPublisherAnnotation.getInstance());
        type.editor().rebuild();
    }

    private static class JavacEventPublisherHandler extends EventPublisherHandler<JavacType> {

    }
}
