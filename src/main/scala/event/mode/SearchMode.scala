package event.mode

import java.nio.file.Path

import com.typesafe.scalalogging.slf4j.LazyLogging
import command._
import event.CommandHandler
import model.SearchResults
import tag.db.SlickTagDb

class SearchMode(imageSource: Path,
                 tagDb: SlickTagDb,
                 searchResults: SearchResults,
                 val imagesPerRow: Int) extends Mode with LazyLogging {

  override val commandHandler: CommandHandler = new CommandHandler {
    override def handleCommand(cmd: String): CommandResult = cmd match {
      case "" => OK // Ignore empty inputs
      case TagModeCommand(_) => ModeSwitch
      case tag: String => {
        if (tagDb.tags.contains(tag)) {
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
        else
          Error(s"$tag is not a known tag.")
      }
    }
  }

  override def start(): CommandResult = {
    logger.debug("Search mode starting.")
    OK
  }

}
