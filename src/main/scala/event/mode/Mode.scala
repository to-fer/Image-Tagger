package event.mode

import command.debug.{CreateDatabaseCommand, DeleteDatabaseCommand}
import command.{OK, Error, CommandResult}
import gui.ModeView
import tag.db.TaggerDb


abstract class Mode(tagDb: TaggerDb, val view: ModeView, debugEnabled: Boolean) {

  type CommandHandler = PartialFunction[String, CommandResult]

  // Handler for mode-specific commands.
  protected val modeHandler: CommandHandler

  // Command handling common to all modes.
  private val commonCommandActions: CommandHandler = {
    // Ignore empty inputs
    case "" => OK
    case DeleteDatabaseCommand(_) if debugEnabled => {
      tagDb.delete()
      OK
    }
    case CreateDatabaseCommand(_) if debugEnabled => {
      tagDb.create()
      OK
    }
    case cmd => Error(s"Unknown command: $cmd.")
  }

  // Must be lazy, otherwise throws NullPointerException
  final lazy val commandHandler: CommandHandler = modeHandler orElse commonCommandActions

  final def onCommand(cmd: String): CommandResult =
    commandHandler(cmd)

  def start(): CommandResult
}
