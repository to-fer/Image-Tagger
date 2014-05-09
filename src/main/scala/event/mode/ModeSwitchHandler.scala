package event.mode

import event.CommandHandler
import command.{Error, QuitCommand, CommandResult}

class ModeSwitchHandler(modeSwitcher: ModeSwitcher, modes: Seq[Mode]) extends CommandHandler {
  override def handleCommand(cmd: String): CommandResult = {
    modes.find(_.name == cmd) match {
      case Some(mode) => {
        val result = mode.start()
        modeSwitcher.switch(mode)
        result
      }
      case None => {
        cmd match {
          case QuitCommand(_) => {
            modes.find(_.name == "search") match {
              case Some(searchMode) => {
                val result = searchMode.start()
                modeSwitcher.switch(searchMode)
                result
              }
              case None => throw new Exception("There is no search mode!")
            }
          }
          case unknownCommand: String =>
            Error(s"Unknown command: $unknownCommand")
        }
      }
    }
  }
}
