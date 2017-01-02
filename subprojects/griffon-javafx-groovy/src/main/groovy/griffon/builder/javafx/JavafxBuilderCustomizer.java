/*
 * Copyright 2008-2017 the original author or authors.
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
package griffon.builder.javafx;

import griffon.builder.javafx.factory.ActionFactory;
import griffon.builder.javafx.factory.ApplicationFactory;
import griffon.builder.javafx.factory.ControlFactory;
import griffon.builder.javafx.factory.FXMLFactory;
import griffon.builder.javafx.factory.LabeledFactory;
import griffon.builder.javafx.factory.MenuFactory;
import griffon.builder.javafx.factory.MenuItemFactory;
import griffon.inject.DependsOn;
import groovy.util.Factory;
import groovyx.javafx.SceneGraphBuilder;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import org.codehaus.griffon.runtime.groovy.view.AbstractBuilderCustomizer;

import javax.inject.Named;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Andres Almiray
 */
@Named("javafx")
@DependsOn({"core"})
@SuppressWarnings("rawtypes")
public class JavafxBuilderCustomizer extends AbstractBuilderCustomizer {
    @SuppressWarnings("unchecked")
    public JavafxBuilderCustomizer() {
        SceneGraphBuilder builder = new SceneGraphBuilder();

        Map<String, Factory> factories = new LinkedHashMap<>(builder.getFactories());
        factories.put("application", new ApplicationFactory());
        factories.remove("fxaction");
        factories.put("action", new ActionFactory());

        factories.put("menuBar", new MenuFactory(MenuBar.class));
        factories.put("contextMenu", new MenuFactory(ContextMenu.class));
        factories.put("menuButton", new MenuFactory(MenuButton.class));
        factories.put("splitMenuButton", new MenuFactory(SplitMenuButton.class));

        factories.put("menu", new MenuItemFactory(Menu.class));
        factories.put("menuItem", new MenuItemFactory(MenuItem.class));
        factories.put("checkMenuItem", new MenuItemFactory(CheckMenuItem.class));
        factories.put("customMenuItem", new MenuItemFactory(CustomMenuItem.class));
        factories.put("separatorMenuItem", new MenuItemFactory(SeparatorMenuItem.class));
        factories.put("radioMenuItem", new MenuItemFactory(RadioMenuItem.class));

        factories.put("button", new LabeledFactory(Button.class));
        factories.put("checkBox", new LabeledFactory(CheckBox.class));
        factories.put("label", new LabeledFactory(Label.class));
        factories.put("choiceBox", new LabeledFactory(ChoiceBox.class));
        factories.put("hyperlink", new LabeledFactory(Hyperlink.class));
        factories.put("tooltip", new LabeledFactory(Tooltip.class));
        factories.put("radioButton", new LabeledFactory(RadioButton.class));
        factories.put("toggleButton", new LabeledFactory(ToggleButton.class));

        factories.put("comboBox", new ControlFactory(ComboBox.class));

        factories.put("fxml", new FXMLFactory());

        setFactories(factories);
        setVariables(builder.getVariables());
        setMethods(builder.getExplicitMethods());
        setProps(builder.getExplicitProperties());
        setAttributeDelegates(builder.getAttributeDelegates());
        setPreInstantiateDelegates(builder.getPreInstantiateDelegates());
        setPostInstantiateDelegates(builder.getPostInstantiateDelegates());
        setPostNodeCompletionDelegates(builder.getPostNodeCompletionDelegates());
        setDisposalClosures(builder.getDisposalClosures());
        setMethodMissingDelegate(builder.getMethodMissingDelegate());
        setPropertyMissingDelegate(builder.getPropertyMissingDelegate());
    }
}
