@artifact.package@abstract class AbstractDialogModel {
    @Bindable String title
    @Bindable int width = 0
    @Bindable int height = 0
    @Bindable boolean resizable = true
    @Bindable boolean modal = true

    protected abstract String getDialogKey()
    protected abstract String getDialogTitle()

    void mvcGroupInit(Map<String, Object> args) {
        title = GriffonNameUtils.capitalize(app.getMessage('application.dialog.'+dialogKey+'.title', dialogTitle))
    }
}
