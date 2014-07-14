package command

object QuitCommand {
  def unapply(cmdStr: String): Option[String] = cmdStr match {
    case "quit" => Some("quit")
    case "q" => Some("quit")
    case _ => None
  }
}
