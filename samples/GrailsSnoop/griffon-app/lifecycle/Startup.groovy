def rootBuilder = app.builders.root
def rootController = app.controllers.root
def rootModel = app.models.root

rootBuilder.build(GrailsSnoopDialogs)
rootController.loadPages()
