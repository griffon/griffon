/*
 * Copyright 2008-2010 the original author or authors.
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

import griffon.core.GriffonApplication
import griffon.core.BaseGriffonApplication
import griffon.util.GriffonApplicationHelper
import griffon.util.UIThreadHelper
import griffon.util.SwingUIThreadHandler
import javax.swing.JApplet

/**
 * @author Danno.Ferrin
 * @author Andres.Almiray
 */
class SwingApplet extends JApplet {
    private boolean appletContainerDispensed = false

    @Delegate private final BaseGriffonApplication _base

    SwingApplet() {
        _base = new BaseGriffonApplication(this)
        UIThreadHelper.instance.setUIThreadHandler(new SwingUIThreadHandler())
        loadApplicationProperties()
    }

    public void init() {
        GriffonApplicationHelper.prepare(this)
        event("BootstrapEnd",[this]) // to keep it in sync with app version
        GriffonApplicationHelper.startup(this)
    }

    public void start() {
        ready()
    }

    public void stop() {
        event("StopStart",[this])
        GriffonApplicationHelper.runScriptInsideUIThread("Stop", this)
        event("StopEnd",[this])
    }

    public void destroy() {
        if(canShutdown()) shutdown()
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
