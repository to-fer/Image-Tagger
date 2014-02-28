import com.trolltech.qt.gui.{QFrame, QPalette, QSizePolicy}
import command.{AddTagCommand, QuitCommand, TagCommand}
import db.TagDb
import qt.image.{SequentialImageViewer, Image}
import qt.init.QtApp
import qt.gui._
import java.nio.file.{Files, Paths}
import qt.util.Screen

object GUI extends QtApp {

  val mainWindow: Window = new Window {
    title = "Tagger"
    maximized = true
    sizePolicy = new QSizePolicy(QSizePolicy.Policy.Fixed, QSizePolicy.Policy.Fixed)
    backgroundRole = QPalette.ColorRole.Dark
  }

  val (screenWidth, screenHeight) = Screen.size
  val searchWidget = new ScrollWidget {
    width = screenWidth
    height = screenHeight
  }
  val imageWidget = new VBoxWidget {
    backgroundRole = QPalette.ColorRole.Dark
    hide()
  }

  val tagDb = new TagDb("db.sqlite")
  val knownTags = tagDb.getTableNames
  val imageDir = Paths.get(System.getProperty("user.home"), "images")
  val imageDest = Paths.get(System.getProperty("user.home"), "images", "tagged")
  if (!Files.exists(imageDest))
    Files.createDirectory(imageDest)


  val lineEdit = new LineEdit {
    width = 400
    height = 25

    val commandEntered: () => Unit = () => {
      def onCommand() = {
        val enteredCommand = text
        text = ""
        enteredCommand
      }

      val enteredCommand = onCommand()

      enteredCommand match {
        case "tag" => {
          searchWidget.hide()
          imageWidget.show()

          val imageFiles = Image.imageFilesIn(imageDir.toString)

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
    searchWidget,
    imageWidget
  )

  lineEdit.parent = mainWindow
  lineEdit.move((screenWidth - lineEdit.width)/2, screenHeight - lineEdit.height * 3)
  lineEdit.focus()

  mainWindow.show()
}
