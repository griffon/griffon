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
package org.codehaus.griffon.runtime.domain;

import griffon.persistence.Transient;
import griffon.plugins.domain.GriffonDomainClass;
import griffon.plugins.domain.GriffonDomainProperty;
import griffon.util.GriffonClassUtils;
import org.codehaus.griffon.runtime.core.artifact.ClassPropertyFetcher;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.*;

// import javax.persistence.Domain;
// import org.apache.commons.logging.Log;
// import org.apache.commons.logging.LogFactory;
// import org.codehaus.griffon.exceptions.GriffonConfigurationException;
// import org.codehaus.griffon.orm.hibernate.cfg.GriffonDomainBinder;
// import org.codehaus.griffon.orm.hibernate.cfg.PropertyConfig;
// import org.codehaus.griffon.plugins.PluginManagerHolder;
// import org.codehaus.griffon.validation.ConstrainedProperty;
// import org.codehaus.griffon.validation.ConstrainedPropertyAssembler;

/**
 * Utility methods used in configuring the Griffon Hibernate integration.
 *
 * @author Graeme Rocher (Grails)
 */
public class GriffonDomainConfigurationUtil {
    /**
     * Returns the association map for the specified domain class
     *
     * @param domainClass the domain class
     * @return The association map
     */
    public static Map<?, ?> getAssociationMap(Class<?> domainClass) {
        ClassPropertyFetcher cpf = ClassPropertyFetcher.forClass(domainClass);

        Map<?, ?> associationMap = cpf.getPropertyValue(GriffonDomainProperty.HAS_MANY, Map.class);
        if (associationMap == null) {
            associationMap = Collections.EMPTY_MAP;
        }
        return associationMap;
    }

    /**
     * Establish whether it's a basic type.
     *
     * @param prop The domain class property
     * @return True if it is basic
     */
    public static boolean isBasicType(GriffonDomainProperty prop) {
        if (prop == null) return false;
        return isBasicType(prop.getType());
    }

    private static final Set<String> BASIC_TYPES;

    static {
        Set<String> basics = new HashSet<String>(Arrays.asList(
            boolean.class.getName(),
            long.class.getName(),
            short.class.getName(),
            int.class.getName(),
            byte.class.getName(),
            float.class.getName(),
            double.class.getName(),
            char.class.getName(),
            Boolean.class.getName(),
            Long.class.getName(),
            Short.class.getName(),
            Integer.class.getName(),
            Byte.class.getName(),
            Float.class.getName(),
            Double.class.getName(),
            Character.class.getName(),
            String.class.getName(),
            java.util.Date.class.getName(),
            Time.class.getName(),
            Timestamp.class.getName(),
            java.sql.Date.class.getName(),
            BigDecimal.class.getName(),
            BigInteger.class.getName(),
            Locale.class.getName(),
            Calendar.class.getName(),
            GregorianCalendar.class.getName(),
            java.util.Currency.class.getName(),
            TimeZone.class.getName(),
            Object.class.getName(),
            Class.class.getName(),
            byte[].class.getName(),
            Byte[].class.getName(),
            char[].class.getName(),
            Character[].class.getName(),
            Blob.class.getName(),
            Clob.class.getName(),
            Serializable.class.getName(),
            URI.class.getName(),
            URL.class.getName()));
        BASIC_TYPES = Collections.unmodifiableSet(basics);
    }

    public static boolean isBasicType(Class<?> propType) {
        if (propType == null) return false;
        if (propType.isArray()) {
            return isBasicType(propType.getComponentType());
        }
        return BASIC_TYPES.contains(propType.getName());
    }

    /**
     * Checks whether is property is configurational.
     *
     * @param descriptor The property descriptor
     * @return True if it is configurational
     */
    public static boolean isNotConfigurational(PropertyDescriptor descriptor) {
        return isNotConfigurational(descriptor.getName());
    }

    /**
     * Checks whether is property is configurational.
     *
     * @param name The property name
     * @return True if it is configurational
     */
    public static boolean isNotConfigurational(String name) {
        return
            !GriffonDomainClass.STANDARD_PROPERTIES.contains(name) &&
                !GriffonDomainProperty.NON_CONFIGURATIONAL_PROPERTIES.contains(name);
    }

    public static boolean isTransientProperty(Class<?> owner, PropertyDescriptor descriptor) {
        final Field propertyField = GriffonClassUtils.getField(owner, descriptor.getName());
        return propertyField != null && propertyField.getAnnotation(Transient.class) != null;
    }
}