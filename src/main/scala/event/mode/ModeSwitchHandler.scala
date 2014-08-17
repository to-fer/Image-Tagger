package event.mode

import command._

class ModeSwitchHandler(modeSwitcher: ModeSwitcher, searchMode: SearchMode, tagMode: TagMode) {
  private def startAndSwitchMode(mode: Mode): CommandResult = {
    val result = mode.start()
    modeSwitcher.switch(mode)
    result
  }

  type CommandHandler = PartialFunction[String, CommandResult]

  val commandHandler: CommandHandler = {
    case TagModeCommand(_) => startAndSwitchMode(tagMode)
    case SearchModeCommand(_) => startAndSwitchMode(searchMode)
  }
}
