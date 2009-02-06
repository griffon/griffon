package greet;

import javax.swing.*;
import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: danno.ferrin
 * Date: Jan 7, 2009
 * Time: 9:12:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class FixedSizeImageIcon extends ImageIcon {
    final int w;
    final int h;

    public FixedSizeImageIcon(int h, int w, URL url) {
        super(url);
        this.w = w;
        this.h = h;
    }

    @Override
    public int getIconWidth() {
        return w;
    }

    @Override
    public int getIconHeight() {
        return h;
    }
}
