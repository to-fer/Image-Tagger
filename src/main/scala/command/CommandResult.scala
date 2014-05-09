package command

abstract class CommandResult
case object OK extends CommandResult
case class Error(errorMessage: String) extends CommandResult
case object QuitMode extends CommandResult
case object ModeSwitch extends CommandResult
case class DisplayMessage(message: String) extends CommandResult
