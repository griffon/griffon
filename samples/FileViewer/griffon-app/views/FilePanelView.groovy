tabbedPane(filesPane, selectedIndex: filesPane.tabCount) {
    scrollPane(title:tabName) {
        textArea(editable:false, text:bind {model.fileText})
    }
}