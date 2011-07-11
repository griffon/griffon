package swing;

import org.jdesktop.fuse.InjectedResource;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

class HeaderPanel extends JComponent {
    ////////////////////////////////////////////////////////////////////////////
    // THEME SPECIFIC FIELDS
    ////////////////////////////////////////////////////////////////////////////
    @InjectedResource
    private Color lightColor;
    @InjectedResource
    private Color shadowColor;
    @InjectedResource
    private int preferredHeight;
    @InjectedResource
    private BufferedImage backgroundGradient;

    @Override
    public Dimension getPreferredSize() {
        Dimension size = super.getPreferredSize();
        size.height = preferredHeight;
        return size;
    }
    
    @Override
    public Dimension getMaximumSize() {
        Dimension size = super.getMaximumSize();
        size.height = preferredHeight;
        return size;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        if (!isVisible()) {
            return;
        }
        
        Graphics2D g2 = (Graphics2D) g;
        paintBackground(g2);
    }

    private void paintBackground(final Graphics2D g2) {
        int height = backgroundGradient.getHeight();
        
        Rectangle bounds = g2.getClipBounds();
        g2.drawImage(backgroundGradient,
                     (int) bounds.getX(), 0,
                     (int) bounds.getWidth(), height,
                     null);
        
        g2.setColor(lightColor);
        g2.drawLine(0, height, getWidth(), height);
        
        g2.setColor(shadowColor);
        g2.drawLine(0, height + 1, getWidth(), height + 1);
    }
}