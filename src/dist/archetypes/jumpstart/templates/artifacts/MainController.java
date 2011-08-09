@artifact.package@import griffon.core.GriffonApplication;
import griffon.core.MVCClosure;
import java.awt.event.ActionEvent;
import org.codehaus.griffon.runtime.core.AbstractGriffonController;

public class @artifact.name@ extends AbstractGriffonController {    
    public void newAction(ActionEvent event) {
    }

    public void openAction(ActionEvent event) {
    }

    public void saveAction(ActionEvent event) {
    }

    public void saveAsAction(ActionEvent event) {
    }

    public void aboutAction(ActionEvent event) {
        withMVCGroup("about", new MVCClosure<AboutModel, AboutView, DialogController>() {
            public void call(AboutModel m, AboutView v, DialogController c) {
                c.show();
            }
        });
    }

    public void preferencesAction(ActionEvent event) {
        withMVCGroup("preferences", new MVCClosure<PreferencesModel, PreferencesView, DialogController>() {
            public void call(PreferencesModel m, PreferencesView v, DialogController c) {
                c.show();
            }
        });
    }

    public void quitAction(ActionEvent event) {
        getApp().shutdown();
    }

    public void undoAction(ActionEvent event) {
    }

    public void redoAction(ActionEvent event) {
    }

    public void cutAction(ActionEvent event) {
    }

    public void copyAction(ActionEvent event) {
    }

    public void pasteAction(ActionEvent event) {
    }

    public void deleteAction(ActionEvent event) {
    }

    public void onOSXAbout(GriffonApplication app) {
        aboutAction(null);
    }

    public void onOSXQuit(GriffonApplication app) {
        quitAction(null);
    }

    public void onOSXPrefs(GriffonApplication app) {
        preferencesAction(null);
    }
}
