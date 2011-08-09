@artifact.package@spanCount = 1

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
           shortDescription: app.getMessage('application.action.Credits.short_description', 'Credits')
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
           shortDescription: app.getMessage('application.action.License.short_description', 'License')
        )
    }
    action(hideAction,
       name: app.getMessage('application.action.Close.name', 'Close'),
       mnemonic: app.getMessage('application.action.Close.mnemonic', 'C'),
       shortDescription: app.getMessage('application.action.Close.short_description', 'Close')
    )
}

rowConstraints = "center, span $spanCount, wrap".toString()

panel(id: 'content') {
    migLayout layoutConstraints: 'fill'
    label(icon: imageIcon('/griffon-icon-64x64.png'), constraints: rowConstraints)
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
    button(hideAction, constraints: 'right')

    keyStrokeAction(component: current,
        keyStroke: 'ESCAPE',
        condition: 'in focused window',
        action: hideAction)
}
