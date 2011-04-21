/*
 * Copyright 2007-2011 the original author or authors.
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

spanCount = 1

actions {
    if(model.includeCredits) {
        spanCount++
        action(id: 'creditsAction',
           name: app.getMessage('application.action.Credits.name', 'Credits'),
           closure: {
               def window = SwingUtilities.windowForComponent(content)
               withMVCGroup('credits') { m, v, c -> c.show(window) }
           },
           mnemonic: app.getMessage('application.action.New.mnemonic', 'R'),
           shortDescription: app.getMessage('application.action.Credits.description', 'Credits')
        )
    }
    if(model.includeLicense) {
        spanCount++
        action(id: 'licenseAction',
           name: app.getMessage('application.action.License.name', 'License'),
           closure: {
               def window = SwingUtilities.windowForComponent(content)
               withMVCGroup('license') { m, v, c -> c.show(window) }
           },
           mnemonic: app.getMessage('application.action.License.mnemonic', 'L'),
           shortDescription: app.getMessage('application.action.License.description', 'License')
        )
    }
    action(id: 'closeAction',
       name: app.getMessage('application.action.Close.name', 'Close'),
       closure: controller.hide,
       mnemonic: app.getMessage('application.action.Close.mnemonic', 'C'),
       shortDescription: app.getMessage('application.action.Close.description', 'Close')
    )
}

rowConstraints = "center, span $spanCount, wrap".toString()

panel(id: 'content') {
    migLayout layoutConstraints: 'fill'
    label(icon: imageIcon('/griffon-icon-64x64.png'), constraints: "center, span $spanCount, wrap".toString())
    label(GriffonNameUtils.capitalize(app.getMessage('application.title', app.config.application.title)) +
          ' ' + Metadata.current.getApplicationVersion(),
        font: current.font.deriveFont(Font.BOLD),
        constraints: rowConstraints)
    label(text: bind{ model.description }, constraints: rowConstraints)
    scrollPane(preferredSize: [320, 160], constraints: rowConstraints) {
        table {
            tableFormat = defaultTableFormat(columnNames: ['Name', 'Version'])
            eventTableModel(source: model.plugins, format: tableFormat)
            installTableComparatorChooser(source: model.plugins)
        }
    }
    if(model.includeCredits) button(creditsAction, constraints: 'left')
    if(model.includeLicense) button(licenseAction, constraints: (model.includeCredits?'center':'left'))
    button(closeAction, constraints: 'right')

    keyStrokeAction(component: current,
        keyStroke: 'ESCAPE',
        condition: 'in focused window',
        action: closeAction)
}
