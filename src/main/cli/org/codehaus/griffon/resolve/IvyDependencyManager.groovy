/* 
 * Copyright 2004-2011 the original author or authors.
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
 * limitations under the License.
 */
package org.codehaus.griffon.resolve

import org.apache.ivy.core.event.EventManager
import org.apache.ivy.core.module.descriptor.Configuration
import org.apache.ivy.core.module.descriptor.DefaultModuleDescriptor
import org.apache.ivy.core.module.descriptor.DependencyDescriptor
import org.apache.ivy.core.module.id.ModuleRevisionId
import org.apache.ivy.core.report.ResolveReport
import org.apache.ivy.core.resolve.IvyNode
import org.apache.ivy.core.resolve.ResolveEngine
import org.apache.ivy.core.resolve.ResolveOptions
import org.apache.ivy.core.settings.IvySettings
import org.apache.ivy.core.sort.SortEngine
import org.apache.ivy.plugins.resolver.ChainResolver
import org.apache.ivy.plugins.resolver.FileSystemResolver
import org.apache.ivy.plugins.resolver.IBiblioResolver
import org.apache.ivy.util.DefaultMessageLogger
import org.apache.ivy.util.Message

import griffon.util.BuildSettings
import griffon.util.GriffonUtil
import griffon.util.Metadata
import griffon.util.PlatformUtils
import org.apache.ivy.core.module.descriptor.ExcludeRule
import org.apache.ivy.plugins.parser.m2.PomReader
import org.apache.ivy.plugins.repository.file.FileResource
import org.apache.ivy.plugins.repository.file.FileRepository
import org.apache.ivy.plugins.parser.m2.PomDependencyMgt
import org.apache.ivy.core.module.id.ModuleId
import org.apache.ivy.core.report.ArtifactDownloadReport
import org.apache.ivy.util.url.CredentialsStore
import org.apache.ivy.core.module.descriptor.ModuleDescriptor
import org.apache.ivy.core.module.descriptor.DefaultDependencyArtifactDescriptor
import org.apache.ivy.plugins.latest.LatestTimeStrategy
import org.apache.ivy.util.MessageLogger
import org.apache.ivy.core.module.descriptor.Artifact
import org.apache.ivy.core.report.ConfigurationResolveReport
import org.apache.ivy.core.report.DownloadReport
import org.apache.ivy.core.report.DownloadStatus
import org.apache.ivy.plugins.matcher.PatternMatcher
import org.apache.ivy.plugins.matcher.ExactPatternMatcher
import org.apache.ivy.core.module.descriptor.DefaultExcludeRule
import org.apache.ivy.core.module.id.ArtifactId
import org.apache.ivy.core.module.descriptor.DefaultDependencyDescriptor
import org.apache.ivy.plugins.repository.TransferListener
import java.util.concurrent.ConcurrentLinkedQueue
import org.apache.ivy.plugins.resolver.RepositoryResolver

/**
 * Implementation that uses Apache Ivy under the hood
 *
 * @author Graeme Rocher (Grails 1.2)
 */
class IvyDependencyManager extends AbstractIvyDependencyManager implements DependencyResolver, DependencyDefinitionParser {
    private hasApplicationDependencies = false
    ResolveEngine resolveEngine
    BuildSettings buildSettings
    IvySettings ivySettings
    MessageLogger logger
    Metadata metadata
    ChainResolver chainResolver = new ChainResolver(name:"default",returnFirst:true)
    DefaultModuleDescriptor moduleDescriptor
    DefaultDependencyDescriptor currentDependencyDescriptor
    Collection repositoryData = new ConcurrentLinkedQueue()
    Collection<String> configuredPlugins = new ConcurrentLinkedQueue()
    Collection<String> usedConfigurations = new ConcurrentLinkedQueue()
    Collection moduleExcludes = new ConcurrentLinkedQueue()
    TransferListener transferListener

    boolean readPom = false
    boolean inheritsAll = false
    boolean resolveErrors = false

    /**
     * Creates a new IvyDependencyManager instance
     */
    IvyDependencyManager(String applicationName, String applicationVersion, BuildSettings settings=null, Metadata metadata = null) {
        ivySettings = new IvySettings()

        ivySettings.defaultInit()
        // don't cache for snapshots
        if(settings?.griffonVersion?.endsWith("SNAPSHOT")) {
            ivySettings.setDefaultUseOrigin(true) 
        }

        ivySettings.validate = false
        chainResolver.settings = ivySettings
        def eventManager = new EventManager()
        def sortEngine = new SortEngine(ivySettings)
        resolveEngine = new ResolveEngine(ivySettings,eventManager,sortEngine)
        resolveEngine.dictatorResolver = chainResolver

        this.applicationName = applicationName
        this.applicationVersion = applicationVersion
        this.buildSettings = settings
        this.metadata = metadata

        // addPlatformSpecificResolvers(applicationName)
    }

    void addPlatformSpecificResolvers(String applicationName) {
        PlatformUtils.doForAllPlatforms { platformKey, platformValue ->
            addFlatDirResolver(
                name: "${applicationName}-${platformKey}",
                dirs: "lib/${platformKey}"
            )
        }
    }

    /**
     * Allows settings an alternative chain resolver to be used
     * @param resolver The resolver to be used
     */
    void setChainResolver(ChainResolver resolver) {
        this.chainResolver = resolver
        resolveEngine.dictatorResolver = chainResolver
    }

    /**
     * Sets the default message logger used by Ivy
     *
     * @param logger
     */
    void setLogger(MessageLogger logger) {
        Message.setDefaultLogger logger
        this.logger = logger
    }
    
    MessageLogger getLogger() { this.logger }

     /**
      * @return The current chain resolver 
     */
    ChainResolver getChainResolver() { chainResolver }

    /**
     * Resets the Griffon plugin resolver if it is used
     */
    void resetGriffonPluginsResolver() {
       def resolver = chainResolver.resolvers.find { it.name == 'griffonPlugins' }
       chainResolver.resolvers.remove(resolver)
       chainResolver.resolvers.add(new GriffonPluginsDirectoryResolver(buildSettings, ivySettings))
    }
    /**
     * Returns true if the application has any dependencies that are not inherited
     * from the framework or other plugins
     */
    boolean hasApplicationDependencies() { this.hasApplicationDependencies }

    /**
     * Serializes the parsed dependencies using the given builder.
     *
     * @param builder A builder such as groovy.xml.MarkupBuilder
     */
    void serialize(builder, boolean createRoot = true) {
        if (createRoot) {
            builder.dependencies {
                serializeResolvers(builder)
                serializeDependencies(builder)
            }
        }
        else {
            serializeResolvers(builder)
            serializeDependencies(builder)
        }
    }

    private serializeResolvers(builder) {
        builder.resolvers {
            for(resolverData in repositoryData) {
                if(resolverData.name=='griffonHome') continue

                builder.resolver resolverData
            }
        }
    }

    private serializeDependencies(builder) {
        for (EnhancedDefaultDependencyDescriptor dd in dependencyDescriptors) {
            // dependencies inherited by Griffon' global config are not included
            if(dd.inherited) continue
            
            def mrid = dd.dependencyRevisionId
            builder.dependency( group: mrid.organisation, name: mrid.name, version: mrid.revision, conf: dd.scope, transitive: dd.transitive ) {
                for(ExcludeRule er in dd.allExcludeRules) {
                   def mid = er.id.moduleId
                   excludes group:mid.organisation,name:mid.name
                }
            }
        }
    }

    /**
     * Obtains the default dependency definitions for the given Griffon version
     */
    static Closure getDefaultDependencies(String griffonVersion) {
        // String antVersion = '1.8.1'
        // String slf4jVersion = '1.6.1'
        // String springVersion = '3.0.5.RELEASE'

        return {
            // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
            log "warn"
            repositories {
                griffonPlugins()
                griffonHome()
            }
            dependencies {
                // dependencies needed by the Griffon build system
                build "org.codehaus.gpars:gpars:0.9",
                      "org.tmatesoft.svnkit:svnkit:1.3.1",
                      "org.apache.ant:ant:$buildSettings.antVersion",
                      "org.apache.ant:ant-launcher:$buildSettings.antVersion",
                      "org.apache.ant:ant-junit:$buildSettings.antVersion",
                      "org.apache.ant:ant-nodeps:$buildSettings.antVersion",
                      "jline:jline:0.9.94",
                      "org.fusesource.jansi:jansi:1.4",
                      "commons-io:commons-io:1.4",
                      "commons-lang:commons-lang:2.5",
                      "org.codehaus.griffon:griffon-cli:$griffonVersion",
                      "org.codehaus.griffon:griffon-scripts:$griffonVersion",
                      "org.codehaus.griffon:griffon-rt:$griffonVersion",
                      "org.springframework:org.springframework.core:$buildSettings.springVersion",
                      "org.springframework:org.springframework.aop:$buildSettings.springVersion",
                      "org.springframework:org.springframework.aspects:$buildSettings.springVersion",
                      "org.springframework:org.springframework.asm:$buildSettings.springVersion",
                      "org.springframework:org.springframework.beans:$buildSettings.springVersion",
                      "org.springframework:org.springframework.context:$buildSettings.springVersion",
                      "org.springframework:org.springframework.context.support:$buildSettings.springVersion",
                      "org.springframework:org.springframework.expression:$buildSettings.springVersion",
                      "org.springframework:org.springframework.instrument:$buildSettings.springVersion"
                build("log4j:log4j:1.2.16",
                      "org.slf4j:slf4j-log4j12:$buildSettings.slf4jVersion",
                      "org.slf4j:slf4j-api:$buildSettings.slf4jVersion",
                      "org.slf4j:jcl-over-slf4j:$buildSettings.slf4jVersion",
                      "org.slf4j:jul-to-slf4j:$buildSettings.slf4jVersion") {
                    excludes 'mail', 'jms', 'jmxtools', 'jmxri'
                }
                build("org.codehaus.groovy:groovy-all:$buildSettings.groovyVersion") {
                    transitive = false
                }
                      
                docs("org.xhtmlrenderer:core-renderer:R8pre2",
                     "com.lowagie:itext:2.0.8",
                     "radeox:radeox:1.0-b2",
                     "org.grails:grails-docs:1.3.6") {
                    transitive = false
                }

                // dependencies needed for compilation
                compile("org.codehaus.groovy:groovy-all:$buildSettings.groovyVersion") {
                    transitive = false
                }
                compile "org.codehaus.griffon:griffon-rt:$griffonVersion",
                        "org.slf4j:slf4j-api:$buildSettings.slf4jVersion"

                // dependencies needed for running tests
                test "junit:junit:4.8.1",
                     "org.codehaus.griffon:griffon-cli:$griffonVersion"

                // logging
                runtime("log4j:log4j:1.2.16",
                        "org.slf4j:slf4j-log4j12:$buildSettings.slf4jVersion",
                        "org.slf4j:jcl-over-slf4j:$buildSettings.slf4jVersion",
                        "org.slf4j:jul-to-slf4j:$buildSettings.slf4jVersion") {
                    excludes 'mail', 'jms', 'jmxtools', 'jmxri'
                }
            }
        }
    }

    /**
     * Returns all of the dependency descriptors for dependencies of the application and not
     * those inherited from frameworks or plugins
     */
    Set<DependencyDescriptor> getApplicationDependencyDescriptors(String scope = null) {
        dependencyDescriptors.findAll { EnhancedDefaultDependencyDescriptor dd ->
            !dd.inherited && (!scope || dd.scope == scope)
        }
    }

    /**
     * Returns all the dependency descriptors for dependencies of a plugin that have been exported for use in the application
     */
    Set<DependencyDescriptor> getExportedDependencyDescriptors(String scope = null) {
        getApplicationDependencyDescriptors(scope).findAll { it.exported }
    }

    boolean isExcluded(String name) {
        def aid = createExcludeArtifactId(name)
        return moduleDescriptor.doesExclude(configurationNames, aid)
    }

    /**
     * For usages such as addPluginDependency("foo", [group:"junit", name:"junit", version:"4.8.1"])
     *
     * This method is designed to be used by the internal framework and plugins and not be end users.
     * The idea is that plugins can provide dependencies at runtime which are then inherited by
     * the user's dependency configuration
     *
     * A user can however override a plugin's dependencies inside the dependency resolution DSL
     */
    void addPluginDependency(String pluginName, Map args) {
        // do nothing if the dependencies of the plugin are configured by the application
        if(isPluginConfiguredByApplication(pluginName)) return
        if(args?.group && args?.name && args?.version) {
             def transitive = getBooleanValue(args, 'transitive')
             def exported = getBooleanValue(args, 'export')
             def scope = args.conf ?: 'runtime'
             def mrid = ModuleRevisionId.newInstance(args.group, args.name, args.version)
             def dd = new EnhancedDefaultDependencyDescriptor(mrid, true, transitive, scope)
             dd.exported = exported
             dd.inherited=true
             dd.plugin = pluginName
             configureDependencyDescriptor(dd, scope)
             if(args.excludes) {
                 for(ex in excludes) {
                     dd.exclude(ex)
                 }                 
            }
            addDependencyDescriptor dd
        }
    }

    /**
     * For usages such as addApplicationDependency(group:"junit", name:"junit", version:"4.8.1")
     *
     * This method is designed to be used by the internal framework and plugins and not be end users.
     * The idea is that applications can provide platform specific dependencies at buldtime 
     */
    void addApplicationDependency(Map args) {
        if(args?.group && args?.name && args?.version) {
             def transitive = getBooleanValue(args, 'transitive')
             def scope = args.conf ?: 'runtime'
             def mrid = ModuleRevisionId.newInstance(args.group, args.name, args.version)
             def dd = new EnhancedDefaultDependencyDescriptor(mrid, true, transitive, scope)
             dd.exported = true
             dd.inherited = false
             configureDependencyDescriptor(dd, scope)
             if(args.excludes) {
                 for(ex in excludes) {
                     dd.exclude(ex)
                 }
            }
            addDependencyDescriptor dd
        }
    }

    boolean isPluginConfiguredByApplication(String name) {
        (configuredPlugins.contains(name) || configuredPlugins.contains(GriffonUtil.getPropertyNameForLowerCaseHyphenSeparatedName(name)))        
    }

    def configureDependencyDescriptor(EnhancedDefaultDependencyDescriptor dependencyDescriptor, String scope, Closure dependencyConfigurer=null, boolean pluginMode = false) {
        if(!usedConfigurations.contains(scope)) {
            usedConfigurations << scope
        }

        try {
            this.currentDependencyDescriptor = dependencyDescriptor
            if (dependencyConfigurer) {
                dependencyConfigurer.resolveStrategy = Closure.DELEGATE_ONLY
                dependencyConfigurer.setDelegate(dependencyDescriptor)
                dependencyConfigurer.call()
            }
        }
        finally {
            this.currentDependencyDescriptor = null
        }
        if (dependencyDescriptor.getModuleConfigurations().length == 0){
            def mappings = configurationMappings[scope]
            if(mappings) {
                for(m in mappings) {
                    dependencyDescriptor.addDependencyConfiguration scope, m
                }
            }
        }
        if(!dependencyDescriptor.inherited) {
            hasApplicationDependencies = true
        }
        if(pluginMode) {
            def name = dependencyDescriptor.dependencyId.name
            pluginDependencyNames << name
            pluginDependencyDescriptors << dependencyDescriptor
            pluginNameToDescriptorMap[name] = dependencyDescriptor
        } else {
            dependencyDescriptors << dependencyDescriptor
            if(dependencyDescriptor.isExportedToApplication())
            moduleDescriptor.addDependency dependencyDescriptor
        }
    }

    Set<ModuleRevisionId> getModuleRevisionIds(String org) { orgToDepMap[org] }

    /**
     * Lists all known dependencies for the given configuration name (defaults to all dependencies)
     */
    IvyNode[] listDependencies(String conf = null) {
        def options = new ResolveOptions()
        if(conf) {
            options.confs = [conf] as String[]
        }

        resolveEngine.getDependencies(moduleDescriptor, options, new ResolveReport(moduleDescriptor))
    }

    ResolveReport resolveDependencies(Configuration conf) {
        resolveDependencies(conf.name)
    }
    
    /**
     * Performs a resolve of all dependencies for the given configuration,
     * potentially going out to the internet to download jars if they are not found locally
     */
    public ResolveReport resolveDependencies(String conf) {
        resolveErrors = false
        if(usedConfigurations.contains(conf) || conf == '') {
            def options = new ResolveOptions(checkIfChanged:false, outputReport:true, validate:false)
            if(conf) options.confs = [conf] as String[]

            ResolveReport resolve = resolveEngine.resolve(moduleDescriptor, options)
            resolveErrors = resolve.hasError()
            return resolve
        }

        // return an empty resolve report
        return new ResolveReport(moduleDescriptor)
    }
    
    /**
     * Similar to resolveDependencies, but will load the resolved dependencies into the 
     * application RootLoader if it exists
     * 
     * @return The ResolveReport
     * @throws IllegalStateException If no RootLoader exists
     */
    ResolveReport loadDependencies(String conf = '') {
        URLClassLoader rootLoader = getClass().classLoader.rootLoader
        if(rootLoader) {
            def urls = rootLoader.URLs.toList()
            ResolveReport report = resolveDependencies(conf)
            for(ArtifactDownloadReport downloadReport in report.allArtifactsReports) {
                def url = downloadReport.localFile.toURL()
                if(!urls.contains(url)) rootLoader.addURL(url)
            }
        } else {
            throw new IllegalStateException("No root loader found. Could not load dependencies. Note this method cannot be called when running in a WAR.")
        }
    }

    /**
     * Resolves only application dependencies and returns a list of the resolves JAR files
     */
    List<ArtifactDownloadReport> resolveApplicationDependencies(String conf = '') {
        ResolveReport report = resolveDependencies(conf)

        def descriptors = getApplicationDependencyDescriptors(conf)
        report.allArtifactsReports.findAll { ArtifactDownloadReport downloadReport ->
            def mrid = downloadReport.artifact.moduleRevisionId
            descriptors.any { DependencyDescriptor dd -> mrid == dd.dependencyRevisionId}
        }
    }

    /**
     * Resolves only plugin dependencies that should be exported to the application
     */
    List<ArtifactDownloadReport> resolveExportedDependencies(String conf='') {
        def descriptors = getExportedDependencyDescriptors(conf)
        resolveApplicationDependencies(conf)?.findAll { ArtifactDownloadReport downloadReport ->
            def mrid = downloadReport.artifact.moduleRevisionId
            descriptors.any { DependencyDescriptor dd -> mrid == dd.dependencyRevisionId}
        }
    }

    /**
     * Performs a resolve of all dependencies, potentially going out to the internet to download jars
     * if they are not found locally
     */
    ResolveReport resolveDependencies() {
        resolveDependencies('')
    }

    /**
     * Performs a resolve of declared plugin dependencies (zip files containing plugin distributions)
     */
    ResolveReport resolvePluginDependencies(String conf = '', Map args = [:]) {
        resolveErrors = false
        if(usedConfigurations.contains(conf) || conf == '') {

            if(args.checkIfChanged==null) args.checkIfChanged = true
            if(args.outputReport==null) args.outputReport = true
            if(args.validate==null) args.validate = false

            def options = new ResolveOptions(args)
            if (conf) {
                options.confs = [conf] as String[]
            }

            def md = createModuleDescriptor()
            for(dd in pluginDependencyDescriptors) {
                md.addDependency dd
            }
            if(!options.download) {
                def date = new Date()
                def report = new ResolveReport(md)
                def ivyNodes = resolveEngine.getDependencies(md, options, report)
                for(IvyNode node in ivyNodes) {
                    if(node.isLoaded()) {
                        for(Artifact a in node.allArtifacts) {
                            def origin = resolveEngine.locate(a)
                            def cr = new ConfigurationResolveReport(resolveEngine, md, conf, date, options)
                            def dr = new DownloadReport()
                            def adr = new ArtifactDownloadReport(a)
                            adr.artifactOrigin = origin
                            adr.downloadStatus = DownloadStatus.NO
                            dr.addArtifactReport(adr)
                            cr.addDependency(node, dr)
                            report.addReport(conf, cr)
                        }
                    }
                }
                return report
            }

            ResolveReport resolve = resolveEngine.resolve(md, options)
            resolveErrors = resolve.hasError()
            return resolve
        }

        // return an empty resolve report
        return new ResolveReport(createModuleDescriptor())
    }
    
     /**
     * Tests whether the given ModuleId is defined in the list of dependencies
     */
    boolean hasDependency(ModuleId mid) {
       return modules.contains(mid)
    }

    /**
     * Tests whether the given group and name are defined in the list of dependencies 
     */
    boolean hasDependency(String group, String name) {
        return hasDependency(ModuleId.newInstance(group, name))
    }

    /**
     * Parses the Ivy DSL definition
     */
    void parseDependencies(Closure definition) {
        if(definition && applicationName && applicationVersion) {
            if(this.moduleDescriptor == null) {                
                this.moduleDescriptor = createModuleDescriptor()
            }

            def evaluator = new IvyDomainSpecificLanguageEvaluator(this)
            definition.delegate = evaluator
            definition.resolveStrategy = Closure.DELEGATE_FIRST
            definition()
            evaluator = null

            if(readPom && buildSettings) {
                List dependencies = readDependenciesFromPOM()

                if(dependencies!=null){
                    for(PomDependencyMgt dep in dependencies) {
                        final String scope = dep.scope ?: 'runtime'
                        Message.debug("Read dependency [$dep.groupId:$dep.artifactId:$dep.version] (scope $scope) from Maven pom.xml")

                        def mrid = ModuleRevisionId.newInstance(dep.groupId, dep.artifactId, dep.version)
                        def mid = mrid.getModuleId()
                        if(!hasDependency(mid)) {
                            def dd = new EnhancedDefaultDependencyDescriptor(mrid, false, true, scope)
                            for(ModuleId ex in dep.excludedModules) {
                                dd.addRuleForModuleId(ex, scope)
                            }
                            configureDependencyDescriptor(dd, scope)
                            addDependencyDescriptor dd
                        }
                    }
                }
            }

            def installedPlugins = metadata?.getInstalledPlugins()
            if(installedPlugins) {
                for(entry in installedPlugins) {
                    if(!pluginDependencyNames.contains(entry.key)) {
                        def name = entry.key
                        def scope = "runtime"
                        def mrid = ModuleRevisionId.newInstance("org.codehaus.griffon.plugins", name, entry.value)
                        def dd = new EnhancedDefaultDependencyDescriptor(mrid, true, true, scope)
                        def artifact = new DefaultDependencyArtifactDescriptor(dd, name, "zip", "zip", null, null)
                        dd.addDependencyArtifact(scope, artifact)
                        metadataRegisteredPluginNames << name
                        configureDependencyDescriptor(dd, scope, null, true)
                        pluginDependencyDescriptors << dd
                    }
                }
            }
        }
    }

    List readDependenciesFromPOM() {
        def dependencies = null
        def pom = new File("${buildSettings.baseDir.path}/pom.xml")
        if (pom.exists()) {
            def reader = new PomReader(pom.toURL(), new FileResource(new FileRepository(), pom))
            dependencies = reader.getDependencies()
        }
        return dependencies
    }

    /**
     * Parses dependencies of a plugin
     *
     * @param pluginName the name of the plugin
     * @param definition the Ivy DSL definition
     */
    void parseDependencies(String pluginName,Closure definition) {
        if (definition) {
            if (moduleDescriptor == null) {
                throw new IllegalStateException("Call parseDependencies(Closure) first to parse the application dependencies")
            }

            def evaluator = new IvyDomainSpecificLanguageEvaluator(this)
            evaluator.currentPluginBeingConfigured = pluginName
            definition.delegate = evaluator
            definition.resolveStrategy = Closure.DELEGATE_FIRST
            definition()
        }
    }

    boolean getBooleanValue(dependency, String name) {
        return dependency.containsKey(name) ? Boolean.valueOf(dependency[name]) : true
    }

    void flatDirResolver(Map args) {
        def name = args.name?.toString()
        if(name && args.dirs) {
            def fileSystemResolver = new FileSystemResolver()
            fileSystemResolver.local = true
            fileSystemResolver.name = name

            def dirs = args.dirs instanceof Collection ? args.dirs : [args.dirs]

            repositoryData << ['type':'flatDir', name:name, dirs:dirs.join(',')]
            dirs.each { dir ->
               def path = new File(dir?.toString()).absolutePath
               fileSystemResolver.addIvyPattern( "${path}/[module]-[revision](-[classifier]).xml")
               fileSystemResolver.addArtifactPattern "${path}/[module]-[revision](-[classifier]).[ext]"
            }
            fileSystemResolver.settings = ivySettings

            addToChainResolver(fileSystemResolver)
        }
    }

    void addToChainResolver(org.apache.ivy.plugins.resolver.DependencyResolver resolver) {
        if(transferListener !=null && (resolver instanceof RepositoryResolver)) {
            ((RepositoryResolver)resolver).repository.addTransferListener transferListener
        }
        // Fix for GRAILS-5805
        synchronized(chainResolver.resolvers) {
            chainResolver.add resolver
        }
    }
}

class IvyDomainSpecificLanguageEvaluator {
    static final String WILDCARD = '*'

    boolean inherited = false
    boolean pluginMode = false
    String currentPluginBeingConfigured = null
    @Delegate IvyDependencyManager delegate

    IvyDomainSpecificLanguageEvaluator(IvyDependencyManager delegate) {
        this.delegate = delegate
    }

    void useOrigin(boolean b) {
        ivySettings.setDefaultUseOrigin(b)
    }

    void credentials(Closure c) {
        def creds = [:]
        c.delegate = creds
        c.resolveStrategy = Closure.DELEGATE_FIRST
        c.call()

        if(creds) {
            CredentialsStore.INSTANCE.addCredentials(creds.realm ?: null, creds.host ?: 'localhost', creds.username ?: '', creds.password ?: '')
        }
    }

    void pom(boolean b) {
        delegate.readPom = b
    }

    void excludes(Map exclude) {
        def anyExpression = PatternMatcher.ANY_EXPRESSION
        def mid = ModuleId.newInstance(exclude.group ?: anyExpression, exclude.name.toString())
        def aid = new ArtifactId(
                mid, anyExpression,
                anyExpression,
                anyExpression)

        def excludeRule = new DefaultExcludeRule(aid,
                        ExactPatternMatcher.INSTANCE, null)

        for(String conf in configurationNames) {
            excludeRule.addConfiguration conf
        }

        if(currentDependencyDescriptor==null) {
            moduleDescriptor.addExcludeRule(excludeRule)
        } else {
            for(String conf in configurationNames) {
                currentDependencyDescriptor.addExcludeRule(conf, excludeRule);
            }            
        }

    }
    void excludes(String... excludeList) {
        for(exclude in excludeList) {
            excludes name:exclude
        }
    }

    void inherits(String name, Closure configurer) {
        // plugins can't configure inheritance
        if(currentPluginBeingConfigured) return

        configurer?.delegate=this
        configurer?.call()

        def config = buildSettings?.config?.griffon
        if(config) {
            def dependencies = config[name]?.dependency?.resolution
            if(dependencies instanceof Closure) {
                try {
                    inherited = true
                    dependencies.delegate = this
                    dependencies.call()
                    moduleExcludes.clear()
                }
                finally {
                    inherited = false
                }

            }
        }
    }

    void inherits(String name) {
        inherits name, null
    }

    void plugins(Closure callable) {
         try {
             pluginMode = true
             callable.call()
         } finally {
             pluginMode = false
         }
    }

    void plugin(String name, Closure callable) {
        configuredPlugins << name

        try {
            currentPluginBeingConfigured = name
            callable?.delegate = this
            callable?.call()
        } finally {
            currentPluginBeingConfigured = null
        }
    }

    void log(String level) {
        // plugins can't configure log
        if(currentPluginBeingConfigured) return

        switch(level) {
            case "warn":
                setLogger(new DefaultMessageLogger(Message.MSG_WARN)); break
            case "error":
                setLogger(new DefaultMessageLogger(Message.MSG_ERR)); break
            case "info":
                setLogger(new DefaultMessageLogger(Message.MSG_INFO)); break
            case "debug":
                setLogger(new DefaultMessageLogger(Message.MSG_DEBUG)); break
            case "verbose":
                setLogger(new DefaultMessageLogger(Message.MSG_VERBOSE)); break
            default:
                setLogger(new DefaultMessageLogger(Message.MSG_WARN))
        }
        Message.setDefaultLogger logger
    }

    /**
     * Defines dependency resolvers
     */
    void resolvers(Closure resolvers) {
        repositories resolvers
    }

    /**
     * Same as #resolvers(Closure) 
     */
    void repositories(Closure repos) {
        repos?.delegate = this
        repos?.call()
    }

    void flatDir(Map args) {
        flatDirResolver(args)
    }

    void griffonPlugins() {
        if(isResolverNotAlreadyDefined('griffonPlugins')) {            
           repositoryData << ['type':'griffonPlugins', name:"griffonPlugins"]
           if(buildSettings!=null) {               
               def pluginResolver = new GriffonPluginsDirectoryResolver(buildSettings, ivySettings)
               addToChainResolver(pluginResolver)
           }
        }
    }

    void griffonHome() {
        if(isResolverNotAlreadyDefined('griffonHome')) {
            def griffonHome = buildSettings?.griffonHome?.absolutePath ?: System.getenv("GRIFFON_HOME")
            if(griffonHome) {
                flatDir(name:"griffonHome", dirs:"${griffonHome}/lib")
                flatDir(name:"griffonHome", dirs:"${griffonHome}/dist")
                if(griffonHome!='.') {
                    def resolver = createLocalPluginResolver("griffonHome", griffonHome)
                    addToChainResolver(resolver)
                }
            }
        }
    }

    FileSystemResolver createLocalPluginResolver(String name, String location) {
        def pluginResolver = new FileSystemResolver(name: name)
        pluginResolver.addArtifactPattern("${location}/plugins/griffon-[artifact]-[revision].[ext]")
        pluginResolver.settings = ivySettings
        pluginResolver.latestStrategy = new LatestTimeStrategy()
        pluginResolver.changingPattern = ".*SNAPSHOT"
        pluginResolver.setCheckmodified(true)
        return pluginResolver
    }

    private boolean isResolverNotAlreadyDefined(String name) {
        def resolver
        // Fix for GRAILS-5805
        synchronized(chainResolver.resolvers) {
          resolver = chainResolver.resolvers.any { it.name == name }
        }
        if(resolver) {
            Message.debug("Dependency resolver $name already defined. Ignoring...")
            return false
        }
        return true
    }

    void mavenRepo(String url) {
        if(isResolverNotAlreadyDefined(url)) {
            repositoryData << ['type':'mavenRepo', root:url, name:url, m2compatbile:true]
            def resolver = new IBiblioResolver(name: url, root: url, m2compatible: true, settings: ivySettings, changingPattern: ".*SNAPSHOT")
            addToChainResolver(resolver)
        }
    }

    void mavenRepo(Map args) {
        if(args && args.name) {
            if(isResolverNotAlreadyDefined(args.name)) {
                repositoryData << ( ['type':'mavenRepo'] + args )
                args.settings = ivySettings
                def resolver = new IBiblioResolver(args)
                addToChainResolver(resolver)
            }
        } else {
            Message.warn("A mavenRepo specified doesn't have a name argument. Please specify one!")
        }
    }

    void resolver(org.apache.ivy.plugins.resolver.DependencyResolver resolver) {
        if(resolver) {
            resolver.setSettings(ivySettings)
            addToChainResolver(resolver)
        }        
    }

    void ebr() {
        if(isResolverNotAlreadyDefined('ebr')) {
            repositoryData << ['type':'ebr']
            IBiblioResolver ebrReleaseResolver = new IBiblioResolver(name:"ebrRelease",
                                                                     root:"http://repository.springsource.com/maven/bundles/release",
                                                                     m2compatible:true,
                                                                     settings:ivySettings)
            addToChainResolver(ebrReleaseResolver)

            IBiblioResolver ebrExternalResolver = new IBiblioResolver(name:"ebrExternal",
                                                                      root:"http://repository.springsource.com/maven/bundles/external",
                                                                      m2compatible:true,
                                                                      settings:ivySettings)

            addToChainResolver(ebrExternalResolver)
        }
    }

    /**
     * Defines a repository that uses Griffon plugin repository format. Griffon repositories are
     * SVN repositories that follow a particular convention that is not Maven compatible.
     *
     * Ivy is flexible enough to allow the configuration of a resolver that resolves artifacts
     * against non-Maven repositories 
     */
    void griffonRepo(String url, String name=null) {
        if(isResolverNotAlreadyDefined(name ?: url)) {            
            repositoryData << ['type':'griffonRepo', url:url]
            def urlResolver = new GriffonRepoResolver(name ?: url, new URL(url) )
            urlResolver.addArtifactPattern("${url}/griffon-[artifact]/tags/RELEASE_*/griffon-[artifact]-[revision].[ext]")
            urlResolver.settings = ivySettings
            urlResolver.latestStrategy = new org.apache.ivy.plugins.latest.LatestTimeStrategy()
            urlResolver.changingPattern = ".*"
            urlResolver.setCheckmodified(true)
            addToChainResolver(urlResolver)
        }
    }

    void griffonCentral() {
        if(isResolverNotAlreadyDefined('griffonCentral')) {
            griffonRepo("http://svn.codehaus.org/griffon/plugins", "griffonCentral")
        }
    }

    void mavenCentral() {
        if(isResolverNotAlreadyDefined('mavenCentral')) {
            repositoryData << ['type':'mavenCentral']
            IBiblioResolver mavenResolver = new IBiblioResolver(name:"mavenCentral")
            mavenResolver.m2compatible = true
            mavenResolver.settings = ivySettings
            mavenResolver.changingPattern = ".*SNAPSHOT"
            addToChainResolver(mavenResolver)
        }
    }

    void mavenLocal(String repoPath = "${System.getProperty('user.home')}/.m2/repository") {
        if (isResolverNotAlreadyDefined('mavenLocal')) {
            repositoryData << ['type':'mavenLocal']
            FileSystemResolver localMavenResolver = new FileSystemResolver(name:'localMavenResolver');
            localMavenResolver.local = true
            localMavenResolver.m2compatible = true
            localMavenResolver.changingPattern = ".*SNAPSHOT"
            localMavenResolver.addIvyPattern(
                                "${repoPath}/[organisation]/[module]/[revision]/[module]-[revision](-[classifier]).pom")

            localMavenResolver.addArtifactPattern(
                    "${repoPath}/[organisation]/[module]/[revision]/[module]-[revision](-[classifier]).[ext]")
            
            localMavenResolver.settings = ivySettings
            addToChainResolver(localMavenResolver)
        }
    }

    void dependencies(Closure deps) {
        deps?.delegate = this
        deps?.call()
    }

    def invokeMethod(String name, args) {
        if(!args || !((args[0] instanceof CharSequence)||(args[0] instanceof Map)||(args[0] instanceof Collection))) {
            println "WARNING: Configurational method [$name] in griffon-app/conf/BuildConfig.groovy doesn't exist. Ignoring.."
        } else {
            def dependencies = args
            def callable
            if(dependencies && (dependencies[-1] instanceof Closure)) {
                callable = dependencies[-1]
                dependencies = dependencies[0..-2]
            }

            if(dependencies) {
                parseDependenciesInternal(dependencies, name, callable)
            }            
        }
    }

    private parseDependenciesInternal(dependencies, String scope, Closure dependencyConfigurer) {
        boolean usedArgs = false
        def parseDep = { dependency ->
                if ((dependency instanceof CharSequence)) {
                    def args = [:]
                    if(dependencies[-1] instanceof Map) {
                        args = dependencies[-1]
                        usedArgs = true
                    }
                    def depDefinition = dependency.toString()

                    def m = depDefinition =~ /([a-zA-Z0-9\-\/\._+=]*?):([a-zA-Z0-9\-\/\._+=]+?):([a-zA-Z0-9\-\/\._+=]+)/

                    if (m.matches()) {

                        String name = m[0][2]
                        boolean isExcluded = currentPluginBeingConfigured ? isExcludedFromPlugin(currentPluginBeingConfigured, name) : isExcluded(name)
                        if(!isExcluded) {
                            def group = m[0][1]
                            def version = m[0][3]
                            if(pluginMode) {
                                group = group ?: 'org.codehaus.griffon.plugins'
                            }
                            
                            def mrid = ModuleRevisionId.newInstance(group, name, version)

                            def dependencyDescriptor = new EnhancedDefaultDependencyDescriptor(mrid, false, getBooleanValue(args, 'transitive'), scope)

                            if(pluginMode) {
                                def artifact = new DefaultDependencyArtifactDescriptor(dependencyDescriptor, name, "zip", "zip", null, null )
                                dependencyDescriptor.addDependencyArtifact(scope, artifact)
                            } else {
                                def artifact = new DefaultDependencyArtifactDescriptor(dependencyDescriptor, name, "jar", "jar", null, null )
                                dependencyDescriptor.addDependencyArtifact(scope, artifact)                                
                                addDependency mrid
                            }
                            dependencyDescriptor.exported = getBooleanValue(args, 'export')
                            dependencyDescriptor.inherited = inherited || inheritsAll || currentPluginBeingConfigured

                            if(currentPluginBeingConfigured) {
                                dependencyDescriptor.plugin = currentPluginBeingConfigured
                            }
                            configureDependencyDescriptor(dependencyDescriptor, scope, dependencyConfigurer, pluginMode)
                        }
                    }
                    else {
                        println "WARNING: Specified dependency definition ${scope}(${depDefinition.inspect()}) is invalid! Skipping.."
                    }

                }
                else if(dependency instanceof Map) {
                    def name = dependency.name
                    if(!dependency.group && pluginMode) dependency.group = "org.codehaus.griffon.plugins"
                    
                    if(dependency.group && name && dependency.version) {
                       boolean isExcluded = currentPluginBeingConfigured ? isExcludedFromPlugin(currentPluginBeingConfigured, name) : isExcluded(name)
                       if(!isExcluded) {

                           def attrs = ["m:classifier":dependency.classifier ?: 'jar']
                           def mrid
                           if(dependency.branch) {
                               mrid = ModuleRevisionId.newInstance(dependency.group, name, dependency.branch, dependency.version, attrs)
                           }
                           else {
                               mrid = ModuleRevisionId.newInstance(dependency.group, name, dependency.version, attrs)
                           }


                           def dependencyDescriptor = new EnhancedDefaultDependencyDescriptor(mrid, false, getBooleanValue(dependency, 'transitive'), scope)
                           if(pluginMode) {
                               def artifact
                               if(dependency.classifier == 'plugin')
                                    artifact = new DefaultDependencyArtifactDescriptor(dependencyDescriptor, name, "xml", "xml", null, null )
                               else
                                    artifact = new DefaultDependencyArtifactDescriptor(dependencyDescriptor, name, "zip", "zip", null, null )

                               dependencyDescriptor.addDependencyArtifact(scope, artifact)
                           } else {
                               addDependency mrid
                           }

                           dependencyDescriptor.exported = getBooleanValue(dependency, 'export')
                           dependencyDescriptor.inherited = inherited || inheritsAll
                           if(currentPluginBeingConfigured) {
                               dependencyDescriptor.plugin = currentPluginBeingConfigured
                           }

                           configureDependencyDescriptor(dependencyDescriptor, scope, dependencyConfigurer, pluginMode)
                       }

                    }
                }
            }

            for(dep in dependencies) {
                parseDep dep
                if((dependencies[-1] == dep) && usedArgs) break
            }          
    }
}
