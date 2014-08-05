package gui

import javafx.event.{ActionEvent, EventHandler}

import event.CommandListener
import util.JavaFXExecutionContext.javaFxExecutionContext

import scala.concurrent.Future
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.control.TextField
import scalafx.scene.layout.{BorderPane, StackPane}
import scalafx.scene.{Node, Scene}

class MainWindow(commandListener: CommandListener) extends PrimaryStage {
  title = "Tagger"
  fullScreen = true
  width = 750
  height = 750

  private val imageDisplayArea = new StackPane
  scene = new Scene {
    root = new StackPane {
      content = List(
        imageDisplayArea,
        new BorderPane {
          mouseTransparent = true
          bottom = new TextField {
            requestFocus()
            onAction = new EventHandler[ActionEvent] {
              override def handle(event: ActionEvent): Unit = {
                def displayMessage(message: String): Unit = {
                  text = message
                }

                val commandString = text.value
                text = ""
                commandListener.commandEntered(commandString, displayMessage)
              }
            }
          }
        }
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
