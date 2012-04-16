/*
 * Copyright 2007-2012 the original author or authors.
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

/**
 * @author Andres Almiray
 */

package griffon.samples.swingpad

import org.fife.ui.autocomplete.AutoCompletion
import org.fife.ui.rsyntaxtextarea.SyntaxConstants

rowHeader = new ScrollPaneRuler(ScrollPaneRuler.VERTICAL)
columnHeader = new ScrollPaneRuler(ScrollPaneRuler.HORIZONTAL)
emptyRowHeader = label('')
emptyColumnHeader = label('')

splitPane(id: 'mainContent', resizeWeight: 0.45f, border: emptyBorder(0),
        orientation: bind('layout', source: model, converter: {it ? JSplitPane.HORIZONTAL_SPLIT : JSplitPane.VERTICAL_SPLIT})) {
    tabbedPane(id: 'tabs') {
        panel(title: 'Source ', id: 'sourceTab', tabIcon: silkIcon('script_code')) {
            borderLayout()
            rtextScrollPane(id: 'codeEditorContainer') {
                rsyntaxTextArea(id: 'codeEditor',
                        syntaxEditingStyle: SyntaxConstants.SYNTAX_STYLE_GROOVY,
                        tabSize: 4,
                        text: bind('code', source: model, mutual: true),
                        cssClass: 'codeEditor') {
                    action(runScriptAction)
                }
                noparent {
                    model.font = codeEditor.font
                    bean(codeEditor, font: bind {model.font})
                    bean(new AutoCompletion(model.codeCompletionProvider),
                            triggerKey: shortcut('shift SPACE'),
                            showDescWindow: false,
                            autoCompleteSingleChoices: true
                    ).install(codeEditor)
                }
            }
        }
        panel(title: 'Styles ', id: 'styleTab', tabIcon: silkIcon('script_palette')) {
            borderLayout()
            rtextScrollPane(id: 'cssEditorContainer') {
                rsyntaxTextArea(id: 'cssEditor',
                        syntaxEditingStyle: SyntaxConstants.SYNTAX_STYLE_CSS,
                        tabSize: 4,
                        font: bind {model.font},
                        text: bind('stylesheet', source: model, mutual: true),
                        cssClass: 'cssEditor') {
                    action(runScriptAction)
                }
            }
        }
        panel(title: 'Errors ', id: 'errorsTab', tabIcon: silkIcon('cancel')) {
            borderLayout()
            scrollPane(border: emptyBorder(0)) {
                textArea(id: 'errors', border: emptyBorder(0),
                        background: Color.WHITE, editable: false,
                        font: bind {model.font},
                        caretPosition: bind('errors', source: model, converter: {0i}),
                        text: bind {model.errors})
            }
        }
    }
    scrollPane(id: 'scroller',
            rowHeaderView: rowHeader, columnHeaderView: columnHeader) {
        panel(id: 'canvas', border: emptyBorder(0)) {
            flowLayout(alignment: FlowLayout.LEFT, hgap: 0, vgap: 0)
        }
    }
}

def screen = Toolkit.defaultToolkit.screenSize
rowHeader.opaque = true
rowHeader.preferredSize = [20, screen.width as int]
columnHeader.opaque = true
columnHeader.preferredSize = [screen.height as int, 20]
canvas.addMouseListener(rowHeader)
canvas.addMouseMotionListener(rowHeader)
canvas.addMouseListener(columnHeader)
canvas.addMouseMotionListener(columnHeader)

def caretListener = { evt ->
    def rootElement = evt.source.document.defaultRootElement
    def cursorPos = evt.source.caretPosition
    def rowNum = rootElement.getElementIndex(cursorPos) + 1
    def rowElement = rootElement.getElement(rowNum - 1)
    def colNum = cursorPos - rowElement.startOffset + 1
    model.rowAndCol = "$rowNum:$colNum"
}

codeEditor.addCaretListener(caretListener as CaretListener)
cssEditor.addCaretListener(caretListener as CaretListener)

bean(model, dirty: bind {codeEditor.text?.size() > 0})

// return the widget so that it can be embedded by parent script
mainContent