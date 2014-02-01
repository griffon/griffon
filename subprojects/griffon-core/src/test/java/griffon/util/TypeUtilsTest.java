/*
 * Copyright 2008-2014 the original author or authors.
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
package griffon.util;

import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static griffon.util.CollectionUtils.newList;
import static griffon.util.CollectionUtils.newSet;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TypeUtilsTest {
    @Test
    public void setEquality() {
        Set<?> s1 = newSet(2, "a");
        Set<?> s2 = newSet("a", 2);
        Set<?> s3 = newSet(3, "a");
        Set<?> s4 = newSet(3);

        assertTrue(TypeUtils.equals(s1, s2));
        assertFalse(TypeUtils.equals(s1, s3));
        assertFalse(TypeUtils.equals(s1, s4));
    }

    @Test
    public void mapEquality() {
        Map<String, String> m1 = CollectionUtils.<String, String>newMap("k1", "v1", "k2", "v2");
        Map<String, String> m2 = CollectionUtils.<String, String>newMap("k2", "v2", "k1", "v1");
        Map<String, String> m3 = CollectionUtils.<String, String>newMap("k1", "v1", "k3", "v3");
        Map<String, String> m4 = CollectionUtils.<String, String>newMap("k1", "v1");

        assertTrue(TypeUtils.equals(m1, m2));
        assertFalse(TypeUtils.equals(m1, m3));
        assertFalse(TypeUtils.equals(m1, m4));
    }

    @Test
    public void listEquality() {
        List<?> l1 = newList(2, "a");
        List<?> l2 = newList(2, "a");
        List<?> l3 = newList(3, "a");
        List<?> l4 = newList(3);

        assertTrue(TypeUtils.equals(l1, l2));
        assertFalse(TypeUtils.equals(l1, l3));
        assertFalse(TypeUtils.equals(l1, l4));
    }

    @Test
    public void arrayEquality() {
        Object[] a1 = new Object[]{2, "a"};
        Object[] a2 = new Object[]{2, "a"};
        Object[] a3 = new Object[]{3, "a"};
        Object[] a4 = new Object[]{2};

        assertTrue(TypeUtils.equals(a1, a2));
        assertFalse(TypeUtils.equals(a1, a3));
        assertFalse(TypeUtils.equals(a1, a4));
    }

    @Test
    public void objectEquality() {
        Object o1 = new Object();
        Object o2 = new Object();

        assertTrue(TypeUtils.equals(o1, o1));
        assertFalse(TypeUtils.equals(o1, o2));
        assertTrue(TypeUtils.equals(1, Integer.valueOf(1)));
        assertTrue(TypeUtils.equals("a", "a"));
    }
}
