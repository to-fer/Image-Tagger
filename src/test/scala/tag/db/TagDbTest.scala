package tag.db

import org.specs2.mutable._
import org.specs2.ScalaCheck
import java.nio.file.{Files, Paths}
import org.specs2.specification.{Step, Fragments}

class TagDbTest extends TestDbSpec with ScalaCheck {
  import dbSetup.testDbDir

  "TagDb" should {
    "Create a database file if it doesn't exist" in {
      val databasePath = testDbDir resolve "this file does not exist.sqlite"

      new TaggerDb(databasePath.toString)
      Files.exists(databasePath) mustEqual true
    }

    "addTag('tag that doesn't exist')" in {
      val databasePath = testDbDir resolve "tag-doesnt-exist-test.sqlite"

      val tagDb = new TaggerDb(databasePath.toString)
      val tagToAdd = "Bananas"
      tagDb.addTag(tagToAdd)

      tagDb.contains(tagToAdd) mustEqual true
    }

    "addTag('tag that already exists')" in {
      val databasePath = testDbDir resolve "tag-already-exists-test.sqlite"

      val tagDb = new TaggerDb(databasePath.toString)
      val tagToAdd = "Toast"
      tagDb.addTag(tagToAdd)
      tagDb.addTag(tagToAdd) must throwA[IllegalArgumentException]
    }

    "addTag to database" in {
      val databasePath = testDbDir resolve "tag-test.sqlite"

      val tagDb1 = new TaggerDb(databasePath.toString)
      val tagToAdd = "THEBESTTHEBESTTHEBEST"
      tagDb1.addTag(tagToAdd)

      val tagDb2 = new TaggerDb(databasePath.toString)
      tagDb2.contains(tagToAdd) mustEqual true
    }

    "tagFile(_, 'existing tag')" in {
      val tag = "tree"
      val pathToTag = Paths.get("a-tree.jpg")
      val databasePath = testDbDir resolve "file-tag-test.sqlite"

      val tagDb = new TaggerDb(databasePath.toString)
      tagDb.addTag(tag)
      tagDb.tagFile(pathToTag, tag)
      tagDb.filesWithTag(tag) mustEqual List(pathToTag.toFile)
    }

    "tagFile(_, 'tag that doesn't exist')" in {
      val databasePath = testDbDir resolve "tag-doesnt-exist-test.sqlite"

      val tagDb = new TaggerDb(databasePath.toString)
      tagDb.tagFile(
        Paths.get("test-file"),
        "this tag doesn't exist!"
      ) must throwA[IllegalArgumentException]
    }
  }
}

object dbSetup {
  lazy val testDbDir = Paths.get("test-dbs")
  lazy val setupDbDir = { Files.createDirectory(testDbDir) }
  lazy val cleanDbs = {
    testDbDir.toFile.listFiles.foreach(_.delete)
    Files.delete(testDbDir)
  }
}

trait TestDbSpec extends Specification {
  lazy val db = dbSetup
  override def map(fs: => Fragments) = Step(db.setupDbDir) ^ fs ^ Step(db.cleanDbs)
}
