package file

import java.nio.file.{Paths, Files, Path}

object ConfigFileParser {

  def parse(configFile: Path): (Option[Path], Option[Path]) = {
    val configFileContents = Files.readAllLines(configFile).toArray(Array[String]())
    def findAndParse(configFileVar: String): Option[Path] = {
      val fileVar = configFileContents.find(_.contains(configFileVar))
      val varValue = fileVar.flatMap(v => {
        val equals = " = "
        if (v.contains(equals)) {
          val path = v.substring(v.indexOf(equals) + equals.length)
          Some(Paths.get(path))
        }
        else None
      })
      varValue
    }
    val imageSourceDir = findAndParse("source")
    val imageDestDir = findAndParse("dest")
    (imageSourceDir, imageDestDir)
  }

}
