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

package griffon.samples.groovyfxpad

import griffon.plugins.dialogs.Finder
import griffon.plugins.jfxtras.factory.GaugeFactory
import griffon.plugins.jfxtras.factory.GroovyFXBeanFactory
import griffon.transform.Threading
import groovyx.javafx.SceneGraphBuilder
import javafx.application.Platform
import javafx.scene.Node
import javafx.scene.paint.Color
import jfxtras.labs.scene.control.gauge.GaugeModel
import jfxtras.labs.scene.control.gauge.Radial
import jfxtras.labs.scene.control.gauge.StyleModel

import java.awt.Rectangle
import java.awt.Robot
import java.awt.image.BufferedImage
import java.util.concurrent.CountDownLatch
import javax.imageio.ImageIO
import javax.swing.JFileChooser
import javax.swing.JOptionPane

import static griffon.samples.groovyfxpad.GroovFXPadUtils.*
import static griffon.util.GriffonNameUtils.capitalize
import static griffon.util.GriffonNameUtils.isBlank

/**
 * @author Andres Almiray
 */
class IdeController {
    def model
    def view

    private GroovyClassLoader groovyClassLoader
    private Thread evalThread
    private int scriptCounter = 0

    void mvcGroupInit(Map<String, Object> args) {
        SceneGraphBuilder.metaClass.'_delegateProperty:id' = 'id'
        groovyClassLoader = new GroovyClassLoader(this.class.classLoader)
        int recentScriptsListSize = PREFERENCES.get('recentScripts.list.size', '0') as int
        (0..<recentScriptsListSize).each { i ->
            File file = new File(PREFERENCES.get("recentScripts.${i}.file", ''))
            if (file.exists()) model.addRecentScript(file, PREFERENCES)
        }
    }

    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    def clearRecentScriptsAction = {
        model.recentScripts.clear()
        PREFERENCES.put('recentScripts.list.size', '0')
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
        File file = selectFileOrDir(FILE_CHOOSER_DIR, 'FILE_CHOOSER_DIR')
        if (file != null) openFile(file)
    }

    private void openFile(File file) {
        execOutsideUI {
            String code = file.readLines().join('\n')
            if (isBlank(code)) return
            model.scriptFile = file
            model.addRecentScript(file, PREFERENCES)
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
        model.scriptFile = selectFileOrDir(FILE_CHOOSER_DIR, 'FILE_CHOOSER_DIR', JFileChooser.FILES_ONLY, name)
        if (model.scriptFile) {
            model.scriptFile.write(model.code)
            model.addRecentScript(model.scriptFile, PREFERENCES)
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

    def snapshotAction = {
        String name = app.getMessage('application.dialog.Snapshot.title', 'Take a Snapshot')
        File file = selectFileOrDir(SNAPSHOT_DIR,
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
            File target = new File(SNAPSHOT_DIR, filename)
            ImageIO.write(capture, ext, target)

            String message = app.getMessage('application.dialog.Snapshot.message',
                    [target.absolutePath],
                    "Successfully saved snapshot to ${target.canonicalFile}".toString())
            JOptionPane.showMessageDialog(
                    app.windowManager.startingWindow,
                    message,
                    app.getMessage('application.dialog.Snapshot.title', 'Take a Snapshot'),
                    JOptionPane.INFORMATION_MESSAGE
            )
        }
    }

    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    def addJarToClasspathAction = {
        String name = app.getMessage('application.dialog.AddJarToClasspath.title', 'Add JAR')
        File file = selectFileOrDir(CLASSPATH_JAR_DIR,
                'CLASSPATH_JAR_DIR',
                JFileChooser.FILES_ONLY,
                name)
        if (file != null) groovyClassLoader.addURL(file.toURL())
    }

    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    def addDirToClasspathAction = {
        String name = app.getMessage('application.dialog.AddDirToClasspath.title', 'Add Directory')
        CLASSPATH_DIR = selectFileOrDir(CLASSPATH_DIR,
                'CLASSPATH_DIR',
                JFileChooser.DIRECTORIES_ONLY,
                name)
        if (CLASSPATH_DIR != null) groovyClassLoader.addURL(CLASSPATH_DIR.toURL())
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
        codeSource += '''
    import javafx.animation.*
    import javafx.application.*
    import javafx.beans.*
    import javafx.beans.binding.*
    import javafx.beans.property.*
    import javafx.beans.value.*
    import javafx.collections.*
    import javafx.concurrent.*
    import javafx.embed.swing.*
    import javafx.embed.swt.*
    import javafx.event.*
    import javafx.fxml.*
    import javafx.geometry.*
    import javafx.scene.*
    import javafx.scene.chart.*
    import javafx.scene.control.*
    import javafx.scene.control.cell.*
    import javafx.scene.effect.*
    import javafx.scene.image.*
    import javafx.scene.input.*
    import javafx.scene.layout.*
    import javafx.scene.media.*
    import javafx.scene.paint.*
    import javafx.scene.shape.*
    import javafx.scene.text.*
    import javafx.scene.transform.*
    import javafx.scene.web.*
    import javafx.stage.*
    import javafx.util.*
    import jfxtras.labs.scene.control.gauge.*
        '''

        try {
            def scriptClass = groovyClassLoader.parseClass(codeSource, getScriptName())
            SceneGraphBuilder sgb = createSceneGraphBuilder()
            sgb.setVariable('currentScene', view.canvas.scene)
            CountDownLatch latch = new CountDownLatch(1)
            def node = null
            Platform.runLater {
                node = sgb.build(scriptClass)
                latch.countDown()
            }
            latch.await()
            execInsideUIAsync { finishNormal(node) }
        } catch (Exception e) {
            execInsideUIAsync { finishWithException(e) }
        }
    }

    private String getScriptName() {
        "GroovyFXPad_script" + (scriptCounter++)
    }

    private void finishNormal(node) {
        model.success = true
        model.status = 'Execution complete.'
        boolean isNode = node instanceof Node
        if (isNode) {
            Platform.runLater({
                view.canvas.scene.root.children.clear()
                view.canvas.scene.root.children.add(node)
            })
        } else {
            model.status = 'The script did not return a Node!'
        }
    }

    private void finishWithException(Exception e) {
        model.success = false
        model.status = 'Execution terminated with exception.'
        displayErrorMessages(e)
        view.canvas.removeAll()
        view.canvas.repaint()
    }

    private void displayErrorMessages(Exception e) {
        GriffonExceptionHandler.sanitize(e)
        e.printStackTrace()
        def baos = new ByteArrayOutputStream()
        e.printStackTrace(new PrintStream(baos))
        view.tabs.selectedIndex = 1 // errorsTab
        model.errors = baos.toString()
    }

    private SceneGraphBuilder createSceneGraphBuilder() {
        SceneGraphBuilder builder = new SceneGraphBuilder()
        builder.setVariable('_delegateProperty:id', 'id')
        builder.registerFactory('radial', new GaugeFactory(Radial))
        builder.registerFactory('gaugeModel', new GroovyFXBeanFactory(GaugeModel))
        builder.registerFactory('styleModel', new GroovyFXBeanFactory(StyleModel))
        Color.NamedColors.namedColors.each { namedColor, color ->
            builder.setVariable(namedColor, color)
        }
        builder
    }
}
