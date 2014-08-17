package event.mode

import java.nio.file.Path

import com.typesafe.scalalogging.slf4j.LazyLogging
import command._
import gui.SearchModeView
import model.SearchResults
import tag.db.TaggerDb
import util.JavaFXExecutionContext.javaFxExecutionContext

import scala.concurrent.Future

class SearchMode(tagDb: TaggerDb,
                 imageSource: Path,
                 searchResults: SearchResults,
                 val imagesPerRow: Int,
                 debugEnabled: Boolean = false)
  extends Mode(tagDb, view = new SearchModeView, debugEnabled) with LazyLogging {

  override val view: SearchModeView = new SearchModeView

  override protected val modeHandler: CommandHandler = {
    case TagModeCommand(_) => {
      Future { view.hideImages() }
      ModeSwitch
    }
    case tag: String if tagDb.contains(tag) => {
      logger.info(s"Search query: $tag")
      val taggedImageFiles = tagDb.filesWithTag(tag)
      if (!taggedImageFiles.isEmpty) {
        val existingTaggedImageFiles = taggedImageFiles.filter(_.exists())
        val notExistingTaggedImageFiles = taggedImageFiles.filter(!_.exists())
        if (!notExistingTaggedImageFiles.isEmpty)
          logger.warn(s"Search results contain non-existent files: $notExistingTaggedImageFiles.")

        searchResults.imagePaths = existingTaggedImageFiles.map(_.toURI.toString)
        OK
      }
      else Error(s"There are no files tagged with '$tag'.")
    }
  }

  override def start(): CommandResult = {
    logger.info("Search mode starting.")
    OK
  }

}
