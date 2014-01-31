/*
 * Copyright 2009-2013 the original author or authors.
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
import griffon.transform.ThreadingAware;
import lombok.core.AnnotationValues;
import lombok.core.handlers.ThreadingAwareHandler;
import lombok.javac.JavacAnnotationHandler;
import lombok.javac.JavacNode;
import lombok.javac.handlers.ast.JavacType;
import org.codehaus.griffon.core.compile.ThreadingAwareConstants;
import org.kordamp.jipsy.ServiceProviderFor;

import static lombok.core.util.ErrorMessages.canBeUsedOnClassAndEnumOnly;
import static lombok.javac.handlers.JavacHandlerUtil.deleteAnnotationIfNeccessary;

/**
 * @author Andres Almiray
 */
@ServiceProviderFor(JavacAnnotationHandler.class)
public class HandleThreadingAware extends JavacAnnotationHandler<ThreadingAware> {
    private final JavacThreadingAwareHandler handler = new JavacThreadingAwareHandler();

    public void handle(AnnotationValues<ThreadingAware> annotation, JCTree.JCAnnotation source, JavacNode annotationNode) {
        deleteAnnotationIfNeccessary(annotationNode, ThreadingAware.class);

        JavacType type = JavacType.typeOf(annotationNode, source);
        if (type.isAnnotation() || type.isInterface()) {
            annotationNode.addError(canBeUsedOnClassAndEnumOnly(ThreadingAware.class));
            return;
        }

        JavacUtil.addInterface(type.node(), ThreadingAwareConstants.THREADING_HANDLER_TYPE);
        handler.addThreadingHandlingSupport(type);
        type.editor().rebuild();
    }

    private static class JavacThreadingAwareHandler extends ThreadingAwareHandler<JavacType> {
    }
}
