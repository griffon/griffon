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
package griffon.gui.test

import griffon.gui.GUIBuilder
import groovy.swing.SwingBuilder
import groovy.swing.SwingXBuilder
import groovy.swing.factory.BeanFactory
import javax.swing.JPanel

/**
 * Created by IntelliJ IDEA.
 *@author Danno
 * Date: Nov 7, 2007
 * Time: 4:11:26 PM
 */
public class GUIBuilderTest extends GroovyTestCase {

    public void testConstructor() {
        new GUIBuilder()
        new GUIBuilder('swingx')
        // GraphicsBuilder looping issue
        //new GUIBuilder('gfx', 'swingx')
        new GUIBuilder()
    }

    public void testSingle() {
        def gb = new GUIBuilder('swing')
        gb.panel(id:'p1') {
            assert currentBuilder instanceof SwingBuilder
        }
        assert gb.p1

        gb = new GUIBuilder(j:'swing')
        gb.jpanel(id:'p2') {
            assert currentBuilder instanceof SwingBuilder
        }
        assert gb.p2

        gb = new GUIBuilder(spanel: new BeanFactory(JPanel))
        def p3 = gb.spanel() { // nothing is defining id: in this instance
            assert currentBuilder instanceof GUIBuilder
        }
        assert p3
    }

    public void testNestedMono() {
        def gb = new GUIBuilder('swing')
        gb.panel(id:'p1') {
            assert currentBuilder instanceof SwingBuilder
            panel(id:'p11') {
                assert currentBuilder instanceof SwingBuilder
            }
        }
        assert gb.p1
        assert gb.p11

        gb = new GUIBuilder(j:'swing')
        gb.jpanel(id:'p2') {
            assert currentBuilder instanceof SwingBuilder
            jpanel(id:'p21') {
                assert currentBuilder instanceof SwingBuilder
            }
        }
        assert gb.p2
        assert gb.p21

        gb = new GUIBuilder(spanel: new BeanFactory(JPanel))
        def p31
        def p3 = gb.spanel() { // nothing is defining id: in this instance
            assert currentBuilder instanceof GUIBuilder
            p31 = spanel() {
                assert currentBuilder instanceof GUIBuilder
            }
        }
        assert p3
        assert p31
    }

    public void testNestedPoly() {
        def gb = new GUIBuilder('swingx', 'swing')
        gb.panel(id:'p1') {
            assert currentBuilder instanceof SwingXBuilder
            checkBox(id:'p11') {
                assert currentBuilder instanceof SwingXBuilder
            }
        }
        assert gb.p1
        assert gb.p11

        gb = new GUIBuilder('swing', 'swingx')
        gb.panel(id:'p2') {
            assert !(currentBuilder instanceof SwingXBuilder)
            checkBox(id:'p21') {
                assert !(currentBuilder instanceof SwingXBuilder)
            }
        }
        assert gb.p2
        assert gb.p21

        gb = new GUIBuilder(j:'swing', jx:'swingx')
        gb.jxpanel(id:'p3') {
            assert (currentBuilder instanceof SwingXBuilder)
            jpanel(id:'p31') {
                assert !(currentBuilder instanceof SwingXBuilder)
            }
        }
        assert gb.p3
        assert gb.p31

        gb.jpanel(id:'p4') {
            assert !(currentBuilder instanceof SwingXBuilder)
            jxpanel(id:'p41') {
                assert (currentBuilder instanceof SwingXBuilder)
            }
        }
        assert gb.p4
        assert gb.p41

        //todo text with custom factory?
    }

    public void testResources() {
        // broken for now for some reason?
        def gb = new GUIBuilder('swing')

        assert gb.resources.getString('foo') == 'bar'
        gb.frame(id:'f')  {
            // note the class here is actually
            // griffon.gui.test.GUIBuilderTest$_testResources_closureX
            // we are testing closure unwrapping
            assert resources.getString('foo') == 'bar'
        }

        shouldFail(MissingResourceException) {
            ExteriorClass.test(gb)
        }
    }
}

class ExteriorClass {
    // must not have a griffon.gui.ExteriorClass.properties file
    public static void test(GUIBuilder gb) {
        gb.resources
    }
}

