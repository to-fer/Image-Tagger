package event.mode

import java.nio.file.{Files, Path}

import com.typesafe.scalalogging.slf4j.LazyLogging
import command._
import event.CommandHandler
import gui.TagModeView
import image.ImageFiles
import model.UntaggedImages
import tag.db.SlickTagDb

import scala.concurrent.Future
import util.JavaFXExecutionContext.javaFxExecutionContext

class TagMode(untaggedImages: UntaggedImages,
              tagDb: SlickTagDb,
              imageSource: Path,
              imageDest: Path) extends Mode with LazyLogging {

  override val view: TagModeView = new TagModeView

  override val commandHandler = new CommandHandler {
    override def handleCommand(cmd: String): CommandResult = cmd match {
        case SkipCommand(_) => {
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
          val currentImageFile = untaggedImages.currentImageFile
          val result = displayNextImageIfExists()
          currentImageFile.delete()
          result
        }
        case QuitCommand(_) => ModeSwitch
        case "" => OK // Ignore empty inputs
        case TagCommand(tags) if (tags.forall(tagDb.tags.contains)) => {
          val imageFile = untaggedImages.currentImageFile
          val destFile = imageDest resolve imageFile.toPath.getFileName
          tagDb.tagFile(destFile, tags)

          val result = displayNextImageIfExists()

          Files.move(imageFile.toPath, destFile)

          result
        }
        case unknownCommand: String =>
          Error(s"Unknown command: $unknownCommand")
    }
  }

  def start(): CommandResult = {
    logger.debug("Tag mode starting.")

    val imageFiles = ImageFiles.imageFilesIn(imageSource.toString)
    if (imageFiles != null && !imageFiles.isEmpty) {
      untaggedImages.untaggedImageFiles = imageFiles
      OK
    }
    else
      DisplayMessage("There are no images to tag.")
  }

  private def displayNextImageIfExists(): CommandResult = {
    untaggedImages.currentImage.cancel()
    val result = if (untaggedImages.hasNextImage()) OK else DisplayMessage("Tagging Done :3")
    if (untaggedImages.hasNextImage())
      Future { untaggedImages.nextImage() }
    result
  }
}