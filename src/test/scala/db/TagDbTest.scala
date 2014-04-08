package db

import org.specs2.mutable._
import org.specs2.ScalaCheck
import java.nio.file.{Files, Paths}

class TagDbTest extends Specification with ScalaCheck {
  "TagDb" should {
    "Create a database file if it doesn't exist" in {
      val databaseFile = "this file does not exist.sqlite"
      new SlickTagDb(databaseFile)

      val path = Paths.get(databaseFile)
      val fileExists = Files exists path
      Files delete path

      fileExists mustEqual true
    }
  }
}
