import javax.swing.JOptionPane

class AbeilleFormsDemoController {

    // these will be injected by Griffon
    def model
    def view

    def showInfo = { evt = null ->
       def message = "I'm sorry but user\n\n${model.username}\n\nis not authorized."
       JOptionPane.showMessageDialog(app.appFrames[0], message,
          "Alert", JOptionPane.INFORMATION_MESSAGE)
    }
}
