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
package griffon.pivot;

import org.apache.pivot.wtk.DesktopApplicationContext;

import javax.swing.SwingUtilities;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class DesktopPivotGriffonApplication extends AbstractPivotGriffonApplication {
    public DesktopPivotGriffonApplication() {
        this(AbstractPivotGriffonApplication.EMPTY_ARGS);
    }

    public DesktopPivotGriffonApplication(String[] args) {
        super(args);
    }

    public static void main(final String[] args) {
        doRun(args);
    }

    public static void run(String[] args) {
        doRun(args);
    }

    private static void doRun(final String[] args) {
        SwingUtilities.invokeLater(() -> DesktopApplicationContext.main(DesktopPivotGriffonApplication.class, args));
    }
}
