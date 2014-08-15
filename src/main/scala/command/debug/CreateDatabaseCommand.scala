package command.debug

object CreateDatabaseCommand {
  def unapply(cmdStr: String): Option[String] = {
    cmdStr match {
      case "create database" => Some(cmdStr)
      case "create db" => Some(cmdStr)
      case _ => None
    }
  }
}
