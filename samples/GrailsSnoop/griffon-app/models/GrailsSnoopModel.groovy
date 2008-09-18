import groovy.beans.Bindable

class GrailsSnoopModel {
   String baseUrl = "http://grails.org/doc/1.0.x/"
   String menuUrl = baseUrl + "ref/menu.html"
   String guideIndex = baseUrl + "guide/index.html"

   @Bindable String currentPageId
   @Bindable URL currentPage = new URL("file:/")
   @Bindable String message
   @Bindable String searchText

   @Bindable List history = []
   @Bindable int historyIndex = -1
}