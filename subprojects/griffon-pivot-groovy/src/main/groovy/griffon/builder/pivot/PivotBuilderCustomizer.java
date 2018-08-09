/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2018 the original author or authors.
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
package griffon.builder.pivot;

import griffon.annotations.inject.DependsOn;
import griffon.builder.pivot.factory.AccordionFactory;
import griffon.builder.pivot.factory.AdapterFactory;
import griffon.builder.pivot.factory.ApplicationFactory;
import griffon.builder.pivot.factory.BeanFactory;
import griffon.builder.pivot.factory.BoundsFactory;
import griffon.builder.pivot.factory.BoxPaneFactory;
import griffon.builder.pivot.factory.ButtonDataFactory;
import griffon.builder.pivot.factory.ButtonDataRendererFactory;
import griffon.builder.pivot.factory.ButtonFactory;
import griffon.builder.pivot.factory.ButtonGroupFactory;
import griffon.builder.pivot.factory.BxmlFactory;
import griffon.builder.pivot.factory.CalendarDateSpinnerDataFactory;
import griffon.builder.pivot.factory.ComponentFactory;
import griffon.builder.pivot.factory.ContainerFactory;
import griffon.builder.pivot.factory.FileBrowserSheetFactory;
import griffon.builder.pivot.factory.FormFlagFactory;
import griffon.builder.pivot.factory.FormSectionFactory;
import griffon.builder.pivot.factory.GridPaneFactory;
import griffon.builder.pivot.factory.GridPaneRowFactory;
import griffon.builder.pivot.factory.ImageViewFactory;
import griffon.builder.pivot.factory.InsetsFactory;
import griffon.builder.pivot.factory.JavaCollectionFactory;
import griffon.builder.pivot.factory.MenuBarFactory;
import griffon.builder.pivot.factory.MenuBarItemFactory;
import griffon.builder.pivot.factory.MenuFactory;
import griffon.builder.pivot.factory.MenuItemFactory;
import griffon.builder.pivot.factory.NumericSpinnerDataFactory;
import griffon.builder.pivot.factory.PairFactory;
import griffon.builder.pivot.factory.PictureFactory;
import griffon.builder.pivot.factory.PivotBeanFactory;
import griffon.builder.pivot.factory.RollupFactory;
import griffon.builder.pivot.factory.ScrollBarScopeFactory;
import griffon.builder.pivot.factory.ScrollPaneFactory;
import griffon.builder.pivot.factory.SingleElementContainerFactory;
import griffon.builder.pivot.factory.SliderFactory;
import griffon.builder.pivot.factory.SplitPaneFactory;
import griffon.builder.pivot.factory.TabPaneFactory;
import griffon.builder.pivot.factory.TablePaneColumnFactory;
import griffon.builder.pivot.factory.TablePaneFactory;
import griffon.builder.pivot.factory.TablePaneRowFactory;
import griffon.builder.pivot.factory.TextComponentFactory;
import griffon.builder.pivot.factory.ViewportFactory;
import griffon.builder.pivot.factory.WidgetFactory;
import griffon.exceptions.PropertyException;
import griffon.pivot.support.PivotAction;
import griffon.pivot.support.adapters.*;
import groovy.lang.Closure;
import groovy.util.Factory;
import groovy.util.FactoryBuilderSupport;
import org.apache.pivot.wtk.ActivityIndicator;
import org.apache.pivot.wtk.Border;
import org.apache.pivot.wtk.BoxPane;
import org.apache.pivot.wtk.Calendar;
import org.apache.pivot.wtk.CalendarButton;
import org.apache.pivot.wtk.CardPane;
import org.apache.pivot.wtk.Checkbox;
import org.apache.pivot.wtk.ColorChooser;
import org.apache.pivot.wtk.ColorChooserButton;
import org.apache.pivot.wtk.Component;
import org.apache.pivot.wtk.Container;
import org.apache.pivot.wtk.Dialog;
import org.apache.pivot.wtk.Dimensions;
import org.apache.pivot.wtk.Expander;
import org.apache.pivot.wtk.FileBrowser;
import org.apache.pivot.wtk.FlowPane;
import org.apache.pivot.wtk.Form;
import org.apache.pivot.wtk.Frame;
import org.apache.pivot.wtk.GridPane;
import org.apache.pivot.wtk.Label;
import org.apache.pivot.wtk.LinkButton;
import org.apache.pivot.wtk.ListButton;
import org.apache.pivot.wtk.ListView;
import org.apache.pivot.wtk.MenuButton;
import org.apache.pivot.wtk.MenuPopup;
import org.apache.pivot.wtk.Meter;
import org.apache.pivot.wtk.Orientation;
import org.apache.pivot.wtk.Palette;
import org.apache.pivot.wtk.Panel;
import org.apache.pivot.wtk.Panorama;
import org.apache.pivot.wtk.Point;
import org.apache.pivot.wtk.PushButton;
import org.apache.pivot.wtk.RadioButton;
import org.apache.pivot.wtk.ScrollBar;
import org.apache.pivot.wtk.Separator;
import org.apache.pivot.wtk.Sheet;
import org.apache.pivot.wtk.Span;
import org.apache.pivot.wtk.Spinner;
import org.apache.pivot.wtk.StackPane;
import org.apache.pivot.wtk.TablePane;
import org.apache.pivot.wtk.TextArea;
import org.apache.pivot.wtk.TextInput;
import org.apache.pivot.wtk.Tooltip;
import org.apache.pivot.wtk.Visual;
import org.apache.pivot.wtk.Window;
import org.apache.pivot.wtk.content.ButtonDataRenderer;
import org.apache.pivot.wtk.content.CalendarButtonDataRenderer;
import org.apache.pivot.wtk.content.LinkButtonDataRenderer;
import org.apache.pivot.wtk.content.ListButtonColorItemRenderer;
import org.apache.pivot.wtk.content.ListButtonDataRenderer;
import org.apache.pivot.wtk.content.MenuBarItemDataRenderer;
import org.apache.pivot.wtk.content.MenuButtonDataRenderer;
import org.apache.pivot.wtk.content.MenuItemDataRenderer;
import org.apache.pivot.wtk.effects.BaselineDecorator;
import org.apache.pivot.wtk.effects.BlurDecorator;
import org.apache.pivot.wtk.effects.ClipDecorator;
import org.apache.pivot.wtk.effects.DropShadowDecorator;
import org.apache.pivot.wtk.effects.FadeDecorator;
import org.apache.pivot.wtk.effects.GrayscaleDecorator;
import org.apache.pivot.wtk.effects.OverlayDecorator;
import org.apache.pivot.wtk.effects.ReflectionDecorator;
import org.apache.pivot.wtk.effects.RotationDecorator;
import org.apache.pivot.wtk.effects.SaturationDecorator;
import org.apache.pivot.wtk.effects.ScaleDecorator;
import org.apache.pivot.wtk.effects.ShadeDecorator;
import org.apache.pivot.wtk.effects.TagDecorator;
import org.apache.pivot.wtk.effects.TranslationDecorator;
import org.apache.pivot.wtk.effects.WatermarkDecorator;
import org.apache.pivot.wtk.effects.easing.Circular;
import org.apache.pivot.wtk.effects.easing.Cubic;
import org.apache.pivot.wtk.effects.easing.Exponential;
import org.apache.pivot.wtk.effects.easing.Linear;
import org.apache.pivot.wtk.effects.easing.Quadratic;
import org.apache.pivot.wtk.effects.easing.Quartic;
import org.apache.pivot.wtk.effects.easing.Quintic;
import org.apache.pivot.wtk.effects.easing.Sine;
import org.apache.pivot.wtk.media.Image;
import org.codehaus.griffon.runtime.groovy.view.AbstractBuilderCustomizer;
import org.codehaus.groovy.runtime.MethodClosure;

import javax.inject.Named;
import java.util.LinkedHashMap;
import java.util.Map;

import static griffon.util.CollectionUtils.newList;
import static griffon.util.GriffonClassUtils.getPropertyValue;
import static griffon.util.GriffonClassUtils.setPropertyValue;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
@Named("pivot")
@DependsOn({"core"})
@SuppressWarnings("rawtypes")
public class PivotBuilderCustomizer extends AbstractBuilderCustomizer {
    private final Map<String, Factory> factories = new LinkedHashMap<>();

    @SuppressWarnings("unchecked")
    public PivotBuilderCustomizer() {
        // -- griffon
        registerFactory("application", new ApplicationFactory());

        // -- support
        registerPivotBeanFactory("action", PivotAction.class);
        registerFactory("actions", new JavaCollectionFactory());
        registerFactory("noparent", new JavaCollectionFactory());
        registerFactory("bxml", new BxmlFactory());
        registerFactory("buttonGroup", new ButtonGroupFactory());

        registerFactory("dimensions", new PairFactory(Dimensions.class, "width", "height"));
        registerFactory("point", new PairFactory(Point.class, "x", "y"));
        registerFactory("span", new PairFactory(Span.class, "start", "end"));
        registerFactory("insets", new InsetsFactory());
        registerFactory("bounds", new BoundsFactory());

        registerFactory("widget", new WidgetFactory(Component.class, false));
        registerFactory("container", new WidgetFactory(Container.class, false));
        registerFactory("bean", new WidgetFactory(Object.class, true));

        // -- widgets
        registerPivotComponentFactory("activityIndicator", ActivityIndicator.class);
        registerPivotComponentFactory("fileBrowser", FileBrowser.class);
        registerFactory("label", new TextComponentFactory(Label.class));
        registerFactory("meter", new TextComponentFactory(Meter.class));
        registerPivotComponentFactory("separator", Separator.class);
        registerFactory("textArea", new TextComponentFactory(TextArea.class));
        registerFactory("textInput", new TextComponentFactory(TextInput.class));

        registerFactory("slider", new SliderFactory());
        registerPivotComponentFactory("spinner", Spinner.class);
        registerFactory("numericSpinnerData", new NumericSpinnerDataFactory());
        registerFactory("calendarDateSpinnerData", new CalendarDateSpinnerDataFactory());
        registerPivotComponentFactory("scrollBar", ScrollBar.class);
        registerFactory("scrollBarScope", new ScrollBarScopeFactory());

        // -- views
        registerPivotComponentFactory("listView", ListView.class);
        registerFactory("imageView", new ImageViewFactory());
        // registerPivotComponentFactory("movieView", MovieView)
        // registerPivotComponentFactory("tableView", TableView)
        // registerPivotComponentFactory("tableViewHeader", TableViewHeader)
        // registerPivotComponentFactory("treeView", TreeView)

        // -- buttons
        registerFactory("buttonData", new ButtonDataFactory());
        registerFactory("calendarButton", new ButtonFactory(CalendarButton.class));
        registerFactory("checkbox", new ButtonFactory(Checkbox.class));
        registerFactory("colorChooserButton", new ButtonFactory(ColorChooserButton.class));
        registerFactory("linkButton", new ButtonFactory(LinkButton.class));
        registerFactory("listButton", new ButtonFactory(ListButton.class));
        registerFactory("menuButton", new ButtonFactory(MenuButton.class));
        Factory button = new ButtonFactory(PushButton.class);
        registerFactory("button", button);
        registerFactory("pushButton", button);
        registerFactory("radioButton", new ButtonFactory(RadioButton.class));

        registerFactory("buttonDataRenderer", new ButtonDataRendererFactory(ButtonDataRenderer.class));
        registerFactory("calendarButtonDataRenderer", new ButtonDataRendererFactory(CalendarButtonDataRenderer.class));
        registerFactory("linkButtonDataRenderer", new ButtonDataRendererFactory(LinkButtonDataRenderer.class));
        registerFactory("listButtonColorItemRenderer", new ButtonDataRendererFactory(ListButtonColorItemRenderer.class));
        registerFactory("listButtonDataRenderer", new ButtonDataRendererFactory(ListButtonDataRenderer.class));
        registerFactory("menuButtonDataRenderer", new ButtonDataRendererFactory(MenuButtonDataRenderer.class));
        registerFactory("buttonDataRenderer", new ButtonDataRendererFactory(ButtonDataRenderer.class));

        // -- menus
        registerFactory("menu", new MenuFactory());
        registerFactory("menuItem", new MenuItemFactory());
        registerFactory("menuBar", new MenuBarFactory());
        registerFactory("menuBarItem", new MenuBarItemFactory());
        registerPivotComponentFactory("menuPopup", MenuPopup.class);
        registerFactory("menuBarItemDataRenderer", new ButtonDataRendererFactory(MenuBarItemDataRenderer.class));
        registerFactory("menuItemDataRenderer", new ButtonDataRendererFactory(MenuItemDataRenderer.class));

        // -- panes
        Factory hbox = new BoxPaneFactory(Orientation.HORIZONTAL);
        registerFactory("box", hbox);
        registerFactory("hbox", hbox);
        registerFactory("vbox", new BoxPaneFactory(Orientation.VERTICAL));
        registerPivotContainerFactory("boxPane", BoxPane.class);
        registerPivotContainerFactory("cardPane", CardPane.class);
        registerPivotContainerFactory("flowPane", FlowPane.class);
        registerFactory("gridPane", new GridPaneFactory());
        registerFactory("gridRow", new GridPaneRowFactory());
        registerPivotContainerFactory("gridFiller", GridPane.Filler.class);
        registerFactory("scrollPane", new ScrollPaneFactory());
        registerFactory("splitPane", new SplitPaneFactory());
        registerPivotContainerFactory("stackPane", StackPane.class);
        registerFactory("tabPane", new TabPaneFactory());
        registerFactory("tablePane", new TablePaneFactory());
        registerFactory("tablePaneColumn", new TablePaneColumnFactory());
        registerFactory("tablePaneRow", new TablePaneRowFactory());
        registerPivotContainerFactory("tablePaneFiller", TablePane.Filler.class);

        // -- containers
        registerFactory("accordion", new AccordionFactory());
        registerPivotContainerFactory("border", Border.class, true);
        registerPivotContainerFactory("calendar", Calendar.class);
        registerPivotContainerFactory("colorChooser", ColorChooser.class);
        registerPivotContainerFactory("expander", Expander.class, true);
        registerPivotComponentFactory("form", Form.class);
        registerFactory("formSection", new FormSectionFactory());
        registerFactory("formFlag", new FormFlagFactory());
        registerPivotContainerFactory("panel", Panel.class);
        registerFactory("panorama", new ViewportFactory(Panorama.class));
        registerFactory("rollup", new RollupFactory());

        // -- windows
        // registerPivotContainerFactory("alert", Alert)
        // registerPivotContainerFactory("prompt", Prompt)
        registerPivotContainerFactory("dialog", Dialog.class, true);
        registerPivotContainerFactory("frame", Frame.class, true);
        registerFactory("fileBrowserSheet", new FileBrowserSheetFactory());
        registerPivotContainerFactory("palette", Palette.class, true);
        registerPivotContainerFactory("sheet", Sheet.class, true);
        registerFactory("tooltip", new TextComponentFactory(Tooltip.class));
        registerPivotContainerFactory("window", Window.class, true);

        // -- effects
        registerPivotBeanFactory("baselineDecorator", BaselineDecorator.class);
        registerPivotBeanFactory("blurDecorator", BlurDecorator.class);
        registerPivotBeanFactory("clipDecorator", ClipDecorator.class);
        registerPivotBeanFactory("dropShadowDecorator", DropShadowDecorator.class);
        registerPivotBeanFactory("fadeDecorator", FadeDecorator.class);
        registerPivotBeanFactory("grayscaleDecorator", GrayscaleDecorator.class);
        registerPivotBeanFactory("reflectionDecorator", ReflectionDecorator.class);
        registerPivotBeanFactory("rotationDecorator", RotationDecorator.class);
        registerPivotBeanFactory("saturationDecorator", SaturationDecorator.class);
        registerPivotBeanFactory("scaleDecorator", ScaleDecorator.class);
        registerPivotBeanFactory("shadeDecorator", ShadeDecorator.class);
        registerPivotBeanFactory("translationDecorator", TranslationDecorator.class);
        registerFactory("overlayDecorator", new SingleElementContainerFactory(OverlayDecorator.class, "component", Component.class));
        registerFactory("tagDecorator", new SingleElementContainerFactory(TagDecorator.class, "tag", Visual.class));
        registerFactory("watermarkDecorator", new SingleElementContainerFactory(WatermarkDecorator.class, "image", Image.class));

        registerBeanFactory("easingCircular", Circular.class);
        registerBeanFactory("easingCubic", Cubic.class);
        registerBeanFactory("easingExponential", Exponential.class);
        registerBeanFactory("easingLinear", Linear.class);
        registerBeanFactory("easingQuadratic", Quadratic.class);
        registerBeanFactory("easingQuartic", Quartic.class);
        registerBeanFactory("easingQuintic", Quintic.class);
        registerBeanFactory("easingSine", Sine.class);

        registerFactory("picture", new PictureFactory());

        // -- listeners
        registerFactory("accordionListener", new AdapterFactory(AccordionAdapter.class));
        registerFactory("accordionAttributeListener", new AdapterFactory(AccordionAttributeAdapter.class));
        registerFactory("accordionSelectionListener", new AdapterFactory(AccordionSelectionAdapter.class));
        registerFactory("actionListener", new AdapterFactory(ActionAdapter.class));
        registerFactory("actionClassListener", new AdapterFactory(ActionClassAdapter.class));
        registerFactory("activityIndicatorListener", new AdapterFactory(ActivityIndicatorAdapter.class));
        registerFactory("alertListener", new AdapterFactory(AlertAdapter.class));
        registerFactory("blockListener", new AdapterFactory(BlockAdapter.class));
        registerFactory("borderListener", new AdapterFactory(BorderAdapter.class));
        registerFactory("boxPaneListener", new AdapterFactory(BoxPaneAdapter.class));
        registerFactory("bulletedListListener", new AdapterFactory(BulletedListAdapter.class));
        registerFactory("buttonListener", new AdapterFactory(ButtonAdapter.class));
        registerFactory("buttonBindingListener", new AdapterFactory(ButtonBindingAdapter.class));
        registerFactory("buttonGroupListener", new AdapterFactory(ButtonGroupAdapter.class));
        registerFactory("buttonPressListener", new AdapterFactory(ButtonPressAdapter.class));
        registerFactory("buttonStateListener", new AdapterFactory(ButtonStateAdapter.class));
        registerFactory("calendarListener", new AdapterFactory(CalendarAdapter.class));
        registerFactory("calendarBindingListener", new AdapterFactory(CalendarBindingAdapter.class));
        registerFactory("calendarButtonListener", new AdapterFactory(CalendarButtonAdapter.class));
        registerFactory("calendarButtonBindingListener", new AdapterFactory(CalendarButtonBindingAdapter.class));
        registerFactory("calendarButtonSelectionListener", new AdapterFactory(CalendarButtonSelectionAdapter.class));
        registerFactory("calendarSelectionListener", new AdapterFactory(CalendarSelectionAdapter.class));
        registerFactory("cardPaneListener", new AdapterFactory(CardPaneAdapter.class));
        registerFactory("clipboardContentListener", new AdapterFactory(ClipboardContentAdapter.class));
        registerFactory("colorChooserBindingListener", new AdapterFactory(ColorChooserBindingAdapter.class));
        registerFactory("colorChooserButtonBindingListener", new AdapterFactory(ColorChooserButtonBindingAdapter.class));
        registerFactory("colorChooserButtonSelectionListener", new AdapterFactory(ColorChooserButtonSelectionAdapter.class));
        registerFactory("colorChooserSelectionListener", new AdapterFactory(ColorChooserSelectionAdapter.class));
        registerFactory("componentListener", new AdapterFactory(ComponentAdapter.class));
        registerFactory("componentClassListener", new AdapterFactory(ComponentClassAdapter.class));
        registerFactory("componentDataListener", new AdapterFactory(ComponentDataAdapter.class));
        registerFactory("componentDecoratorListener", new AdapterFactory(ComponentDecoratorAdapter.class));
        registerFactory("componentKeyListener", new AdapterFactory(ComponentKeyAdapter.class));
        registerFactory("componentMouseListener", new AdapterFactory(ComponentMouseAdapter.class));
        registerFactory("componentMouseButtonListener", new AdapterFactory(ComponentMouseButtonAdapter.class));
        registerFactory("componentMouseWheelListener", new AdapterFactory(ComponentMouseWheelAdapter.class));
        registerFactory("componentNodeListener", new AdapterFactory(ComponentNodeAdapter.class));
        registerFactory("componentStateListener", new AdapterFactory(ComponentStateAdapter.class));
        registerFactory("componentStyleListener", new AdapterFactory(ComponentStyleAdapter.class));
        registerFactory("componentTooltipListener", new AdapterFactory(ComponentTooltipAdapter.class));
        registerFactory("containerListener", new AdapterFactory(ContainerAdapter.class));
        registerFactory("containerMouseListener", new AdapterFactory(ContainerMouseAdapter.class));
        registerFactory("dialogListener", new AdapterFactory(DialogAdapter.class));
        registerFactory("dialogCloseListener", new AdapterFactory(DialogCloseAdapter.class));
        registerFactory("dialogStateListener", new AdapterFactory(DialogStateAdapter.class));
        registerFactory("elementListener", new AdapterFactory(ElementAdapter.class));
        registerFactory("expanderListener", new AdapterFactory(ExpanderAdapter.class));
        registerFactory("fileBrowserListener", new AdapterFactory(FileBrowserAdapter.class));
        registerFactory("fileBrowserSheetListener", new AdapterFactory(FileBrowserSheetAdapter.class));
        registerFactory("fillPaneListener", new AdapterFactory(FillPaneAdapter.class));
        registerFactory("formListener", new AdapterFactory(FormAdapter.class));
        registerFactory("formAttributeListener", new AdapterFactory(FormAttributeAdapter.class));
        registerFactory("frameListener", new AdapterFactory(FrameAdapter.class));
        registerFactory("gridPaneListener", new AdapterFactory(GridPaneAdapter.class));
        registerFactory("griffonPivotListener", new AdapterFactory(GriffonPivotAdapter.class));
        registerFactory("imageListener", new AdapterFactory(ImageAdapter.class));
        registerFactory("imageNodeListener", new AdapterFactory(ImageNodeAdapter.class));
        registerFactory("imageViewListener", new AdapterFactory(ImageViewAdapter.class));
        registerFactory("imageViewBindingListener", new AdapterFactory(ImageViewBindingAdapter.class));
        registerFactory("labelListener", new AdapterFactory(LabelAdapter.class));
        registerFactory("labelBindingListener", new AdapterFactory(LabelBindingAdapter.class));
        registerFactory("listButtonListener", new AdapterFactory(ListButtonAdapter.class));
        registerFactory("listButtonBindingListener", new AdapterFactory(ListButtonBindingAdapter.class));
        registerFactory("listButtonItemListener", new AdapterFactory(ListButtonItemAdapter.class));
        registerFactory("listButtonSelectionListener", new AdapterFactory(ListButtonSelectionAdapter.class));
        registerFactory("listViewListener", new AdapterFactory(ListViewAdapter.class));
        registerFactory("listViewBindingListener", new AdapterFactory(ListViewBindingAdapter.class));
        registerFactory("listViewItemListener", new AdapterFactory(ListViewItemAdapter.class));
        registerFactory("listViewItemStateListener", new AdapterFactory(ListViewItemStateAdapter.class));
        registerFactory("listViewSelectionListener", new AdapterFactory(ListViewSelectionAdapter.class));
        registerFactory("menuListener", new AdapterFactory(MenuAdapter.class));
        registerFactory("menuBarListener", new AdapterFactory(MenuBarAdapter.class));
        registerFactory("menuButtonListener", new AdapterFactory(MenuButtonAdapter.class));
        registerFactory("menuItemSelectionListener", new AdapterFactory(MenuItemSelectionAdapter.class));
        registerFactory("menuPopupListener", new AdapterFactory(MenuPopupAdapter.class));
        registerFactory("menuPopupStateListener", new AdapterFactory(MenuPopupStateAdapter.class));
        registerFactory("meterListener", new AdapterFactory(MeterAdapter.class));
        registerFactory("movieListener", new AdapterFactory(MovieAdapter.class));
        registerFactory("movieViewListener", new AdapterFactory(MovieViewAdapter.class));
        registerFactory("nodeListener", new AdapterFactory(NodeAdapter.class));
        registerFactory("numberedListListener", new AdapterFactory(NumberedListAdapter.class));
        registerFactory("promptListener", new AdapterFactory(PromptAdapter.class));
        registerFactory("rollupListener", new AdapterFactory(RollupAdapter.class));
        registerFactory("rollupStateListener", new AdapterFactory(RollupStateAdapter.class));
        registerFactory("scrollBarListener", new AdapterFactory(ScrollBarAdapter.class));
        registerFactory("scrollBarValueListener", new AdapterFactory(ScrollBarValueAdapter.class));
        registerFactory("scrollPaneListener", new AdapterFactory(ScrollPaneAdapter.class));
        registerFactory("separatorListener", new AdapterFactory(SeparatorAdapter.class));
        registerFactory("sheetCloseListener", new AdapterFactory(SheetCloseAdapter.class));
        registerFactory("sheetStateListener", new AdapterFactory(SheetStateAdapter.class));
        registerFactory("sliderListener", new AdapterFactory(SliderAdapter.class));
        registerFactory("sliderValueListener", new AdapterFactory(SliderValueAdapter.class));
        registerFactory("spinnerListener", new AdapterFactory(SpinnerAdapter.class));
        registerFactory("spinnerBindingListener", new AdapterFactory(SpinnerBindingAdapter.class));
        registerFactory("spinnerItemListener", new AdapterFactory(SpinnerItemAdapter.class));
        registerFactory("spinnerSelectionListener", new AdapterFactory(SpinnerSelectionAdapter.class));
        registerFactory("splitPaneListener", new AdapterFactory(SplitPaneAdapter.class));
        registerFactory("suggestionPopupListener", new AdapterFactory(SuggestionPopupAdapter.class));
        registerFactory("suggestionPopupCloseListener", new AdapterFactory(SuggestionPopupCloseAdapter.class));
        registerFactory("suggestionPopupItemListener", new AdapterFactory(SuggestionPopupItemAdapter.class));
        registerFactory("suggestionPopupSelectionListener", new AdapterFactory(SuggestionPopupSelectionAdapter.class));
        registerFactory("suggestionPopupStateListener", new AdapterFactory(SuggestionPopupStateAdapter.class));
        registerFactory("tablePaneListener", new AdapterFactory(TablePaneAdapter.class));
        registerFactory("tablePaneAttributeListener", new AdapterFactory(TablePaneAttributeAdapter.class));
        registerFactory("tableViewListener", new AdapterFactory(TableViewAdapter.class));
        registerFactory("tableViewBindingListener", new AdapterFactory(TableViewBindingAdapter.class));
        registerFactory("tableViewColumnListener", new AdapterFactory(TableViewColumnAdapter.class));
        registerFactory("tableViewHeaderListener", new AdapterFactory(TableViewHeaderAdapter.class));
        registerFactory("tableViewHeaderPressListener", new AdapterFactory(TableViewHeaderPressAdapter.class));
        registerFactory("tableViewRowListener", new AdapterFactory(TableViewRowAdapter.class));
        registerFactory("tableViewSelectionListener", new AdapterFactory(TableViewSelectionAdapter.class));
        registerFactory("tableViewSortListener", new AdapterFactory(TableViewSortAdapter.class));
        registerFactory("tabPaneListener", new AdapterFactory(TabPaneAdapter.class));
        registerFactory("tabPaneAttributeListener", new AdapterFactory(TabPaneAttributeAdapter.class));
        registerFactory("tabPaneSelectionListener", new AdapterFactory(TabPaneSelectionAdapter.class));
        registerFactory("textAreaListener", new AdapterFactory(TextAreaAdapter.class));
        registerFactory("textAreaBindingListener", new AdapterFactory(TextAreaBindingAdapter.class));
        registerFactory("textAreaContentListener", new AdapterFactory(TextAreaContentAdapter.class));
        registerFactory("textAreaSelectionListener", new AdapterFactory(TextAreaSelectionAdapter.class));
        registerFactory("textInputListener", new AdapterFactory(TextInputAdapter.class));
        registerFactory("textInputBindingListener", new AdapterFactory(TextInputBindingAdapter.class));
        registerFactory("textInputContentListener", new AdapterFactory(TextInputContentAdapter.class));
        registerFactory("textInputSelectionListener", new AdapterFactory(TextInputSelectionAdapter.class));
        registerFactory("textNodeListener", new AdapterFactory(TextNodeAdapter.class));
        registerFactory("textPaneListener", new AdapterFactory(TextPaneAdapter.class));
        registerFactory("textPaneCharacterListener", new AdapterFactory(TextPaneCharacterAdapter.class));
        registerFactory("textPaneSelectionListener", new AdapterFactory(TextPaneSelectionAdapter.class));
        registerFactory("transitionListener", new AdapterFactory(TransitionAdapter.class));
        registerFactory("treeViewListener", new AdapterFactory(TreeViewAdapter.class));
        registerFactory("treeViewBranchListener", new AdapterFactory(TreeViewBranchAdapter.class));
        registerFactory("treeViewNodeListener", new AdapterFactory(TreeViewNodeAdapter.class));
        registerFactory("treeViewNodeStateListener", new AdapterFactory(TreeViewNodeStateAdapter.class));
        registerFactory("treeViewSelectionListener", new AdapterFactory(TreeViewSelectionAdapter.class));
        registerFactory("viewportListener", new AdapterFactory(ViewportAdapter.class));
        registerFactory("windowActionMappingListener", new AdapterFactory(WindowActionMappingAdapter.class));
        registerFactory("windowListener", new AdapterFactory(WindowAdapter.class));
        registerFactory("windowClassListener", new AdapterFactory(WindowClassAdapter.class));
        registerFactory("windowStateListener", new AdapterFactory(WindowStateAdapter.class));

        setFactories(factories);

        Closure c1 = new Closure(this) {
            private static final long serialVersionUID = -4842025938657659150L;

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

                    // set id: as name: if node supports the property
                    try {
                        Object name = getPropertyValue(node, "name");
                        if (name == null) {
                            setPropertyValue(node, "name", id);
                        }
                    } catch (PropertyException pe) {
                        // ignore
                    }
                }
                return null;
            }
        };
        Closure c2 = new MethodClosure(ButtonGroupFactory.class, "buttonGroupAttributeDelegate");
        setAttributeDelegates(newList(c1, c2));
    }

    private void registerFactory(String name, Factory factory) {
        factories.put(name, factory);
    }

    private void registerBeanFactory(String name, Class pivotBeanClass) {
        registerBeanFactory(name, pivotBeanClass, true);
    }

    private void registerBeanFactory(String name, Class pivotBeanClass, boolean leaf) {
        registerFactory(name, new BeanFactory(pivotBeanClass, leaf));
    }

    private void registerPivotBeanFactory(String name, Class pivotBeanClass) {
        registerPivotBeanFactory(name, pivotBeanClass, true);
    }

    private void registerPivotBeanFactory(String name, Class pivotBeanClass, boolean leaf) {
        registerFactory(name, new PivotBeanFactory(pivotBeanClass, leaf));
    }

    private void registerPivotComponentFactory(String name, Class pivotBeanClass) {
        registerPivotComponentFactory(name, pivotBeanClass, true);
    }

    private void registerPivotComponentFactory(String name, Class pivotBeanClass, boolean leaf) {
        registerFactory(name, new ComponentFactory(pivotBeanClass, leaf));
    }

    private void registerPivotContainerFactory(String name, Class pivotBeanClass) {
        registerPivotContainerFactory(name, pivotBeanClass, true);
    }

    private void registerPivotContainerFactory(String name, Class pivotBeanClass, boolean singleElement) {
        registerFactory(name, singleElement ? new SingleElementContainerFactory(pivotBeanClass, "content", Component.class) : new ContainerFactory(pivotBeanClass));
    }
}
