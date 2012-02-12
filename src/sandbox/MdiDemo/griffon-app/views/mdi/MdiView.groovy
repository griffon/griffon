package mdi

application(title: 'mdi',
  preferredSize: [640, 480],
  pack: true,
  locationByPlatform:true,
  iconImage: imageIcon('/griffon-icon-48x48.png').image,
  iconImages: [imageIcon('/griffon-icon-48x48.png').image,
               imageIcon('/griffon-icon-32x32.png').image,
               imageIcon('/griffon-icon-16x16.png').image]) {
    menuBar {
        menu('Window') {
            menuItem(newAction)
            menuItem(closeAction)
            separator()
            menu('Windows', id: 'windowsMenu')
        }
    }
    
    borderLayout()
    desktopPane(id: 'desktop', constraints: CENTER)
}

update = {
    windowsMenu.removeAll()
    noparent {
        container(windowsMenu) {
            int index = 0
            desktop.allFrames.sort { it.title }.each { frame ->
                if(index > 9) return
                noparent{
                    trigger = action(
                        name: frame.title,
                        accelerator: shortcut((index++).toString()),
                        closure: { f, e -> f.selected = true }.curry(frame))
                }
                menuItem(trigger)
            }
        }
    }
}

desktop.addContainerListener(new ContainerListener() {
    void componentAdded(ContainerEvent e) {
        update()
    }
    void componentRemoved(ContainerEvent e) {
        update()
    }
})