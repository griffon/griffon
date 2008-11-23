openAction = action(closure: controller.openFile, name:"Open")
fileViewerFrame = application(title:'File Viewer', size:[500, 300],
    locationByPlatform:true)
{
    borderLayout()
	hbox(constraints:NORTH) {
        textField(columns:20, action:openAction,
            text: bind('fileName', target:model, id:'textBinding'))
        button("...", actionPerformed:controller.browse)
        button(openAction)
    }
    filesPane = tabbedPane(constraints:CENTER)
}
fileChooserWindow = fileChooser()