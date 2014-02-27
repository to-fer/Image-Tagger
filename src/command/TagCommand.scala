package command

object TagCommand {
  def unapply(cmdStr: String): Option[Seq[String]] = {
    Option(cmdStr.split(" "))
  }
}
