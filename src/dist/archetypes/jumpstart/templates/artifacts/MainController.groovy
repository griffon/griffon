@artifact.package@class @artifact.name@ {
    def newAction = { evt = null ->
    }

    def openAction = { evt = null ->
    }

    def saveAction = { evt = null ->
    }

    def saveAsAction = { evt = null ->
    }

    def aboutAction = { evt = null ->
        withMVCGroup('about') { m, v, c ->
            c.show()
        }
    }

    def preferencesAction = { evt = null ->
        withMVCGroup('preferences') { m, v, c ->
            c.show()
        }
    }

    def quitAction = { evt = null ->
        app.shutdown()
    }

    def undoAction = { evt = null ->
    }

    def redoAction = { evt = null ->
    }

    def cutAction = { evt = null ->
    }

    def copyAction = { evt = null ->
    }

    def pasteAction = { evt = null ->
    }

    def deleteAction = { evt = null ->
    }

    def onOSXAbout = { app ->
        withMVCGroup('about') { m, v, c ->
            c.show()
        }
    }

    def onOSXQuit = { app ->
        app.shutdown()
    }

    def onOSXPrefs = { app ->
        withMVCGroup('preferences') { m, v, c ->
            c.show()
        }
    }
}
