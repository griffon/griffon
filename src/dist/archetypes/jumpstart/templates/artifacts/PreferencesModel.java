@artifact.package@import java.util.Map;

public class PreferencesModel extends AbstractDialogModel {
    public void mvcGroupInit(Map<String, Object> args) {
        super.mvcGroupInit(args);
        setWidth(600);
        setHeight(320);
        setResizable(false);
    }

    protected String getDialogKey() {
        return "Preferences";
    }

    protected String getDialogTitle() {
        return "Preferences";
    }
}
