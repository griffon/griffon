package org.codehaus.griffon.console

import groovy.beans.Bindable
import java.awt.Color
import java.awt.Font
import java.beans.Introspector

class TextEditorPreferencesModel {

    @Bindable boolean autoIndentEnabled

    @Bindable def background // java.awt.Color or java.awt.Image

    @Bindable boolean bracketMatchingEnabled

    @Bindable boolean clearWhitespaceLinesEnabled

    @Bindable Color currentLineHighlightColor

    @Bindable boolean currentLineHighlightEnabled

    @Bindable boolean fadeCurrentLineHighlight

    @Bindable Font font

    @Bindable boolean fractionalFontMetricsEnabled

    @Bindable boolean lineWrap

    @Bindable Color marginLineColor

    @Bindable boolean marginLineEnabled

    @Bindable int marginLinePosition

    @Bindable Color matchedBracketBGColor

    @Bindable Color matchedBracketBorderColor

    //@Bindable SyntaxHighlightingColorScheme syntaxHighlightingColorScheme

    @Bindable boolean tabsEmulated

    @Bindable int tabSize

    //@Bindable File templateDirectory

    @Bindable boolean templatesEnabled

    @Bindable boolean whitespaceVisible

    static props = Introspector.getBeanInfo(TextEditorPreferencesModel, TextEditorPreferencesModel.superclass)\
                    .propertyDescriptors.collect {it.name} - ['metaClass', 'propertyChangeListeners', 'props']

    void applyToObject(def target) {
        props.each {
            try {
                target[it] = this[it]
            } catch (MissingPropertyException mpe) {
                mpe.printStackTrace(); //TODO ignore
            }
        }
    }

    void readFromObject(def target) {
        props.each {
            try {
                this[it] = target[it]
            } catch (MissingPropertyException mpe) {
                mpe.printStackTrace(); //TODO ignore
            }
        }
    }


}