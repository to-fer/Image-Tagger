package mode

import com.trolltech.qt.gui.{QVBoxLayout, QLineEdit}
import command.{AddTagCommand, QuitCommand, TagCommand}
import db.TagDb
import image.SequentialImageViewer
import java.nio.file.Paths
import java.util.Set
import image.Image.imageFilesIn
import com.trolltech.qt.core.QRect
/*
class TagMode(otherCmdHandler: CommandHandler,
              commandHistory: List[() => Unit],
              knownTags: Set[String],
              tagDb: TagDb,
              layout: QVBoxLayout,
              edit: QLineEdit,
              screenGeometry: QRect) extends CommandHandler(edit) {
  private var viewer: SequentialImageViewer = _

  def handle(cmd: String): Unit = {
    if (cmd != "") {
      cmd match {
        case AddTagCommand(tag) => {
          if (tag.contains(" "))
            println("Tags cannot contain spaces.")
          else if (knownTags.contains(tag))
            println("That tag already exists.")
          else {
            knownTags.add(tag)
            tagDb.createTable(tag)
          }
        }

        case QuitCommand(_) => quit()
        // THIS WILL MATCH EVERYTHING! KEEP IT LAST.
        case TagCommand(tags) => {
          if (!tags.forall(knownTags.contains(_)))
            edit.setText("Unknown tag!")
          else {
            val imageFile = viewer.currentImageFile
            tags foreach { tag => {
              tagDb.addPathToTable(tag, imageFile.getPath())
            }}
            viewer.showNextImage()
          }
        }
      }
    }
  }

  def start() = {

    val imageFiles = imageFilesIn(Paths.get(System.getProperty("user.home"), "Pictures", "Pony").toString)
    viewer = new SequentialImageViewer(
      layout = layout,
      imageFiles = imageFiles,
      imageWidth = screenGeometry.width,
      imageHeight = screenGeometry.height
    )
    viewer.showFirstImage()
  }

  def quit() = {
    layout.removeWidget(viewer.getCurrentImage)
    edit.returnPressed.disconnect(this)
    edit.returnPressed.connect(otherCmdHandler, "onCommand()")
  }
}
*/

