package gui

import javafx.event.{ActionEvent, EventHandler}

import event.CommandListener
import util.JavaFXExecutionContext.javaFxExecutionContext

import scala.concurrent.Future
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.control.TextField
import scalafx.scene.layout.{BorderPane, StackPane}
import scalafx.scene.{Parent, Scene}

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

  private var _currentModeView: Parent = _

  def currentModeView = _currentModeView
  
  def currentModeView_=(modeWidget: Parent): Unit = Future {
    imageDisplayArea.children.clear()
    imageDisplayArea.children.add(modeWidget)
    _currentModeView = modeWidget
  }
}
