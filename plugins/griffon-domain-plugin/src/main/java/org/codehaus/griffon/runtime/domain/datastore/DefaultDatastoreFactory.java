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
import griffon.exceptions.InstanceNotFoundException;
import griffon.plugins.domain.datastore.Datastore;
import griffon.plugins.domain.datastore.DatastoreFactory;
import org.codehaus.griffon.runtime.core.storage.AbstractObjectFactory;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Map;

import static griffon.util.AnnotationUtils.named;
import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class DefaultDatastoreFactory extends AbstractObjectFactory<Datastore> implements DatastoreFactory {
    @Inject
    public DefaultDatastoreFactory(@Nonnull GriffonApplication application) {
        super(application.getConfiguration(), application);
    }

    @Nonnull
    @Override
    protected String getSingleKey() {
        return "datastore";
    }

    @Nonnull
    @Override
    protected String getPluralKey() {
        return "datastores";
    }

    @Nonnull
    @Override
    public Datastore create(@Nonnull String name) {
        Map<String, Object> config = narrowConfig(name);
        event("DatastoreConnectStart", asList(name, config));
        Datastore datastore = createDatastore(config, name);
        event("DatastoreConnectEnd", asList(config, name, datastore));
        return datastore;
    }

    @Override
    public void destroy(@Nonnull String name, @Nonnull Datastore instance) {
        requireNonNull(instance, "Argument 'instance' cannot be null");
        Map<String, Object> config = narrowConfig(name);
        event("DatastoreDisconnectStart", asList(config, name, instance));
        event("DatastoreDisconnectEnd", asList(name, config));
    }

    @Nonnull
    private Datastore createDatastore(@Nonnull Map<String, Object> config, @Nonnull String name) {
        try {
            return getApplication().getInjector().getInstance(Datastore.class, named(name));
        } catch (InstanceNotFoundException infe) {
            throw new IllegalArgumentException("Datastore with name '" + name + "' is not configured", infe);
        }
    }
}
