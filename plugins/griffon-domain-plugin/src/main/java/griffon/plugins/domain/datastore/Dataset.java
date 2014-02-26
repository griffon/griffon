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
package griffon.plugins.domain.datastore;

import griffon.plugins.domain.GriffonDomain;
import griffon.plugins.domain.orm.Criterion;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public interface Dataset<T extends GriffonDomain> {
    String KEY_MAX = "max";
    String KEY_SORT = "sort";
    String KEY_ORDER = "order";
    String KEY_OFFSET = "offset";

    @Nonnull
    Long nextId();

    @Nonnull
    String getName();

    @Nonnull
    T save(@Nonnull T entity);

    @Nonnull
    T remove(@Nonnull T entity);

    @Nonnull
    List<T> list();

    @Nonnull
    List<T> list(@Nonnull Map<String, Object> options);

    @Nullable
    T fetch(@Nonnull Object identity);

    @Nonnull
    List<T> query(@Nonnull Object example);

    @Nonnull
    List<T> query(@Nonnull Object example, @Nonnull Map<String, Object> options);

    @Nonnull
    List<T> query(@Nonnull Map<String, Object> params);

    @Nonnull
    List<T> query(@Nonnull Map<String, Object> params, @Nonnull Map<String, Object> options);

    @Nonnull
    List<T> query(@Nonnull Criterion criterion);

    @Nonnull
    List<T> query(@Nonnull Criterion criterion, @Nonnull Map<String, Object> options);

    @Nullable
    T first(@Nonnull Object example);

    @Nullable
    T first(@Nonnull Map<String, Object> params);

    @Nullable
    T first(@Nonnull Criterion criterion);

    int size();
}
