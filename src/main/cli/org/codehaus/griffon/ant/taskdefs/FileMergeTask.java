package org.codehaus.griffon.ant.taskdefs;

import griffon.util.BuildSettings;
import griffon.util.BuildSettingsHolder;
import groovy.util.ConfigObject;
import groovy.util.ConfigSlurper;
import org.apache.commons.io.FileUtils;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Manifest;
import org.apache.tools.ant.taskdefs.ManifestException;
import org.apache.tools.ant.taskdefs.MatchingTask;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.ZipFileSet;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.types.resources.ZipResource;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

public class FileMergeTask extends MatchingTask {
    public static interface MergeStrategy {
        void merge(File file1, File file2) throws IOException;
    }

    public static class Skip implements MergeStrategy {
        public static final MergeStrategy INSTANCE = new Skip();

        public void merge(File file1, File file2) throws IOException {
            // empty
        }
    }

    public static class Replace implements MergeStrategy {
        public static final MergeStrategy INSTANCE = new Replace();

        public void merge(File file1, File file2) throws IOException {
            FileUtils.copyFile(file2, file1);
        }
    }

    public static abstract class AbstractMergeStrategy implements MergeStrategy {
        protected void closeQuietly(InputStream in) {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }

        protected void closeQuietly(OutputStream out) {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }

        protected void closeQuietly(Reader reader) {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }

        protected void closeQuietly(Writer writer) {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }

    public static class Append extends AbstractMergeStrategy {
        public static final MergeStrategy INSTANCE = new Append();

        public void merge(File file1, File file2) throws IOException {
            String text1 = DefaultGroovyMethods.getText(file1);
            String text2 = DefaultGroovyMethods.getText(file2);
            DefaultGroovyMethods.setText(file1, text1 + text2);
        }
    }

    public static class Merge extends AbstractMergeStrategy {
        public static final MergeStrategy INSTANCE = new Merge();

        public void merge(File file1, File file2) throws IOException {
            PrintWriter w = null;
            try {
                List<String> lines1 = FileUtils.readLines(file1);
                List<String> lines2 = FileUtils.readLines(file2);
                for (String line : lines2) {
                    if (!lines1.contains(line)) {
                        lines1.add(line);
                    }
                }

                w = new PrintWriter(new FileWriter(file1));
                for (String line : lines1) {
                    w.println(line);
                }
            } finally {
                closeQuietly(w);
            }
        }
    }

    public static class MergeManifest extends AbstractMergeStrategy {
        public static final MergeStrategy INSTANCE = new MergeManifest();

        public void merge(File file1, File file2) throws IOException {
            Reader r1 = null;
            Reader r2 = null;
            PrintWriter w = null;
            try {
                r1 = new FileReader(file1);
                r2 = new FileReader(file2);
                Manifest manifest1 = new Manifest(r1);
                Manifest manifest2 = new Manifest(r2);
                manifest1.merge(manifest2);
                w = new PrintWriter(new FileWriter(file1));
                manifest1.write(w);
            } catch (ManifestException e) {
                throw new IOException("Cannot merge manifest " + file1 + " with " + file2, e);
            } finally {
                closeQuietly(r1);
                closeQuietly(r2);
                closeQuietly(w);
            }
        }
    }

    public static class MergeProperties extends AbstractMergeStrategy {
        public static final MergeStrategy INSTANCE = new MergeProperties();

        public void merge(File file1, File file2) throws IOException {
            InputStream in1 = null;
            InputStream in2 = null;
            OutputStream out = null;

            try {
                Properties p1 = new Properties();
                Properties p2 = new Properties();
                in1 = new FileInputStream(file1);
                in2 = new FileInputStream(file2);
                p1.load(in1);
                p2.load(in2);

                ConfigSlurper slurper = new ConfigSlurper();
                ConfigObject cfg = slurper.parse(p1);
                cfg.merge(slurper.parse(p2));
                out = new FileOutputStream(file1);
                cfg.toProperties().store(out, null);
            } finally {
                closeQuietly(in1);
                closeQuietly(in2);
                closeQuietly(out);
            }
        }
    }

    public static class MergeGriffonArtifacts extends AbstractMergeStrategy {
        public static final MergeStrategy INSTANCE = new MergeGriffonArtifacts();

        public void merge(File file1, File file2) throws IOException {
            InputStream in1 = null;
            InputStream in2 = null;
            OutputStream out = null;

            try {
                Properties p1 = new Properties();
                Properties p2 = new Properties();
                in1 = new FileInputStream(file1);
                in2 = new FileInputStream(file2);
                p1.load(in1);
                p2.load(in2);

                for (String artifactType : p2.stringPropertyNames()) {
                    String value2 = p2.getProperty(artifactType).trim();
                    if (p1.containsKey(artifactType)) {
                        String value1 = p1.getProperty(artifactType).trim();
                        value1 = value1.substring(0, value1.length() - 1) + ',' + value2.substring(1);
                        p1.setProperty(artifactType, value1);
                    } else {
                        p1.setProperty(artifactType, value2);
                    }
                }
                out = new FileOutputStream(file1);
                p1.store(out, null);
            } finally {
                closeQuietly(in1);
                closeQuietly(in2);
                closeQuietly(out);
            }
        }
    }

    private File dir;
    private String applicationName;
    private final List<ZipFileSet> zipFileSets = new ArrayList<ZipFileSet>();
    private final List<FileSet> fileSets = new ArrayList<FileSet>();
    private static final Map<Pattern, MergeStrategy> DEFAULT_MAPPINGS = new LinkedHashMap<Pattern, MergeStrategy>();
    private static final Map<Pattern, MergeStrategy> MERGER_MAPPINGS = new LinkedHashMap<Pattern, MergeStrategy>();

    static {
        DEFAULT_MAPPINGS.put(Pattern.compile("META-INF/griffon-artifacts.properties"), MergeGriffonArtifacts.INSTANCE);
        DEFAULT_MAPPINGS.put(Pattern.compile("META-INF/griffon-lookandfeel.properties"), Merge.INSTANCE);
        DEFAULT_MAPPINGS.put(Pattern.compile("META-INF/MANIFEST.MF"), MergeManifest.INSTANCE);
        DEFAULT_MAPPINGS.put(Pattern.compile("META-INF/services/.*"), Merge.INSTANCE);
        DEFAULT_MAPPINGS.put(Pattern.compile(".*.properties"), MergeProperties.INSTANCE);
    }

    @Override
    public void execute() throws BuildException {
        ConfigObject buildSettings = getBuildSettings();
        ConfigObject griffon = (ConfigObject) buildSettings.get("griffon");
        ConfigObject jars = (ConfigObject) griffon.get("jars");
        if (jars.containsKey("merge")) {
            Map<String, Class<?>> merge = (Map<String, Class<?>>) jars.get("merge");
            for (Map.Entry<String, Class<?>> entry : merge.entrySet()) {
                try {
                    MERGER_MAPPINGS.put(Pattern.compile(entry.getKey()), (MergeStrategy) entry.getValue().newInstance());
                } catch (InstantiationException e) {
                    log("Cannot create an instance of " + entry.getValue() + " using the default no-args constructor.", Project.MSG_WARN);
                } catch (IllegalAccessException e) {
                    log("Cannot create an instance of " + entry.getValue() + " using the default no-args constructor.", Project.MSG_WARN);
                }
            }
        }

        try {
            if (dir.exists()) FileUtils.deleteDirectory(dir);
        } catch (IOException e) {
            throw new BuildException(e);
        }
        dir.mkdirs();

        try {
            for (ZipFileSet zipFileSet : zipFileSets) {
                process(zipFileSet, dir);
            }
            for (FileSet fileSet : fileSets) {
                process(fileSet, dir);
            }
        } catch (IOException e) {
            throw new BuildException(e);
        }
    }

    private void process(FileSet fileSet, File dir) throws IOException {
        for (Iterator entries = fileSet.iterator(); entries.hasNext(); ) {
            FileResource entry = (FileResource) entries.next();

            File file1 = new File(dir, entry.getName());
            MergeStrategy merger = getMergeStrategyFor(entry.getName());
            if (merger != Skip.INSTANCE) {
                if (!file1.exists()) {
                    file1.getParentFile().mkdirs();
                    FileUtils.copyFile(entry.getFile(), file1);
                } else {
                    log("Duplicate entry " + entry.getName(), Project.MSG_INFO);
                    merger.merge(file1, entry.getFile());
                }
            }
        }
    }

    private void process(ZipFileSet zipFileSet, File dir) throws IOException {
        for (Iterator entries = zipFileSet.iterator(); entries.hasNext(); ) {
            ZipResource entry = (ZipResource) entries.next();
            File file1 = new File(dir, entry.getName());

            MergeStrategy merger = getMergeStrategyFor(entry.getName());
            if (!(merger instanceof Skip)) {
                if (!file1.exists()) {
                    file1.getParentFile().mkdirs();
                    copy(entry.getInputStream(), file1);
                } else {
                    log("Duplicate entry " + entry.getName() + " from " + entry.getZipfile().getName(), Project.MSG_INFO);
                    File buffer = File.createTempFile(applicationName + "_griffon_merge", ".tmp");
                    buffer.deleteOnExit();
                    copy(entry.getInputStream(), buffer);
                    merger.merge(file1, buffer);
                }
            }
        }
    }

    private MergeStrategy getMergeStrategyFor(String filename) {
        for (Map.Entry<Pattern, MergeStrategy> entry : MERGER_MAPPINGS.entrySet()) {
            if (entry.getKey().matcher(filename).matches()) {
                return entry.getValue();
            }
        }
        for (Map.Entry<Pattern, MergeStrategy> entry : DEFAULT_MAPPINGS.entrySet()) {
            if (entry.getKey().matcher(filename).matches()) {
                return entry.getValue();
            }
        }
        return Skip.INSTANCE;
    }

    private void copy(InputStream in, File dest) throws IOException {
        DefaultGroovyMethods.setText(dest, DefaultGroovyMethods.getText(in));
    }

    public File getDir() {
        return dir;
    }

    public void setDir(File dir) {
        this.dir = dir;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public void addZipFileset(ZipFileSet set) {
        zipFileSets.add(set);
    }

    public void addFileSet(FileSet set) {
        // ignore
    }

    private ConfigObject getBuildSettings() {
        BuildSettings settings = BuildSettingsHolder.getSettings();
        return settings != null ? settings.getConfig() : new ConfigObject();
    }
}
