
import java.awt.GridBagConstraints
import javax.swing.JTabbedPane
import javax.swing.SwingConstants
import java.util.GregorianCalendar
import org.jdesktop.swingx.calendar.DaySelectionModel
import org.jdesktop.swingx.calendar.DateSelectionModel.SelectionMode
import java.awt.Color
import org.jdesktop.swingx.painter.*
import org.jdesktop.swingx.painter.effects.*
import java.awt.*
import java.awt.geom.*


gridBagLayout()

tabbedPane(tabPlacement:JTabbedPane.LEFT, weightx:1.0, weighty:1.0, fill:GridBagConstraints.BOTH) {
    vbox(title:'JXTitledPanel') {
        jxtitledPanel(title:"Panel 1") {
            label("Content goes here")
        }
        jxtitledPanel(title:"Panel 2") {
            label("Content goes here as well")
        }
        jxtitledPanel(title:"Panel 3") {
            label("Content goes here in addition to the previous two")
        }
    }

    vbox(title:'JXBusyLabel') {
        // default painter is hosed right now :(  Not a groovy problem
	    jxbusyLabel(id:'busy', enabled:true, busy:true, size:[100, 100], preferredSize:[100, 100], minimumSize:[100, 100], maximumSize:[100, 100])
        checkBox("I'm Busy", selected:bind(target:busy, targetProperty:'busy'))	
    }

    vbox(title:'JXLabel') {
        hbox {
          jxlabel('Griffon', icon:imageIcon('/griffon.jpeg'),
                  horizontalTextPosition: SwingConstants.CENTER, verticalTextPosition: SwingConstants.BOTTOM)
          jxlabel('Griffon', icon:imageIcon('/griffon.jpeg'),
                  horizontalTextPosition: SwingConstants.CENTER, verticalTextPosition: SwingConstants.BOTTOM,
                  textRotation:Math.PI/2)
        }
        hbox {
          jxlabel('Griffon', icon:imageIcon('/griffon.jpeg'),
                  horizontalTextPosition: SwingConstants.CENTER, verticalTextPosition: SwingConstants.BOTTOM,
                  textRotation:Math.PI)
          jxlabel('Griffon', icon:imageIcon('/griffon.jpeg'),
                  horizontalTextPosition: SwingConstants.CENTER, verticalTextPosition: SwingConstants.BOTTOM,
                  textRotation:Math.PI/2*3)
        }
    }

    vbox(title:'JXDatePicker'){
	hbox {
	    jxlabel('Default DatePicker: ')
	    datePicker()
        }
        hbox {
	    jxlabel('Preselected Date: ')
	    datePicker(date:new GregorianCalendar(2008,10,10).getTime())
        }
    }
    vbox(title:'JXGradientChooser') {
        gradientChooser()
    }

    vbox(title:'JXMonthView') {
        hbox {
	    jxlabel('Default MonthView: ')
	    monthView()
        }
        hbox {
	    jxlabel('MonthView Interval: ')
	    def model = new DaySelectionModel(selectionMode:SelectionMode.SINGLE_INTERVAL_SELECTION)
	    model.addSelectionInterval(new GregorianCalendar(2008,10,10).getTime(), new GregorianCalendar(2008,10,16).getTime())
	    monthView(firstDisplayedDay:new GregorianCalendar(2008,10,10).getTime(), model:model)
        }
    }
    vbox(title:'JXGraph') {
        hbox {
	    jxlabel('Plain Graph: ')
	    graph()
        }
        hbox {
	    jxlabel('Sine curve:  ')
	    graph(plots:[[Color.GREEN,{value -> Math.sin(value)}]])
        }
    }
    vbox(title:'Painters') {
        hbox {
	       jxlabel('Pinstripe:    ')
	       def painter = pinstripePainter(angle:45, paint:Color.BLUE, spacing:10.4d, stripeWidth:3.6d)
	       jxpanel(backgroundPainter:painter)
	   }
	   hbox {
	       jxlabel('Matte:        ')
	       def painter = mattePainter(fillPaint:Color.RED)
	       jxpanel(backgroundPainter:painter)
	   }
	   hbox {
	       jxlabel('Checkerboard: ')
	       def painter = checkerboardPainter(squareSize:40.0d, lightPaint:Color.WHITE, darkPaint:Color.BLACK)
	       jxpanel(backgroundPainter:painter)
	   }
	   hbox {
	       jxlabel('Capsule:      ')
	       def painter = capsulePainter(portion:CapsulePainter.Portion.Bottom)
	       jxpanel(backgroundPainter:painter)
	   }
	   hbox {
	       jxlabel('Shape:        ')
	       def painter = shapePainter(shape:new Rectangle2D.Double(0, 0, 50, 50),horizontalAlignment:AbstractLayoutPainter.HorizontalAlignment.RIGHT)
	       jxpanel(backgroundPainter:painter)
	   }
	   hbox {
	       jxlabel('Text:         ')
	       def painter = textPainter(font:new Font("Tahoma", Font.BOLD, 32), text:"Test Text")
	       jxpanel(backgroundPainter:painter)
	   }
	   hbox {
	       jxlabel('Compound:     ')
	       def ap = compoundPainter() {
			   mattePainter(fillPaint:Color.GRAY)
			   glossPainter(position: GlossPainter.GlossPosition.TOP)
			}
	       jxpanel(backgroundPainter:ap)
	   }
	   hbox {
	       jxlabel('Alpha:        ')
	       def ap = alphaPainter(alpha:0.25f) {
			   mattePainter(fillPaint:Color.GRAY)
			   glossPainter(position: GlossPainter.GlossPosition.TOP)
			}
	       jxpanel(backgroundPainter:ap)
	   }
    }
    vbox(title:'MultiSplitPane') {
    	multiSplitPane() {
		split() {
			leaf(name:"left")
			divider()
			split(rowLayout:false) {
				leaf(name:"top")
				divider()
				leaf(name:"bottom")
			}
		}
		button(text:"Left Button", constraints:"left")
		button(text:"Right Button", constraints:"right")
		button(text:"Top Button", constraints:"top")
		button(text:"Bottom Button", constraints:"bottom")
	}

    }
    
}

