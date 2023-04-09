/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2023 the original author or authors.
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

public class FieldsModuleTestcase {
    public static class Targets {
        @BindTo(Atom.class)
        public Hydrogen hydrogen;

        @BindTo(Atom.class)
        @Named("Helium")
        public Helium litium;
    }

    public static class SingletonTargets {
        @Singleton
        @BindTo(Atom.class)
        public Hydrogen hydrogen;

        @Singleton
        @BindTo(Atom.class)
        @Named("Helium")
        public Helium helium;
    }

    public static class ProviderTypes {
        @BindTo(Atom.class)
        public LitiumProvider litium;

        @BindTo(Atom.class)
        @Named("Berilium")
        public BeriliumProvider berilium;
    }

    public static class SingletonProviderTypes {
        @Singleton
        @BindTo(Atom.class)
        public LitiumProvider litium;

        @Singleton
        @BindTo(Atom.class)
        @Named("Berilium")
        public BeriliumProvider berilium;
    }

    public static class Instances {
        @BindTo(Atom.class)
        public Atom boron = new Boron();

        @BindTo(Atom.class)
        @Named("Carbon")
        public Atom carbon = new Carbon();
    }

    public static class Providers {
        @BindTo(Atom.class)
        public Provider<Nitrogen> nitrogen = new NitrogenProvider();

        @BindTo(Atom.class)
        @Named("Oxygen")
        public Provider<Oxygen> oxygen = new OxygenProvider();
    }

    public static class SingletonProviders {
        @Singleton
        @BindTo(Atom.class)
        public Provider<Nitrogen> nitrogen = new NitrogenProvider();

        @Singleton
        @BindTo(Atom.class)
        @Named("Oxygen")
        public Provider<Oxygen> oxygen = new OxygenProvider();
    }

    public static class Hydrogen implements Atom {
        @Override
        public int getNumElectrons() {
            return 1;
        }
    }

    public static class Helium implements Atom {
        @Override
        public int getNumElectrons() {
            return 2;
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

    public static class Boron implements Atom {
        @Override
        public int getNumElectrons() {
            return 5;
        }
    }

    public static class Carbon implements Atom {
        @Override
        public int getNumElectrons() {
            return 6;
        }
    }

    public static class Nitrogen implements Atom {
        @Override
        public int getNumElectrons() {
            return 7;
        }
    }

    public static class Oxygen implements Atom {
        @Override
        public int getNumElectrons() {
            return 8;
        }
    }

    public static class LitiumProvider implements Provider<Litium> {
        @Override
        public Litium get() {
            return new Litium();
        }
    }

    public static class BeriliumProvider implements Provider<Berilium> {
        @Override
        public Berilium get() {
            return new Berilium();
        }
    }

    public static class NitrogenProvider implements Provider<Nitrogen> {
        @Override
        public Nitrogen get() {
            return new Nitrogen();
        }
    }

    public static class OxygenProvider implements Provider<Oxygen> {
        @Override
        public Oxygen get() {
            return new Oxygen();
        }
    }
}
