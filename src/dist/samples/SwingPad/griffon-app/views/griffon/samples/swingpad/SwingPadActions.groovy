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

package griffon.samples.swingpad

import org.fife.ui.rtextarea.RTextArea

import static griffon.util.GriffonNameUtils.isBlank

modifyFont = { sizeFilter, sizeMod ->
    def currentFont = model.font
    if (sizeFilter(currentFont.size)) return
    model.font = currentFont.deriveFont((currentFont.size + sizeMod) as float)
}

// creare an instance of RTextArea to initialize actions right away
rtextArea()

wrapAction = { int actionId, String id, Map params = [:] ->
    bean(new WrappingAction(RTextArea.getAction(actionId), [Action.ACCELERATOR_KEY]), id: id)
    noparent {
        params.each { key, value ->
            getVariable(id).putValue(key, value)
        }
    }
    getVariable(id)
}

actions {
    action(newAction, smallIcon: silkIcon('page_white_cup'))
    action(openAction, smallIcon: silkIcon('folder_page'))
    action(id: 'showTipsAction',
            name: app.getMessage('application.action.ShowTips.name', 'Show Tips'),
            closure: {
                jxtipOfTheDay(model: app.config.tipsModel, currentTip: 0).showDialog(app.windowManager.startingWindow, SwingPadUtils.PREFERENCES, true)
            },
            mnemonic: app.getMessage('application.action.ShowTips.mnemonic', 'T'),
            shortDescription: app.getMessage('application.action.ShowTips.short_description', 'Show Tips')
    )
    action(saveAction,
            smallIcon: silkIcon('disk'),
            enabled: bind {model.dirty}
    )
    action(saveAsAction, enabled: bind {model.dirty})

    wrapAction(RTextArea.UNDO_ACTION, 'undoAction', [
            (Action.SMALL_ICON): silkIcon('arrow_undo')])
    wrapAction(RTextArea.REDO_ACTION, 'redoAction', [
            (Action.SMALL_ICON): silkIcon('arrow_redo')])
    wrapAction(RTextArea.CUT_ACTION, 'cutAction', [
            (Action.SMALL_ICON): silkIcon('cut')])
    wrapAction(RTextArea.COPY_ACTION, 'copyAction', [
            (Action.SMALL_ICON): silkIcon('page_copy')])
    wrapAction(RTextArea.PASTE_ACTION, 'pasteAction', [
            (Action.SMALL_ICON): silkIcon('page_paste')])
    wrapAction(RTextArea.DELETE_ACTION, 'deleteAction', [
            (Action.SMALL_ICON): silkIcon('delete')])
    wrapAction(RTextArea.SELECT_ALL_ACTION, 'selectAllAction')

    action(id: 'largerFontAction',
            name: app.getMessage('application.action.LargerFont.name', 'Larger Font'),
            closure: { modifyFont({it > 40}, +2) },
            mnemonic: app.getMessage('application.action.LargerFont.mnemonic', 'L'),
            accelerator: KeyStroke.getKeyStroke(app.getMessage('application.action.LargerFont.accelerator', 'shift meta L')),
            shortDescription: app.getMessage('application.action.LargerFont.short_description', 'Larger font')
    )
    action(id: 'smallerFontAction',
            name: app.getMessage('application.action.SmallerFont.name', 'Smaller Font'),
            closure: { modifyFont({it < 5}, -2) },
            mnemonic: app.getMessage('application.action.SmallerFont.mnemonic', 'M'),
            accelerator: KeyStroke.getKeyStroke(app.getMessage('application.action.SmallerFont.accelerator', 'shift meta M')),
            shortDescription: app.getMessage('application.action.SmallerFont.short_description', 'Smaller font')
    )
    action(id: 'packComponentsAction',
            name: app.getMessage('application.action.PackComponents.name', 'Pack'),
            closure: { evt ->
                def newLayout = evt?.source?.state ? flowLayout(alignment: FlowLayout.LEFT, hgap: 0, vgap: 0) : borderLayout()
                if (!newLayout.class.isAssignableFrom(canvas.layout.class)) {
                    canvas.layout = newLayout
                    if (model.success) controller.runScriptAction()
                }
            },
            accelerator: KeyStroke.getKeyStroke(app.getMessage('application.action.PackComponents.accelerator', 'shift meta P')),
            shortDescription: app.getMessage('application.action.PackComponents.short_description', 'Pack')
    )
    action(id: 'showRulersAction',
            name: app.getMessage('application.action.ShowRulers.name', 'Show Rulers'),
            closure: { evt ->
                def rh = evt.source.state ? rowHeader : emptyRowHeader
                def ch = evt.source.state ? columnHeader : emptyColumnHeader
                if (scroller.rowHeader.view != rh) {
                    scroller.rowHeaderView = rh
                    scroller.columnHeaderView = ch
                    scroller.repaint()
                }
            },
            accelerator: KeyStroke.getKeyStroke(app.getMessage('application.action.ShowRulers.accelerator', 'shift meta U')),
            shortDescription: app.getMessage('application.action.ShowRulers.short_description', 'Show rulers')
    )
    action(id: 'showToolbarAction',
            name: app.getMessage('application.action.ShowToolbar.name', 'Show Toolbar'),
            closure: {toolbar.visible = it.source.selected},
            accelerator: KeyStroke.getKeyStroke(app.getMessage('application.action.ShowToolbar.accelerator', 'shift meta T')),
            shortDescription: app.getMessage('application.action.ShowToolbar.short_description', 'Show toolbar')
    )

    action(addJarToClasspathAction, smallIcon: silkIcon('cup_add'))
    action(addDirToClasspathAction, smallIcon: silkIcon('folder_add'))

    action(runScriptAction,
            smallIcon: silkIcon('script_go'),
            enabled: bind('code', source: model, converter: {!isBlank(it)})
    )

    action(snapshotAction, smallIcon: silkIcon('camera'))

    silkIcon(id: 'verticalLayoutIcon', 'application_tile_vertical')
    silkIcon(id: 'horizontalLayoutIcon', 'application_tile_horizontal')

    action(id: 'toggleLayoutAction',
            name: app.getMessage('application.action.ToggleLayout.name', 'Toggle Layout'),
            closure: {
                model.layout = !model.layout
                toggleLayoutAction.putValue('SmallIcon', model.layout ? verticalLayoutIcon : horizontalLayoutIcon)
            },
            mnemonic: app.getMessage('application.action.ToggleLayout.mnemonic', 'Y'),
            accelerator: KeyStroke.getKeyStroke(app.getMessage('application.action.ToggleLayout.accelerator', 'shift meta Y')),
            shortDescription: app.getMessage('application.action.ToggleLayout.short_description', 'Toggle layout'),
            smallIcon: verticalLayoutIcon
    )
}
