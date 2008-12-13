import java.awt.datatransfer.DataFlavor
import java.awt.dnd.*

def m = app.models.root
def v = app.builders.root
def c = app.controllers.root

v.editor.textEditor.requestFocus()

def dtListener =  [
    dragEnter:{DropTargetDragEvent evt ->
        if (evt.dropTargetContext.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            evt.acceptDrag(DnDConstants.ACTION_COPY)
        } else {
            evt.rejectDrag()
        }
    },
    dragOver:{DropTargetDragEvent evt ->
        //dragEnter(evt)
    },
    dropActionChanged:{DropTargetDragEvent evt ->
        //dragEnter(evt)
    },
    dragExit:{DropTargetEvent evt  ->
    },
    drop:{DropTargetDropEvent evt  ->
        evt.acceptDrop DnDConstants.ACTION_COPY
        //println "Dropping! ${evt.transferable.getTransferData(DataFlavor.javaFileListFlavor)}"
        if (controller.askToSaveFile()) {
            controller.loadScriptFile(evt.transferable.getTransferData(DataFlavor.javaFileListFlavor)[0])
        }
    },
] as DropTargetListener

[app.appFrames[0], v.editor.textEditor].each {
    new DropTarget(it, DnDConstants.ACTION_COPY, dtListener)
}
