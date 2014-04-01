package gui

import com.trolltech.qt.gui.QFrame
import command.{AddTagCommand, QuitCommand, TagCommand}
import qt.image.{SequentialImageViewer, Image}
import qt.init.QtApp
import qt.gui._
import java.nio.file.{Files, Paths}
import qt.util.Screen
import com.trolltech.qt.core.Qt.AlignmentFlag
import com.trolltech.qt.gui.QStackedLayout.StackingMode
import db.SlickTagDb

object GUI extends QtApp {

  override val mainWindow = new Window {
    title = "Tagger"
    maximized = true
  }

  val (screenWidth, screenHeight) = Screen.size
  val searchWidget = new ScrollWidget {
    width = screenWidth
    height = screenHeight
  }
  val imageWidget = new StackedWidget {
    width = screenWidth
    height = screenHeight
  }
  val displayStack = new StackedWidget {
    width = screenWidth
    height = screenHeight
    stackingMode = StackingMode.StackOne
    content = List (
      searchWidget,
      imageWidget
    )
  }
  val tagDb = new SlickTagDb("db.sqlite")
  var knownTags = tagDb.tags match {
    case Some(tags) => tags.toSet
    case None => throw new RuntimeException("Could not read tags from database.")
  }

  val imageDir = Paths.get(System.getProperty("user.home"), "images")
  if (!Files.exists(imageDir))
    Files.createDirectory(imageDir)

  val imageDest = Paths.get(System.getProperty("user.home"), "images", "tagged")
  if (!Files.exists(imageDest))
    Files.createDirectory(imageDest)

  val lineEdit: LineEdit = new LineEdit {
    width = 400
    height = 25
    alignment = List(AlignmentFlag.AlignHCenter, AlignmentFlag.AlignBottom)
    styleSheet = "background: white;"
    focus()

    val commandEntered: () => Unit = () => {
      def onCommand() = {
        val enteredCommand = text
        text = ""
        enteredCommand
      }

      def errorMessage(msg: String): Unit = {
        text = msg
        lineEdit.selectAll()
      }
      val enteredCommand = onCommand()

      enteredCommand match {
        case "tag" => {
          val imageFiles = Image.imageFilesIn(imageDir.toString)
          if (!imageFiles.isEmpty) {
            displayStack.currentWidget = imageWidget

            val viewer = new SequentialImageViewer(
              layout = imageWidget,
              imageFiles = imageFiles,
              imageWidth = screenWidth,
              imageHeight = screenHeight
            )
            viewer.showFirstImage()
            returnPressed = () => {
              val enteredCommand = onCommand()

              if (enteredCommand != "") {
                enteredCommand match {
                  case AddTagCommand(tag) => {
                    if (tag.contains(" "))
                      errorMessage("Tags cannot contain spaces.")
                    else if (knownTags.contains(tag))
                      errorMessage("That tag already exists.")
                    else {
                      knownTags = knownTags + tag
                      tagDb.addTag(tag)
                    }
                  }
                  case TagCommand(tags) if (tags.forall(knownTags.contains(_))) => {
                    val imageFile = viewer.currentImageFile
                    val destFile = imageDest resolve imageFile.toPath.getFileName
                    tagDb.tagFile(destFile.toString, tags)
                    if (viewer.hasNext)
                      viewer.showNextImage()
                    else
                      errorMessage("All images have been tagged.")
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
                    returnPressed = commandEntered
                    displayStack.currentWidget = searchWidget
                  }
                  case _ =>
                    errorMessage("Unknown command.")
                }
              }
            }
          }
          else
            errorMessage("There are no images to tag.")
        }
        case tag => {
          if (knownTags.contains(tag)) {
            val taggedImageFiles = tagDb.filesWithTag(tag)
            val gridWidget = new GridWidget
            val imageWidth = screenWidth/5
            val imageHeight = screenHeight/5
            taggedImageFiles foreach { f => {
              val image = new Image(f.toString, imageWidth, imageHeight) {
                frameShape = QFrame.Shape.Box
              }
              gridWidget += image
            }}
            searchWidget.content = gridWidget
          }
          else
            errorMessage("Invalid tag.")
        }
      }}
    returnPressed = commandEntered
  }

  mainWindow content = List (
    new Container(lineEdit),
    new Container(displayStack)
  )
}
