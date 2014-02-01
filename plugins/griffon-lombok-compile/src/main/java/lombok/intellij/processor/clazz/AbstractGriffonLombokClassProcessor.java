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

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import de.plushnikov.intellij.lombok.ErrorMessages;
import de.plushnikov.intellij.lombok.problem.ProblemBuilder;
import de.plushnikov.intellij.lombok.processor.clazz.AbstractLombokClassProcessor;
import de.plushnikov.intellij.lombok.util.PsiClassUtil;
import de.plushnikov.intellij.lombok.util.PsiMethodUtil;
import org.codehaus.griffon.core.compile.MethodDescriptor;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Locale;

/**
 * @author Andres Almiray
 */
public abstract class AbstractGriffonLombokClassProcessor extends AbstractLombokClassProcessor {
    private final Class<? extends Annotation> annotationClass;

    public AbstractGriffonLombokClassProcessor(@NotNull Class<? extends Annotation> annotationClass, @NotNull Class<?> psiClass) {
        super(annotationClass, psiClass);
        this.annotationClass = annotationClass;
    }

    protected Class<? extends Annotation> getAnnotationClass() {
        return this.annotationClass;
    }

    @Override
    protected boolean validate(@NotNull PsiAnnotation psiAnnotation, @NotNull PsiClass psiClass, @NotNull ProblemBuilder builder) {
        return validateAnnotationOnRigthType(psiClass, builder);
    }

    protected boolean validateAnnotationOnRigthType(@NotNull PsiClass psiClass, @NotNull ProblemBuilder builder) {
        boolean result = true;
        if (psiClass.isAnnotationType() || psiClass.isInterface() || psiClass.isEnum()) {
            builder.addError(ErrorMessages.canBeUsedOnClassOnly(getAnnotationClass()));
            result = false;
        }
        return result;
    }

    public PsiElementFactory psiElementFactory(@NotNull PsiClass psiClass) {
        Project project = psiClass.getProject();
        return JavaPsiFacade.getElementFactory(project);
    }

    protected <Psi extends PsiElement> void delegateTo(@NotNull PsiClass psiClass, @NotNull PsiAnnotation psiAnnotation, @NotNull List<Psi> target, @NotNull MethodDescriptor[] methods) {
        for (MethodDescriptor methodDesc : methods) {
            PsiMethod method = PsiMethodUtil.createMethod(psiClass, methodDesc.signature, psiAnnotation);
            safeAddMethod(psiClass, method, target);
        }
    }

    protected <Psi extends PsiElement> void safeAddMethod(@NotNull PsiClass psiClass, @NotNull String method, @NotNull List<Psi> target) {
        safeAddMethod(psiClass,
            PsiMethodUtil.createMethod(psiClass, method, psiClass),
            target);
    }

    protected <Psi extends PsiElement> void safeAddMethod(@NotNull PsiClass psiClass, @NotNull PsiMethod targetMethod, @NotNull List<Psi> target) {
        final PsiMethod[] classMethods = PsiClassUtil.collectClassMethodsIntern(psiClass);

        boolean found = false;
        for (PsiMethod method : classMethods) {
            if (method.getName().equals(targetMethod.getName()) &&
                method.getText().equals(targetMethod.getText())) {
                found = true;
                break;
            }
        }

        if (!found) target.add((Psi) targetMethod);
    }

    public static String getGetterName(String propertyName) {
        return "get" + capitalize(propertyName);
    }

    public static String getSetterName(String propertyName) {
        return "set" + capitalize(propertyName);
    }

    public static String capitalize(String str) {
        if (isBlank(str)) return str;
        if (str.length() == 1) return str.toUpperCase();
        return str.substring(0, 1).toUpperCase(Locale.ENGLISH) + str.substring(1);
    }

    public static boolean isBlank(String str) {
        return str == null || str.trim().length() == 0;
    }
}
