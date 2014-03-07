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
package org.codehaus.griffon.runtime.datasource;

import griffon.plugins.datasource.*;
import griffon.plugins.datasource.exceptions.RuntimeSQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class DefaultDataSourceHandler implements DataSourceHandler {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultDataSourceHandler.class);
    private static final String ERROR_DATASOURCE_BLANK = "Argument 'dataSourceName' must not be blank";
    private static final String ERROR_DATASOURCE_NULL = "Argument 'dataSource' must not be null";
    private static final String ERROR_CALLBACK_NULL = "Argument 'callback' must not be null";

    private final DataSourceFactory dataSourceFactory;
    private final DataSourceStorage dataSourceStorage;

    @Inject
    public DefaultDataSourceHandler(@Nonnull DataSourceFactory dataSourceFactory, @Nonnull DataSourceStorage dataSourceStorage) {
        this.dataSourceFactory = requireNonNull(dataSourceFactory, "Argument 'dataSourceFactory' must not be null");
        this.dataSourceStorage = requireNonNull(dataSourceStorage, "Argument 'dataSourceStorage' must not be null");
    }

    @Nullable
    @Override
    public <R> R withDataSource(@Nonnull DataSourceCallback<R> callback) {
        return withDataSource(DefaultDataSourceFactory.KEY_DEFAULT, callback);
    }

    @Nullable
    @Override
    public <R> R withDataSource(@Nonnull String dataSourceName, @Nonnull DataSourceCallback<R> callback) {
        requireNonBlank(dataSourceName, ERROR_DATASOURCE_BLANK);
        requireNonNull(callback, ERROR_CALLBACK_NULL);
        DataSource dataSource = getDataSource(dataSourceName);
        try {
            LOG.debug("Executing statements on dataSource '{}'", dataSourceName);
            return callback.handle(dataSourceName, dataSource);
        } catch (SQLException e) {
            throw new RuntimeSQLException(dataSourceName, e);
        }
    }

    @Nullable
    @Override
    public <R> R withConnection(@Nonnull ConnectionCallback<R> callback) {
        return withConnection(DefaultDataSourceFactory.KEY_DEFAULT, callback);
    }

    @Nullable
    @Override
    public <R> R withConnection(@Nonnull String dataSourceName, @Nonnull ConnectionCallback<R> callback) throws RuntimeSQLException {
        requireNonBlank(dataSourceName, ERROR_DATASOURCE_BLANK);
        requireNonNull(callback, ERROR_CALLBACK_NULL);

        DataSource dataSource = getDataSource(dataSourceName);
        return doWithConnection(dataSourceName, dataSource, callback);
    }

    @Nullable
    @SuppressWarnings("ThrowFromFinallyBlock")
    public static <R> R doWithConnection(String dataSourceName, DataSource dataSource, ConnectionCallback<R> callback) throws RuntimeSQLException {
        requireNonBlank(dataSourceName, ERROR_DATASOURCE_BLANK);
        requireNonNull(dataSource, ERROR_DATASOURCE_NULL);
        requireNonNull(callback, ERROR_CALLBACK_NULL);

        Connection connection = null;
        try {
            connection = dataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeSQLException(dataSourceName, e);
        }
        try {
            LOG.debug("Executing statements on dataSource '{}'", dataSourceName);
            return callback.handle(dataSourceName, dataSource, connection);
        } catch (SQLException e) {
            throw new RuntimeSQLException(dataSourceName, e);
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeSQLException(dataSourceName, e);
            }
        }
    }

    @Override
    public void closeDataSource() {
        closeDataSource(DefaultDataSourceFactory.KEY_DEFAULT);
    }

    @Override
    public void closeDataSource(@Nonnull String dataSourceName) {
        DataSource dataSource = dataSourceStorage.get(dataSourceName);
        if (dataSource != null) {
            dataSourceFactory.destroy(dataSourceName, dataSource);
            dataSourceStorage.remove(dataSourceName);
        }
    }

    @Nonnull
    private DataSource getDataSource(@Nonnull String dataSourceName) {
        DataSource dataSource = dataSourceStorage.get(dataSourceName);
        if (dataSource == null) {
            dataSource = dataSourceFactory.create(dataSourceName);
            dataSourceStorage.set(dataSourceName, dataSource);
        }
        return dataSource;
    }
}
