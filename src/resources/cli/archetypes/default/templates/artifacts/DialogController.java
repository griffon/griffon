@artifact.package@import java.awt.Window;
import org.codehaus.griffon.runtime.core.AbstractGriffonController;

public class @artifact.name@ extends AbstractGriffonController {
    private @artifact.name.plain@View view;

    public void setView(@artifact.name.plain@View view) {
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
