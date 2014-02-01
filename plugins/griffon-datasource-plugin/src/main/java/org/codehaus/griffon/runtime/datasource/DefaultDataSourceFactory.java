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

import griffon.core.Configuration;
import griffon.core.GriffonApplication;
import griffon.exceptions.GriffonException;
import griffon.plugins.datasource.ConnectionCallback;
import griffon.plugins.datasource.DataSourceFactory;
import griffon.util.GriffonClassUtils;
import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.codehaus.griffon.runtime.core.storage.AbstractObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.Map;
import java.util.Scanner;

import static griffon.core.GriffonExceptionHandler.sanitize;
import static griffon.core.env.Environment.getEnvironmentShortName;
import static griffon.util.ConfigUtils.getConfigValue;
import static griffon.util.ConfigUtils.getConfigValueAsBoolean;
import static griffon.util.ConfigUtils.getConfigValueAsString;
import static griffon.util.GriffonNameUtils.isBlank;
import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class DefaultDataSourceFactory extends AbstractObjectFactory<DataSource> implements DataSourceFactory {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultDataSourceFactory.class);

    @Inject
    public DefaultDataSourceFactory(@Nonnull @Named("dataSource") Configuration configuration, @Nonnull GriffonApplication application) {
        super(configuration, application);
    }

    @Nonnull
    @Override
    protected String getSingleKey() {
        return "dataSource";
    }

    @Nonnull
    @Override
    protected String getPluralKey() {
        return "dataSources";
    }

    @Nonnull
    @Override
    public DataSource create(@Nonnull String name) {
        Map<String, Object> config = narrowConfig(name);
        event("DataSourceConnectStart", asList(name, config));
        DataSource dataSource = createDataSource(config, name);
        boolean skipSchema = getConfigValueAsBoolean(config, "schema", false);
        if (!skipSchema) {
            processSchema(config, name, dataSource);
        }
        event("DataSourceConnectEnd", asList(config, name, dataSource));
        return dataSource;
    }

    @Override
    public void destroy(@Nonnull String name, @Nonnull DataSource instance) {
        requireNonNull(instance, "Argument 'instance' cannot be null");
        Map<String, Object> config = narrowConfig(name);
        event("DataSourceDisconnectStart", asList(config, name, instance));
        event("DataSourceDisconnectEnd", asList(name, config));
    }

    @Nonnull
    @SuppressWarnings("ConstantConditions")
    private DataSource createDataSource(@Nonnull Map<String, Object> config, @Nonnull String name) {
        String driverClassName = getConfigValueAsString(config, "driverClassName", "");
        requireNonBlank(driverClassName, "Configuration for " + name + ".driverClassName cannot be blank");
        String url = getConfigValueAsString(config, "url", "");
        requireNonBlank(url, "Configuration for " + name + ".url cannot be blank");

        try {
            getApplication().getApplicationClassLoader().get().loadClass(driverClassName);
        } catch (ClassNotFoundException e) {
            throw new GriffonException(e);
        }

        GenericObjectPool<DataSource> connectionPool = new GenericObjectPool<>(null);
        Map<String, Object> pool = getConfigValue(config, "pool", Collections.<String, Object>emptyMap());
        GriffonClassUtils.setPropertiesNoException(connectionPool, pool);

        String username = getConfigValueAsString(config, "username", "");
        String password = getConfigValueAsString(config, "password", "");
        ConnectionFactory connectionFactory = null;
        if (isBlank(username)) {
            connectionFactory = new DriverManagerConnectionFactory(url, null);
        } else {
            connectionFactory = new DriverManagerConnectionFactory(url, username, password);
        }

        new PoolableConnectionFactory(connectionFactory, connectionPool, null, null, false, true);
        return new PoolingDataSource(connectionPool);
    }

    private void processSchema(@Nonnull Map<String, Object> config, @Nonnull String name, @Nonnull DataSource dataSource) {
        String dbCreate = getConfigValueAsString(config, "dbCreate", "skip");
        if (!"create".equals(dbCreate)) {
            return;
        }

        String env = getEnvironmentShortName();
        URL ddl = null;
        for (String schemaName : asList(name + "-schema-" + env + ".ddl", name + "-schema.ddl", "schema-" + env + ".ddl", "schema.ddl")) {
            ddl = getApplication().getResourceHandler().getResourceAsURL(schemaName);
            if (ddl == null) {
                LOG.warn("DataSource[{}].dbCreate was set to 'create' but {} was not found in classpath.", name, schemaName);
            } else {
                break;
            }
        }
        if (ddl == null) {
            LOG.error("DataSource[{}].dbCreate was set to 'create' but no suitable schema was found in classpath.", name);
            return;
        }
        final URL url = ddl;

        DefaultDataSourceHandler.doWithConnection(name, dataSource, new ConnectionCallback<Object>() {
            @Override
            public Object handle(@Nonnull String dataSourceName, @Nonnull DataSource ds, @Nonnull Connection connection) {
                try (Scanner sc = new Scanner(url.openStream()); Statement statement = connection.createStatement()) {
                    sc.useDelimiter(";");
                    while (sc.hasNext()) {
                        String line = sc.next().trim();
                        statement.execute(line);
                    }
                } catch (IOException | SQLException e) {
                    LOG.error("An error occurred when reading schema DDL from " + url, sanitize(e));
                    return null;
                }

                return null;
            }
        });
    }
}
