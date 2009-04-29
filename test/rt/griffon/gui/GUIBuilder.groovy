/*
 * Copyright 2007-2008 the original author or authors.
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

/*
 * Created by IntelliJ IDEA.
 *@author Danno.Ferrin
 * Date: Nov 7, 2007
 * Time: 3:55:26 PM
 */
package griffon.gui

import griffon.builder.UberBuilder
import groovy.swing.SwingBuilder
import groovy.swing.SwingXBuilder
import org.codehaus.groovy.reflection.ReflectionUtils

//import groovy.swing.j2d.GraphicsBuilder
class GUIBuilder extends UberBuilder {

    protected static final Set<String> builderPackages = new HashSet<String>();
    static {
        // TODO also either dynamically add pacakges of builders or match builders themselves as excludes 
        builderPackages.add("org.codehaus.groovy.runtime.metaclass");
        builderPackages.add("griffon.builder");
        builderPackages.add("groovy.util");
        builderPackages.add("groovy.swing");
        builderPackages.add("griffon.gui");
    }

    public GUIBuilder() {
        super(['default'] as Object[])
    }

    public GUIBuilder(Object[] builders) {
        super(builders)
    }

    protected Object loadBuilderLookups() {
        // looping proble with graphisBuidler.getProperty
        //this.@builderLookup['default'] = ['swing', 'swingx', 'gfx', [j:'swing', jx:'swingx']] as Object[]
        builderLookup['default'] = ['swing', 'swingx', [j:'swing', jx:'swingx']] as Object[]
        this.@builderLookup.swing = SwingBuilder
        this.@builderLookup.SwingBuilder = SwingBuilder
        this.@builderLookup.swingx = SwingXBuilder
        this.@builderLookup.SwingXBuilder = SwingXBuilder
        // looping proble with graphisBuidler.getProperty
//        this.@builderLookup.gfx = GraphicsBuilder
//        this.@builderLookup.GraphicsBuilder = GraphicsBuilder

        registerExplicitProperty(
            'resources',
            {->
                Class callingClass = ReflectionUtils.getCallingClass(0, builderPackages)
                ResourceBundle.getBundle(
                    callingClass.name.split('\\$')[0].replace('.', '/'),
                    Locale.getDefault(),
                    callingClass.getClassLoader())
            },
            null);
    }

}