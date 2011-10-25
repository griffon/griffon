@artifact.package@import java.awt.event.ActionEvent;
import org.codehaus.griffon.runtime.core.AbstractGriffonController;

public class @artifact.name@ extends AbstractGriffonController {
    // these will be injected by Griffon
    private @artifact.name.plain@Model model;
    private @artifact.name.plain@View view;

    public void setModel(@artifact.name.plain@Model model) {
        this.model = model;
    }

    public void setView(@artifact.name.plain@View view) {
        this.view = view;
    }

    /*
        Remember to use proper threading when dealing with
        long computations.
        Please read chapter 9 of the Griffon Guide to know more.

    public void action(final ActionEvent e) {
        execOutside(new Runnable() {
            public void run() {
                // action code
            }
        });
    }
    */
}
