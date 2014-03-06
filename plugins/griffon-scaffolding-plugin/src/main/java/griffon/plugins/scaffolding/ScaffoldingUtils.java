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
package griffon.plugins.scaffolding;

import griffon.core.Configuration;
import griffon.core.GriffonApplication;
import griffon.core.GriffonExceptionHandler;
import griffon.core.artifact.GriffonController;
import griffon.core.artifact.GriffonControllerClass;
import griffon.core.controller.ActionManager;
import griffon.core.i18n.NoSuchMessageException;
import griffon.exceptions.InstanceNotFoundException;
import griffon.plugins.scaffolding.atoms.*;
import griffon.plugins.validation.Validateable;
import griffon.plugins.validation.constraints.ConstrainedProperty;
import griffon.util.CollectionUtils;
import griffon.util.GriffonNameUtils;
import griffon.util.ServiceLoaderUtils;
import org.joda.time.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

import static griffon.util.AnnotationUtils.named;
import static griffon.util.GriffonNameUtils.capitalize;
import static griffon.util.GriffonNameUtils.getLogicalPropertyName;
import static griffon.util.GriffonNameUtils.getPropertyName;
import static griffon.util.GriffonNameUtils.isKeyword;

/**
 * @author Andres Almiray
 */
public final class ScaffoldingUtils {
    private static final Logger LOG = LoggerFactory.getLogger(ScaffoldingUtils.class);
    private static final String KEY_SCAFFOLDING_UIDEFAULTS = "scaffolding.uidefaults";
    private static final String GLOBAL_UIDEFAULTS_CONFIG_FILE = "UiDefaults";
    private static Map<String, Object> uiDefaults;
    private static final Object LOCK = new Object[0];

    public static final String COMMAND_OBJECT_SUFFIX = "CommandObject";
    public static final String VALIDATABLE_SUFFIX = "Validatable";
    private static final String DEFAULT_APPLICATION_TEMPLATE_PATH = "templates.scaffolding";
    private static final String DEFAULT_TEMPLATE_PATH = "griffon.plugins.scaffolding.templates";
    private static final String KEY_DEFAULT = "default";
    private static final String KEY_UNKNOWN = "Unknown";
    private static final String KEY_TEMPLATE = "Template";
    private static final String KEY_LABELER_TEMPLATE = "LabelerTemplate";
    private static final String KEY_ENUM = "Enum";

    private static Map<Class<?>, Class<?>> SUPPORTED_ATOM_TYPES = CollectionUtils.<Class<?>, Class<?>>map()
        .e(BigDecimal.class, BigDecimalValue.class)
        .e(BigInteger.class, BigIntegerValue.class)
        .e(Boolean.class, BooleanValue.class)
        .e(Byte.class, ByteValue.class)
        .e(Calendar.class, CalendarValue.class)
        .e(Date.class, DateValue.class)
        .e(Double.class, DoubleValue.class)
        .e(Float.class, FloatValue.class)
        .e(Integer.class, IntegerValue.class)
        .e(Long.class, LongValue.class)
        .e(Short.class, ShortValue.class)
        .e(String.class, StringValue.class)
        .e(Boolean.TYPE, BooleanValue.class)
        .e(Byte.TYPE, ByteValue.class)
        .e(Double.TYPE, DoubleValue.class)
        .e(Float.TYPE, FloatValue.class)
        .e(Integer.TYPE, IntegerValue.class)
        .e(Long.TYPE, LongValue.class)
        .e(Short.TYPE, ShortValue.class)
        .e(DateTime.class, DateTimeValue.class)
        .e(DateTimeZone.class, DateTimeZoneValue.class)
        .e(LocalDate.class, LocalDateValue.class)
        .e(LocalDateTime.class, LocalDateTimeValue.class)
        .e(LocalTime.class, LocalTimeValue.class)
        .e(Instant.class, InstantValue.class)
        .e(Years.class, YearsValue.class)
        .e(Months.class, MonthsValue.class)
        .e(Weeks.class, WeeksValue.class)
        .e(Days.class, DaysValue.class)
        .e(Hours.class, HoursValue.class)
        .e(Minutes.class, MinutesValue.class)
        .e(Seconds.class, SecondsValue.class)
        .e(Duration.class, DurationValue.class);

    @Nonnull
    public static Map<Class<?>, Class<?>> initializeAtomTypes(@Nonnull ClassLoader classLoader) {
        ServiceLoaderUtils.load(classLoader, "META-INF/services/", AtomicValue.class, new ServiceLoaderUtils.LineProcessor() {
            @Override
            public void process(@Nonnull ClassLoader classLoader, @Nonnull Class<?> type, @Nonnull String line) {
                try {
                    String[] parts = line.trim().split("=");
                    Class targetType = loadClass(parts[0].trim());
                    Class atomicValueClass = loadClass(parts[1].trim());
                    LOG.debug("Registering {} as AtomicValue for {}", atomicValueClass.getName(), targetType.getName());
                    SUPPORTED_ATOM_TYPES.put(targetType, atomicValueClass);
                } catch (Exception e) {
                    if (LOG.isWarnEnabled()) {
                        LOG.warn("Could not load " + type.getName() + " with " + line, GriffonExceptionHandler.sanitize(e));
                    }
                }
            }
        });

        return SUPPORTED_ATOM_TYPES;
    }

    @Nonnull
    public static Class<?> loadClass(@Nonnull String className) throws ClassNotFoundException {
        ClassNotFoundException cnfe = null;

        ClassLoader cl = ScaffoldingUtils.class.getClassLoader();
        try {
            return cl.loadClass(className);
        } catch (ClassNotFoundException e) {
            cnfe = e;
        }

        cl = Thread.currentThread().getContextClassLoader();
        try {
            return cl.loadClass(className);
        } catch (ClassNotFoundException e) {
            cnfe = e;
        }

        throw cnfe;
    }

    @Nullable
    public static Class<?> safeLoadClass(@Nonnull String className) {
        try {
            return loadClass(className);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    private ScaffoldingUtils() {
    }

    @Nonnull
    public static String[] mvcMemberCodes(@Nonnull GriffonController controller, @Nonnull String actionName, @Nonnull Validateable validateable, String suffix) {
        // Given the following values
        //   controller    = com.acme.MailController
        //   actionName    = sendMail
        //   commandObject = com.acme.commands.MailCommandObject

        // mail
        String controllerName = getLogicalPropertyName(controller.getClass().getSimpleName(), GriffonControllerClass.TRAILING);
        // sendmail
        String normalizedActionName = normalizeActionName(controller, actionName);
        // com.acme
        String controllerPackageName = controller.getClass().getPackage().getName();
        // MailCommandObject
        String validateableName = capitalize(getLogicalPropertyName(validateable.getClass().getSimpleName(), COMMAND_OBJECT_SUFFIX));
        if (validateable instanceof CommandObject) {
            validateableName += COMMAND_OBJECT_SUFFIX;
        }
        // com.acme.commands
        String validateablePackageName = validateable.getClass().getPackage().getName();

        List<String> codes = new ArrayList<>();
        // com.acme.mail.sendmail.MailCommandObject<suffix> | com.acme.mail.sendmail.Mail<suffix>
        codes.add(dot(controllerPackageName, controllerName, normalizedActionName, validateableName) + suffix);
        // com.acme.mail.MailCommandObject<suffix> | com.acme.mail.Mail<suffix>
        codes.add(dot(controllerPackageName, controllerName, validateableName) + suffix);
        // com.acme.MailCommandObject<suffix> | com.acme.Mail<suffix>
        codes.add(dot(controllerPackageName, validateableName) + suffix);
        // com.acme != com.acme.commands ?
        if (!controllerPackageName.equals(validateablePackageName)) {
            // com.acme.commands.MailCommandObject<suffix> | com.acme.commands.Mail<suffix>
            codes.add(dot(validateablePackageName, validateableName) + suffix);
        }
        // templates.scaffolding.CommandObject<suffix> | templates.scaffolding.Validateable<suffix>
        codes.add(dot(DEFAULT_APPLICATION_TEMPLATE_PATH, validateable instanceof CommandObject ? COMMAND_OBJECT_SUFFIX : VALIDATABLE_SUFFIX) + suffix);
        // griffon.plugins.scaffolding.templates.CommandObject<suffix>
        codes.add(dot(DEFAULT_TEMPLATE_PATH, COMMAND_OBJECT_SUFFIX) + suffix);

        return codes.toArray(new String[codes.size()]);
    }

    @Nonnull
    public static String[] messageCodes(@Nonnull GriffonController controller, @Nonnull String actionName, @Nonnull Validateable validateable, @Nonnull String propertyName) {
        // Given the following values
        //   controller    = com.acme.MailController
        //   actionName    = sendMail
        //   commandObject = com.acme.commands.MailCommandObject

        String controllerName = controller.getClass().getSimpleName();
        // com.acme
        String controllerPackageName = controller.getClass().getPackage().getName();
        // MailCommandObject
        String validateableName = capitalize(getLogicalPropertyName(validateable.getClass().getSimpleName(), COMMAND_OBJECT_SUFFIX));
        if (validateable instanceof CommandObject) {
            validateableName += COMMAND_OBJECT_SUFFIX;
        }
        // com.acme.commands
        String validateablePackageName = validateable.getClass().getPackage().getName();

        List<String> codes = new ArrayList<>();
        // com.acme.MailController.sendMail.MailCommandObject.<propertyName> | com.acme.MailController.sendMail.Mail.<propertyName>
        codes.add(dot(controllerPackageName, controllerName, actionName, validateableName, propertyName));
        // com.acme.MailController.MailCommandObject.<propertyName> | com.acme.MailController.Mail.<propertyName>
        codes.add(dot(controllerPackageName, controllerName, validateableName, propertyName));
        // com.acme.MailCommandObject.<propertyName> | com.acme.Mail.<propertyName>
        codes.add(dot(controllerPackageName, validateableName, propertyName));
        // com.acme != com.acme.commands ?
        if (!controllerPackageName.equals(validateablePackageName)) {
            // com.acme.commands.MailCommandObject.<propertyName> | com.acme.commands.Mail.<propertyName>
            codes.add(dot(validateablePackageName, validateableName, propertyName));
        }
        // default.<propertyName>
        codes.add(dot(KEY_DEFAULT, propertyName));

        return codes.toArray(new String[codes.size()]);
    }

    @Nonnull
    public static String[] propertyTemplates(@Nonnull GriffonController controller, @Nonnull String actionName, @Nonnull Validateable validateable, @Nonnull String propertyName) {
        ConstrainedProperty constrainedProperty = validateable.constrainedProperties().get(propertyName);

        // Given the following values
        //   controller    = com.acme.MailController
        //   actionName    = sendMail
        //   commandObject = com.acme.commands.MailCommandObject

        // mail
        String controllerName = getLogicalPropertyName(controller.getClass().getSimpleName(), GriffonControllerClass.TRAILING);
        // sendmail
        String normalizedActionName = normalizeActionName(controller, actionName);
        // com.acme
        String controllerPackageName = controller.getClass().getPackage().getName();
        // mail
        String validateableName = getLogicalPropertyName(validateable.getClass().getSimpleName(), COMMAND_OBJECT_SUFFIX);
        // com.acme.commands
        String validateablePackageName = validateable.getClass().getPackage().getName();

        propertyName = capitalize(propertyName);
        String simpleType = constrainedProperty.getPropertyType().getSimpleName();
        boolean isEnumType = constrainedProperty.getPropertyType().isEnum();
        if ("int".equals(simpleType)) simpleType = "integer";
        String propertyType = capitalize(getLogicalPropertyName(simpleType, "Value"));

        List<String> templates = new ArrayList<>();
        // com.acme.mail.sendmail.<propertyName>Template
        templates.add(dot(controllerPackageName, controllerName, normalizedActionName, propertyName) + KEY_TEMPLATE);
        // com.acme.mail.sendmail.<propertyType>Template
        templates.add(dot(controllerPackageName, controllerName, normalizedActionName, propertyType) + KEY_TEMPLATE);
        // com.acme.mail.<propertyName>Template
        templates.add(dot(controllerPackageName, controllerName, propertyName) + KEY_TEMPLATE);
        // com.acme.mail.<propertyType>Template
        templates.add(dot(controllerPackageName, controllerName, propertyType) + KEY_TEMPLATE);
        // com.acme != com.acme.commands ?
        if (!controllerPackageName.equals(validateablePackageName)) {
            // com.acme.commands.mail.<propertyName>Template
            templates.add(dot(validateablePackageName, validateableName, propertyName) + KEY_TEMPLATE);
            // com.acme.commands.mail.<propertyType>Template
            templates.add(dot(validateablePackageName, validateableName, propertyType) + KEY_TEMPLATE);
        }
        // templates.scaffolding.mail.<propertyName>Template
        templates.add(dot(DEFAULT_APPLICATION_TEMPLATE_PATH, validateableName, propertyName + KEY_TEMPLATE));
        // templates.scaffolding.mail.<propertyType>Template
        templates.add(dot(DEFAULT_APPLICATION_TEMPLATE_PATH, validateableName, propertyType + KEY_TEMPLATE));
        if (isEnumType) {
            // templates.scaffolding.EnumTemplate
            templates.add(dot(DEFAULT_APPLICATION_TEMPLATE_PATH, validateableName, KEY_ENUM + KEY_TEMPLATE));
        }
        // templates.scaffolding.<propertyName>Template
        templates.add(dot(DEFAULT_APPLICATION_TEMPLATE_PATH, propertyName + KEY_TEMPLATE));
        // templates.scaffolding.<propertyType>Template
        templates.add(dot(DEFAULT_APPLICATION_TEMPLATE_PATH, propertyType + KEY_TEMPLATE));
        Class supertype = constrainedProperty.getPropertyType().getSuperclass();
        while (supertype != null && supertype != Object.class) {
            // templates.scaffolding.<supertype>Template
            templates.add(dot(DEFAULT_APPLICATION_TEMPLATE_PATH, capitalize(getLogicalPropertyName(supertype.getSimpleName(), "Value")) + KEY_TEMPLATE));
            supertype = supertype.getSuperclass();
        }
        // griffon.plugins.scaffolding.templates.<propertyType>Template
        templates.add(dot(DEFAULT_TEMPLATE_PATH, propertyType + KEY_TEMPLATE));
        if (isEnumType) {
            // griffon.plugins.scaffolding.templates.EnumTemplate
            templates.add(dot(DEFAULT_TEMPLATE_PATH, KEY_ENUM + KEY_TEMPLATE));
        }

        return templates.toArray(new String[templates.size()]);
    }

    @Nonnull
    public static String[] propertyLabelerTemplates(@Nonnull GriffonController controller, @Nonnull String actionName, @Nonnull Validateable validateable, @Nonnull String propertyName) {
        String[] propertyTemplates = propertyTemplates(controller, actionName, validateable, propertyName);
        String[] propertyLabelerTemplates = new String[propertyTemplates.length];

        for (int i = 0; i < propertyTemplates.length; i++) {
            propertyLabelerTemplates[i] = propertyTemplates[i].substring(0, propertyTemplates[i].length() - KEY_TEMPLATE.length()) + KEY_LABELER_TEMPLATE;
        }

        return propertyLabelerTemplates;
    }

    @Nonnull
    public static String[] widgetLabelerTemplates(@Nonnull GriffonController controller, @Nonnull String actionName, @Nonnull Validateable validateable, @Nonnull String widget) {
        String[] templates = widgetTemplates(controller, actionName, validateable, widget);
        String[] labelerTemplates = new String[templates.length];

        for (int i = 0; i < templates.length; i++) {
            labelerTemplates[i] = templates[i].substring(0, templates[i].length() - KEY_TEMPLATE.length()) + KEY_LABELER_TEMPLATE;
        }

        return labelerTemplates;
    }

    @Nonnull
    public static String[] defaultLabelerTemplates() {
        List<String> templates = new ArrayList<>();

        // templates.scaffolding.LabelerTemplate
        templates.add(dot(DEFAULT_APPLICATION_TEMPLATE_PATH, KEY_LABELER_TEMPLATE));
        // griffon.plugins.scaffolding.LabelerTemplate
        templates.add(dot(DEFAULT_TEMPLATE_PATH, KEY_LABELER_TEMPLATE));

        return templates.toArray(new String[templates.size()]);
    }

    @Nonnull
    public static String[] widgetTemplates(@Nonnull GriffonController controller, @Nonnull String actionName, @Nonnull Validateable validateable, @Nonnull String widget) {
        // Given the following values
        //   controller    = com.acme.MailController
        //   actionName    = sendMail
        //   commandObject = com.acme.commands.MailCommandObject

        // mail
        String controllerName = getLogicalPropertyName(controller.getClass().getSimpleName(), GriffonControllerClass.TRAILING);
        // sendmail
        String normalizedActionName = normalizeActionName(controller, actionName);
        // com.acme
        String controllerPackageName = controller.getClass().getPackage().getName();
        // mail
        String validateableName = getLogicalPropertyName(validateable.getClass().getSimpleName(), COMMAND_OBJECT_SUFFIX);
        // com.acme.commands
        String validateablePackageName = validateable.getClass().getPackage().getName();

        widget = capitalize(widget);

        List<String> templates = new ArrayList<>();
        // com.acme.mail.sendmail.<widget>Template
        templates.add(dot(controllerPackageName, controllerName, normalizedActionName, widget) + KEY_TEMPLATE);
        // com.acme.mail.<widget>Template
        templates.add(dot(controllerPackageName, controllerName, widget) + KEY_TEMPLATE);
        // com.acme.commands.mail.<widget>Template
        templates.add(dot(validateablePackageName, validateableName, widget) + KEY_TEMPLATE);
        // templates.scaffolding.<widget>Template
        templates.add(dot(DEFAULT_APPLICATION_TEMPLATE_PATH, widget + KEY_TEMPLATE));
        // griffon.plugins.scaffolding.<widget>Template
        templates.add(dot(DEFAULT_TEMPLATE_PATH, widget + KEY_TEMPLATE));

        return templates.toArray(new String[templates.size()]);
    }

    @Nonnull
    public static String[] unknownWidgetTemplates() {
        List<String> templates = new ArrayList<String>();

        // templates.scaffolding.UnknownTemplate
        templates.add(dot(DEFAULT_APPLICATION_TEMPLATE_PATH, KEY_UNKNOWN + KEY_TEMPLATE));
        // griffon.plugins.scaffolding.UnknownTemplate
        templates.add(dot(DEFAULT_TEMPLATE_PATH, KEY_UNKNOWN + KEY_TEMPLATE));

        return templates.toArray(new String[templates.size()]);
    }

    @Nonnull
    public static String getNaturalName(@Nonnull CommandObject commandObject) {
        return GriffonNameUtils.getNaturalName(
            getLogicalPropertyName(commandObject.getClass().getName(), COMMAND_OBJECT_SUFFIX));
    }

    public static String resolveMessage(@Nonnull GriffonController controller, @Nonnull String actionName, @Nonnull Validateable validateable, @Nonnull String property, String defaultValue) {
        for (String code : messageCodes(controller, actionName, validateable, property)) {
            try {
                return controller.getApplication().getMessageSource().getMessage(code);
            } catch (NoSuchMessageException e) {
                // ignore, continue with next code
            }
        }

        return defaultValue;
    }

    @Nonnull
    public static String qualifyActionName(@Nonnull GriffonController controller, @Nonnull String actionName) {
        return controller.getClass().getName() + "." + actionName;
    }

    @Nonnull
    public static String qualifyActionValidatable(@Nonnull GriffonController controller, @Nonnull String actionName, @Nonnull Validateable validateable) {
        return qualifyActionName(controller, actionName) + "." + getPropertyName(validateable.getClass());
    }

    @Nonnull
    public static String normalizeActionName(@Nonnull GriffonController controller, @Nonnull String actionName) {
        actionName = controller.getApplication().getActionManager().normalizeName(actionName).toLowerCase();
        if (isKeyword(actionName)) {
            actionName += ActionManager.ACTION.toLowerCase();
        }
        return actionName;
    }

    @Nonnull
    public static String dot(String... parts) {
        StringBuilder b = new StringBuilder();

        boolean first = true;

        for (String part : parts) {
            if (first) {
                first = false;
            } else {
                b.append(".");
            }
            b.append(part);
        }

        return b.toString();
    }

    @Nonnull
    @SuppressWarnings("ConstantConditions")
    private static Map<String, Object> loadUiDefaults(@Nonnull GriffonApplication application) {
        synchronized (LOCK) {
            Map<String, Object> config = new LinkedHashMap<>();

            try {
                Configuration globalDefaults = application.getInjector().getInstance(Configuration.class, named(GLOBAL_UIDEFAULTS_CONFIG_FILE));
                config.putAll(globalDefaults.asFlatMap());
            } catch (InstanceNotFoundException infe) {
                // ignore
            }

            Map<String, Object> localDefaults = application.getConfiguration().get(KEY_SCAFFOLDING_UIDEFAULTS, new LinkedHashMap<String, Object>());
            config.putAll(localDefaults);

            return config;
        }
    }

    public static Map<String, Object> getUiDefaults(@Nonnull GriffonApplication application) {
        synchronized (LOCK) {
            if (uiDefaults == null) {
                uiDefaults = loadUiDefaults(application);
            }
            return uiDefaults;
        }
    }
}
