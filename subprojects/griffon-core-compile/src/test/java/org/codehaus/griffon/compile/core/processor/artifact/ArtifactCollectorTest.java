/*
 * Copyright 2008-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codehaus.griffon.compile.core.processor.artifact;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kordamp.jipsy.processor.testutils.NoOutputTestBase;
import org.kordamp.jipsy.processor.testutils.TestInitializer;
import org.kordamp.jipsy.processor.testutils.TestLogger;

import java.util.Collection;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ArtifactCollectorTest extends NoOutputTestBase {
    private TestInitializer initializer;
    private TestLogger logger;
    private ArtifactCollector collector;

    @Before
    public void loadFrameWork() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("griffon.core.artifact.GriffonModel", "provider1\n");
        map.put("griffon.core.artifact.GriffonView", "provider1\nprovider2\n");
        map.put("griffon.core.artifact.GriffonController", "provider1\nprovider3\n");
        initializer = new TestInitializer(map);
        logger = new TestLogger();
        collector = new ArtifactCollector(initializer, logger);
    }

    @Test(expected = NullPointerException.class)
    public void testGetArtifactNull() {
        collector.getArtifact(null);
    }

    @Test
    public void testGetArtifactExisting() {
        assertEquals(0, collector.artifacts().size());
        collector.getArtifact("artifact");
        assertEquals(1, collector.artifacts().size());
        logger.reset();
        Artifact artifact = collector.getArtifact("artifact");
        assertTrue(logger.records().isEmpty());
        assertEquals("artifact", artifact.getName());
        assertEquals(1, collector.artifacts().size());
    }

    @Test
    public void testGetArtifactNew() {
        assertEquals(0, collector.artifacts().size());
        logger.reset();
        assertEquals("artifact", collector.getArtifact("artifact").getName());
        assertEquals(1, logger.records().size());
        assertEquals(1, collector.artifacts().size());
    }

    @Test
    public void testGetArtifactNewWithInitializer() {
        assertEquals(0, collector.artifacts().size());
        Artifact artifact = collector.getArtifact("griffon.core.artifact.GriffonModel");
        assertEquals("griffon.core.artifact.GriffonModel", artifact.getName());
        assertTrue(artifact.contains("provider1"));
        assertEquals(1, collector.artifacts().size());
    }

    @Test
    public void testGetArtifactNewWithBiggerInitializer() {
        assertEquals(0, collector.artifacts().size());
        Artifact artifact = collector.getArtifact("griffon.core.artifact.GriffonView");
        assertEquals("griffon.core.artifact.GriffonView", artifact.getName());
        assertTrue(artifact.contains("provider1"));
        assertTrue(artifact.contains("provider2"));
        assertEquals(1, collector.artifacts().size());
    }

    @Test
    public void testGetArtifactNewWithInitializerContainingRemovedElement() {
        assertEquals(0, collector.artifacts().size());
        collector.removeProvider("provider1");
        Artifact artifact = collector.getArtifact("griffon.core.artifact.GriffonModel");
        Assert.assertFalse(artifact.contains("provider1"));
        assertEquals(1, collector.artifacts().size());
    }

    @Test
    public void testArtifactsEmpty() {
        Collection<Artifact> artifacts = collector.artifacts();
        assertEquals(0, artifacts.size());
    }

    @Test
    public void testArtifactsOne() {
        Artifact artifact = collector.getArtifact("artifact");
        Collection<Artifact> artifacts = collector.artifacts();
        assertEquals(1, artifacts.size());
        assertTrue(artifacts.contains(artifact));
    }

    @Test
    public void testArtifactsMore() {
        Artifact model = collector.getArtifact("griffon.core.artifact.GriffonModel");
        Artifact view = collector.getArtifact("griffon.core.artifact.GriffonView");
        Collection<Artifact> artifacts = collector.artifacts();
        assertEquals(2, artifacts.size());
        assertTrue(artifacts.contains(model));
        assertTrue(artifacts.contains(view));
    }

    @Test
    public void testArtifactsDuplicate() {
        Artifact model = collector.getArtifact("griffon.core.artifact.GriffonModel");
        Artifact view = collector.getArtifact("griffon.core.artifact.GriffonModel");
        assertTrue(model == view);
        Collection<Artifact> artifacts = collector.artifacts();
        assertEquals(1, artifacts.size());
        assertTrue(artifacts.contains(model));
    }

    @Test(expected = NullPointerException.class)
    public void testRemoveProviderNull() {
        collector.removeProvider(null);
    }

    @Test
    public void testRemoveProviderWhenEmpty() {
        collector.removeProvider("provider1");
        assertEquals(1, logger.records().size());
        assertEquals("Removing provider1\n", logger.getFileContent());
    }

    @Test
    public void testRemoveProviderWhenInNotOneArtifact() {
        collector.getArtifact("griffon.core.artifact.GriffonModel");
        logger.reset();
        collector.removeProvider("provider2");
        assertEquals(1, logger.records().size());
    }

    @Test
    public void testRemoveProviderWhenInOneArtifact() {
        collector.getArtifact("griffon.core.artifact.GriffonModel");
        logger.reset();
        collector.removeProvider("provider1");
        assertEquals(2, logger.records().size());
    }

    @Test
    public void testRemoveProviderWhenInTwoArtifacts() {
        collector.getArtifact("griffon.core.artifact.GriffonModel");
        collector.getArtifact("griffon.core.artifact.GriffonView");
        logger.reset();
        collector.removeProvider("provider1");
        assertEquals(3, logger.records().size());
    }

    @Test
    public void testRemoveProviderWhenInSomeArtifacts() {
        collector.getArtifact("griffon.core.artifact.GriffonModel");
        collector.getArtifact("griffon.core.artifact.GriffonView");
        collector.getArtifact("griffon.core.artifact.GriffonController");
        logger.reset();
        collector.removeProvider("provider2");
        assertEquals(2, logger.records().size());
    }

    @Test
    public void testToStringEmpty() {
        collector.toString();
    }

    @Test
    public void testToStringNonExistingArtifact() {
        collector.getArtifact("nonExistingArtifact");
        collector.toString();
    }

    @Test
    public void testToStringExistingArtifact() {
        collector.getArtifact("griffon.core.artifact.GriffonModel");
        collector.toString();
    }

    @Test
    public void testToStringMoreExistingArtifacts() {
        collector.getArtifact("griffon.core.artifact.GriffonModel");
        collector.getArtifact("griffon.core.artifact.GriffonView");
        collector.toString();
    }
}