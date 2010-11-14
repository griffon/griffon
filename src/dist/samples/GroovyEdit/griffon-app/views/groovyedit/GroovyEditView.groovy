package groovyedit

import static griffon.util.GriffonApplicationUtils.isMacOSX

actions {
   action(id: 'openAction',
      name: 'Open',
      mnemonic: 'O',
      accelerator: shortcut('O'),
      closure: controller.openFile)
   action(id: 'saveAction',
      enabled: bind {model.documentProxy.dirty},
      name: 'Save',
      mnemonic: 'S',
      accelerator: shortcut('S'),
      closure: controller.saveFile)
   action(id: 'closeAction',
      name: 'Close',
      mnemonic: 'W',
      accelerator: shortcut('W'),
      closure: controller.closeFile)
   action(id: 'quitAction',
      name: 'Quit',
      mnemonic: 'Q',
      accelerator: shortcut('Q'),
      closure: controller.quit)
}

fileChooserWindow = fileChooser()
fileViewerWindow = application(title:'GroovyEdit', size:[480,320], locationByPlatform:true,
  iconImage: imageIcon('/griffon-icon-48x48.png').image,
  iconImages: [imageIcon('/griffon-icon-48x48.png').image,
               imageIcon('/griffon-icon-32x32.png').image,
               imageIcon('/griffon-icon-16x16.png').image]) {
   menuBar {
      menu('File') {
         menuItem openAction
         menuItem closeAction
         separator()
         menuItem saveAction
         if(!isMacOSX) {
            separator()
            menuItem quitAction
         }
      }
   }

   borderLayout()
   tabbedPane id: 'tabGroup', constraints: CENTER
   noparent {
      tabGroup.addChangeListener(model)
   }
}
