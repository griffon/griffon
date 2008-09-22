import afdemo.FormPanelFactory

features {
   factories = [
      "AbeilleForms": [
         formPanel: new FormPanelFactory()
      ]
   ]
}

root {
    'groovy.swing.SwingBuilder' {
        controller = ['Threading']
        view = "*"
    }
    'griffon.app.ApplicationBuilder' {
        view = "*"
    }
}
