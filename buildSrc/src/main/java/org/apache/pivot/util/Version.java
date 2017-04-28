/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.pivot.util;

import java.io.Serializable;

/**
 * Represents a version number. Version numbers are defined as: <p>
 * <i>major</i>.<i>minor</i>.<i>maintenance</i>_<i>update</i> <p> for example,
 * "JDK 1.6.0_10".
 */
public class Version implements Comparable<Version>, Serializable {
    private static final long serialVersionUID = -3677773163272115116L;

    private short majorRevision = 0;
    private short minorRevision = 0;
    private short maintenanceRevision = 0;
    private short updateRevision = 0;
    private String build = null;

    public Version(int majorRevision, int minorRevision, int maintenanceRevision, int updateRevision) {
        this(majorRevision, minorRevision, maintenanceRevision, updateRevision, null);
    }

    public Version(int majorRevision, int minorRevision, int maintenanceRevision,
                   int updateRevision, String build) {
        if (majorRevision > 0x7fff) {
            throw new IllegalArgumentException("majorRevision must be less than or equal "
                + 0x7fff + ".");
        }

        if (minorRevision > 0x7fff) {
            throw new IllegalArgumentException("minorRevision must be less than or equal "
                + 0x7fff + ".");
        }

        if (maintenanceRevision > 0x7fff) {
            throw new IllegalArgumentException("maintenanceRevision must be less than or equal "
                + 0x7fff + ".");
        }

        if (updateRevision > 0x7fff) {
            throw new IllegalArgumentException("updateRevision must be less than or equal "
                + 0x7fff + ".");
        }

        this.majorRevision = (short)majorRevision;
        this.minorRevision = (short)minorRevision;
        this.maintenanceRevision = (short)maintenanceRevision;
        this.updateRevision = (short)updateRevision;
        this.build = build;
    }

    public short getMajorRevision() {
        return majorRevision;
    }

    public short getMinorRevision() {
        return minorRevision;
    }

    public short getMaintenanceRevision() {
        return maintenanceRevision;
    }

    public short getUpdateRevision() {
        return updateRevision;
    }

    public long getNumber() {
        long number = (long)((majorRevision) & 0xffff) << (16 * 3)
            | (long)((minorRevision) & 0xffff) << (16 * 2)
            | (long)((maintenanceRevision) & 0xffff) << (16 * 1)
            | (long)((updateRevision) & 0xffff) << (16 * 0);

        return number;
    }

    @Override
    public int compareTo(Version version) {
        return new Long(getNumber()).compareTo(version.getNumber());
    }

    @Override
    public boolean equals(Object object) {
        return (object instanceof Version && compareTo((Version) object) == 0);
    }

    @Override
    public int hashCode() {
        return new Long(getNumber()).hashCode();
    }

    @Override
    public String toString() {
        String string = this.majorRevision
            + "." + this.minorRevision
            + "." + this.maintenanceRevision
            + "_" + String.format("%02d", this.updateRevision);

        if (this.build != null) {
            string += "-" + this.build;
        }

        return string;
    }

    public static Version decode(String string) {
        Version version = null;

        short majorRevision = 0;
        short minorRevision = 0;
        short maintenanceRevision = 0;
        short updateRevision = 0;
        String build = null;

        String revision;
        // Some "version" strings separate fields with a space
        int i = string.indexOf(" ");
        if (i == -1) {
            i = string.indexOf("-");
        }
        if (i == -1) {
            revision = string;
        } else {
            revision = string.substring(0, i);
            build = string.substring(i + 1);
        }

        String[] revisionNumbers = revision.split("\\.");

        if (revisionNumbers.length > 0) {
            majorRevision = Short.parseShort(revisionNumbers[0]);

            if (revisionNumbers.length > 1) {
                minorRevision = Short.parseShort(revisionNumbers[1]);

                if (revisionNumbers.length > 2) {
                    String[] maintenanceRevisionNumbers = revisionNumbers[2].split("_");

                    if (maintenanceRevisionNumbers.length > 0) {
                        maintenanceRevision = Short.parseShort(maintenanceRevisionNumbers[0]);

                        if (maintenanceRevisionNumbers.length > 1) {
                            updateRevision = Short.parseShort(maintenanceRevisionNumbers[1]);
                        }
                    }
                }
            }

            version = new Version(majorRevision, minorRevision, maintenanceRevision,
                updateRevision, build);
        }

        return version;
    }
}
