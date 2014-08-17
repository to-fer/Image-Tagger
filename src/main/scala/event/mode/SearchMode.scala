package event.mode

import java.nio.file.Path

import com.typesafe.scalalogging.slf4j.LazyLogging
import command._
import command.debug.{CreateDatabaseCommand, DeleteDatabaseCommand}
import event.CommandHandler
import gui.SearchModeView
import model.SearchResults
import tag.db.TaggerDb

import scala.concurrent.Future
import util.JavaFXExecutionContext.javaFxExecutionContext

class SearchMode(imageSource: Path,
                 tagDb: TaggerDb,
                 searchResults: SearchResults,
                 val imagesPerRow: Int)
                (implicit override val debugEnabled: Boolean)
  extends Mode with LazyLogging {

  override val view: SearchModeView = new SearchModeView

  override val commandHandler: CommandHandler = new CommandHandler {
    override def handleCommand(cmd: String): CommandResult = cmd match {
      case "" => OK // Ignore empty inputs
      case TagModeCommand(_) => {
        Future { view.hideImages() }
        ModeSwitch
      }
      case DeleteDatabaseCommand(_) if debugEnabled => {
        tagDb.delete()
        OK
      }
      case CreateDatabaseCommand(_) if debugEnabled => {
        tagDb.create()
        OK
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
        else
          Error(s"There are no images tagged with $tag.")
      }
      case _ => Error(s"Unknown command: $cmd")
    }
  }

  override def start(): CommandResult = {
    logger.info("Search mode starting.")
    OK
  }

}
