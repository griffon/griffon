@artifact.package@import static griffon.util.GriffonApplicationUtils.*

menuBar = menuBar {
    menu(text: app.getMessage('application.menu.File.name',' File'), 
         mnemonic: app.getMessage('application.menu.File.mnemonic', 'F')) {
        menuItem(newAction)
        menuItem(openAction)
        separator()
        menuItem(saveAction)
        menuItem(saveAsAction)
        if(!isMacOSX) {
            separator()
            menuItem(quitAction)
        }
    }
 
    menu(text: app.getMessage('application.menu.Edit.name', 'Edit'), 
         mnemonic: app.getMessage('application.menu.Edit.mnemonic', 'E')) {   
        menuItem(undoAction)
        menuItem(redoAction)
        separator()
        menuItem(cutAction)
        menuItem(copyAction)
        menuItem(pasteAction)
        menuItem(deleteAction)
    }
 
    menu(text: app.getMessage('application.menu.View.name', 'View'), 
         mnemonic: app.getMessage('application.menu.View.mnemonic', 'V')) {
    }
 
    if(!isMacOSX) glue()  
    menu(text: app.getMessage('application.menu.Help.name', 'Help'), 
         mnemonic: app.getMessage('application.menu.Help.mnemonic', 'H')) {
        if(!isMacOSX) {
            menuItem(aboutAction)
            menuItem(preferencesAction)
        }
    }
}
