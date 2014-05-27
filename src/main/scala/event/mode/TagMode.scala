package event.mode

import event.CommandHandler
import command._
import java.nio.file.{Path, Files}
import command.Error
import tag.db.SlickTagDb
import qt.image.ImageFiles
import image.UntaggedImages
import com.typesafe.scalalogging.slf4j.LazyLogging

class TagMode(untaggedImages: UntaggedImages,
              tagDb: SlickTagDb,
              imageSource: Path,
              imageDest: Path,
              override val name: String) extends Mode with LazyLogging {

  override val commandHandler = new CommandHandler {
    override def handleCommand(cmd: String): CommandResult = cmd match {
        case SkipCommand(_) => {
          untaggedImages.currentImage.dispose()
          displayNextImageIfExists()
        }
        case AddTagCommand(tag) => {
          if (tag.contains(" "))
            Error("Tags cannot contain spaces.")
          else if (tagDb.tags.contains(tag))
            Error("That tag already exists.")
          else {
            tagDb.addTag(tag)
            OK
          }
        }
        case SearchModeCommand(_) => ModeSwitch
        case "delete" => {
          untaggedImages.currentImage.dispose()
          untaggedImages.currentImageFile.delete()
          displayNextImageIfExists()
        }
        case QuitCommand(_) => ModeSwitch
        case "" => OK // Ignore empty inputs
        case TagCommand(tags) if (tags.forall(tagDb.tags.contains)) => {
          val imageFile = untaggedImages.currentImageFile
          val destFile = imageDest resolve imageFile.toPath.getFileName
          tagDb.tagFile(destFile, tags)

          untaggedImages.currentImage.dispose()
          val result = displayNextImageIfExists()

          Files.move(imageFile.toPath, destFile)

          result
        }
        case unknownCommand: String =>
          Error(s"Unknown command: $unknownCommand")
    }
  }

  def start() = {
    logger.debug("Tag mode starting.")

    val imageFiles = ImageFiles.imageFilesIn(imageSource.toString)
    if (imageFiles != null && !imageFiles.isEmpty) {
      untaggedImages.untaggedImageFiles = imageFiles
      OK
    }
    else
      DisplayMessage("There are no images to tag.")
  }

  private def displayNextImageIfExists() = {
    if (untaggedImages.hasNextImage()) {
      untaggedImages.nextImage()
      OK
    }
    else
      DisplayMessage("Tagging Done :3")
  }

}