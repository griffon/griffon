import groovy.beans.Bindable

class WordFinderModel {
   static final String TYPE_A_WORD = "Please enter a valid word"
   static final String UNKNOWN_WORD = "Word doesn't exist in dictionary"

   @Bindable String answer = TYPE_A_WORD
   @Bindable String word = ""
}