def v = app.builders.root
def c = app.controllers.root
def m = app.models.root

v.build(SwingPadDialogs)
v.bean( app.appFrames[0],
   title: v.bind { m.dirty ? c.updateTitle(): c.updateTitle() } )

v.flamingoMenu.selected = false
v.trayMenu.selected = false