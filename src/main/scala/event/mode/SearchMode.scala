package event.mode

import java.nio.file.Path
import tag.db.SlickTagDb
import command._
import event.CommandHandler
import image.SearchResults
import command.Error

class SearchMode(imageSource: Path,
                 tagDb: SlickTagDb,
                 searchResults: SearchResults,
                 val imagesPerRow: Int,
                 override val name: String) extends Mode {

  override val commandHandler: CommandHandler = new CommandHandler {
    override def handleCommand(cmd: String): CommandResult = cmd match {
      case "" => OK // Ignore empty inputs
      case TagModeCommand(_) => ModeSwitch
      case tag: String => {
        if (tagDb.tags.contains(tag)) {
          val taggedImageFiles = tagDb.filesWithTag(tag)
          if (!taggedImageFiles.isEmpty) {
            searchResults.imagePaths = taggedImageFiles.map(_.toString)
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
  override def start(): CommandResult =
    OK
}
