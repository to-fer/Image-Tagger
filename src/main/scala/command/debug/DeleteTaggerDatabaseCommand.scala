package command.debug

object DeleteTaggerDatabaseCommand {
  def unapply(cmdStr: String): Option[String] = {
    cmdStr match {
      case "delete database" => Some("")
      case "del db" => Some("")
      case "ddb" => Some("")
      case _ => None
    }
  }
}
