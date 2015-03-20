/*
 * Copyright 2008-2015 the original author or authors.
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
package griffon.builder.javafx.factory

import griffon.javafx.support.JavaFXAction
import groovyx.javafx.factory.AbstractFXBeanFactory
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.scene.control.Tooltip
import javafx.scene.image.Image
import javafx.scene.image.ImageView

import static griffon.javafx.support.JavaFXUtils.resolveIcon

/**
 *
 * @author Andres Almiray
 */
class ActionFactory extends AbstractFXBeanFactory {
    ActionFactory() {
        super(JavaFXAction, false)
    }

    @Override
    boolean onHandleNodeAttributes(FactoryBuilderSupport builder, Object node, Map attributes) {
        attributes.remove('id')
        return super.onHandleNodeAttributes(builder, node, attributes)
    }

    static Map extractActionParams(Map attributes) {
        Map actionParams = [:]

        actionParams.skipOnAction = attributes.remove('skipOnAction')
        actionParams.skipName = attributes.remove('skipName')
        actionParams.skipDescription = attributes.remove('skipDescription')
        actionParams.skipAccelerator = attributes.remove('skipAccelerator')
        actionParams.skipIcon = attributes.remove('skipIcon')
        actionParams.skipSelected = attributes.remove('skipSelected')
        actionParams.skipEnabled = attributes.remove('skipEnabled')

        actionParams
    }

    static void applyAction(control, JavaFXAction action, Map actionParams) {
        MetaClass mc = control.metaClass

        if (!actionParams.skipOnAction && mc.respondsTo(control, "onActionProperty")) {
            action.onActionProperty().addListener(new ChangeListener() {
                void changed(ObservableValue observableValue, Object oldValue, Object newValue) {
                    control.onActionProperty().set(newValue)
                }
            })
            control.onActionProperty().set(action.onAction)
        }
        if (!actionParams.skipName && mc.respondsTo(control, "textProperty")) {
            action.nameProperty().addListener(new ChangeListener() {
                void changed(ObservableValue observableValue, Object oldValue, Object newValue) {
                    control.textProperty().set(newValue)
                }
            })
            control.textProperty().set(action.name)
        }
        if (!actionParams.skipDescription && mc.respondsTo(control, "tooltipProperty")) {
            action.descriptionProperty().addListener(new ChangeListener() {
                void changed(ObservableValue observableValue, Object oldValue, Object newValue) {
                    setTooltip(control, newValue)
                }
            })
            if (action.description) setTooltip(control, action.description)
        }
        if (!actionParams.skipAccelerator && mc.respondsTo(control, "acceleratorProperty")) {
            action.acceleratorProperty().addListener(new ChangeListener() {
                void changed(ObservableValue observableValue, Object oldValue, Object newValue) {
                    control.acceleratorProperty().set(newValue)
                }
            })
            if (action.accelerator != null) control.acceleratorProperty().set(action.accelerator)
        }
        if (mc.respondsTo(control, "graphicProperty")) {
            if (!actionParams.skipIcon) {
                action.iconProperty().addListener(new ChangeListener() {
                    void changed(ObservableValue observableValue, Object oldValue, Object newValue) {
                        setIcon(control, newValue)
                    }
                })
                if (action.icon) setIcon(control, action.icon)
            }
        }
        if (!actionParams.skipSelected && mc.respondsTo(control, "selectedProperty")) {
            action.selectedProperty().addListener(new ChangeListener() {
                void changed(ObservableValue observableValue, Object oldValue, Object newValue) {
                    control.selectedProperty().set(newValue)
                }
            })
            control.selectedProperty().set(action.selected)
        }
        if (!actionParams.skipEnabled && mc.respondsTo(control, "disableProperty")) {
            action.enabledProperty().addListener(new ChangeListener() {
                void changed(ObservableValue observableValue, Object oldValue, Object newValue) {
                    control.disableProperty().set(!newValue)
                }
            })
            control.disableProperty().set(!action.enabled)
        }
    }

    static void setIcon(node, String iconUrl) {
        node.graphicProperty().set(resolveIcon(iconUrl))
    }

    static void setTooltip(node, String text) {
        Tooltip tooltip = node.tooltipProperty().get()
        if (!tooltip) {
            tooltip = new Tooltip()
            node.tooltipProperty().set(tooltip)
        }
        tooltip.text = text
    }
}
