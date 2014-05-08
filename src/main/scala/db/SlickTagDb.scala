package db

import java.io.File
import scala.slick.driver.SQLiteDriver.simple._
import Database.dynamicSession
import java.nio.file.{Files, Paths}

class SlickTagDb(dbPath: String) {
  private var _tags: Set[String] = Set.empty[String]

  private class Tags(tag: Tag) extends Table[String](tag, "TAGS") {
    def tagName = column[String]("TAG_NAME", O.PrimaryKey)
    override def * = tagName
  }
  private val tagTable = TableQuery[Tags]

  private class TagAliases(tag: Tag) extends Table[(String, String)](tag, "TAG_ALIASES") {
    def alias = column[String]("ALIAS")
    def aliasedTag = column[String]("ALIASED_TAG")
    override def * = (alias, aliasedTag)
    def aliasedTagForeignKey = foreignKey("ALIASED_TAG_FK", aliasedTag, tagTable)(_.tagName)
    def aliasPrimaryKey = primaryKey("ALIAS_PK", (alias, aliasedTag))
  }
  private val tagAliasesTable = TableQuery[TagAliases]

  private class TaggedFiles(tag: Tag) extends Table[(String, String)](tag, "TAGGED_FILES") {
    def path = column[String]("PATH")
    def tagName = column[String]("TAG_NAME")
    override def * = (path, tagName)
    def tagNameForeignKey = foreignKey("TAG_NAME_FK", tagName, tagTable)(_.tagName)
    def taggedFilePrimaryKey = primaryKey("TAGGED_FILE_PK", (path, tagName))
    def taggedFileIndex = index("TAGGED_FILE_IDX", (path, tagName), unique = true)
  }
  private val taggedFilesTable = TableQuery[TaggedFiles]

  private lazy val database = Database.forURL(s"jdbc:sqlite:$dbPath", driver = "org.sqlite.JDBC")

  if (!Files.exists(Paths.get(dbPath)))
    database withDynSession {
      (tagTable.ddl ++ tagAliasesTable.ddl ++ taggedFilesTable.ddl).create
    }

  def addTag(tagName: String): Unit = database withDynTransaction {
    tagTable += tagName
    _tags = _tags + tagName
  }

  def tags: Set[String] = {
    if (_tags.isEmpty) {
      _tags = database withDynTransaction {
        for (row <- tagTable) yield row.tagName
      }.list.toSet
    }

    _tags
  }

  def tagFile(pathToTag: String, tagsToApply: Seq[String]): Unit =
    database withDynTransaction {
      val rowsToAdd = tagsToApply.map((pathToTag, _))
      taggedFilesTable ++= rowsToAdd
    }

  def filesWithTag(tag: String): List[File] = {
    val pathsWithTag = database withDynTransaction {
      val rowsWithTag = taggedFilesTable filter (_.tagName is tag) // "is" is a Column[_]'s "=="
      rowsWithTag map (_.path)
    }.list

    pathsWithTag.map(new File(_))
  }

}