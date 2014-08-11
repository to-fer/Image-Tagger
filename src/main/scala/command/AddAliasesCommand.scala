package command

object AddAliasesCommand {
  def unapply(cmdStr: String): Option[(String, List[String])] = {
    val splitStr = cmdStr.split(" ")

    splitStr match {
      case Array("add", "alias", tag, alias) => Some((tag, List(alias)))
      case Array("add", "alias", tag, aliases @ _*) => Some((tag, aliases.toList))
      case Array("aa", tag, alias) => Some((tag, List(alias)))
      case Array("aa", tag, aliases @ _*) => Some((tag, aliases.toList))
      case _ => None
    }
  }
}
