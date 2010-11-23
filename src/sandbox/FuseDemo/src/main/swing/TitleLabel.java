/**
 * Copyright (c) 2006, Sun Microsystems, Inc
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 *   * Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above
 *     copyright notice, this list of conditions and the following 
 *     disclaimer in the documentation and/or other materials provided 
 *     with the distribution.
 *   * Neither the name of the Fuse project nor the names of its
 *     contributors may be used to endorse or promote products derived 
 *     from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package swing;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;

import org.jdesktop.fuse.InjectedResource;

public class TitleLabel extends JComponent {
    private String text;
    
    ////////////////////////////////////////////////////////////////////////////
    // THEME SPECIFIC FIELDS
    ////////////////////////////////////////////////////////////////////////////
    @InjectedResource
    private float shadowOpacity;
    @InjectedResource
    private Color shadowColor;
    @InjectedResource
    private int shadowDistance;
    @InjectedResource
    private int shadowDirection;
    @InjectedResource
    private Font titleFont;
    @InjectedResource
    private Color titleColor;

    public TitleLabel() {
        setOpaque(false);
    }

    public TitleLabel(String text) {
        setOpaque(false);
        this.text = text;
    }

    public void setText( String text ) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        setupGraphics(g2);
     
        FontRenderContext context = g2.getFontRenderContext();
        TextLayout layout = new TextLayout(text, titleFont, context);
        Rectangle2D bounds = layout.getBounds();

        float x = (float) (getWidth() - bounds.getWidth()) / 2.0f;
        float y = (float) (getHeight() - bounds.getHeight()) / 2.0f;
        
        double rads = Math.toRadians(shadowDirection);
        float shadowOffsetX = (float) Math.cos(rads) * shadowDistance;
        float shadowOffsetY = (float) Math.sin(rads) * shadowDistance;
        
        Composite composite = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                                                   shadowOpacity));
        g2.setColor(shadowColor);
        layout.draw(g2,
                    x + shadowOffsetX,
                    y + layout.getAscent() / 2.0f + shadowOffsetY);
        
        g2.setComposite(composite);
        g2.setColor(titleColor);
        layout.draw(g2,
                    x,
                    y + layout.getAscent() / 2.0f);
    }
    
    private void setupGraphics(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
    }
}

