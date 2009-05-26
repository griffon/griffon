/*
 * Copyright 2007-2008 the original author or authors.
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
 */

import static griffon.util.GriffonApplicationUtils.*

import java.awt.Color
import javax.swing.JComponent
import org.codehaus.groovy.runtime.InvokerHelper
import javax.swing.text.Style
import javax.swing.text.StyleContext
import javax.swing.text.StyleConstants
import javax.swing.text.StyledDocument

import groovy.ui.text.GroovyFilter

styles = [
    // output window styles
    regular: [
            (StyleConstants.FontFamily): "Monospaced",
        ],
    prompt: [
            (StyleConstants.Foreground): new Color(0, 128, 0),
        ],
    command: [
            (StyleConstants.Foreground): Color.BLUE,
        ],
    output: [:],
    result: [
            (StyleConstants.Foreground): Color.BLUE,
            (StyleConstants.Background): Color.YELLOW,
        ],

    // syntax highlighting styles
    (StyleContext.DEFAULT_STYLE) : [
            (StyleConstants.FontFamily): "Monospaced",
        ],
    (GroovyFilter.COMMENT): [
            (StyleConstants.Foreground): Color.LIGHT_GRAY.darker().darker(),
            (StyleConstants.Italic) : true,
        ],
    (GroovyFilter.QUOTES): [
            (StyleConstants.Foreground): Color.MAGENTA.darker().darker(),
        ],
    (GroovyFilter.SINGLE_QUOTES): [
            (StyleConstants.Foreground): Color.GREEN.darker().darker(),
        ],
    (GroovyFilter.SLASHY_QUOTES): [
            (StyleConstants.Foreground): Color.ORANGE.darker(),
        ],
    (GroovyFilter.DIGIT): [
            (StyleConstants.Foreground): Color.RED.darker(),
        ],
    (GroovyFilter.OPERATION): [
            (StyleConstants.Bold): true,
        ],
    (GroovyFilter.IDENT): [:],
    (GroovyFilter.RESERVED_WORD): [
        (StyleConstants.Bold): true,
        (StyleConstants.Foreground): Color.BLUE.darker().darker(),
    ],
]

if( isWindowsVista ) {
    styles.regular[StyleConstants.FontFamily] = 'Consolas'
    styles[StyleContext.DEFAULT_STYLE][StyleConstants.FontFamily] = 'Consolas'

    // in JDK 1.5 we need to turn on anti-aliasing so consolas looks better
    if( isJdk15 && !isJdk16 ) {
        key = InvokerHelper.getProperty('com.sun.java.swing.SwingUtilities2' as Class,
            'AA_TEXT_PROPERTY_KEY')
        addAttributeDelegate { builder, node, attributes ->
            if( node instanceof JComponent ) {
                node.putClientProperty(key, true)
            }
        }
    }
} else if( isLinux ) {
    // change font to DejaVu Sans Mono, much clearer
    styles.regular[StyleConstants.FontFamily] = 'DejaVu Sans Mono'
    styles[StyleContext.DEFAULT_STYLE][StyleConstants.FontFamily] = 'DejaVu Sans Mono'

    // possibly change look and feel
    if( isJdk15 && !isJdk16 ) {
        // GTK wasn't where it needed to be in 1.5, especially with toolbars
        // use metal instead
        lookAndFeel('metal', boldFonts:false)

        // we also need to turn on anti-alising ourselves
        key = InvokerHelper.getProperty('com.sun.java.swing.SwingUtilities2' as Class,
                'AA_TEXT_PROPERTY_KEY')
        addAttributeDelegate { builder, node, attributes ->
            if (node instanceof JComponent) {
                node.putClientProperty(key, true)
            }
        }
    }
} else if( isMacOSX ) {
    // redo output styles
    styles = [
        // output window styles
        regular: [
                (StyleConstants.FontFamily): "Monaco",
            ],
        prompt: [
                (StyleConstants.Foreground): Color.LIGHT_GRAY,
            ],
        command: [
                (StyleConstants.Foreground): Color.GRAY,
            ],
        output: [:],
        result: [
                (StyleConstants.Foreground): Color.WHITE,
                (StyleConstants.Background): Color.BLACK,
            ],

        // syntax highlighting styles
        (GroovyFilter.COMMENT): [
                (StyleConstants.Foreground): Color.LIGHT_GRAY.darker().darker(),
                (StyleConstants.Italic) : true,
            ],
        (GroovyFilter.QUOTES): [
                (StyleConstants.Foreground): Color.MAGENTA.darker().darker(),
            ],
        (GroovyFilter.SINGLE_QUOTES): [
                (StyleConstants.Foreground): Color.GREEN.darker().darker(),
            ],
        (GroovyFilter.SLASHY_QUOTES): [
                (StyleConstants.Foreground): Color.ORANGE.darker(),
            ],
        (GroovyFilter.DIGIT): [
                (StyleConstants.Foreground): Color.RED.darker(),
            ],
        (GroovyFilter.OPERATION): [
                (StyleConstants.Bold): true,
            ],
        (GroovyFilter.IDENT): [:],
        (GroovyFilter.RESERVED_WORD): [
            (StyleConstants.Bold): true,
            (StyleConstants.Foreground): Color.BLUE.darker().darker(),
        ],
    ]
}

inputArea = editor.textEditor
def applyStyle = {Style style, values -> values.each{k, v -> style.addAttribute(k, v)}}

// redo styles for editor
doc = inputArea.getStyledDocument()
StyleContext styleContext = StyleContext.getDefaultStyleContext()
styles.each {styleName, defs ->
    Style style = styleContext.getStyle(styleName)
    if (style) {
        applyStyle(style, defs)
    }
}

inputArea.background = Color.WHITE
