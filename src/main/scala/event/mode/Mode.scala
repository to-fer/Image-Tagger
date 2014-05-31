package event.mode

import event.CommandHandler
import command.CommandResult

trait Mode {
  val commandHandler: CommandHandler
  def start(): CommandResult
}
