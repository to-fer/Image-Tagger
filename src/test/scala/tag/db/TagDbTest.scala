package tag.db

import java.nio.file.{Files, Paths}

import org.specs2._

class TagDbTest extends mutable.Specification {

  lazy val testDbDir = Paths.get("test-databases")

  // Executed before all specifications.
  step {
    if (Files.notExists(testDbDir))
      Files.createDirectory(testDbDir)
    success
  }

  "TagDb" should {
    "Create a database file if it doesn't exist when constructor is called" in {
      val databasePath = testDbDir resolve "this file does not exist.sqlite"
      new TaggerDb(databasePath)
      Files.exists(databasePath) mustEqual true
    }

    "Add tags to the database" in {
      val databasePath = testDbDir resolve "tag-doesnt-exist-test.sqlite"

      val tagDb = new TaggerDb(databasePath)
      val tagToAdd = "Bananas"
      tagDb.addTag(tagToAdd)

      tagDb.contains(tagToAdd) mustEqual true
    }

    "Throw an exception when trying to add a tag that already exists to the database" in {
      val databasePath = testDbDir resolve "tag-already-exists-test.sqlite"

      val tagDb = new TaggerDb(databasePath)
      val tagToAdd = "Toast"
      tagDb.addTag(tagToAdd)
      tagDb.addTag(tagToAdd) must throwA[IllegalArgumentException]
    }

    "Tag files" in {
      val tag = "tree"
      val pathToTag = Paths.get("a-tree.jpg")
      val databasePath = testDbDir resolve "file-tag-test.sqlite"

      val tagDb = new TaggerDb(databasePath)
      tagDb.addTag(tag)
      tagDb.tagFile(pathToTag, tag)
      tagDb.filesWithTag(tag) mustEqual List(pathToTag.toFile)
    }

    "Throw an exception when trying to add a tag that doesn't exist to a file" in {
      val databasePath = testDbDir resolve "tag-doesnt-exist-test.sqlite"

      val tagDb = new TaggerDb(databasePath)
      tagDb.tagFile(
        Paths.get("test-file"),
        "this tag doesn't exist!"
      ) must throwA[IllegalArgumentException]
    }
  }

  // Executed after all specifications
  step {
    val testDatabases = testDbDir.toFile.listFiles
    testDatabases.foreach(_.delete())
    Files.delete(testDbDir)

    success
  }
}
