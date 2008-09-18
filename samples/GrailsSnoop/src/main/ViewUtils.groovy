/*
 * Copyright 2008 the original author or authors.
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

import javax.imageio.ImageIO

import java.awt.Color
import javax.swing.BorderFactory
import javax.swing.JTree
import javax.swing.tree.TreePath
import javax.imageio.ImageIO
import org.jdesktop.swingx.border.*
import static javax.swing.SwingUtilities.*

class ViewUtils {
   static createShadowBorder() {
     return BorderFactory.createCompoundBorder( BorderFactory.createCompoundBorder(
           BorderFactory.createEmptyBorder(3, 3, 3, 3),
           new DropShadowBorder() ),
         BorderFactory.createLineBorder(Color.black, 1) )
   }

   static loadImage = { String filename ->
       URL imageUrl = Thread.currentThread().getContextClassLoader().getResource(filename)
       return ImageIO.read(imageUrl)
   }

   static expandTree( tree ) {
      invokeLater {
         def max = tree.rowCount
         ((max-1)..1).each { tree.expandRow(it) }
      }
   }

   static collapseTree( tree ) {
      invokeLater {
         def max = tree.rowCount
         ((max-1)..1).each {
            def path = tree.getPathForRow(it)
            if( path.pathCount == 2 && tree.isExpanded(path) ) tree.collapsePath( path )
         }
      }
   }

   // the following code is based in Santhosh Kumar's search algorithm
   // http://www.jroller.com/santhosh/entry/incremental_search_jtree

   static searchTree( JTree tree, String searchString ) {
      boolean startingFromSelection = true
      int max = tree.rowCount
      int startingRow = (tree.leadSelectionRow + 1 + max) % max
      if( startingRow < 0 || startingRow >= tree.rowCount ) {
         startingFromSelection = false
         startingRow = 0
      }

      // TODO save selection state
      // expand the whole tree otherwise search will fail
      if( !tree.selectionPath ) tree.selectionRow = 0
      expandTree( tree )
      def path = getNextMatch( tree, searchString, startingRow )
      // TODO restore selection state
      if( path ) {
         changeSelection( tree, path )
      }else if( startingFromSelection ) {
         path = getNextMatch( tree, searchString, 0 )
         if( path ) changeSelection( tree, path )
      }
   }

   private static TreePath getNextMatch( JTree tree, String searchString, int startingRow ) {
      boolean firstIteration = true
      int max = tree.rowCount
      int row = -1
      while( row != startingRow ) {
         if( firstIteration ) {
            row = startingRow
            firstIteration = false
         }
         TreePath path = tree.getPathForRow(row)
         if( path.pathCount == 3 ) {
            String text = tree.convertValueToText( path.lastPathComponent,
                                                   tree.isRowSelected(row),
                                                   tree.isExpanded(row),
                                                   true, row, false )
            if( text =~ /$searchString/ ) return path
         }
         row = (row + 1 + max) % max
      }
      return null
   }

   private static changeSelection( tree, path ) {
      invokeLater {
         tree.selectionPath = path
         tree.scrollPathToVisible( path )
      }
   }

   static icons = [:]

   static {
      icons.folder = loadImage("org/tango-project/tango-icon-theme/16x16/places/folder.png")
      icons.folderOpen = loadImage("org/tango-project/tango-icon-theme/16x16/status/folder-open.png")
      icons.entry = loadImage("images/grails16.png")
      icons.next = loadImage("org/tango-project/tango-icon-theme/16x16/actions/go-next.png")
      icons.previous = loadImage("org/tango-project/tango-icon-theme/16x16/actions/go-previous.png")
      icons.home = loadImage("org/tango-project/tango-icon-theme/16x16/actions/go-home.png")
   }
}