application(title:'FeatureTest',  pack:true, locationByPlatform:true) {
  vbox {
    label( 'This is not important' )
    label( 'This is really important', cssClass: 'important' )
    button ('Nothing', actionPerformed: controller.doNothing)
    button ('Oh Noes!', cssClass: 'important')
  }
}
