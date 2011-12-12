@artifact.package@import griffon.swing.BindUtils;
import griffon.util.ApplicationHolder;
import groovy.util.FactoryBuilderSupport;
import javax.swing.*;

public class @artifact.name.plain@StatusBar {
    public static JComponent statusBar() {
        Box vbox = Box.createVerticalBox();

        vbox.add(new JSeparator());
        JLabel label = new JLabel();
        BindUtils.binding()
                .withSource(model())
                .withSourceProperty("status")
                .withTarget(label)
                .withTargetProperty("text")
                .make(builder());
        builder().setVariable("status", label);
        vbox.add(label);

        return vbox;
    }

    private static @artifact.name.plain@Model model() {
        return (@artifact.name.plain@Model) ApplicationHolder.getApplication().getMvcGroupManager().findGroup("@griffon.project.name@").getModel();
    }

    private static FactoryBuilderSupport builder() {
        return ApplicationHolder.getApplication().getMvcGroupManager().findGroup("@griffon.project.name@").getBuilder();
    }
}
