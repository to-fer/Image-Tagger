package db

import java.nio.file.{StandardOpenOption, Files, Path}
import scala.util.parsing.combinator.JavaTokenParsers
import java.io.FileReader

class TagFile(val path: Path) {
  private object TagFileParser extends JavaTokenParsers {
    def file: Parser[List[(String, List[String])]] = rep(tagDef) ^^ (List() ++ _)
    def tagDef: Parser[(String, List[String])] = stringLiteral ~ " = " | "=" | "" ~ repsep(stringLiteral, ",") ^^ {case (tag ~ " = " | "=" | "" ~ aliases) => (tag, aliases)}
  }

  def read = {
    val fileReader = new FileReader(path.toFile)
    val tagsAndAliases = TagFileParser.parseAll(file, fileReader)
    fileReader.close()

    tagsAndAliases
  }

  def write(tag: String, aliases: List[String] = Nil) = {
    val writeBytes =
      if (aliases.isEmpty)
        tag.getBytes
      else
        (tag + " = " + aliases.mkString(",")).getBytes
    Files.write(path, writeBytes, StandardOpenOption.APPEND)
  }

  def remove(tag: String) = {
    val fileLines = Files.readAllLines(path)

    val tagLineInd = fileLines.indexOf(tag)
    if (tagLineInd != -1) {
      val newFileLines = fileLines.remove(tagLineInd)
      val writeBytes = newFileLines.mkString("\n").getBytes
      Files.write(path, writeBytes)
    }
    else
      throw new IllegalArgumentException("That tag does not exist.")
  }
}
