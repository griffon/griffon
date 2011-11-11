package mdi

class WindowController {
    private String groupId
    def view

    void mvcGroupInit(Map args) {
        view.window.putClientProperty('groupId', groupId = args.mvcName)
        app.windowManager.attach(view.window)
    }

    def onWindowHidden = { window ->
        if(groupId == window.getClientProperty('groupId')) {
            app.windowManager.detach(window)
            execOutside { destroyMVCGroup(groupId) }
        }
    }
}
