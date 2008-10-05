class WordFinderController {
    def model
    def view

    static dictionary = [
       "pugnacious": "Combative in nature; belligerent."
    ]

    def findWord = { evt = null ->
       if( model.word ) {
          model.answer = dictionary.get( model.word, WordFinderModel.UNKNOWN_WORD )
       }else{
          model.answer = WordFinderModel.TYPE_A_WORD
       }
    }
}