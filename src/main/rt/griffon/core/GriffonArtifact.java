/*
 * Copyright 2010-2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package griffon.core;

import java.util.Map;
import java.util.List;

import groovy.lang.Closure;
import groovy.lang.MetaClass;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;

/**
 * Identifies an object as a Griffon artifact.<p>
 * Griffon artifacts are usually placed under the special "griffon-app" directory
 * that every application has. They are aslo grouped together in in a subdirectory that
 * clearly identifies their nature. For example "griffon-app/controllers" contains all
 * Controller artifacts.<p>
 * Implementing this interface for a custom artifact definition is highly recommended
 * but not enforced.
 *
 * @author Andres Almiray
 *
 * @since 0.9.1
 */
public interface GriffonArtifact {
    /**
     * Returns the reference to the current application.
     */
    GriffonApplication getApp();
    
    /**
     * Creates a new instance of the specified class and type.
     */
    Object newInstance(Class clazz, String type);

    /**
     * Gets the {@code MetaClass} of this artifact.<p>
     * It should delegate to its GriffonClass to get the real MetaClass.
     *
     * @return The MetaClass for this Griffon class
     */
    MetaClass getMetaClass();

    /**
     * Returns the <tt>GriffonClass</tt> associated with this artifact.
     */
    GriffonClass getGriffonClass();

    /**
     * True if the current thread is the UI thread.
     */
    boolean isUIThread();

    /**
     * Executes a code block asynchronously on the UI thread.
     */
    void execAsync(Runnable runnable);

    /**
     * Executes a code block synchronously on the UI thread.
     */
    void execSync(Runnable runnable);

    /**
     * Executes a code block outside of the UI thread.
     */
    void execOutside(Runnable runnable);

    /**
     * Executes a code block as a Future on an ExecutorService.
     */
    Future execFuture(ExecutorService executorService, Closure closure);

    /**
     * Executes a code block as a Future on a default ExecutorService.
     */
    Future execFuture(Closure closure);

    /**
     * Executes a code block as a Future on an ExecutorService.
     */
    Future execFuture(ExecutorService executorService, Callable callable);

    /**
     * Executes a code block as a Future on a default ExecutorService.
     */
    Future execFuture(Callable callable);
    
    /**
     * Returns a Logger instance suitable for this Artifact.<p>
     * The Logger is configured with the following prefix 'griffon.app.&lt;type&gt;'
     * where &lt;type&gt; stands for the artifact's type.<p>
     * Example: the Logger for class com.acme.SampleController will be configured for
     * 'griffon.app.controller.com.acme.SampleController'.
     *
     * @return a Logger instance associated with this artifact.
     *
     * @since 0.9.2
     */
    Logger getLog();

    /**
     * Instantiates an MVC group of the specified type.<p>
     * MVC Groups must be previously configured with the application's metadata
     * before they can be used. This registration process usually takes place automatically
     * at boot time. The type of the group can be normally found in the application's
     * configuration file.<p>
     * For example, with the following entry available in {@code Application.groovy}
     *
     * <pre>
     * mvcGroups {
     *     'foo' {
     *         model      = 'com.acme.FooModel'
     *         view       = 'com.acme.FooView'
     *         controller = 'com.acme.FooController'
     *     }
     * }
     * </pre>
     *
     * An instance of the "foo" group can be created as follows
     *
     * <pre>
     * Map<String, Object> fooGroup = buildMVCGroup('foo')
     * assert (fooGroup.controller instanceof FooController)
     * </pre>
     *
     * @param mvcType the type of group to build.
     * @return a Map with every member of the group keyed by type
     * @throws IllegalArgumentException if the type specified is not found in the application's
     * configuration.
     */
    Map<String, Object> buildMVCGroup(String mvcType);

    /**
     * Instantiates an MVC group of the specified type with a particular name.<p>
     * MVC Groups must be previously configured with the application's metadata
     * before they can be used. This registration process usually takes place automatically
     * at boot time. The type of the group can be normally found in the application's
     * configuration file.<p>
     * For example, with the following entry available in {@code Application.groovy}
     *
     * <pre>
     * mvcGroups {
     *     'foo' {
     *         model      = 'com.acme.FooModel'
     *         view       = 'com.acme.FooView'
     *         controller = 'com.acme.FooController'
     *     }
     * }
     * </pre>
     *
     * An instance of the "foo" group can be created as follows
     *
     * <pre>
     * Map<String, Object> fooGroup = buildMVCGroup('foo', 'foo' + System.currentTimeMillis())
     * assert (fooGroup.controller instanceof FooController)
     * </pre>
     *
     * MVC groups must have an unique name.
     *
     * @param mvcType the type of group to build.
     * @param mvcName the name to assign to the built group.
     * @return a Map with every member of the group keyed by type
     * @throws IllegalArgumentException if the type specified is not found in the application's
     * configuration.
     */
    Map<String, Object> buildMVCGroup(String mvcType, String mvcName);

    /**
     * Instantiates an MVC group of the specified type with additional variables.<p>
     * MVC Groups must be previously configured with the application's metadata
     * before they can be used. This registration process usually takes place automatically
     * at boot time. The type of the group can be normally found in the application's
     * configuration file.<p>
     * The <tt>args</tt> Map can contain any value that will be used in one of the following
     * scenarios <ul>
     * <li>The key matches a member definition; the value will be used as the instance of such member.</li>
     * <li>The key does not match a member definition, the value is assumed to be a property that can be set
     * on any MVC member of the group.</li>
     * </ul>
     *
     * For example, with the following entry available in {@code Application.groovy}
     *
     * <pre>
     * mvcGroups {
     *     'foo' {
     *         model      = 'com.acme.FooModel'
     *         view       = 'com.acme.FooView'
     *         controller = 'com.acme.FooController'
     *     }
     *     'bar' {
     *         model      = 'com.acme.FooModel'
     *         view       = 'com.acme.BarView'
     *         controller = 'com.acme.BarController'
     *     }
     * }
     * </pre>
     *
     * Notice that groups "foo" and "bar share the same model type, We can make them share the same model
     * instance by creating the groups in the following way:
     *
     * <pre>
     * Map<String, Object> fooGroup = buildMVCGroup('foo')
     * Map<String, Object> barGroup = buildMVCGroup('bar', model: fooGroup.model)
     * assert fooGroup.model == barGroup.model
     * </pre>
     *
     * @param args any useful values that can be set as properties on each MVC member or that
     * identify a member that can be shared with other groups.
     * @param mvcType the type of group to build.
     * @return a Map with every member of the group keyed by type
     * @throws IllegalArgumentException if the type specified is not found in the application's
     * configuration.
     */
    Map<String, Object> buildMVCGroup(Map<String, Object> args, String mvcType);

    /**
     * Instantiates an MVC group of the specified type with a particular name.<p>
     * MVC Groups must be previously configured with the application's metadata
     * before they can be used. This registration process usually takes place automatically
     * at boot time. The type of the group can be normally found in the application's
     * configuration file.<p>
     * The <tt>args</tt> Map can contain any value that will be used in one of the following
     * scenarios <ul>
     * <li>The key matches a member definition; the value will be used as the instance of such member.</li>
     * <li>The key does not match a member definition, the value is assumed to be a property that can be set
     * on any MVC member of the group.</li>
     * </ul>
     *
     * For example, with the following entry available in {@code Application.groovy}
     *
     * <pre>
     * mvcGroups {
     *     'foo' {
     *         model      = 'com.acme.FooModel'
     *         view       = 'com.acme.FooView'
     *         controller = 'com.acme.FooController'
     *     }
     * }
     * </pre>
     *
     * We can create two instances of the same group that share the same model instance in the following way:
     *
     * <pre>
     * Map<String, Object> fooGroup1 = buildMVCGroup('foo', 'foo1')
     * Map<String, Object> fooGroup2 = buildMVCGroup('bar', 'foo2', model: fooGroup1.model)
     * assert fooGroup1.model == fooGroup2.model
     * </pre>
     *
     * MVC groups must have an unique name.
     *
     * @param args any useful values that can be set as properties on each MVC member or that
     * identify a member that can be shared with other groups.
     * @param mvcType the type of group to build.
     * @param mvcName the name to assign to the built group.
     * @return a Map with every member of the group keyed by type
     * @throws IllegalArgumentException if the type specified is not found in the application's
     * configuration.
     */
    Map<String, Object> buildMVCGroup(Map<String, Object> args, String mvcType, String mvcName);

    /**
     * Instantiates an MVC group of the specified type returning only the MVC parts.<p>
     * MVC Groups must be previously configured with the application's metadata
     * before they can be used. This registration process usually takes place automatically
     * at boot time. The type of the group can be normally found in the application's
     * configuration file.<p>
     * For example, with the following entry available in {@code Application.groovy}
     *
     * <pre>
     * mvcGroups {
     *     'foo' {
     *         model      = 'com.acme.FooModel'
     *         view       = 'com.acme.FooView'
     *         controller = 'com.acme.FooController'
     *     }
     * }
     * </pre>
     *
     * An instance of the "foo" group can be created as follows
     *
     * <pre>
     * def (m, v, c) = createMVCGroup('foo')
     * assert (c instanceof FooController)
     * </pre>
     *
     * @param mvcType the type of group to build.
     * @return a List with the canonical MVC members of the group
     * @throws IllegalArgumentException if the type specified is not found in the application's
     * configuration.
     */
    List<? extends GriffonMvcArtifact> createMVCGroup(String mvcType);

    /**
     * Instantiates an MVC group of the specified type with additional variables.<p>
     * MVC Groups must be previously configured with the application's metadata
     * before they can be used. This registration process usually takes place automatically
     * at boot time. The type of the group can be normally found in the application's
     * configuration file.<p>
     * The <tt>args</tt> Map can contain any value that will be used in one of the following
     * scenarios <ul>
     * <li>The key matches a member definition; the value will be used as the instance of such member.</li>
     * <li>The key does not match a member definition, the value is assumed to be a property that can be set
     * on any MVC member of the group.</li>
     * </ul>
     *
     * For example, with the following entry available in {@code Application.groovy}
     *
     * <pre>
     * mvcGroups {
     *     'foo' {
     *         model      = 'com.acme.FooModel'
     *         view       = 'com.acme.FooView'
     *         controller = 'com.acme.FooController'
     *     }
     *     'bar' {
     *         model      = 'com.acme.FooModel'
     *         view       = 'com.acme.BarView'
     *         controller = 'com.acme.BarController'
     *     }
     * }
     * </pre>
     *
     * Notice that groups "foo" and "bar share the same model type, We can make them share the same model
     * instance by creating the groups in the following way:
     *
     * <pre>
     * def (m1, v1, c1) = createMVCGroup('foo')
     * def (m2, v2, c2) = createMVCGroup('bar', model: m1)
     * assert fm1 == m2
     * </pre>
     *
     * @param args any useful values that can be set as properties on each MVC member or that
     * identify a member that can be shared with other groups.
     * @param mvcType the type of group to build.
     * @return a List with the canonical MVC members of the group
     * @throws IllegalArgumentException if the type specified is not found in the application's
     * configuration.
     */
    List<? extends GriffonMvcArtifact> createMVCGroup(Map<String, Object> args, String mvcType);

    /**
     * Instantiates an MVC group of the specified type with additional variables.<p>
     * MVC Groups must be previously configured with the application's metadata
     * before they can be used. This registration process usually takes place automatically
     * at boot time. The type of the group can be normally found in the application's
     * configuration file.<p>
     * The <tt>args</tt> Map can contain any value that will be used in one of the following
     * scenarios <ul>
     * <li>The key matches a member definition; the value will be used as the instance of such member.</li>
     * <li>The key does not match a member definition, the value is assumed to be a property that can be set
     * on any MVC member of the group.</li>
     * </ul>
     *
     * For example, with the following entry available in {@code Application.groovy}
     *
     * <pre>
     * mvcGroups {
     *     'foo' {
     *         model      = 'com.acme.FooModel'
     *         view       = 'com.acme.FooView'
     *         controller = 'com.acme.FooController'
     *     }
     *     'bar' {
     *         model      = 'com.acme.FooModel'
     *         view       = 'com.acme.BarView'
     *         controller = 'com.acme.BarController'
     *     }
     * }
     * </pre>
     *
     * Notice that groups "foo" and "bar share the same model type, We can make them share the same model
     * instance by creating the groups in the following way:
     *
     * <pre>
     * def (m1, v1, c1) = createMVCGroup('foo')
     * def (m2, v2, c2) = createMVCGroup('bar', model: m1)
     * assert fm1 == m2
     * </pre>
     *
     * @param mvcType the type of group to build.
     * @param args any useful values that can be set as properties on each MVC member or that
     * identify a member that can be shared with other groups.
     * @return a List with the canonical MVC members of the group
     * @throws IllegalArgumentException if the type specified is not found in the application's
     * configuration.
     */
    List<? extends GriffonMvcArtifact> createMVCGroup(String mvcType, Map<String, Object> args);

    /**
     * Instantiates an MVC group of the specified type with a particular name.<p>
     * MVC Groups must be previously configured with the application's metadata
     * before they can be used. This registration process usually takes place automatically
     * at boot time. The type of the group can be normally found in the application's
     * configuration file.<p>
     * For example, with the following entry available in {@code Application.groovy}
     *
     * <pre>
     * mvcGroups {
     *     'foo' {
     *         model      = 'com.acme.FooModel'
     *         view       = 'com.acme.FooView'
     *         controller = 'com.acme.FooController'
     *      }
     * }
     * </pre>
     *
     * An instance of the "foo" group can be created as follows
     *
     * <pre>
     * def (m, v, c) = createMVCGroup('foo', 'foo' + System.currenttimeMillis())
     * assert (c instanceof FooController)
     * </pre>
     *
     * MVC groups must have an unique name.
     *
     * @param mvcType the type of group to build.
     * @param mvcName the name to assign to the built group.
     * @return a List with the canonical MVC members of the group
     * @throws IllegalArgumentException if the type specified is not found in the application's
     * configuration.
     */
    List<? extends GriffonMvcArtifact> createMVCGroup(String mvcType, String mvcName);

    /**
     * Instantiates an MVC group of the specified type with a particular name.<p>
     * MVC Groups must be previously configured with the application's metadata
     * before they can be used. This registration process usually takes place automatically
     * at boot time. The type of the group can be normally found in the application's
     * configuration file.<p>
     * The <tt>args</tt> Map can contain any value that will be used in one of the following
     * scenarios <ul>
     * <li>The key matches a member definition; the value will be used as the instance of such member.</li>
     * <li>The key does not match a member definition, the value is assumed to be a property that can be set
     * on any MVC member of the group.</li>
     * </ul>
     *
     * For example, with the following entry available in {@code Application.groovy}
     *
     * <pre>
     * mvcGroups {
     *     'foo' {
     *         model      = 'com.acme.FooModel'
     *         view       = 'com.acme.FooView'
     *         controller = 'com.acme.FooController'
     *     }
     * }
     * </pre>
     *
     * We can create two instances of the same group that share the same model instance in the following way:
     *
     * <pre>
     * def (m1, v1, c1) = createMVCGroup('foo', 'foo1')
     * def (m2, v2, c2) = createMVCGroup('foo', 'foo2', model: m1)
     * assert fm1 == m2
     * </pre>
     *
     * MVC groups must have an unique name.
     *
     * @param args any useful values that can be set as properties on each MVC member or that
     * identify a member that can be shared with other groups.
     * @param mvcType the type of group to build.
     * @param mvcName the name to assign to the built group.
     * @return a List with the canonical MVC members of the group
     * @throws IllegalArgumentException if the type specified is not found in the application's
     * configuration.
     */
    List<? extends GriffonMvcArtifact> createMVCGroup(Map<String, Object> args, String mvcType, String mvcName);

    /**
     * Instantiates an MVC group of the specified type with a particular name.<p>
     * MVC Groups must be previously configured with the application's metadata
     * before they can be used. This registration process usually takes place automatically
     * at boot time. The type of the group can be normally found in the application's
     * configuration file.<p>
     * The <tt>args</tt> Map can contain any value that will be used in one of the following
     * scenarios <ul>
     * <li>The key matches a member definition; the value will be used as the instance of such member.</li>
     * <li>The key does not match a member definition, the value is assumed to be a property that can be set
     * on any MVC member of the group.</li>
     * </ul>
     *
     * For example, with the following entry available in {@code Application.groovy}
     *
     * <pre>
     * mvcGroups {
     *     'foo' {
     *         model      = 'com.acme.FooModel'
     *         view       = 'com.acme.FooView'
     *         controller = 'com.acme.FooController'
     *     }
     * }
     * </pre>
     *
     * We can create two instances of the same group that share the same model instance in the following way:
     *
     * <pre>
     * def (m1, v1, c1) = createMVCGroup('foo', 'foo1')
     * def (m2, v2, c2) = createMVCGroup('foo', 'foo2', model: m1)
     * assert fm1 == m2
     * </pre>
     *
     * MVC groups must have an unique name.
     *
     * @param mvcType the type of group to build.
     * @param mvcName the name to assign to the built group.
     * @param args any useful values that can be set as properties on each MVC member or that
     * identify a member that can be shared with other groups.
     * @return a List with the canonical MVC members of the group
     * @throws IllegalArgumentException if the type specified is not found in the application's
     * configuration.
     */
    List<? extends GriffonMvcArtifact> createMVCGroup(String mvcType, String mvcName, Map<String, Object> args);

    /**
     * Destroys an MVC group identified by a particular name.<p>
     * <b>ATTENTION:</b> make sure to call the super implementation if you override this method
     * otherwise group references will not be kept up to date.
     *
     * @param mvcName the name of the group to destroy and dispose.
     */
    void destroyMVCGroup(String mvcName);

    /**
     * Instantiates an MVC group of the specified type then destroys it after it has been handled.<p>
     * <p>This method is of particular interest when working with short lived MVC groups such as
     * those used to build dialogs.<p/>
     * <p>MVC Groups must be previously configured with the application's metadata
     * before they can be used. This registration process usually takes place automatically
     * at boot time. The type of the group can be normally found in the application's
     * configuration file.</p>
     * For example, with the following entry available in {@code Application.groovy}
     *
     * <pre>
     * mvcGroups {
     *     'foo' {
     *         model      = 'com.acme.FooModel'
     *         view       = 'com.acme.FooView'
     *         controller = 'com.acme.FooController'
     *     }
     * }
     * </pre>
     *
     * An instance of the "foo" group can be used as follows
     *
     * <pre>
     * withMVCGroup('foo') { m, v, c->
     *     m.someProperty = someValue
     *     c.invokeAnAction()
     * }
     * </pre>
     *
     * @param mvcType the type of group to build.
     * @throws IllegalArgumentException if the type specified is not found in the application's
     * configuration
     *
     * @since 0.9.3
     */
    void withMVCGroup(String mvcType, Closure handler);

    /**
     * Instantiates an MVC group of the specified type then destroys it after it has been handled.<p>
     * <p>This method is of particular interest when working with short lived MVC groups such as
     * those used to build dialogs.<p/>
     * <p>MVC Groups must be previously configured with the application's metadata
     * before they can be used. This registration process usually takes place automatically
     * at boot time. The type of the group can be normally found in the application's
     * configuration file.</p>
     * For example, with the following entry available in {@code Application.groovy}
     *
     * <pre>
     * mvcGroups {
     *     'foo' {
     *         model      = 'com.acme.FooModel'
     *         view       = 'com.acme.FooView'
     *         controller = 'com.acme.FooController'
     *     }
     * }
     * </pre>
     *
     * An instance of the "foo" group can be used as follows
     *
     * <pre>
     * withMVCGroup('foo', 'foo1') { m, v, c->
     *     m.someProperty = someValue
     *     c.invokeAnAction()
     * }
     * </pre>
     *
     * MVC groups must have an unique name.
     *
     * @param mvcType the type of group to build.
     * @param mvcName the name to assign to the built group.
     * @throws IllegalArgumentException if the type specified is not found in the application's
     * configuration
     *
     * @since 0.9.3
     */
    void withMVCGroup(String mvcType, String mvcName, Closure handler);

    /**
     * Instantiates an MVC group of the specified type then destroys it after it has been handled.<p>
     * <p>This method is of particular interest when working with short lived MVC groups such as
     * those used to build dialogs.<p/>
     * <p>MVC Groups must be previously configured with the application's metadata
     * before they can be used. This registration process usually takes place automatically
     * at boot time. The type of the group can be normally found in the application's
     * configuration file.</p>
     * The <tt>args</tt> Map can contain any value that will be used in one of the following
     * scenarios <ul>
     * <li>The key matches a member definition; the value will be used as the instance of such member.</li>
     * <li>The key does not match a member definition, the value is assumed to be a property that can be set
     * on any MVC member of the group.</li>
     * For example, with the following entry available in {@code Application.groovy}
     *
     * <pre>
     * mvcGroups {
     *     'foo' {
     *         model      = 'com.acme.FooModel'
     *         view       = 'com.acme.FooView'
     *         controller = 'com.acme.FooController'
     *     }
     * }
     * </pre>
     *
     * An instance of the "foo" group can be used as follows
     *
     * <pre>
     * withMVCGroup('foo', 'foo1') { m, v, c->
     *     m.someProperty = someValue
     *     c.invokeAnAction()
     * }
     * </pre>
     *
     * MVC groups must have an unique name.
     *
     * @param mvcType the type of group to build.
     * @param mvcName the name to assign to the built group.
     * @param args any useful values that can be set as properties on each MVC member or that
     * identify a member that can be shared with other groups.
     * @throws IllegalArgumentException if the type specified is not found in the application's
     * configuration
     *
     * @since 0.9.3
     */
    void withMVCGroup(String mvcType, String mvcName, Map<String, Object> args, Closure handler);

    /**
     * Instantiates an MVC group of the specified type then destroys it after it has been handled.<p>
     * <p>This method is of particular interest when working with short lived MVC groups such as
     * those used to build dialogs.<p/>
     * <p>MVC Groups must be previously configured with the application's metadata
     * before they can be used. This registration process usually takes place automatically
     * at boot time. The type of the group can be normally found in the application's
     * configuration file.</p>
     * The <tt>args</tt> Map can contain any value that will be used in one of the following
     * scenarios <ul>
     * <li>The key matches a member definition; the value will be used as the instance of such member.</li>
     * <li>The key does not match a member definition, the value is assumed to be a property that can be set
     * on any MVC member of the group.</li>
     * For example, with the following entry available in {@code Application.groovy}
     *
     * <pre>
     * mvcGroups {
     *     'foo' {
     *         model      = 'com.acme.FooModel'
     *         view       = 'com.acme.FooView'
     *         controller = 'com.acme.FooController'
     *     }
     * }
     * </pre>
     *
     * An instance of the "foo" group can be used as follows
     *
     * <pre>
     * withMVCGroup('foo', 'foo1') { m, v, c->
     *     m.someProperty = someValue
     *     c.invokeAnAction()
     * }
     * </pre>
     *
     * @param mvcType the type of group to build.
     * @param args any useful values that can be set as properties on each MVC member or that
     * identify a member that can be shared with other groups.
     * @throws IllegalArgumentException if the type specified is not found in the application's
     * configuration
     *
     * @since 0.9.3
     */
    void withMVCGroup(String mvcType, Map<String, Object> args, Closure handler);

    /**
     * Instantiates an MVC group of the specified type then destroys it after it has been handled.<p>
     * <p>This method is of particular interest when working with short lived MVC groups such as
     * those used to build dialogs.<p/>
     * <p>MVC Groups must be previously configured with the application's metadata
     * before they can be used. This registration process usually takes place automatically
     * at boot time. The type of the group can be normally found in the application's
     * configuration file.</p>
     * For example, with the following entry available in {@code Application.groovy}
     *
     * <pre>
     * mvcGroups {
     *     'foo' {
     *         model      = 'com.acme.FooModel'
     *         view       = 'com.acme.FooView'
     *         controller = 'com.acme.FooController'
     *    }
     * }
     * </pre>
     *
     * An instance of the "foo" group can be used as follows
     *
     * <pre>
     * withMVCGroup("foo", new MVCClosure&lt;FooModel, FooView, FooController&gt;() {
     *     public void call(FooModel m, FooView v, FooController c) {
     *         m.setSomeProperty(someValue);
     *         c.invokeAnAction();
     *     }
     * });
     * </pre>
     *
     * @param mvcType the type of group to build.
     * @throws IllegalArgumentException if the type specified is not found in the application's
     * configuration
     *
     * @since 0.9.3
     */
    <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVCGroup(String mvcType, MVCClosure<M, V, C> handler);

    /**
     * Instantiates an MVC group of the specified type then destroys it after it has been handled.<p>
     * <p>This method is of particular interest when working with short lived MVC groups such as
     * those used to build dialogs.<p/>
     * <p>MVC Groups must be previously configured with the application's metadata
     * before they can be used. This registration process usually takes place automatically
     * at boot time. The type of the group can be normally found in the application's
     * configuration file.</p>
     * For example, with the following entry available in {@code Application.groovy}
     *
     * <pre>
     * mvcGroups {
     *     'foo' {
     *         model      = 'com.acme.FooModel'
     *         view       = 'com.acme.FooView'
     *         controller = 'com.acme.FooController'
     *     }
     * }
     * </pre>
     *
     * An instance of the "foo" group can be used as follows
     *
     * <pre>
     * withMVCGroup("foo", "foo1", new MVCClosure&lt;FooModel, FooView, FooController&gt;() {
     *     public void call(FooModel m, FooView v, FooController c) {
     *         m.setSomeProperty(someValue);
     *         c.invokeAnAction();
     *     }
     * });
     * </pre>
     *
     * MVC groups must have an unique name.
     *
     * @param mvcType the type of group to build.
     * @param mvcName the name to assign to the built group.
     * @throws IllegalArgumentException if the type specified is not found in the application's
     * configuration
     *
     * @since 0.9.3
     */
    <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVCGroup(String mvcType, String mvcName, MVCClosure<M, V, C> handler);

    /**
     * Instantiates an MVC group of the specified type then destroys it after it has been handled.<p>
     * <p>This method is of particular interest when working with short lived MVC groups such as
     * those used to build dialogs.<p/>
     * <p>MVC Groups must be previously configured with the application's metadata
     * before they can be used. This registration process usually takes place automatically
     * at boot time. The type of the group can be normally found in the application's
     * configuration file.</p>
     * The <tt>args</tt> Map can contain any value that will be used in one of the following
     * scenarios <ul>
     * <li>The key matches a member definition; the value will be used as the instance of such member.</li>
     * <li>The key does not match a member definition, the value is assumed to be a property that can be set
     * on any MVC member of the group.</li>
     * For example, with the following entry available in {@code Application.groovy}
     *
     * <pre>
     * mvcGroups {
     *     'foo' {
     *         model      = 'com.acme.FooModel'
     *         view       = 'com.acme.FooView'
     *         controller = 'com.acme.FooController'
     *     }
     * }
     * </pre>
     *
     * An instance of the "foo" group can be used as follows
     *
     * <pre>
     * Map<String, Object> map = ... // initialized elsewhere
     * withMVCGroup("foo", "foo1", map, new MVCClosure&lt;FooModel, FooView, FooController&gt;() {
     *    public void call(FooModel m, FooView v, FooController c) {
     *        m.setSomeProperty(someValue);
     *        c.invokeAnAction();
     *    }
     * });
     * </pre>
     *
     * MVC groups must have an unique name.
     *
     * @param mvcType the type of group to build.
     * @param mvcName the name to assign to the built group.
     * @param args any useful values that can be set as properties on each MVC member or that
     * identify a member that can be shared with other groups.
     * @throws IllegalArgumentException if the type specified is not found in the application's
     * configuration
     *
     * @since 0.9.3
     */
    <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVCGroup(String mvcType, String mvcName, Map<String, Object> args, MVCClosure<M, V, C> handler);

    /**
     * Instantiates an MVC group of the specified type then destroys it after it has been handled.<p>
     * <p>This method is of particular interest when working with short lived MVC groups such as
     * those used to build dialogs.<p/>
     * <p>MVC Groups must be previously configured with the application's metadata
     * before they can be used. This registration process usually takes place automatically
     * at boot time. The type of the group can be normally found in the application's
     * configuration file.</p>
     * The <tt>args</tt> Map can contain any value that will be used in one of the following
     * scenarios <ul>
     * <li>The key matches a member definition; the value will be used as the instance of such member.</li>
     * <li>The key does not match a member definition, the value is assumed to be a property that can be set
     * on any MVC member of the group.</li>
     * For example, with the following entry available in {@code Application.groovy}
     *
     * <pre>
     * mvcGroups {
     *     'foo' {
     *         model      = 'com.acme.FooModel'
     *         view       = 'com.acme.FooView'
     *         controller = 'com.acme.FooController'
     *    }
     * }
     * </pre>
     *
     * An instance of the "foo" group can be used as follows
     *
     * <pre>
     * Map<String, Object> map = ... // initialized elsewhere
     * withMVCGroup("foo", map, new MVCClosure&lt;FooModel, FooView, FooController&gt;() {
     *    public void call(FooModel m, FooView v, FooController c) {
     *        m.setSomeProperty(someValue);
     *        c.invokeAnAction();
     *    }
     * });
     * </pre>
     *
     * @param mvcType the type of group to build.
     * @param args any useful values that can be set as properties on each MVC member or that
     * identify a member that can be shared with other groups.
     * @throws IllegalArgumentException if the type specified is not found in the application's
     * configuration
     *
     * @since 0.9.3
     */
    <M extends GriffonModel, V extends GriffonView, C extends GriffonController> void withMVCGroup(String mvcType, Map<String, Object> args, MVCClosure<M, V, C> handler);
}
