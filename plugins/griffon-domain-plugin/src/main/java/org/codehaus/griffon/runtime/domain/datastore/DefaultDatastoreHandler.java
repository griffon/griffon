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

import griffon.plugins.domain.datastore.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class DefaultDatastoreHandler implements DatastoreHandler {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultDatastoreHandler.class);
    private static final String ERROR_DATASOURCE_BLANK = "Argument 'datastoreName' cannot be blank";
    private static final String ERROR_DATASOURCE_NULL = "Argument 'datastore' cannot be null";
    private static final String ERROR_CALLBACK_NULL = "Argument 'callback' cannot be null";

    private final DatastoreFactory datastoreFactory;
    private final DatastoreStorage datastoreStorage;

    @Inject
    public DefaultDatastoreHandler(@Nonnull DatastoreFactory datastoreFactory, @Nonnull DatastoreStorage datastoreStorage) {
        this.datastoreFactory = requireNonNull(datastoreFactory, "Argument 'datastoreFactory' cannot be null");
        this.datastoreStorage = requireNonNull(datastoreStorage, "Argument 'datastoreStorage' cannot be null");
    }

    @Nullable
    @Override
    public <R> R withDatastore(@Nonnull DatastoreCallback<R> callback) {
        return withDatastore(DefaultDatastoreFactory.KEY_DEFAULT, callback);
    }

    @Nullable
    @Override
    public <R> R withDatastore(@Nonnull String datastoreName, @Nonnull DatastoreCallback<R> callback) {
        requireNonBlank(datastoreName, ERROR_DATASOURCE_BLANK);
        requireNonNull(callback, ERROR_CALLBACK_NULL);
        Datastore datastore = getDatastore(datastoreName);
        LOG.debug("Executing statements on datastore '{}'", datastoreName);
        return callback.handle(datastoreName, datastore);
    }

    @Nonnull
    private Datastore getDatastore(@Nonnull String datastoreName) {
        Datastore datastore = datastoreStorage.get(datastoreName);
        if (datastore == null) {
            datastore = datastoreFactory.create(datastoreName);
            datastoreStorage.set(datastoreName, datastore);
        }
        return datastore;
    }
}
