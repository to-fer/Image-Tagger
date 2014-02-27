package command

object TagModeCommand {
  def unapply(cmdStr: String): Option[String] = {
    println("unapply")
    cmdStr match {
      case "tag mode" => Some("tag mode")
      case "tagm"     => Some("tag mode")
      case "tag"      => Some("tag mode")
      case "tm"       => Some("tag mode")
      case _          => None
    }
  }
}


