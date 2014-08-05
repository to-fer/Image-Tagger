package event.mode

import event.CommandHandler
import command.CommandResult
import gui.ModeView

trait Mode {
  val commandHandler: CommandHandler
  val view: ModeView
  def start(): CommandResult
}
