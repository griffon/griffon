package swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;

import javax.swing.JComponent;

import org.jdesktop.fuse.InjectedResource;

class FooterPanel extends JComponent {
    ////////////////////////////////////////////////////////////////////////////
    // THEME SPECIFIC FIELDS
    ////////////////////////////////////////////////////////////////////////////
    @InjectedResource
    private GradientPaint backgroundGradient;
    @InjectedResource
    private int preferredHeight;
    @InjectedResource(key="Common.lightColor")
    private Color lightColor;
    @InjectedResource(key="Common.darkColor")
    private Color shadowColor;

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

        Paint paint = g2.getPaint();
        g2.setPaint(backgroundGradient);
        Rectangle clip = g2.getClipBounds();
        clip = clip.intersection(new Rectangle(0, 2, getWidth(), getHeight()));
        g2.fillRect(clip.x, clip.y, clip.width, clip.height);
        g2.setPaint(paint);
        
        g2.setColor(lightColor);
        g2.drawLine(0, 0, getWidth(), 0);
        
        g2.setColor(shadowColor);
        g2.drawLine(0, 1, getWidth(), 1);
    }
}
