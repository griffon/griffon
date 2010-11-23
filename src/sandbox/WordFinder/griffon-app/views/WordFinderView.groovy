actions {
   action( id: "findWordAction",
      name: "Find Word",
      mnemonic: "F",
      accelerator: shortcut("F"),
      closure: controller.findWord
   )
}

application( title: 'WordFinder', pack: true, locationByPlatform: true ) {
   vbox {
      hbox {
         label( "Word:")
         textField( id: "wordValue", name: "wordValue" )
      }
      label( id: "answer", name: "answer", text: bind{ model.answer } )
      button( findWordAction, name: "findWord" )
   }
}

bean( model, word: bind{ wordValue.text } )