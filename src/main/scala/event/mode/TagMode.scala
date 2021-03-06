package event.mode

import java.io.File
import java.net.URI
import java.nio.file.{Files, Path, Paths}

import com.typesafe.scalalogging.slf4j.LazyLogging
import command._
import file.ImageFiles
import gui.TagModeView
import model.UntaggedImages
import tag.db.TaggerDb
import util.JavaFXExecutionContext.javaFxExecutionContext

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class TagMode(tagDb: TaggerDb,
              untaggedImages: UntaggedImages,
              imageSource: Path,
              imageDest: Path,
              debugEnabled: Boolean = false)
  extends Mode(tagDb, view = new TagModeView, debugEnabled) with LazyLogging {

  override val view: TagModeView = new TagModeView

  private var taggingAlmostDone = false

  override protected val modeHandler: CommandHandler = {
    case SkipCommand(_) => {
      val result = Future {
        displayNextImageIfExists()
      }
      Await.result(result, Duration.Inf)
    }
    case AddTagCommand(tag) => {
      if (tag.contains(" "))
        Error("Tags cannot contain spaces.")
      else if (tagDb.contains(tag))
        Error("That tag already exists.")
      else {
        tagDb.addTag(tag)
        OK
      }
    }
    case AddAliasesCommand(tag, aliases) => {
      if (aliases.find(_.contains(" ")).isDefined)
        Error("Tags cannot contain spaces.")
      else if (aliases.forall(tagDb.contains(_)))
        Error("Alias already defined.")
      else {
        tagDb.addAlias(tag, aliases)
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
    case TagCommand(tags) if (tags.forall(tagDb.contains(_))) => {
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
  }

  def start(): CommandResult = {
    logger.info("Tag mode starting.")

    val imageFiles = ImageFiles.imageFilesIn(imageSource.toString)
    if (imageFiles != null && !imageFiles.isEmpty) {
      untaggedImages.untaggedImageFileURIs = imageFiles.map(_.toURI.toString)
      Future {
        view.root.style = "-fx-background-color: black;"
        view.cache(untaggedImages.nextURI)
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