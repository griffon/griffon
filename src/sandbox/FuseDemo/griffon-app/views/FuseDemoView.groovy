//import swing.*
import static java.awt.BorderLayout.*
import static java.awt.FlowLayout.*

actions {
   action( id: 'changeToBlackAction',
      name: "Black Theme",
      enabled: bind { model.currentThemeId != FuseDemoModel.BLACK_THEME },
      closure: { controller.changeTheme(FuseDemoModel.BLACK_THEME) }
   )
   action( id: 'changeToRedAction',
      name: "Red Theme",
      enabled: bind { model.currentThemeId != FuseDemoModel.RED_THEME },
      closure: { controller.changeTheme(FuseDemoModel.RED_THEME) }
   )
}

application( title: "Fuse Griffon Demo",  size: [400,300], locationByPlatform: true,
             resizable: false ) {
   borderLayout()
   contentPanel( id: 'content', constraints: CENTER, injectResources: true ) {
      borderLayout()
      headerPanel( id: 'header', constraints: NORTH,  injectResources: true )
      titleLabel(  id: 'title',  constraints: CENTER, injectResources: true, text: "Fuse + Groovy" )
      footerPanel( id: 'footer', constraints: SOUTH,  injectResources: true )
   }
   /*
   container( new ContentPanel(), id: 'content', constraints: CENTER ) {
      borderLayout()
      widget( id: 'header', new HeaderPanel(), constraints: NORTH )
      widget( id: 'title',  new TitleLabel("Fuse + Groovy"), constraints: CENTER )
      widget( id: 'footer', new FooterPanel(), constraints: SOUTH )
   }
   */
   panel( constraints: SOUTH ) {
      flowLayout( alignment: TRAILING )
      button( changeToBlackAction )
      button( changeToRedAction )
   }
}
