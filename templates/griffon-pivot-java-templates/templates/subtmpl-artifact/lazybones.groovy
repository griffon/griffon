import org.apache.commons.io.FileUtils
import org.apache.commons.io.FilenameUtils
import uk.co.cacoethes.util.NameType

import static org.apache.commons.io.FilenameUtils.concat

def props = [:]
def artifactBaseName
def className
if (tmplQualifiers) {
    artifactBaseName = tmplQualifiers[0].toLowerCase()
    if (tmplQualifiers[1]) props.project_package = tmplQualifiers[1]
    if (tmplQualifiers[2]) className = tmplQualifiers[2].capitalize()
} else {
    def templateNames = templateDir.list().findAll {
        it.endsWith('.java') || it.endsWith('.groovy')
    }.collect { it.toLowerCase() - '.java' - '.groovy' }.sort() - 'lazybones'
    println '\nThe following artifact templates are available\n'
	templateNames << 'mvcgroup'
    templateNames.each { println "  $it" }
    println ' '
    while (!(artifactBaseName in templateNames )) {
        artifactBaseName = ask("Which type of artifact do you want to generate? ", null, "type")?.toLowerCase()
    }
}

if (!props.project_package) {
	while (!(props.project_package = ask("Define value for 'package' [" + parentParams.package + "]: ", parentParams.package, "package")?.trim())) {}
}

while (!(className = ask("Define value for 'class' name: ", null, "class")?.capitalize()?.trim())) {}

processArtifact = { artifactClass, artifactTemplate, artifactType, artifactPath ->
    props.project_class_name = artifactClass
    String suffix = artifactTemplate.capitalize()
    String templateName = suffix + '.java'
    File templateFile = new File(templateDir, templateName)
    if (!templateFile.exists()) templateName = suffix + '.groovy'

    props.artifact_type = artifactType
    if (props.project_class_name.endsWith(suffix)) {
        props.project_class_name -= suffix
    }
    props.name = transformText(props.project_class_name, from: NameType.CAMEL_CASE, to: NameType.HYPHENATED)

    processTemplates(templateName, props)

    String packagePath = props.project_package.replace('.' as char, '/' as char)
    def filename = props.project_class_name + templateName
    def destFile = new File(projectDir, concat(concat(artifactPath, packagePath), filename))
    destFile.parentFile.mkdirs()

    if (destFile.exists()) {
        def overwrite = ask("Overwrite ${destFile.absolutePath} [yes]", 'true', 'overwrite')
        if (overwrite in [true, 'yes', 'y', 'Y', 'true']) {
            destFile.delete()
            FileUtils.moveFile(new File(templateDir, templateName), destFile)
            println "Created new artifact ${FilenameUtils.normalize(destFile.path)}"
        }
    } else {
        FileUtils.moveFile(new File(templateDir, templateName), destFile)
        println "Created new artifact ${FilenameUtils.normalize(destFile.path)}"
    }
}

if (artifactBaseName == 'mvcgroup') {
    String groupName = transformText(className, from: NameType.CAMEL_CASE, to: NameType.HYPHENATED)
    processArtifact(className, 'Model', 'model', 'griffon-app/models')
    processArtifact(className, 'View', 'view', 'griffon-app/views')
    processArtifact(className, 'Controller', 'controller', 'griffon-app/controllers')
    processArtifact(className + 'Controller', 'Test', 'controller', 'src/test/java')
    processArtifact(className, 'IntegrationTest', 'view', 'src/integration-test/java')
    println "Do not forget to add the group '$groupName' to griffon-app/conf/Config.java"
} else if (artifactBaseName == 'test') {
    String artifactType = ''
    ['Controller', 'Model', 'Service'].each { s ->
        if (className.endsWith(s)) {
            artifactType = s.toLowerCase()
        }
    }
    processArtifact(className, 'Test', artifactType, 'src/test/java')
} else if (artifactBaseName == 'integrationtest') {
    processArtifact(className, 'IntegrationTest', 'view', 'src/integration-test/java')
} else if (artifactBaseName == 'spec') {
    String artifactType = ''
    ['Controller', 'Model', 'Service'].each { s ->
        if (className.endsWith(s)) {
            artifactType = s.toLowerCase()
        }
    }
    processArtifact(className, 'Spec', artifactType, 'src/test/groovy')
} else if (artifactBaseName == 'integrationspec') {
    processArtifact(className, 'IntegrationSpec', 'view', 'src/integration-test/groovy')
} else {
    processArtifact(className, artifactBaseName.capitalize(), artifactBaseName, 'griffon-app/' + artifactBaseName + 's')
    if (artifactBaseName != 'view') {
        processArtifact(className + artifactBaseName.capitalize(), 'Test', artifactBaseName, 'src/test/java')
    } else {
        processArtifact(className, 'IntegrationTest', 'view', 'src/integration-test/java')
    }
}