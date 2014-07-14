package event

import gui.qt.gui.LineEdit
import command._
import gui.qt.Application.executionContext
import scala.concurrent.Future

class CommandListener {

  private var _modeCommandHandler: CommandHandler = _
  private var _modeSwitchHandler: CommandHandler = _
  private var _lineEdit: LineEdit = _

  def commandHandler = _modeCommandHandler

  def commandHandler_=(h: CommandHandler) = _modeCommandHandler = h

  def modeSwitchHandler = _modeSwitchHandler

  def modeSwitchHandler_=(h: CommandHandler) = _modeSwitchHandler = h

  def lineEdit = _lineEdit

  def lineEdit_=(le: LineEdit) = _lineEdit = le

  def commandEntered(): Unit = Future {
    val enteredCommand = lineEdit.text
    lineEdit.text = ""

    // TODO make a proper message/error message displays
    def displayMessage(msg: String): Unit = {
      lineEdit.text = msg
      lineEdit.selectAll()
    }

    val commandResult = commandHandler.handleCommand(enteredCommand)
    commandResult match {
      case OK =>
      case Error(errorMsg) => displayMessage(errorMsg)
      case DisplayMessage(msg) => displayMessage(msg)
      case QuitMode => {
        modeSwitchHandler.handleCommand(enteredCommand)
      }
      case ModeSwitch => modeSwitchHandler.handleCommand(enteredCommand)
    }
  }
}
