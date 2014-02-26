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
package org.codehaus.griffon.runtime.domain.datastore;

import griffon.core.GriffonApplication;
import griffon.plugins.domain.GriffonDomain;
import griffon.plugins.domain.GriffonDomainClass;
import griffon.plugins.domain.GriffonDomainHandler;
import griffon.plugins.domain.GriffonDomainProperty;
import griffon.plugins.domain.datastore.Dataset;
import griffon.plugins.domain.datastore.Datastore;
import griffon.plugins.domain.datastore.DatastoreCallback;
import griffon.plugins.domain.datastore.DatastoreHandler;
import griffon.plugins.domain.methods.InstanceMethodInvocation;
import griffon.plugins.domain.methods.StaticMethodInvocation;
import griffon.plugins.domain.orm.Criterion;
import griffon.plugins.validation.constraints.ConstrainedProperty;
import griffon.plugins.validation.exceptions.ValidationException;
import griffon.util.CollectionUtils;
import org.codehaus.griffon.runtime.domain.AbstractGriffonDomainHandler;
import org.codehaus.griffon.runtime.domain.methods.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.*;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 */
public class MemoryGriffonDomainHandler extends AbstractGriffonDomainHandler {
    private static final String MAPPING = "memory";

    private final DatastoreHandler datastoreHandler;

    @Inject
    public MemoryGriffonDomainHandler(final @Nonnull GriffonApplication application, final @Nonnull DatastoreHandler datastoreHandler) {
        super(application);
        this.datastoreHandler = requireNonNull(datastoreHandler, "Argument 'datastoreHandler' cannot be null");
    }

    @Nonnull
    protected Map<String, InstanceMethodInvocation> getInstanceMethods() {
        Map<String, InstanceMethodInvocation> instanceMethods = new LinkedHashMap<>();
        instanceMethods.put(SaveMethod.METHOD_NAME, new SaveMethod(this));
        instanceMethods.put(DeleteMethod.METHOD_NAME, new DeleteMethod(this));
        return instanceMethods;
    }

    @Nonnull
    protected Map<String, StaticMethodInvocation> getStaticMethods() {
        Map<String, StaticMethodInvocation> staticMethods = new LinkedHashMap<>();
        staticMethods.put(CountByMethod.METHOD_NAME, new CountByMethod(this));
        staticMethods.put(CountMethod.METHOD_NAME, new CountMethod(this));
        staticMethods.put(CreateMethod.METHOD_NAME, new CreateMethod(this));
        staticMethods.put(ExistsMethod.METHOD_NAME, new ExistsMethod(this));
        staticMethods.put(FindAllByMethod.METHOD_NAME, new FindAllByMethod(this));
        staticMethods.put(FindAllMethod.METHOD_NAME, new FindAllMethod(this));
        staticMethods.put(FindAllWhereMethod.METHOD_NAME, new FindAllWhereMethod(this));
        staticMethods.put(FindByMethod.METHOD_NAME, new FindByMethod(this));
        staticMethods.put(FindMethod.METHOD_NAME, new FindMethod(this));
        staticMethods.put(FindOrCreateByMethod.METHOD_NAME, new FindOrCreateByMethod(this));
        staticMethods.put(FindOrCreateWhereMethod.METHOD_NAME, new FindOrCreateWhereMethod(this));
        staticMethods.put(FindOrSaveByMethod.METHOD_NAME, new FindOrSaveByMethod(this));
        staticMethods.put(FindOrSaveWhereMethod.METHOD_NAME, new FindOrSaveWhereMethod(this));
        staticMethods.put(FindWhereMethod.METHOD_NAME, new FindWhereMethod(this));
        staticMethods.put(FirstMethod.METHOD_NAME, new FirstMethod(this));
        staticMethods.put(GetAllMethod.METHOD_NAME, new GetAllMethod(this));
        staticMethods.put(GetMethod.METHOD_NAME, new GetMethod(this));
        staticMethods.put(LastMethod.METHOD_NAME, new LastMethod(this));
        staticMethods.put(ListMethod.METHOD_NAME, new ListMethod(this));
        staticMethods.put(ListOrderByMethod.METHOD_NAME, new ListOrderByMethod(this));
        staticMethods.put(WithCriteriaMethod.METHOD_NAME, new WithCriteriaMethod(this));
        return staticMethods;
    }

    @Nonnull
    public String getMapping() {
        return MAPPING;
    }

    protected static interface DatasetCallback<T extends GriffonDomain, R> {
        R handle(Dataset<T> dataset);
    }

    @Nullable
    protected <T extends GriffonDomain, R> R withDataset(final @Nonnull GriffonDomainClass<T> domainClass, final @Nonnull DatasetCallback<T, R> callback) {
        requireNonNull(domainClass, "domainClass");
        requireNonNull(callback, "callback");
        return datastoreHandler.withDatastore(new DatastoreCallback<R>() {
            @Override
            public R handle(final @Nonnull String datastoreName, final @Nonnull Datastore datastore) {
                Dataset<T> dataset = datastore.dataset(domainClass);
                return callback.handle(dataset);
            }
        });
    }

    private class SaveMethod extends AbstractSavePersistentMethod {
        public SaveMethod(final @Nonnull GriffonDomainHandler griffonDomainHandler) {
            super(griffonDomainHandler);
        }

        @Override
        protected <T extends GriffonDomain> boolean shouldInsert(final @Nonnull GriffonDomainClass<T> domainClass, final @Nonnull T target, final @Nonnull Object[] arguments) {
            GriffonDomainProperty identity = identityOf(target);
            Object identityValue = identity.getValue(target);
            return identityValue == null;
        }

        @Nullable
        @Override
        protected <T extends GriffonDomain> T insert(final @Nonnull GriffonDomainClass<T> domainClass, final @Nonnull T target, final @Nonnull Object[] arguments, final @Nonnull Map<String, Object> params) {
            return withDataset(domainClass, new DatasetCallback<T, T>() {
                @Override
                public T handle(Dataset<T> dataset) {
                    if (null != checkUniqueConstraints(domainClass, target, params, dataset)) {
                        GriffonDomainProperty identity = identityOf(target);
                        identity.setValue(target, dataset.nextId());
                        return dataset.save(target);
                    }
                    return null;
                }
            });
        }

        @Nullable
        @Override
        protected <T extends GriffonDomain> T save(final @Nonnull GriffonDomainClass<T> domainClass, final @Nonnull T target, final @Nonnull Object[] arguments, final @Nonnull Map<String, Object> params) {
            return withDataset(domainClass, new DatasetCallback<T, T>() {
                @Override
                public T handle(Dataset<T> dataset) {
                    if (null != checkUniqueConstraints(domainClass, target, params, dataset)) {
                        return dataset.save(target);
                    }
                    return null;
                }
            });
        }

        @SuppressWarnings("ConstantConditions")
        private <T extends GriffonDomain> GriffonDomain checkUniqueConstraints(GriffonDomainClass<T> domainClass, T target, Map<String, Object> params, Dataset<T> dataset) {
            boolean validate = (Boolean) params.get(VALIDATE);
            boolean failOnError = (Boolean) params.get(FAIL_ON_ERROR);

            if (validate) {
                Map<String, ConstrainedProperty> constrainedProperties = domainClass.getConstrainedProperties();
                for (Map.Entry<String, ConstrainedProperty> entry : constrainedProperties.entrySet()) {
                    String propertyName = entry.getKey();
                    GriffonDomainProperty domainProperty = domainClass.getPropertyByName(propertyName);
                    ConstrainedProperty constrainedProperty = entry.getValue();
                    Object uniqueValue = constrainedProperty.getMetaConstraintValue("unique");
                    if (null != uniqueValue && (Boolean) uniqueValue) {
                        Object value = domainProperty.getValue(target);
                        Map<String, Object> args = CollectionUtils.<String, Object>map().e(propertyName, value);
                        GriffonDomain other = dataset.first(args);
                        if (null != other && target != other) {
                            if (failOnError) {
                                throw new ValidationException("Constraint 'unique' failed validation for property '" + propertyName + "' with value " + value);
                            } else {
                                target.getErrors().rejectField(propertyName, value, "unique", null);
                            }
                        }
                    }
                }
                if (target.getErrors().hasErrors()) return null;
            }

            return target;
        }
    }

    private class DeleteMethod extends AbstractDeletePersistentMethod {
        public DeleteMethod(final @Nonnull GriffonDomainHandler griffonDomainHandler) {
            super(griffonDomainHandler);
        }

        @Nonnull
        @Override
        @SuppressWarnings("ConstantConditions")
        protected <T extends GriffonDomain> T delete(final @Nonnull GriffonDomainClass<T> domainClass, final @Nonnull T target, final @Nonnull Map<String, Object> params) {
            return withDataset(domainClass, new DatasetCallback<T, T>() {
                @Override
                public T handle(Dataset<T> dataset) {
                    return dataset.remove(target);
                }
            });
        }
    }

    private class CreateMethod extends AbstractCreatePersistentMethod {
        public CreateMethod(GriffonDomainHandler domainHandler) {
            super(domainHandler);
        }
    }

    private class GetMethod extends AbstractGetPersistentMethod {
        public GetMethod(final @Nonnull GriffonDomainHandler griffonDomainHandler) {
            super(griffonDomainHandler);
        }

        @Nullable
        @Override
        protected <T extends GriffonDomain> T get(final @Nonnull GriffonDomainClass<T> domainClass, final @Nonnull Object key) {
            return withDataset(domainClass, new DatasetCallback<T, T>() {
                @Override
                public T handle(Dataset<T> dataset) {
                    return dataset.fetch(key);
                }
            });
        }
    }

    private class ExistsMethod extends AbstractExistsPersistentMethod {
        public ExistsMethod(final @Nonnull GriffonDomainHandler griffonDomainHandler) {
            super(griffonDomainHandler);
        }

        @Override
        @SuppressWarnings("ConstantConditions")
        protected <T extends GriffonDomain> boolean exists(final @Nonnull GriffonDomainClass<T> domainClass, final @Nonnull Object key) {
            return withDataset(domainClass, new DatasetCallback<T, Boolean>() {
                @Override
                public Boolean handle(Dataset<T> dataset) {
                    return dataset.fetch(key) != null;
                }
            });
        }
    }

    private class GetAllMethod extends AbstractGetAllPersistentMethod {
        public GetAllMethod(final @Nonnull GriffonDomainHandler griffonDomainHandler) {
            super(griffonDomainHandler);
        }

        @Nonnull
        @Override
        @SuppressWarnings("ConstantConditions")
        protected <T extends GriffonDomain> Collection<T> getAll(final @Nonnull GriffonDomainClass<T> domainClass) {
            return withDataset(domainClass, new DatasetCallback<T, Collection<T>>() {
                @Override
                public Collection<T> handle(Dataset<T> dataset) {
                    return dataset.list();
                }
            });
        }

        @Nonnull
        @Override
        @SuppressWarnings("ConstantConditions")
        protected <T extends GriffonDomain> Collection<T> getAllByIdentities(final @Nonnull GriffonDomainClass<T> domainClass, final @Nonnull List<Object> identities) {
            return withDataset(domainClass, new DatasetCallback<T, Collection<T>>() {
                @Override
                public Collection<T> handle(Dataset<T> dataset) {
                    List<T> entities = new ArrayList<>();
                    if (identities.size() > 0) {
                        for (Object identity : identities) {
                            T entity = dataset.fetch(identity);
                            if (entity != null) {
                                entities.add(entity);
                            }
                        }
                    }
                    Collections.sort(entities, IDENTITY_COMPARATOR);
                    return entities;
                }
            });
        }

        @Nonnull
        @Override
        @SuppressWarnings("ConstantConditions")
        protected <T extends GriffonDomain> Collection<T> getAllByIdentities(final @Nonnull GriffonDomainClass<T> domainClass, final @Nonnull Object[] identities) {
            return withDataset(domainClass, new DatasetCallback<T, Collection<T>>() {
                @Override
                public Collection<T> handle(Dataset<T> dataset) {
                    List<T> entities = new ArrayList<>();
                    if (identities.length > 0) {
                        for (Object identity : identities) {
                            T entity = dataset.fetch(identity);
                            if (entity != null) {
                                entities.add(entity);
                            }
                        }
                    }
                    Collections.sort(entities, IDENTITY_COMPARATOR);
                    return entities;
                }
            });
        }
    }

    private class ListMethod extends AbstractListPersistentMethod {
        public ListMethod(final @Nonnull GriffonDomainHandler griffonDomainHandler) {
            super(griffonDomainHandler);
        }

        @Nonnull
        @Override
        @SuppressWarnings("ConstantConditions")
        protected <T extends GriffonDomain> Collection<T> list(final @Nonnull GriffonDomainClass<T> domainClass, final @Nonnull Map<String, Object> options) {
            return withDataset(domainClass, new DatasetCallback<T, Collection<T>>() {
                @Override
                public Collection<T> handle(Dataset<T> dataset) {
                    if (dataset == null) {
                        return Collections.emptyList();
                    } else {
                        return dataset.list(options);
                    }
                }
            });
        }
    }

    private class ListOrderByMethod extends AbstractListOrderByPersistentMethod {
        protected ListOrderByMethod(final @Nonnull GriffonDomainHandler griffonDomainHandler) {
            super(griffonDomainHandler);
        }

        @Nonnull
        @Override
        @SuppressWarnings("ConstantConditions")
        protected <T extends GriffonDomain> Collection<T> listOrderBy(final @Nonnull GriffonDomainClass<T> domainClass, final @Nonnull String propertyName, final @Nonnull Map<String, Object> params) {
            return withDataset(domainClass, new DatasetCallback<T, Collection<T>>() {
                @Override
                public Collection<T> handle(Dataset<T> dataset) {
                    List<T> entities = dataset.list(params);
                    Collections.sort(entities, new GriffonDomain.Comparator(propertyName, (String) params.get(ListOrderByMethod.ORDER)));
                    return entities;
                }
            });
        }
    }

    private class CountMethod extends AbstractCountPersistentMethod {
        public CountMethod(final @Nonnull GriffonDomainHandler griffonDomainHandler) {
            super(griffonDomainHandler);
        }

        @Override
        @SuppressWarnings("ConstantConditions")
        protected <T extends GriffonDomain> int count(final @Nonnull GriffonDomainClass<T> domainClass) {
            return withDataset(domainClass, new DatasetCallback<T, Integer>() {
                @Override
                public Integer handle(Dataset<T> dataset) {
                    return dataset.size();
                }
            });
        }
    }

    private class FindAllWhereMethod extends AbstractFindAllWherePersistentMethod {
        public FindAllWhereMethod(final @Nonnull GriffonDomainHandler griffonDomainHandler) {
            super(griffonDomainHandler);
        }

        @Nonnull
        @Override
        @SuppressWarnings("ConstantConditions")
        protected <T extends GriffonDomain> Collection<T> findByParams(final @Nonnull GriffonDomainClass<T> domainClass, final @Nonnull Map<String, Object> params, final @Nonnull Map<String, Object> options) {
            return withDataset(domainClass, new DatasetCallback<T, Collection<T>>() {
                @Override
                public Collection<T> handle(Dataset<T> dataset) {
                    return dataset.query(params, options);
                }
            });
        }
    }

    private class FindWhereMethod extends AbstractFindWherePersistentMethod {
        public FindWhereMethod(final @Nonnull GriffonDomainHandler griffonDomainHandler) {
            super(griffonDomainHandler);
        }

        @Override
        @SuppressWarnings("ConstantConditions")
        protected <T extends GriffonDomain> T findByParams(final @Nonnull GriffonDomainClass<T> domainClass, final @Nonnull Map<String, Object> params) {
            return withDataset(domainClass, new DatasetCallback<T, T>() {
                @Override
                public T handle(Dataset<T> dataset) {
                    return dataset.first(params);
                }
            });
        }
    }

    private class FindMethod extends AbstractFindPersistentMethod {
        public FindMethod(final @Nonnull GriffonDomainHandler griffonDomainHandler) {
            super(griffonDomainHandler);
        }

        @Nullable
        @Override
        @SuppressWarnings("ConstantConditions")
        protected <T extends GriffonDomain> T findByProperties(final @Nonnull GriffonDomainClass<T> domainClass, final @Nonnull Map<String, Object> properties) {
            return withDataset(domainClass, new DatasetCallback<T, T>() {
                @Override
                public T handle(Dataset<T> dataset) {
                    return dataset.first(properties);
                }
            });
        }

        @Nullable
        @Override
        @SuppressWarnings("ConstantConditions")
        protected <T extends GriffonDomain> T findByExample(final @Nonnull GriffonDomainClass<T> domainClass, final @Nonnull Object example) {
            return withDataset(domainClass, new DatasetCallback<T, T>() {
                @Override
                public T handle(Dataset<T> dataset) {
                    return dataset.first(example);
                }
            });
        }

        @Nullable
        @Override
        @SuppressWarnings("ConstantConditions")
        protected <T extends GriffonDomain> T findByCriterion(final @Nonnull GriffonDomainClass<T> domainClass, final @Nonnull Criterion criterion) {
            return withDataset(domainClass, new DatasetCallback<T, T>() {
                @Override
                public T handle(Dataset<T> dataset) {
                    return dataset.first(criterion);
                }
            });
        }
    }

    private class FirstMethod extends AbstractFirstPersistentMethod {
        public FirstMethod(final @Nonnull GriffonDomainHandler griffonDomainHandler) {
            super(griffonDomainHandler);
        }

        @Nullable
        @Override
        @SuppressWarnings("ConstantConditions")
        protected <T extends GriffonDomain> T firstByPropertyName(final @Nonnull GriffonDomainClass<T> domainClass, final @Nonnull String propertyName) {
            final Map<String, Object> options = CollectionUtils.<String, Object>map()
                .e(Dataset.KEY_SORT, propertyName);
            return withDataset(domainClass, new DatasetCallback<T, T>() {
                @Override
                public T handle(Dataset<T> dataset) {
                    List<T> objects = dataset.list(options);
                    return objects.size() > 0 ? objects.get(0) : null;
                }
            });
        }
    }

    private class LastMethod extends AbstractLastPersistentMethod {
        public LastMethod(final @Nonnull GriffonDomainHandler griffonDomainHandler) {
            super(griffonDomainHandler);
        }

        @Nullable
        @Override
        @SuppressWarnings("ConstantConditions")
        protected <T extends GriffonDomain> T lastByPropertyName(final @Nonnull GriffonDomainClass<T> domainClass, final @Nonnull String propertyName) {
            final Map<String, Object> options = CollectionUtils.<String, Object>map()
                .e(Dataset.KEY_SORT, propertyName)
                .e(Dataset.KEY_ORDER, "desc");
            return withDataset(domainClass, new DatasetCallback<T, T>() {
                @Override
                public T handle(Dataset<T> dataset) {
                    List<T> objects = dataset.list(options);
                    return objects.size() > 0 ? objects.get(0) : null;
                }
            });
        }
    }

    private class FindAllMethod extends AbstractFindAllPersistentMethod {
        public FindAllMethod(final @Nonnull GriffonDomainHandler griffonDomainHandler) {
            super(griffonDomainHandler);
        }

        @Nonnull
        @Override
        @SuppressWarnings("ConstantConditions")
        protected <T extends GriffonDomain> Collection<T> findByProperties(final @Nonnull GriffonDomainClass<T> domainClass, final @Nonnull Map<String, Object> properties, final @Nonnull Map<String, Object> options) {
            return withDataset(domainClass, new DatasetCallback<T, Collection<T>>() {
                @Override
                public Collection<T> handle(Dataset<T> dataset) {
                    return dataset.query(properties, options);
                }
            });
        }

        @Nonnull
        @Override
        @SuppressWarnings("ConstantConditions")
        protected <T extends GriffonDomain> Collection<T> findByExample(final @Nonnull GriffonDomainClass<T> domainClass, final @Nonnull Object example, final @Nonnull Map<String, Object> options) {
            return withDataset(domainClass, new DatasetCallback<T, Collection<T>>() {
                @Override
                public Collection<T> handle(Dataset<T> dataset) {
                    return dataset.query(example, options);
                }
            });
        }

        @Nonnull
        @Override
        @SuppressWarnings("ConstantConditions")
        protected <T extends GriffonDomain> Collection<T> findByCriterion(final @Nonnull GriffonDomainClass<T> domainClass, final @Nonnull Criterion criterion, final @Nonnull Map<String, Object> options) {
            return withDataset(domainClass, new DatasetCallback<T, Collection<T>>() {
                @Override
                public Collection<T> handle(Dataset<T> dataset) {
                    return dataset.query(criterion, options);
                }
            });
        }
    }

    private class WithCriteriaMethod extends AbstractWithCriteriaPersistentMethod {
        public WithCriteriaMethod(final @Nonnull GriffonDomainHandler griffonDomainHandler) {
            super(griffonDomainHandler);
        }

        @Nonnull
        @Override
        @SuppressWarnings("ConstantConditions")
        protected <T extends GriffonDomain> Collection<T> withCriteria(final @Nonnull GriffonDomainClass<T> domainClass, final @Nonnull Criterion criterion, final @Nonnull Map<String, Object> options) {
            return withDataset(domainClass, new DatasetCallback<T, Collection<T>>() {
                @Override
                public Collection<T> handle(Dataset<T> dataset) {
                    return dataset.query(criterion, options);
                }
            });
        }
    }

    private class FindByMethod extends AbstractFindByPersistentMethod {
        public FindByMethod(final @Nonnull GriffonDomainHandler griffonDomainHandler) {
            super(griffonDomainHandler);
        }

        @Nullable
        @Override
        @SuppressWarnings("ConstantConditions")
        protected <T extends GriffonDomain> T findBy(final @Nonnull GriffonDomainClass<T> domainClass, final @Nonnull String methodName, final @Nonnull Criterion criterion) {
            return withDataset(domainClass, new DatasetCallback<T, T>() {
                @Override
                public T handle(Dataset<T> dataset) {
                    return dataset.first(criterion);
                }
            });
        }
    }

    private class FindOrCreateByMethod extends AbstractFindOrCreateByPersistentMethod {
        public FindOrCreateByMethod(final @Nonnull GriffonDomainHandler griffonDomainHandler) {
            super(griffonDomainHandler);
        }

        @Nullable
        @Override
        @SuppressWarnings("ConstantConditions")
        protected <T extends GriffonDomain> T findOrCreateBy(final @Nonnull GriffonDomainClass<T> domainClass, final @Nonnull String methodName, final @Nonnull Criterion criterion) {
            return withDataset(domainClass, new DatasetCallback<T, T>() {
                @Override
                public T handle(Dataset<T> dataset) {
                    T domain = dataset.first(criterion);

                    if (null == domain) {
                        domain = getApplication().getArtifactManager().newInstance(domainClass);
                        Map<String, Object> props = criterionToMap(criterion);
                        for (GriffonDomainProperty property : domainClass.getProperties()) {
                            Object value = props.get(property.getName());
                            if (value != null) property.setValue(domain, value);
                        }
                    }

                    return domain;
                }
            });
        }
    }

    private class FindOrCreateWhereMethod extends AbstractFindOrCreateWherePersistentMethod {
        private FindOrCreateWhereMethod(final @Nonnull GriffonDomainHandler griffonDomainHandler) {
            super(griffonDomainHandler);
        }

        @Nullable
        @Override
        @SuppressWarnings("ConstantConditions")
        protected <T extends GriffonDomain> T findOrCreateByParams(final @Nonnull GriffonDomainClass<T> domainClass, final @Nonnull Map<String, Object> params) {
            return withDataset(domainClass, new DatasetCallback<T, T>() {
                @Override
                public T handle(Dataset<T> dataset) {
                    T domain = dataset.first(params);

                    if (null == domain) {
                        domain = getApplication().getArtifactManager().newInstance(domainClass);
                        for (GriffonDomainProperty property : domainClass.getProperties()) {
                            Object value = params.get(property.getName());
                            if (value != null) property.setValue(domain, value);
                        }
                    }

                    return domain;
                }
            });
        }
    }

    private class FindOrSaveByMethod extends AbstractFindOrSaveByPersistentMethod {
        public FindOrSaveByMethod(final @Nonnull GriffonDomainHandler griffonDomainHandler) {
            super(griffonDomainHandler);
        }

        @Nullable
        @Override
        @SuppressWarnings("ConstantConditions")
        protected <T extends GriffonDomain> T findOrSaveBy(final @Nonnull GriffonDomainClass<T> domainClass, final @Nonnull String methodName, final @Nonnull Criterion criterion, final @Nonnull Map<String, Object> options) {
            return withDataset(domainClass, new DatasetCallback<T, T>() {
                @Override
                @SuppressWarnings("unchecked")
                public T handle(Dataset<T> dataset) {
                    T domain = dataset.first(criterion);

                    if (null == domain) {
                        domain = getApplication().getArtifactManager().newInstance(domainClass);
                        Map<String, Object> props = criterionToMap(criterion);
                        for (GriffonDomainProperty property : domainClass.getProperties()) {
                            Object value = props.get(property.getName());
                            if (value != null) property.setValue(domain, value);
                        }
                        domain = getGriffonDomainHandler().invokeInstanceMethod(domain, SaveMethod.METHOD_NAME, options);
                    }

                    return domain;
                }
            });
        }
    }

    private class FindOrSaveWhereMethod extends AbstractFindOrSaveWherePersistentMethod {
        public FindOrSaveWhereMethod(final @Nonnull GriffonDomainHandler griffonDomainHandler) {
            super(griffonDomainHandler);
        }

        @Nullable
        @Override
        @SuppressWarnings("ConstantConditions")
        protected <T extends GriffonDomain> T findOrSaveByParams(final @Nonnull GriffonDomainClass<T> domainClass, final @Nonnull Map<String, Object> params, final @Nonnull Map<String, Object> options) {
            return withDataset(domainClass, new DatasetCallback<T, T>() {
                @Override
                @SuppressWarnings("unchecked")
                public T handle(Dataset<T> dataset) {
                    T domain = dataset.first(params);

                    if (null == domain) {
                        domain = getApplication().getArtifactManager().newInstance(domainClass);
                        for (GriffonDomainProperty property : domainClass.getProperties()) {
                            Object value = params.get(property.getName());
                            if (value != null) property.setValue(domain, value);
                        }
                        domain = getGriffonDomainHandler().invokeInstanceMethod(domain, SaveMethod.METHOD_NAME, options);
                    }

                    return domain;
                }
            });
        }
    }

    private class FindAllByMethod extends AbstractFindAllByPersistentMethod {
        public FindAllByMethod(final @Nonnull GriffonDomainHandler griffonDomainHandler) {
            super(griffonDomainHandler);
        }

        @Nonnull
        @Override
        @SuppressWarnings("ConstantConditions")
        protected <T extends GriffonDomain> Collection<T> findAllBy(final @Nonnull GriffonDomainClass<T> domainClass, final @Nonnull String methodName, final @Nonnull Criterion criterion, final @Nonnull Map<String, Object> options) {
            return withDataset(domainClass, new DatasetCallback<T, Collection<T>>() {
                @Override
                public Collection<T> handle(Dataset<T> dataset) {
                    return dataset.query(criterion, options);
                }
            });
        }
    }

    private class CountByMethod extends AbstractCountByPersistentMethod {
        public CountByMethod(final @Nonnull GriffonDomainHandler griffonDomainHandler) {
            super(griffonDomainHandler);
        }

        @Override
        @SuppressWarnings("ConstantConditions")
        protected <T extends GriffonDomain> int countBy(final @Nonnull GriffonDomainClass<T> domainClass, final @Nonnull String methodName, final @Nonnull Criterion criterion) {
            return withDataset(domainClass, new DatasetCallback<T, Integer>() {
                @Override
                public Integer handle(Dataset<T> dataset) {
                    return dataset.query(criterion).size();
                }
            });
        }
    }
}
