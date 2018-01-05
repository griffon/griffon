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
package griffon.javafx.utils

import griffon.javafx.support.JavaFXAction
import griffon.javafx.support.JavaFXUtils
import javafx.scene.Node
import javafx.scene.chart.Axis
import javafx.scene.control.ButtonBase
import javafx.scene.control.CheckBox
import javafx.scene.control.CheckMenuItem
import javafx.scene.control.Control
import javafx.scene.control.Labeled
import javafx.scene.control.MenuItem
import javafx.scene.control.RadioButton
import javafx.scene.control.RadioMenuItem
import javafx.scene.control.Tab
import javafx.scene.control.TableColumn
import javafx.scene.control.ToggleButton
import javafx.scene.image.Image

/**
 * @author Andres Almiray
 * @since 2.13.0
 */

fun ButtonBase.configure(action: JavaFXAction) {
    JavaFXUtils.configure(this, action)
}

fun CheckBox.configure(action: JavaFXAction) {
    JavaFXUtils.configure(this, action)
}

fun CheckMenuItem.configure(action: JavaFXAction) {
    JavaFXUtils.configure(this, action)
}

fun MenuItem.configure(action: JavaFXAction) {
    JavaFXUtils.configure(this, action)
}

fun RadioButton.configure(action: JavaFXAction) {
    JavaFXUtils.configure(this, action)
}

fun RadioMenuItem.configure(action: JavaFXAction) {
    JavaFXUtils.configure(this, action)
}

fun ToggleButton.configure(action: JavaFXAction) {
    JavaFXUtils.configure(this, action)
}

fun Node.getGriffonActionId(): String? {
    return JavaFXUtils.getGriffonActionId(this)
}

fun Node.setGriffonActionId(actionId: String) {
    JavaFXUtils.setGriffonActionId(this, actionId)
}

fun MenuItem.getGriffonActionId(): String? {
    return JavaFXUtils.getGriffonActionId(this)
}

fun MenuItem.setGriffonActionId(actionId: String) {
    JavaFXUtils.setGriffonActionId(this, actionId)
}

fun Labeled.getI18nKey(): String? {
    return JavaFXUtils.getI18nKey(this)
}

fun MenuItem.getI18nKey(): String? {
    return JavaFXUtils.getI18nKey(this)
}

fun Tab.getI18nKey(): String? {
    return JavaFXUtils.getI18nKey(this)
}

fun <T> Axis<T>.getI18nKey(): String? {
    return JavaFXUtils.getI18nKey(this)
}

fun <S, T> TableColumn<S, T>.getI18nKey(): String? {
    return JavaFXUtils.getI18nKey(this)
}

fun Labeled.getI18nDefaultValue(): String? {
    return JavaFXUtils.getI18nDefaultValue(this)
}

fun MenuItem.getI18nDefaultValue(): String? {
    return JavaFXUtils.getI18nDefaultValue(this)
}

fun Tab.getI18nDefaultValue(): String? {
    return JavaFXUtils.getI18nDefaultValue(this)
}

fun <T> Axis<T>.getI18nDefaultValue(): String? {
    return JavaFXUtils.getI18nDefaultValue(this)
}

fun <S, T> TableColumn<S, T>.getI18nDefaultValue(): String? {
    return JavaFXUtils.getI18nDefaultValue(this)
}

fun Labeled.getI18nArgs(): String? {
    return JavaFXUtils.getI18nArgs(this)
}

fun MenuItem.getI18nArgs(): String? {
    return JavaFXUtils.getI18nArgs(this)
}

fun Tab.getI18nArgs(): String? {
    return JavaFXUtils.getI18nArgs(this)
}

fun <T> Axis<T>.getI18nArgs(): String? {
    return JavaFXUtils.getI18nArgs(this)
}

fun <S, T> TableColumn<S, T>.getI18nArgs(): String? {
    return JavaFXUtils.getI18nArgs(this)
}

fun Labeled.setI18nKey(key: String) {
    return JavaFXUtils.setI18nKey(this, key)
}

fun MenuItem.setI18nKey(key: String) {
    return JavaFXUtils.setI18nKey(this, key)
}

fun Tab.setI18nKey(key: String) {
    return JavaFXUtils.setI18nKey(this, key)
}

fun <T> Axis<T>.setI18nKey(key: String) {
    return JavaFXUtils.setI18nKey(this, key)
}

fun <S, T> TableColumn<S, T>.setI18nKey(key: String) {
    return JavaFXUtils.setI18nKey(this, key)
}

fun Labeled.setI18nDefaultValue(defaultValue: String) {
    return JavaFXUtils.setI18nDefaultValue(this, defaultValue)
}

fun MenuItem.setI18nDefaultValue(defaultValue: String) {
    return JavaFXUtils.setI18nDefaultValue(this, defaultValue)
}

fun Tab.setI18nDefaultValue(defaultValue: String) {
    return JavaFXUtils.setI18nDefaultValue(this, defaultValue)
}

fun <T> Axis<T>.setI18nDefaultValue(defaultValue: String) {
    return JavaFXUtils.setI18nDefaultValue(this, defaultValue)
}

fun <S, T> TableColumn<S, T>.setI18nDefaultValue(defaultValue: String) {
    return JavaFXUtils.setI18nDefaultValue(this, defaultValue)
}

fun Labeled.setI18nArgs(args: String) {
    return JavaFXUtils.setI18nArgs(this, args)
}

fun MenuItem.setI18nArgs(args: String) {
    return JavaFXUtils.setI18nArgs(this, args)
}

fun Tab.setI18nArgs(args: String) {
    return JavaFXUtils.setI18nArgs(this, args)
}

fun <T> Axis<T>.setI18nArgs(args: String) {
    return JavaFXUtils.setI18nArgs(this, args)
}

fun <S, T> TableColumn<S, T>.setI18nArgs(args: String) {
    return JavaFXUtils.setI18nArgs(this, args)
}

fun Labeled.setIcon(iconUrl: String) {
    JavaFXUtils.setIcon(this, iconUrl)
}

fun MenuItem.setIcon(iconUrl: String) {
    JavaFXUtils.setIcon(this, iconUrl)
}

fun Labeled.setGraphic(image: Image?) {
    JavaFXUtils.setGraphic(this, image)
}

fun Labeled.setGraphic(node: Node?) {
    JavaFXUtils.setGraphic(this, node)
}

fun MenuItem.setGraphic(image: Image?) {
    JavaFXUtils.setGraphic(this, image)
}

fun MenuItem.setGraphic(node: Node?) {
    JavaFXUtils.setGraphic(this, node)
}

fun ButtonBase.setGraphicStyle(style: String) {
    JavaFXUtils.setGraphicStyle(this, style)
}

fun MenuItem.setGraphicStyle(style: String) {
    JavaFXUtils.setGraphicStyle(this, style)
}

fun ButtonBase.setGraphicStyleClass(styleClass: String, remove: Boolean = false) {
    JavaFXUtils.setGraphicStyleClass(this, styleClass, remove)
}

fun MenuItem.setGraphicStyleClass(styleClass: String, remove: Boolean = false) {
    JavaFXUtils.setGraphicStyleClass(this, styleClass, remove)
}

fun Control.setTooltip(text: String?) {
    JavaFXUtils.setTooltip(this, text)
}

fun Node.findNode(id: String): Node? {
    return JavaFXUtils.findNode(this, id)
}