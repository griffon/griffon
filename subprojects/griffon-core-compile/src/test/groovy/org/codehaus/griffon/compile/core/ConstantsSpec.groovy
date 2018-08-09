/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2018 the original author or authors.
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
package org.codehaus.griffon.compile.core

import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class ConstantsSpec extends Specification implements BaseConstants {
    void "Verify methods of #classname"() {
        given:
        def actualMethods = classname.METHODS

        expect:
        actualMethods.size() == methods.size()
        actualMethods.each { assert methods.contains(it.toString()) }

        where:
        classname                      | methods
        EventPublisherConstants        | EVENT_PUBLISHER_METHODS
        MessageSourceAwareConstants    | MESSAGE_SOURCE_METHODS
        MVCAwareConstants              | MVC_METHODS
        ResourceResolverAwareConstants | RESOURCE_RESOLVER_METHODS
        ResourcesAwareConstants        | RESOURCES_METHODS
        ThreadingAwareConstants        | THREADING_METHODS
    }

    private static final List<String> EVENT_PUBLISHER_METHODS = [
        'public void addEventListener(@griffon.annotations.core.Nonnull java.lang.Object arg0)',
        'public void addEventListener(@griffon.annotations.core.Nonnull java.lang.String arg0, @griffon.annotations.core.Nonnull griffon.core.RunnableWithArgs<?> arg1)',
        'public void addEventListener(@griffon.annotations.core.Nonnull java.util.Map<java.lang.String, java.lang.Object> arg0)',
        'public <E extends griffon.core.event.Event> void addEventListener(@griffon.annotations.core.Nonnull java.lang.Class<E> arg0, @griffon.annotations.core.Nonnull griffon.core.RunnableWithArgs<?> arg1)',
        'public void removeEventListener(@griffon.annotations.core.Nonnull java.lang.Object arg0)',
        'public void removeEventListener(@griffon.annotations.core.Nonnull java.lang.String arg0, @griffon.annotations.core.Nonnull griffon.core.RunnableWithArgs<?> arg1)',
        'public void removeEventListener(@griffon.annotations.core.Nonnull java.util.Map<java.lang.String, java.lang.Object> arg0)',
        'public <E extends griffon.core.event.Event> void removeEventListener(@griffon.annotations.core.Nonnull java.lang.Class<E> arg0, @griffon.annotations.core.Nonnull griffon.core.RunnableWithArgs<?> arg1)',
        'public void publishEvent(@griffon.annotations.core.Nonnull java.lang.String arg0)',
        'public void publishEvent(@griffon.annotations.core.Nonnull java.lang.String arg0, @griffon.annotations.core.Nonnull java.util.List<?> arg1)',
        'public void publishEvent(@griffon.annotations.core.Nonnull griffon.core.event.Event arg0)',
        'public void publishEventOutsideUI(@griffon.annotations.core.Nonnull java.lang.String arg0)',
        'public void publishEventOutsideUI(@griffon.annotations.core.Nonnull java.lang.String arg0, @griffon.annotations.core.Nonnull java.util.List<?> arg1)',
        'public void publishEventOutsideUI(@griffon.annotations.core.Nonnull griffon.core.event.Event arg0)',
        'public void publishEventAsync(@griffon.annotations.core.Nonnull java.lang.String arg0)',
        'public void publishEventAsync(@griffon.annotations.core.Nonnull java.lang.String arg0, @griffon.annotations.core.Nonnull java.util.List<?> arg1)',
        'public void publishEventAsync(@griffon.annotations.core.Nonnull griffon.core.event.Event arg0)',
        'public boolean isEventPublishingEnabled()',
        'public void setEventPublishingEnabled(boolean arg0)',
        '@griffon.annotations.core.Nonnull public java.util.Collection<java.lang.Object> getEventListeners()',
        '@griffon.annotations.core.Nonnull public java.util.Collection<java.lang.Object> getEventListeners(@griffon.annotations.core.Nonnull java.lang.String arg0)',
    ]

    private static final List<String> MESSAGE_SOURCE_METHODS = [
        '@griffon.annotations.core.Nonnull public java.lang.String resolveMessageValue(@griffon.annotations.core.Nonnull java.lang.String arg0, @griffon.annotations.core.Nonnull java.util.Locale arg1) throws griffon.core.i18n.NoSuchMessageException',
        '@griffon.annotations.core.Nonnull public java.lang.String getMessage(@griffon.annotations.core.Nonnull java.lang.String arg0) throws griffon.core.i18n.NoSuchMessageException',
        '@griffon.annotations.core.Nonnull public java.lang.String getMessage(@griffon.annotations.core.Nonnull java.lang.String arg0, @griffon.annotations.core.Nonnull java.util.Locale arg1) throws griffon.core.i18n.NoSuchMessageException',
        '@griffon.annotations.core.Nonnull public java.lang.String getMessage(@griffon.annotations.core.Nonnull java.lang.String arg0, @griffon.annotations.core.Nonnull java.lang.Object[] arg1) throws griffon.core.i18n.NoSuchMessageException',
        '@griffon.annotations.core.Nonnull public java.lang.String getMessage(@griffon.annotations.core.Nonnull java.lang.String arg0, @griffon.annotations.core.Nonnull java.lang.Object[] arg1, @griffon.annotations.core.Nonnull java.util.Locale arg2) throws griffon.core.i18n.NoSuchMessageException',
        '@griffon.annotations.core.Nonnull public java.lang.String getMessage(@griffon.annotations.core.Nonnull java.lang.String arg0, @griffon.annotations.core.Nonnull java.util.List<?> arg1) throws griffon.core.i18n.NoSuchMessageException',
        '@griffon.annotations.core.Nonnull public java.lang.String getMessage(@griffon.annotations.core.Nonnull java.lang.String arg0, @griffon.annotations.core.Nonnull java.util.List<?> arg1, @griffon.annotations.core.Nonnull java.util.Locale arg2) throws griffon.core.i18n.NoSuchMessageException',
        '@griffon.annotations.core.Nonnull public java.lang.String getMessage(@griffon.annotations.core.Nonnull java.lang.String arg0, @griffon.annotations.core.Nonnull java.util.Map<java.lang.String, java.lang.Object> arg1) throws griffon.core.i18n.NoSuchMessageException',
        '@griffon.annotations.core.Nonnull public java.lang.String getMessage(@griffon.annotations.core.Nonnull java.lang.String arg0, @griffon.annotations.core.Nonnull java.util.Map<java.lang.String, java.lang.Object> arg1, @griffon.annotations.core.Nonnull java.util.Locale arg2) throws griffon.core.i18n.NoSuchMessageException',
        '@griffon.annotations.core.Nullable public java.lang.String getMessage(@griffon.annotations.core.Nonnull java.lang.String arg0, @griffon.annotations.core.Nullable java.lang.String arg1)',
        '@griffon.annotations.core.Nullable public java.lang.String getMessage(@griffon.annotations.core.Nonnull java.lang.String arg0, @griffon.annotations.core.Nonnull java.util.Locale arg1, @griffon.annotations.core.Nullable java.lang.String arg2)',
        '@griffon.annotations.core.Nullable public java.lang.String getMessage(@griffon.annotations.core.Nonnull java.lang.String arg0, @griffon.annotations.core.Nonnull java.lang.Object[] arg1, @griffon.annotations.core.Nullable java.lang.String arg2)',
        '@griffon.annotations.core.Nullable public java.lang.String getMessage(@griffon.annotations.core.Nonnull java.lang.String arg0, @griffon.annotations.core.Nonnull java.lang.Object[] arg1, @griffon.annotations.core.Nonnull java.util.Locale arg2, @griffon.annotations.core.Nullable java.lang.String arg3)',
        '@griffon.annotations.core.Nullable public java.lang.String getMessage(@griffon.annotations.core.Nonnull java.lang.String arg0, @griffon.annotations.core.Nonnull java.util.List<?> arg1, @griffon.annotations.core.Nullable java.lang.String arg2)',
        '@griffon.annotations.core.Nullable public java.lang.String getMessage(@griffon.annotations.core.Nonnull java.lang.String arg0, @griffon.annotations.core.Nonnull java.util.List<?> arg1, @griffon.annotations.core.Nonnull java.util.Locale arg2, @griffon.annotations.core.Nullable java.lang.String arg3)',
        '@griffon.annotations.core.Nullable public java.lang.String getMessage(@griffon.annotations.core.Nonnull java.lang.String arg0, @griffon.annotations.core.Nonnull java.util.Map<java.lang.String, java.lang.Object> arg1, @griffon.annotations.core.Nullable java.lang.String arg2)',
        '@griffon.annotations.core.Nullable public java.lang.String getMessage(@griffon.annotations.core.Nonnull java.lang.String arg0, @griffon.annotations.core.Nonnull java.util.Map<java.lang.String, java.lang.Object> arg1, @griffon.annotations.core.Nonnull java.util.Locale arg2, @griffon.annotations.core.Nullable java.lang.String arg3)',
        '@griffon.annotations.core.Nonnull public java.lang.String formatMessage(@griffon.annotations.core.Nonnull java.lang.Object[] arg0, @griffon.annotations.core.Nonnull java.util.Map<java.lang.String, java.lang.Object> arg1)',
        '@griffon.annotations.core.Nonnull public java.lang.String formatMessage(@griffon.annotations.core.Nonnull java.lang.String arg0, @griffon.annotations.core.Nonnull java.util.List<?> arg1)',
        '@griffon.annotations.core.Nonnull public java.lang.String formatMessage(@griffon.annotations.core.Nonnull java.lang.String arg0, @griffon.annotations.core.Nonnull java.util.Map<java.lang.String, java.lang.Object> arg1)',
    ]

    private static final List<String> MVC_METHODS = [
        'public void destroyMVCGroup(@griffon.annotations.core.Nonnull java.lang.String arg0)',
        '@griffon.annotations.core.Nonnull public griffon.core.mvc.MVCGroup createMVCGroup(@griffon.annotations.core.Nonnull java.lang.String arg0)',
        '@griffon.annotations.core.Nonnull public griffon.core.mvc.MVCGroup createMVCGroup(@griffon.annotations.core.Nonnull java.lang.String arg0, java.lang.String arg1)',
        '@griffon.annotations.core.Nonnull public griffon.core.mvc.MVCGroup createMVCGroup(@griffon.annotations.core.Nonnull java.util.Map<java.lang.String, java.lang.Object> arg0, @griffon.annotations.core.Nonnull java.lang.String arg1)',
        '@griffon.annotations.core.Nonnull public griffon.core.mvc.MVCGroup createMVCGroup(@griffon.annotations.core.Nonnull java.lang.String arg0, @griffon.annotations.core.Nonnull java.util.Map<java.lang.String, java.lang.Object> arg1)',
        '@griffon.annotations.core.Nonnull public griffon.core.mvc.MVCGroup createMVCGroup(@griffon.annotations.core.Nonnull java.util.Map<java.lang.String, java.lang.Object> arg0, @griffon.annotations.core.Nonnull java.lang.String arg1, @griffon.annotations.core.Nonnull java.lang.String arg2)',
        '@griffon.annotations.core.Nonnull public griffon.core.mvc.MVCGroup createMVCGroup(@griffon.annotations.core.Nonnull java.lang.String arg0, @griffon.annotations.core.Nonnull java.lang.String arg1, @griffon.annotations.core.Nonnull java.util.Map<java.lang.String, java.lang.Object> arg2)',
        '@griffon.annotations.core.Nonnull public <MVC extends griffon.core.mvc.TypedMVCGroup> MVC createMVCGroup(@griffon.annotations.core.Nonnull java.lang.Class<MVC> arg0)',
        '@griffon.annotations.core.Nonnull public <MVC extends griffon.core.mvc.TypedMVCGroup> MVC createMVCGroup(@griffon.annotations.core.Nonnull java.lang.Class<MVC> arg0, java.lang.String arg1)',
        '@griffon.annotations.core.Nonnull public <MVC extends griffon.core.mvc.TypedMVCGroup> MVC createMVCGroup(@griffon.annotations.core.Nonnull java.util.Map<java.lang.String, java.lang.Object> arg0, @griffon.annotations.core.Nonnull java.lang.Class<MVC> arg1)',
        '@griffon.annotations.core.Nonnull public <MVC extends griffon.core.mvc.TypedMVCGroup> MVC createMVCGroup(@griffon.annotations.core.Nonnull java.lang.Class<MVC> arg0, @griffon.annotations.core.Nonnull java.util.Map<java.lang.String, java.lang.Object> arg1)',
        '@griffon.annotations.core.Nonnull public <MVC extends griffon.core.mvc.TypedMVCGroup> MVC createMVCGroup(@griffon.annotations.core.Nonnull java.util.Map<java.lang.String, java.lang.Object> arg0, @griffon.annotations.core.Nonnull java.lang.Class<MVC> arg1, @griffon.annotations.core.Nonnull java.lang.String arg2)',
        '@griffon.annotations.core.Nonnull public <MVC extends griffon.core.mvc.TypedMVCGroup> MVC createMVCGroup(@griffon.annotations.core.Nonnull java.lang.Class<MVC> arg0, @griffon.annotations.core.Nonnull java.lang.String arg1, @griffon.annotations.core.Nonnull java.util.Map<java.lang.String, java.lang.Object> arg2)',
        '@griffon.annotations.core.Nonnull public java.util.List<? extends griffon.core.artifact.GriffonMvcArtifact> createMVC(@griffon.annotations.core.Nonnull java.lang.String arg0)',
        '@griffon.annotations.core.Nonnull public java.util.List<? extends griffon.core.artifact.GriffonMvcArtifact> createMVC(@griffon.annotations.core.Nonnull java.util.Map<java.lang.String, java.lang.Object> arg0, @griffon.annotations.core.Nonnull java.lang.String arg1)',
        '@griffon.annotations.core.Nonnull public java.util.List<? extends griffon.core.artifact.GriffonMvcArtifact> createMVC(@griffon.annotations.core.Nonnull java.lang.String arg0, @griffon.annotations.core.Nonnull java.util.Map<java.lang.String, java.lang.Object> arg1)',
        '@griffon.annotations.core.Nonnull public java.util.List<? extends griffon.core.artifact.GriffonMvcArtifact> createMVC(@griffon.annotations.core.Nonnull java.lang.String arg0, @griffon.annotations.core.Nonnull java.lang.String arg1)',
        '@griffon.annotations.core.Nonnull public java.util.List<? extends griffon.core.artifact.GriffonMvcArtifact> createMVC(@griffon.annotations.core.Nonnull java.util.Map<java.lang.String, java.lang.Object> arg0, @griffon.annotations.core.Nonnull java.lang.String arg1, @griffon.annotations.core.Nonnull java.lang.String arg2)',
        '@griffon.annotations.core.Nonnull public java.util.List<? extends griffon.core.artifact.GriffonMvcArtifact> createMVC(@griffon.annotations.core.Nonnull java.lang.String arg0, @griffon.annotations.core.Nonnull java.lang.String arg1, @griffon.annotations.core.Nonnull java.util.Map<java.lang.String, java.lang.Object> arg2)',
        'public <M extends griffon.core.artifact.GriffonModel, V extends griffon.core.artifact.GriffonView, C extends griffon.core.artifact.GriffonController> void withMVC(@griffon.annotations.core.Nonnull java.lang.String arg0, @griffon.annotations.core.Nonnull griffon.core.mvc.MVCConsumer<M, V, C> arg1)',
        'public <M extends griffon.core.artifact.GriffonModel, V extends griffon.core.artifact.GriffonView, C extends griffon.core.artifact.GriffonController> void withMVC(@griffon.annotations.core.Nonnull java.lang.String arg0, @griffon.annotations.core.Nonnull java.lang.String arg1, @griffon.annotations.core.Nonnull griffon.core.mvc.MVCConsumer<M, V, C> arg2)',
        'public <M extends griffon.core.artifact.GriffonModel, V extends griffon.core.artifact.GriffonView, C extends griffon.core.artifact.GriffonController> void withMVC(@griffon.annotations.core.Nonnull java.lang.String arg0, @griffon.annotations.core.Nonnull java.lang.String arg1, @griffon.annotations.core.Nonnull java.util.Map<java.lang.String, java.lang.Object> arg2, @griffon.annotations.core.Nonnull griffon.core.mvc.MVCConsumer<M, V, C> arg3)',
        'public <M extends griffon.core.artifact.GriffonModel, V extends griffon.core.artifact.GriffonView, C extends griffon.core.artifact.GriffonController> void withMVC(@griffon.annotations.core.Nonnull java.util.Map<java.lang.String, java.lang.Object> arg0, @griffon.annotations.core.Nonnull java.lang.String arg1, @griffon.annotations.core.Nonnull java.lang.String arg2, @griffon.annotations.core.Nonnull griffon.core.mvc.MVCConsumer<M, V, C> arg3)',
        'public <M extends griffon.core.artifact.GriffonModel, V extends griffon.core.artifact.GriffonView, C extends griffon.core.artifact.GriffonController> void withMVC(@griffon.annotations.core.Nonnull java.lang.String arg0, @griffon.annotations.core.Nonnull java.util.Map<java.lang.String, java.lang.Object> arg1, @griffon.annotations.core.Nonnull griffon.core.mvc.MVCConsumer<M, V, C> arg2)',
        'public <M extends griffon.core.artifact.GriffonModel, V extends griffon.core.artifact.GriffonView, C extends griffon.core.artifact.GriffonController> void withMVC(@griffon.annotations.core.Nonnull java.util.Map<java.lang.String, java.lang.Object> arg0, @griffon.annotations.core.Nonnull java.lang.String arg1, @griffon.annotations.core.Nonnull griffon.core.mvc.MVCConsumer<M, V, C> arg2)',
        'public <MVC extends griffon.core.mvc.TypedMVCGroup, M extends griffon.core.artifact.GriffonModel, V extends griffon.core.artifact.GriffonView, C extends griffon.core.artifact.GriffonController> void withMVC(@griffon.annotations.core.Nonnull java.lang.Class<MVC> arg0, @griffon.annotations.core.Nonnull griffon.core.mvc.MVCConsumer<M, V, C> arg1)',
        'public <MVC extends griffon.core.mvc.TypedMVCGroup, M extends griffon.core.artifact.GriffonModel, V extends griffon.core.artifact.GriffonView, C extends griffon.core.artifact.GriffonController> void withMVC(@griffon.annotations.core.Nonnull java.lang.Class<MVC> arg0, @griffon.annotations.core.Nonnull java.lang.String arg1, @griffon.annotations.core.Nonnull griffon.core.mvc.MVCConsumer<M, V, C> arg2)',
        'public <MVC extends griffon.core.mvc.TypedMVCGroup, M extends griffon.core.artifact.GriffonModel, V extends griffon.core.artifact.GriffonView, C extends griffon.core.artifact.GriffonController> void withMVC(@griffon.annotations.core.Nonnull java.lang.Class<MVC> arg0, @griffon.annotations.core.Nonnull java.lang.String arg1, @griffon.annotations.core.Nonnull java.util.Map<java.lang.String, java.lang.Object> arg2, @griffon.annotations.core.Nonnull griffon.core.mvc.MVCConsumer<M, V, C> arg3)',
        'public <MVC extends griffon.core.mvc.TypedMVCGroup, M extends griffon.core.artifact.GriffonModel, V extends griffon.core.artifact.GriffonView, C extends griffon.core.artifact.GriffonController> void withMVC(@griffon.annotations.core.Nonnull java.util.Map<java.lang.String, java.lang.Object> arg0, @griffon.annotations.core.Nonnull java.lang.Class<MVC> arg1, @griffon.annotations.core.Nonnull java.lang.String arg2, @griffon.annotations.core.Nonnull griffon.core.mvc.MVCConsumer<M, V, C> arg3)',
        'public <MVC extends griffon.core.mvc.TypedMVCGroup, M extends griffon.core.artifact.GriffonModel, V extends griffon.core.artifact.GriffonView, C extends griffon.core.artifact.GriffonController> void withMVC(@griffon.annotations.core.Nonnull java.lang.Class<MVC> arg0, @griffon.annotations.core.Nonnull java.util.Map<java.lang.String, java.lang.Object> arg1, @griffon.annotations.core.Nonnull griffon.core.mvc.MVCConsumer<M, V, C> arg2)',
        'public <MVC extends griffon.core.mvc.TypedMVCGroup, M extends griffon.core.artifact.GriffonModel, V extends griffon.core.artifact.GriffonView, C extends griffon.core.artifact.GriffonController> void withMVC(@griffon.annotations.core.Nonnull java.util.Map<java.lang.String, java.lang.Object> arg0, @griffon.annotations.core.Nonnull java.lang.Class<MVC> arg1, @griffon.annotations.core.Nonnull griffon.core.mvc.MVCConsumer<M, V, C> arg2)',
        'public void withMVCGroup(@griffon.annotations.core.Nonnull java.lang.String arg0, @griffon.annotations.core.Nonnull griffon.core.mvc.MVCGroupConsumer arg1)',
        'public void withMVCGroup(@griffon.annotations.core.Nonnull java.lang.String arg0, @griffon.annotations.core.Nonnull java.lang.String arg1, @griffon.annotations.core.Nonnull griffon.core.mvc.MVCGroupConsumer arg2)',
        'public void withMVCGroup(@griffon.annotations.core.Nonnull java.lang.String arg0, @griffon.annotations.core.Nonnull java.lang.String arg1, @griffon.annotations.core.Nonnull java.util.Map<java.lang.String, java.lang.Object> arg2, @griffon.annotations.core.Nonnull griffon.core.mvc.MVCGroupConsumer arg3)',
        'public void withMVCGroup(@griffon.annotations.core.Nonnull java.util.Map<java.lang.String, java.lang.Object> arg0, @griffon.annotations.core.Nonnull java.lang.String arg1, @griffon.annotations.core.Nonnull java.lang.String arg2, @griffon.annotations.core.Nonnull griffon.core.mvc.MVCGroupConsumer arg3)',
        'public void withMVCGroup(@griffon.annotations.core.Nonnull java.lang.String arg0, @griffon.annotations.core.Nonnull java.util.Map<java.lang.String, java.lang.Object> arg1, @griffon.annotations.core.Nonnull griffon.core.mvc.MVCGroupConsumer arg2)',
        'public void withMVCGroup(@griffon.annotations.core.Nonnull java.util.Map<java.lang.String, java.lang.Object> arg0, @griffon.annotations.core.Nonnull java.lang.String arg1, @griffon.annotations.core.Nonnull griffon.core.mvc.MVCGroupConsumer arg2)',
        'public <MVC extends griffon.core.mvc.TypedMVCGroup> void withMVCGroup(@griffon.annotations.core.Nonnull java.lang.Class<MVC> arg0, @griffon.annotations.core.Nonnull griffon.core.mvc.TypedMVCGroupConsumer<MVC> arg1)',
        'public <MVC extends griffon.core.mvc.TypedMVCGroup> void withMVCGroup(@griffon.annotations.core.Nonnull java.lang.Class<MVC> arg0, @griffon.annotations.core.Nonnull java.lang.String arg1, @griffon.annotations.core.Nonnull griffon.core.mvc.TypedMVCGroupConsumer<MVC> arg2)',
        'public <MVC extends griffon.core.mvc.TypedMVCGroup> void withMVCGroup(@griffon.annotations.core.Nonnull java.lang.Class<MVC> arg0, @griffon.annotations.core.Nonnull java.lang.String arg1, @griffon.annotations.core.Nonnull java.util.Map<java.lang.String, java.lang.Object> arg2, @griffon.annotations.core.Nonnull griffon.core.mvc.TypedMVCGroupConsumer<MVC> arg3)',
        'public <MVC extends griffon.core.mvc.TypedMVCGroup> void withMVCGroup(@griffon.annotations.core.Nonnull java.util.Map<java.lang.String, java.lang.Object> arg0, @griffon.annotations.core.Nonnull java.lang.Class<MVC> arg1, @griffon.annotations.core.Nonnull java.lang.String arg2, @griffon.annotations.core.Nonnull griffon.core.mvc.TypedMVCGroupConsumer<MVC> arg3)',
        'public <MVC extends griffon.core.mvc.TypedMVCGroup> void withMVCGroup(@griffon.annotations.core.Nonnull java.lang.Class<MVC> arg0, @griffon.annotations.core.Nonnull java.util.Map<java.lang.String, java.lang.Object> arg1, @griffon.annotations.core.Nonnull griffon.core.mvc.TypedMVCGroupConsumer<MVC> arg2)',
        'public <MVC extends griffon.core.mvc.TypedMVCGroup> void withMVCGroup(@griffon.annotations.core.Nonnull java.util.Map<java.lang.String, java.lang.Object> arg0, @griffon.annotations.core.Nonnull java.lang.Class<MVC> arg1, @griffon.annotations.core.Nonnull griffon.core.mvc.TypedMVCGroupConsumer<MVC> arg2)',
    ]

    private static final List<String> RESOURCES_METHODS = [
        '@griffon.annotations.core.Nullable public java.net.URL getResourceAsURL(@griffon.annotations.core.Nonnull java.lang.String arg0)',
        '@griffon.annotations.core.Nullable public java.io.InputStream getResourceAsStream(@griffon.annotations.core.Nonnull java.lang.String arg0)',
        '@griffon.annotations.core.Nullable public java.util.List<java.net.URL> getResources(@griffon.annotations.core.Nonnull java.lang.String arg0)',
        '@griffon.annotations.core.Nonnull public java.lang.ClassLoader classloader()'
    ]

    private static final List<String> RESOURCE_RESOLVER_METHODS = [
        '@griffon.annotations.core.Nonnull public java.lang.Object resolveResourceValue(@griffon.annotations.core.Nonnull java.lang.String arg0, @griffon.annotations.core.Nonnull java.util.Locale arg1) throws griffon.core.resources.NoSuchResourceException',
        '@griffon.annotations.core.Nonnull public java.lang.Object resolveResource(@griffon.annotations.core.Nonnull java.lang.String arg0) throws griffon.core.resources.NoSuchResourceException',
        '@griffon.annotations.core.Nonnull public java.lang.Object resolveResource(@griffon.annotations.core.Nonnull java.lang.String arg0, @griffon.annotations.core.Nonnull java.util.Locale arg1) throws griffon.core.resources.NoSuchResourceException',
        '@griffon.annotations.core.Nonnull public java.lang.Object resolveResource(@griffon.annotations.core.Nonnull java.lang.String arg0, @griffon.annotations.core.Nonnull java.lang.Object[] arg1) throws griffon.core.resources.NoSuchResourceException',
        '@griffon.annotations.core.Nonnull public java.lang.Object resolveResource(@griffon.annotations.core.Nonnull java.lang.String arg0, @griffon.annotations.core.Nonnull java.lang.Object[] arg1, @griffon.annotations.core.Nonnull java.util.Locale arg2) throws griffon.core.resources.NoSuchResourceException',
        '@griffon.annotations.core.Nonnull public java.lang.Object resolveResource(@griffon.annotations.core.Nonnull java.lang.String arg0, @griffon.annotations.core.Nonnull java.util.List<?> arg1) throws griffon.core.resources.NoSuchResourceException',
        '@griffon.annotations.core.Nonnull public java.lang.Object resolveResource(@griffon.annotations.core.Nonnull java.lang.String arg0, @griffon.annotations.core.Nonnull java.util.List<?> arg1, @griffon.annotations.core.Nonnull java.util.Locale arg2) throws griffon.core.resources.NoSuchResourceException',
        '@griffon.annotations.core.Nonnull public java.lang.Object resolveResource(@griffon.annotations.core.Nonnull java.lang.String arg0, @griffon.annotations.core.Nonnull java.util.Map<java.lang.String, java.lang.Object> arg1) throws griffon.core.resources.NoSuchResourceException',
        '@griffon.annotations.core.Nonnull public java.lang.Object resolveResource(@griffon.annotations.core.Nonnull java.lang.String arg0, @griffon.annotations.core.Nonnull java.util.Map<java.lang.String, java.lang.Object> arg1, @griffon.annotations.core.Nonnull java.util.Locale arg2) throws griffon.core.resources.NoSuchResourceException',
        '@griffon.annotations.core.Nullable public java.lang.Object resolveResource(@griffon.annotations.core.Nonnull java.lang.String arg0, @griffon.annotations.core.Nullable java.lang.Object arg1)',
        '@griffon.annotations.core.Nullable public java.lang.Object resolveResource(@griffon.annotations.core.Nonnull java.lang.String arg0, @griffon.annotations.core.Nonnull java.util.Locale arg1, @griffon.annotations.core.Nullable java.lang.Object arg2)',
        '@griffon.annotations.core.Nullable public java.lang.Object resolveResource(@griffon.annotations.core.Nonnull java.lang.String arg0, @griffon.annotations.core.Nonnull java.lang.Object[] arg1, @griffon.annotations.core.Nullable java.lang.Object arg2)',
        '@griffon.annotations.core.Nullable public java.lang.Object resolveResource(@griffon.annotations.core.Nonnull java.lang.String arg0, @griffon.annotations.core.Nonnull java.lang.Object[] arg1, @griffon.annotations.core.Nonnull java.util.Locale arg2, @griffon.annotations.core.Nullable java.lang.Object arg3)',
        '@griffon.annotations.core.Nullable public java.lang.Object resolveResource(@griffon.annotations.core.Nonnull java.lang.String arg0, @griffon.annotations.core.Nonnull java.util.List<?> arg1, @griffon.annotations.core.Nullable java.lang.Object arg2)',
        '@griffon.annotations.core.Nullable public java.lang.Object resolveResource(@griffon.annotations.core.Nonnull java.lang.String arg0, @griffon.annotations.core.Nonnull java.util.List<?> arg1, @griffon.annotations.core.Nonnull java.util.Locale arg2, @griffon.annotations.core.Nullable java.lang.Object arg3)',
        '@griffon.annotations.core.Nullable public java.lang.Object resolveResource(@griffon.annotations.core.Nonnull java.lang.String arg0, @griffon.annotations.core.Nonnull java.util.Map<java.lang.String, java.lang.Object> arg1, @griffon.annotations.core.Nullable java.lang.Object arg2)',
        '@griffon.annotations.core.Nullable public java.lang.Object resolveResource(@griffon.annotations.core.Nonnull java.lang.String arg0, @griffon.annotations.core.Nonnull java.util.Map<java.lang.String, java.lang.Object> arg1, @griffon.annotations.core.Nonnull java.util.Locale arg2, @griffon.annotations.core.Nullable java.lang.Object arg3)',
        '@griffon.annotations.core.Nonnull public <T> T resolveResourceConverted(@griffon.annotations.core.Nonnull java.lang.String arg0, @griffon.annotations.core.Nonnull java.lang.Class<T> arg1) throws griffon.core.resources.NoSuchResourceException',
        '@griffon.annotations.core.Nonnull public <T> T resolveResourceConverted(@griffon.annotations.core.Nonnull java.lang.String arg0, @griffon.annotations.core.Nonnull java.util.Locale arg1, @griffon.annotations.core.Nonnull java.lang.Class<T> arg2) throws griffon.core.resources.NoSuchResourceException',
        '@griffon.annotations.core.Nonnull public <T> T resolveResourceConverted(@griffon.annotations.core.Nonnull java.lang.String arg0, @griffon.annotations.core.Nonnull java.lang.Object[] arg1, @griffon.annotations.core.Nonnull java.lang.Class<T> arg2) throws griffon.core.resources.NoSuchResourceException',
        '@griffon.annotations.core.Nonnull public <T> T resolveResourceConverted(@griffon.annotations.core.Nonnull java.lang.String arg0, @griffon.annotations.core.Nonnull java.lang.Object[] arg1, @griffon.annotations.core.Nonnull java.util.Locale arg2, @griffon.annotations.core.Nonnull java.lang.Class<T> arg3) throws griffon.core.resources.NoSuchResourceException',
        '@griffon.annotations.core.Nonnull public <T> T resolveResourceConverted(@griffon.annotations.core.Nonnull java.lang.String arg0, @griffon.annotations.core.Nonnull java.util.List<?> arg1, @griffon.annotations.core.Nonnull java.lang.Class<T> arg2) throws griffon.core.resources.NoSuchResourceException',
        '@griffon.annotations.core.Nonnull public <T> T resolveResourceConverted(@griffon.annotations.core.Nonnull java.lang.String arg0, @griffon.annotations.core.Nonnull java.util.List<?> arg1, @griffon.annotations.core.Nonnull java.util.Locale arg2, @griffon.annotations.core.Nonnull java.lang.Class<T> arg3) throws griffon.core.resources.NoSuchResourceException',
        '@griffon.annotations.core.Nonnull public <T> T resolveResourceConverted(@griffon.annotations.core.Nonnull java.lang.String arg0, @griffon.annotations.core.Nonnull java.util.Map<java.lang.String, java.lang.Object> arg1, @griffon.annotations.core.Nonnull java.lang.Class<T> arg2) throws griffon.core.resources.NoSuchResourceException',
        '@griffon.annotations.core.Nonnull public <T> T resolveResourceConverted(@griffon.annotations.core.Nonnull java.lang.String arg0, @griffon.annotations.core.Nonnull java.util.Map<java.lang.String, java.lang.Object> arg1, @griffon.annotations.core.Nonnull java.util.Locale arg2, @griffon.annotations.core.Nonnull java.lang.Class<T> arg3) throws griffon.core.resources.NoSuchResourceException',
        '@griffon.annotations.core.Nullable public <T> T resolveResourceConverted(@griffon.annotations.core.Nonnull java.lang.String arg0, @griffon.annotations.core.Nullable java.lang.Object arg1, @griffon.annotations.core.Nonnull java.lang.Class<T> arg2)',
        '@griffon.annotations.core.Nullable public <T> T resolveResourceConverted(@griffon.annotations.core.Nonnull java.lang.String arg0, @griffon.annotations.core.Nonnull java.util.Locale arg1, @griffon.annotations.core.Nullable java.lang.Object arg2, @griffon.annotations.core.Nonnull java.lang.Class<T> arg3)',
        '@griffon.annotations.core.Nullable public <T> T resolveResourceConverted(@griffon.annotations.core.Nonnull java.lang.String arg0, @griffon.annotations.core.Nonnull java.lang.Object[] arg1, @griffon.annotations.core.Nullable java.lang.Object arg2, @griffon.annotations.core.Nonnull java.lang.Class<T> arg3)',
        '@griffon.annotations.core.Nullable public <T> T resolveResourceConverted(@griffon.annotations.core.Nonnull java.lang.String arg0, @griffon.annotations.core.Nonnull java.lang.Object[] arg1, @griffon.annotations.core.Nonnull java.util.Locale arg2, @griffon.annotations.core.Nullable java.lang.Object arg3, @griffon.annotations.core.Nonnull java.lang.Class<T> arg4)',
        '@griffon.annotations.core.Nullable public <T> T resolveResourceConverted(@griffon.annotations.core.Nonnull java.lang.String arg0, @griffon.annotations.core.Nonnull java.util.List<?> arg1, @griffon.annotations.core.Nullable java.lang.Object arg2, @griffon.annotations.core.Nonnull java.lang.Class<T> arg3)',
        '@griffon.annotations.core.Nullable public <T> T resolveResourceConverted(@griffon.annotations.core.Nonnull java.lang.String arg0, @griffon.annotations.core.Nonnull java.util.List<?> arg1, @griffon.annotations.core.Nonnull java.util.Locale arg2, @griffon.annotations.core.Nullable java.lang.Object arg3, @griffon.annotations.core.Nonnull java.lang.Class<T> arg4)',
        '@griffon.annotations.core.Nullable public <T> T resolveResourceConverted(@griffon.annotations.core.Nonnull java.lang.String arg0, @griffon.annotations.core.Nonnull java.util.Map<java.lang.String, java.lang.Object> arg1, @griffon.annotations.core.Nullable java.lang.Object arg2, @griffon.annotations.core.Nonnull java.lang.Class<T> arg3)',
        '@griffon.annotations.core.Nullable public <T> T resolveResourceConverted(@griffon.annotations.core.Nonnull java.lang.String arg0, @griffon.annotations.core.Nonnull java.util.Map<java.lang.String, java.lang.Object> arg1, @griffon.annotations.core.Nonnull java.util.Locale arg2, @griffon.annotations.core.Nullable java.lang.Object arg3, @griffon.annotations.core.Nonnull java.lang.Class<T> arg4)',
        '@griffon.annotations.core.Nonnull public java.lang.String formatResource(@griffon.annotations.core.Nonnull java.lang.Object[] arg0, @griffon.annotations.core.Nonnull java.util.Map<java.lang.String, java.lang.Object> arg1)',
        '@griffon.annotations.core.Nonnull public java.lang.String formatResource(@griffon.annotations.core.Nonnull java.lang.String arg0, @griffon.annotations.core.Nonnull java.util.List<?> arg1)',
        '@griffon.annotations.core.Nonnull public java.lang.String formatResource(@griffon.annotations.core.Nonnull java.lang.String arg0, @griffon.annotations.core.Nonnull java.util.Map<java.lang.String, java.lang.Object> arg1)'
    ]

    private static final List<String> THREADING_METHODS = [
        'public boolean isUIThread()',
        'public void executeInsideUIAsync(@griffon.annotations.core.Nonnull java.lang.Runnable arg0)',
        'public void executeInsideUISync(@griffon.annotations.core.Nonnull java.lang.Runnable arg0)',
        'public void executeOutsideUI(@griffon.annotations.core.Nonnull java.lang.Runnable arg0)',
        'public void executeOutsideUIAsync(@griffon.annotations.core.Nonnull java.lang.Runnable arg0)',
        '@griffon.annotations.core.Nonnull public <R> java.util.concurrent.Future<R> executeFuture(@griffon.annotations.core.Nonnull java.util.concurrent.ExecutorService arg0, @griffon.annotations.core.Nonnull java.util.concurrent.Callable<R> arg1)',
        '@griffon.annotations.core.Nonnull public <R> java.util.concurrent.Future<R> executeFuture(@griffon.annotations.core.Nonnull java.util.concurrent.Callable<R> arg0)',
        '@griffon.annotations.core.Nullable public <R> R executeInsideUISync(@griffon.annotations.core.Nonnull java.util.concurrent.Callable<R> arg0)'
    ]
}
