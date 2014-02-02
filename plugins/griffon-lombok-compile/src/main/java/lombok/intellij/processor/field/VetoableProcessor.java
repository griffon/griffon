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
package lombok.intellij.processor.field;

import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import de.plushnikov.intellij.lombok.processor.LombokProcessor;
import de.plushnikov.intellij.lombok.processor.field.LombokFieldProcessor;
import griffon.transform.Vetoable;
import org.codehaus.griffon.core.compile.VetoableConstants;
import org.jetbrains.annotations.NotNull;
import org.kordamp.jipsy.ServiceProviderFor;

import java.util.List;

/**
 * @author Andres Almiray
 */
@ServiceProviderFor({LombokProcessor.class, LombokFieldProcessor.class})
public class VetoableProcessor extends AbstractGriffonLombokFieldProcessor implements VetoableConstants {
    public VetoableProcessor() {
        super(Vetoable.class, PsiMethod.class);
    }

    protected <PSI extends PsiElement> void processIntern(@NotNull PsiField psiField, @NotNull PsiAnnotation psiAnnotation, @NotNull List<PSI> target) {
        if (psiField.getName().startsWith("$") || psiField.getName().startsWith("this$")) {
            return;
        }

        delegateTo(psiField, psiAnnotation, target, VETOABLE_METHODS);
        delegateTo(psiField, psiAnnotation, target, VETOABLE_FIRE_METHODS);
        delegateTo(psiField, psiAnnotation, target, OBSERVABLE_METHODS);
        delegateTo(psiField, psiAnnotation, target, OBSERVABLE_FIRE_METHODS);

        String getterMethodSignature = new StringBuilder("public ")
            .append(psiField.getType().getCanonicalText())
            .append(" ")
            .append(getGetterName(psiField.getName()))
            .append("()")
            .toString();
        String setterMethodSignature = new StringBuilder("public void ")
            .append(getSetterName(psiField.getName()))
            .append("(")
            .append(psiField.getType().getCanonicalText())
            .append(" ")
            .append(psiField.getName())
            .append(") throws ")
            .append(PROPERTY_VETO_EXCEPTION_TYPE)
            .toString();

        safeAddMethod(psiField, getterMethodSignature, target);
        safeAddMethod(psiField, setterMethodSignature, target);
    }
}
