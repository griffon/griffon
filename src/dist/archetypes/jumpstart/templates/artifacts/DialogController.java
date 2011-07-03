@artifact.package@import java.awt.Window;
import org.codehaus.griffon.runtime.core.AbstractGriffonController;

public class DialogController extends AbstractGriffonController {
    private AbstractDialogView view;

    public void setView(AbstractDialogView view) {
        this.view = view;
    }

    public void show() {
        show(null);
    }

    public void show(Window window) {
        view.show(window);
    }

    public void hide() {
        view.hide();
    }
}
