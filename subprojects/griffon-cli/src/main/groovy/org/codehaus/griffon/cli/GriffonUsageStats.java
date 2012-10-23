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
import griffon.util.CollectionUtils;
import griffon.util.GriffonUtil;
import griffon.util.MD5;
import groovy.lang.Binding;
import groovyx.net.http.ContentType;
import groovyx.net.http.HttpURLClient;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

import static griffon.util.CollectionUtils.map;
import static griffon.util.GriffonNameUtils.isBlank;

/**
 * @author Andres Almiray
 * @since 1.2.0
 */
public class GriffonUsageStats {
    public static final String KEY_GRIFFON_COMMAND_LAUNCHER = "griffon.command.launcher";

    public static String banner() {
        StringBuilder b = new StringBuilder();
        b.append("*********************************************************\n");
        b.append("*                                                       *\n");
        b.append("* Please consider helping the Griffon team by opting in *\n");
        b.append("* gathering usage stats. All information is anonymized. *\n");
        b.append("* You can opt-in at any time by invoking                *\n");
        b.append("*                                                       *\n");
        b.append("*     griffon usage-stats --enabled=true                *\n");
        b.append("*                                                       *\n");
        b.append("* This command can be used to opt-out as well.          *\n");
        b.append("* Thank you for your support!                           *\n");
        b.append("*                                                       *\n");
        b.append("*********************************************************\n");
        return b.toString();
    }

    public static void ping(BuildSettings settings, Binding binding) {
        if (!isEnabled(settings)) return;

        if (isBlank(System.getProperty(KEY_GRIFFON_COMMAND_LAUNCHER))) {
            System.setProperty(KEY_GRIFFON_COMMAND_LAUNCHER, "griffon");
        }
        String scriptName = (String) binding.getVariable(GriffonScriptRunner.VAR_SCRIPT_NAME);
        final Map stats = map()
            .e("gv", GriffonEnvironment.getGriffonVersion())
            .e("jv", System.getProperty("java.version"))
            .e("ve", System.getProperty("java.vendor"))
            .e("vm", System.getProperty("java.vm.version"))
            .e("on", System.getProperty("os.name"))
            .e("ov", System.getProperty("os.version"))
            .e("oa", System.getProperty("os.arch"))
            .e("cn", System.getProperty(KEY_GRIFFON_COMMAND_LAUNCHER))
            .e("sn", GriffonUtil.getHyphenatedName(scriptName))
            .e("un", MD5.encode(System.getProperty("user.name")));

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    send(stats);
                } catch (Exception e) {
                    // ignore. Any errors thrown can be safely discarded
                    // as we don't provide any recovery if sending stats
                    // fails
                }
            }
        });
        t.setDaemon(true);
        t.start();
    }

    private static void send(Map stats) throws Exception {
        HttpURLClient http = new HttpURLClient();
        http.setUrl(new URL("http://artifacts.griffon-framework.org/usage"));
        http.setContentType(ContentType.JSON);
        http.setRequestContentType(ContentType.URLENC);
        http.setHeaders(map().e("X-Griffon-Usage-Stats", "V1"));
        http.request(CollectionUtils.<String, Object>map()
            .e("method", "POST")
            .e("body", stats));
    }

    public static boolean isEnabled(BuildSettings settings) {
        if (settings.isOfflineMode()) return false;

        File usageStatsFile = getUsageStatesFile(settings);
        if (usageStatsFile.exists()) {
            String text = "false";
            try {
                text = DefaultGroovyMethods.getText(usageStatsFile);
            } catch (IOException e) {
                // ignore
            }
            return Boolean.parseBoolean(text.trim());
        }
        return false;
    }

    public static void setEnabled(BuildSettings settings, boolean enabled) {
        File usageStatsFile = getUsageStatesFile(settings);
        try {
            DefaultGroovyMethods.setText(usageStatsFile, String.valueOf(enabled));
        } catch (IOException e) {
            // ignore
        }
    }

    private static File getUsageStatesFile(BuildSettings settings) {
        return new File(settings.getGriffonWorkDir().getParentFile(), ".usage-stats");
    }
}
