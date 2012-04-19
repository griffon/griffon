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

import griffon.core.GriffonApplication
import griffon.util.ApplicationHolder
import java.util.prefs.Preferences
import javax.swing.JFileChooser
import static griffon.util.GriffonNameUtils.isBlank

/**
 * @author Andres Almiray
 */
final class GroovFXPadUtils {
    static final Preferences PREFERENCES = Preferences.userNodeForPackage(GroovFXPadUtils)
    static File FILE_CHOOSER_DIR  = new File(PREFERENCES.get('FILE_CHOOSER_DIR',  '.'))
    static File CLASSPATH_JAR_DIR = new File(PREFERENCES.get('CLASSPATH_JAR_DIR', '.'))
    static File CLASSPATH_DIR     = new File(PREFERENCES.get('CLASSPATH_DIR',     '.'))
    static File SNAPSHOT_DIR      = new File(PREFERENCES.get('SNAPSHOT_DIR',      '.'))
    
    static File selectFileOrDir(File location, String locationPrefs, int selectionMode = JFileChooser.FILES_ONLY, String name = null) {
        GriffonApplication app = ApplicationHolder.application
        if(isBlank(name)) name = app.getMessage('application.dialog.Open.title', 'Open')
        JFileChooser fc = new JFileChooser(location)
        fc.fileSelectionMode = selectionMode
        fc.acceptAllFileFilterUsed = true
        fc.dialogTitle = name
        if (fc.showDialog(app.windowManager.startingWindow, app.getMessage('application.dialog.Select.name', 'Select')) == JFileChooser.APPROVE_OPTION) {
            location = fc.currentDirectory
            PREFERENCES.put(locationPrefs, fc.currentDirectory.path)
            return selectionMode == JFileChooser.FILES_ONLY ? fc.selectedFile : fc.currentDirectory
        }
        return null
    }
}
