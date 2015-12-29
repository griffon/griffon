/*
 * Copyright 2008-2016 the original author or authors.
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
package griffon.core.injection;

import griffon.exceptions.InstanceNotFoundException;
import griffon.exceptions.MembersInjectionException;

import javax.annotation.Nonnull;
import java.lang.annotation.Annotation;
import java.util.Collection;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public interface Injector<I> {
    @Nonnull
    <T> T getInstance(@Nonnull Class<T> type) throws InstanceNotFoundException;

    @Nonnull
    <T> T getInstance(@Nonnull Class<T> type, @Nonnull Annotation qualifier) throws InstanceNotFoundException;

    @Nonnull
    <T> Collection<T> getInstances(@Nonnull Class<T> type) throws InstanceNotFoundException;

    @Nonnull
    <T> Collection<Qualified<T>> getQualifiedInstances(@Nonnull Class<T> type) throws InstanceNotFoundException;

    void injectMembers(@Nonnull Object instance) throws MembersInjectionException;

    @Nonnull
    I getDelegateInjector();

    void close();
}
