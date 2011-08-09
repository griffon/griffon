@artifact.package@import java.awt.Window;
import java.awt.event.ActionEvent;
import org.codehaus.griffon.runtime.core.AbstractGriffonController;

public class DialogController extends AbstractGriffonController {
    private AbstractDialogView view;

    public void setView(AbstractDialogView view) {
        this.view = view;
    }

    public void show() {
        show((Window) null);
    }

    public void show(ActionEvent event) {
        show((Window) null);
    }

    public void show(Window window) {
        view.show(window);
    }

    public void hide() {
        hide(null);
    }

    public void hide(ActionEvent event) {
        view.hide();
    }
}
