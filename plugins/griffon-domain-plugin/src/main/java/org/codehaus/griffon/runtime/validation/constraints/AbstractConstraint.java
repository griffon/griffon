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
package org.codehaus.griffon.runtime.validation.constraints;

import griffon.core.i18n.MessageSource;
import griffon.core.i18n.NoSuchMessageException;
import griffon.exceptions.GriffonException;
import griffon.plugins.validation.Errors;
import griffon.plugins.validation.FieldObjectError;
import griffon.plugins.validation.constraints.ConstrainedProperty;
import griffon.plugins.validation.constraints.Constraint;
import griffon.util.GriffonClassUtils;
import griffon.util.GriffonNameUtils;
import org.codehaus.griffon.runtime.validation.DefaultErrors;
import org.codehaus.griffon.runtime.validation.DefaultFieldObjectError;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

import static griffon.core.GriffonExceptionHandler.sanitize;
import static griffon.util.GriffonNameUtils.getPropertyNameRepresentation;
import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

/**
 * Abstract class for constraints to extend.
 *
 * @author Graeme Rocher (Grails)
 */
public abstract class AbstractConstraint implements Constraint {
    protected String constraintPropertyName;
    protected Class<?> constraintOwningClass;
    protected Object constraintParameter;
    protected String classShortName;
    protected MessageSource messageSource;

    public AbstractConstraint() {
    }

    public void setMessageSource(@Nonnull MessageSource source) {
        messageSource = source;
    }

    @Nonnull
    public String getPropertyName() {
        return constraintPropertyName;
    }

    @SuppressWarnings("rawtypes")
    public void setOwningClass(@Nonnull Class constraintOwningClass) {
        this.constraintOwningClass = constraintOwningClass;
        classShortName = getPropertyNameRepresentation(constraintOwningClass);
    }

    /**
     * @param constraintPropertyName The constraintPropertyName to set.
     */
    public void setPropertyName(@Nonnull String constraintPropertyName) {
        this.constraintPropertyName = constraintPropertyName;
    }

    /**
     * @param constraintParameter The constraintParameter to set.
     */
    public void setParameter(@Nonnull Object constraintParameter) {
        this.constraintParameter = constraintParameter;
    }

    @Nonnull
    public Object getParameter() {
        return constraintParameter;
    }

    protected void checkState() {
        requireNonBlank(constraintPropertyName, "Property 'propertyName' must be set on the constraint");
        requireNonNull(constraintOwningClass, "Property 'owningClass' must be set on the constraint");
        requireNonNull(constraintParameter, "Property 'constraintParameter' must be set on the constraint");
    }

    public void validate(@Nonnull Object target, @Nullable Object propertyValue, @Nonnull Errors errors) {
        checkState();

        // Skip null values if desired.
        if (propertyValue == null && skipNullValues()) {
            return;
        }

        // Skip blank values if desired.
        if (skipBlankValues() && propertyValue instanceof String && GriffonNameUtils.isBlank((String) propertyValue)) {
            return;
        }

        // Do the validation for this constraint.
        processValidate(target, propertyValue, errors);
    }

    protected boolean skipNullValues() {
        // a null is not a value we should even check in most cases
        return true;
    }

    protected boolean skipBlankValues() {
        // Most constraints ignore blank values, leaving it to the explicit "blank" constraint.
        return true;
    }

    public void rejectValue(@Nonnull Object target, @Nonnull Errors errors, String defaultMessageCode, Object[] args) {
        rejectValue(target, errors, defaultMessageCode, new String[]{}, args);
    }

    public void rejectValue(@Nonnull Object target, @Nonnull Errors errors, String defaultMessageCode, String code, Object[] args) {
        rejectValue(target, errors, defaultMessageCode, new String[]{code}, args);
    }

    public void rejectValue(@Nonnull Object target, @Nonnull Errors errors, String defaultMessageCode, String[] codes, Object[] args) {
        rejectValueWithDefaultMessage(target, errors, getDefaultMessage(defaultMessageCode), codes, args);
    }

    @SuppressWarnings("ConstantConditions")
    public void rejectValueWithDefaultMessage(@Nonnull Object target, @Nonnull Errors errors, String defaultMessage, String[] codes, Object[] args) {
        DefaultErrors result = (DefaultErrors) errors;
        Set<String> newCodes = new LinkedHashSet<>();

        if (args.length > 1 && messageSource != null) {
            if ((args[0] instanceof String) && (args[1] instanceof Class<?>)) {
                final Locale locale = Locale.getDefault();
                final Class<?> constrainedClass = (Class<?>) args[1];
                final String fullClassName = constrainedClass.getName();

                String classNameCode = fullClassName + ".error.label";
                String resolvedClassName = messageSource.getMessage(classNameCode, locale, fullClassName);
                final String classAsPropertyName = GriffonNameUtils.getPropertyName(constrainedClass);

                if (resolvedClassName.equals(fullClassName)) {
                    // try short version
                    classNameCode = classAsPropertyName + ".error.label";
                    resolvedClassName = messageSource.getMessage(classNameCode, locale, fullClassName);
                }

                // update passed version
                if (!resolvedClassName.equals(fullClassName)) {
                    args[1] = resolvedClassName;
                }

                if (args[1] instanceof Class<?>) {
                    args[1] = ((Class<?>) args[1]).getName();
                }

                String propertyName = (String) args[0];
                String propertyNameCode = fullClassName + '.' + propertyName + ".error.label";
                String resolvedPropertyName = messageSource.getMessage(propertyNameCode, locale, propertyName);
                if (resolvedPropertyName.equals(propertyName)) {
                    propertyNameCode = classAsPropertyName + '.' + propertyName + ".error.label";
                    resolvedPropertyName = messageSource.getMessage(propertyNameCode, locale, propertyName);
                }

                // update passed version
                if (!resolvedPropertyName.equals(propertyName)) {
                    args[0] = resolvedPropertyName;
                }
            }
        }

        Class<?> fieldType = getPropertyType(target);
        //Qualified class name is added first to match before unqualified class (which is still resolved for backwards compatibility)
        newCodes.addAll(asList(result.resolveMessageCodes(constraintOwningClass.getName() + '.' + constraintPropertyName + '.' + getName() + ".error", constraintPropertyName, fieldType)));
        newCodes.addAll(asList(result.resolveMessageCodes(classShortName + '.' + constraintPropertyName + '.' + getName() + ".error", constraintPropertyName, fieldType)));
        for (String code : codes) {
            newCodes.addAll(asList(result.resolveMessageCodes(constraintOwningClass.getName() + '.' + constraintPropertyName + '.' + code, constraintPropertyName, fieldType)));
            newCodes.addAll(asList(result.resolveMessageCodes(classShortName + '.' + constraintPropertyName + '.' + code, constraintPropertyName, fieldType)));
            //We resolve the error code on it's own last so that a global code doesn't override a class/field specific error
            newCodes.addAll(asList(result.resolveMessageCodes(code, constraintPropertyName, fieldType)));
        }

        FieldObjectError error = new DefaultFieldObjectError(constraintPropertyName,
            getPropertyValue(errors, target),
            newCodes.toArray(new String[newCodes.size()]),
            args,
            defaultMessage);
        errors.addError(error);
    }

    private Object getPropertyValue(Errors errors, Object target) {
        try {
            return GriffonClassUtils.getProperty(target, constraintPropertyName);
        } catch (IllegalAccessException | NoSuchMethodException e) {
            throw new GriffonException(e);
        } catch (InvocationTargetException e) {
            throw new GriffonException(e.getTargetException());
        }
    }

    @Nonnull
    @SuppressWarnings("ConstantConditions")
    private Class<?> getPropertyType(@Nonnull Object target) {
        try {
            return GriffonClassUtils.getPropertyDescriptor(target, constraintPropertyName).getPropertyType();
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new IllegalArgumentException(sanitize(e));
        }
    }

    // For backward compatibility
    public void rejectValue(@Nonnull Object target, @Nonnull Errors errors, String code, String defaultMessage) {
        rejectValueWithDefaultMessage(target, errors, defaultMessage, new String[]{code}, null);
    }

    // For backward compatibility
    public void rejectValue(@Nonnull Object target, @Nonnull Errors errors, String code, Object[] args, String defaultMessage) {
        rejectValueWithDefaultMessage(target, errors, defaultMessage, new String[]{code}, args);
    }

    /**
     * Returns the default message for the given message code in the
     * current locale. Note that the string returned includes any
     * placeholders that the required message has - these must be
     * expanded by the caller if required.
     *
     * @param code The i18n message code to look up.
     * @return The message corresponding to the given code in the
     * current locale.
     */
    protected String getDefaultMessage(String code) {
        try {
            if (messageSource != null) {
                return messageSource.getMessage(code, Locale.getDefault());
            }
            return ConstrainedProperty.DEFAULT_MESSAGES.get(code);
        } catch (NoSuchMessageException e) {
            return ConstrainedProperty.DEFAULT_MESSAGES.get(code);
        }
    }

    protected abstract void processValidate(@Nonnull Object target, Object propertyValue, @Nonnull Errors errors);

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
            "constraintOwningClass=" + constraintOwningClass +
            ", constraintPropertyName='" + constraintPropertyName + '\'' +
            ", constraintParameter=" + constraintParameter +
            '}';
    }

    /**
     * Return whether the constraint is valid for the owning class
     *
     * @return True if it is
     */
    public boolean isValid() {
        return true;
    }
}
