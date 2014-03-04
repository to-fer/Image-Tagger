import com.trolltech.qt.core.Qt.AlignmentFlag
import com.trolltech.qt.gui.{QFrame, QSizePolicy}
import command.{AddTagCommand, QuitCommand, TagCommand}
import db.TagDb
import qt.image.{SequentialImageViewer, Image}
import qt.init.QtApp
import qt.gui._
import java.nio.file.{Files, Paths}
import qt.util.Screen

object GUI extends QtApp {

  override val mainWindow = new Window {
    title = "Tagger"
    maximized = true
    sizePolicy = new QSizePolicy(QSizePolicy.Policy.Fixed, QSizePolicy.Policy.Fixed)
  }

  val (screenWidth, screenHeight) = Screen.size
  val searchWidget = new ScrollWidget {
    width = screenWidth
    height = screenHeight
  }
  val imageWidget = new VBoxWidget {
    hide()
  }

  val tagDb = new TagDb("db.sqlite")
  val knownTags = tagDb.getTableNames
  val imageDir = Paths.get(System.getProperty("user.home"), "images")
  if (!Files.exists(imageDir))
    Files.createDirectory(imageDir)

  val imageDest = Paths.get(System.getProperty("user.home"), "images", "tagged")
  if (!Files.exists(imageDest))
    Files.createDirectory(imageDest)

  val lineEdit = new LineEdit {
    width = 400
    height = 25
    alignment = List(AlignmentFlag.AlignHCenter, AlignmentFlag.AlignBottom)
    focus()

    val commandEntered: () => Unit = () => {
      def onCommand() = {
        val enteredCommand = text
        text = ""
        enteredCommand
      }

      val enteredCommand = onCommand()

      enteredCommand match {
        case "tag" => {
          val imageFiles = Image.imageFilesIn(imageDir.toString)
          if (!imageFiles.isEmpty) {
            searchWidget.hide()
            imageWidget.show()

            val viewer = new SequentialImageViewer(
              parent = imageWidget,
              imageFiles = imageFiles,
              imageWidth = screenWidth,
              imageHeight = screenHeight
            )
            viewer.showFirstImage()
            returnPressed_=(() => {
              val enteredCommand = onCommand()

              if (enteredCommand != "") {
                enteredCommand match {
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
                  case TagCommand(tags) if (tags.forall(knownTags.contains(_))) => {
                    val imageFile = viewer.currentImageFile
                    val destFile = imageDest resolve imageFile.toPath.getFileName
                    tags foreach { tag => {
                      tagDb.addPathToTable(tag, destFile.toString)
                    }}
                    if (viewer.hasNext)
                      viewer.showNextImage()
                    else
                      text = "All images have been tagged."
                    Files.move(imageFile.toPath, destFile)
                  }
                  case "delete" => {
                    val curImageFile = viewer.currentImageFile
                    val curImage = viewer.getCurrentImage
                    curImage.dispose()
                    curImageFile.delete()
                    viewer.showNextImage()
                  }
                  case QuitCommand(_) => {
                    viewer.dispose()
                    returnPressed_=(commandEntered)
                    searchWidget.show()
                    imageWidget.hide()
                  }
                  case _ =>
                    text = "Unknown command."
                }
              }
            })
          }
          else
            text = "There are no images to tag."
        }
        case tag => {
          if (knownTags.contains(tag)) {
            val taggedImagefiles = tagDb.getTableFiles(tag)
            val gridWidget = new GridWidget
            val imageWidth = screenWidth/5
            val imageHeight = screenHeight/5
            taggedImagefiles foreach { f => {
              val image = new Image(f.toString, imageWidth, imageHeight) {
                frameShape_=(QFrame.Shape.Box)
              }
              gridWidget += image
            }}
            searchWidget.content = gridWidget
          }
          else
            println("Invalid tag.")
        }
      }}
    returnPressed_=(commandEntered)
  }

  mainWindow content = List (
    lineEdit,
    searchWidget,
    imageWidget
  )
}
