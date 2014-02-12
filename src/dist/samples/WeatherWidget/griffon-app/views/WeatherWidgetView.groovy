/*
 * Copyright 2009-2014 the original author or authors.
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

/**
 * @author Danno Ferrin
 */

import java.awt.Color
import java.awt.Font
import javax.swing.Timer

degreeFormatter = {"$it\u00b0${model.celsius?'C':'F'}"}

def weatherIcons = [:]

hw = hudWindow(locationByPlatform:true, alwaysOnTop:true) {
    vbox {
        hbox {
            hglue()
            label(text:bind{model.locationName}, font:new Font("Arial", Font.BOLD, 14), foreground:Color.WHITE)
            hglue()
        }
        vstrut(6)
        hbox {
            vbox {
                label(text: bind {degreeFormatter(model.high)},
                    font:new Font("Arial", Font.BOLD, 21), foreground:Color.RED)
                hstrut 2
                label(text: bind(converter:degreeFormatter, source:model, 'low'),
                    font:new Font("Arial", Font.BOLD, 21), foreground:Color.CYAN)
            }
            hstrut(6)
            currentTemp = label(
                icon:bind { weatherIcons.get(model.state,
                        imageIcon(new URL("http://icons.weatherunderground.com/graphics/conds/2005/${model.state}.gif")))},
                text: bind {degreeFormatter(model.current)}, font:new Font("Arial", Font.BOLD, 42),
                foreground:Color.WHITE)
        }
        vstrut(12)

        hbox {
            widget(app.views.small1.smallPanel)
            hstrut(6)
            widget(app.views.small2.smallPanel)
            hstrut(6)
            widget(app.views.small3.smallPanel)
            hstrut(6)
            widget(app.views.small4.smallPanel)
        }

        vstrut(6)
        hbox {
          label("Data from Weather Underground", font:new Font("Arial", Font.BOLD, 10), foreground:Color.WHITE)
          hglue()
          button('\u24d8', font:new Font("Dialog", Font.BOLD, 14), foreground:Color.WHITE, contentAreaFilled:false, borderPainted:false,
              actionPerformed: controller.showPreferences)
        }
    }

}

hw.size = hw.preferredSize
hw.visible = true
hw.JDialog.windowClosing = {app.shutdown()}

Timer t = new Timer(300000, controller.updateWeather as java.awt.event.ActionListener)
t.initialDelay = 0
t.start()
