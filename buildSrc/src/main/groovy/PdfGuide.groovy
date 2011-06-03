import org.gradle.api.file.FileCollection
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import grails.doc.PdfBuilder

class PdfGuide extends DefaultTask {
    @OutputDirectory File outputFile
	@Input String pdfName
	
	public PdfGuide(){
		super()
		outputFile = new File("${project.buildDir}/pdfGuide")
	}
	
	@TaskAction
    def publish() {
		try {
            PdfBuilder.build(
                    basedir: project.buildDir.absolutePath,
                    home: project.file('grails-doc').absolutePath,
                    tool: 'pdf/gpars'
            )
        } catch (x) {
            // it's very likely that the stream is closed before
            // the renderer 'finishes' but it actually does
            // ignore for now
        }
    	
		project.file("${project.buildDir}/manual/guide/single.pdf")
			   .renameTo(new File(outputFile.absolutePath, pdfName).absolutePath)
	}
}