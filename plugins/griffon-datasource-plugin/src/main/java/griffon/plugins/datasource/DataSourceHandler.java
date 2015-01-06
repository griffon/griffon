/*
 * Copyright 2008-2015 the original author or authors.
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
package griffon.plugins.datasource;

import griffon.plugins.datasource.exceptions.RuntimeSQLException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public interface DataSourceHandler {
    @Nullable
    <R> R withDataSource(@Nonnull DataSourceCallback<R> callback) throws RuntimeSQLException;

    @Nullable
    <R> R withDataSource(@Nonnull String dataSourceName, @Nonnull DataSourceCallback<R> callback) throws RuntimeSQLException;

    @Nullable
    <R> R withConnection(@Nonnull ConnectionCallback<R> callback) throws RuntimeSQLException;

    @Nullable
    <R> R withConnection(@Nonnull String dataSourceName, @Nonnull ConnectionCallback<R> callback) throws RuntimeSQLException;

    void closeDataSource();

    void closeDataSource(@Nonnull String dataSourceName);
}