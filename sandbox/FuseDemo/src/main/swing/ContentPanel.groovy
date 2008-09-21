package swing;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import org.jdesktop.fuse.InjectedResource;

class ContentPanel extends JPanel {
    ////////////////////////////////////////////////////////////////////////////
    // THEME SPECIFIC FIELDS
    ////////////////////////////////////////////////////////////////////////////
    @InjectedResource
    private GradientPaint backgroundGradient
    @InjectedResource
    private BufferedImage light
    @InjectedResource
    private float lightOpacity

    @Override
    protected void paintComponent(Graphics g) {
        if (!isVisible()) {
            return;
        }
        
        Graphics2D g2 = (Graphics2D) g;
        
        Composite composite = g2.getComposite();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                            RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setPaint(backgroundGradient);
        Rectangle bounds = g2.getClipBounds();
        g2.fillRect((int)bounds.x, (int)bounds.y, (int)bounds.width, (int)bounds.height);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                         lightOpacity));
        g2.drawImage(light, 0, 0, getWidth(), light.getHeight(), null);
        g2.setComposite(composite);
    }
}
