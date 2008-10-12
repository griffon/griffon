/*
 * Copyright 2008 the original author or authors.
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
 * limitations under the License.
 */
 import java.awt.Font
 import javax.swing.JPanel
 import java.awt.GraphicsEnvironment
 import groovy.ui.*
 import net.miginfocom.swing.MigLayout
 
class FontPickerController {

    // these will be injected by Griffon
    def model
    def view
    
    public JPanel createFontPanel(Font font) {
        if (model.bold)
            font = font.deriveFont(Font.BOLD)
        if (model.italic)
            font = font.deriveFont(Font.ITALIC)
        if(model.italic && model.bold)
            font = font.deriveFont(Font.BOLD + Font.ITALIC)
        font = font.deriveFont((Float) view.fontSize.getValue())
        app.builders.root.panel(layout:new MigLayout()) {
            label(font:font,text:bind(source:model, sourceProperty:'sampleText'), constraints:'wrap')
            label(text:font.getFontName(), constraints:'wrap')     
        }   
    }
    
    public void generateFonts() {
        def ge = GraphicsEnvironment.getLocalGraphicsEnvironment()
        def fonts = ge.getAllFonts()
        for (f in fonts) {
            view.pane.add(createFontPanel(f), 'wrap')
        }
        view.pane.validate()
    }
    
    public void changeStyle() { 
        doLater {
        view.pane.removeAll()
        generateFonts()
        }
    }
}
