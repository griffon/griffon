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
 */
package lombok.intellij.processor.clazz;

import com.intellij.psi.*;
import de.plushnikov.intellij.lombok.processor.LombokProcessor;
import de.plushnikov.intellij.lombok.processor.clazz.LombokClassProcessor;
import de.plushnikov.intellij.lombok.util.PsiFieldUtil;
import griffon.transform.Vetoable;
import org.codehaus.griffon.core.compile.VetoableConstants;
import org.jetbrains.annotations.NotNull;
import org.kordamp.jipsy.ServiceProviderFor;

import java.util.Collection;
import java.util.List;

/**
 * @author Andres Almiray
 */
@ServiceProviderFor({LombokProcessor.class, LombokClassProcessor.class})
public class VetoableProcessor extends AbstractGriffonLombokClassProcessor implements VetoableConstants {
    public VetoableProcessor() {
        super(Vetoable.class, PsiMethod.class);
    }

    protected <Psi extends PsiElement> void processIntern(@NotNull PsiClass psiClass, @NotNull PsiAnnotation psiAnnotation, @NotNull List<Psi> target) {
        delegateTo(psiClass, psiAnnotation, target, VETOABLE_METHODS);
        delegateTo(psiClass, psiAnnotation, target, VETOABLE_FIRE_METHODS);
        delegateTo(psiClass, psiAnnotation, target, OBSERVABLE_METHODS);
        delegateTo(psiClass, psiAnnotation, target, OBSERVABLE_FIRE_METHODS);

        Collection<PsiField> fields = PsiFieldUtil.filterFieldsByModifiers(psiClass.getFields(), PsiModifier.STATIC, PsiModifier.TRANSIENT, PsiModifier.PROTECTED, PsiModifier.PUBLIC, PsiModifier.FINAL);
        for (PsiField field : fields) {
            if (field.getName().startsWith("$") || field.getName().startsWith("this$"))
                continue;
            String getterMethodSignature = new StringBuilder("public ")
                .append(field.getType().getCanonicalText())
                .append(" ")
                .append(getGetterName(field.getName()))
                .append("()")
                .toString();
            String setterMethodSignature = new StringBuilder("public void ")
                .append(getSetterName(field.getName()))
                .append("(")
                .append(field.getType().getCanonicalText())
                .append(" ")
                .append(field.getName())
                .append(") throws ")
                .append(PROPERTY_VETO_EXCEPTION_TYPE)
                .toString();

            safeAddMethod(psiClass, getterMethodSignature, target);
            safeAddMethod(psiClass, setterMethodSignature, target);
        }
    }
}
