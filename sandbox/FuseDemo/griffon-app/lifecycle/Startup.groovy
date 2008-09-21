import swing.FuseInjector

def rootController = app.controllers.root
rootController.changeTheme(FuseDemoModel.BLACK_THEME)
//rootController.injectResources(["content","header","footer","title"])
FuseInjector.injectResources()