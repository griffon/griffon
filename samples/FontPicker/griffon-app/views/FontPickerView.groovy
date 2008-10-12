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
import net.miginfocom.swing.MigLayout 
application(title:'FontPicker', size:[850,480], locationByPlatform:true, layout:new MigLayout()) {
    scrollPane (constraints:'w 100%'){
        panel(id:'pane', layout:new MigLayout())
    }
    panel(layout:new MigLayout(), constraints:'newline') {
        panel(border:titledBorder(title:'Font Size', position:'belowTop'), constraints:'h 100%') {
            slider(id:'fontSize',minimum:0, maximum:96, value:bind(target:model, targetProperty:'fontSize', converter:{(Float)it}), paintTicks:true, majorTickSpacing:10, paintLabels:true)
            button(text:'Refresh', actionPerformed:{controller.changeStyle()})
        }
        panel(border:titledBorder(title:'Style', position:'belowTop'), constraints:'h 100%') {
            toggleButton(text:'BOLD', selected:bind(target:model, targetProperty:'bold'), actionPerformed:{controller.changeStyle()})
            toggleButton(id:'italic',text:'ITALIC', selected:bind(target:model, targetProperty:'italic'),actionPerformed:{controller.changeStyle()})
        }
        panel(border:titledBorder(title:'Sample Text', position:'belowTop'), constraints:'h 100%') {
            textField(id:'textField',text:bind(target:model, targetProperty:'sampleText'), columns:25, constraints:'newline')
        }
    }
}
