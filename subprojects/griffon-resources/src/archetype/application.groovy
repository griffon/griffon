import griffon.util.Metadata

includeTargets << griffonScript('CreateMvc' )

target(name: 'createApplicationProject',
        description: 'Creates a new application project',
        prehook: null, posthook: null) {
    createProjectWithDefaults()
    createMVC()

    // to install plugins do the following
    // Metadata md = Metadata.getInstance(new File("${basedir}/application.properties"))
    // installPluginExternal md, pluginName, pluginVersion
    //
    // pluginVersion is optional
}
setDefaultTarget(createApplicationProject)