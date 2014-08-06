package event.mode

import java.io.File
import java.net.URI
import java.nio.file.{Files, Path, Paths}

import com.typesafe.scalalogging.slf4j.LazyLogging
import command._
import event.CommandHandler
import gui.TagModeView
import file.ImageFiles
import model.UntaggedImages
import tag.db.SlickTagDb
import util.JavaFXExecutionContext.javaFxExecutionContext

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class TagMode(untaggedImages: UntaggedImages,
              tagDb: SlickTagDb,
              imageSource: Path,
              imageDest: Path) extends Mode with LazyLogging {

  override val view: TagModeView = new TagModeView

  private var taggingAlmostDone = false

  override val commandHandler = new CommandHandler {
    override def handleCommand(cmd: String): CommandResult = cmd match {
        case SkipCommand(_) => {
          val result = Future {
            displayNextImageIfExists()
          }
          Await.result(result, Duration.Inf)
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
          val currentImageFile = new File(untaggedImages.currentURI)
          val result = Future {
            val res = displayNextImageIfExists()
            currentImageFile.delete()
            res
          }
          Await.result(result, Duration.Inf)
        }
        case QuitCommand(_) => ModeSwitch
        case "" => OK // Ignore empty inputs
        case TagCommand(tags) if (tags.forall(tagDb.tags.contains)) => {
          val filePathString = new URI(untaggedImages.currentURI).getPath()
          val imageFile = Paths.get(filePathString)
          val destFile = imageDest resolve imageFile.getFileName
          tagDb.tagFile(destFile, tags)

          val result = Future {
            val res = displayNextImageIfExists()
            Files.move(imageFile, destFile)
            res
          }

          Await.result(result, Duration.Inf)
        }
        case unknownCommand: String =>
          Error(s"Unknown command: $unknownCommand")
    }
  }

  def start(): CommandResult = {
    logger.info("Tag mode starting.")

    val imageFiles = ImageFiles.imageFilesIn(imageSource.toString)
    if (imageFiles != null && !imageFiles.isEmpty) {
      untaggedImages.untaggedImageFileURIs = imageFiles.map(_.toURI.toString)
      Future {
        view.root.style = "-fx-background-color: black;"
        view.cache(untaggedImages.currentURI)
        displayNextImageIfExists()
      }
      OK
    }
    else
      DisplayMessage("There are no images to tag.")
  }

  private def displayNextImageIfExists(): CommandResult = {
    if (!taggingAlmostDone) {
        if (!untaggedImages.hasNext)
          taggingAlmostDone = true
        else
          untaggedImages.nextImageURI()
      OK
    }
    else {
      view.root.style = "-fx-background-color: green;"
      DisplayMessage("Tagging Done :3")
    }
  }
}