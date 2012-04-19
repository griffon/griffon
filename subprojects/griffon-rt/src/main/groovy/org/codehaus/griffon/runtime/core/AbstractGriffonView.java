/*
 * Copyright 2010-2012 the original author or authors.
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

package org.codehaus.griffon.runtime.core;

import griffon.core.GriffonView;
import griffon.core.GriffonViewClass;
import griffon.util.Xml2Groovy;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import groovy.util.FactoryBuilderSupport;

import java.io.InputStream;
import java.util.Map;

import static griffon.util.GriffonNameUtils.isBlank;

/**
 * Base implementation of the GriffonView interface.
 *
 * @author Andres Almiray
 * @since 0.9.1
 */
public abstract class AbstractGriffonView extends AbstractGriffonMvcArtifact implements GriffonView {
    private FactoryBuilderSupport builder;

    protected String getArtifactType() {
        return GriffonViewClass.TYPE;
    }

    public FactoryBuilderSupport getBuilder() {
        return builder;
    }

    public void setBuilder(FactoryBuilderSupport builder) {
        this.builder = builder;
    }

    /**
     * Transforms an XML file into a Groovy script and evaluates it using a builder.</p>
     * <p>The file name matches the name of this class plus '.xml'. It must be found somewhere
     * in the classpath.</p>
     * <p>Every XML attribute that represents a string literal must be single quoted explicitly
     * otherwise the build will not be able to parse it. The following XML contents</p>
     * <pre><xmp>
     * <application title="app.config.application.title"
     *              pack="true">
     *     <actions>
     *         <action id="'clickAction'"
     *                 name="'Click'"
     *                 closure="{controller.click(it)}"/>
     *     </actions>
     *     <gridLayout cols="1" rows="3"/>
     *     <textField id="'input'" columns="20"
     *         text="bind('value', target: model)"/>
     *     <textField id="'output'" columns="20"
     *         text="bind{model.value}" editable="false"/>
     *     <button action="clickAction"/>
     * </application>
     * </xmp></pre>
     * <p/>
     * <p>are translated to</p>
     * <pre>
     * application(title: app.config.application.title, pack: true) {
     *   actions {
     *     action(id: 'clickAction', name: 'Click', closure: {controller.click(it)})
     *   }
     *   gridLayout(cols: 1, rows: 3)
     *   textField(id: 'input', text: bind('value', target: model), columns: 20)
     *   textField(id: 'output', text: bind{target.model}, columns: 20, editable: false)
     *   button(action: clickAction)
     * }
     * </pre>
     *
     * @param args a Map containing all relevant values that the build might need to build the
     *             View; this typically includes 'app', 'controller' and 'model'.
     * @since 0.9.2
     */
    public void buildViewFromXml(Map<String, Object> args) {
        buildViewFromXml(args, getClass().getName().replace('.', '/') + ".xml");
    }

    /**
     * Transforms an XML file into a Groovy script and evaluates it using a builder.</p>
     * <p>Every XML attribute that represents a string literal must be single quoted explicitly
     * otherwise the build will not be able to parse it. The following XML contents</p>
     * <pre><xmp>
     * <application title="app.config.application.title"
     *              pack="true">
     *     <actions>
     *         <action id="'clickAction'"
     *                 name="'Click'"
     *                 closure="{controller.click(it)}"/>
     *     </actions>
     *     <gridLayout cols="1" rows="3"/>
     *     <textField id="'input'" columns="20"
     *         text="bind('value', target: model)"/>
     *     <textField id="'output'" columns="20"
     *         text="bind{model.value}" editable="false"/>
     *     <button action="clickAction"/>
     * </application>
     * </xmp></pre>
     * <p/>
     * <p>are translated to</p>
     * <pre>
     * application(title: app.config.application.title, pack: true) {
     *   actions {
     *     action(id: 'clickAction', name: 'Click', closure: {controller.click(it)})
     *   }
     *   gridLayout(cols: 1, rows: 3)
     *   textField(id: 'input', text: bind('value', target: model), columns: 20)
     *   textField(id: 'output', text: bind{target.model}, columns: 20, editable: false)
     *   button(action: clickAction)
     * }
     * </pre>
     *
     * @param args     a Map containing all relevant values that the build might need to build the
     *                 View; this typically includes 'app', 'controller' and 'model'.
     * @param fileName the name of an XML file
     * @since 0.9.2
     */
    public void buildViewFromXml(Map<String, Object> args, String fileName) {
        if (isBlank(fileName)) {
            throw new IllegalArgumentException("Invalid file name for externalized view.");
        }

        InputStream is = getClass().getClassLoader().getResourceAsStream(fileName);
        if (is == null) {
            throw new IllegalArgumentException("Could not read file " + fileName);
        }

        String groovyScript = Xml2Groovy.getInstance().parse(is);
        if (isBlank(groovyScript)) {
            throw new IllegalArgumentException("File " + fileName + " is empty.");
        } else if (getLog().isTraceEnabled()) {
            getLog().trace("View script for " + fileName + "\n" + groovyScript);
        }

        final Script script = new GroovyShell().parse(groovyScript);
        script.setBinding(getBuilder());
        for (Map.Entry<String, ?> arg : args.entrySet()) {
            script.getBinding().setVariable(arg.getKey(), arg.getValue());
        }

        getApp().execInsideUISync(new Runnable() {
            public void run() {
                getBuilder().build(script);
            }
        });
    }
}
