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

/**
 * @author Andres Almiray
 */

package griffon.samples.groovyfxpad

import static griffon.util.GriffonApplicationUtils.isMacOSX

groovy.swing.SwingBuilder.metaClass.'_delegateProperty:id' = 'id'
setVariable('_delegateProperty:id', 'id')

makeSampleScriptAction = { identifier, name, mn ->
    doLater {
        mn.add(menuItem {
            action(id: "${identifier}Action",
                    name: name,
                    closure: { sampleId, evt -> model.currentSampleId = sampleId; controller.runSampleScriptAction() }.curry(identifier),
                    smallIcon: silkIcon('script_gear')
            )
        })
    }
    execOutsideUI {
        model.samples[identifier] = Thread.currentThread().contextClassLoader.
                getResourceAsStream("samples/${identifier}.txt").text
    }
}

menuBar = menuBar {
    menu(text: app.getMessage('application.menu.File.name', ' File'),
            mnemonic: app.getMessage('application.menu.File.mnemonic', 'F')) {
        menuItem(newAction)
        menuItem(openAction)
        menu(id: 'openRecentMenu', enabled: false,
                text: app.getMessage('application.menu.OpenRecent.name', ' Open Recent...'),
                mnemonic: app.getMessage('application.menu.OpenRecent.mnemonic', 'E'))
        separator()
        menuItem(saveAction)
        menuItem(saveAsAction)
        if (!isMacOSX) {
            separator()
            menuItem(quitAction)
        }
    }

    menu(text: app.getMessage('application.menu.Edit.name', 'Edit'),
            mnemonic: app.getMessage('application.menu.Edit.mnemonic', 'E')) {
        menuItem(undoAction)
        menuItem(redoAction)
        separator()
        menuItem(cutAction)
        menuItem(copyAction)
        menuItem(pasteAction)
        menuItem(deleteAction)
        separator()
        menuItem(selectAllAction)
        separator()
        menu(text: app.getMessage('application.menu.Find.name', ' Find')) {
            menuItem(findAction)
            separator()
            menuItem(findNextAction)
            menuItem(findPreviousAction)
            separator()
            menuItem(replaceAction)
            menuItem(replaceAllAction)
        }
    }

    menu(text: app.getMessage('application.menu.View.name', 'View'),
            mnemonic: app.getMessage('application.menu.View.mnemonic', 'V')) {
        menuItem(largerFontAction)
        menuItem(smallerFontAction)
        separator()
        checkBoxMenuItem(showRulersAction, selected: true)
        checkBoxMenuItem(showToolbarAction, selected: true)
        separator()
        menuItem(toggleLayoutAction)
        menuItem(snapshotAction)
    }

    menu(text: app.getMessage('application.menu.Script.name', 'Script'),
            mnemonic: app.getMessage('application.menu.Script.mnemonic', 'S')) {
        menuItem(runScriptAction)
        separator()
        menuItem(addJarToClasspathAction)
        menuItem(addDirToClasspathAction)
        separator()
        menu(text: app.getMessage('application.menu.Samples.name', 'Samples')) {
            menu(text: app.getMessage('application.menu.GroovyFX.name', 'GroovyFX')) {
                [hello: 'Hello GroovyFX',
                        charts1: 'Charts',
                        effects1: 'Effects',
                        clock: 'Analog Clock',
                        rejectfield: 'Custom Component',
                        table: 'TableView',
                        circles: 'Animation - Circles',
                        logo: 'Animation - Logo'].each { identifier, name ->
                    makeSampleScriptAction(identifier, name, current)
                }
            }
            menu(text: app.getMessage('application.menu.JFXtras.name', 'JFXtras')) {
                [gauges1: 'Gauges'].each { identifier, name ->
                    makeSampleScriptAction(identifier, name, current)
                }
            }
        }
    }

    if (!isMacOSX) glue()
    menu(text: app.getMessage('application.menu.Help.name', 'Help'),
            mnemonic: app.getMessage('application.menu.Help.mnemonic', 'H')) {
        if (!isMacOSX) {
            menuItem(aboutAction)
            menuItem(preferencesAction)
        }
        menuItem(nodeListAction)
        menuItem(showTipsAction)
    }
}

openRecentMenu.popupMenu.addPopupMenuListener([
        popupMenuWillBecomeVisible: {
            println openRecentMenu
            openRecentMenu.removeAll()
            model.recentScripts.eachWithIndex { file, int i ->
                openRecentMenu.add(action(
                        name: "${i + 1}. ${file.name}".toString(),
                        mnemonic: 1 + i,
                        closure: { controller.openFile(file) }
                ))
            }
            if (model.recentScripts.size()) {
                openRecentMenu.add(separator())
                openRecentMenu.add(clearRecentScriptsAction)
            }
        },
        popupMenuWillBecomeInvisible: {/*empty*/},
        popupMenuCanceled: {/*empty*/}
] as PopupMenuListener)

return menuBar
