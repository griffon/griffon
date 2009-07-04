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