@artifact.package@build(@artifact.name.plain@Actions)

application(title: GriffonNameUtils.capitalize(app.getMessage('application.title', app.config.application.title)),
    pack: true,
    locationByPlatform:true,
    iconImage: imageIcon('/griffon-icon-48x48.png').image,
    iconImages: [imageIcon('/griffon-icon-48x48.png').image,
               imageIcon('/griffon-icon-32x32.png').image,
               imageIcon('/griffon-icon-16x16.png').image]) {
   widget(build(@artifact.name.plain@MenuBar))
   migLayout(layoutConstraints: 'fill')
   widget(build(@artifact.name.plain@Content), constraints: 'center, grow')
   widget(build(@artifact.name.plain@StatusBar), constraints: 'south, grow')
}
