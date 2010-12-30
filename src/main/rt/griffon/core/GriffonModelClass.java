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

/**
 * Represents a Model class in Griffon.<p>
 *
 * @author Andres Almiray
 *
 * @since 0.9.1
 */
public interface GriffonModelClass extends GriffonClass {
    /** "model" */
    String TYPE = "model";
    /** "Model" */
    String TRAILING = "Model";

    /**
     * Matches all public properties that are not event handlers nor
     * have a Closure as their value.<p>
     */
    String[] getPropertyNames();

    /**
     * Matches all public methods and closure properties whose name
     * matches the event handler convention, i.e, starts with "on" and
     * is followed by at least one uppercase character.<p>
     */
    String[] getEventNames();
}