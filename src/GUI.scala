import com.trolltech.qt.core.Qt.AlignmentFlag
import command.{AddTagCommand, QuitCommand, TagCommand}
import db.TagDb
import image.{SequentialImageViewer, Image}
import init.QtApp
import gui._
import java.nio.file.{Files, Paths}
import util.Screen

object GUI extends QtApp {

  val mainWindow = new Window {
    title = "Tagger"

    val searchWidget = new GridWidget
    val imageWidget = new VBoxWidget {
      hide()
    }

    val tagDb = new TagDb("db.sqlite")
    val tags = tagDb.getTableNames
    val imageDir = Paths.get(System.getProperty("user.home"), "Pictures", "Pony")
    val imageDest = Paths.get(System.getProperty("user.home"), "Pictures", "Tagged")
    if (!Files.exists(imageDest))
      Files.createDirectory(imageDest)

    val (screenWidth, screenHeight) = Screen.size

    val lineEdit = new LineEdit {
      alignment = List(AlignmentFlag.AlignCenter, AlignmentFlag.AlignBottom)
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
                    else if (tags.contains(tag))
                      println("That tag already exists.")
                    else {
                      tags.add(tag)
                      tagDb.createTable(tag)
                    }
                  }
                  case TagCommand(tags) => {
                    if (!tags.forall(tags.contains(_)))
                      text = "Unknown tag!"
                    else {
                      val imageFile = viewer.currentImageFile
                      val destFile = imageDest resolve imageFile.toPath.getFileName
                      tags foreach { tag => {
                        tagDb.addPathToTable(tag, destFile.toString)
                      }}
                      viewer.showNextImage()
                      Files.move(imageFile.toPath, destFile)
                    }
                  }
                  // THIS WILL MATCH EVERYTHING! KEEP IT LAST.
                  case QuitCommand(_) => {
                    viewer.dispose()
                    returnPressed_=(commandEntered)
                    searchWidget.show()
                    imageWidget.hide()
                  }
                }
              }
            })
          }
          case tag => {
            if (tags.contains(tag)) {
              val taggedImagefiles = tagDb.getTableFiles(tag)
              taggedImagefiles foreach { f => {
                val image = new Image(f.toString, screenWidth, screenHeight)
                searchWidget += image
              }}
            }
            else
              println("Invalid tag.")
          }
      }}
      returnPressed_=(commandEntered)
    }
    val lineEditContainer = new VBoxWidget {
      content = lineEdit
    }

    content = List (
      lineEditContainer,
      searchWidget,
      imageWidget
    )

    maximized = true
    show()
  }
}
