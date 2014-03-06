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
package org.codehaus.griffon.runtime.scaffolding;

import griffon.core.artifact.GriffonController;
import griffon.core.i18n.MessageSource;
import griffon.core.i18n.NoSuchMessageException;
import griffon.plugins.scaffolding.Disposable;
import griffon.plugins.scaffolding.ScaffoldingContext;
import griffon.plugins.scaffolding.ScaffoldingUtils;
import griffon.plugins.validation.FieldObjectError;
import griffon.plugins.validation.ObjectError;
import griffon.plugins.validation.Validateable;
import griffon.plugins.validation.constraints.ConstrainedProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import static griffon.plugins.scaffolding.ScaffoldingUtils.dot;
import static griffon.plugins.scaffolding.ScaffoldingUtils.getUiDefaults;
import static griffon.plugins.scaffolding.ScaffoldingUtils.qualifyActionValidatable;
import static griffon.util.ConfigUtils.getConfigValue;
import static griffon.util.GriffonNameUtils.isBlank;

/**
 * @author Andres Almiray
 */
public class DefaultScaffoldingContext implements ScaffoldingContext {
    private final Logger LOG = LoggerFactory.getLogger(DefaultScaffoldingContext.class);
    // private Binding binding;
    private GriffonController controller;
    private String actionName;
    private Validateable validateable;

    private final Map<String, Class<?>> widgetTemplates = new LinkedHashMap<>();
    private final Map<String, Class<?>> labelerTemplates = new LinkedHashMap<>();
    private final List<Disposable> disposables = new ArrayList<>();
    private final Map<String, String> errorCodes = new TreeMap<>();

    /*
    public Binding getBinding() {
        return binding;
    }

    public void setBinding(Binding binding) {
        this.binding = binding;
    }
    */

    @Override
    public GriffonController getController() {
        return controller;
    }

    @Override
    public void setController(GriffonController controller) {
        this.controller = controller;
    }

    @Override
    public String getActionName() {
        return actionName;
    }

    @Override
    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    @Override
    public Validateable getValidateable() {
        return validateable;
    }

    @Override
    public void setValidateable(Validateable validateable) {
        this.validateable = validateable;
    }

    @Override
    public void addDisposable(Disposable disposable) {
        if (disposable == null || disposables.contains(disposable)) return;
        disposables.add(disposable);
    }

    @Override
    public void dispose() {
        controller = null;
        validateable = null;
        // binding = null;
        for (Disposable disposable : disposables) {
            disposable.dispose();
        }
        disposables.clear();
    }

    @Override
    @SuppressWarnings({"unchecked", "ConstantConditions"})
    public Map<String, Object> widgetAttributes(String widget, ConstrainedProperty constrainedProperty) {
        Map<String, Object> config = new LinkedHashMap<>();
        config.putAll(getConfigValue(getUiDefaults(controller.getApplication()), "widget", new LinkedHashMap<String, Object>()));
        config.putAll(getConfigValue(getUiDefaults(controller.getApplication()), widget, new LinkedHashMap<String, Object>()));
        config.putAll(constrainedProperty.getAttributes());
        Map<String, Object> attributes = new LinkedHashMap<>();
        attributes.putAll(config);
        attributes.put("id", constrainedProperty.getPropertyName());
        return attributes;
    }

    @Override
    public String resolveMessage(String key, String defaultValue) {
        return ScaffoldingUtils.resolveMessage(controller, actionName, validateable, key, defaultValue);
    }

    @Override
    public Class resolveLabeler(String property) {
        Class labelerTemplate = labelerTemplates.get(property);

        if (labelerTemplate == null) {
            LOG.trace("Resolving labeler template for {}", qualify(property));
            ConstrainedProperty constrainedProperty = validateable.constrainedProperties().get(property);
            if (!isBlank(constrainedProperty.getWidget())) {
                labelerTemplate = resolveLabelerTemplateByWidget(constrainedProperty);
            }

            if (labelerTemplate == null) {
                labelerTemplate = resolveLabelerTemplateByProperty(property);
            }

            if (labelerTemplate == null) {
                labelerTemplate = resolveLabelerTemplateByDefault();
            }

            if (labelerTemplate == null) {
                LOG.warn("  Could not resolve a suitable labeler template for {}", qualify(property));
                throw new IllegalArgumentException("Could not resolve a suitable labeler template for " + qualify(property));
            } else if (LOG.isDebugEnabled()) {
                LOG.trace("Resolved labeler template for {} is {}", qualify(property), labelerTemplate.getName());
            }
            labelerTemplates.put(property, labelerTemplate);
        }

        return labelerTemplate;
    }

    private Class resolveLabelerTemplateByWidget(ConstrainedProperty constrainedProperty) {
        String[] templates = ScaffoldingUtils.widgetLabelerTemplates(controller, actionName, validateable, constrainedProperty.getWidget());
        return resolveTemplate(templates);
    }

    private Class resolveLabelerTemplateByProperty(String property) {
        String[] templates = ScaffoldingUtils.propertyLabelerTemplates(controller, actionName, validateable, property);
        return resolveTemplate(templates);
    }

    private Class resolveLabelerTemplateByDefault() {
        String[] templates = ScaffoldingUtils.defaultLabelerTemplates();
        return resolveTemplate(templates);
    }

    @Override
    public Class resolveWidget(String property) {
        Class widgetTemplate = widgetTemplates.get(property);

        if (widgetTemplate == null) {
            LOG.trace("Resolving widget template for {}", qualify(property));
            ConstrainedProperty constrainedProperty = validateable.constrainedProperties().get(property);
            if (!isBlank(constrainedProperty.getWidget())) {
                widgetTemplate = resolveWidgetTemplateByWidget(constrainedProperty);
            }

            if (widgetTemplate == null) {
                widgetTemplate = resolveWidgetTemplateByProperty(property);
            }

            if (widgetTemplate == null) {
                widgetTemplate = resolveWidgetTemplateByUnknown();
            }

            if (widgetTemplate == null) {
                LOG.warn("  Could not resolve a suitable widget template for {}", qualify(property));
                throw new IllegalArgumentException("Could not resolve a suitable widget template for " + qualify(property));
            } else if (LOG.isDebugEnabled()) {
                LOG.trace("Resolved widget template for {} is {}", qualify(property), widgetTemplate.getName());
            }
            widgetTemplates.put(property, widgetTemplate);
        }

        return widgetTemplate;
    }

    private Class resolveWidgetTemplateByWidget(ConstrainedProperty constrainedProperty) {
        String[] templates = ScaffoldingUtils.widgetTemplates(controller, actionName, validateable, constrainedProperty.getWidget());
        return resolveTemplate(templates);
    }

    private Class resolveWidgetTemplateByProperty(String property) {
        String[] templates = ScaffoldingUtils.propertyTemplates(controller, actionName, validateable, property);
        return resolveTemplate(templates);
    }

    private Class resolveWidgetTemplateByUnknown() {
        String[] templates = ScaffoldingUtils.unknownWidgetTemplates();
        return resolveTemplate(templates);
    }

    private Class resolveTemplate(String[] templates) {
        Class labelerTemplate = null;

        // attempt i18n resolution first
        LOG.trace("  [I18N]");
        for (String resourceKey : templates) {
            try {
                LOG.trace("  Resolving {}", resourceKey);
                String widgetTemplateClassName = messageSource().getMessage(resourceKey);
                labelerTemplate = loadClass(widgetTemplateClassName);
                break;
            } catch (Exception e) {
                // continue
            }
        }

        // attempt direct class load
        if (labelerTemplate == null) {
            LOG.trace("  [CLASS]");
            for (String widgetName : templates) {
                try {
                    LOG.trace("  Resolving {}", widgetName);
                    labelerTemplate = loadClass(widgetName);
                    break;
                } catch (Exception e) {
                    // continue
                }
            }
        }

        return labelerTemplate;
    }

    @Nonnull
    private Class<?> loadClass(@Nonnull String className) throws ClassNotFoundException {
        return controller.getApplication().getApplicationClassLoader().get().loadClass(className);
    }

    @Nonnull
    private MessageSource messageSource() {
        return controller.getApplication().getMessageSource();
    }

    @Override
    public String[] resolveErrorMessages() {
        List<String> errorList = new ArrayList<>();
        for (ObjectError error : validateable.getErrors().getAllErrors()) {
            resolveErrorMessages(error, errorList);
        }
        return errorList.toArray(new String[errorList.size()]);
    }

    @Override
    public String[] resolveFieldErrorMessages(List<FieldObjectError> errors) {
        List<String> errorList = new ArrayList<String>();
        for (ObjectError error : errors) {
            resolveErrorMessages(error, errorList);
        }
        return errorList.toArray(new String[errorList.size()]);
    }

    @Override
    public String[] resolveErrorMessages(List<ObjectError> errors) {
        List<String> errorList = new ArrayList<String>();
        for (ObjectError error : errors) {
            resolveErrorMessages(error, errorList);
        }
        return errorList.toArray(new String[errorList.size()]);
    }

    @Override
    public String[] resolveErrorMessages(ObjectError error) {
        List<String> errorList = new ArrayList<String>();
        resolveErrorMessages(error, errorList);
        return errorList.toArray(new String[errorList.size()]);
    }

    private void resolveErrorMessages(ObjectError error, List<String> errors) {
        String errorKey = errorKey(error);
        String errorCode = errorCodes.get(errorKey);
        if (!isBlank(errorCode)) {
            errors.add(messageSource().formatMessage(errorCode, error.getArguments()));
            return;
        }

        boolean messageResolved = false;
        for (String code : error.getCodes()) {
            try {
                String message = messageSource().getMessage(code, error.getArguments());
                errors.add(message);
                errorCodes.put(errorKey, code);
                messageResolved = true;
                break;
            } catch (NoSuchMessageException e) {
                // continue;
            }
        }
        if (!messageResolved) {
            errors.add(messageSource().formatMessage(error.getDefaultMessage(), error.getArguments()));
            errorCodes.put(errorKey, error.getDefaultMessage());
        }
    }


    private String qualify() {
        return qualifyActionValidatable(controller, actionName, validateable);
    }

    private String qualify(String extra) {
        return dot(qualifyActionValidatable(controller, actionName, validateable), extra);
    }

    private String errorKey(ObjectError error) {
        StringBuilder b = new StringBuilder(error.getClass().getName())
            .append(Arrays.toString(error.getCodes()))
            .append(error.getDefaultMessage());
        if (error instanceof FieldObjectError) {
            b.append(((FieldObjectError) error).getFieldName());
        }
        return hash(b.toString());
    }

    protected String hash(String str) throws IllegalArgumentException {
        if (str == null || str.length() == 0) {
            throw new IllegalArgumentException("String to encode cannot be null or have zero length");
        }

        return hash(str.getBytes());
    }

    protected String hash(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            throw new IllegalArgumentException("Byte array to encode cannot be null or have zero length");
        }

        MessageDigest digester = createDigester();
        digester.update(bytes);
        byte[] hash = digester.digest();
        StringBuilder hexString = new StringBuilder();
        for (byte aHash : hash) {
            if ((0xff & aHash) < 0x10) {
                hexString.append("0")
                    .append(Integer.toHexString((0xFF & aHash)));
            } else {
                hexString.append(Integer.toHexString(0xFF & aHash));
            }
        }
        return hexString.toString();
    }

    protected MessageDigest createDigester() {
        try {
            return MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
