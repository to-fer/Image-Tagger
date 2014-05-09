package event.mode

import event.CommandHandler
import command.CommandResult

trait Mode {
  val commandHandler: CommandHandler
  val name: String
  def start(): CommandResult
}
