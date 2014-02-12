/*
 * Copyright 2009-2014 the original author or authors.
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

/**
 * @author Danno Ferrin
 */

// create instance of view object
dialog(new PrefsPanel(parent, false), id:'prefsPanel', show:true, windowClosing:controller.doCancel)

noparent {
    // javax.swing.JButton buttonCancel declared in PrefsPanel
    bean(prefsPanel.buttonCancel, id:'buttonCancel', actionPerformed:controller.doCancel)

    // javax.swing.JButton buttonOK declared in PrefsPanel
    bean(prefsPanel.buttonOK, id:'buttonOK', actionPerformed: controller.doOK)

    // javax.swing.JRadioButton rbCelcius declared in PrefsPanel
    bean(prefsPanel.rbCelcius, id:'rbCelcius', selected: bind(source:model, 'isCelsius', mutual:true))

    // javax.swing.JRadioButton rbFarenheit declared in PrefsPanel
    bean(prefsPanel.rbFahrenheit, id:'rbFahrenheit', selected: bind(source:model, 'isFahrenheit', mutual:true))

    // javax.swing.JTextField tfLocation declared in PrefsPanel
    bean(prefsPanel.tfLocation, id:'tfLocation', text: bind(source:model, 'location', mutual:true))
}
