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

    void "Verify methods of org.codehaus.griffon.compile.core.ObservableConstants"() {
        given:
        def actualMethods = ObservableConstants.OBSERVABLE_METHODS + ObservableConstants.OBSERVABLE_FIRE_METHODS

        expect:
        actualMethods.size() == OBSERVABLE_METHODS.size()
        actualMethods.each { assert OBSERVABLE_METHODS.contains(it.toString()) }
    }

    void "Verify methods of org.codehaus.griffon.compile.core.VetoableConstants"() {
        given:
        def actualMethods = VetoableConstants.VETOABLE_METHODS + VetoableConstants.VETOABLE_FIRE_METHODS

        expect:
        actualMethods.size() == VETOABLE_METHODS.size()
        actualMethods.each { assert VETOABLE_METHODS.contains(it.toString()) }
    }

    private static final List<String> EVENT_PUBLISHER_METHODS = [
        'public void addEventListener(@javax.annotation.Nonnull java.lang.Object arg0)',
        'public void addEventListener(@javax.annotation.Nonnull java.lang.String arg0, @javax.annotation.Nonnull griffon.core.CallableWithArgs<?> arg1)',
        'public void addEventListener(@javax.annotation.Nonnull java.lang.String arg0, @javax.annotation.Nonnull griffon.core.RunnableWithArgs<?> arg1)',
        'public void addEventListener(@javax.annotation.Nonnull java.util.Map<java.lang.String, java.lang.Object> arg0)',
        'public <E extends griffon.core.event.Event> void addEventListener(@javax.annotation.Nonnull java.lang.Class<E> arg0, @javax.annotation.Nonnull griffon.core.CallableWithArgs<?> arg1)',
        'public <E extends griffon.core.event.Event> void addEventListener(@javax.annotation.Nonnull java.lang.Class<E> arg0, @javax.annotation.Nonnull griffon.core.RunnableWithArgs<?> arg1)',
        'public void removeEventListener(@javax.annotation.Nonnull java.lang.Object arg0)',
        'public void removeEventListener(@javax.annotation.Nonnull java.lang.String arg0, @javax.annotation.Nonnull griffon.core.CallableWithArgs<?> arg1)',
        'public void removeEventListener(@javax.annotation.Nonnull java.lang.String arg0, @javax.annotation.Nonnull griffon.core.RunnableWithArgs<?> arg1)',
        'public void removeEventListener(@javax.annotation.Nonnull java.util.Map<java.lang.String, java.lang.Object> arg0)',
        'public <E extends griffon.core.event.Event> void removeEventListener(@javax.annotation.Nonnull java.lang.Class<E> arg0, @javax.annotation.Nonnull griffon.core.CallableWithArgs<?> arg1)',
        'public <E extends griffon.core.event.Event> void removeEventListener(@javax.annotation.Nonnull java.lang.Class<E> arg0, @javax.annotation.Nonnull griffon.core.RunnableWithArgs<?> arg1)',
        'public void publishEvent(@javax.annotation.Nonnull java.lang.String arg0)',
        'public void publishEvent(@javax.annotation.Nonnull java.lang.String arg0, @javax.annotation.Nonnull java.util.List<?> arg1)',
        'public void publishEvent(@javax.annotation.Nonnull griffon.core.event.Event arg0)',
        'public void publishEventOutsideUI(@javax.annotation.Nonnull java.lang.String arg0)',
        'public void publishEventOutsideUI(@javax.annotation.Nonnull java.lang.String arg0, @javax.annotation.Nonnull java.util.List<?> arg1)',
        'public void publishEventOutsideUI(@javax.annotation.Nonnull griffon.core.event.Event arg0)',
        'public void publishEventAsync(@javax.annotation.Nonnull java.lang.String arg0)',
        'public void publishEventAsync(@javax.annotation.Nonnull java.lang.String arg0, @javax.annotation.Nonnull java.util.List<?> arg1)',
        'public void publishEventAsync(@javax.annotation.Nonnull griffon.core.event.Event arg0)',
        'public boolean isEventPublishingEnabled()',
        'public void setEventPublishingEnabled(boolean arg0)',
        '@javax.annotation.Nonnull public java.util.Collection<java.lang.Object> getEventListeners()',
        '@javax.annotation.Nonnull public java.util.Collection<java.lang.Object> getEventListeners(@javax.annotation.Nonnull java.lang.String arg0)',
    ]

    private static final List<String> MESSAGE_SOURCE_METHODS = [
        '@javax.annotation.Nonnull public java.lang.String resolveMessageValue(@javax.annotation.Nonnull java.lang.String arg0, @javax.annotation.Nonnull java.util.Locale arg1) throws griffon.core.i18n.NoSuchMessageException',
        '@javax.annotation.Nonnull public java.lang.String getMessage(@javax.annotation.Nonnull java.lang.String arg0) throws griffon.core.i18n.NoSuchMessageException',
        '@javax.annotation.Nonnull public java.lang.String getMessage(@javax.annotation.Nonnull java.lang.String arg0, @javax.annotation.Nonnull java.util.Locale arg1) throws griffon.core.i18n.NoSuchMessageException',
        '@javax.annotation.Nonnull public java.lang.String getMessage(@javax.annotation.Nonnull java.lang.String arg0, @javax.annotation.Nonnull java.lang.Object[] arg1) throws griffon.core.i18n.NoSuchMessageException',
        '@javax.annotation.Nonnull public java.lang.String getMessage(@javax.annotation.Nonnull java.lang.String arg0, @javax.annotation.Nonnull java.lang.Object[] arg1, @javax.annotation.Nonnull java.util.Locale arg2) throws griffon.core.i18n.NoSuchMessageException',
        '@javax.annotation.Nonnull public java.lang.String getMessage(@javax.annotation.Nonnull java.lang.String arg0, @javax.annotation.Nonnull java.util.List<?> arg1) throws griffon.core.i18n.NoSuchMessageException',
        '@javax.annotation.Nonnull public java.lang.String getMessage(@javax.annotation.Nonnull java.lang.String arg0, @javax.annotation.Nonnull java.util.List<?> arg1, @javax.annotation.Nonnull java.util.Locale arg2) throws griffon.core.i18n.NoSuchMessageException',
        '@javax.annotation.Nonnull public java.lang.String getMessage(@javax.annotation.Nonnull java.lang.String arg0, @javax.annotation.Nonnull java.util.Map<java.lang.String, java.lang.Object> arg1) throws griffon.core.i18n.NoSuchMessageException',
        '@javax.annotation.Nonnull public java.lang.String getMessage(@javax.annotation.Nonnull java.lang.String arg0, @javax.annotation.Nonnull java.util.Map<java.lang.String, java.lang.Object> arg1, @javax.annotation.Nonnull java.util.Locale arg2) throws griffon.core.i18n.NoSuchMessageException',
        '@javax.annotation.Nullable public java.lang.String getMessage(@javax.annotation.Nonnull java.lang.String arg0, @javax.annotation.Nullable java.lang.String arg1)',
        '@javax.annotation.Nullable public java.lang.String getMessage(@javax.annotation.Nonnull java.lang.String arg0, @javax.annotation.Nonnull java.util.Locale arg1, @javax.annotation.Nullable java.lang.String arg2)',
        '@javax.annotation.Nullable public java.lang.String getMessage(@javax.annotation.Nonnull java.lang.String arg0, @javax.annotation.Nonnull java.lang.Object[] arg1, @javax.annotation.Nullable java.lang.String arg2)',
        '@javax.annotation.Nullable public java.lang.String getMessage(@javax.annotation.Nonnull java.lang.String arg0, @javax.annotation.Nonnull java.lang.Object[] arg1, @javax.annotation.Nonnull java.util.Locale arg2, @javax.annotation.Nullable java.lang.String arg3)',
        '@javax.annotation.Nullable public java.lang.String getMessage(@javax.annotation.Nonnull java.lang.String arg0, @javax.annotation.Nonnull java.util.List<?> arg1, @javax.annotation.Nullable java.lang.String arg2)',
        '@javax.annotation.Nullable public java.lang.String getMessage(@javax.annotation.Nonnull java.lang.String arg0, @javax.annotation.Nonnull java.util.List<?> arg1, @javax.annotation.Nonnull java.util.Locale arg2, @javax.annotation.Nullable java.lang.String arg3)',
        '@javax.annotation.Nullable public java.lang.String getMessage(@javax.annotation.Nonnull java.lang.String arg0, @javax.annotation.Nonnull java.util.Map<java.lang.String, java.lang.Object> arg1, @javax.annotation.Nullable java.lang.String arg2)',
        '@javax.annotation.Nullable public java.lang.String getMessage(@javax.annotation.Nonnull java.lang.String arg0, @javax.annotation.Nonnull java.util.Map<java.lang.String, java.lang.Object> arg1, @javax.annotation.Nonnull java.util.Locale arg2, @javax.annotation.Nullable java.lang.String arg3)',
        '@javax.annotation.Nonnull public java.lang.String formatMessage(@javax.annotation.Nonnull java.lang.Object[] arg0, @javax.annotation.Nonnull java.util.Map<java.lang.String, java.lang.Object> arg1)',
        '@javax.annotation.Nonnull public java.lang.String formatMessage(@javax.annotation.Nonnull java.lang.String arg0, @javax.annotation.Nonnull java.util.List<?> arg1)',
        '@javax.annotation.Nonnull public java.lang.String formatMessage(@javax.annotation.Nonnull java.lang.String arg0, @javax.annotation.Nonnull java.util.Map<java.lang.String, java.lang.Object> arg1)',
    ]

    private static final List<String> MVC_METHODS = [
        'public void destroyMVCGroup(@javax.annotation.Nonnull java.lang.String arg0)',
        '@javax.annotation.Nonnull public griffon.core.mvc.MVCGroup createMVCGroup(@javax.annotation.Nonnull java.lang.String arg0)',
        '@javax.annotation.Nonnull public griffon.core.mvc.MVCGroup createMVCGroup(@javax.annotation.Nonnull java.lang.String arg0, java.lang.String arg1)',
        '@javax.annotation.Nonnull public griffon.core.mvc.MVCGroup createMVCGroup(@javax.annotation.Nonnull java.util.Map<java.lang.String, java.lang.Object> arg0, @javax.annotation.Nonnull java.lang.String arg1)',
        '@javax.annotation.Nonnull public griffon.core.mvc.MVCGroup createMVCGroup(@javax.annotation.Nonnull java.lang.String arg0, @javax.annotation.Nonnull java.util.Map<java.lang.String, java.lang.Object> arg1)',
        '@javax.annotation.Nonnull public griffon.core.mvc.MVCGroup createMVCGroup(@javax.annotation.Nonnull java.util.Map<java.lang.String, java.lang.Object> arg0, @javax.annotation.Nonnull java.lang.String arg1, @javax.annotation.Nonnull java.lang.String arg2)',
        '@javax.annotation.Nonnull public griffon.core.mvc.MVCGroup createMVCGroup(@javax.annotation.Nonnull java.lang.String arg0, @javax.annotation.Nonnull java.lang.String arg1, @javax.annotation.Nonnull java.util.Map<java.lang.String, java.lang.Object> arg2)',
        '@javax.annotation.Nonnull public java.util.List<? extends griffon.core.artifact.GriffonMvcArtifact> createMVC(@javax.annotation.Nonnull java.lang.String arg0)',
        '@javax.annotation.Nonnull public java.util.List<? extends griffon.core.artifact.GriffonMvcArtifact> createMVC(@javax.annotation.Nonnull java.util.Map<java.lang.String, java.lang.Object> arg0, @javax.annotation.Nonnull java.lang.String arg1)',
        '@javax.annotation.Nonnull public java.util.List<? extends griffon.core.artifact.GriffonMvcArtifact> createMVC(@javax.annotation.Nonnull java.lang.String arg0, @javax.annotation.Nonnull java.util.Map<java.lang.String, java.lang.Object> arg1)',
        '@javax.annotation.Nonnull public java.util.List<? extends griffon.core.artifact.GriffonMvcArtifact> createMVC(@javax.annotation.Nonnull java.lang.String arg0, @javax.annotation.Nonnull java.lang.String arg1)',
        '@javax.annotation.Nonnull public java.util.List<? extends griffon.core.artifact.GriffonMvcArtifact> createMVC(@javax.annotation.Nonnull java.util.Map<java.lang.String, java.lang.Object> arg0, @javax.annotation.Nonnull java.lang.String arg1, @javax.annotation.Nonnull java.lang.String arg2)',
        '@javax.annotation.Nonnull public java.util.List<? extends griffon.core.artifact.GriffonMvcArtifact> createMVC(@javax.annotation.Nonnull java.lang.String arg0, @javax.annotation.Nonnull java.lang.String arg1, @javax.annotation.Nonnull java.util.Map<java.lang.String, java.lang.Object> arg2)',
        'public <M extends griffon.core.artifact.GriffonModel, V extends griffon.core.artifact.GriffonView, C extends griffon.core.artifact.GriffonController> void withMVC(@javax.annotation.Nonnull java.lang.String arg0, @javax.annotation.Nonnull griffon.core.mvc.MVCFunction<M, V, C> arg1)',
        'public <M extends griffon.core.artifact.GriffonModel, V extends griffon.core.artifact.GriffonView, C extends griffon.core.artifact.GriffonController> void withMVC(@javax.annotation.Nonnull java.lang.String arg0, @javax.annotation.Nonnull java.lang.String arg1, @javax.annotation.Nonnull griffon.core.mvc.MVCFunction<M, V, C> arg2)',
        'public <M extends griffon.core.artifact.GriffonModel, V extends griffon.core.artifact.GriffonView, C extends griffon.core.artifact.GriffonController> void withMVC(@javax.annotation.Nonnull java.lang.String arg0, @javax.annotation.Nonnull java.lang.String arg1, @javax.annotation.Nonnull java.util.Map<java.lang.String, java.lang.Object> arg2, @javax.annotation.Nonnull griffon.core.mvc.MVCFunction<M, V, C> arg3)',
        'public <M extends griffon.core.artifact.GriffonModel, V extends griffon.core.artifact.GriffonView, C extends griffon.core.artifact.GriffonController> void withMVC(@javax.annotation.Nonnull java.util.Map<java.lang.String, java.lang.Object> arg0, @javax.annotation.Nonnull java.lang.String arg1, @javax.annotation.Nonnull java.lang.String arg2, @javax.annotation.Nonnull griffon.core.mvc.MVCFunction<M, V, C> arg3)',
        'public <M extends griffon.core.artifact.GriffonModel, V extends griffon.core.artifact.GriffonView, C extends griffon.core.artifact.GriffonController> void withMVC(@javax.annotation.Nonnull java.lang.String arg0, @javax.annotation.Nonnull java.util.Map<java.lang.String, java.lang.Object> arg1, @javax.annotation.Nonnull griffon.core.mvc.MVCFunction<M, V, C> arg2)',
        'public <M extends griffon.core.artifact.GriffonModel, V extends griffon.core.artifact.GriffonView, C extends griffon.core.artifact.GriffonController> void withMVC(@javax.annotation.Nonnull java.util.Map<java.lang.String, java.lang.Object> arg0, @javax.annotation.Nonnull java.lang.String arg1, @javax.annotation.Nonnull griffon.core.mvc.MVCFunction<M, V, C> arg2)',
        'public void withMVCGroup(@javax.annotation.Nonnull java.lang.String arg0, @javax.annotation.Nonnull griffon.core.mvc.MVCGroupFunction arg1)',
        'public void withMVCGroup(@javax.annotation.Nonnull java.lang.String arg0, @javax.annotation.Nonnull java.lang.String arg1, @javax.annotation.Nonnull griffon.core.mvc.MVCGroupFunction arg2)',
        'public void withMVCGroup(@javax.annotation.Nonnull java.lang.String arg0, @javax.annotation.Nonnull java.lang.String arg1, @javax.annotation.Nonnull java.util.Map<java.lang.String, java.lang.Object> arg2, @javax.annotation.Nonnull griffon.core.mvc.MVCGroupFunction arg3)',
        'public void withMVCGroup(@javax.annotation.Nonnull java.util.Map<java.lang.String, java.lang.Object> arg0, @javax.annotation.Nonnull java.lang.String arg1, @javax.annotation.Nonnull java.lang.String arg2, @javax.annotation.Nonnull griffon.core.mvc.MVCGroupFunction arg3)',
        'public void withMVCGroup(@javax.annotation.Nonnull java.lang.String arg0, @javax.annotation.Nonnull java.util.Map<java.lang.String, java.lang.Object> arg1, @javax.annotation.Nonnull griffon.core.mvc.MVCGroupFunction arg2)',
        'public void withMVCGroup(@javax.annotation.Nonnull java.util.Map<java.lang.String, java.lang.Object> arg0, @javax.annotation.Nonnull java.lang.String arg1, @javax.annotation.Nonnull griffon.core.mvc.MVCGroupFunction arg2)',
    ]

    private static final List<String> RESOURCES_METHODS = [
        '@javax.annotation.Nullable public java.net.URL getResourceAsURL(@javax.annotation.Nonnull java.lang.String arg0)',
        '@javax.annotation.Nullable public java.io.InputStream getResourceAsStream(@javax.annotation.Nonnull java.lang.String arg0)',
        '@javax.annotation.Nullable public java.util.List<java.net.URL> getResources(@javax.annotation.Nonnull java.lang.String arg0)',
        '@javax.annotation.Nonnull public java.lang.ClassLoader classloader()'
    ]

    private static final List<String> RESOURCE_RESOLVER_METHODS = [
        '@javax.annotation.Nonnull public java.lang.Object resolveResourceValue(@javax.annotation.Nonnull java.lang.String arg0, @javax.annotation.Nonnull java.util.Locale arg1) throws griffon.core.resources.NoSuchResourceException',
        '@javax.annotation.Nonnull public java.lang.Object resolveResource(@javax.annotation.Nonnull java.lang.String arg0) throws griffon.core.resources.NoSuchResourceException',
        '@javax.annotation.Nonnull public java.lang.Object resolveResource(@javax.annotation.Nonnull java.lang.String arg0, @javax.annotation.Nonnull java.util.Locale arg1) throws griffon.core.resources.NoSuchResourceException',
        '@javax.annotation.Nonnull public java.lang.Object resolveResource(@javax.annotation.Nonnull java.lang.String arg0, @javax.annotation.Nonnull java.lang.Object[] arg1) throws griffon.core.resources.NoSuchResourceException',
        '@javax.annotation.Nonnull public java.lang.Object resolveResource(@javax.annotation.Nonnull java.lang.String arg0, @javax.annotation.Nonnull java.lang.Object[] arg1, @javax.annotation.Nonnull java.util.Locale arg2) throws griffon.core.resources.NoSuchResourceException',
        '@javax.annotation.Nonnull public java.lang.Object resolveResource(@javax.annotation.Nonnull java.lang.String arg0, @javax.annotation.Nonnull java.util.List<?> arg1) throws griffon.core.resources.NoSuchResourceException',
        '@javax.annotation.Nonnull public java.lang.Object resolveResource(@javax.annotation.Nonnull java.lang.String arg0, @javax.annotation.Nonnull java.util.List<?> arg1, @javax.annotation.Nonnull java.util.Locale arg2) throws griffon.core.resources.NoSuchResourceException',
        '@javax.annotation.Nonnull public java.lang.Object resolveResource(@javax.annotation.Nonnull java.lang.String arg0, @javax.annotation.Nonnull java.util.Map<java.lang.String, java.lang.Object> arg1) throws griffon.core.resources.NoSuchResourceException',
        '@javax.annotation.Nonnull public java.lang.Object resolveResource(@javax.annotation.Nonnull java.lang.String arg0, @javax.annotation.Nonnull java.util.Map<java.lang.String, java.lang.Object> arg1, @javax.annotation.Nonnull java.util.Locale arg2) throws griffon.core.resources.NoSuchResourceException',
        '@javax.annotation.Nullable public java.lang.Object resolveResource(@javax.annotation.Nonnull java.lang.String arg0, @javax.annotation.Nullable java.lang.Object arg1)',
        '@javax.annotation.Nullable public java.lang.Object resolveResource(@javax.annotation.Nonnull java.lang.String arg0, @javax.annotation.Nonnull java.util.Locale arg1, @javax.annotation.Nullable java.lang.Object arg2)',
        '@javax.annotation.Nullable public java.lang.Object resolveResource(@javax.annotation.Nonnull java.lang.String arg0, @javax.annotation.Nonnull java.lang.Object[] arg1, @javax.annotation.Nullable java.lang.Object arg2)',
        '@javax.annotation.Nullable public java.lang.Object resolveResource(@javax.annotation.Nonnull java.lang.String arg0, @javax.annotation.Nonnull java.lang.Object[] arg1, @javax.annotation.Nonnull java.util.Locale arg2, @javax.annotation.Nullable java.lang.Object arg3)',
        '@javax.annotation.Nullable public java.lang.Object resolveResource(@javax.annotation.Nonnull java.lang.String arg0, @javax.annotation.Nonnull java.util.List<?> arg1, @javax.annotation.Nullable java.lang.Object arg2)',
        '@javax.annotation.Nullable public java.lang.Object resolveResource(@javax.annotation.Nonnull java.lang.String arg0, @javax.annotation.Nonnull java.util.List<?> arg1, @javax.annotation.Nonnull java.util.Locale arg2, @javax.annotation.Nullable java.lang.Object arg3)',
        '@javax.annotation.Nullable public java.lang.Object resolveResource(@javax.annotation.Nonnull java.lang.String arg0, @javax.annotation.Nonnull java.util.Map<java.lang.String, java.lang.Object> arg1, @javax.annotation.Nullable java.lang.Object arg2)',
        '@javax.annotation.Nullable public java.lang.Object resolveResource(@javax.annotation.Nonnull java.lang.String arg0, @javax.annotation.Nonnull java.util.Map<java.lang.String, java.lang.Object> arg1, @javax.annotation.Nonnull java.util.Locale arg2, @javax.annotation.Nullable java.lang.Object arg3)',
        '@javax.annotation.Nonnull public <T> T resolveResourceConverted(@javax.annotation.Nonnull java.lang.String arg0, @javax.annotation.Nonnull java.lang.Class<T> arg1) throws griffon.core.resources.NoSuchResourceException',
        '@javax.annotation.Nonnull public <T> T resolveResourceConverted(@javax.annotation.Nonnull java.lang.String arg0, @javax.annotation.Nonnull java.util.Locale arg1, @javax.annotation.Nonnull java.lang.Class<T> arg2) throws griffon.core.resources.NoSuchResourceException',
        '@javax.annotation.Nonnull public <T> T resolveResourceConverted(@javax.annotation.Nonnull java.lang.String arg0, @javax.annotation.Nonnull java.lang.Object[] arg1, @javax.annotation.Nonnull java.lang.Class<T> arg2) throws griffon.core.resources.NoSuchResourceException',
        '@javax.annotation.Nonnull public <T> T resolveResourceConverted(@javax.annotation.Nonnull java.lang.String arg0, @javax.annotation.Nonnull java.lang.Object[] arg1, @javax.annotation.Nonnull java.util.Locale arg2, @javax.annotation.Nonnull java.lang.Class<T> arg3) throws griffon.core.resources.NoSuchResourceException',
        '@javax.annotation.Nonnull public <T> T resolveResourceConverted(@javax.annotation.Nonnull java.lang.String arg0, @javax.annotation.Nonnull java.util.List<?> arg1, @javax.annotation.Nonnull java.lang.Class<T> arg2) throws griffon.core.resources.NoSuchResourceException',
        '@javax.annotation.Nonnull public <T> T resolveResourceConverted(@javax.annotation.Nonnull java.lang.String arg0, @javax.annotation.Nonnull java.util.List<?> arg1, @javax.annotation.Nonnull java.util.Locale arg2, @javax.annotation.Nonnull java.lang.Class<T> arg3) throws griffon.core.resources.NoSuchResourceException',
        '@javax.annotation.Nonnull public <T> T resolveResourceConverted(@javax.annotation.Nonnull java.lang.String arg0, @javax.annotation.Nonnull java.util.Map<java.lang.String, java.lang.Object> arg1, @javax.annotation.Nonnull java.lang.Class<T> arg2) throws griffon.core.resources.NoSuchResourceException',
        '@javax.annotation.Nonnull public <T> T resolveResourceConverted(@javax.annotation.Nonnull java.lang.String arg0, @javax.annotation.Nonnull java.util.Map<java.lang.String, java.lang.Object> arg1, @javax.annotation.Nonnull java.util.Locale arg2, @javax.annotation.Nonnull java.lang.Class<T> arg3) throws griffon.core.resources.NoSuchResourceException',
        '@javax.annotation.Nullable public <T> T resolveResourceConverted(@javax.annotation.Nonnull java.lang.String arg0, @javax.annotation.Nullable java.lang.Object arg1, @javax.annotation.Nonnull java.lang.Class<T> arg2)',
        '@javax.annotation.Nullable public <T> T resolveResourceConverted(@javax.annotation.Nonnull java.lang.String arg0, @javax.annotation.Nonnull java.util.Locale arg1, @javax.annotation.Nullable java.lang.Object arg2, @javax.annotation.Nonnull java.lang.Class<T> arg3)',
        '@javax.annotation.Nullable public <T> T resolveResourceConverted(@javax.annotation.Nonnull java.lang.String arg0, @javax.annotation.Nonnull java.lang.Object[] arg1, @javax.annotation.Nullable java.lang.Object arg2, @javax.annotation.Nonnull java.lang.Class<T> arg3)',
        '@javax.annotation.Nullable public <T> T resolveResourceConverted(@javax.annotation.Nonnull java.lang.String arg0, @javax.annotation.Nonnull java.lang.Object[] arg1, @javax.annotation.Nonnull java.util.Locale arg2, @javax.annotation.Nullable java.lang.Object arg3, @javax.annotation.Nonnull java.lang.Class<T> arg4)',
        '@javax.annotation.Nullable public <T> T resolveResourceConverted(@javax.annotation.Nonnull java.lang.String arg0, @javax.annotation.Nonnull java.util.List<?> arg1, @javax.annotation.Nullable java.lang.Object arg2, @javax.annotation.Nonnull java.lang.Class<T> arg3)',
        '@javax.annotation.Nullable public <T> T resolveResourceConverted(@javax.annotation.Nonnull java.lang.String arg0, @javax.annotation.Nonnull java.util.List<?> arg1, @javax.annotation.Nonnull java.util.Locale arg2, @javax.annotation.Nullable java.lang.Object arg3, @javax.annotation.Nonnull java.lang.Class<T> arg4)',
        '@javax.annotation.Nullable public <T> T resolveResourceConverted(@javax.annotation.Nonnull java.lang.String arg0, @javax.annotation.Nonnull java.util.Map<java.lang.String, java.lang.Object> arg1, @javax.annotation.Nullable java.lang.Object arg2, @javax.annotation.Nonnull java.lang.Class<T> arg3)',
        '@javax.annotation.Nullable public <T> T resolveResourceConverted(@javax.annotation.Nonnull java.lang.String arg0, @javax.annotation.Nonnull java.util.Map<java.lang.String, java.lang.Object> arg1, @javax.annotation.Nonnull java.util.Locale arg2, @javax.annotation.Nullable java.lang.Object arg3, @javax.annotation.Nonnull java.lang.Class<T> arg4)',
        '@javax.annotation.Nonnull public java.lang.String formatResource(@javax.annotation.Nonnull java.lang.Object[] arg0, @javax.annotation.Nonnull java.util.Map<java.lang.String, java.lang.Object> arg1)',
        '@javax.annotation.Nonnull public java.lang.String formatResource(@javax.annotation.Nonnull java.lang.String arg0, @javax.annotation.Nonnull java.util.List<?> arg1)',
        '@javax.annotation.Nonnull public java.lang.String formatResource(@javax.annotation.Nonnull java.lang.String arg0, @javax.annotation.Nonnull java.util.Map<java.lang.String, java.lang.Object> arg1)'
    ]

    private static final List<String> THREADING_METHODS = [
        'public boolean isUIThread()',
        'public void runInsideUIAsync(@javax.annotation.Nonnull java.lang.Runnable arg0)',
        'public void runInsideUISync(@javax.annotation.Nonnull java.lang.Runnable arg0)',
        'public void runOutsideUI(@javax.annotation.Nonnull java.lang.Runnable arg0)',
        '@javax.annotation.Nonnull public <R> java.util.concurrent.Future<R> runFuture(@javax.annotation.Nonnull java.util.concurrent.ExecutorService arg0, @javax.annotation.Nonnull java.util.concurrent.Callable<R> arg1)',
        '@javax.annotation.Nonnull public <R> java.util.concurrent.Future<R> runFuture(@javax.annotation.Nonnull java.util.concurrent.Callable<R> arg0)',
        '@javax.annotation.Nullable public <R> R runInsideUISync(@javax.annotation.Nonnull java.util.concurrent.Callable<R> arg0)'
    ]

    private static final List<String> OBSERVABLE_METHODS = [
        'public void addPropertyChangeListener(@javax.annotation.Nullable java.beans.PropertyChangeListener arg0)',
        'public void addPropertyChangeListener(@javax.annotation.Nullable java.lang.String arg0, @javax.annotation.Nullable java.beans.PropertyChangeListener arg1)',
        'public void removePropertyChangeListener(@javax.annotation.Nullable java.beans.PropertyChangeListener arg0)',
        'public void removePropertyChangeListener(@javax.annotation.Nullable java.lang.String arg0, @javax.annotation.Nullable java.beans.PropertyChangeListener arg1)',
        '@javax.annotation.Nonnull public java.beans.PropertyChangeListener[] getPropertyChangeListeners()',
        '@javax.annotation.Nonnull public java.beans.PropertyChangeListener[] getPropertyChangeListeners(@javax.annotation.Nullable java.lang.String arg0)',
        'protected void firePropertyChange(@javax.annotation.Nonnull java.beans.PropertyChangeEvent arg0)',
        'protected void firePropertyChange(@javax.annotation.Nonnull java.lang.String arg0, @javax.annotation.Nullable java.lang.Object arg1, @javax.annotation.Nullable java.lang.Object arg2)'
    ]

    private static final List<String> VETOABLE_METHODS = [
        'public void addVetoableChangeListener(@javax.annotation.Nullable java.beans.VetoableChangeListener arg0)',
        'public void addVetoableChangeListener(@javax.annotation.Nullable java.lang.String arg0, @javax.annotation.Nullable java.beans.VetoableChangeListener arg1)',
        'public void removeVetoableChangeListener(@javax.annotation.Nullable java.beans.VetoableChangeListener arg0)',
        'public void removeVetoableChangeListener(@javax.annotation.Nullable java.lang.String arg0, @javax.annotation.Nullable java.beans.VetoableChangeListener arg1)',
        '@javax.annotation.Nonnull public java.beans.VetoableChangeListener[] getVetoableChangeListeners()',
        '@javax.annotation.Nonnull public java.beans.VetoableChangeListener[] getVetoableChangeListeners(@javax.annotation.Nullable java.lang.String arg0)',
        'protected void fireVetoableChange(@javax.annotation.Nonnull java.beans.PropertyChangeEvent arg0) throws java.beans.PropertyVetoException',
        'protected void fireVetoableChange(@javax.annotation.Nonnull java.lang.String arg0, @javax.annotation.Nullable java.lang.Object arg1, @javax.annotation.Nullable java.lang.Object arg2) throws java.beans.PropertyVetoException'
    ]
}
