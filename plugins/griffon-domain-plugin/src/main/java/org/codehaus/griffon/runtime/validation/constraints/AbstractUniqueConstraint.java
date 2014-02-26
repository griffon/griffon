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

import griffon.exceptions.GriffonException;
import griffon.exceptions.PropertyException;
import griffon.plugins.domain.GriffonDomainProperty;
import griffon.plugins.validation.Errors;
import griffon.util.GriffonClassUtils;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * A constraint that validates the uniqueness of a property (will query the
 * database during validation process).
 *
 * @author Graeme Rocher (Grails 0.4)
 * @author Sergey Nebolsin (Grails 0.4)
 * @author Andres Almiray
 */
public abstract class AbstractUniqueConstraint extends AbstractConstraint {
    private static final String DEFAULT_NOT_UNIQUE_MESSAGE_CODE = "default.not.unique.message";
    public static final String VALIDATION_DSL_NAME = "unique";

    private boolean unique;
    private List<String> uniquenessGroup = new ArrayList<>();

    /**
     * @return Returns the unique.
     */
    public boolean isUnique() {
        return unique;
    }

    /**
     * @return Whether the property is unique within a group
     */
    public boolean isUniqueWithinGroup() {
        return !uniquenessGroup.isEmpty();
    }

    @Override
    public boolean supports(@Nonnull Class<?> type) {
        return true;
    }

    @Override
    public void setParameter(@Nonnull Object constraintParameter) {
        if (!(constraintParameter instanceof Boolean ||
            constraintParameter instanceof String ||
            constraintParameter instanceof CharSequence ||
            constraintParameter instanceof List<?>)) {
            throw new IllegalArgumentException("Parameter for constraint [" + VALIDATION_DSL_NAME +
                "] of property [" + constraintPropertyName + "] of class [" +
                constraintOwningClass + "] must be a boolean or string value");
        }

        if (constraintParameter instanceof List<?>) {
            for (Object parameter : ((List<?>) constraintParameter)) {
                if (!(parameter instanceof String || parameter instanceof CharSequence)) {
                    throw new IllegalArgumentException("Parameter for constraint [" + VALIDATION_DSL_NAME +
                        "] of property [" + constraintPropertyName + "] of class [" +
                        constraintOwningClass + "] must be a boolean or string value");
                }
                uniquenessGroup.add(parameter.toString());
            }
        } else if (constraintParameter instanceof String || constraintParameter instanceof CharSequence) {
            uniquenessGroup.add(constraintParameter.toString());
            unique = true;
        } else {
            unique = ((Boolean) constraintParameter).booleanValue();
        }

        if (!uniquenessGroup.isEmpty()) {
            unique = true;
            for (Object anUniquenessGroup : uniquenessGroup) {
                String propertyName = (String) anUniquenessGroup;
                if (GriffonClassUtils.getPropertyType(constraintOwningClass, propertyName) == null) {
                    throw new IllegalArgumentException("Scope for constraint [" + VALIDATION_DSL_NAME +
                        "] of property [" + constraintPropertyName + "] of class [" +
                        constraintOwningClass + "] must be a valid property name of same class");
                }
            }
        }

        super.setParameter(constraintParameter);
    }

    @Nonnull
    public String getName() {
        return VALIDATION_DSL_NAME;
    }

    @Override
    protected void processValidate(@Nonnull Object target, Object propertyValue, @Nonnull Errors errors) {
        if (!unique) {
            return;
        }

        try {
            GriffonClassUtils.getPropertyValue(target, GriffonDomainProperty.IDENTITY);
        } catch (PropertyException pe) {
            throw new GriffonException("Target of [unique] constraints [" + target +
                "] is not a domain instance. Unique constraint can only be applied to " +
                "domain classes and not custom user types or embedded instances");
        }

        doUniqueConstraintCheck(target, propertyValue, errors);
        /*
        HibernateTemplate hibernateTemplate = getHibernateTemplate();
        Assert.state(hibernateTemplate != null,
                "Unable use [unique] constraint, no Hibernate SessionFactory found!");
        List<?> results = hibernateTemplate.executeFind(new HibernateCallback<List<?>>() {
            public List<?> doInHibernate(Session session) throws HibernateException {
                session.setFlushMode(FlushMode.MANUAL);
                try {
                    boolean shouldValidate = true;
                    Class<?> constraintClass = constraintOwningClass;
                    if (propertyValue != null && DomainClassArtefactHandler.isDomainClass(propertyValue.getClass())) {
                        shouldValidate = session.contains(propertyValue);
                    }
                    if (shouldValidate) {
                        GrailsApplication application = (GrailsApplication) applicationContext.getBean(GrailsApplication.APPLICATION_ID);
                        GrailsDomainClass domainClass = (GrailsDomainClass) application.getArtefact(DomainClassArtefactHandler.TYPE, constraintClass.getName());
                        if (domainClass != null && !domainClass.isRoot()) {
                            GrailsDomainClassProperty property = domainClass.getPropertyByName(constraintPropertyName);
                            while (property.isInherited() && domainClass != null) {
                                domainClass = (GrailsDomainClass) application.getArtefact(
                                        DomainClassArtefactHandler.TYPE, domainClass.getClazz().getSuperclass().getName());
                                if (domainClass != null) {
                                    property = domainClass.getPropertyByName(constraintPropertyName);
                                }
                            }
                            constraintClass = domainClass != null ? domainClass.getClazz() : constraintClass;
                        }
                        Criteria criteria = session.createCriteria(constraintClass)
                                .add(Restrictions.eq(constraintPropertyName, propertyValue));
                        if (uniquenessGroup != null) {
                            for (Object anUniquenessGroup : uniquenessGroup) {
                                String uniquenessGroupPropertyName = (String) anUniquenessGroup;
                                Object uniquenessGroupPropertyValue = GrailsClassUtils.getPropertyOrStaticPropertyOrFieldValue(target, uniquenessGroupPropertyName);

                                if (uniquenessGroupPropertyValue != null && DomainClassArtefactHandler.isDomainClass(uniquenessGroupPropertyValue.getClass())) {
                                    try {
                                        // We are merely verifying that the object is not transient here
                                        session.lock(uniquenessGroupPropertyValue, LockMode.NONE);
                                    } catch (TransientObjectException e) {
                                        shouldValidate = false;
                                    }
                                }
                                if (shouldValidate) {
                                    criteria.add(Restrictions.eq(uniquenessGroupPropertyName, uniquenessGroupPropertyValue));
                                } else {
                                    break; // we aren't validating, so no point continuing
                                }
                            }
                        }

                        if (shouldValidate) {
                            return criteria.list();
                        }
                        return Collections.EMPTY_LIST;
                    }
                    return Collections.EMPTY_LIST;
                } finally {
                    session.setFlushMode(FlushMode.AUTO);
                }
            }
        });

        if (results.isEmpty()) {
            return;
        }

        boolean reject = false;
        if (id != null) {
            Object existing = results.get(0);
            Object existingId = null;
            try {
                existingId = InvokerHelper.invokeMethod(existing, "ident", null);
            } catch (Exception e) {
                // result is not a domain class
            }
            if (!id.equals(existingId)) {
                reject = true;
            }
        } else {
            reject = true;
        }
        if (reject) {
            Object[] args = {constraintPropertyName, constraintOwningClass, propertyValue};
            rejectValue(target, errors, UNIQUE_CONSTRAINT, args, getDefaultMessage(DEFAULT_NOT_UNIQUE_MESSAGE_CODE));
        }
        */
    }

    protected abstract void doUniqueConstraintCheck(Object target, Object propertyValue, Errors errors);


    public List<String> getUniquenessGroup() {
        return uniquenessGroup;
    }
}
