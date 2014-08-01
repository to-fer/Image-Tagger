package event

import command._

class CommandListener {
  private var _modeCommandHandler: CommandHandler = _
  private var _modeSwitchHandler: CommandHandler = _

  def commandHandler = _modeCommandHandler

  def commandHandler_=(h: CommandHandler) = _modeCommandHandler = h

  def modeSwitchHandler = _modeSwitchHandler

  def modeSwitchHandler_=(h: CommandHandler) = _modeSwitchHandler = h

  def commandEntered(commandString: String, displayMessage: String => Unit): Unit = {
    val commandResult = commandHandler.handleCommand(commandString)
    commandResult match {
      case OK =>
      case Error(errorMsg) => displayMessage(errorMsg)
      case DisplayMessage(msg) => displayMessage(msg)
      case QuitMode => modeSwitchHandler.handleCommand(commandString)
      case ModeSwitch => modeSwitchHandler.handleCommand(commandString)
    }
  }
}
