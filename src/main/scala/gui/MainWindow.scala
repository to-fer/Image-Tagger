package gui

import javafx.event.{ActionEvent, EventHandler}

import event.CommandListener
import util.JavaFXExecutionContext.javaFxExecutionContext

import scala.concurrent.Future
import scalafx.application.JFXApp.PrimaryStage
import scalafx.geometry.Pos
import scalafx.scene.control.TextField
import scalafx.scene.layout.StackPane
import scalafx.scene.{Node, Scene}

class MainWindow(commandListener: CommandListener, messageDisplay: MessageDisplay, inputFieldWidth: Int) extends PrimaryStage {
  title = "Tagger"
  fullScreen = true
  width = 750
  height = 750

  private val imageDisplayArea = new StackPane

  // TODO make it so only the input text field can be focused
  scene = new Scene {
    root = new StackPane {
      content = List(
        imageDisplayArea,
        new TextField {
          alignmentInParent = Pos.BOTTOM_CENTER
          maxWidth = inputFieldWidth
          requestFocus()
          onAction = new EventHandler[ActionEvent] {

            override def handle(event: ActionEvent): Unit = {
              if (messageDisplay.isVisible)
                messageDisplay.hide()

              val commandString = text.value
              text = ""
              commandListener.commandEntered(commandString)
            }

          }
        },
        messageDisplay.node
      )
    }
  }

  private var _currentModeView: Node = _

  def currentModeView = _currentModeView
  
  def currentModeView_=(modeRoot: Node): Unit = Future {
    imageDisplayArea.content = modeRoot
    _currentModeView = modeRoot
  }


}
