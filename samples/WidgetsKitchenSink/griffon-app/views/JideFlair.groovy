
import java.awt.BorderLayout
import java.awt.GridBagConstraints
import javax.swing.JTabbedPane
import com.jidesoft.swing.TristateCheckBox

gridBagLayout()

tabbedPane(tabPlacement:JTabbedPane.LEFT, constraints:gbc(weightx:1.0, weighty:1.0, fill:GridBagConstraints.BOTH)) {
    vbox(title:'Tri-State Checkbox') {
        tristateCheckBox(text:"Yes, No, or Maybe", state:TristateCheckBox.SELECTED)
        tristateCheckBox(text:"Yes, No, or Maybe", state:TristateCheckBox.NOT_SELECTED)
        tristateCheckBox(text:"Yes, No, or Maybe", state:TristateCheckBox.DONT_CARE)
    }

    panel(title:'CheckBoxList') {
	scrollPane {
            checkBoxList(listData: ["One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten"] as Object[])
        }
    }
}

