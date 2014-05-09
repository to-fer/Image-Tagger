package event

import command.CommandResult

trait CommandHandler {
  def handleCommand(cmd: String): CommandResult
}
