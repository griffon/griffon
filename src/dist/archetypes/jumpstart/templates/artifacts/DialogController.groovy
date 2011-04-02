@artifact.package@import java.awt.Window

class DialogController {
    def model
    def view
    def builder
    
    private dialog

    @Threading(Threading.Policy.INSIDE_UITHREAD_SYNC)
    def show = { Window window = null ->
        if(!dialog) {
            window = window ?: Window.windows.find{it.focused}
            dialog = builder.dialog(
                owner: window,
                title: model.title,
                size: [model.width, model.height],
                resizable: model.resizable,
                modal: true) {
                container(view.content)        
            }
            if(model.width == 0 || model.height == 0) dialog.pack()
            int x = window.x + (window.width - dialog.width) / 2
            int y = window.y + (window.height - dialog.height) / 2
            dialog.setLocation(x, y)
        }
        dialog.visible = true
    }

    @Threading(Threading.Policy.INSIDE_UITHREAD_SYNC)
    def hide = { evt = null ->
        dialog?.visible = false
        dialog?.dispose()
        dialog = null
    }
}
