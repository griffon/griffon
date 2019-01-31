/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2019 the original author or authors.
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
package griffon.builder.lanterna3;

import com.googlecode.lanterna.gui2.BorderLayout;
import com.googlecode.lanterna.gui2.Component;
import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.LinearLayout;
import griffon.annotations.inject.DependsOn;
import griffon.builder.lanterna3.factory.ActionFactory;
import griffon.builder.lanterna3.factory.ActionListBoxFactory;
import griffon.builder.lanterna3.factory.ApplicationFactory;
import griffon.builder.lanterna3.factory.BoxFactory;
import griffon.builder.lanterna3.factory.ButtonFactory;
import griffon.builder.lanterna3.factory.CheckBoxFactory;
import griffon.builder.lanterna3.factory.CollectionFactory;
import griffon.builder.lanterna3.factory.ComponentFactory;
import griffon.builder.lanterna3.factory.EmptySpaceFactory;
import griffon.builder.lanterna3.factory.LabelFactory;
import griffon.builder.lanterna3.factory.LayoutFactory;
import griffon.builder.lanterna3.factory.PanelFactory;
import griffon.builder.lanterna3.factory.ProgressBarFactory;
import griffon.builder.lanterna3.factory.TableFactory;
import griffon.builder.lanterna3.factory.TextAreaFactory;
import griffon.builder.lanterna3.factory.TextBoxFactory;
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
 * @since 3.0.0
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
        factories.put("hbox", new BoxFactory(Direction.HORIZONTAL));
        factories.put("horisontalLayout", new LayoutFactory(LinearLayout.class, Direction.HORIZONTAL));
        factories.put("horizontalLayout", new LayoutFactory(LinearLayout.class, Direction.HORIZONTAL));
        factories.put("label", new LabelFactory());
        factories.put("list", new CollectionFactory());
        factories.put("panel", new PanelFactory());
        factories.put("progressBar", new ProgressBarFactory());
        factories.put("table", new TableFactory());
        factories.put("textArea", new TextAreaFactory());
        factories.put("textBox", new TextBoxFactory());
        factories.put("vbox", new BoxFactory(Direction.VERTICAL));
        factories.put("verticalLayout", new LayoutFactory(LinearLayout.class, Direction.VERTICAL));
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
