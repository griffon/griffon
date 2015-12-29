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
package griffon.builder.lanterna;

import com.googlecode.lanterna.gui.Component;
import com.googlecode.lanterna.gui.component.Panel;
import com.googlecode.lanterna.gui.layout.BorderLayout;
import com.googlecode.lanterna.gui.layout.HorisontalLayout;
import com.googlecode.lanterna.gui.layout.VerticalLayout;
import griffon.builder.lanterna.factory.ActionFactory;
import griffon.builder.lanterna.factory.ActionListBoxFactory;
import griffon.builder.lanterna.factory.ApplicationFactory;
import griffon.builder.lanterna.factory.BoxFactory;
import griffon.builder.lanterna.factory.ButtonFactory;
import griffon.builder.lanterna.factory.CheckBoxFactory;
import griffon.builder.lanterna.factory.CollectionFactory;
import griffon.builder.lanterna.factory.ComponentFactory;
import griffon.builder.lanterna.factory.EmptySpaceFactory;
import griffon.builder.lanterna.factory.LabelFactory;
import griffon.builder.lanterna.factory.LayoutFactory;
import griffon.builder.lanterna.factory.PanelFactory;
import griffon.builder.lanterna.factory.PasswordBoxFactory;
import griffon.builder.lanterna.factory.ProgressBarFactory;
import griffon.builder.lanterna.factory.TableFactory;
import griffon.builder.lanterna.factory.TextAreaFactory;
import griffon.builder.lanterna.factory.TextBoxFactory;
import griffon.inject.DependsOn;
import groovy.lang.Closure;
import groovy.util.Factory;
import groovy.util.FactoryBuilderSupport;
import org.codehaus.griffon.runtime.groovy.view.AbstractBuilderCustomizer;

import javax.inject.Named;
import java.util.LinkedHashMap;
import java.util.Map;

import static griffon.util.CollectionUtils.newList;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
@Named("lanterna")
@DependsOn({"core"})
@SuppressWarnings("rawtypes")
public class LanternaBuilderCustomizer extends AbstractBuilderCustomizer {
    @SuppressWarnings("unchecked")
    public LanternaBuilderCustomizer() {
        Map<String, Factory> factories = new LinkedHashMap<>();
        factories.put("action", new ActionFactory());
        factories.put("actionListBox", new ActionListBoxFactory());
        factories.put("actions", new CollectionFactory());
        factories.put("application", new ApplicationFactory());
        factories.put("bean", new ComponentFactory(Object.class, true));
        factories.put("borderLayout", new LayoutFactory(BorderLayout.class));
        factories.put("button", new ButtonFactory());
        factories.put("checkBox", new CheckBoxFactory());
        factories.put("container", new ComponentFactory(Component.class, false));
        factories.put("emptySpace", new EmptySpaceFactory());
        factories.put("hbox", new BoxFactory(Panel.Orientation.HORISONTAL));
        factories.put("horisontalLayout", new LayoutFactory(HorisontalLayout.class));
        factories.put("horizontalLayout", new LayoutFactory(HorisontalLayout.class));
        factories.put("label", new LabelFactory());
        factories.put("list", new CollectionFactory());
        factories.put("panel", new PanelFactory());
        factories.put("passwordBox", new PasswordBoxFactory());
        factories.put("progressBar", new ProgressBarFactory());
        factories.put("table", new TableFactory());
        factories.put("textArea", new TextAreaFactory());
        factories.put("textBox", new TextBoxFactory());
        factories.put("vbox", new BoxFactory(Panel.Orientation.VERTICAL));
        factories.put("verticalLayout", new LayoutFactory(VerticalLayout.class));
        factories.put("widget", new ComponentFactory(Component.class, true));
        setFactories(factories);

        Closure c = new Closure(this) {
            @Override
            public Object call(Object... args) {
                return handleIdAttribute(args);
            }

            private Object handleIdAttribute(Object[] args) {
                FactoryBuilderSupport builder = (FactoryBuilderSupport) args[0];
                Object node = args[1];
                Map attributes = (Map) args[2];
                if (attributes.containsKey("id")) {
                    String id = attributes.remove("id").toString();
                    builder.setVariable(id, node);
                }
                return null;
            }
        };
        setAttributeDelegates(newList(c));
    }
}
