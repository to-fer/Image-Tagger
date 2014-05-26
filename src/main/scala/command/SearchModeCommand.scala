package command

object SearchModeCommand {
  def unapply(cmdStr: String) = cmdStr match {
    case "search mode" => Some("search")
    case "searchm"     => Some("search")
    case "search"      => Some("search")
    case "sm"       => Some("search")
    case _          => None
  }
}
