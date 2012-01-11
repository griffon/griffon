/*
 * Copyright 2012 the original author or authors.
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

package org.codehaus.griffon.cli;

import griffon.util.BuildSettings;
import org.codehaus.griffon.artifacts.LocalArtifactRepository;
import org.codehaus.griffon.artifacts.model.Archetype;
import org.codehaus.griffon.artifacts.model.Plugin;
import org.codehaus.griffon.artifacts.model.Release;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.lang.System.out;
import static org.codehaus.griffon.artifacts.ArtifactRepository.DEFAULT_LOCAL_LOCATION;
import static org.codehaus.griffon.artifacts.ArtifactRepository.DEFAULT_LOCAL_NAME;
import static org.codehaus.griffon.artifacts.ArtifactUtils.TIMESTAMP_FORMAT;
import static org.codehaus.griffon.artifacts.ArtifactUtils.createReleaseFromMetadata;
import static org.codehaus.groovy.runtime.DefaultGroovyMethods.setText;

/**
 * @author Andres Almiray
 * @since 0.9.5
 */
public final class GriffonSetup {
    public static void run(BuildSettings settings) {
        if (isFirstRun(settings)) {
            out.println("It looks like you're running Griffon " + settings.getGriffonVersion() + " for the first time");
            out.println("Please wait a few moments while Griffon configures itself.");
            out.println(" ");
            uploadBundles(settings);
            configured(settings);
            out.println(" ");
            out.println("Done.");
            out.println(" ");
        }
    }

    private static boolean isFirstRun(BuildSettings settings) {
        File firstRunWitness = new File(settings.getGriffonWorkDir(), ".configured");
        return !firstRunWitness.exists();
    }

    private static void uploadBundles(BuildSettings settings) {
        LocalArtifactRepository griffonLocal = new LocalArtifactRepository();
        griffonLocal.setName(DEFAULT_LOCAL_NAME);
        griffonLocal.setPath(DEFAULT_LOCAL_LOCATION);

        File bundleHome = new File(settings.getGriffonHome(), "bundles");
        if (!bundleHome.exists()) return;

        uploadBundles(Plugin.TYPE, bundleHome, griffonLocal);
        uploadBundles(Archetype.TYPE, bundleHome, griffonLocal);
    }

    private static void uploadBundles(String type, File bundleHome, LocalArtifactRepository griffonLocal) {
        File artifactDir = new File(bundleHome, type + "s");
        if (!artifactDir.exists()) return;

        File[] artifacts = artifactDir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.getName().startsWith("griffon-") &&
                        file.getName().endsWith(".zip");
            }
        });

        for (File file : artifacts) {
            out.println("Uploading " + type + " " + file.getName() + " to griffon-local");
            try {
                Release release = createReleaseFromMetadata(type, file);
                release.setFile(file);
                griffonLocal.uploadRelease(release, null, null);
            } catch (Exception e) {
                // oops
                out.println("Failed to upload " + type + " " + file.getName() + " => " + e);
            }
        }
    }

    private static void configured(BuildSettings settings) {
        File firstRunWitness = new File(settings.getGriffonWorkDir(), ".configured");
        firstRunWitness.getParentFile().mkdirs();
        try {
            setText(firstRunWitness, new SimpleDateFormat(TIMESTAMP_FORMAT).format(new Date()));
        } catch (IOException e) {
            // ignore ??
        }
    }
}
