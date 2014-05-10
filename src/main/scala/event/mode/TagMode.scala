package event.mode

import event.CommandHandler
import command._
import java.nio.file.{Path, Files}
import command.Error
import tag.db.SlickTagDb
import image.{UntaggedImages, ImageFiles}

class TagMode(untaggedImages: UntaggedImages,
              tagDb: SlickTagDb,
              imageSource: Path,
              imageDest: Path,
              override val name: String) extends Mode {

  override val commandHandler = new CommandHandler {
    override def handleCommand(cmd: String): CommandResult = {

      cmd match {
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
  }

  def start() = {
    val imageFiles = ImageFiles.imageFilesIn(imageSource.toString)
    if (imageFiles != null && !imageFiles.isEmpty) {
      untaggedImages.untaggedImageFiles = imageFiles
      displayNextImageIfExists()
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