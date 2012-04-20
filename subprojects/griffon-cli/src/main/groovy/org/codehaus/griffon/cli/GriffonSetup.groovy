/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
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

package org.codehaus.griffon.cli

import griffon.util.BuildSettings
import griffon.util.BuildSettingsHolder
import griffon.util.Metadata
import org.codehaus.griffon.artifacts.ArtifactInstallEngine
import org.codehaus.griffon.artifacts.LocalArtifactRepository
import org.codehaus.griffon.artifacts.model.Archetype
import org.codehaus.griffon.artifacts.model.Plugin
import org.codehaus.griffon.artifacts.model.Release

import static griffon.util.ArtifactSettings.TIMESTAMP_FORMAT
import static griffon.util.ArtifactSettings.createReleaseFromMetadata
import static java.lang.System.out
import static java.util.Collections.emptyMap
import static org.codehaus.griffon.artifacts.ArtifactRepository.DEFAULT_LOCAL_LOCATION
import static org.codehaus.griffon.artifacts.ArtifactRepository.DEFAULT_LOCAL_NAME
import static org.codehaus.griffon.cli.CommandLineConstants.KEY_NON_INTERACTIVE_DEFAULT_ANSWER

/**
 * @author Andres Almiray
 * @since 0.9.5
 */
public final class GriffonSetup {
    public static void run() {
        BuildSettings settings = BuildSettingsHolder.getSettings()
        if (settings != null) {
            settings.debug("Checking configuration for initial setup")
        }

        if (settings != null && isFirstRun(settings)) {
            String defaultAnswerNonInteractive = System.getProperty(KEY_NON_INTERACTIVE_DEFAULT_ANSWER)
            System.setProperty(KEY_NON_INTERACTIVE_DEFAULT_ANSWER, "y")
            try {
                printSetupHeader(settings)
                uploadBundles(settings)
                configured(settings)
                printSetupFooter()
            } finally {
                if (defaultAnswerNonInteractive != null) {
                    System.setProperty(KEY_NON_INTERACTIVE_DEFAULT_ANSWER, defaultAnswerNonInteractive)
                }
            }
        }
    }

    private static void printSetupHeader(BuildSettings settings) {
        out.println("It looks like you're running Griffon " + settings.getGriffonVersion() + " for the first time")
        out.println("Please wait a few moments while Griffon configures itself.")
        out.println(" ")
    }

    private static void printSetupFooter() {
        out.println(" ")
        out.println("Done.")
        out.println(" ")
    }

    private static boolean isFirstRun(BuildSettings settings) {
        File firstRunWitness = new File(settings.getGriffonWorkDir(), ".configured")
        return !firstRunWitness.exists()
    }

    private static void uploadBundles(BuildSettings settings) {
        LocalArtifactRepository griffonLocal = new LocalArtifactRepository()
        griffonLocal.setName(DEFAULT_LOCAL_NAME)
        griffonLocal.setPath(DEFAULT_LOCAL_LOCATION)

        AntBuilder ant = new AntBuilder()
        File bundleHome = unpackBundles(ant)

        uploadBundles(Plugin.TYPE, bundleHome, griffonLocal)
        Map<String, String> archetypes = uploadBundles(Archetype.TYPE, bundleHome, griffonLocal)
        if (archetypes) println ' '
        installArchetypes(settings, griffonLocal, archetypes)

        ant.delete(dir: bundleHome, failonerror: false, quiet: true)
    }

    private static File unpackBundles(AntBuilder ant) {
        File bundleHome = new File(System.getProperty('java.io.tmpdir'), 'griffon-bundles')
        String jar = 'griffon-default-bundles.jar'

        ant.copy(todir: bundleHome) {
            javaresource(name: jar)
        }

        // Now unjar it, excluding the META-INF directory.
        ant.unjar(dest: bundleHome, src: "${bundleHome}/${jar}", overwrite: true) {
            patternset {
                exclude(name: "META-INF/**")
            }
        }

        println ' '

        bundleHome
    }

    private static Map<String, String> uploadBundles(String type, File bundleHome, LocalArtifactRepository griffonLocal) {
        File artifactDir = new File(bundleHome, type + "s")
        if (!artifactDir.exists()) emptyMap()

        File[] artifacts = artifactDir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.getName().startsWith("griffon-") &&
                        file.getName().endsWith("-release.zip")
            }
        })

        Map<String, String> releases = new LinkedHashMap<String, String>()

        for (File file: artifacts) {
            out.println("Uploading " + type + " " + file.getName() + " to griffon-local")
            try {
                Release release = createReleaseFromMetadata(type, file)
                release.setFile(file)
                griffonLocal.uploadRelease(release, null, null)
                releases.put(release.getArtifact().getName(), release.getVersion())
            } catch (Exception e) {
                // oops
                out.println("Failed to upload " + type + " " + file.getName() + " => " + e)
            }
        }

        return releases
    }

    private static void installArchetypes(BuildSettings settings, LocalArtifactRepository griffonLocal, Map<String, String> archetypes) {
        ArtifactInstallEngine artifactInstallEngine = new ArtifactInstallEngine(settings, Metadata.getCurrent(), new AntBuilder())
        for (Map.Entry<String, String> release: archetypes.entrySet()) {
            out.println("Installing archetype " + release.getKey() + "-" + release.getValue())
            File file = griffonLocal.downloadFile(Archetype.TYPE, release.getKey(), release.getValue(), null)
            artifactInstallEngine.installFromFile(Archetype.TYPE, file, false, false)
        }
    }

    private static void configured(BuildSettings settings) {
        File firstRunWitness = new File(settings.getGriffonWorkDir(), ".configured")
        firstRunWitness.getParentFile().mkdirs()
        firstRunWitness.text = new Date().format(TIMESTAMP_FORMAT)
    }
}
