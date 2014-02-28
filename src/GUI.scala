import com.trolltech.qt.gui.QSizePolicy
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
  }

  val searchWidget = new GridWidget
  val imageWidget = new VBoxWidget {
    hide()
  }

  val tagDb = new TagDb("db.sqlite")
  val tags = tagDb.getTableNames
  val imageDir = Paths.get(System.getProperty("user.home"), "images")
  val imageDest = Paths.get(System.getProperty("user.home"), "images", "tagged")
  if (!Files.exists(imageDest))
    Files.createDirectory(imageDest)

  val (screenWidth, screenHeight) = Screen.size
  val lineEdit = new LineEdit {
    width = 400
    height = 25
    parent = mainWindow
    move((screenWidth - width)/2, screenHeight - height * 3)

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
                  else if (tags.contains(tag))
                    println("That tag already exists.")
                  else {
                    tags.add(tag)
                    tagDb.createTable(tag)
                  }
                }
                case TagCommand(tags) if (tags.forall(tags.contains(_))) => {
                  val imageFile = viewer.currentImageFile
                  val destFile = imageDest resolve imageFile.toPath.getFileName
                  tags foreach { tag => {
                    tagDb.addPathToTable(tag, destFile.toString)
                  }}
                  viewer.showNextImage()
                  Files.move(imageFile.toPath, destFile)
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
          if (tags.contains(tag)) {
            val taggedImagefiles = tagDb.getTableFiles(tag)
            taggedImagefiles foreach { f => {
              val image = new Image(f.toString)
              searchWidget += image
            }}
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

  mainWindow.show()
}
