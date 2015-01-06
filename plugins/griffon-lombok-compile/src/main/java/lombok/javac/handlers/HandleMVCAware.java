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
package lombok.javac.handlers;

import com.sun.tools.javac.tree.JCTree;
import griffon.transform.MVCAware;
import lombok.core.AnnotationValues;
import lombok.core.handlers.MVCAwareHandler;
import lombok.javac.JavacAnnotationHandler;
import lombok.javac.JavacNode;
import lombok.javac.handlers.ast.JavacType;
import org.codehaus.griffon.compile.core.MVCAwareConstants;
import org.kordamp.jipsy.ServiceProviderFor;

import static lombok.core.util.ErrorMessages.canBeUsedOnClassAndEnumOnly;
import static lombok.javac.handlers.JavacHandlerUtil.deleteAnnotationIfNeccessary;

/**
 * @author Andres Almiray
 */
@ServiceProviderFor(JavacAnnotationHandler.class)
public class HandleMVCAware extends JavacAnnotationHandler<MVCAware> {
    private final JavacMVCAwareHandler handler = new JavacMVCAwareHandler();

    @Override
    public void handle(AnnotationValues<MVCAware> annotation, final JCTree.JCAnnotation source, final JavacNode annotationNode) {
        deleteAnnotationIfNeccessary(annotationNode, MVCAware.class);

        JavacType type = JavacType.typeOf(annotationNode, source);
        if (type.isAnnotation() || type.isInterface()) {
            annotationNode.addError(canBeUsedOnClassAndEnumOnly(MVCAware.class));
            return;
        }
        JavacUtil.addInterface(type.node(), MVCAwareConstants.MVC_HANDLER_TYPE);
        handler.addMVCSupport(type);
        type.editor().rebuild();
    }

    private static class JavacMVCAwareHandler extends MVCAwareHandler<JavacType> {
    }
}
