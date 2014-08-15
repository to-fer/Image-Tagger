package tag.db

import java.io.File
import scala.slick.driver.SQLiteDriver.simple._
import Database.dynamicSession
import java.nio.file.{Path, Files, Paths}
import com.typesafe.scalalogging.slf4j.LazyLogging

class TaggerDb(dbPath: String) extends LazyLogging {
  private var _tags: Set[String] = Set.empty
  private var _aliases: Map[String, String] = Map.empty

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

  if (!Files.exists(Paths.get(dbPath)))
    logger.info(s"Path to database $dbPath doesn't exist. Creating.")
  private lazy val database = Database.forURL(s"jdbc:sqlite:$dbPath", driver = "org.sqlite.JDBC")

  if (!Files.exists(Paths.get(dbPath))) {
    logger.info("Initializing database.")
    database withDynSession {
      (tagTable.ddl ++ tagAliasesTable.ddl ++ taggedFilesTable.ddl).create
    }
  }
  else {
    _tags = database withDynTransaction {
      for (row <- tagTable) yield row.tagName
    }.list.toSet

    _aliases = database withDynTransaction {
      for (alias <- tagAliasesTable) yield (alias.alias -> alias.aliasedTag)
    }.toMap
  }

  def addTag(tagName: String): Unit = database withDynTransaction {
    logger.info(s"Adding tag $tagName.")
    if (!contains(tagName)) {
      tagTable += tagName
      _tags = _tags + tagName
    }
    else
      throw new IllegalArgumentException("That tag already exists!")
  }

  def addTag(tagName: String, aliases: List[String]): Unit = database withDynTransaction {
    logger.info(s"Adding tag $tagName with aliases $aliases.")
    if (!contains(tagName) && aliases.forall(!contains(_))) {
      tagTable += tagName
      _tags = (_tags + tagName)
      addAlias(tagName, aliases)
    }
    else
      // TODO use a proper error message. Report problem to GUI
      throw new IllegalArgumentException("One of those tags already exists!")
  }

  def addAlias(tag: String, aliases: List[String]): Unit = database withDynTransaction {
    logger.info(s"Adding aliases $aliases to tag $tag.")
    if (contains(tag)) {
      if (aliases.forall(!contains(_))) {
        aliases.foreach(a => {
          _aliases = _aliases ++ Map(a -> tag)
        })
        val rowsToAdd = aliases.map((_, tag))
        tagAliasesTable ++= rowsToAdd
      }
      else
        throw new IllegalArgumentException("Alias already defined as an alias or tag!")
    }
    else
      throw new IllegalArgumentException("You may only add aliases to a tag that is already defined!")
  }

  def tagFile(tagPath: Path, maybeAliasedTags: Seq[String]): Unit = {
    val tags = maybeAliasedTags.map(convertAlias).distinct
    if (tags.forall(_tags.contains)) {
      logger.info(s"Tagging $tagPath with $tags.")
      database withDynTransaction {
        val rowsToAdd = tags.map((tagPath.toString, _))
        taggedFilesTable ++= rowsToAdd
      }
    }
    else
      throw new IllegalArgumentException("You cannot tag a file with a tag that doesn't exist!")
  }

  def tagFile(pathToTag: Path, tagToApply: String): Unit =
    tagFile(pathToTag, List(tagToApply))

  def filesWithTag(tag: String): List[File] = {
    val realTag = convertAlias(tag)
    val pathsWithTag = database withDynTransaction {
      val rowsWithTag = taggedFilesTable filter (_.tagName === realTag) // "===" is a Column[_]'s "=="
      rowsWithTag map (_.path)
    }.list

    pathsWithTag.map(new File(_))
  }

  private def convertAlias(tag: String): String = {
    if (_aliases.contains(tag))
      _aliases(tag)
    else
      tag
  }

  def contains(tag: String): Boolean =
    _aliases.contains(tag) || _tags.contains(tag)
}