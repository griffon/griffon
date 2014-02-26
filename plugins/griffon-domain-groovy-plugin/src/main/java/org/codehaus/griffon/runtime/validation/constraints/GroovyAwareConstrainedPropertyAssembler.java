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

import griffon.core.GriffonApplication;
import griffon.plugins.validation.constraints.ConstrainedProperty;
import griffon.plugins.validation.constraints.Constraint;
import griffon.plugins.validation.constraints.ConstraintsEvaluator;
import griffon.types.*;
import griffon.types.IntRange;
import griffon.types.Range;
import groovy.lang.*;
import groovy.util.BuilderSupport;
import org.codehaus.griffon.runtime.core.artifact.ClassPropertyFetcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.beans.PropertyDescriptor;
import java.util.*;

import static griffon.util.TypeUtils.*;

/**
 * @author Andres Almiray
 */
public class GroovyAwareConstrainedPropertyAssembler extends DefaultConstrainedPropertyAssembler {
    private static final Logger LOG = LoggerFactory.getLogger(GroovyAwareConstrainedPropertyAssembler.class);

    @Inject
    public GroovyAwareConstrainedPropertyAssembler(@Nonnull GriffonApplication application) {
        super(application);
    }

    @Override
    public void assemble(@Nonnull Object constraints) {
        if (constraints instanceof Closure) {
            ConstrainedPropertyBuilder builder = new ConstrainedPropertyBuilder();
            Closure<?> c = (Closure<?>) constraints;
            c = (Closure<?>) c.clone();
            c.setResolveStrategy(Closure.DELEGATE_ONLY);
            c.setDelegate(builder);
            c.call();
        } else {
            super.assemble(constraints);
        }
    }

    private class ConstrainedPropertyBuilder extends BuilderSupport {
        private MetaClass targetMetaClass;

        public ConstrainedPropertyBuilder() {
            classPropertyFetcher = ClassPropertyFetcher.forClass(targetClass);
            this.targetMetaClass = GroovySystem.getMetaClassRegistry().getMetaClass(targetClass);
        }

        @Override
        protected Object doInvokeMethod(String methodName, Object name, Object args) {
            try {
                return super.doInvokeMethod(methodName, name, args);
            } catch (MissingMethodException e) {
                return targetMetaClass.invokeMethod(targetClass, methodName, args);
            }
        }

        @Override
        public Object getProperty(String property) {
            try {
                return super.getProperty(property);
            } catch (MissingPropertyException e) {
                return targetMetaClass.getProperty(targetClass, property);
            }
        }

        @Override
        public void setProperty(String property, Object newValue) {
            try {
                super.setProperty(property, newValue);
            } catch (MissingPropertyException e) {
                targetMetaClass.setProperty(targetClass, property, newValue);
            }
        }

        @Override
        @SuppressWarnings("rawtypes")
        protected Object createNode(Object name, Map attributes) {
            // we do this so that missing property exception is thrown if it doesn't exist

            String property = (String) name;
            ConstrainedProperty cp;
            if (constrainedProperties.containsKey(property)) {
                cp = constrainedProperties.get(property);
            } else {
                Class<?> propertyType = classPropertyFetcher.getPropertyType(property);
                if (propertyType == null) {
                    throw new MissingMethodException(property, targetClass, new Object[]{attributes}, true);
                }
                cp = new ConstrainedProperty(targetClass, property, propertyType);
                cp.setMessageSource(getApplication().getMessageSource());
                cp.setOrder(order++);
                constrainedProperties.put(property, cp);
            }

            if (cp.getPropertyType() == null) {
                if (!IMPORT_FROM_CONSTRAINT.equals(name)) {
                    if (LOG.isWarnEnabled()) {
                        LOG.warn("Property [" + cp.getPropertyName() + "] not found in domain class " +
                            targetClass.getName() + "; cannot apply constraints: " + attributes);
                    }
                }
                return cp;
            }

            for (Object o : attributes.keySet()) {
                String constraintName = (String) o;
                final Object value = attributes.get(constraintName);
                if (SHARED_CONSTRAINT.equals(constraintName)) {
                    if (value != null) {
                        sharedConstraints.put(property, value.toString());
                    }
                    continue;
                }
                addConstraint(cp, constraintName, value);
            }

            return cp;
        }

        private void addConstraint(ConstrainedProperty cp, String constraintName, Object value) {
            if (value instanceof groovy.lang.Range) {
                value = toGriffonRange((groovy.lang.Range) value);
            }

            if (cp.supportsContraint(constraintName)) {
                cp.applyConstraint(constraintName, value);
            } else {
                if (ConstrainedProperty.hasRegisteredConstraint(constraintName)) {
                    // constraint is registered but doesn't support this property's type
                    if (LOG.isWarnEnabled()) {
                        LOG.warn("Property [" + cp.getPropertyName() + "] of class " +
                            targetClass.getName() + " has type [" + cp.getPropertyType().getName() +
                            "] and doesn't support constraint [" + constraintName +
                            "]. This constraint will not be checked during validation.");
                    }
                } else {
                    // in the case where the constraint is not supported we still retain meta data
                    // about the constraint in case its needed for other things
                    cp.addMetaConstraint(constraintName, value);
                }
            }
        }

        @SuppressWarnings("rawtypes")
        @Override
        protected Object createNode(Object name, Map attributes, Object value) {
            if (IMPORT_FROM_CONSTRAINT.equals(name) && (value instanceof Class)) {
                return handleImportFrom(attributes, (Class) value);
            }
            throw new MissingMethodException((String) name, targetClass, new Object[]{attributes, value});
        }


        @SuppressWarnings({"unchecked", "rawtypes"})
        private Object handleImportFrom(Map attributes, Class importFromClazz) {
            ConstraintsEvaluator constraintEvaluator = getApplication().getInjector().getInstance(ConstraintsEvaluator.class);
            Map importFromConstrainedProperties = constraintEvaluator.evaluate(importFromClazz);

            PropertyDescriptor[] targetPropertyDescriptorArray = classPropertyFetcher.getPropertyDescriptors();

            List toBeIncludedPropertyNamesParam = (List) attributes.get("include");
            List toBeExcludedPropertyNamesParam = (List) attributes.get("exclude");

            List<String> resultingPropertyNames = new ArrayList<>();
            for (PropertyDescriptor targetPropertyDescriptor : targetPropertyDescriptorArray) {
                String targetPropertyName = targetPropertyDescriptor.getName();

                if (toBeIncludedPropertyNamesParam == null) {
                    resultingPropertyNames.add(targetPropertyName);
                } else if (isListOfRegexpsContainsString(toBeIncludedPropertyNamesParam, targetPropertyName)) {
                    resultingPropertyNames.add(targetPropertyName);
                }

                if (toBeExcludedPropertyNamesParam != null
                    && isListOfRegexpsContainsString(toBeExcludedPropertyNamesParam, targetPropertyName)) {
                    resultingPropertyNames.remove(targetPropertyName);
                }
            }

            resultingPropertyNames.remove("class");
            resultingPropertyNames.remove("metaClass");

            for (String targetPropertyName : resultingPropertyNames) {
                ConstrainedProperty importFromConstrainedProperty =
                    (ConstrainedProperty) importFromConstrainedProperties.get(targetPropertyName);

                if (importFromConstrainedProperty != null) {
                    // Map importFromConstrainedPropertyAttributes = importFromConstrainedProperty.getAttributes();
                    // createNode(targetPropertyName, importFromConstrainedPropertyAttributes);
                    Map importFromConstrainedPropertyAttributes = new HashMap();
                    for (Constraint importFromAppliedConstraint : importFromConstrainedProperty.getAppliedConstraints()) {
                        String importFromAppliedConstraintName = importFromAppliedConstraint.getName();
                        Object importFromAppliedConstraintParameter = importFromAppliedConstraint.getParameter();
                        importFromConstrainedPropertyAttributes.put(
                            importFromAppliedConstraintName, importFromAppliedConstraintParameter);
                    }

                    createNode(targetPropertyName, importFromConstrainedPropertyAttributes);
                }
            }

            return null;
        }

        private boolean isListOfRegexpsContainsString(List<String> listOfStrings, String stringToMatch) {
            boolean result = false;

            for (String listElement : listOfStrings) {
                if (stringToMatch.matches(listElement)) {
                    result = true;
                    break;
                }
            }

            return result;
        }

        @Override
        protected void setParent(Object parent, Object child) {
            // do nothing
        }

        @Override
        protected Object createNode(Object name) {
            return createNode(name, Collections.EMPTY_MAP);
        }

        @Override
        protected Object createNode(Object name, Object value) {
            return createNode(name, Collections.EMPTY_MAP, value);
        }

        public Map<String, ConstrainedProperty> getConstrainedProperties() {
            return constrainedProperties;
        }
    }

    private static Range toGriffonRange(groovy.lang.Range range) {
        Comparable from = range.getFrom();
        Comparable to = range.getTo();

        if (from instanceof Enum && to instanceof Enum) {
            return new EnumRange<>((Enum) from, (Enum) to);
        } else if (isDouble(from.getClass()) || isDouble(to.getClass())) {
            return new DoubleRange(castToDouble(from), castToDouble(to));
        } else if (isFloat(from.getClass()) || isFloat(to.getClass())) {
            return new FloatRange(castToFloat(from), castToFloat(to));
        } else if (isLong(from.getClass()) || isLong(to.getClass())) {
            return new LongRange(castToLong(from), castToLong(to));
        } else if (isCharacter(from.getClass()) || isCharacter(to.getClass())) {
            return new CharRange(castToChar(from), castToChar(to));
        } else if (isInteger(from.getClass()) || isInteger(to.getClass())) {
            return new IntRange(castToInt(from), castToInt(to));
        }

        return null;
    }
}
