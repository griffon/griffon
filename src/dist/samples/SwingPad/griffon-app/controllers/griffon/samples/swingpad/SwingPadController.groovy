/*
 * Copyright 2007-2012 the original author or authors.
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

package griffon.samples.swingpad

import griffon.builder.css.CSSDecorator
import griffon.plugins.dialogs.Finder
import griffon.transform.Threading

import java.awt.Rectangle
import java.awt.Robot
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import javax.swing.JComponent
import javax.swing.JFileChooser
import javax.swing.JOptionPane

import static griffon.util.GriffonNameUtils.capitalize
import static griffon.util.GriffonNameUtils.isBlank

/**
 * @author Andres Almiray
 */
class SwingPadController {
    def model
    def view

    private GroovyClassLoader groovyClassLoader
    private Thread evalThread
    private int scriptCounter = 0

    void mvcGroupInit(Map<String, Object> args) {
        groovyClassLoader = new GroovyClassLoader(this.class.classLoader)
        int recentScriptsListSize = SwingPadUtils.PREFERENCES.get('recentScripts.list.size', '0') as int
        (0..<recentScriptsListSize).each { i ->
            File file = new File(SwingPadUtils.PREFERENCES.get("recentScripts.${i}.file", ''))
            if (file.exists()) model.addRecentScript(file, SwingPadUtils.PREFERENCES)
        }
    }

    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    def clearRecentScriptsAction = {
        model.recentScripts.clear()
        SwingPadUtils.PREFERENCES.put('recentScripts.list.size', '0')
    }

    def newAction = {
        if (!model.dirty || askToSaveScript()) {
            model.scriptFile = null
            model.code = ''
            model.dirty = false
            view.codeEditor.requestFocus()
        }
    }

    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    def openAction = {
        File file = SwingPadUtils.selectFileOrDir(SwingPadUtils.FILE_CHOOSER_DIR, 'FILE_CHOOSER_DIR')
        if (file != null) openFile(file)
    }

    private void openFile(File file) {
        execOutsideUI {
            String code = file.readLines().join('\n')
            if (isBlank(code)) return
            model.scriptFile = file
            model.addRecentScript(file, SwingPadUtils.PREFERENCES)
            execInsideUIAsync {
                model.code = code
                model.dirty = false
                view.codeEditor.caretPosition = 0
                view.codeEditor.requestFocus()
            }
        }
    }

    def saveAction = {
        if (model.scriptFile) {
            model.scriptFile.write(model.code)
            model.dirty = false
        } else {
            saveAsAction()
        }
    }

    def saveAsAction = {
        String name = app.getMessage('application.dialog.Save.title', 'Save Script')
        model.scriptFile = SwingPadUtils.selectFileOrDir(SwingPadUtils.FILE_CHOOSER_DIR, 'FILE_CHOOSER_DIR', JFileChooser.FILES_ONLY, name)
        if (model.scriptFile) {
            model.scriptFile.write(model.code)
            model.addRecentScript(model.scriptFile, SwingPadUtils.PREFERENCES)
            model.dirty = false
        }
    }

    def aboutAction = {
        withMVCGroup('about') { m, v, c ->
            c.show()
        }
    }

    def nodeListAction = {
        withMVCGroup('nodes') { m, v, c ->
            c.show()
        }
    }

    def preferencesAction = {
        withMVCGroup('preferences') { m, v, c ->
            c.show()
        }
    }

    def quitAction = {
        if (!model.dirty || askToSaveScript()) app.shutdown()
    }

    def findAction = {
        Finder.instance.findIt(view.codeEditor)
    }

    def findPreviousAction = {
        Finder.instance.findPrevious(view.codeEditor)
    }

    def findNextAction = {
        Finder.instance.findNext(view.codeEditor)
    }

    def replaceAction = {
        Finder.instance.replace(view.codeEditor)
    }

    def replaceAllAction = {
        Finder.instance.replaceAll(view.codeEditor)
    }

    def runScriptAction = {
        if (isBlank(model.code)) return
        evalThread = Thread.start {
            execInsideUIAsync {
                model.status = 'Running script...'
                model.errors = ''
            }
            try {
                executeScript(model.code)
            } catch (Exception e) {
                execInsideUIAsync { finishWithException(e) }
            } finally {
                evalThread = null
            }
        }
    }

    def runSampleScriptAction = {
        if (model.currentSampleId) {
            model.status = 'Loading Script ...'
            model.code = model.samples[model.currentSampleId]
            // view.runAction.enabled = true
            runScriptAction()
        }
    }

    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    def addJarToClasspathAction = {
        String name = app.getMessage('application.dialog.AddJarToClasspath.title', 'Add JAR')
        File file = SwingPadUtils.selectFileOrDir(SwingPadUtils.CLASSPATH_JAR_DIR,
                'CLASSPATH_JAR_DIR',
                JFileChooser.FILES_ONLY,
                name)
        if (file != null) groovyClassLoader.addURL(file.toURL())
    }

    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    def addDirToClasspathAction = {
        String name = app.getMessage('application.dialog.AddDirToClasspath.title', 'Add Directory')
        SwingPadUtils.CLASSPATH_DIR = SwingPadUtils.selectFileOrDir(SwingPadUtils.CLASSPATH_DIR,
                'CLASSPATH_DIR',
                JFileChooser.DIRECTORIES_ONLY,
                name)
        if (SwingPadUtils.CLASSPATH_DIR != null) groovyClassLoader.addURL(SwingPadUtils.CLASSPATH_DIR.toURL())
    }

    def snapshotAction = {
        String name = app.getMessage('application.dialog.Snapshot.title', 'Take a Snapshot')
        File file = SwingPadUtils.selectFileOrDir(SwingPadUtils.SNAPSHOT_DIR,
                'SNAPSHOT_DIR',
                JFileChooser.FILES_ONLY,
                name)
        if (file != null) {
            Rectangle frameBounds = app.windowManager.startingWindow.bounds
            BufferedImage capture = new Robot().createScreenCapture(frameBounds)
            String filename = file.name
            int dot = filename.lastIndexOf('.')
            String ext = 'png'
            if (dot > 0) {
                ext = filename[dot + 1..-1]
            } else {
                filename += '.' + ext
            }
            File target = new File(SwingPadUtils.SNAPSHOT_DIR, filename)
            ImageIO.write(capture, ext, target)

            String message = app.getMessage('application.dialog.Snapshot.message',
                    [target.canonicalPath],
                    "Successfully saved snapshot to ${target.canonicalPath}".toString())
            JOptionPane.showMessageDialog(
                    app.windowManager.startingWindow,
                    message,
                    app.getMessage('application.dialog.Snapshot.title', 'Take a Snapshot'),
                    JOptionPane.INFORMATION_MESSAGE
            )
        }
    }

    def onOSXAbout = { app ->
        withMVCGroup('about') { m, v, c ->
            c.show()
        }
    }

    def onOSXQuit = { app ->
        if (!model.dirty || askToSaveScript()) app.shutdown()
    }

    def onOSXPrefs = { app ->
        withMVCGroup('preferences') { m, v, c ->
            c.show()
        }
    }

    private boolean askToSaveScript() {
        String message = app.getMessage('application.dialog.AskToSave.noscript.message', 'Save script to file?')
        if (model.scriptFile) {
            message = app.getMessage('application.dialog.AskToSave.script.message', [model.scriptFile.name], 'Save changes to ' + model.scriptFile.name + '?')
        }

        switch (JOptionPane.showConfirmDialog(
                app.windowManager.startingWindow,
                message,
                capitalize(app.getMessage('application.title')),
                JOptionPane.YES_NO_CANCEL_OPTION)) {
            case JOptionPane.YES_OPTION: saveAction()
            case JOptionPane.NO_OPTION: return true
        }
        false
    }

    private void executeScript(codeSource) {
        try {
            def script = groovyClassLoader.parseClass(codeSource, getScriptName()).newInstance()
            def b = app.builders.script
            def component = null
            b.edt { component = b.build(script) }
            execInsideUIAsync { finishNormal(component) }
        } catch (Exception e) {
            execInsideUIAsync { finishWithException(e) }
        }
    }

    private getScriptName() {
        "SwingPad_script" + (scriptCounter++)
    }

    private void finishNormal(component) {
        model.success = true
        execInsideUIAsync {
            model.status = 'Execution complete.'
            view.canvas.removeAll()
            view.canvas.repaint()
            if (component instanceof JComponent) {
                view.canvas.add(component)
                if (model.stylesheet) {
                    try {
                        CSSDecorator.applyStyle(model.stylesheet, view.canvas)
                    } catch (Exception e) {
                        displayErrorMessages(e)
                    }
                }
            } else {
                model.status = 'The script did not return a JComponent!'
            }
        }
    }

    private void finishWithException(Exception e) {
        model.success = false
        model.status = 'Execution terminated with exception.'
        displayErrorMessages(e)
        execInsideUIAsync {
            view.canvas.removeAll()
            view.canvas.repaint()
        }
    }

    private void displayErrorMessages(Exception e) {
        GriffonExceptionHandler.sanitize(e)
        e.printStackTrace()
        ByteArrayOutputStream baos = new ByteArrayOutputStream()
        e.printStackTrace(new PrintStream(baos))
        execInsideUIAsync {
            view.tabs.selectedIndex = 2 // errorsTab
            model.errors = baos.toString()
        }
    }
}
