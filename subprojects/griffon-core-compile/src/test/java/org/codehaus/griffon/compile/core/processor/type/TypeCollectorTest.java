/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2018 the original author or authors.
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
package org.codehaus.griffon.compile.core.processor.type;

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

public class TypeCollectorTest extends NoOutputTestBase {
    private TestInitializer initializer;
    private TestLogger logger;
    private TypeCollector collector;

    @Before
    public void loadFrameWork() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("type1", "provider1\n");
        map.put("type2", "provider1\nprovider2\n");
        map.put("type3", "provider1\nprovider3\n");
        initializer = new TestInitializer(map);
        logger = new TestLogger();
        collector = new TypeCollector(initializer, logger);
    }

    @Test(expected = NullPointerException.class)
    public void testGetTypeNull() {
        collector.getType(null);
    }

    @Test
    public void testGetTypeExisting() {
        assertEquals(0, collector.types().size());
        collector.getType("type");
        assertEquals(1, collector.types().size());
        logger.reset();
        Type type = collector.getType("type");
        assertTrue(logger.records().isEmpty());
        assertEquals("type", type.getName());
        assertEquals(1, collector.types().size());
    }

    @Test
    public void testGetTypeNew() {
        assertEquals(0, collector.types().size());
        logger.reset();
        assertEquals("type", collector.getType("type").getName());
        assertEquals(1, logger.records().size());
        assertEquals(1, collector.types().size());
    }

    @Test
    public void testGetTypeNewWithInitializer() {
        assertEquals(0, collector.types().size());
        Type type = collector.getType("type1");
        assertEquals("type1", type.getName());
        assertTrue(type.contains("provider1"));
        assertEquals(1, collector.types().size());
    }

    @Test
    public void testGetTypeNewWithBiggerInitializer() {
        assertEquals(0, collector.types().size());
        Type type = collector.getType("type2");
        assertEquals("type2", type.getName());
        assertTrue(type.contains("provider1"));
        assertTrue(type.contains("provider2"));
        assertEquals(1, collector.types().size());
    }

    @Test
    public void testGetTypeNewWithInitializerContainingRemovedElement() {
        assertEquals(0, collector.types().size());
        collector.removeProvider("provider1");
        Type type = collector.getType("type1");
        Assert.assertFalse(type.contains("provider1"));
        assertEquals(1, collector.types().size());
    }

    @Test
    public void testTypesEmpty() {
        Collection<Type> types = collector.types();
        assertEquals(0, types.size());
    }

    @Test
    public void testTypesOne() {
        Type type = collector.getType("type");
        Collection<Type> types = collector.types();
        assertEquals(1, types.size());
        assertTrue(types.contains(type));
    }

    @Test
    public void testTypesMore() {
        Type type1 = collector.getType("type1");
        Type type2 = collector.getType("type2");
        Collection<Type> types = collector.types();
        assertEquals(2, types.size());
        assertTrue(types.contains(type1));
        assertTrue(types.contains(type2));
    }

    @Test
    public void testTypesDuplicate() {
        Type type1 = collector.getType("type1");
        Type type2 = collector.getType("type1");
        assertTrue(type1 == type2);
        Collection<Type> types = collector.types();
        assertEquals(1, types.size());
        assertTrue(types.contains(type1));
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
    public void testRemoveProviderWhenInNotOneType() {
        collector.getType("type1");
        logger.reset();
        collector.removeProvider("provider2");
        assertEquals(1, logger.records().size());
    }

    @Test
    public void testRemoveProviderWhenInOneType() {
        collector.getType("type1");
        logger.reset();
        collector.removeProvider("provider1");
        assertEquals(2, logger.records().size());
    }

    @Test
    public void testRemoveProviderWhenInTwoTypes() {
        collector.getType("type1");
        collector.getType("type2");
        logger.reset();
        collector.removeProvider("provider1");
        assertEquals(3, logger.records().size());
    }

    @Test
    public void testRemoveProviderWhenInSomeTypes() {
        collector.getType("type1");
        collector.getType("type2");
        collector.getType("type3");
        logger.reset();
        collector.removeProvider("provider2");
        assertEquals(2, logger.records().size());
    }

    @Test
    public void testToStringEmpty() {
        collector.toString();
    }

    @Test
    public void testToStringNonExistingType() {
        collector.getType("nonExistingType");
        collector.toString();
    }

    @Test
    public void testToStringExistingType() {
        collector.getType("type1");
        collector.toString();
    }

    @Test
    public void testToStringMoreExistingTypes() {
        collector.getType("type1");
        collector.getType("type2");
        collector.toString();
    }
}