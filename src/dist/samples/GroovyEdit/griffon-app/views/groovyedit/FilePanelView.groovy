package groovyedit

tabbedPane(tabGroup, selectedIndex: tabGroup.tabCount) {
   scrollPane(title: tabName, id: 'tab', clientProperties: [mvcIdentifier: mvcIdentifier]) {
      textArea(id: 'editor', text: bind {model.document.contents})
   }
}

bean(model.document, dirty: bind {editor.text != model.document.contents})
