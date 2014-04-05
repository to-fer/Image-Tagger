package command

object SkipCommand {
  def unapply(cmdStr: String): Option[String] = cmdStr match {
    case "skip" => Some(cmdStr)
    case "s" => Some(cmdStr)
    case _ => None
  }
}
