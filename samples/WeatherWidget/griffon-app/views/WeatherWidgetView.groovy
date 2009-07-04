import java.awt.Color
import java.awt.Font
import javax.swing.Timer

degreeFormatter = {"$it\u00b0${model.celsius?'C':'F'}"}

def weatherIcons = [:]

hw = hudWindow(locationByPlatform:true) {
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
          label("Data from Weather Undergroud", font:new Font("Arial", Font.BOLD, 8), foreground:Color.WHITE)
          hglue()
          hudButton("i", font:new Font("Arial", Font.BOLD, 8), actionPerformed: controller.showPreferences)
        }
    }

}

hw.size = hw.preferredSize
hw.visible = true
hw.JDialog.windowClosing = {app.shutdown()}

Timer t = new Timer(300000, controller.updateWeather as java.awt.event.ActionListener)
t.initialDelay = 0
t.start()