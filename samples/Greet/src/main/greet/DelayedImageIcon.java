package greet;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: danno.ferrin
 * Date: Jan 7, 2009
 * Time: 9:12:17 PM
 */
public class DelayedImageIcon implements Icon {

    protected final static Component component = new Component() {};
    protected final static MediaTracker tracker = new MediaTracker(component);
    private static int mediaTrackerID;
    private static Set<Component> refreshSet = new HashSet();

    final int w;
    final int h;
    Image image;
    int imageID;

    

    public DelayedImageIcon(int h, int w, URL url) {
        this.w = w;
        this.h = h;
        image = Toolkit.getDefaultToolkit().getImage(url);
        tracker.addImage(image, imageID = mediaTrackerID++, w, h);
    }

    public void paintIcon(final Component c, Graphics g, int x, int y) {
        int status;
        final int localImageID = imageID;
        if (localImageID  > 0) {
            switch (status = tracker.statusID(localImageID, true)) {
                case MediaTracker.ERRORED:
                case MediaTracker.ABORTED:
                    image = null;
                    // fall through
                case MediaTracker.COMPLETE:
                    imageID = -1;
                    break;
                default:
                    // nothing
                    break;
            }
            if (!refreshSet.contains(c) && imageID > 0) {
                refreshSet.add(c);
                new Thread(new Runnable() { public void run() {
                    try {
                        tracker.waitForID(localImageID);
                        imageID = -1;
                        refreshSet.remove(c);
                        c.repaint(100);
                    } catch (InterruptedException ignore) { }
                }}).start();

            }
        } else {
            status = image == null ? MediaTracker.ERRORED : MediaTracker.COMPLETE;
        }

        if (status == MediaTracker.LOADING) {
            Color oldC = g.getColor();
            g.setColor(Color.DARK_GRAY);
            g.drawRect(x, y, w, h);
            g.setColor(Color.LIGHT_GRAY);
            g.drawRect(x+1, y+1, w-2, h-2);
            g.setColor(oldC);
        } else if (image == null) {
            Color oldC = g.getColor();
            g.setColor(Color.RED);
            g.drawRect(x, y, w, h);
            g.setColor(Color.PINK);
            g.drawRect(x+1, y+1, w-2, h-2);
            g.setColor(oldC);
        } else {
            g.drawImage(image, x, y, component);
            }

    }

    public int getIconWidth() {
        return w;
    }

    public int getIconHeight() {
        return h;
    }
}
