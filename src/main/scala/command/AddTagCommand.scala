package command

object AddTagCommand {
  def unapply(cmdStr: String): Option[String] = {
    val splitStr = cmdStr.split(" ")

    splitStr match {
      case Array("add", "tag", tag) => Some(tag)
      case Array("add", tag) => Some(tag)
      case Array("at" , tag) => Some(tag)
      case _ => None
    }
  }
}
