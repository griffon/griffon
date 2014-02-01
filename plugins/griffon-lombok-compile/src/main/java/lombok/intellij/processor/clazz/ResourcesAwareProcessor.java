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
package lombok.intellij.processor.clazz;

import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import de.plushnikov.intellij.lombok.processor.LombokProcessor;
import de.plushnikov.intellij.lombok.processor.clazz.LombokClassProcessor;
import griffon.transform.ResourcesAware;
import org.codehaus.griffon.core.compile.ResourcesAwareConstants;
import org.jetbrains.annotations.NotNull;
import org.kordamp.jipsy.ServiceProviderFor;

import java.util.List;

/**
 * @author Andres Almiray
 */
@ServiceProviderFor({LombokProcessor.class, LombokClassProcessor.class})
public class ResourcesAwareProcessor extends AbstractGriffonLombokClassProcessor implements ResourcesAwareConstants {
    public ResourcesAwareProcessor() {
        super(ResourcesAware.class, PsiMethod.class);
    }

    protected <Psi extends PsiElement> void processIntern(@NotNull PsiClass psiClass, @NotNull PsiAnnotation psiAnnotation, @NotNull List<Psi> target) {
        delegateTo(psiClass, psiAnnotation, target, METHODS);
    }
}
