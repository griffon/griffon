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
package lombok.intellij.processor.clazz;

import com.intellij.psi.*;
import de.plushnikov.intellij.lombok.processor.LombokProcessor;
import de.plushnikov.intellij.lombok.processor.clazz.LombokClassProcessor;
import de.plushnikov.intellij.lombok.psi.LombokLightMethodBuilder;
import de.plushnikov.intellij.lombok.psi.LombokPsiElementFactory;
import de.plushnikov.intellij.lombok.util.PsiPrimitiveTypeFactory;
import griffon.transform.EventPublisher;
import org.codehaus.griffon.compile.core.EventPublisherConstants;
import org.jetbrains.annotations.NotNull;
import org.kordamp.jipsy.ServiceProviderFor;

import java.util.List;

/**
 * @author Andres Almiray
 */
@ServiceProviderFor({LombokProcessor.class, LombokClassProcessor.class})
public class EventPublisherProcessor extends AbstractGriffonLombokClassProcessor implements EventPublisherConstants {
    public EventPublisherProcessor() {
        super(EventPublisher.class, PsiMethod.class);
    }

    protected <PSI extends PsiElement> void processIntern(@NotNull PsiClass psiClass, @NotNull PsiAnnotation psiAnnotation, @NotNull List<PSI> target) {
        PsiElementFactory psiElementFactory = psiElementFactory(psiClass);
        PsiManager manager = psiClass.getContainingFile().getManager();

        PsiType psiEventRouterType = psiElementFactory.createTypeFromText(EVENT_ROUTER_TYPE, psiClass);

        LombokLightMethodBuilder method = LombokPsiElementFactory.getInstance().createLightMethod(psiClass.getManager(), METHOD_SET_EVENT_ROUTER)
            .withMethodReturnType(PsiPrimitiveTypeFactory.getInstance().getVoidType())
            .withContainingClass(psiClass)
            .withParameter(EVENT_ROUTER_PROPERTY, psiEventRouterType)
            .withModifier(PsiModifier.PUBLIC)
            .withNavigationElement(psiAnnotation);
        target.add((PSI) method);

        delegateTo(psiClass, psiAnnotation, target, METHODS);
    }
}
