/*
 * Copyright 2004-2012 the original author or authors.
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

import org.apache.ivy.core.module.id.ModuleRevisionId
import org.apache.ivy.core.report.ArtifactDownloadReport
import org.apache.ivy.core.report.ResolveReport
import org.apache.ivy.core.resolve.IvyNode

/**
 * Exception thrown when dependencies fail to resolve
 *
 * @author Graeme Rocher (Grails 2.0)
 */
class ResolveException extends RuntimeException {
    ResolveReport resolveReport;

    public ResolveException(ResolveReport resolveReport) {
        this.resolveReport = resolveReport;
    }

    @Override
    public String getMessage() {
        def configurations = resolveReport.configurations
        def unresolvedDependencies = []
        for (conf in configurations) {
            final confReport = resolveReport.getConfigurationReport(conf)
            for (IvyNode node in confReport.getUnresolvedDependencies()) {
                unresolvedDependencies << node.id
            }
            def failedDownloads = confReport.getFailedArtifactsReports()
            if (failedDownloads) {
                for (ArtifactDownloadReport dl in failedDownloads) {
                    unresolvedDependencies << dl.artifact.moduleRevisionId
                }
            }
        }
        def dependencies = unresolvedDependencies.collect { ModuleRevisionId mid ->
            "- ${mid.organisation}:${mid.name}:${mid.revision}"
        }.join(System.getProperty("line.separator"))
        return """Failed to resolve dependencies (Set log level to 'warn' in BuildConfig.groovy for more information):

$dependencies

"""
    }
}
