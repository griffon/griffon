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
package org.codehaus.griffon.test.core;

import griffon.annotations.inject.BindTo;

import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

public class InnerClassModuleTestcase {
    public static class Targets {
        @BindTo(Atom.class)
        public static class Hydrogen implements Atom {
            @Override
            public int getNumElectrons() {
                return 1;
            }
        }

        @BindTo(Atom.class)
        @Named("Helium")
        public static class Helium implements Atom {
            @Override
            public int getNumElectrons() {
                return 2;
            }
        }

        public static class Unobtanium implements Atom {
            @Override
            public int getNumElectrons() {
                return 9001;
            }
        }
    }

    public static class SingletonTargets {
        @Singleton
        @BindTo(Atom.class)
        public static class Hydrogen implements Atom {
            @Override
            public int getNumElectrons() {
                return 1;
            }
        }

        @Singleton
        @BindTo(Atom.class)
        @Named("Helium")
        public static class Helium implements Atom {
            @Override
            public int getNumElectrons() {
                return 2;
            }
        }
    }

    public static class Providers {
        @BindTo(Atom.class)
        public static class LitiumProvider implements Provider<Litium> {
            @Override
            public Litium get() {
                return new Litium();
            }
        }

        @BindTo(Atom.class)
        @Named("Berilium")
        public static class BeriliumProvider implements Provider<Berilium> {
            @Override
            public Berilium get() {
                return new Berilium();
            }
        }
    }

    public static class SingletonProviders {
        @Singleton
        @BindTo(Atom.class)
        public static class LitiumProvider implements Provider<Litium> {
            @Override
            public Litium get() {
                return new Litium();
            }
        }

        @Singleton
        @BindTo(Atom.class)
        @Named("Berilium")
        public static class BeriliumProvider implements Provider<Berilium> {
            @Override
            public Berilium get() {
                return new Berilium();
            }
        }
    }

    public static class Litium implements Atom {
        @Override
        public int getNumElectrons() {
            return 3;
        }
    }

    public static class Berilium implements Atom {
        @Override
        public int getNumElectrons() {
            return 4;
        }
    }
}
