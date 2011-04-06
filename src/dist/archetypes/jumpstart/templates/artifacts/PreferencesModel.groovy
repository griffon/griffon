@artifact.package@class PreferencesModel extends AbstractDialogModel {
    protected String getDialogKey() { 'Preferences' }
    protected String getDialogTitle() { 'Preferences' }

    void mvcGroupInit(Map<String, Object> args) {
        super.mvcGroupInit(args)
        resizable = true
        width = 320
        height = 240
    }
}
