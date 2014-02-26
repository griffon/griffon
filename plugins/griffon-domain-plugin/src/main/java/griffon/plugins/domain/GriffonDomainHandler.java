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
package griffon.plugins.domain;

import griffon.core.GriffonApplication;
import griffon.plugins.domain.methods.support.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Andres Almiray
 */
public interface GriffonDomainHandler extends
    CountByMethodHandler,
    CountMethodHandler,
    CreateMethodHandler,
    DeleteMethodHandler,
    ExistsMethodHandler,
    FindAllByMethodHandler,
    FindAllMethodHandler,
    FindAllWhereMethodHandler,
    FindByMethodHandler,
    FindMethodHandler,
    FindOrCreateByMethodHandler,
    FindOrCreateWhereMethodHandler,
    FindOrSaveByMethodHandler,
    FindOrSaveWhereMethodHandler,
    FindWhereMethodHandler,
    FirstMethodHandler,
    GetAllMethodHandler,
    GetMethodHandler,
    LastMethodHandler,
    ListMethodHandler,
    ListOrderByMethodHandler,
    SaveMethodHandler,
    WithCriteriaMethodHandler {

    @Nonnull
    GriffonApplication getApplication();

    @Nonnull
    String getMapping();

    @Nullable
    <T extends GriffonDomain> T invokeInstanceMethod(@Nonnull T target, @Nonnull String methodName, Object... args);

    @Nullable
    <T extends GriffonDomain> Object invokeStaticMethod(@Nonnull Class<T> clazz, @Nonnull String methodName, Object... args);
}