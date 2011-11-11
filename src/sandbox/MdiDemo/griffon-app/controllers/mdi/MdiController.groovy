package mdi

import griffon.transform.Threading

class MdiController {
    def model
    def view
    def builder

    def newAction = {
        String id = 'window-' + System.currentTimeMillis()
        def (m, v, c) = createMVCGroup('window', id, title: "Window ${model.count++}")
        execAsync {
            builder.desktopPane(view.desktop) {
                widget(v.window)
            }
            app.windowManager.show(v.window)
        }  
    }
 
    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)   
    def close = {
        app.windowManager.hide(view.desktop.selectedFrame)
    }
}