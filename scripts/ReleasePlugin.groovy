import org.tmatesoft.svn.core.io.SVNRepositoryFactory
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.io.*
import org.tmatesoft.svn.core.*
import org.tmatesoft.svn.core.auth.*
import org.tmatesoft.svn.core.wc.*
import org.tmatesoft.svn.core.io.diff.SVNDeltaGenerator;

defaultTarget ("A target for plug-in developers that uploads and commits the current plug-in as the latest revision. The command will prompt for your SVN login details.") {
    releasePlugin()
}

includeTargets << griffonScript("PackagePlugin")

pluginSVN = "https://svn.codehaus.org/griffon/plugins"
authManager = null
commitMessage = null
trunk = null
latestRelease = null
versionedRelease = null


target(processAuth:"Prompts user for login details to create authentication manager") {
    if(!authManager) {
        ant.input(message:"Please enter your SVN username:", addproperty:"user.svn.username")
        ant.input(message:"Please enter your SVN password:", addproperty:"user.svn.password")

        def username = ant.antProject.properties."user.svn.username"
        def password = ant.antProject.properties."user.svn.password"

        authManager = SVNWCUtil.createDefaultAuthenticationManager( username , password )
    }
}
target(releasePlugin: "The implementation target") {
    //depends(packagePlugin)
    depends(parseArguments,packagePlugin, processAuth)

    remoteLocation = "${pluginSVN}/griffon-${pluginName}"
    trunk = SVNURL.parseURIDecoded("${remoteLocation}/trunk")
    latestRelease = "${remoteLocation}/tags/LATEST_RELEASE"
    versionedRelease = "${remoteLocation}/tags/RELEASE_${plugin.version.toString().replaceAll('\\.','_')}"


    FSRepositoryFactory.setup()
    DAVRepositoryFactory.setup()

    try {
        if(argsMap.pluginlist) {
            commitNewGlobalPluginList()
        }
        else {
            def statusClient = new SVNStatusClient((ISVNAuthenticationManager)authManager,null)

            boolean imported = false
            try {
                // get status of base directory, if this fails exception will be thrown
                statusClient.doStatus(baseFile, true)
            }
            catch(SVNException ex) {
                // error with status, not in repo, attempt import.
                importToSVN()
                commitNewGlobalPluginList()
                imported = true
            }
            if(!imported) {
                updateAndCommitLatest()
                tagPluginRelease()
                commitNewGlobalPluginList()
                event('StatusFinal', ["Plug-in release successfully published"])
            }
        }
    }
    catch(Exception e) {
        event('StatusFinal', ["Error occurred with release-plugin: ${e.message}"])
        e.printStackTrace()
    }
}

target(commitNewGlobalPluginList:"updates the plugins.xml descriptor stored in the repo") {

   if(!commitMessage) askForMessage()
   ant.delete(file:pluginsListFile)
   println "Building plugin list for commit..."
   updatePluginsListManually()

    def pluginMetaDir = new File("${pluginsHome}/.plugin-meta")
    def updateClient = new SVNUpdateClient((ISVNAuthenticationManager)authManager, null)
    def importClient = new SVNCommitClient((ISVNAuthenticationManager)authManager, null)
    String remotePluginMetadata = "${pluginSVN}/.plugin-meta"
    if(!pluginMetaDir.exists()) {
       println "Checking out locally to '${pluginMetaDir}'."
       checkoutOrImportPluginMetadata(pluginMetaDir, remotePluginMetadata, updateClient, importClient)
   }
   else {
       try {
           updateClient.doUpdate(pluginMetaDir, SVNRevision.HEAD, true)
       } catch (SVNException e) {
           println "Plugin meta directory corrupt, checking out again"
           checkoutOrImportPluginMetadata(pluginMetaDir, remotePluginMetadata, updateClient, importClient)
       }
       ant.copy(file:pluginsListFile, todir:pluginMetaDir, overwrite:true)

       def commit = importClient.doCommit([pluginMetaDir] as File[],false,commitMessage,true,true)

       println "Committed revision ${commit.newRevision} of plugins-list.xml."
   }


}

private checkoutOrImportPluginMetadata (File pluginMetaDir, String remotePluginMetadata, SVNUpdateClient updateClient, SVNCommitClient importClient) {
    def svnURL = SVNURL.parseURIDecoded (remotePluginMetadata)
    try {
        updateClient.doCheckout(svnURL, pluginMetaDir, SVNRevision.HEAD, SVNRevision.HEAD, true)
    } catch (SVNException e) {
        println "Importing plugin meta data to ${remotePluginMetadata}. Please wait..."

        ant.mkdir(dir: pluginMetaDir)
        ant.copy(file: pluginsListFile, todir: pluginMetaDir)

        importClient.doImport(pluginMetaDir, svnURL, commitMessage, true)
        ant.delete(dir: pluginMetaDir)
        updateClient.doCheckout(svnURL, pluginMetaDir, SVNRevision.HEAD, SVNRevision.HEAD, true)

    }
}


target(checkInPluginZip:"Checks in the plug-in zip if it has not been checked in already") {
    def statusClient = new SVNStatusClient((ISVNAuthenticationManager)authManager,null)
    def wcClient = new SVNWCClient((ISVNAuthenticationManager)authManager,null)
    def pluginFile = new File(pluginZip)
    def addPluginFile = false
    try {
        def status = statusClient.doStatus(pluginFile, true)
        if(status.kind == SVNNodeKind.NONE || status.kind == SVNNodeKind.UNKNOWN) addPluginFile = true
    }
    catch(SVNException) {
        // not checked in add and commit
        addPluginFile = true
    }
    if(addPluginFile) wcClient.doAdd(pluginFile,true,false,false,false)
    def pluginXml = new File("${basedir}/plugin.xml")
    addPluginFile = false
    try {
        def status = statusClient.doStatus(pluginXml, true)
        if(status.kind == SVNNodeKind.NONE || status.kind == SVNNodeKind.UNKNOWN) addPluginFile = true
    }
    catch(SVNException e) {
        addPluginFile = true
    }
    if(addPluginFile) wcClient.doAdd(pluginXml, true, false,false,false)
}
target(updateAndCommitLatest:"Commits the latest revision of the Plug-in") {
   def result = confirmInput("""
This command will perform the following steps to release your plug-in into Griffon' SVN repository:
* Update your sources to the HEAD revision
* Commit any changes you've made to SVN
* Tag the release

NOTE: This command will not add new resources for you, if you have additional sources to add please run 'svn add' before running this command.
NOTE: Make sure you have updated the version number in your *GriffonPlugin.groovy descriptor.

Are you sure you wish to proceed?
""")
    if(result == 'n') exit(0)

        checkInPluginZip()


    updateClient = new SVNUpdateClient((ISVNAuthenticationManager)authManager, null)

    println "Updating from SVN '${remoteLocation}'"
    long r = updateClient.doUpdate(baseFile, SVNRevision.HEAD, true)
    println "Updated to revision ${r}. Committing local, please wait..."

    commitClient = new SVNCommitClient((ISVNAuthenticationManager)authManager, null)

    if(!commitMessage) askForMessage()

    println "Committing code. Please wait..."

    def commit = commitClient.doCommit([baseFile] as File[],false,commitMessage,true,true)

    println "Committed revision ${commit.newRevision}."
}

target(importToSVN:"Imports a plug-in project to Griffon' remote SVN repository") {
    checkOutDir = new File("${baseFile.parentFile.absolutePath}/checkout/${baseFile.name}")

    def result = confirmInput("""
This plug-in project is not currently in the repository, this command will now:
* Perform an SVN import into the repository
* Tag the plug-in project as the LATEST_RELEASE
* Checkout the imported version of the project from SVN to '${checkOutDir}'
Are you sure you wish to proceed?
    """)
    if(result == 'n') exit(0)

    ant.unzip(src:pluginZip, dest:"${basedir}/unzipped")
    ant.copy(file:pluginZip, todir:"${basedir}/unzipped")


    importClient = new SVNCommitClient((ISVNAuthenticationManager)authManager, null)
    askForMessage()

    println "Importing project to ${remoteLocation}. Please wait..."

    def svnURL = SVNURL.parseURIDecoded("${remoteLocation}/trunk")
    importClient.doImport(new File("${basedir}/unzipped"),svnURL,commitMessage,true)
    println "Plug-in project imported to SVN at location '${remoteLocation}/trunk'"

    tagPluginRelease()

    ant.delete(dir:"${basedir}/unzipped")


    checkOutDir.parentFile.mkdirs()

    updateClient = new SVNUpdateClient((ISVNAuthenticationManager)authManager, null)
    println "Checking out locally to '${checkOutDir}'."
    updateClient.doCheckout(svnURL, checkOutDir, SVNRevision.HEAD,SVNRevision.HEAD, true)

    event('StatusFinal', ["""
Completed SVN project import. If you are in terminal navigate to imported project with:
cd ${checkOutDir}

Future changes should be made to the SVN controlled sources!"""])
}

target(tagPluginRelease:"Tags a plugin-in with the LATEST_RELEASE tag and version tag within the /tags area of SVN") {

    copyClient = new SVNCopyClient((ISVNAuthenticationManager)authManager, null)
    commitClient = new SVNCommitClient((ISVNAuthenticationManager)authManager, null)

    if(!commitMessage) askForMessage()

    println "Tagging release. Please wait..."

    tags = SVNURL.parseURIDecoded("${remoteLocation}/tags")
    latest = SVNURL.parseURIDecoded(latestRelease)
    release = SVNURL.parseURIDecoded(versionedRelease)

    try { commitClient.doMkDir([tags] as SVNURL[], commitMessage) }
    catch(SVNException e) {
        // ok - already exists
    }
    try { commitClient.doDelete([latest] as SVNURL[], commitMessage) }
    catch(SVNException e) {
      // ok - the tag doesn't exist yet
    }
    try { commitClient.doDelete([release] as SVNURL[], commitMessage) }
    catch(SVNException e) {
      // ok - the tag doesn't exist yet
    }

    repository = SVNRepositoryFactory.create( trunk )

    def commit

    // First tag this release with the version number.
    println "Tagging version release, please wait..."
    def copySource = new SVNCopySource(SVNRevision.HEAD, SVNRevision.HEAD, trunk)
    commit = copyClient.doCopy([copySource] as SVNCopySource[], release, false, false, true, commitMessage, new SVNProperties())
    println "Copied trunk to ${versionedRelease} with revision ${commit.newRevision} on ${commit.date}"

    // And now make it the latest release.
    println "Tagging latest release, please wait..."
    copySource = new SVNCopySource(SVNRevision.HEAD, SVNRevision.HEAD, release)
    commit = copyClient.doCopy([copySource] as SVNCopySource[], latest, false, false, true, commitMessage, new SVNProperties())
    println "Copied trunk to ${latestRelease} with revision ${commit.newRevision} on ${commit.date}"
}

target(askForMessage:"Asks for the users commit message") {
    ant.input(message:"Enter a SVN commit message:", addproperty:"commit.message")
    commitMessage = ant.antProject.properties."commit.message"
}
