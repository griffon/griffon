@artifact.package@import javax.swing.KeyStroke

actions {
   action(id: 'newAction',
      name: app.getMessage('application.action.New.name', 'New'),
      closure: controller.newAction,
      mnemonic: app.getMessage('application.action.New.mnemonic', 'N'),
      accelerator: shortcut(app.getMessage('application.action.New.shortcut', 'N')),
      shortDescription: app.getMessage('application.action.New.description', 'New')
   )
   action(id: 'openAction',
      name: app.getMessage('application.action.Open.name', 'Open...'),
      closure: controller.openAction,
      mnemonic: app.getMessage('application.action.Open.mnemonic', 'O'),
      accelerator: shortcut(app.getMessage('application.action.Open.shortcut', 'O')),
      shortDescription: app.getMessage('application.action.Open.description', 'Open')
   )
   action(id: 'quitAction',
      name: app.getMessage('application.action.Quit.name', 'Quit'),
      closure: controller.quitAction,
      mnemonic: app.getMessage('application.action.Quit.mnemonic', 'Q'),
      accelerator: shortcut(app.getMessage('application.action.Quit.shortcut', 'Q')),
      shortDescription: app.getMessage('application.action.Quit.description', 'Quit')
   )
   action(id: 'aboutAction',
      name: app.getMessage('application.action.About.name', 'About'),
      closure: controller.aboutAction,
      mnemonic: app.getMessage('application.action.About.mnemonic', 'B'),
      accelerator: shortcut(app.getMessage('application.action.About.shortcut', 'B')),
      shortDescription: app.getMessage('application.action.About.description', 'About')
   )
   action(id: 'preferencesAction',
      name: app.getMessage('application.action.Preferences.name', 'Preferences'),
      closure: controller.preferencesAction,
      mnemonic: app.getMessage('application.action.Preferences.mnemonic', 'E'),
      accelerator: shortcut(app.getMessage('application.action.Preferences.shortcut', 'E')),
      shortDescription: app.getMessage('application.action.Preferences.description', 'Preferences')
   )

   action(id: 'saveAction',
      name: app.getMessage('application.action.Save.name', 'Save'),
      closure: controller.saveAction,
      mnemonic: app.getMessage('application.action.Save.mnemonic', 'S'),
      accelerator: shortcut(app.getMessage('application.action.Save.shortcut', 'S')),
      shortDescription: app.getMessage('application.action.Save.description', 'Save'),
      enabled: false
   )
   action(id: 'saveAsAction',
      name: app.getMessage('application.action.SaveAs.name', 'Save as...'),
      closure: controller.saveAsAction,
      accelerator: shortcut(app.getMessage('application.action.SaveAs.shortcut', 'shift S')),
      enabled: false
   )

   action(id: 'undoAction',
      name: app.getMessage('application.action.Undo.name', 'Undo'),
      closure: controller.undoAction,
      mnemonic: app.getMessage('application.action.Undo.mnemonic', 'U'),
      accelerator: shortcut(app.getMessage('application.action.Undo.shortcut', 'Z')),
      shortDescription: app.getMessage('application.action.Undo.description', 'Undo'),
      enabled: false
   )
   action(id: 'redoAction',
      name: app.getMessage('application.action.Redo.name', 'Redo'),
      closure: controller.redoAction,
      mnemonic: app.getMessage('application.action.Redo.mnemonic', 'R'),
      accelerator: shortcut(app.getMessage('application.action.Redo.shortcut', 'shift Z')),
      shortDescription: app.getMessage('application.action.Redo.description', 'Redo'),
      enabled: false
   )
   action(id: 'cutAction',
      name: app.getMessage('application.action.Cut.name', 'Cut'),
      closure: controller.cutAction,
      mnemonic: app.getMessage('application.action.Cut.mnemonic', 'T'),
      accelerator: shortcut(app.getMessage('application.action.Cut.shortcut', 'X')),
      shortDescription: app.getMessage('application.action.Cut.description', 'Cut'),
      enabled: false
   )
   action(id: 'copyAction',
      name: app.getMessage('application.action.Copy.name', 'Copy'),
      closure: controller.copyAction,
      mnemonic: app.getMessage('application.action.Copy.mnemonic', 'C'),
      accelerator: shortcut(app.getMessage('application.action.Copy.shortcut', 'C')),
      shortDescription: app.getMessage('application.action.Copy.description', 'Copy'),
      enabled: false
   )
   action(id: 'pasteAction',
      name: app.getMessage('application.action.Paste.name', 'Paste'),
      closure: controller.pasteAction,
      mnemonic: app.getMessage('application.action.Paste.mnemonic', 'P'),
      accelerator: shortcut(app.getMessage('application.action.Paste.shortcut', 'V')),
      shortDescription: app.getMessage('application.action.Paste.description', 'Paste'),
      enabled: false
   )
   action(id: 'deleteAction',
      name: app.getMessage('application.action.Delete.name', 'Delete'),
      closure: controller.deleteAction,
      mnemonic: app.getMessage('application.action.Delete.mnemonic', 'D'),
      accelerator: KeyStroke.getKeyStroke(app.getMessage('application.action.Delete.shortcut', 'DELETE')),
      shortDescription: app.getMessage('application.action.Delete.description', 'Delete'),
      enabled: false
   )
}
