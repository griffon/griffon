/*
 * Copyright 2007-2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 */

import java.awt.*
import java.awt.event.*
import javax.swing.*
import javax.swing.BorderFactory as BF

/**
 * Creates a Ruler to be used as a row or column view on a ScrollPane
 * Referenced from http://forum.java.sun.com/thread.jspa?threadID=5205520&messageID=9821974
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class ScrollPaneRuler extends JComponent implements MouseListener, MouseMotionListener {
   private static final int SIZE = 20
   public static final int HORIZONTAL = 0
   public static final int VERTICAL = 1

   private static final Font DEFAULT_FONT = new Font("SansSerif", Font.PLAIN, 9)

   private int tickHeight
   private int orientation
   private Point crossHair

   public ScrollPaneRuler(int o) {
      orientation = o
      setBorder(BF.createLineBorder(Color.BLACK))
   }

   protected void paintComponent(Graphics g) {
      Toolkit toolkit = Toolkit.getDefaultToolkit()
      Dimension screen = toolkit.getScreenSize()
      int width = screen.width as int
      int height = screen.height as int

      super.paintComponent(g)
      if( isOpaque() ) {
         g.setColor(Color.WHITE)
         if( orientation == HORIZONTAL ){
            g.fillRect(0, 0, getWidth(), getHeight())
         }else{
            g.fillRect(0, 0, getWidth(), getHeight())
         }
      }
      g.setFont(DEFAULT_FONT)
      g.setColor(Color.BLACK)

      if( orientation == HORIZONTAL ) {
         for(int i = 0; i <= width; i += 10) {
            if(i % 100 == 0){
               tickHeight = 10
               g.drawString("$i", i - 4, 10)
            }else if(i % 50 == 0){
               tickHeight = 7
               g.drawString("$i", i - 4, 10)
            }else{
               tickHeight = 5
            }
            g.drawLine(i, SIZE, i, SIZE - tickHeight)
         }
         if( crossHair ){
            g.setColor( Color.RED )
            g.drawLine(crossHair.x as int, SIZE, crossHair.x as int, 0)
         }
      }else{
         for( int i = 0; i <= height; i += 10 ) {
            if( i % 100 == 0 ){
               tickHeight = 10
               g.drawString("$i", 2, i - 1)
            }else if( i % 50 == 0 ){
               tickHeight = 7
               g.drawString("$i", 2, i - 1)
            }else{
               tickHeight = 5
            }
            g.drawLine(SIZE, i, SIZE - tickHeight, i)
         }
         if( crossHair ){
            g.setColor( Color.RED )
            g.drawLine(SIZE, crossHair.y as int, 0, crossHair.y as int)
         }
      }
   }

   public void mouseMoved( MouseEvent event ) {
      crossHair = event.point
      repaint()
   }
   public void mouseDragged( MouseEvent event ) {
      crossHair = event.point
      repaint()
   }
   public void mouseEntered( MouseEvent event ) {
      crossHair = event.point
      repaint()
   }
   public void mouseExited( MouseEvent event ) {
      crossHair = null
      repaint()
   }

   // ----

   public void mouseClicked( MouseEvent event ) {
      // empty
   }
   public void mousePressed( MouseEvent event ) {
      // empty
   }
   public void mouseReleased( MouseEvent event ) {
      // empty
   }
}