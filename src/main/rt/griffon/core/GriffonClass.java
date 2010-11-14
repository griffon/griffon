/*
 * Copyright 2004-2010 the original author or authors.
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

import groovy.lang.MetaClass;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

/**
 * Represents any class in a Griffon application that is related to an artifact.</p>
 * While {@code GriffonArtifact} points to the real artifact instance, this class points to the meta
 * information that can be obtained from such artifact.
 * 
 * @author Steven Devijver (Grails 0.1)
 * @author Graeme Rocher (Grails 0.1)
 * @author Andres Almiray
 */
public interface GriffonClass {	
    Set<String> STANDARD_PROPERTIES = new TreeSet<String>(
        Arrays.asList("class", "metaClass", "app", "UIThread", "griffonClass", "log", "artifactType"));
	
    /**
     * Gets the initial value of the given property on the class.</p>
     *
     * @param name The name of the property
     * @return The initial value
     */
    Object getPropertyValue(String name);
    
    /**
     * Returns true if the class has the specified property.</p>
     *
     * @param name The name of the property
     * @return True if it does
     */
    boolean hasProperty(String name);

    /**
     * Creates a new instance of this class.</p>
     *
     * This method can be used as factory method in the Spring application context.</p>
     * @return a new instance of this class
     */
    Object newInstance();

    /**
     * Returns the logical name of the class in the application without the trailing convention part if applicable
     * and without the package name.</p>
     *
     * @return the logical name
     */
    String getName();

    /**
     * Returns the short name of the class without package prefix</p>
     *
     * @return The short name
     */
    String getShortName();

    /**
     * Returns the full name of the class in the application with the the trailing convention part and with
     * the package name.</p>
     *
     * @return the full name
     */
    String getFullName();

    /**
     * Returns the name of the class as a property name</p>
     *
     * @return The property name representation
     */
    String getPropertyName();

    /**
     * Returns the logical name of the class as a property name</p>
     *
     * @return The logical property name
     */
    String getLogicalPropertyName();

    /**
     * Returns the name of the property in natural terms (eg. 'lastName' becomes 'Last Name').<p>
	 *
     * @return The natural property name
     */
    String getNaturalName();

    /**
     * Returns the package name of the class.</p>
     *
     * @return the package name
     */
    String getPackageName();

    /**
     * Returns the actual clazz represented by the GriffonClass</p>
     *
     * @return the class
     */
    Class getClazz();
    
    /**
     * Returns the artifact type represented by the GriffonClass</p>
     *
     * @return the artifact type, i.e. "controller".
     */
    String getArtifactType();

    /**
     * Returns reference to the current application</p>
     *
     * @return the app
     */
    GriffonApplication getApp();

    /**
     * Gets the {@code MetaClass} of this GriffonClass.
     *
     * @return The MetaClass for this Griffon class
     */
    MetaClass getMetaClass();

    /**
     * @return Sample (reference) instance for this Griffon class
     */
    Object getReferenceInstance();

    /**
     * Obtains a property value for the given name and type.
     *
     * @param name The name
     * @param type The type
     *
     * @return  The property value
     */
    <T> T getPropertyValue(String name, Class<T> type);
}