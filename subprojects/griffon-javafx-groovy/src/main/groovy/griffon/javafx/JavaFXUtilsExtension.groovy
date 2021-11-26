/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2021 the original author or authors.
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
package griffon.javafx

import griffon.javafx.support.JavaFXAction
import griffon.javafx.support.JavaFXUtils
import groovy.transform.CompileStatic
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

import javax.annotation.Nonnull
import javax.annotation.Nullable

/**
 * @author Andres Almiray
 * @since 2.13.0
 */
@CompileStatic
final class JavaFXUtilsExtension {
    @Nullable
    static String getGriffonActionId(@Nonnull Node self) {
        JavaFXUtils.getGriffonActionId(self)
    }

    @Nullable
    static String getGriffonActionId(@Nonnull MenuItem self) {
        JavaFXUtils.getGriffonActionId(self)
    }

    static void setGriffonActionId(@Nonnull Node self, @Nonnull String actionId) {
        JavaFXUtils.setGriffonActionId(self, actionId)
    }

    static void setGriffonActionId(@Nonnull MenuItem self, @Nonnull String actionId) {
        JavaFXUtils.setGriffonActionId(self, actionId)
    }

    static void configure(@Nonnull ButtonBase self, @Nonnull JavaFXAction action) {
        JavaFXUtils.configure(self, action)
    }

    static void configure(@Nonnull CheckBox self, @Nonnull JavaFXAction action) {
        JavaFXUtils.configure(self, action)
    }

    static void configure(@Nonnull CheckMenuItem self, @Nonnull JavaFXAction action) {
        JavaFXUtils.configure(self, action)
    }

    static void configure(@Nonnull MenuItem self, @Nonnull JavaFXAction action) {
        JavaFXUtils.configure(self, action)
    }

    static void configure(@Nonnull RadioButton self, @Nonnull JavaFXAction action) {
        JavaFXUtils.configure(self, action)
    }

    static void configure(@Nonnull RadioMenuItem self, @Nonnull JavaFXAction action) {
        JavaFXUtils.configure(self, action)
    }

    static void configure(@Nonnull ToggleButton self, @Nonnull JavaFXAction action) {
        JavaFXUtils.configure(self, action)
    }

    @Nullable
    static String getI18nKey(@Nonnull Labeled self) {
        JavaFXUtils.getI18nKey(self)
    }

    @Nullable
    static String getI18nKey(@Nonnull MenuItem self) {
        JavaFXUtils.getI18nKey(self)
    }

    @Nullable
    static String getI18nKey(@Nonnull Tab self) {
        JavaFXUtils.getI18nKey(self)
    }

    @Nullable
    static <T> String getI18nKey(@Nonnull Axis<T> self) {
        JavaFXUtils.getI18nKey(self)
    }

    @Nullable
    static <S, T> String getI18nKey(@Nonnull TableColumn<S, T> self) {
        JavaFXUtils.getI18nKey(self)
    }

    @Nullable
    static String getI18nDefaultValue(@Nonnull Labeled self) {
        JavaFXUtils.getI18nDefaultValue(self)
    }

    @Nullable
    static String getI18nDefaultValue(@Nonnull MenuItem self) {
        JavaFXUtils.getI18nDefaultValue(self)
    }

    @Nullable
    static String getI18nDefaultValue(@Nonnull Tab self) {
        JavaFXUtils.getI18nDefaultValue(self)
    }

    @Nullable
    static <T> String getI18nDefaultValue(@Nonnull Axis<T> self) {
        JavaFXUtils.getI18nDefaultValue(self)
    }

    @Nullable
    static <S, T> String getI18nDefaultValue(@Nonnull TableColumn<S, T> self) {
        JavaFXUtils.getI18nDefaultValue(self)
    }

    @Nullable
    static String getI18nArgs(@Nonnull Labeled self) {
        JavaFXUtils.getI18nArgs(self)
    }

    @Nullable
    static String getI18nArgs(@Nonnull MenuItem self) {
        JavaFXUtils.getI18nArgs(self)
    }

    @Nullable
    static String getI18nArgs(@Nonnull Tab self) {
        JavaFXUtils.getI18nArgs(self)
    }

    @Nullable
    static <T> String getI18nArgs(@Nonnull Axis<T> self) {
        JavaFXUtils.getI18nArgs(self)
    }

    @Nullable
    static <S, T> String getI18nArgs(@Nonnull TableColumn<S, T> self) {
        JavaFXUtils.getI18nArgs(self)
    }

    static void setI18nKey(@Nonnull Labeled self, @Nonnull String key) {
        JavaFXUtils.setI18nKey(self, key)
    }

    static void setI18nKey(@Nonnull MenuItem self, @Nonnull String key) {
        JavaFXUtils.setI18nKey(self, key)
    }

    static void setI18nKey(@Nonnull Tab self, @Nonnull String key) {
        JavaFXUtils.setI18nKey(self, key)
    }

    static <T> void setI18nKey(@Nonnull Axis<T> self, @Nonnull String key) {
        JavaFXUtils.setI18nKey(self, key)
    }

    static <S, T> void setI18nKey(@Nonnull TableColumn<S, T> self, @Nonnull String key) {
        JavaFXUtils.setI18nKey(self, key)
    }

    static void setI18nDefaultValue(@Nonnull Labeled self, @Nonnull String defaultValue) {
        JavaFXUtils.setI18nDefaultValue(self, defaultValue)
    }

    static void setI18nDefaultValue(@Nonnull MenuItem self, @Nonnull String defaultValue) {
        JavaFXUtils.setI18nDefaultValue(self, defaultValue)
    }

    static void setI18nDefaultValue(@Nonnull Tab self, @Nonnull String defaultValue) {
        JavaFXUtils.setI18nDefaultValue(self, defaultValue)
    }

    static <T> void setI18nDefaultValue(@Nonnull Axis<T> self, @Nonnull String defaultValue) {
        JavaFXUtils.setI18nDefaultValue(self, defaultValue)
    }

    static <S, T> void setI18nDefaultValue(@Nonnull TableColumn<S, T> self, @Nonnull String defaultValue) {
        JavaFXUtils.setI18nDefaultValue(self, defaultValue)
    }

    static void setI18nArgs(@Nonnull Labeled self, @Nonnull String args) {
        JavaFXUtils.setI18nArgs(self, args)
    }

    static void setI18nArgs(@Nonnull MenuItem self, @Nonnull String args) {
        JavaFXUtils.setI18nArgs(self, args)
    }

    static void setI18nArgs(@Nonnull Tab self, @Nonnull String args) {
        JavaFXUtils.setI18nArgs(self, args)
    }

    static <T> void setI18nArgs(@Nonnull Axis<T> self, @Nonnull String args) {
        JavaFXUtils.setI18nArgs(self, args)
    }

    static <S, T> void setI18nArgs(@Nonnull TableColumn<S, T> self, @Nonnull String args) {
        JavaFXUtils.setI18nArgs(self, args)
    }

    static void setIcon(@Nonnull Labeled self, @Nonnull String iconUrl) {
        JavaFXUtils.setIcon(self, iconUrl)
    }

    static void setIcon(@Nonnull MenuItem self, @Nonnull String iconUrl) {
        JavaFXUtils.setIcon(self, iconUrl)
    }

    static void setGraphic(@Nonnull Labeled self, @Nonnull Image image) {
        JavaFXUtils.setGraphic(self, image)
    }

    static void setGraphic(@Nonnull Labeled self, @Nonnull Node node) {
        JavaFXUtils.setGraphic(self, node)
    }

    static void setGraphic(@Nonnull MenuItem self, @Nonnull Image image) {
        JavaFXUtils.setGraphic(self, image)
    }

    static void setGraphic(@Nonnull MenuItem self, @Nonnull Node node) {
        JavaFXUtils.setGraphic(self, node)
    }

    static void setGraphicStyle(@Nonnull ButtonBase self, @Nonnull String style) {
        JavaFXUtils.setGraphicStyle(self, style)
    }

    static void setGraphicStyle(@Nonnull MenuItem self, @Nonnull String style) {
        JavaFXUtils.setGraphicStyle(self, style)
    }

    static void setGraphicStyleClass(@Nonnull ButtonBase self, @Nonnull String styleClass, boolean remove = false) {
        JavaFXUtils.setGraphicStyleClass(self, styleClass, remove)
    }

    static void setGraphicStyleClass(@Nonnull MenuItem self, @Nonnull String styleClass, boolean remove = false) {
        JavaFXUtils.setGraphicStyleClass(self, styleClass, remove)
    }

    static void setTooltip(@Nonnull Control self, @Nonnull String text) {
        JavaFXUtils.setTooltip(self, text)
    }

    @Nullable
    static Node findNode(@Nonnull Node self, @Nonnull String id) {
        JavaFXUtils.findNode(self, id)
    }
}
