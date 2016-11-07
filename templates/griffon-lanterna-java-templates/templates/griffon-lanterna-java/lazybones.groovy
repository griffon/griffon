import uk.co.cacoethes.util.NameType
import org.apache.commons.io.FileUtils

Map props = [:]
if (projectDir.name =~ /\-/) {
    props.project_class_name = transformText(projectDir.name, from: NameType.HYPHENATED, to: NameType.CAMEL_CASE)
} else {
    props.project_class_name = transformText(projectDir.name, from: NameType.PROPERTY, to: NameType.CAMEL_CASE)
}
project_name = props.project_name = transformText(props.project_class_name, from: NameType.CAMEL_CASE, to: NameType.HYPHENATED)
project_class_name = props.project_class_name

while (!(props.project_group = ask("Define value for 'group' [org.example]: ", "org.example", "group").trim())) {}
while (!(props.project_name = ask("Define value for 'artifactId' [" + project_name + "]: ", project_name , "artifactId").trim())) {}
props.project_version = ask("Define value for 'version' [0.1.0-SNAPSHOT]: ", "0.1.0-SNAPSHOT", "version")
while (!(props.griffon_version = ask("Define value for 'griffonVersion' [2.9.0-SNAPSHOT]: ", "2.9.0-SNAPSHOT", "griffonVersion").trim())) {}
while (!(props.project_package = ask("Define value for 'package' [" + props.project_group + "]: ", props.project_group, "package").trim())) {}
while (!(props.project_class_name = ask("Define value for 'className' [" + project_class_name + "]: ", project_class_name, "className").capitalize().trim())) {}
props.project_property_name = transformText(props.project_class_name, from: NameType.CAMEL_CASE, to: NameType.PROPERTY)
props.project_capitalized_name = props.project_property_name.capitalize()
String packagePath = props.project_package.replace('.' as char, '/' as char)

processTemplates 'pom.xml', props
processTemplates 'build.gradle', props
processTemplates 'settings.gradle', props
processTemplates 'gradle.properties', props
processTemplates 'src/main/java/*.java', props
processTemplates 'src/test/java/*.java', props
processTemplates 'griffon-app/*/*.java', props
processTemplates 'maven/distribution/bin/*', props

File mainSources = new File(projectDir, 'src/main/java')
File testSources = new File(projectDir, 'src/test/java')
File integrationTestSources = new File(projectDir, 'src/integration-test/java')
File binSources = new File(projectDir, 'maven/distribution/bin')

File mainSourcesPath = new File(mainSources, packagePath)
mainSourcesPath.mkdirs()
File testSourcesPath = new File(testSources, packagePath)
testSourcesPath.mkdirs()
File integrationTestSourcesPath = new File(integrationTestSources, packagePath)
integrationTestSourcesPath.mkdirs()

def renameFile = { File from, String path ->
    if (from.file) {
        File to = new File(path)
        to.parentFile.mkdirs()
        FileUtils.moveFile(from, to)
    }
}
mainSources.eachFile { File file ->
    renameFile(file, mainSourcesPath.absolutePath + '/' + file.name)
}
testSources.eachFile { File file ->
    renameFile(file, testSourcesPath.absolutePath + '/' + props.project_capitalized_name + file.name)
}
integrationTestSources.eachFile { File file ->
    renameFile(file, integrationTestSourcesPath.absolutePath + '/' + props.project_capitalized_name + file.name)
}

renameFile(new File(binSources, 'run-app'), binSources.absolutePath + '/' + props.project_name)
renameFile(new File(binSources, 'run-app.bat'), binSources.absolutePath + '/' + props.project_name + '.bat')

['controllers', 'models', 'services', 'views'].each { String category ->
    File artifactDir = new File(projectDir, "griffon-app/$category")
    artifactDir.eachFile { File file ->
        File artifactSourcesPath = new File(projectDir, "griffon-app/$category/$packagePath")
        artifactSourcesPath.mkdirs()
        renameFile(file, artifactSourcesPath.absolutePath + '/' + props.project_capitalized_name + file.name)
    }
}
