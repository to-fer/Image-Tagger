package event

import command._
import model.{NormalMessage, ErrorMessage, MessageModel}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CommandListener(messageModel: MessageModel) {
  type CommandHandler = PartialFunction[String, CommandResult]
  private var _modeCommandHandler: CommandHandler = _
  private var _modeSwitchHandler: CommandHandler = _

  def commandHandler = _modeCommandHandler

  def commandHandler_=(h: CommandHandler) = _modeCommandHandler = h

  def modeSwitchHandler = _modeSwitchHandler

  def modeSwitchHandler_=(h: CommandHandler) = _modeSwitchHandler = h

  def commandEntered(commandString: String): Unit = Future {
    val commandResult = commandHandler(commandString)
    commandResult match {
      case OK =>
      case Error(errorMsg) => messageModel.message = new ErrorMessage(errorMsg)
      case DisplayMessage(msg) => messageModel.message = new NormalMessage(msg)
      case QuitMode => modeSwitchHandler(commandString)
      case ModeSwitch => modeSwitchHandler(commandString)
    }
  }
}
