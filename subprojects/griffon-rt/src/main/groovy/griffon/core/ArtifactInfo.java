/*
 * Copyright 2009-2013 the original author or authors.
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

package griffon.core;

/**
 * Describes an artifact that can be handled by {@code ArtifactManager}.<p>
 * This class is used for communication between {@code ArtifactManager} and
 * {@code ArtifactHandler}s, and as such is considered to be of internal use.
 *
 * @author Andres Almiray
 */
public class ArtifactInfo {
    private final Class clazz;
    private final String type;

    public ArtifactInfo(Class clazz, String type) {
        this.clazz = clazz;
        this.type = type;
    }

    public Class getClazz() {
        return clazz;
    }

    public String getType() {
        return type;
    }

    public String toString() {
        return type + "[" + clazz.getName() + "]";
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (!(obj instanceof ArtifactInfo)) return false;

        ArtifactInfo other = (ArtifactInfo) obj;
        return clazz.equals(other.clazz) &&
                type.equals(other.type);
    }

    public int hashCode() {
        return (clazz.hashCode() * 37) +
                (type.hashCode() * 31);
    }
}