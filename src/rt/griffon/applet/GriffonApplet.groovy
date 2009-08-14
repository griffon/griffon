/*
 * Copyright 2008-2009 the original author or authors.
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
package griffon.applet

import griffon.util.GriffonApplicationHelper
import griffon.util.IGriffonApplication
import griffon.util.BaseGriffonApplication
import javax.swing.JApplet

/**
 * @author Danno.Ferrin
 * @author Andres.Almiray
 */
class GriffonApplet extends JApplet /*implements IGriffonApplication*/ {
    private boolean appletContainerDispensed = false

    @Delegate private final BaseGriffonApplication _base

    GriffonApplet() {
       _base = new BaseGriffonApplication(this)
    }

    public void init() {
        GriffonApplicationHelper.prepare(this)
        event("BootstrapEnd",[this]) // to keep it in sync with app version
        GriffonApplicationHelper.startup(this)
    }

    public void start() {
        //GriffonApplicationHelper.callReady()
        // skip the EDT sillyness, just call ready
        ready()
    }

    public void stop() {
        GriffonApplicationHelper.runScriptInsideEDT("Stop", this)
    }

    public void destroy() {
        shutdown()
    }

    public Object createApplicationContainer() {
        if (appletContainerDispensed) {
            return GriffonApplicationHelper.createJFrameApplication(this)
        } else {
            appletContainerDispensed = true
            return this;
        }
    }
}