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
package lombok.eclipse.handlers;

import griffon.transform.ResourcesAware;
import lombok.core.AnnotationValues;
import lombok.core.handlers.ResourcesAwareHandler;
import lombok.eclipse.EclipseAnnotationHandler;
import lombok.eclipse.EclipseNode;
import lombok.eclipse.handlers.ast.EclipseType;
import org.codehaus.griffon.compile.core.ResourcesAwareConstants;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.kordamp.jipsy.ServiceProviderFor;

import static lombok.core.util.ErrorMessages.canBeUsedOnClassAndEnumOnly;

/**
 * @author Andres Almiray
 */
@ServiceProviderFor(EclipseAnnotationHandler.class)
public class HandleResourcesAware extends EclipseAnnotationHandler<ResourcesAware> {
    private final EclipseResourcesAwareHandler handler = new EclipseResourcesAwareHandler();

    @Override
    public void handle(AnnotationValues<ResourcesAware> annotation, Annotation source, EclipseNode annotationNode) {
        EclipseType type = EclipseType.typeOf(annotationNode, source);
        if (type.isAnnotation() || type.isInterface()) {
            annotationNode.addError(canBeUsedOnClassAndEnumOnly(ResourcesAware.class));
            return;
        }

        EclipseUtil.addInterface(type.get(), ResourcesAwareConstants.RESOURCE_HANDLER_TYPE, source);
        handler.addResourceHandlerSupport(type);
        type.editor().rebuild();
    }

    private static class EclipseResourcesAwareHandler extends ResourcesAwareHandler<EclipseType> {
    }
}
