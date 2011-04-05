@artifact.package@class PreferencesModel extends AbstractDialogModel {
    protected String getDialogKey() { 'Preferences' }
    protected String getDialogTitle() { 'Preferences' }

    void mvcGroupInit(Map<String, Object> args) {
        resizable = true
        width = 320
        height = 240
    }
}
