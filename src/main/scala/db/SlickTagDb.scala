import scala.slick.driver.SQLiteDriver.simple._

object SlickTagDb {
  class Tags(tag: Tag) extends Table[String](tag, "TAGS") {
    def tagName = column[String]("TAG_NAME", O.PrimaryKey)
    override def * = tagName
  }
  val tagTable = TableQuery[Tags]

  class TagAliases(tag: Tag) extends Table[(String, String)](tag, "TAG_ALIASES") {
    def alias = column[String]("ALIAS")
    def aliasedTag = column[String]("ALIASED_TAG")
    def aliasedTagForeignKey = foreignKey("ALIASED_TAG_FK", aliasedTag, tagTable)(_.tagName)
    def aliasPrimaryKey = primaryKey("ALIAS_PK", (alias, aliasedTag))
    override def * = (alias, aliasedTag)
  }
  val tagAliasesTable = TableQuery[TagAliases]

  class TaggedFiles(tag: Tag) extends Table[(String, String)](tag, "TAGGED_FILES") {
    def path = column[String]("PATH")
    def tagName = column[String]("TAG_NAME")
    def tagNameForeignKey = foreignKey("TAG_NAME_FK", tagName, tagTable)(_.tagName)
    def taggedFilePrimaryKey = primaryKey("TAGGED_FILE_PK", (path, tag))
    def taggedFileIndex = index("TAGGED_FILE_IDX", (path, tag), unique = true)
    def * = (path, tagName)
  }
  val taggedFilesTable = TableQuery[TaggedFiles]
}