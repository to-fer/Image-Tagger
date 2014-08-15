package command.debug

object DeleteDatabaseCommand {
  def unapply(cmdStr: String): Option[String] = {
    cmdStr match {
      case "delete database" => Some(cmdStr)
      case "del db" => Some(cmdStr)
      case _ => None
    }
  }
}
