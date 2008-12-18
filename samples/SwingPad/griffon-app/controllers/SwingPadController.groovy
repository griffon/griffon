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

import java.awt.Color
import java.awt.Font
import java.awt.Robot
import java.awt.FlowLayout
import java.awt.event.ActionEvent
import javax.swing.JComponent
import javax.swing.JFileChooser
import javax.swing.JOptionPane
import javax.swing.SwingConstants
import javax.imageio.ImageIO
import java.util.prefs.Preferences
import groovy.ui.text.FindReplaceUtility
import org.codehaus.groovy.runtime.StackTraceUtils

class SwingPadController {
   def model
   def view
   def builder

   private prefs = Preferences.userNodeForPackage(SwingPadController)
   private File currentFileChooserDir = new File(prefs.get('currentFileChooserDir', '.'))
   private File currentClasspathJarDir = new File(prefs.get('currentClasspathJarDir', '.'))
   private File currentClasspathDir = new File(prefs.get('currentClasspathDir', '.'))
   private File currentSnapshotDir = new File(prefs.get('currentSnapshotDir', '.'))
   private runThread = null
   private GroovyClassLoader groovyClassLoader
   private static int scriptCounter = 0
   private Set factorySet = new TreeSet()

   void mvcGroupInit( Map args ) {
      groovyClassLoader = new GroovyClassLoader(this.class.classLoader)
   }

   def updateTitle = { ->
      // TODO handle undo!
      if( model.scriptFile ) {
         return model.scriptFile.name + (model.dirty ? " *" : "") + " - SwingPad"
      }
      return "SwingPad"
   }

   def newScript = { evt = null ->
      if( askToSaveFile(evt) ) {
         model.scriptFile = null
         model.dirty = false
         view.editor.textEditor.text = ''
         view.editor.textEditor.requestFocus()
      }
   }

   def open = { evt = null ->
      model.scriptFile = selectFilename()
      if( !model.scriptFile ) return
      doOutside {
         def scriptText = model.scriptFile.readLines().join('\n')
         doLater {
            if( !scriptText ) return
            // need 2-way binding!
            view.editor.textEditor.text = scriptText
            model.dirty = false
            view.editor.textEditor.caretPosition = 0
            view.editor.textEditor.requestFocus()
         }
      }
   }

   def save = { evt = null ->
      if( !model.scriptFile ) return saveAs(evt)
      model.scriptFile.write(model.content)
      model.dirty = false
      return true
   }

   def saveAs = { evt = null ->
      model.scriptFile = selectFilename("Save")
      if( model.scriptFile ) {
         model.scriptFile.write(model.content)
         model.dirty = false
         return true
      }
      return false
   }

   def exit = { evt = null ->
      if( askToSaveFile() ) {
         FindReplaceUtility.dispose()
         app.shutdown()
      }
   }

   def snapshot = { evt ->
      def fc = new JFileChooser(currentSnapshotDir)
      fc.fileSelectionMode = JFileChooser.FILES_ONLY
      fc.acceptAllFileFilterUsed = true
      if (fc.showDialog(app.appFrames[0], "Snapshot") == JFileChooser.APPROVE_OPTION) {
         currentSnapshotDir = fc.currentDirectory
         prefs.put('currentSnapshotDir', currentSnapshotDir.path)
         def frameBounds = app.appFrames[0].bounds
         def capture = new Robot().createScreenCapture(frameBounds)
         def filename = fc.selectedFile.name
         def dot = filename.lastIndexOf(".")
         def ext = "png"
         if( dot > 0 )  {
            ext = filename[dot+1..-1] 
         } else {
            filename += ".$ext"
         }
         def target = new File(currentSnapshotDir,filename)
         ImageIO.write( capture, ext, target )
         def pane = builder.optionPane()
         pane.setMessage("Successfully saved snapshot to\n\n${target.absolutePath}")
         def dialog = pane.createDialog(app.appFrames[0], 'Snapshot')
         dialog.show()
      }
   }

   private void invokeTextAction( evt, closure ) {
      if( evt.source ) closure(view.editor.textEditor)
   }

   def cut = { evt = null -> invokeTextAction(evt, { source -> source.cut() }) }
   def copy = { evt = null -> invokeTextAction(evt, { source -> source.copy() }) }
   def paste = { evt = null -> invokeTextAction(evt, { source -> source.paste() }) }
   def selectAll = { evt = null -> invokeTextAction(evt, { source -> source.selectAll() }) }

   // TODO yet unconnected!!
   def find = { evt = null -> FindReplaceUtility.showDialog() }
   def findNext = { evt = null -> FindReplaceUtility.FIND_ACTION.actionPerformed(evt) }
   def findPrevious = { evt = null -> 
      def reverseEvt = new ActionEvent( evt.source, evt.iD, 
         evt.actionCommand, evt.when,
         ActionEvent.SHIFT_MASK) //reverse
      FindReplaceUtility.FIND_ACTION.actionPerformed(reverseEvt)
   }
   def replace = { evt = null -> FindReplaceUtility.showDialog(true) }

   def largerFont = { evt = null ->
      modifyFont(view.editor.textEditor, {it > 40}, +2)
      modifyFont(view.errors, {it > 40}, +2)
   }

   def smallerFont = { evt = null ->
      modifyFont(view.editor.textEditor, {it < 5}, -2)
      modifyFont(view.errors, {it < 5}, -2)
   }

   def packComponents = { evt = null ->
      def newLayout = evt?.source?.state ? builder.flowLayout(alignment:FlowLayout.LEFT, hgap: 0, vgap: 0) : builder.borderLayout()
      if( !newLayout.class.isAssignableFrom(view.canvas.layout.class) ) {
         view.canvas.layout = newLayout
         runScript(evt)
      }
   }

   def showRulers = { evt = null ->
      def rh = evt?.source?.state ? view.rowHeader : view.emptyRowHeader
      def ch = evt?.source?.state ? view.columnHeader : view.emptyColumnHeader
      if( view.scroller.rowHeader.view != rh ) {
         view.scroller.rowHeaderView = rh
         view.scroller.columnHeaderView = ch
         view.scroller.repaint()
      }
   }

   def runScript = { evt = null ->
      if( !model.content ) return
      runThread = Thread.start {
         try {
            doLater {
               model.status = "Running Script ..."
               if( model.errors != "" ) {
                  model.errors = ""
                  model.caretPosition = 0
               }
               showDialog( "runWaitDialog" )
            }
            executeScript( model.content )
         } catch( Throwable t ) {
            doLater { finishWithException(t) }
         } finally {
            doLater {
               hideDialog( "runWaitDialog" )
               runThread = null
            }
         }
      }
   }

   def about = { evt = null ->
      def pane = builder.optionPane()
       // work around GROOVY-1048
      pane.setMessage('Welcome to SwingPad')
      def dialog = pane.createDialog(app.appFrames[0], 'About SwingPad')
      dialog.show()
   }

   def confirmRunInterrupt = { evt = null ->
      def rc = JOptionPane.showConfirmDialog( app.appFrames[0], "Attempt to interrupt script?",
            "GraphicsPad", JOptionPane.YES_NO_OPTION)
      if( rc == JOptionPane.YES_OPTION && runThread ) {
          runThread.interrupt()
      }
   }

   // the folowing 4 actions taken from groovy.ui.Console
    def addClasspathJar = { evt = null ->
        def fc = new JFileChooser(currentClasspathJarDir)
        fc.fileSelectionMode = JFileChooser.FILES_ONLY
        fc.acceptAllFileFilterUsed = true
        if (fc.showDialog(app.appFrames[0], "Add") == JFileChooser.APPROVE_OPTION) {
            currentClasspathJarDir = fc.currentDirectory
            prefs.put('currentClasspathJarDir', currentClasspathJarDir.path)
            groovyClassLoader.addURL(fc.selectedFile.toURL())
        }
    }

    def addClasspathDir = { evt = null ->
        def fc = new JFileChooser(currentClasspathDir)
        fc.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
        fc.acceptAllFileFilterUsed = true
        if (fc.showDialog(app.appFrames[0], "Add") == JFileChooser.APPROVE_OPTION) {
            currentClasspathDir = fc.currentDirectory
            prefs.put('currentClasspathDir', currentClasspathDir.path)
            groovyClassLoader.addURL(fc.selectedFile.toURL())
        }
    }

    def showToolbar = { evt = null ->
        def showToolbar = evt.source.selected
        prefs.putBoolean('showToolbar', showToolbar)
        view.toolbar.visible = showToolbar
    }

   def suggestNodeName = { evt = null ->
      if( !model.content ) return 

      def editor = view.editor.textEditor
      def caret = editor.caretPosition
      if( !caret ) return

      def document = editor.document
      def target = ""
      def ch = document.getText(--caret,1)
      def update = false
      while( ch =~ /[a-zA-Z]/ ) {
         target = ch + target
         if( caret ){ ch = document.getText(--caret,1); update = true }
         else break
      }
      if( update ) caret++

      if( !factorySet ) populateFactorySet()
      def suggestions = factorySet.findAll{ it.startsWith(target) }
      if( !suggestions ) return
      if( suggestions.size() == 1 ) {
         model.suggestion = [
            start: caret,
            end: caret + target.size(),
            offset: target.size(),
            text: suggestions.iterator().next()
         ]
         writeSuggestion()
      } else {
         model.suggestion = [ 
            start: caret,
            end: caret + target.size(),
            offset: target.size()
         ]
         model.suggestions.clear()
         model.suggestions.addAll(suggestions)
         view.popup.showPopup(SwingConstants.CENTER, app.appFrames[0])
         view.suggestionList.selectedIndex = 0
      }
   }

   def codeComplete = { evt ->
      model.suggestion.text = model.suggestions[view.suggestionList.selectedIndex]
      view.popup.hidePopup(true)
      writeSuggestion()
   }

   private writeSuggestion() {
      if( !model.suggestion ) return

      def editor = view.editor.textEditor
      def document = editor.document
      def s = model.suggestion
      def text = s.text.substring(s.offset)
      document.insertString(s.start+s.offset, text, null)
      editor.requestFocus()

      // clear it!
      model.suggestion = [:]
   }

   private void finishNormal( component ) {
      doLater {
         model.status = 'Execution complete.'
         view.canvas.removeAll()
         view.canvas.repaint()
         view.canvas.add(component)
      }
   }

   private void finishWithException( Throwable t ) {
      model.status = 'Execution terminated with exception.'
      StackTraceUtils.deepSanitize(t)
      t.printStackTrace()
      def baos = new ByteArrayOutputStream()
      t.printStackTrace(new PrintStream(baos))
      doLater {
         view.canvas.removeAll()
         view.canvas.repaint()
         view.tabs.selectedIndex = 1 // errorsTab
         model.errors = baos.toString()
         model.caretPosition = 0
      }
   }

   private void showAlert(title, message) {
      doLater {
         JOptionPane.showMessageDialog(app.appFrames[0], message,
               title, JOptionPane.WARNING_MESSAGE)
      }
   }

   private void showMessage(title, message) {
      doLater {
         JOptionPane.showMessageDialog(app.appFrames[0], message,
               title, JOptionPane.INFORMATION_MESSAGE)
      }
   }

   private void showDialog( dialogName, pack = true ) {
      def dialog = view."$dialogName"
      if( pack ) dialog.pack()
      int x = app.appFrames[0].x + (app.appFrames[0].width - dialog.width) / 2
      int y = app.appFrames[0].y + (app.appFrames[0].height - dialog.height) / 2
      dialog.setLocation(x, y)
      dialog.show()
   }

   private void hideDialog( dialogName ) {
      def dialog = view."$dialogName"
      dialog.hide()
   }

   private selectFilename( name = "Open" ) {
      // should use builder.fileChooser() ?
      def fc = new JFileChooser(currentFileChooserDir)
      fc.fileSelectionMode = JFileChooser.FILES_ONLY
      fc.acceptAllFileFilterUsed = true
      if( fc.showDialog(app.appFrames[0], name ) == JFileChooser.APPROVE_OPTION ) {
         currentFileChooserDir = fc.currentDirectory
         prefs.put('currentFileChooserDir', currentFileChooserDir.path)
         return fc.selectedFile
      }
      return null
   }

   private boolean askToSaveFile(evt) {
      if( !model.scriptFile || !model.dirty ) return true
      switch( JOptionPane.showConfirmDialog( app.appFrames[0],
                 "Save changes to " + model.scriptFile.name + "?",
                 "GraphicsPad", JOptionPane.YES_NO_CANCEL_OPTION)){
         case JOptionPane.YES_OPTION: return save(evt)
         case JOptionPane.NO_OPTION: return true
      }
      return false
   }

   private void executeScript( codeSource ) {
      try {
         def script = groovyClassLoader.parseClass(codeSource,getScriptName()).newInstance()
         def b = app.builders.Script
         def component = null
         b.edt{ component = b.build(script)}
         if( !(component instanceof JComponent) ) {
            throw new IllegalArgumentException("The script did not return a JComponent!")
         }
         doLater { finishNormal(component) }
      } catch( Throwable t ) {
         doLater { finishWithException(t) }
      }
   }

   private getScriptName() {
      "SwingPad_script" + (scriptCounter++)
   }

   private modifyFont( target, sizeFilter, sizeMod ) {
      def currentFont = target.font
      if( sizeFilter(currentFont.size) ) return
      target.font = new Font( 'Monospaced', currentFont.style, currentFont.size + sizeMod )
   }

   private populateFactorySet() {
      // TODO filter factories coming from SwingXBuilder that have jxclassicSwing: on their name
      def ub = app.builders.Script
      factorySet.clear()
      ub.builderRegistration.each { ubr ->
         def builder = ubr.builder
         def oldProxy = builder.proxyBuilder
         try {
            builder.proxyBuilder = builder
            factorySet.addAll(ubr.builder.factories.keySet().sort().collect(){ (ubr.prefixString?:"")+it })
         } finally {
            builder.proxyBuilder = oldProxy
         }
      }
      factorySet -= factorySet.grep{ it.startsWith("jxclassicSwing:") }
   }
}
