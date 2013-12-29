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

degreeFormatter = {"$it\u00b0${app.models.WeatherWidget.celsius?'C':'F'}"}
def theFont = new Font("Arial", Font.BOLD, 21)

def weatherIcons = [:]

smallPanel = vbox {
    label(text:bind {model.day[0..2]}, font: theFont, foreground:Color.WHITE)
    label(text: bind {degreeFormatter(model.high)}, foreground:Color.RED, font: theFont)
    label(icon:bind { weatherIcons.get(model.state,
            imageIcon(new URL("http://icons.weatherunderground.com/graphics/conds/2005/${model.state}.gif")))})
    label(text: bind {degreeFormatter(model.low) }, foreground:Color.CYAN, font: theFont)
}
