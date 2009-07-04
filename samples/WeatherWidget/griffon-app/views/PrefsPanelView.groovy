// create instance of view object
frame(new PrefsPanel(), id:'prefsPanel', show:true)


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

