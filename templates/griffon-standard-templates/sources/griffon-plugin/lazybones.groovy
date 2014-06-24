import uk.co.cacoethes.util.NameType

Map props = [:]
File projectDir = targetDir instanceof File ? targetDir : new File(String.valueOf(targetDir))
String projectName = projectDir.name
projectName = !projectName.startsWith('griffon-') ? 'griffon-' + projectName : projectName
projectName += !projectName.endsWith('-plugin') ? '-plugin' : ''
String pluginName = projectName - 'griffon-' - '-plugin'

if (projectName =~ /\-/) {
    props.project_class_name = transformText(pluginName, from: NameType.HYPHENATED, to: NameType.CAMEL_CASE).capitalize()
    pluginName = pluginName.toLowerCase()
} else {
    props.project_class_name = transformText(pluginName, from: NameType.NATURAL, to: NameType.CAMEL_CASE).capitalize()
    pluginName = transformText(pluginName, from: NameType.NATURAL, to: NameType.HYPHENATED).toLowerCase()()
}

props.plugin_name = pluginName
props.plugin_full_name = 'griffon-' + pluginName + '-plugin'
props.plugin_natural_name = transformText(pluginName, from: NameType.HYPHENATED, to: NameType.NATURAL)
props.plugin_full_natural_name = transformText(props.plugin_full_name, from: NameType.HYPHENATED, to: NameType.NATURAL)

props.project_year = Calendar.instance.get(Calendar.YEAR)
props.project_author = System.getProperty('user.name')
props.project_group = ask("Define value for 'group' [org.codehaus.griffon.plugins]: ", "org.codehaus.griffon.plugins", "group")
props.project_version = ask("Define value for 'version' [0.1.0.SNAPSHOT]: ", "0.1.0.SNAPSHOT", "version")
props.project_package = ask("Define value for 'package' [org.codehaus.griffon.runtime." +pluginName + "]: ",
                        'org.codehaus.griffon.runtime.' + pluginName, "package")
props.griffon_version = ask("Define value for 'griffonVersion' [@griffon.version@]: ", "@griffon.version@", "griffonVersion")
props.project_website = ask("Define value for 'website' [http://artifacts.griffon-framework.org/plugin/" + pluginName +"]: ",
                        "http://artifacts.griffon-framework.org/plugin/" + pluginName, "website")
props.project_issue_tracker = ask("Define value for 'issueTracker' [http://jira.codehaus.org/browse/griffon]: ",
                              "http://jira.codehaus.org/browse/griffon", "issueTracker")
props.project_vcs = ask("Define value for 'vcs' [https://github.com/griffon/" + props.plugin_full_name +".git]: ",
                    "https://github.com/griffon/" + props.plugin_full_name, "vcs")

String packagePath = props.project_package.replace('.' as char, '/' as char)

processTemplates 'build.gradle', props
processTemplates 'gradle/publishing.gradle', props
processTemplates 'subprojects/guide/guide.gradle', props
processTemplates 'gradle.properties', props
processTemplates 'settings.gradle', props
processTemplates 'subprojects/plugin/gradle.properties', props
processTemplates 'subprojects/plugin/src/main/java/*.java', props
processTemplates 'subprojects/guide/src/javadoc/*.html', props

File mainSources = new File(projectDir, 'subprojects/plugin/src/main/java')

File mainSourcesPath = new File(mainSources, packagePath)
mainSourcesPath.mkdirs()

mainSources.eachFile { File file ->
    file.renameTo(mainSourcesPath.absolutePath + '/' + props.project_class_name + file.name)
}

File pluginDir = new File(projectDir, 'subprojects/plugin')
File guideDir = new File(projectDir, 'subprojects/guide')
new File(pluginDir, 'plugin.gradle').renameTo(pluginDir.absolutePath + '/griffon-' + pluginName + '.gradle')
new File(guideDir, 'guide.gradle').renameTo(guideDir.absolutePath + '/griffon-' + pluginName + '-guide.gradle')
pluginDir.renameTo(projectDir.absolutePath + '/subprojects/griffon-' + pluginName)
guideDir.renameTo(projectDir.absolutePath + '/subprojects/griffon-' + pluginName + '-guide')

projectDir.renameTo(projectName)