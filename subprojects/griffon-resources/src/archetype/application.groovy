import griffon.util.Metadata

includeTargets << griffonScript('CreateMvc')

target(name: 'createApplicationProject',
        description: 'Creates a new application project',
        prehook: null, posthook: null) {
    createProjectWithDefaults()
    createMVC()

    // to install plugins do the following
    // Metadata md = Metadata.getInstance(new File("${basedir}/application.properties"))
    //
    // for a single plugin
    //     installPluginExternal md, pluginName, pluginVersion
    //        ** pluginVersion is optional **
    //
    // for multiple plugins where the latest version is preferred
    //     installPluginsLatest md, [pluginName1, pluginName2]
    //
    // for multiple plugins with an specific version
    //     installPlugins md, [pluginName1: pluginVersion1]
}
setDefaultTarget(createApplicationProject)