/*
 * Copyright 2010-2011 the original author or authors.
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