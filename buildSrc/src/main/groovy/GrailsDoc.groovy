import org.gradle.api.file.FileCollection
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

class GrailsDoc extends DefaultTask {
    @InputDirectory File srcDir
    @InputFiles File props
    @OutputDirectory File outputDir
	//@InputFiles File styleDir = 
	//@InputFiles File cssDir
	@InputFiles File imagesDir
	
    @TaskAction
    def publish() {
		ant.taskdef(name: 'docs', classname: 'grails.doc.ant.DocPublisherTask', 
			classpath: project.configurations.docs.asPath);
        ant.docs(src: srcDir.absolutePath,
                dest: outputDir.absolutePath,
                properties: props.absolutePath,
                //styleDir: styleDir,
                //cssDir: cssDir,
                imagesDir: imagesDir
        )
	}
}