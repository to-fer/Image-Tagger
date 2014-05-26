package event.mode

import event.CommandHandler
import command._
import command.Error
import scala.Some

class ModeSwitchHandler(modeSwitcher: ModeSwitcher, searchMode: SearchMode, tagMode: TagMode) extends CommandHandler {
  private def startAndSwitchMode(mode: Mode): CommandResult = {
    val result = mode.start()
    modeSwitcher.switch(mode)
    result
  }

  override def handleCommand(cmd: String): CommandResult = cmd match {
    case TagModeCommand(_) => startAndSwitchMode(tagMode)
    case SearchModeCommand(_) => startAndSwitchMode(searchMode)
    case QuitCommand(_) => startAndSwitchMode(searchMode)
  }
}
