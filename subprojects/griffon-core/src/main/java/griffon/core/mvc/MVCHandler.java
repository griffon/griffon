/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2021 the original author or authors.
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
package griffon.core.mvc;

import griffon.core.artifact.GriffonController;
import griffon.core.artifact.GriffonModel;
import griffon.core.artifact.GriffonMvcArtifact;
import griffon.core.artifact.GriffonView;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

/**
 * Base contract for classes that can manipulate MVC groups.
 * There are 4 types of methods used for instantiating a group:
 * <ul>
 * <ol>{@code createMVCGroup()} - creates a new group instance returning all members.</ol>
 * <ol>{@code createMVC()} - creates a new group instance returning only Model, View and Controller members.</ol>
 * <ol>{@code withMVCGroup()} - creates a new group instance and destroys it immediately after it has been processed by the callback.</ol>
 * <ol>{@code withMVC()} - creates a new group instance and destroys it immediately after it has been processed by the callback.</ol>
 * </ul>
 * <p/>
 * It's worth mentioning that the value of the {@code mvcId} parameter must be unique otherwise a collision will occur.
 * When that happens the application will report and exception and terminate. This behavior can be configured to be more
 * lenient, by defining a configuration flag {@code griffon.mvcid.collision} in {@code Config}. <br/>
 * Accepted values are
 * <ul>
 * <ol>warning - reports the error but allows the application to continue. Destroys the existing group before continuing.</ol>
 * <ol>exception - reports the error and terminates the application. this is the default behavior.</ol>
 * </ul>
 *
 * @author Andres Almiray
 * @since 2.0.0
 */
public interface MVCHandler {
    /**
     * Instantiates an MVC group of the specified type.<p>
     * MVC groups must be previously configured with the application's metadata
     * before they can be used. This registration process usually takes place automatically
     * at boot time. The type of the group can be normally found in the application's
     * configuration file.<p>
     * For example, with the following entry available in {@code Config.groovy}
     * <p/>
     * <pre>
     * mvcGroups {
     *     foo {
     *         model      = 'com.acme.FooModel'
     *         view       = 'com.acme.FooView'
     *         controller = 'com.acme.FooController'
     *     }
     * }
     * </pre>
     * <p/>
     * An instance of the "foo" group can be created as follows
     * <p/>
     * <pre>
     * MVCGroup fooGroup = createMVCGroup('foo')
     * assert (fooGroup.controller instanceof FooController)
     * </pre>
     *
     * @param mvcType the type of group to build.
     *
     * @return an MVCGroup instance of the desired type
     *
     * @throws griffon.exceptions.MVCGroupInstantiationException - if the type specified is not found in the application's
     *                                                           configuration or if a group with the same mvcId exists already.
     */
    @Nonnull
    MVCGroup createMVCGroup(@Nonnull String mvcType);

    /**
     * Instantiates an MVC group of the specified type with a particular name.<p>
     * MVC groups must be previously configured with the application's metadata
     * before they can be used. This registration process usually takes place automatically
     * at boot time. The type of the group can be normally found in the application's
     * configuration file.<p>
     * For example, with the following entry available in {@code Config.groovy}
     * <p/>
     * <pre>
     * mvcGroups {
     *     foo {
     *         model      = 'com.acme.FooModel'
     *         view       = 'com.acme.FooView'
     *         controller = 'com.acme.FooController'
     *     }
     * }
     * </pre>
     * <p/>
     * An instance of the "foo" group can be created as follows
     * <p/>
     * <pre>
     * MVCGroup fooGroup = createMVCGroup('foo', 'foo' + System.currentTimeMillis())
     * assert (fooGroup.controller instanceof FooController)
     * </pre>
     * <p/>
     * MVC groups must have an unique name.
     *
     * @param mvcType the type of group to build.
     * @param mvcId   the name to assign to the built group.
     *
     * @return an MVCGroup instance of the desired type
     *
     * @throws griffon.exceptions.MVCGroupInstantiationException - if the type specified is not found in the application's
     *                                                           configuration or if a group with the same mvcId exists already.
     */
    @Nonnull
    MVCGroup createMVCGroup(@Nonnull String mvcType, @Nonnull String mvcId);

    /**
     * Instantiates an MVC group of the specified type with additional variables.<p>
     * MVC groups must be previously configured with the application's metadata
     * before they can be used. This registration process usually takes place automatically
     * at boot time. The type of the group can be normally found in the application's
     * configuration file.<p>
     * The <tt>args</tt> Map can contain any value that will be used in one of the following
     * scenarios <ul>
     * <li>The key matches a member definition; the value will be used as the instance of such member.</li>
     * <li>The key does not match a member definition, the value is assumed to be a property that can be set
     * on any MVC member of the group.</li>
     * </ul>
     * <p/>
     * For example, with the following entry available in {@code Config.groovy}
     * <p/>
     * <pre>
     * mvcGroups {
     *     foo {
     *         model      = 'com.acme.FooModel'
     *         view       = 'com.acme.FooView'
     *         controller = 'com.acme.FooController'
     *     }
     *     bar {
     *         model      = 'com.acme.FooModel'
     *         view       = 'com.acme.BarView'
     *         controller = 'com.acme.BarController'
     *     }
     * }
     * </pre>
     * <p/>
     * Notice that groups "foo" and "bar share the same model type, We can make them share the same model
     * instance by creating the groups in the following way:
     * <p/>
     * <pre>
     * MVCGroup fooGroup = createMVCGroup('foo')
     * MVCGroup barGroup = createMVCGroup('bar', model: fooGroup.model)
     * assert fooGroup.model == barGroup.model
     * </pre>
     *
     * @param args    any useful values that can be set as properties on each MVC member or that
     *                identify a member that can be shared with other groups.
     * @param mvcType the type of group to build.
     *
     * @return an MVCGroup instance of the desired type
     *
     * @throws griffon.exceptions.MVCGroupInstantiationException - if the type specified is not found in the application's
     *                                                           configuration or if a group with the same mvcId exists already.
     */
    @Nonnull
    MVCGroup createMVCGroup(@Nonnull Map<String, Object> args, @Nonnull String mvcType);

    /**
     * Instantiates an MVC group of the specified type with additional variables.<p>
     * MVC groups must be previously configured with the application's metadata
     * before they can be used. This registration process usually takes place automatically
     * at boot time. The type of the group can be normally found in the application's
     * configuration file.<p>
     * The <tt>args</tt> Map can contain any value that will be used in one of the following
     * scenarios <ul>
     * <li>The key matches a member definition; the value will be used as the instance of such member.</li>
     * <li>The key does not match a member definition, the value is assumed to be a property that can be set
     * on any MVC member of the group.</li>
     * </ul>
     * <p/>
     * For example, with the following entry available in {@code Config.groovy}
     * <p/>
     * <pre>
     * mvcGroups {
     *     foo {
     *         model      = 'com.acme.FooModel'
     *         view       = 'com.acme.FooView'
     *         controller = 'com.acme.FooController'
     *     }
     *     bar {
     *         model      = 'com.acme.FooModel'
     *         view       = 'com.acme.BarView'
     *         controller = 'com.acme.BarController'
     *     }
     * }
     * </pre>
     * <p/>
     * Notice that groups "foo" and "bar share the same model type, We can make them share the same model
     * instance by creating the groups in the following way:
     * <p/>
     * <pre>
     * MVCGroup fooGroup = createMVCGroup('foo')
     * MVCGroup barGroup = createMVCGroup('bar', model: fooGroup.model)
     * assert fooGroup.model == barGroup.model
     * </pre>
     *
     * @param mvcType the type of group to build.
     * @param args    any useful values that can be set as properties on each MVC member or that
     *                identify a member that can be shared with other groups.
     *
     * @return an MVCGroup instance of the desired type
     *
     * @throws griffon.exceptions.MVCGroupInstantiationException - if the type specified is not found in the application's
     *                                                           configuration or if a group with the same mvcId exists already.
     */
    @Nonnull
    MVCGroup createMVCGroup(@Nonnull String mvcType, @Nonnull Map<String, Object> args);

    /**
     * Instantiates an MVC group of the specified type with a particular name.<p>
     * MVC groups must be previously configured with the application's metadata
     * before they can be used. This registration process usually takes place automatically
     * at boot time. The type of the group can be normally found in the application's
     * configuration file.<p>
     * The <tt>args</tt> Map can contain any value that will be used in one of the following
     * scenarios <ul>
     * <li>The key matches a member definition; the value will be used as the instance of such member.</li>
     * <li>The key does not match a member definition, the value is assumed to be a property that can be set
     * on any MVC member of the group.</li>
     * </ul>
     * <p/>
     * For example, with the following entry available in {@code Config.groovy}
     * <p/>
     * <pre>
     * mvcGroups {
     *     foo {
     *         model      = 'com.acme.FooModel'
     *         view       = 'com.acme.FooView'
     *         controller = 'com.acme.FooController'
     *     }
     * }
     * </pre>
     * <p/>
     * We can create two instances of the same group that share the same model instance in the following way:
     * <p/>
     * <pre>
     * MVCGroup fooGroup1 = createMVCGroup('foo', 'foo1')
     * MVCGroup fooGroup2 = createMVCGroup('foo', 'foo2', model: fooGroup1.model)
     * assert fooGroup1.model == fooGroup2.model
     * </pre>
     * <p/>
     * MVC groups must have an unique name.
     *
     * @param args    any useful values that can be set as properties on each MVC member or that
     *                identify a member that can be shared with other groups.
     * @param mvcType the type of group to build.
     * @param mvcId   the name to assign to the built group.
     *
     * @return an MVCGroup instance of the desired type
     *
     * @throws griffon.exceptions.MVCGroupInstantiationException - if the type specified is not found in the application's
     *                                                           configuration or if a group with the same mvcId exists already.
     */
    @Nonnull
    MVCGroup createMVCGroup(@Nonnull Map<String, Object> args, @Nonnull String mvcType, @Nonnull String mvcId);

    /**
     * Instantiates an MVC group of the specified type with a particular name.<p>
     * MVC groups must be previously configured with the application's metadata
     * before they can be used. This registration process usually takes place automatically
     * at boot time. The type of the group can be normally found in the application's
     * configuration file.<p>
     * The <tt>args</tt> Map can contain any value that will be used in one of the following
     * scenarios <ul>
     * <li>The key matches a member definition; the value will be used as the instance of such member.</li>
     * <li>The key does not match a member definition, the value is assumed to be a property that can be set
     * on any MVC member of the group.</li>
     * </ul>
     * <p/>
     * For example, with the following entry available in {@code Config.groovy}
     * <p/>
     * <pre>
     * mvcGroups {
     *     foo {
     *         model      = 'com.acme.FooModel'
     *         view       = 'com.acme.FooView'
     *         controller = 'com.acme.FooController'
     *     }
     * }
     * </pre>
     * <p/>
     * We can create two instances of the same group that share the same model instance in the following way:
     * <p/>
     * <pre>
     * MVCGroup fooGroup1 = createMVCGroup('foo', 'foo1')
     * MVCGroup fooGroup2 = createMVCGroup('foo', 'foo2', model: fooGroup1.model)
     * assert fooGroup1.model == fooGroup2.model
     * </pre>
     * <p/>
     * MVC groups must have an unique name.
     *
     * @param mvcType the type of group to build.
     * @param mvcId   the name to assign to the built group.
     * @param args    any useful values that can be set as properties on each MVC member or that
     *                identify a member that can be shared with other groups.
     *
     * @return an MVCGroup instance of the desired type
     *
     * @throws griffon.exceptions.MVCGroupInstantiationException - if the type specified is not found in the application's
     *                                                           configuration or if a group with the same mvcId exists already.
     */
    @Nonnull
    MVCGroup createMVCGroup(@Nonnull String mvcType, @Nonnull String mvcId, @Nonnull Map<String, Object> args);

    /**
     * Instantiates an MVC group of the specified type.<p>
     * MVC groups must be previously configured with the application's metadata
     * before they can be used. This registration process usually takes place automatically
     * at boot time. The type of the group can be normally found in the application's
     * configuration file.<p>
     * For example, with the following entry available in {@code Config.groovy}
     * <p/>
     * <pre>
     * mvcGroups {
     *     foo {
     *         model      = 'com.acme.FooModel'
     *         view       = 'com.acme.FooView'
     *         controller = 'com.acme.FooController'
     *     }
     * }
     * </pre>
     * <p/>
     * An instance of the "foo" group can be created as follows
     * <p/>
     * <pre>
     * FooMVCGroup fooGroup = createMVCGroup(FooMVCGroup)
     * assert (fooGroup.controller instanceof FooController)
     * </pre>
     *
     * @param mvcType the type of group to build.
     *
     * @return an MVCGroup instance of the desired type
     *
     * @throws griffon.exceptions.MVCGroupInstantiationException - if the type specified is not found in the application's
     *                                                           configuration or if a group with the same mvcId exists already.
     * @since 2.11.0
     */
    @Nonnull
    <MVC extends TypedMVCGroup> MVC createMVCGroup(@Nonnull Class<? extends MVC> mvcType);

    /**
     * Instantiates an MVC group of the specified type with a particular name.<p>
     * MVC groups must be previously configured with the application's metadata
     * before they can be used. This registration process usually takes place automatically
     * at boot time. The type of the group can be normally found in the application's
     * configuration file.<p>
     * For example, with the following entry available in {@code Config.groovy}
     * <p/>
     * <pre>
     * mvcGroups {
     *     foo {
     *         model      = 'com.acme.FooModel'
     *         view       = 'com.acme.FooView'
     *         controller = 'com.acme.FooController'
     *     }
     * }
     * </pre>
     * <p/>
     * An instance of the "foo" group can be created as follows
     * <p/>
     * <pre>
     * FooMVCGroup fooGroup = createMVCGroup(FooMVCGroup, 'foo' + System.currentTimeMillis())
     * assert (fooGroup.controller instanceof FooController)
     * </pre>
     * <p/>
     * MVC groups must have an unique name.
     *
     * @param mvcType the type of group to build.
     * @param mvcId   the name to assign to the built group.
     *
     * @return an MVCGroup instance of the desired type
     *
     * @throws griffon.exceptions.MVCGroupInstantiationException - if the type specified is not found in the application's
     *                                                           configuration or if a group with the same mvcId exists already.
     * @since 2.11.0
     */
    @Nonnull
    <MVC extends TypedMVCGroup> MVC createMVCGroup(@Nonnull Class<? extends MVC> mvcType, @Nonnull String mvcId);

    /**
     * Instantiates an MVC group of the specified type with additional variables.<p>
     * MVC groups must be previously configured with the application's metadata
     * before they can be used. This registration process usually takes place automatically
     * at boot time. The type of the group can be normally found in the application's
     * configuration file.<p>
     * The <tt>args</tt> Map can contain any value that will be used in one of the following
     * scenarios <ul>
     * <li>The key matches a member definition; the value will be used as the instance of such member.</li>
     * <li>The key does not match a member definition, the value is assumed to be a property that can be set
     * on any MVC member of the group.</li>
     * </ul>
     * <p/>
     * For example, with the following entry available in {@code Config.groovy}
     * <p/>
     * <pre>
     * mvcGroups {
     *     foo {
     *         model      = 'com.acme.FooModel'
     *         view       = 'com.acme.FooView'
     *         controller = 'com.acme.FooController'
     *     }
     *     bar {
     *         model      = 'com.acme.FooModel'
     *         view       = 'com.acme.BarView'
     *         controller = 'com.acme.BarController'
     *     }
     * }
     * </pre>
     * <p/>
     * Notice that groups "foo" and "bar share the same model type, We can make them share the same model
     * instance by creating the groups in the following way:
     * <p/>
     * <pre>
     * FooMVCGroup fooGroup = createMVCGroup(FooMVCGroup)
     * BarMVCGroup barGroup = createMVCGroup(BarMVCGroup, model: fooGroup.model)
     * assert fooGroup.model == barGroup.model
     * </pre>
     *
     * @param args    any useful values that can be set as properties on each MVC member or that
     *                identify a member that can be shared with other groups.
     * @param mvcType the type of group to build.
     *
     * @return an MVCGroup instance of the desired type
     *
     * @throws griffon.exceptions.MVCGroupInstantiationException - if the type specified is not found in the application's
     *                                                           configuration or if a group with the same mvcId exists already.
     * @since 2.11.0
     */
    @Nonnull
    <MVC extends TypedMVCGroup> MVC createMVCGroup(@Nonnull Map<String, Object> args, @Nonnull Class<? extends MVC> mvcType);

    /**
     * Instantiates an MVC group of the specified type with additional variables.<p>
     * MVC groups must be previously configured with the application's metadata
     * before they can be used. This registration process usually takes place automatically
     * at boot time. The type of the group can be normally found in the application's
     * configuration file.<p>
     * The <tt>args</tt> Map can contain any value that will be used in one of the following
     * scenarios <ul>
     * <li>The key matches a member definition; the value will be used as the instance of such member.</li>
     * <li>The key does not match a member definition, the value is assumed to be a property that can be set
     * on any MVC member of the group.</li>
     * </ul>
     * <p/>
     * For example, with the following entry available in {@code Config.groovy}
     * <p/>
     * <pre>
     * mvcGroups {
     *     foo {
     *         model      = 'com.acme.FooModel'
     *         view       = 'com.acme.FooView'
     *         controller = 'com.acme.FooController'
     *     }
     *     bar {
     *         model      = 'com.acme.FooModel'
     *         view       = 'com.acme.BarView'
     *         controller = 'com.acme.BarController'
     *     }
     * }
     * </pre>
     * <p/>
     * Notice that groups "foo" and "bar share the same model type, We can make them share the same model
     * instance by creating the groups in the following way:
     * <p/>
     * <pre>
     * FooMVCGroup fooGroup = createMVCGroup(FooMVCGroup)
     * BarMVCGroup barGroup = createMVCGroup(BarMVCGroup, model: fooGroup.model)
     * assert fooGroup.model == barGroup.model
     * </pre>
     *
     * @param mvcType the type of group to build.
     * @param args    any useful values that can be set as properties on each MVC member or that
     *                identify a member that can be shared with other groups.
     *
     * @return an MVCGroup instance of the desired type
     *
     * @throws griffon.exceptions.MVCGroupInstantiationException - if the type specified is not found in the application's
     *                                                           configuration or if a group with the same mvcId exists already.
     * @since 2.11.0
     */
    @Nonnull
    <MVC extends TypedMVCGroup> MVC createMVCGroup(@Nonnull Class<? extends MVC> mvcType, @Nonnull Map<String, Object> args);

    /**
     * Instantiates an MVC group of the specified type with a particular name.<p>
     * MVC groups must be previously configured with the application's metadata
     * before they can be used. This registration process usually takes place automatically
     * at boot time. The type of the group can be normally found in the application's
     * configuration file.<p>
     * The <tt>args</tt> Map can contain any value that will be used in one of the following
     * scenarios <ul>
     * <li>The key matches a member definition; the value will be used as the instance of such member.</li>
     * <li>The key does not match a member definition, the value is assumed to be a property that can be set
     * on any MVC member of the group.</li>
     * </ul>
     * <p/>
     * For example, with the following entry available in {@code Config.groovy}
     * <p/>
     * <pre>
     * mvcGroups {
     *     foo {
     *         model      = 'com.acme.FooModel'
     *         view       = 'com.acme.FooView'
     *         controller = 'com.acme.FooController'
     *     }
     * }
     * </pre>
     * <p/>
     * We can create two instances of the same group that share the same model instance in the following way:
     * <p/>
     * <pre>
     * FooMVCGroup fooGroup1 = createMVCGroup(FooMVCGroup, 'foo1')
     * FooMVCGroup fooGroup2 = createMVCGroup(FooMVCGroup, 'foo2', model: fooGroup1.model)
     * assert fooGroup1.model == fooGroup2.model
     * </pre>
     * <p/>
     * MVC groups must have an unique name.
     *
     * @param args    any useful values that can be set as properties on each MVC member or that
     *                identify a member that can be shared with other groups.
     * @param mvcType the type of group to build.
     * @param mvcId   the name to assign to the built group.
     *
     * @return an MVCGroup instance of the desired type
     *
     * @throws griffon.exceptions.MVCGroupInstantiationException - if the type specified is not found in the application's
     *                                                           configuration or if a group with the same mvcId exists already.
     * @since 2.11.0
     */
    @Nonnull
    <MVC extends TypedMVCGroup> MVC createMVCGroup(@Nonnull Map<String, Object> args, @Nonnull Class<? extends MVC> mvcType, @Nonnull String mvcId);

    /**
     * Instantiates an MVC group of the specified type with a particular name.<p>
     * MVC groups must be previously configured with the application's metadata
     * before they can be used. This registration process usually takes place automatically
     * at boot time. The type of the group can be normally found in the application's
     * configuration file.<p>
     * The <tt>args</tt> Map can contain any value that will be used in one of the following
     * scenarios <ul>
     * <li>The key matches a member definition; the value will be used as the instance of such member.</li>
     * <li>The key does not match a member definition, the value is assumed to be a property that can be set
     * on any MVC member of the group.</li>
     * </ul>
     * <p/>
     * For example, with the following entry available in {@code Config.groovy}
     * <p/>
     * <pre>
     * mvcGroups {
     *     foo {
     *         model      = 'com.acme.FooModel'
     *         view       = 'com.acme.FooView'
     *         controller = 'com.acme.FooController'
     *     }
     * }
     * </pre>
     * <p/>
     * We can create two instances of the same group that share the same model instance in the following way:
     * <p/>
     * <pre>
     * FooMVCGroup fooGroup1 = createMVCGroup(FooMVCGroup, 'foo1')
     * FooMVCGroup fooGroup2 = createMVCGroup(FooMVCGroup, 'foo2', model: fooGroup1.model)
     * assert fooGroup1.model == fooGroup2.model
     * </pre>
     * <p/>
     * MVC groups must have an unique name.
     *
     * @param mvcType the type of group to build.
     * @param mvcId   the name to assign to the built group.
     * @param args    any useful values that can be set as properties on each MVC member or that
     *                identify a member that can be shared with other groups.
     *
     * @return an MVCGroup instance of the desired type
     *
     * @throws griffon.exceptions.MVCGroupInstantiationException - if the type specified is not found in the application's
     *                                                           configuration or if a group with the same mvcId exists already.
     * @since 2.11.0
     */
    @Nonnull
    <MVC extends TypedMVCGroup> MVC createMVCGroup(@Nonnull Class<? extends MVC> mvcType, @Nonnull String mvcId, @Nonnull Map<String, Object> args);

    /**
     * Instantiates an MVC group of the specified type returning only the MVC parts.<p>
     * MVC groups must be previously configured with the application's metadata
     * before they can be used. This registration process usually takes place automatically
     * at boot time. The type of the group can be normally found in the application's
     * configuration file.<p>
     * For example, with the following entry available in {@code Config.groovy}
     * <p/>
     * <pre>
     * mvcGroups {
     *     foo {
     *         model      = 'com.acme.FooModel'
     *         view       = 'com.acme.FooView'
     *         controller = 'com.acme.FooController'
     *     }
     * }
     * </pre>
     * <p/>
     * An instance of the "foo" group can be created as follows
     * <p/>
     * <pre>
     * def (m, v, c) = createMVC('foo')
     * assert (c instanceof FooController)
     * </pre>
     *
     * @param mvcType the type of group to build.
     *
     * @return a List with the canonical MVC members of the group
     *
     * @throws griffon.exceptions.MVCGroupInstantiationException - if the type specified is not found in the application's
     *                                                           configuration or if a group with the same mvcId exists already.
     */
    @Nonnull
    List<? extends GriffonMvcArtifact> createMVC(@Nonnull String mvcType);

    /**
     * Instantiates an MVC group of the specified type with additional variables.<p>
     * MVC groups must be previously configured with the application's metadata
     * before they can be used. This registration process usually takes place automatically
     * at boot time. The type of the group can be normally found in the application's
     * configuration file.<p>
     * The <tt>args</tt> Map can contain any value that will be used in one of the following
     * scenarios <ul>
     * <li>The key matches a member definition; the value will be used as the instance of such member.</li>
     * <li>The key does not match a member definition, the value is assumed to be a property that can be set
     * on any MVC member of the group.</li>
     * </ul>
     * <p/>
     * For example, with the following entry available in {@code Config.groovy}
     * <p/>
     * <pre>
     * mvcGroups {
     *     foo {
     *         model      = 'com.acme.FooModel'
     *         view       = 'com.acme.FooView'
     *         controller = 'com.acme.FooController'
     *     }
     *     bar {
     *         model      = 'com.acme.FooModel'
     *         view       = 'com.acme.BarView'
     *         controller = 'com.acme.BarController'
     *     }
     * }
     * </pre>
     * <p/>
     * Notice that groups "foo" and "bar share the same model type, We can make them share the same model
     * instance by creating the groups in the following way:
     * <p/>
     * <pre>
     * def (m1, v1, c1) = createMVC('foo')
     * def (m2, v2, c2) = createMVC('bar', model: m1)
     * assert fm1 == m2
     * </pre>
     *
     * @param args    any useful values that can be set as properties on each MVC member or that
     *                identify a member that can be shared with other groups.
     * @param mvcType the type of group to build.
     *
     * @return a List with the canonical MVC members of the group
     *
     * @throws griffon.exceptions.MVCGroupInstantiationException - if the type specified is not found in the application's
     *                                                           configuration or if a group with the same mvcId exists already.
     */
    @Nonnull
    List<? extends GriffonMvcArtifact> createMVC(@Nonnull Map<String, Object> args, @Nonnull String mvcType);

    /**
     * Instantiates an MVC group of the specified type with additional variables.<p>
     * MVC groups must be previously configured with the application's metadata
     * before they can be used. This registration process usually takes place automatically
     * at boot time. The type of the group can be normally found in the application's
     * configuration file.<p>
     * The <tt>args</tt> Map can contain any value that will be used in one of the following
     * scenarios <ul>
     * <li>The key matches a member definition; the value will be used as the instance of such member.</li>
     * <li>The key does not match a member definition, the value is assumed to be a property that can be set
     * on any MVC member of the group.</li>
     * </ul>
     * <p/>
     * For example, with the following entry available in {@code Config.groovy}
     * <p/>
     * <pre>
     * mvcGroups {
     *     foo {
     *         model      = 'com.acme.FooModel'
     *         view       = 'com.acme.FooView'
     *         controller = 'com.acme.FooController'
     *     }
     *     bar {
     *         model      = 'com.acme.FooModel'
     *         view       = 'com.acme.BarView'
     *         controller = 'com.acme.BarController'
     *     }
     * }
     * </pre>
     * <p/>
     * Notice that groups "foo" and "bar share the same model type, We can make them share the same model
     * instance by creating the groups in the following way:
     * <p/>
     * <pre>
     * def (m1, v1, c1) = createMVC('foo')
     * def (m2, v2, c2) = createMVC('bar', model: m1)
     * assert fm1 == m2
     * </pre>
     *
     * @param mvcType the type of group to build.
     * @param args    any useful values that can be set as properties on each MVC member or that
     *                identify a member that can be shared with other groups.
     *
     * @return a List with the canonical MVC members of the group
     *
     * @throws griffon.exceptions.MVCGroupInstantiationException - if the type specified is not found in the application's
     *                                                           configuration or if a group with the same mvcId exists already.
     */
    @Nonnull
    List<? extends GriffonMvcArtifact> createMVC(@Nonnull String mvcType, @Nonnull Map<String, Object> args);

    /**
     * Instantiates an MVC group of the specified type with a particular name.<p>
     * MVC groups must be previously configured with the application's metadata
     * before they can be used. This registration process usually takes place automatically
     * at boot time. The type of the group can be normally found in the application's
     * configuration file.<p>
     * For example, with the following entry available in {@code Config.groovy}
     * <p/>
     * <pre>
     * mvcGroups {
     *     foo {
     *         model      = 'com.acme.FooModel'
     *         view       = 'com.acme.FooView'
     *         controller = 'com.acme.FooController'
     *      }
     * }
     * </pre>
     * <p/>
     * An instance of the "foo" group can be created as follows
     * <p/>
     * <pre>
     * def (m, v, c) = createMVC('foo', 'foo' + System.currentTimeMillis())
     * assert (c instanceof FooController)
     * </pre>
     * <p/>
     * MVC groups must have an unique name.
     *
     * @param mvcType the type of group to build.
     * @param mvcId   the name to assign to the built group.
     *
     * @return a List with the canonical MVC members of the group
     *
     * @throws griffon.exceptions.MVCGroupInstantiationException - if the type specified is not found in the application's
     *                                                           configuration or if a group with the same mvcId exists already.
     */
    @Nonnull
    List<? extends GriffonMvcArtifact> createMVC(@Nonnull String mvcType, @Nonnull String mvcId);

    /**
     * Instantiates an MVC group of the specified type with a particular name.<p>
     * MVC groups must be previously configured with the application's metadata
     * before they can be used. This registration process usually takes place automatically
     * at boot time. The type of the group can be normally found in the application's
     * configuration file.<p>
     * The <tt>args</tt> Map can contain any value that will be used in one of the following
     * scenarios <ul>
     * <li>The key matches a member definition; the value will be used as the instance of such member.</li>
     * <li>The key does not match a member definition, the value is assumed to be a property that can be set
     * on any MVC member of the group.</li>
     * </ul>
     * <p/>
     * For example, with the following entry available in {@code Config.groovy}
     * <p/>
     * <pre>
     * mvcGroups {
     *     foo {
     *         model      = 'com.acme.FooModel'
     *         view       = 'com.acme.FooView'
     *         controller = 'com.acme.FooController'
     *     }
     * }
     * </pre>
     * <p/>
     * We can create two instances of the same group that share the same model instance in the following way:
     * <p/>
     * <pre>
     * def (m1, v1, c1) = createMVC('foo', 'foo1')
     * def (m2, v2, c2) = createMVC('foo', 'foo2', model: m1)
     * assert fm1 == m2
     * </pre>
     * <p/>
     * MVC groups must have an unique name.
     *
     * @param args    any useful values that can be set as properties on each MVC member or that
     *                identify a member that can be shared with other groups.
     * @param mvcType the type of group to build.
     * @param mvcId   the name to assign to the built group.
     *
     * @return a List with the canonical MVC members of the group
     *
     * @throws griffon.exceptions.MVCGroupInstantiationException - if the type specified is not found in the application's
     *                                                           configuration or if a group with the same mvcId exists already.
     */
    @Nonnull
    List<? extends GriffonMvcArtifact> createMVC(@Nonnull Map<String, Object> args, @Nonnull String mvcType, @Nonnull String mvcId);

    /**
     * Instantiates an MVC group of the specified type with a particular name.<p>
     * MVC groups must be previously configured with the application's metadata
     * before they can be used. This registration process usually takes place automatically
     * at boot time. The type of the group can be normally found in the application's
     * configuration file.<p>
     * The <tt>args</tt> Map can contain any value that will be used in one of the following
     * scenarios <ul>
     * <li>The key matches a member definition; the value will be used as the instance of such member.</li>
     * <li>The key does not match a member definition, the value is assumed to be a property that can be set
     * on any MVC member of the group.</li>
     * </ul>
     * <p/>
     * For example, with the following entry available in {@code Config.groovy}
     * <p/>
     * <pre>
     * mvcGroups {
     *     foo {
     *         model      = 'com.acme.FooModel'
     *         view       = 'com.acme.FooView'
     *         controller = 'com.acme.FooController'
     *     }
     * }
     * </pre>
     * <p/>
     * We can create two instances of the same group that share the same model instance in the following way:
     * <p/>
     * <pre>
     * def (m1, v1, c1) = createMVC('foo', 'foo1')
     * def (m2, v2, c2) = createMVC('foo', 'foo2', model: m1)
     * assert fm1 == m2
     * </pre>
     * <p/>
     * MVC groups must have an unique name.
     *
     * @param mvcType the type of group to build.
     * @param mvcId   the name to assign to the built group.
     * @param args    any useful values that can be set as properties on each MVC member or that
     *                identify a member that can be shared with other groups.
     *
     * @return a List with the canonical MVC members of the group
     *
     * @throws griffon.exceptions.MVCGroupInstantiationException - if the type specified is not found in the application's
     *                                                           configuration or if a group with the same mvcId exists already.
     */
    @Nonnull
    List<? extends GriffonMvcArtifact> createMVC(@Nonnull String mvcType, @Nonnull String mvcId, @Nonnull Map<String, Object> args);

    /**
     * Instantiates an MVC group of the specified type returning only the MVC parts.<p>
     * MVC groups must be previously configured with the application's metadata
     * before they can be used. This registration process usually takes place automatically
     * at boot time. The type of the group can be normally found in the application's
     * configuration file.<p>
     * For example, with the following entry available in {@code Config.groovy}
     * <p/>
     * <pre>
     * mvcGroups {
     *     foo {
     *         model      = 'com.acme.FooModel'
     *         view       = 'com.acme.FooView'
     *         controller = 'com.acme.FooController'
     *     }
     * }
     * </pre>
     * <p/>
     * An instance of the "foo" group can be created as follows
     * <p/>
     * <pre>
     * def (m, v, c) = createMVC(FooMVCGroup)
     * assert (c instanceof FooController)
     * </pre>
     *
     * @param mvcType the type of group to build.
     *
     * @return a List with the canonical MVC members of the group
     *
     * @throws griffon.exceptions.MVCGroupInstantiationException - if the type specified is not found in the application's
     *                                                           configuration or if a group with the same mvcId exists already.
     * @since 2.11.0
     */
    @Nonnull
    <MVC extends TypedMVCGroup> List<? extends GriffonMvcArtifact> createMVC(@Nonnull Class<? extends MVC> mvcType);

    /**
     * Instantiates an MVC group of the specified type with additional variables.<p>
     * MVC groups must be previously configured with the application's metadata
     * before they can be used. This registration process usually takes place automatically
     * at boot time. The type of the group can be normally found in the application's
     * configuration file.<p>
     * The <tt>args</tt> Map can contain any value that will be used in one of the following
     * scenarios <ul>
     * <li>The key matches a member definition; the value will be used as the instance of such member.</li>
     * <li>The key does not match a member definition, the value is assumed to be a property that can be set
     * on any MVC member of the group.</li>
     * </ul>
     * <p/>
     * For example, with the following entry available in {@code Config.groovy}
     * <p/>
     * <pre>
     * mvcGroups {
     *     foo {
     *         model      = 'com.acme.FooModel'
     *         view       = 'com.acme.FooView'
     *         controller = 'com.acme.FooController'
     *     }
     *     bar {
     *         model      = 'com.acme.FooModel'
     *         view       = 'com.acme.BarView'
     *         controller = 'com.acme.BarController'
     *     }
     * }
     * </pre>
     * <p/>
     * Notice that groups "foo" and "bar share the same model type, We can make them share the same model
     * instance by creating the groups in the following way:
     * <p/>
     * <pre>
     * def (m1, v1, c1) = createMVC(FooMVCGroup)
     * def (m2, v2, c2) = createMVC(BarMVCGroup, model: m1)
     * assert fm1 == m2
     * </pre>
     *
     * @param args    any useful values that can be set as properties on each MVC member or that
     *                identify a member that can be shared with other groups.
     * @param mvcType the type of group to build.
     *
     * @return a List with the canonical MVC members of the group
     *
     * @throws griffon.exceptions.MVCGroupInstantiationException - if the type specified is not found in the application's
     *                                                           configuration or if a group with the same mvcId exists already.
     * @since 2.11.0
     */
    @Nonnull
    <MVC extends TypedMVCGroup> List<? extends GriffonMvcArtifact> createMVC(@Nonnull Map<String, Object> args, @Nonnull Class<? extends MVC> mvcType);

    /**
     * Instantiates an MVC group of the specified type with additional variables.<p>
     * MVC groups must be previously configured with the application's metadata
     * before they can be used. This registration process usually takes place automatically
     * at boot time. The type of the group can be normally found in the application's
     * configuration file.<p>
     * The <tt>args</tt> Map can contain any value that will be used in one of the following
     * scenarios <ul>
     * <li>The key matches a member definition; the value will be used as the instance of such member.</li>
     * <li>The key does not match a member definition, the value is assumed to be a property that can be set
     * on any MVC member of the group.</li>
     * </ul>
     * <p/>
     * For example, with the following entry available in {@code Config.groovy}
     * <p/>
     * <pre>
     * mvcGroups {
     *     foo {
     *         model      = 'com.acme.FooModel'
     *         view       = 'com.acme.FooView'
     *         controller = 'com.acme.FooController'
     *     }
     *     bar {
     *         model      = 'com.acme.FooModel'
     *         view       = 'com.acme.BarView'
     *         controller = 'com.acme.BarController'
     *     }
     * }
     * </pre>
     * <p/>
     * Notice that groups "foo" and "bar share the same model type, We can make them share the same model
     * instance by creating the groups in the following way:
     * <p/>
     * <pre>
     * def (m1, v1, c1) = createMVC(FooMVCGroup)
     * def (m2, v2, c2) = createMVC(BarMVCGroup, model: m1)
     * assert fm1 == m2
     * </pre>
     *
     * @param mvcType the type of group to build.
     * @param args    any useful values that can be set as properties on each MVC member or that
     *                identify a member that can be shared with other groups.
     *
     * @return a List with the canonical MVC members of the group
     *
     * @throws griffon.exceptions.MVCGroupInstantiationException - if the type specified is not found in the application's
     *                                                           configuration or if a group with the same mvcId exists already.
     * @since 2.11.0
     */
    @Nonnull
    <MVC extends TypedMVCGroup> List<? extends GriffonMvcArtifact> createMVC(@Nonnull Class<? extends MVC> mvcType, @Nonnull Map<String, Object> args);

    /**
     * Instantiates an MVC group of the specified type with a particular name.<p>
     * MVC groups must be previously configured with the application's metadata
     * before they can be used. This registration process usually takes place automatically
     * at boot time. The type of the group can be normally found in the application's
     * configuration file.<p>
     * For example, with the following entry available in {@code Config.groovy}
     * <p/>
     * <pre>
     * mvcGroups {
     *     foo {
     *         model      = 'com.acme.FooModel'
     *         view       = 'com.acme.FooView'
     *         controller = 'com.acme.FooController'
     *      }
     * }
     * </pre>
     * <p/>
     * An instance of the "foo" group can be created as follows
     * <p/>
     * <pre>
     * def (m, v, c) = createMVC(FooMVCGroup, 'foo' + System.currentTimeMillis())
     * assert (c instanceof FooController)
     * </pre>
     * <p/>
     * MVC groups must have an unique name.
     *
     * @param mvcType the type of group to build.
     * @param mvcId   the name to assign to the built group.
     *
     * @return a List with the canonical MVC members of the group
     *
     * @throws griffon.exceptions.MVCGroupInstantiationException - if the type specified is not found in the application's
     *                                                           configuration or if a group with the same mvcId exists already.
     * @since 2.11.0
     */
    @Nonnull
    <MVC extends TypedMVCGroup> List<? extends GriffonMvcArtifact> createMVC(@Nonnull Class<? extends MVC> mvcType, @Nonnull String mvcId);

    /**
     * Instantiates an MVC group of the specified type with a particular name.<p>
     * MVC groups must be previously configured with the application's metadata
     * before they can be used. This registration process usually takes place automatically
     * at boot time. The type of the group can be normally found in the application's
     * configuration file.<p>
     * The <tt>args</tt> Map can contain any value that will be used in one of the following
     * scenarios <ul>
     * <li>The key matches a member definition; the value will be used as the instance of such member.</li>
     * <li>The key does not match a member definition, the value is assumed to be a property that can be set
     * on any MVC member of the group.</li>
     * </ul>
     * <p/>
     * For example, with the following entry available in {@code Config.groovy}
     * <p/>
     * <pre>
     * mvcGroups {
     *     foo {
     *         model      = 'com.acme.FooModel'
     *         view       = 'com.acme.FooView'
     *         controller = 'com.acme.FooController'
     *     }
     * }
     * </pre>
     * <p/>
     * We can create two instances of the same group that share the same model instance in the following way:
     * <p/>
     * <pre>
     * def (m1, v1, c1) = createMVC(FooMVCGroup, 'foo1')
     * def (m2, v2, c2) = createMVC(FooMVCGroup, 'foo2', model: m1)
     * assert fm1 == m2
     * </pre>
     * <p/>
     * MVC groups must have an unique name.
     *
     * @param args    any useful values that can be set as properties on each MVC member or that
     *                identify a member that can be shared with other groups.
     * @param mvcType the type of group to build.
     * @param mvcId   the name to assign to the built group.
     *
     * @return a List with the canonical MVC members of the group
     *
     * @throws griffon.exceptions.MVCGroupInstantiationException - if the type specified is not found in the application's
     *                                                           configuration or if a group with the same mvcId exists already.
     * @since 2.11.0
     */
    @Nonnull
    <MVC extends TypedMVCGroup> List<? extends GriffonMvcArtifact> createMVC(@Nonnull Map<String, Object> args, @Nonnull Class<? extends MVC> mvcType, @Nonnull String mvcId);

    /**
     * Instantiates an MVC group of the specified type with a particular name.<p>
     * MVC groups must be previously configured with the application's metadata
     * before they can be used. This registration process usually takes place automatically
     * at boot time. The type of the group can be normally found in the application's
     * configuration file.<p>
     * The <tt>args</tt> Map can contain any value that will be used in one of the following
     * scenarios <ul>
     * <li>The key matches a member definition; the value will be used as the instance of such member.</li>
     * <li>The key does not match a member definition, the value is assumed to be a property that can be set
     * on any MVC member of the group.</li>
     * </ul>
     * <p/>
     * For example, with the following entry available in {@code Config.groovy}
     * <p/>
     * <pre>
     * mvcGroups {
     *     foo {
     *         model      = 'com.acme.FooModel'
     *         view       = 'com.acme.FooView'
     *         controller = 'com.acme.FooController'
     *     }
     * }
     * </pre>
     * <p/>
     * We can create two instances of the same group that share the same model instance in the following way:
     * <p/>
     * <pre>
     * def (m1, v1, c1) = createMVC(FooMVCGroup, 'foo1')
     * def (m2, v2, c2) = createMVC(FooMVCGroup, 'foo2', model: m1)
     * assert fm1 == m2
     * </pre>
     * <p/>
     * MVC groups must have an unique name.
     *
     * @param mvcType the type of group to build.
     * @param mvcId   the name to assign to the built group.
     * @param args    any useful values that can be set as properties on each MVC member or that
     *                identify a member that can be shared with other groups.
     *
     * @return a List with the canonical MVC members of the group
     *
     * @throws griffon.exceptions.MVCGroupInstantiationException - if the type specified is not found in the application's
     *                                                           configuration or if a group with the same mvcId exists already.
     * @since 2.11.0
     */
    @Nonnull
    <MVC extends TypedMVCGroup> List<? extends GriffonMvcArtifact> createMVC(@Nonnull Class<? extends MVC> mvcType, @Nonnull String mvcId, @Nonnull Map<String, Object> args);

    /**
     * Destroys an MVC group identified by a particular name.<p>
     * <b>ATTENTION:</b> make sure to call the super implementation if you override this method
     * otherwise group references will not be kept up to date.
     *
     * @param mvcId the name of the group to destroy and dispose.
     */
    void destroyMVCGroup(@Nonnull String mvcId);

    /**
     * Instantiates an MVC group of the specified type then destroys it after it has been handled.<p>
     * <p>This method is of particular interest when working with short lived MVC groups such as
     * those used to build dialogs.<p/>
     * <p>MVC groups must be previously configured with the application's metadata
     * before they can be used. This registration process usually takes place automatically
     * at boot time. The type of the group can be normally found in the application's
     * configuration file.</p>
     * For example, with the following entry available in {@code Config.groovy}
     * <p/>
     * <pre>
     * mvcGroups {
     *     foo {
     *         model      = 'com.acme.FooModel'
     *         view       = 'com.acme.FooView'
     *         controller = 'com.acme.FooController'
     *    }
     * }
     * </pre>
     * <p/>
     * An instance of the "foo" group can be used as follows
     * <p/>
     * <pre>
     * withMVC("foo", new MVCFunction&lt;FooModel, FooView, FooController&gt;() {
     *     public void call(FooModel m, FooView v, FooController c) {
     *         m.setSomeProperty(someValue);
     *         c.invokeAnAction();
     *     }
     * });
     * </pre>
     *
     * @param mvcType the type of group to build.
     * @param handler a code block used to configure and manage the instantiated group
     *
     * @throws griffon.exceptions.MVCGroupInstantiationException - if the type specified is not found in the application's
     *                                                           configuration
     */
    <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull String mvcType, @Nonnull MVCFunction<M, V, C> handler);

    /**
     * Instantiates an MVC group of the specified type then destroys it after it has been handled.<p>
     * <p>This method is of particular interest when working with short lived MVC groups such as
     * those used to build dialogs.<p/>
     * <p>MVC groups must be previously configured with the application's metadata
     * before they can be used. This registration process usually takes place automatically
     * at boot time. The type of the group can be normally found in the application's
     * configuration file.</p>
     * For example, with the following entry available in {@code Config.groovy}
     * <p/>
     * <pre>
     * mvcGroups {
     *     foo {
     *         model      = 'com.acme.FooModel'
     *         view       = 'com.acme.FooView'
     *         controller = 'com.acme.FooController'
     *     }
     * }
     * </pre>
     * <p/>
     * An instance of the "foo" group can be used as follows
     * <p/>
     * <pre>
     * withMVC("foo", "foo1", new MVCFunction&lt;FooModel, FooView, FooController&gt;() {
     *     public void call(FooModel m, FooView v, FooController c) {
     *         m.setSomeProperty(someValue);
     *         c.invokeAnAction();
     *     }
     * });
     * </pre>
     * <p/>
     * MVC groups must have an unique name.
     *
     * @param mvcType the type of group to build.
     * @param mvcId   the name to assign to the built group.
     * @param handler a code block used to configure and manage the instantiated group
     *
     * @throws griffon.exceptions.MVCGroupInstantiationException - if the type specified is not found in the application's
     *                                                           configuration
     */
    <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull String mvcType, @Nonnull String mvcId, @Nonnull MVCFunction<M, V, C> handler);

    /**
     * Instantiates an MVC group of the specified type then destroys it after it has been handled.<p>
     * <p>This method is of particular interest when working with short lived MVC groups such as
     * those used to build dialogs.<p/>
     * <p>MVC groups must be previously configured with the application's metadata
     * before they can be used. This registration process usually takes place automatically
     * at boot time. The type of the group can be normally found in the application's
     * configuration file.</p>
     * The <tt>args</tt> Map can contain any value that will be used in one of the following
     * scenarios <ul>
     * <li>The key matches a member definition; the value will be used as the instance of such member.</li>
     * <li>The key does not match a member definition, the value is assumed to be a property that can be set
     * on any MVC member of the group.</li>
     * For example, with the following entry available in {@code Config.groovy}
     * <p/>
     * <pre>
     * mvcGroups {
     *     foo {
     *         model      = 'com.acme.FooModel'
     *         view       = 'com.acme.FooView'
     *         controller = 'com.acme.FooController'
     *     }
     * }
     * </pre>
     * <p/>
     * An instance of the "foo" group can be used as follows
     * <p/>
     * <pre>
     * Map<String, Object> map = ... // initialized elsewhere
     * withMVC("foo", "foo1", map, new MVCFunction&lt;FooModel, FooView, FooController&gt;() {
     *    public void call(FooModel m, FooView v, FooController c) {
     *        m.setSomeProperty(someValue);
     *        c.invokeAnAction();
     *    }
     * });
     * </pre>
     * <p/>
     * MVC groups must have an unique name.
     *
     * @param mvcType the type of group to build.
     * @param mvcId   the name to assign to the built group.
     * @param args    any useful values that can be set as properties on each MVC member or that
     *                identify a member that can be shared with other groups.
     * @param handler a code block used to configure and manage the instantiated group
     *
     * @throws griffon.exceptions.MVCGroupInstantiationException - if the type specified is not found in the application's
     *                                                           configuration
     */
    <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull String mvcType, @Nonnull String mvcId, @Nonnull Map<String, Object> args, @Nonnull MVCFunction<M, V, C> handler);

    /**
     * Instantiates an MVC group of the specified type then destroys it after it has been handled.<p>
     * <p>This method is of particular interest when working with short lived MVC groups such as
     * those used to build dialogs.<p/>
     * <p>MVC groups must be previously configured with the application's metadata
     * before they can be used. This registration process usually takes place automatically
     * at boot time. The type of the group can be normally found in the application's
     * configuration file.</p>
     * The <tt>args</tt> Map can contain any value that will be used in one of the following
     * scenarios <ul>
     * <li>The key matches a member definition; the value will be used as the instance of such member.</li>
     * <li>The key does not match a member definition, the value is assumed to be a property that can be set
     * on any MVC member of the group.</li>
     * For example, with the following entry available in {@code Config.groovy}
     * <p/>
     * <pre>
     * mvcGroups {
     *     foo {
     *         model      = 'com.acme.FooModel'
     *         view       = 'com.acme.FooView'
     *         controller = 'com.acme.FooController'
     *     }
     * }
     * </pre>
     * <p/>
     * An instance of the "foo" group can be used as follows
     * <p/>
     * <pre>
     * Map<String, Object> map = ... // initialized elsewhere
     * withMVC("foo", "foo1", map, new MVCFunction&lt;FooModel, FooView, FooController&gt;() {
     *    public void call(FooModel m, FooView v, FooController c) {
     *        m.setSomeProperty(someValue);
     *        c.invokeAnAction();
     *    }
     * });
     * </pre>
     * <p/>
     * MVC groups must have an unique name.
     *
     * @param args    any useful values that can be set as properties on each MVC member or that
     *                identify a member that can be shared with other groups.
     * @param mvcType the type of group to build.
     * @param mvcId   the name to assign to the built group.
     * @param handler a code block used to configure and manage the instantiated group
     *
     * @throws griffon.exceptions.MVCGroupInstantiationException - if the type specified is not found in the application's
     *                                                           configuration
     */
    <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull Map<String, Object> args, @Nonnull String mvcType, @Nonnull String mvcId, @Nonnull MVCFunction<M, V, C> handler);

    /**
     * Instantiates an MVC group of the specified type then destroys it after it has been handled.<p>
     * <p>This method is of particular interest when working with short lived MVC groups such as
     * those used to build dialogs.<p/>
     * <p>MVC groups must be previously configured with the application's metadata
     * before they can be used. This registration process usually takes place automatically
     * at boot time. The type of the group can be normally found in the application's
     * configuration file.</p>
     * The <tt>args</tt> Map can contain any value that will be used in one of the following
     * scenarios <ul>
     * <li>The key matches a member definition; the value will be used as the instance of such member.</li>
     * <li>The key does not match a member definition, the value is assumed to be a property that can be set
     * on any MVC member of the group.</li>
     * For example, with the following entry available in {@code Config.groovy}
     * <p/>
     * <pre>
     * mvcGroups {
     *     foo {
     *         model      = 'com.acme.FooModel'
     *         view       = 'com.acme.FooView'
     *         controller = 'com.acme.FooController'
     *    }
     * }
     * </pre>
     * <p/>
     * An instance of the "foo" group can be used as follows
     * <p/>
     * <pre>
     * Map<String, Object> map = ... // initialized elsewhere
     * withMVC("foo", map, new MVCFunction&lt;FooModel, FooView, FooController&gt;() {
     *    public void call(FooModel m, FooView v, FooController c) {
     *        m.setSomeProperty(someValue);
     *        c.invokeAnAction();
     *    }
     * });
     * </pre>
     *
     * @param mvcType the type of group to build.
     * @param args    any useful values that can be set as properties on each MVC member or that
     *                identify a member that can be shared with other groups.
     * @param handler a code block used to configure and manage the instantiated group
     *
     * @throws griffon.exceptions.MVCGroupInstantiationException - if the type specified is not found in the application's
     *                                                           configuration
     */
    <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull String mvcType, @Nonnull Map<String, Object> args, @Nonnull MVCFunction<M, V, C> handler);

    /**
     * Instantiates an MVC group of the specified type then destroys it after it has been handled.<p>
     * <p>This method is of particular interest when working with short lived MVC groups such as
     * those used to build dialogs.<p/>
     * <p>MVC groups must be previously configured with the application's metadata
     * before they can be used. This registration process usually takes place automatically
     * at boot time. The type of the group can be normally found in the application's
     * configuration file.</p>
     * The <tt>args</tt> Map can contain any value that will be used in one of the following
     * scenarios <ul>
     * <li>The key matches a member definition; the value will be used as the instance of such member.</li>
     * <li>The key does not match a member definition, the value is assumed to be a property that can be set
     * on any MVC member of the group.</li>
     * For example, with the following entry available in {@code Config.groovy}
     * <p/>
     * <pre>
     * mvcGroups {
     *     foo {
     *         model      = 'com.acme.FooModel'
     *         view       = 'com.acme.FooView'
     *         controller = 'com.acme.FooController'
     *    }
     * }
     * </pre>
     * <p/>
     * An instance of the "foo" group can be used as follows
     * <p/>
     * <pre>
     * Map<String, Object> map = ... // initialized elsewhere
     * withMVC("foo", map, new MVCFunction&lt;FooModel, FooView, FooController&gt;() {
     *    public void call(FooModel m, FooView v, FooController c) {
     *        m.setSomeProperty(someValue);
     *        c.invokeAnAction();
     *    }
     * });
     * </pre>
     *
     * @param args    any useful values that can be set as properties on each MVC member or that
     *                identify a member that can be shared with other groups.
     * @param mvcType the type of group to build.
     * @param handler a code block used to configure and manage the instantiated group
     *
     * @throws griffon.exceptions.MVCGroupInstantiationException - if the type specified is not found in the application's
     *                                                           configuration
     */
    <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull Map<String, Object> args, @Nonnull String mvcType, @Nonnull MVCFunction<M, V, C> handler);

    /**
     * Instantiates an MVC group of the specified type then destroys it after it has been handled.<p>
     * <p>This method is of particular interest when working with short lived MVC groups such as
     * those used to build dialogs.<p/>
     * <p>MVC groups must be previously configured with the application's metadata
     * before they can be used. This registration process usually takes place automatically
     * at boot time. The type of the group can be normally found in the application's
     * configuration file.</p>
     * For example, with the following entry available in {@code Config.groovy}
     * <p/>
     * <pre>
     * mvcGroups {
     *     foo {
     *         model      = 'com.acme.FooModel'
     *         view       = 'com.acme.FooView'
     *         controller = 'com.acme.FooController'
     *    }
     * }
     * </pre>
     * <p/>
     * An instance of the "foo" group can be used as follows
     * <p/>
     * <pre>
     * withMVC(FooMVCGroup.class, new MVCFunction&lt;FooModel, FooView, FooController&gt;() {
     *     public void call(FooModel m, FooView v, FooController c) {
     *         m.setSomeProperty(someValue);
     *         c.invokeAnAction();
     *     }
     * });
     * </pre>
     *
     * @param mvcType the type of group to build.
     * @param handler a code block used to configure and manage the instantiated group
     *
     * @throws griffon.exceptions.MVCGroupInstantiationException - if the type specified is not found in the application's
     *                                                           configuration
     * @since 2.11.0
     */
    <MVC extends TypedMVCGroup, M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull Class<? extends MVC> mvcType, @Nonnull MVCFunction<M, V, C> handler);

    /**
     * Instantiates an MVC group of the specified type then destroys it after it has been handled.<p>
     * <p>This method is of particular interest when working with short lived MVC groups such as
     * those used to build dialogs.<p/>
     * <p>MVC groups must be previously configured with the application's metadata
     * before they can be used. This registration process usually takes place automatically
     * at boot time. The type of the group can be normally found in the application's
     * configuration file.</p>
     * For example, with the following entry available in {@code Config.groovy}
     * <p/>
     * <pre>
     * mvcGroups {
     *     foo {
     *         model      = 'com.acme.FooModel'
     *         view       = 'com.acme.FooView'
     *         controller = 'com.acme.FooController'
     *     }
     * }
     * </pre>
     * <p/>
     * An instance of the "foo" group can be used as follows
     * <p/>
     * <pre>
     * withMVC(FooMVCGroup.class, "foo1", new MVCFunction&lt;FooModel, FooView, FooController&gt;() {
     *     public void call(FooModel m, FooView v, FooController c) {
     *         m.setSomeProperty(someValue);
     *         c.invokeAnAction();
     *     }
     * });
     * </pre>
     * <p/>
     * MVC groups must have an unique name.
     *
     * @param mvcType the type of group to build.
     * @param mvcId   the name to assign to the built group.
     * @param handler a code block used to configure and manage the instantiated group
     *
     * @throws griffon.exceptions.MVCGroupInstantiationException - if the type specified is not found in the application's
     *                                                           configuration
     * @since 2.11.0
     */
    <MVC extends TypedMVCGroup, M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull Class<? extends MVC> mvcType, @Nonnull String mvcId, @Nonnull MVCFunction<M, V, C> handler);

    /**
     * Instantiates an MVC group of the specified type then destroys it after it has been handled.<p>
     * <p>This method is of particular interest when working with short lived MVC groups such as
     * those used to build dialogs.<p/>
     * <p>MVC groups must be previously configured with the application's metadata
     * before they can be used. This registration process usually takes place automatically
     * at boot time. The type of the group can be normally found in the application's
     * configuration file.</p>
     * The <tt>args</tt> Map can contain any value that will be used in one of the following
     * scenarios <ul>
     * <li>The key matches a member definition; the value will be used as the instance of such member.</li>
     * <li>The key does not match a member definition, the value is assumed to be a property that can be set
     * on any MVC member of the group.</li>
     * For example, with the following entry available in {@code Config.groovy}
     * <p/>
     * <pre>
     * mvcGroups {
     *     foo {
     *         model      = 'com.acme.FooModel'
     *         view       = 'com.acme.FooView'
     *         controller = 'com.acme.FooController'
     *     }
     * }
     * </pre>
     * <p/>
     * An instance of the "foo" group can be used as follows
     * <p/>
     * <pre>
     * Map<String, Object> map = ... // initialized elsewhere
     * withMVC(FooMVCGroup.class, "foo1", map, new MVCFunction&lt;FooModel, FooView, FooController&gt;() {
     *    public void call(FooModel m, FooView v, FooController c) {
     *        m.setSomeProperty(someValue);
     *        c.invokeAnAction();
     *    }
     * });
     * </pre>
     * <p/>
     * MVC groups must have an unique name.
     *
     * @param mvcType the type of group to build.
     * @param mvcId   the name to assign to the built group.
     * @param args    any useful values that can be set as properties on each MVC member or that
     *                identify a member that can be shared with other groups.
     * @param handler a code block used to configure and manage the instantiated group
     *
     * @throws griffon.exceptions.MVCGroupInstantiationException - if the type specified is not found in the application's
     *                                                           configuration
     * @since 2.11.0
     */
    <MVC extends TypedMVCGroup, M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull Class<? extends MVC> mvcType, @Nonnull String mvcId, @Nonnull Map<String, Object> args, @Nonnull MVCFunction<M, V, C> handler);

    /**
     * Instantiates an MVC group of the specified type then destroys it after it has been handled.<p>
     * <p>This method is of particular interest when working with short lived MVC groups such as
     * those used to build dialogs.<p/>
     * <p>MVC groups must be previously configured with the application's metadata
     * before they can be used. This registration process usually takes place automatically
     * at boot time. The type of the group can be normally found in the application's
     * configuration file.</p>
     * The <tt>args</tt> Map can contain any value that will be used in one of the following
     * scenarios <ul>
     * <li>The key matches a member definition; the value will be used as the instance of such member.</li>
     * <li>The key does not match a member definition, the value is assumed to be a property that can be set
     * on any MVC member of the group.</li>
     * For example, with the following entry available in {@code Config.groovy}
     * <p/>
     * <pre>
     * mvcGroups {
     *     foo {
     *         model      = 'com.acme.FooModel'
     *         view       = 'com.acme.FooView'
     *         controller = 'com.acme.FooController'
     *     }
     * }
     * </pre>
     * <p/>
     * An instance of the "foo" group can be used as follows
     * <p/>
     * <pre>
     * Map<String, Object> map = ... // initialized elsewhere
     * withMVC(FooMVCGroup.class, "foo1", map, new MVCFunction&lt;FooModel, FooView, FooController&gt;() {
     *    public void call(FooModel m, FooView v, FooController c) {
     *        m.setSomeProperty(someValue);
     *        c.invokeAnAction();
     *    }
     * });
     * </pre>
     * <p/>
     * MVC groups must have an unique name.
     *
     * @param args    any useful values that can be set as properties on each MVC member or that
     *                identify a member that can be shared with other groups.
     * @param mvcType the type of group to build.
     * @param mvcId   the name to assign to the built group.
     * @param handler a code block used to configure and manage the instantiated group
     *
     * @throws griffon.exceptions.MVCGroupInstantiationException - if the type specified is not found in the application's
     *                                                           configuration
     * @since 2.11.0
     */
    <MVC extends TypedMVCGroup, M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull Map<String, Object> args, @Nonnull Class<? extends MVC> mvcType, @Nonnull String mvcId, @Nonnull MVCFunction<M, V, C> handler);

    /**
     * Instantiates an MVC group of the specified type then destroys it after it has been handled.<p>
     * <p>This method is of particular interest when working with short lived MVC groups such as
     * those used to build dialogs.<p/>
     * <p>MVC groups must be previously configured with the application's metadata
     * before they can be used. This registration process usually takes place automatically
     * at boot time. The type of the group can be normally found in the application's
     * configuration file.</p>
     * The <tt>args</tt> Map can contain any value that will be used in one of the following
     * scenarios <ul>
     * <li>The key matches a member definition; the value will be used as the instance of such member.</li>
     * <li>The key does not match a member definition, the value is assumed to be a property that can be set
     * on any MVC member of the group.</li>
     * For example, with the following entry available in {@code Config.groovy}
     * <p/>
     * <pre>
     * mvcGroups {
     *     foo {
     *         model      = 'com.acme.FooModel'
     *         view       = 'com.acme.FooView'
     *         controller = 'com.acme.FooController'
     *    }
     * }
     * </pre>
     * <p/>
     * An instance of the "foo" group can be used as follows
     * <p/>
     * <pre>
     * Map<String, Object> map = ... // initialized elsewhere
     * withMVC(FooMVCGroup.class, map, new MVCFunction&lt;FooModel, FooView, FooController&gt;() {
     *    public void call(FooModel m, FooView v, FooController c) {
     *        m.setSomeProperty(someValue);
     *        c.invokeAnAction();
     *    }
     * });
     * </pre>
     *
     * @param mvcType the type of group to build.
     * @param args    any useful values that can be set as properties on each MVC member or that
     *                identify a member that can be shared with other groups.
     * @param handler a code block used to configure and manage the instantiated group
     *
     * @throws griffon.exceptions.MVCGroupInstantiationException - if the type specified is not found in the application's
     *                                                           configuration
     * @since 2.11.0
     */
    <MVC extends TypedMVCGroup, M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull Class<? extends MVC> mvcType, @Nonnull Map<String, Object> args, @Nonnull MVCFunction<M, V, C> handler);

    /**
     * Instantiates an MVC group of the specified type then destroys it after it has been handled.<p>
     * <p>This method is of particular interest when working with short lived MVC groups such as
     * those used to build dialogs.<p/>
     * <p>MVC groups must be previously configured with the application's metadata
     * before they can be used. This registration process usually takes place automatically
     * at boot time. The type of the group can be normally found in the application's
     * configuration file.</p>
     * The <tt>args</tt> Map can contain any value that will be used in one of the following
     * scenarios <ul>
     * <li>The key matches a member definition; the value will be used as the instance of such member.</li>
     * <li>The key does not match a member definition, the value is assumed to be a property that can be set
     * on any MVC member of the group.</li>
     * For example, with the following entry available in {@code Config.groovy}
     * <p/>
     * <pre>
     * mvcGroups {
     *     foo {
     *         model      = 'com.acme.FooModel'
     *         view       = 'com.acme.FooView'
     *         controller = 'com.acme.FooController'
     *    }
     * }
     * </pre>
     * <p/>
     * An instance of the "foo" group can be used as follows
     * <p/>
     * <pre>
     * Map<String, Object> map = ... // initialized elsewhere
     * withMVC(FooMVCGroup.class, map, new MVCFunction&lt;FooModel, FooView, FooController&gt;() {
     *    public void call(FooModel m, FooView v, FooController c) {
     *        m.setSomeProperty(someValue);
     *        c.invokeAnAction();
     *    }
     * });
     * </pre>
     *
     * @param args    any useful values that can be set as properties on each MVC member or that
     *                identify a member that can be shared with other groups.
     * @param mvcType the type of group to build.
     * @param handler a code block used to configure and manage the instantiated group
     *
     * @throws griffon.exceptions.MVCGroupInstantiationException - if the type specified is not found in the application's
     *                                                           configuration
     * @since 2.11.0
     */
    <MVC extends TypedMVCGroup, M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVC(@Nonnull Map<String, Object> args, @Nonnull Class<? extends MVC> mvcType, @Nonnull MVCFunction<M, V, C> handler);

    /**
     * Instantiates an MVC group of the specified type then destroys it after it has been handled.<p>
     * <p>This method is of particular interest when working with short lived MVC groups such as
     * those used to build dialogs.<p/>
     * <p>MVC groups must be previously configured with the application's metadata
     * before they can be used. This registration process usually takes place automatically
     * at boot time. The type of the group can be normally found in the application's
     * configuration file.</p>
     * For example, with the following entry available in {@code Config.groovy}
     * <p/>
     * <pre>
     * mvcGroups {
     *     foo {
     *         model      = 'com.acme.FooModel'
     *         view       = 'com.acme.FooView'
     *         controller = 'com.acme.FooController'
     *    }
     * }
     * </pre>
     * <p/>
     * An instance of the "foo" group can be used as follows
     * <p/>
     * <pre>
     * withMVCGroup("foo", new MVCGroupFunction() {
     *     public void call(MVCGroup group) {
     *         group.getModel().setSomeProperty(someValue);
     *         group.getController().invokeAnAction();
     *     }
     * });
     * </pre>
     *
     * @param mvcType the type of group to build.
     * @param handler a code block used to configure and manage the instantiated group
     *
     * @throws griffon.exceptions.MVCGroupInstantiationException - if the type specified is not found in the application's
     *                                                           configuration
     * @since 2.1.0
     */
    void withMVCGroup(@Nonnull String mvcType, @Nonnull MVCGroupFunction handler);

    /**
     * Instantiates an MVC group of the specified type then destroys it after it has been handled.<p>
     * <p>This method is of particular interest when working with short lived MVC groups such as
     * those used to build dialogs.<p/>
     * <p>MVC groups must be previously configured with the application's metadata
     * before they can be used. This registration process usually takes place automatically
     * at boot time. The type of the group can be normally found in the application's
     * configuration file.</p>
     * For example, with the following entry available in {@code Config.groovy}
     * <p/>
     * <pre>
     * mvcGroups {
     *     foo {
     *         model      = 'com.acme.FooModel'
     *         view       = 'com.acme.FooView'
     *         controller = 'com.acme.FooController'
     *     }
     * }
     * </pre>
     * <p/>
     * An instance of the "foo" group can be used as follows
     * <p/>
     * <pre>
     * withMVCGroup("foo", "foo1", new MVCGroupFunction() {
     *     public void call(MVCGroup group) {
     *         group.getModel().setSomeProperty(someValue);
     *         group.getController().invokeAnAction();
     *     }
     * });
     * </pre>
     * <p/>
     * MVC groups must have an unique name.
     *
     * @param mvcType the type of group to build.
     * @param mvcId   the name to assign to the built group.
     * @param handler a code block used to configure and manage the instantiated group
     *
     * @throws griffon.exceptions.MVCGroupInstantiationException - if the type specified is not found in the application's
     *                                                           configuration
     * @since 2.1.0
     */
    void withMVCGroup(@Nonnull String mvcType, @Nonnull String mvcId, @Nonnull MVCGroupFunction handler);

    /**
     * Instantiates an MVC group of the specified type then destroys it after it has been handled.<p>
     * <p>This method is of particular interest when working with short lived MVC groups such as
     * those used to build dialogs.<p/>
     * <p>MVC groups must be previously configured with the application's metadata
     * before they can be used. This registration process usually takes place automatically
     * at boot time. The type of the group can be normally found in the application's
     * configuration file.</p>
     * The <tt>args</tt> Map can contain any value that will be used in one of the following
     * scenarios <ul>
     * <li>The key matches a member definition; the value will be used as the instance of such member.</li>
     * <li>The key does not match a member definition, the value is assumed to be a property that can be set
     * on any MVC member of the group.</li>
     * For example, with the following entry available in {@code Config.groovy}
     * <p/>
     * <pre>
     * mvcGroups {
     *     foo {
     *         model      = 'com.acme.FooModel'
     *         view       = 'com.acme.FooView'
     *         controller = 'com.acme.FooController'
     *     }
     * }
     * </pre>
     * <p/>
     * An instance of the "foo" group can be used as follows
     * <p/>
     * <pre>
     * Map<String, Object> map = ... // initialized elsewhere
     * withMVCGroup("foo", "foo1", map, new MVCGroupFunction() {
     *    public void call(MVCGroup group) {
     *        group.getModel().setSomeProperty(someValue);
     *        group.getController().invokeAnAction();
     *    }
     * });
     * </pre>
     * <p/>
     * MVC groups must have an unique name.
     *
     * @param mvcType the type of group to build.
     * @param mvcId   the name to assign to the built group.
     * @param args    any useful values that can be set as properties on each MVC member or that
     *                identify a member that can be shared with other groups.
     * @param handler a code block used to configure and manage the instantiated group
     *
     * @throws griffon.exceptions.MVCGroupInstantiationException - if the type specified is not found in the application's
     *                                                           configuration
     * @since 2.1.0
     */
    void withMVCGroup(@Nonnull String mvcType, @Nonnull String mvcId, @Nonnull Map<String, Object> args, @Nonnull MVCGroupFunction handler);

    /**
     * Instantiates an MVC group of the specified type then destroys it after it has been handled.<p>
     * <p>This method is of particular interest when working with short lived MVC groups such as
     * those used to build dialogs.<p/>
     * <p>MVC groups must be previously configured with the application's metadata
     * before they can be used. This registration process usually takes place automatically
     * at boot time. The type of the group can be normally found in the application's
     * configuration file.</p>
     * The <tt>args</tt> Map can contain any value that will be used in one of the following
     * scenarios <ul>
     * <li>The key matches a member definition; the value will be used as the instance of such member.</li>
     * <li>The key does not match a member definition, the value is assumed to be a property that can be set
     * on any MVC member of the group.</li>
     * For example, with the following entry available in {@code Config.groovy}
     * <p/>
     * <pre>
     * mvcGroups {
     *     foo {
     *         model      = 'com.acme.FooModel'
     *         view       = 'com.acme.FooView'
     *         controller = 'com.acme.FooController'
     *     }
     * }
     * </pre>
     * <p/>
     * An instance of the "foo" group can be used as follows
     * <p/>
     * <pre>
     * Map<String, Object> map = ... // initialized elsewhere
     * withMVCGroup("foo", "foo1", map, new MVCGroupFunction() {
     *    public void call(MVCGroup group) {
     *        group.getModel().setSomeProperty(someValue);
     *        group.getController().invokeAnAction();
     *    }
     * });
     * </pre>
     * <p/>
     * MVC groups must have an unique name.
     *
     * @param args    any useful values that can be set as properties on each MVC member or that
     *                identify a member that can be shared with other groups.
     * @param mvcType the type of group to build.
     * @param mvcId   the name to assign to the built group.
     * @param handler a code block used to configure and manage the instantiated group
     *
     * @throws griffon.exceptions.MVCGroupInstantiationException - if the type specified is not found in the application's
     *                                                           configuration
     * @since 2.1.0
     */
    void withMVCGroup(@Nonnull Map<String, Object> args, @Nonnull String mvcType, @Nonnull String mvcId, @Nonnull MVCGroupFunction handler);

    /**
     * Instantiates an MVC group of the specified type then destroys it after it has been handled.<p>
     * <p>This method is of particular interest when working with short lived MVC groups such as
     * those used to build dialogs.<p/>
     * <p>MVC groups must be previously configured with the application's metadata
     * before they can be used. This registration process usually takes place automatically
     * at boot time. The type of the group can be normally found in the application's
     * configuration file.</p>
     * The <tt>args</tt> Map can contain any value that will be used in one of the following
     * scenarios <ul>
     * <li>The key matches a member definition; the value will be used as the instance of such member.</li>
     * <li>The key does not match a member definition, the value is assumed to be a property that can be set
     * on any MVC member of the group.</li>
     * For example, with the following entry available in {@code Config.groovy}
     * <p/>
     * <pre>
     * mvcGroups {
     *     foo {
     *         model      = 'com.acme.FooModel'
     *         view       = 'com.acme.FooView'
     *         controller = 'com.acme.FooController'
     *    }
     * }
     * </pre>
     * <p/>
     * An instance of the "foo" group can be used as follows
     * <p/>
     * <pre>
     * Map<String, Object> map = ... // initialized elsewhere
     * withMVCGroup("foo", map, new MVCGroupFunction() {
     *    public void call(MVCGroup group) {
     *        group.getModel().setSomeProperty(someValue);
     *        group.getController().invokeAnAction();
     *    }
     * });
     * </pre>
     *
     * @param mvcType the type of group to build.
     * @param args    any useful values that can be set as properties on each MVC member or that
     *                identify a member that can be shared with other groups.
     * @param handler a code block used to configure and manage the instantiated group
     *
     * @throws griffon.exceptions.MVCGroupInstantiationException - if the type specified is not found in the application's
     *                                                           configuration
     * @since 2.1.0
     */
    void withMVCGroup(@Nonnull String mvcType, @Nonnull Map<String, Object> args, @Nonnull MVCGroupFunction handler);

    /**
     * Instantiates an MVC group of the specified type then destroys it after it has been handled.<p>
     * <p>This method is of particular interest when working with short lived MVC groups such as
     * those used to build dialogs.<p/>
     * <p>MVC groups must be previously configured with the application's metadata
     * before they can be used. This registration process usually takes place automatically
     * at boot time. The type of the group can be normally found in the application's
     * configuration file.</p>
     * The <tt>args</tt> Map can contain any value that will be used in one of the following
     * scenarios <ul>
     * <li>The key matches a member definition; the value will be used as the instance of such member.</li>
     * <li>The key does not match a member definition, the value is assumed to be a property that can be set
     * on any MVC member of the group.</li>
     * For example, with the following entry available in {@code Config.groovy}
     * <p/>
     * <pre>
     * mvcGroups {
     *     foo {
     *         model      = 'com.acme.FooModel'
     *         view       = 'com.acme.FooView'
     *         controller = 'com.acme.FooController'
     *    }
     * }
     * </pre>
     * <p/>
     * An instance of the "foo" group can be used as follows
     * <p/>
     * <pre>
     * Map<String, Object> map = ... // initialized elsewhere
     * withMVCGroup("foo", map, new MVCGroupFunction() {
     *    public void call(MVCGroup group) {
     *        group.getModel().setSomeProperty(someValue);
     *        group.getController().invokeAnAction();
     *    }
     * });
     * </pre>
     *
     * @param args    any useful values that can be set as properties on each MVC member or that
     *                identify a member that can be shared with other groups.
     * @param mvcType the type of group to build.
     * @param handler a code block used to configure and manage the instantiated group
     *
     * @throws griffon.exceptions.MVCGroupInstantiationException - if the type specified is not found in the application's
     *                                                           configuration
     * @since 2.1.0
     */
    void withMVCGroup(@Nonnull Map<String, Object> args, @Nonnull String mvcType, @Nonnull MVCGroupFunction handler);

    /**
     * Instantiates an MVC group of the specified type then destroys it after it has been handled.<p>
     * <p>This method is of particular interest when working with short lived MVC groups such as
     * those used to build dialogs.<p/>
     * <p>MVC groups must be previously configured with the application's metadata
     * before they can be used. This registration process usually takes place automatically
     * at boot time. The type of the group can be normally found in the application's
     * configuration file.</p>
     * For example, with the following entry available in {@code Config.groovy}
     * <p/>
     * <pre>
     * mvcGroups {
     *     foo {
     *         model      = 'com.acme.FooModel'
     *         view       = 'com.acme.FooView'
     *         controller = 'com.acme.FooController'
     *    }
     * }
     * </pre>
     * <p/>
     * An instance of the "foo" group can be used as follows
     * <p/>
     * <pre>
     * withMVCGroup(FooMVCGroup.class, new TypedMVCGroupFunction<>() {
     *     public void call(MVC group) {
     *         group.getModel().setSomeProperty(someValue);
     *         group.getController().invokeAnAction();
     *     }
     * });
     * </pre>
     *
     * @param mvcType the type of group to build.
     * @param handler a code block used to configure and manage the instantiated group
     *
     * @throws griffon.exceptions.MVCGroupInstantiationException - if the type specified is not found in the application's
     *                                                           configuration
     * @since 2.11.0
     */
    <MVC extends TypedMVCGroup> void withMVCGroup(@Nonnull Class<? extends MVC> mvcType, @Nonnull TypedMVCGroupFunction<MVC> handler);

    /**
     * Instantiates an MVC group of the specified type then destroys it after it has been handled.<p>
     * <p>This method is of particular interest when working with short lived MVC groups such as
     * those used to build dialogs.<p/>
     * <p>MVC groups must be previously configured with the application's metadata
     * before they can be used. This registration process usually takes place automatically
     * at boot time. The type of the group can be normally found in the application's
     * configuration file.</p>
     * For example, with the following entry available in {@code Config.groovy}
     * <p/>
     * <pre>
     * mvcGroups {
     *     foo {
     *         model      = 'com.acme.FooModel'
     *         view       = 'com.acme.FooView'
     *         controller = 'com.acme.FooController'
     *     }
     * }
     * </pre>
     * <p/>
     * An instance of the "foo" group can be used as follows
     * <p/>
     * <pre>
     * withMVCGroup(FooMVCGroup.class, "foo1", new TypedMVCGroupFunction<>() {
     *     public void call(MVC group) {
     *         group.getModel().setSomeProperty(someValue);
     *         group.getController().invokeAnAction();
     *     }
     * });
     * </pre>
     * <p/>
     * MVC groups must have an unique name.
     *
     * @param mvcType the type of group to build.
     * @param mvcId   the name to assign to the built group.
     * @param handler a code block used to configure and manage the instantiated group
     *
     * @throws griffon.exceptions.MVCGroupInstantiationException - if the type specified is not found in the application's
     *                                                           configuration
     * @since 2.11.0
     */
    <MVC extends TypedMVCGroup> void withMVCGroup(@Nonnull Class<? extends MVC> mvcType, @Nonnull String mvcId, @Nonnull TypedMVCGroupFunction<MVC> handler);

    /**
     * Instantiates an MVC group of the specified type then destroys it after it has been handled.<p>
     * <p>This method is of particular interest when working with short lived MVC groups such as
     * those used to build dialogs.<p/>
     * <p>MVC groups must be previously configured with the application's metadata
     * before they can be used. This registration process usually takes place automatically
     * at boot time. The type of the group can be normally found in the application's
     * configuration file.</p>
     * The <tt>args</tt> Map can contain any value that will be used in one of the following
     * scenarios <ul>
     * <li>The key matches a member definition; the value will be used as the instance of such member.</li>
     * <li>The key does not match a member definition, the value is assumed to be a property that can be set
     * on any MVC member of the group.</li>
     * For example, with the following entry available in {@code Config.groovy}
     * <p/>
     * <pre>
     * mvcGroups {
     *     foo {
     *         model      = 'com.acme.FooModel'
     *         view       = 'com.acme.FooView'
     *         controller = 'com.acme.FooController'
     *     }
     * }
     * </pre>
     * <p/>
     * An instance of the "foo" group can be used as follows
     * <p/>
     * <pre>
     * Map<String, Object> map = ... // initialized elsewhere
     * withMVCGroup(FooMVCGroup.class, "foo1", map, new TypedMVCGroupFunction<>() {
     *    public void call(MVC group) {
     *        group.getModel().setSomeProperty(someValue);
     *        group.getController().invokeAnAction();
     *    }
     * });
     * </pre>
     * <p/>
     * MVC groups must have an unique name.
     *
     * @param mvcType the type of group to build.
     * @param mvcId   the name to assign to the built group.
     * @param args    any useful values that can be set as properties on each MVC member or that
     *                identify a member that can be shared with other groups.
     * @param handler a code block used to configure and manage the instantiated group
     *
     * @throws griffon.exceptions.MVCGroupInstantiationException - if the type specified is not found in the application's
     *                                                           configuration
     * @since 2.11.0
     */
    <MVC extends TypedMVCGroup> void withMVCGroup(@Nonnull Class<? extends MVC> mvcType, @Nonnull String mvcId, @Nonnull Map<String, Object> args, @Nonnull TypedMVCGroupFunction<MVC> handler);

    /**
     * Instantiates an MVC group of the specified type then destroys it after it has been handled.<p>
     * <p>This method is of particular interest when working with short lived MVC groups such as
     * those used to build dialogs.<p/>
     * <p>MVC groups must be previously configured with the application's metadata
     * before they can be used. This registration process usually takes place automatically
     * at boot time. The type of the group can be normally found in the application's
     * configuration file.</p>
     * The <tt>args</tt> Map can contain any value that will be used in one of the following
     * scenarios <ul>
     * <li>The key matches a member definition; the value will be used as the instance of such member.</li>
     * <li>The key does not match a member definition, the value is assumed to be a property that can be set
     * on any MVC member of the group.</li>
     * For example, with the following entry available in {@code Config.groovy}
     * <p/>
     * <pre>
     * mvcGroups {
     *     foo {
     *         model      = 'com.acme.FooModel'
     *         view       = 'com.acme.FooView'
     *         controller = 'com.acme.FooController'
     *     }
     * }
     * </pre>
     * <p/>
     * An instance of the "foo" group can be used as follows
     * <p/>
     * <pre>
     * Map<String, Object> map = ... // initialized elsewhere
     * withMVCGroup(FooMVCGroup.class, "foo1", map, new TypedMVCGroupFunction<>() {
     *    public void call(MVC group) {
     *        group.getModel().setSomeProperty(someValue);
     *        group.getController().invokeAnAction();
     *    }
     * });
     * </pre>
     * <p/>
     * MVC groups must have an unique name.
     *
     * @param args    any useful values that can be set as properties on each MVC member or that
     *                identify a member that can be shared with other groups.
     * @param mvcType the type of group to build.
     * @param mvcId   the name to assign to the built group.
     * @param handler a code block used to configure and manage the instantiated group
     *
     * @throws griffon.exceptions.MVCGroupInstantiationException - if the type specified is not found in the application's
     *                                                           configuration
     * @since 2.11.0
     */
    <MVC extends TypedMVCGroup> void withMVCGroup(@Nonnull Map<String, Object> args, @Nonnull Class<? extends MVC> mvcType, @Nonnull String mvcId, @Nonnull TypedMVCGroupFunction<MVC> handler);

    /**
     * Instantiates an MVC group of the specified type then destroys it after it has been handled.<p>
     * <p>This method is of particular interest when working with short lived MVC groups such as
     * those used to build dialogs.<p/>
     * <p>MVC groups must be previously configured with the application's metadata
     * before they can be used. This registration process usually takes place automatically
     * at boot time. The type of the group can be normally found in the application's
     * configuration file.</p>
     * The <tt>args</tt> Map can contain any value that will be used in one of the following
     * scenarios <ul>
     * <li>The key matches a member definition; the value will be used as the instance of such member.</li>
     * <li>The key does not match a member definition, the value is assumed to be a property that can be set
     * on any MVC member of the group.</li>
     * For example, with the following entry available in {@code Config.groovy}
     * <p/>
     * <pre>
     * mvcGroups {
     *     foo {
     *         model      = 'com.acme.FooModel'
     *         view       = 'com.acme.FooView'
     *         controller = 'com.acme.FooController'
     *    }
     * }
     * </pre>
     * <p/>
     * An instance of the "foo" group can be used as follows
     * <p/>
     * <pre>
     * Map<String, Object> map = ... // initialized elsewhere
     * withMVCGroup(FooMVCGroup.class, map, new TypedMVCGroupFunction<>() {
     *    public void call(MVC group) {
     *        group.getModel().setSomeProperty(someValue);
     *        group.getController().invokeAnAction();
     *    }
     * });
     * </pre>
     *
     * @param mvcType the type of group to build.
     * @param args    any useful values that can be set as properties on each MVC member or that
     *                identify a member that can be shared with other groups.
     * @param handler a code block used to configure and manage the instantiated group
     *
     * @throws griffon.exceptions.MVCGroupInstantiationException - if the type specified is not found in the application's
     *                                                           configuration
     * @since 2.11.0
     */
    <MVC extends TypedMVCGroup> void withMVCGroup(@Nonnull Class<? extends MVC> mvcType, @Nonnull Map<String, Object> args, @Nonnull TypedMVCGroupFunction<MVC> handler);

    /**
     * Instantiates an MVC group of the specified type then destroys it after it has been handled.<p>
     * <p>This method is of particular interest when working with short lived MVC groups such as
     * those used to build dialogs.<p/>
     * <p>MVC groups must be previously configured with the application's metadata
     * before they can be used. This registration process usually takes place automatically
     * at boot time. The type of the group can be normally found in the application's
     * configuration file.</p>
     * The <tt>args</tt> Map can contain any value that will be used in one of the following
     * scenarios <ul>
     * <li>The key matches a member definition; the value will be used as the instance of such member.</li>
     * <li>The key does not match a member definition, the value is assumed to be a property that can be set
     * on any MVC member of the group.</li>
     * For example, with the following entry available in {@code Config.groovy}
     * <p/>
     * <pre>
     * mvcGroups {
     *     foo {
     *         model      = 'com.acme.FooModel'
     *         view       = 'com.acme.FooView'
     *         controller = 'com.acme.FooController'
     *    }
     * }
     * </pre>
     * <p/>
     * An instance of the "foo" group can be used as follows
     * <p/>
     * <pre>
     * Map<String, Object> map = ... // initialized elsewhere
     * withMVCGroup(FooMVCGroup.class, map, new TypedMVCGroupFunction<>() {
     *    public void call(MVC group) {
     *        group.getModel().setSomeProperty(someValue);
     *        group.getController().invokeAnAction();
     *    }
     * });
     * </pre>
     *
     * @param args    any useful values that can be set as properties on each MVC member or that
     *                identify a member that can be shared with other groups.
     * @param mvcType the type of group to build.
     * @param handler a code block used to configure and manage the instantiated group
     *
     * @throws griffon.exceptions.MVCGroupInstantiationException - if the type specified is not found in the application's
     *                                                           configuration
     * @since 2.11.0
     */
    <MVC extends TypedMVCGroup> void withMVCGroup(@Nonnull Map<String, Object> args, @Nonnull Class<? extends MVC> mvcType, @Nonnull TypedMVCGroupFunction<MVC> handler);
}
