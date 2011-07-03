@artifact.package@import griffon.core.GriffonApplication;
import griffon.core.MVCClosure;
import org.codehaus.griffon.runtime.core.AbstractGriffonController;

public class @artifact.name@ extends AbstractGriffonController {    
    public void newAction() {
    }

    public void openAction() {
    }

    public void saveAction() {
    }

    public void saveAsAction() {
    }

    public void aboutAction() {
        withMVCGroup("about", new MVCClosure<AboutModel, AboutView, DialogController>() {
            public void call(AboutModel m, AboutView v, DialogController c) {
                c.show();
            }
        });
    }

    public void preferencesAction() {
        withMVCGroup("preferences", new MVCClosure<PreferencesModel, PreferencesView, DialogController>() {
            public void call(PreferencesModel m, PreferencesView v, DialogController c) {
                c.show();
            }
        });
    }

    public void quitAction() {
        getApp().shutdown();
    }

    public void undoAction() {
    }

    public void redoAction() {
    }

    public void cutAction() {
    }

    public void copyAction() {
    }

    public void pasteAction() {
    }

    public void deleteAction() {
    }

    public void onOSXAbout(GriffonApplication app) {
        aboutAction();
    }

    public void onOSXQuit(GriffonApplication app) {
        getApp().shutdown();
    }

    public void onOSXPrefs(GriffonApplication app) {
        preferencesAction();
    }
}
