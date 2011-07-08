@artifact.package@import net.miginfocom.swing.MigLayout;
import java.awt.*;
import javax.swing.*;

public class @artifact.name.plain@Content {
    public static JComponent widget() {
        MigLayout layout = new MigLayout();
        layout.setLayoutConstraints("fill");
        JPanel panel = new JPanel(layout);

        panel.add(new JLabel("Your new application has been created!"), "center, wrap");
        panel.add(new JLabel(new ImageIcon(getImage("/griffon-icon-256x256.png"))), "center");

        return panel;
    }

    private static Image getImage(String path) {
        return Toolkit.getDefaultToolkit().getImage(@artifact.name.plain@Content.class.getResource(path));
    }
}
