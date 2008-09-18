/*
 * Copyright 2008 the original author or authors.
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
 */

import java.awt.BorderLayout as BL
import javax.swing.BorderFactory as BF

dialog(title: "Message", id: "messageDialog", modal: true ) {
   vbox(border: BF.createEmptyBorder(6, 6, 6, 6)) {
      label(text: bind { model.message }, alignmentX: 0.5f)
      progressBar( indeterminate: true )
   }
}

dialog(title: "About GrailsSnoop", id: "aboutDialog", modal: true ) {
   panel( border: BF.createEmptyBorder(10,10,10,10) ) {
      borderLayout(hgap: 20, vgap: 20)
      bannerPanel(title: 'GrailsSnoop', constraints: BL.NORTH, id: 'banner',
                  titleIcon: imageIcon("/images/grails48.png"))
      panel( constraints: BL.CENTER ) {
         gridLayout(cols: 1, rows: 4 )
         label("<html>Blame it on Marcel Overdijk for writing <b>Peek in Grails</b><br>&nbsp;&nbsp;&nbsp;(http://www.grails.org/Peek+in+Grails)</html>")
         label("<html>which is based in Marc Palmer's <b>Grails Documentation Widget</b><br>&nbsp;&nbsp;&nbsp; (http://www.grails.org/Grails+Documentation+Widget)</html>")
         label(" ")
         label("Actually, blame it on the Grails team for creating an awesome project ;)")
      }
      buttonPanel( constraints: BL.SOUTH ) {
         button("Close", actionPerformed: {event ->
            aboutDialog.dispose()
         })
      }
   }
}