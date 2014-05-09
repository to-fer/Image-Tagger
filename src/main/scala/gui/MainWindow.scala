package gui

import com.trolltech.qt.gui.QFrame
import command.{SkipCommand, AddTagCommand, QuitCommand, TagCommand}
import qt.image.{SequentialImageViewer, Image}
import qt.init.QtApp
import qt.gui._
import java.nio.file.{Files, Paths}
import qt.util.Screen
import com.trolltech.qt.core.Qt.AlignmentFlag
import com.trolltech.qt.gui.QStackedLayout.StackingMode
import tag.db.SlickTagDb
import event.{CommandListener, CommandHandler}

class MainWindow(commandListener: CommandListener) extends Window {
  title = "Tagger"
  maximized = true

  private val commandLineEdit = new LineEdit {
    width = 400
    height = 25
    alignment = List(AlignmentFlag.AlignHCenter, AlignmentFlag.AlignBottom)
    styleSheet = "background: white;"
    focus()
  }
  commandLineEdit.returnPressed = commandListener.commandEntered
  commandListener.lineEdit = commandLineEdit

  private val (screenWidth, screenHeight) = Screen.size
  
  private val displayStack = new StackedWidget {
    width = screenWidth
    height = screenHeight
    stackingMode = StackingMode.StackOne
  }
  
  private var _viewMode: Widget = _

  def viewMode = _viewMode
  
  def viewMode_=(modeWidget: Widget): Unit = {
    if (!displayStack.content.contains(modeWidget))
      displayStack += modeWidget
    displayStack.currentWidget = modeWidget
    _viewMode = modeWidget
  }

  content = List(
    new Container(commandLineEdit),
    new Container(displayStack)
  )
}

/*
object MainWindow extends QtApp {

  // Non-GUI initialization stuff
  val tagDb = new SlickTagDb("db.sqlite")
  var knownTags = tagDb.tags

  val imageDir = Paths.get(System.getProperty("user.home"), "images")
  if (!Files.exists(imageDir))
    Files.createDirectory(imageDir)

  val imageDest = Paths.get(System.getProperty("user.home"), "images", "tagged")
  if (!Files.exists(imageDest))
    Files.createDirectory(imageDest)

  val (screenWidth, screenHeight) = Screen.size

  override val mainWindow = new Window {
    title = "Tagger"
    maximized = true

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

    content = List (
      new Container(new LineEdit {
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
            selectAll()
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

                  def showNextImageIfExists() =
                    if (viewer.hasNext)
                      viewer.showNextImage()
                    else
                      errorMessage("All images have been tagged.")

                  if (enteredCommand != "") {
                    enteredCommand match {
                      case SkipCommand(_) => showNextImageIfExists()
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
                      case TagCommand(tags) if (tags.forall(knownTags.contains)) => {
                        val imageFile = viewer.currentImageFile
                        val destFile = imageDest resolve imageFile.toPath.getFileName
                        tagDb.tagFile(destFile, tags)
                        showNextImageIfExists()
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
                val gridWidget = new GridWidget
                val ImagesPerRow = 5
                val imageWidth = screenWidth/ImagesPerRow
                val imageHeight = screenHeight/ImagesPerRow
                val taggedImageFiles = tagDb.filesWithTag(tag)
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
          }
        }
        returnPressed = commandEntered
      }),
      new Container(displayStack)
    )
  }
}
*/