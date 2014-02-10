package sample.swing.java;

import org.codehaus.griffon.runtime.swing.DefaultSwingWindowDisplayHandler;

import javax.annotation.Nonnull;
import java.awt.Window;

import static griffon.swing.support.SwingUtils.centerOnScreen;

public class CenteringWindowDisplayHandler extends DefaultSwingWindowDisplayHandler {
    @Override
    public void show(@Nonnull String name, @Nonnull Window window) {
        centerOnScreen(window);
        super.show(name, window);
    }
}