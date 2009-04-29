/*
 * Copyright 2008 the original author or authors.
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
/**
 * Created by IntelliJ IDEA.
 *@author Danno.Ferrin
 * Date: Nov 7, 2007
 * Time: 2:54:00 PM
 */
package griffon.builder

import groovy.swing.SwingBuilder
import groovy.swing.SwingXBuilder

class UberBuilderConstructorsTest extends GroovyTestCase {

    public void testBuilders() {
        new UberBuilder(new SwingBuilder())
        new UberBuilder(new SwingXBuilder(), new SwingBuilder())
        new UberBuilder(jx:new SwingXBuilder(), j:new SwingBuilder())
        new UberBuilder(new SwingXBuilder(), j:new SwingBuilder())
    }

    public void testClasses() {
        new UberBuilder(SwingBuilder)
        new UberBuilder(SwingXBuilder, SwingBuilder)
        new UberBuilder(jx:SwingXBuilder, j:SwingBuilder)
        new UberBuilder(SwingXBuilder, j:SwingBuilder)
    }

}