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

import static griffon.util.GriffonApplicationUtils.*

actions {
   action( id: 'exitAction',
      name: isMacOSX ? "Quit" : "Exit",
      closure: controller.exit,
      mnemonic: isMacOSX ? 'Q' : 'X',
      accelerator: shortcut(isMacOSX ? 'Q' : 'X'),
      smallIcon: imageIcon("org/tango-project/tango-icon-theme/16x16/actions/system-log-out.png")
   )
   action( id: 'aboutAction',
      name: "About",
      closure: controller.about,
      mnemonic: 'B',
      accelerator: shortcut('B'),
   )

   action(id: 'largerFontAction',
      name: "Larger Font",
      closure: controller.largerFont,
      mnemonic: 'L',
      accelerator: shortcut('shift L')
   )
   action(id: 'smallerFontAction',
      name: "Smaller Font",
      closure: controller.smallerFont,
      mnemonic: 'S',
      accelerator: shortcut('shift S')
   )

   action( id: 'goHomeAction',
      name: "Home",
      closure: controller.goHome,
      accelerator: shortcut('M'),
      mnemonic: 'M',
      smallIcon: imageIcon("org/tango-project/tango-icon-theme/16x16/actions/go-home.png")
   )
   action( id: 'goPreviousAction',
      name: "Previous",
      enabled: bind { model.historyIndex > 0 },
      closure: controller.goPrevious,
      accelerator: shortcut('P'),
      mnemonic: 'P',
      smallIcon: imageIcon("org/tango-project/tango-icon-theme/16x16/actions/go-previous.png")
   )
   action( id: 'goNextAction',
      name: "Next",
      enabled: bind { model.historyIndex < model.history.size() - 1 },
      closure: controller.goNext,
      accelerator: shortcut('N'),
      mnemonic: 'N',
      smallIcon: imageIcon("org/tango-project/tango-icon-theme/16x16/actions/go-next.png")
   )
   action( id: 'collapseAllAction',
      name: "Collapse all",
      closure: controller.collapseAll,
      accelerator: shortcut('C'),
      mnemonic: 'C',
      shortDescription: "Collapse all categories",
      smallIcon: imageIcon("org/tango-project/tango-icon-theme/16x16/actions/go-first.png")
   )
   action( id: 'expandAllAction',
      name: "Expand all",
      closure: controller.expandAll,
      accelerator: shortcut('E'),
      mnemonic: 'E',
      shortDescription: "Expand all categories",
      smallIcon: imageIcon("org/tango-project/tango-icon-theme/16x16/actions/go-last.png")
   )
}