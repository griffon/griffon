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
package org.codehaus.griffon.runtime.validation;

import griffon.exceptions.GriffonException;
import griffon.plugins.validation.Errors;
import griffon.plugins.validation.FieldObjectError;
import griffon.plugins.validation.MessageCodesResolver;
import griffon.plugins.validation.ObjectError;
import griffon.util.GriffonClassUtils;
import org.codehaus.griffon.runtime.core.AbstractObservable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import static griffon.core.GriffonExceptionHandler.sanitize;
import static griffon.util.GriffonNameUtils.capitalize;
import static java.util.Collections.synchronizedMap;
import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 */
public class DefaultErrors extends AbstractObservable implements Errors {
    private final Map<String, List<FieldObjectError>> fieldErrors = synchronizedMap(new LinkedHashMap<String, List<FieldObjectError>>());
    private final List<ObjectError> objectErrors = new CopyOnWriteArrayList<>();
    private MessageCodesResolver messageCodesResolver = new DefaultMessageCodesResolver();
    private final String objectName;
    private final Class<?> objectClass;

    public DefaultErrors(@Nonnull Class<?> objectClass) {
        this.objectClass = requireNonNull(objectClass, "Argument 'objectClass' cannot be null");
        this.objectName = objectClass.getName();
    }

    @Nonnull
    public String getObjectName() {
        return objectName;
    }

    public boolean hasErrors() {
        return hasGlobalErrors() || hasFieldErrors();
    }

    public boolean hasGlobalErrors() {
        return !objectErrors.isEmpty();
    }

    public boolean hasFieldErrors() {
        return !fieldErrors.isEmpty();
    }

    public boolean hasFieldErrors(@Nonnull String field) {
        return getFieldErrorCount(field) > 0;
    }

    @Nullable
    public FieldObjectError getFieldError(@Nonnull String field) {
        List<FieldObjectError> errors = fieldErrors.get(field);
        return null != errors && errors.size() > 0 ? errors.get(0) : null;
    }

    @Nonnull
    public List<FieldObjectError> getFieldErrors(@Nonnull String field) {
        List<FieldObjectError> errors = fieldErrors.get(field);
        return null != errors ? unmodifiableList(errors) : Collections.<FieldObjectError>emptyList();
    }

    public int getFieldErrorCount(@Nonnull String field) {
        return getFieldErrors(field).size();
    }

    @Nonnull
    public List<ObjectError> getGlobalErrors() {
        return unmodifiableList(objectErrors);
    }

    public int getGlobalErrorCount() {
        return objectErrors.size();
    }

    @Nullable
    public ObjectError getGlobalError() {
        return objectErrors.size() > 0 ? objectErrors.get(0) : null;
    }

    public int getErrorCount() {
        int count = 0;
        synchronized (fieldErrors) {
            for (List<FieldObjectError> errors : fieldErrors.values()) {
                count += errors.size();
            }
        }
        return count + objectErrors.size();
    }

    @Nonnull
    public List<ObjectError> getAllErrors() {
        List<ObjectError> tmp = new ArrayList<>();
        tmp.addAll(objectErrors);
        synchronized (fieldErrors) {
            for (List<FieldObjectError> errors : fieldErrors.values()) {
                tmp.addAll(errors);
            }
        }
        return tmp;
    }

    public void addError(@Nonnull ObjectError objectError) {
        if (objectError instanceof FieldObjectError) {
            FieldObjectError fieldError = (FieldObjectError) objectError;
            addFieldError(fieldError, fieldErrors.get(fieldError.getFieldName()));
        } else if (objectError != null) {
            addObjectError(objectError);
        }
    }

    private void fireHasErrorFieldEvent(String fieldName, boolean oldValue, boolean newValue) {
        firePropertyChange(fieldName + capitalize(HAS_ERRORS_PROPERTY), oldValue, newValue);
    }

    private void fireErrorCountFieldEvent(String fieldName, int oldValue, int newValue) {
        firePropertyChange(fieldName + capitalize(ERROR_COUNT_PROPERTY), oldValue, newValue);
    }

    private void fireHasErrorEvent(boolean oldValue, boolean newValue) {
        firePropertyChange(HAS_ERRORS_PROPERTY, oldValue, newValue);
    }

    private void fireErrorCountEvent(int oldValue, int newValue) {
        firePropertyChange(ERROR_COUNT_PROPERTY, oldValue, newValue);
    }

    @Override
    protected void firePropertyChange(@Nonnull String propertyName, Object oldValue, Object newValue) {
        if (oldValue != newValue) {
            super.firePropertyChange(propertyName, oldValue, newValue);
        }
    }

    public void clearAllErrors() {
        int totalErrorCount = getErrorCount();
        Map<String, Integer> errorContPerField = new LinkedHashMap<>();
        for (Map.Entry<String, List<FieldObjectError>> entry : fieldErrors.entrySet()) {
            errorContPerField.put(entry.getKey(), entry.getValue().size());
        }

        objectErrors.clear();
        fieldErrors.clear();

        for (Map.Entry<String, Integer> entry : errorContPerField.entrySet()) {
            fireHasErrorFieldEvent(entry.getKey(), true, false);
            fireErrorCountFieldEvent(entry.getKey(), entry.getValue(), 0);
        }
        fireHasErrorEvent(totalErrorCount != 0, false);
        fireErrorCountEvent(totalErrorCount, 0);
    }

    public void clearGlobalErrors() {
        int totalErrorCount = getErrorCount();
        objectErrors.clear();
        fireHasErrorEvent(totalErrorCount != 0, false);
        fireErrorCountEvent(totalErrorCount, 0);
    }

    public void clearFieldErrors() {
        int totalErrorCount = getErrorCount();
        Map<String, Integer> errorContPerField = new LinkedHashMap<>();
        for (Map.Entry<String, List<FieldObjectError>> entry : fieldErrors.entrySet()) {
            errorContPerField.put(entry.getKey(), entry.getValue().size());
        }

        fieldErrors.clear();

        for (Map.Entry<String, Integer> entry : errorContPerField.entrySet()) {
            fireHasErrorFieldEvent(entry.getKey(), true, false);
            fireErrorCountFieldEvent(entry.getKey(), entry.getValue(), 0);
        }
        fireHasErrorEvent(totalErrorCount != 0, false);
        fireErrorCountEvent(totalErrorCount, 0);
    }

    public void clearFieldErrors(@Nonnull String field) {
        int totalErrorCount = getErrorCount();
        List<FieldObjectError> errors = fieldErrors.remove(field);

        fireHasErrorFieldEvent(field, errors != null, false);
        fireErrorCountFieldEvent(field, errors != null ? errors.size() : 0, 0);
        fireHasErrorEvent(totalErrorCount != 0, false);
        fireErrorCountEvent(totalErrorCount, 0);
    }

    public MessageCodesResolver getMessageCodesResolver() {
        return messageCodesResolver;
    }

    public void setMessageCodesResolver(MessageCodesResolver messageCodesResolver) {
        this.messageCodesResolver = messageCodesResolver;
    }

    @Nonnull
    public String[] resolveMessageCodes(@Nonnull String code) {
        return messageCodesResolver.resolveMessageCodes(code, objectName);
    }

    @Nonnull
    public String[] resolveMessageCodes(@Nonnull String code, @Nonnull String field, @Nonnull Class<?> fieldType) {
        return messageCodesResolver.resolveMessageCodes(code, objectName, field, fieldType);
    }

    public void reject(@Nonnull String code, @Nonnull Object[] args, @Nonnull String defaultMessage) {
        ObjectError objectError = new DefaultObjectError(resolveMessageCodes(code), args, defaultMessage);
        addObjectError(objectError);
    }

    private void addObjectError(ObjectError objectError) {
        if (!objectErrors.contains(objectError)) {
            int totalErrorCount = getErrorCount();
            objectErrors.add(objectError);
            fireHasErrorEvent(totalErrorCount != 0, true);
            fireErrorCountEvent(totalErrorCount, totalErrorCount + 1);
        }
    }

    public void reject(@Nonnull String code, @Nonnull String defaultMessage) {
        reject(code, ObjectError.NO_ARGS, defaultMessage);
    }

    public void rejectField(@Nonnull String field, @Nonnull String code, @Nonnull Object[] args, @Nonnull String defaultMessage) {
        rejectField(field, null, code, ObjectError.NO_ARGS, defaultMessage);
    }

    public void rejectField(@Nonnull String field, @Nonnull String code, @Nonnull String defaultMessage) {
        rejectField(field, null, code, ObjectError.NO_ARGS, defaultMessage);
    }

    public void rejectField(@Nonnull String field, Object rejectedValue, @Nonnull String code, @Nonnull Object[] args, @Nonnull String defaultMessage) {
        Class<?> fieldType = getFieldType(field);
        FieldObjectError fieldError = new DefaultFieldObjectError(field, rejectedValue, resolveMessageCodes(code, field, fieldType), args, defaultMessage);
        addFieldError(fieldError, fieldErrors.get(field));
    }

    private void addFieldError(FieldObjectError fieldError, List<FieldObjectError> errors) {
        if (null == errors) {
            int totalErrorCount = getErrorCount();
            errors = new ArrayList<>();
            fieldErrors.put(fieldError.getFieldName(), errors);
            fireHasErrorFieldEvent(fieldError.getFieldName(), false, true);
            fireErrorCountFieldEvent(fieldError.getFieldName(), 0, 1);
            fireErrorCountEvent(totalErrorCount, totalErrorCount + 1);
            fireHasErrorEvent(totalErrorCount != 0, true);
        }
        if (!errors.contains(fieldError)) {
            int totalErrorCount = getErrorCount();
            int fieldErrorCount = errors.size();
            errors.add(fieldError);
            fireHasErrorFieldEvent(fieldError.getFieldName(), fieldErrorCount != 0, true);
            fireErrorCountFieldEvent(fieldError.getFieldName(), fieldErrorCount, fieldErrorCount + 1);
            fireHasErrorEvent(totalErrorCount != 0, true);
            fireErrorCountEvent(totalErrorCount, totalErrorCount + 1);
        }
    }

    public void rejectField(@Nonnull String field, Object rejectedValue, @Nonnull String code, @Nonnull String defaultMessage) {
        rejectField(field, rejectedValue, code, null, defaultMessage);
    }

    private Class<?> getFieldType(String field) {
        try {
            return GriffonClassUtils.getPropertyDescriptor(objectClass, field).getPropertyType();
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new GriffonException(sanitize(e));
        }
    }
}
