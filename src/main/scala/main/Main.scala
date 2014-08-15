package main

import java.nio.file.{Files, Path, Paths}

import com.typesafe.scalalogging.slf4j.LazyLogging
import event.CommandListener
import event.mode._
import file.ConfigFileParser
import gui.{MainWindow, MessageDisplay}
import model._
import tag.db.TaggerDb

import scala.concurrent.Future
import util.JavaFXExecutionContext.javaFxExecutionContext
import scalafx.application.JFXApp

object Main extends JFXApp with LazyLogging {

  val messageModel = new MessageModel
  val commandListener = new CommandListener(messageModel)
  val fieldWidth = 400
  val messageDisplay = new MessageDisplay(fieldWidth)
  val mainWindow = new MainWindow(commandListener, messageDisplay, inputFieldWidth = fieldWidth)
  stage = mainWindow

  def createIfNotExists(path: Path): Unit =
    if (!Files.exists(path)) {
      logger.info(s"$path does not exist. Creating.")
      Files.createDirectory(path)
    }

  val osName = sys.props("os.name")
  val configFile =
    if (osName.contains("Linux"))
      Paths.get(sys.env("HOME"), ".config", "image_tagger", "config")
    else
      Paths.get(sys.env("HOME"), ".image_tagger", "config.txt")

  val configMap = ConfigFileParser.parse(configFile)
  // TODO properly deal with Nones
  val imageSourceDir = configMap("source").get
  val imageDestDir = configMap("dest").get
  createIfNotExists(imageDestDir)

  val tagDb = new TaggerDb(configFile.getParent resolve "tag-db.sqlite")
  val untaggedImages = new UntaggedImages
  val tagMode = new TagMode(untaggedImages, tagDb, imageSourceDir, imageDestDir)

  val searchResults = new SearchResults
  val searchMode = new SearchMode(imageSourceDir, tagDb, searchResults, 5)

  val activeMode = new ActiveMode
  val modeSwitcher = new ModeSwitcher(activeMode)

  val messageObserver = () => { Future {
      val message = messageModel.message

      message match {
        case NormalMessage(msg) => messageDisplay.displayMessage(msg)
        case ErrorMessage(msg) => messageDisplay.displayMessage(msg)
      }

    }
    ()
  }
  messageModel.addObserver(messageObserver)

  val modeSwitchObserver = () => {
    val noModeSwitchErrorMsg = "Mode observer was notified, but no mode switch has occurred!"
    activeMode.currentMode match {
      case Some(newMode) => {
        commandListener.commandHandler = newMode.commandHandler
        mainWindow.currentModeView = newMode.view.root
      }
      case None => {
        class NoModeSwitchException(errorMessage: String) extends Exception(errorMessage)
        throw new NoModeSwitchException(noModeSwitchErrorMsg)
      }
    }
  }
  activeMode.addObserver(modeSwitchObserver)

  var taggingAlmostDone = false
  val showNextImageObserver = () => {
    if (!taggingAlmostDone) {
      val current = untaggedImages.nextURI
      tagMode.view.cache(current)
      logger.info(s"Next image: $current")

      if (!untaggedImages.hasNext)
        taggingAlmostDone = true
    }
    tagMode.view.showNext()
    ()
  }
  untaggedImages.addObserver(showNextImageObserver)

  val searchObserver = () => {
    logger.debug("Showing search results of size " + searchResults.imagePaths.length)
    searchMode.view.show(searchResults.imagePaths)
  }
  searchResults.addObserver(searchObserver)

  val modeSwitchHandler = new ModeSwitchHandler(modeSwitcher, searchMode = searchMode, tagMode = tagMode)
  commandListener.modeSwitchHandler = modeSwitchHandler

  searchMode.start()
  modeSwitcher.switch(searchMode)
}
