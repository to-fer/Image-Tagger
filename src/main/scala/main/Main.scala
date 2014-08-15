package main

import java.nio.file.{Files, Path, Paths}

import com.typesafe.scalalogging.slf4j.LazyLogging
import event.CommandListener
import event.mode._
import file.ConfigFileParser
import gui.MainWindow
import model.{ActiveMode, SearchResults, UntaggedImages}
import tag.db.TaggerDb

import scalafx.application.JFXApp

object Main extends JFXApp with LazyLogging {

  val commandListener = new CommandListener
  val mainWindow = new MainWindow(commandListener)
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

  val (imageSourceDirOption, imageDestDirOption) = ConfigFileParser.parse(configFile)
  // TODO properly deal with Nones
  val imageSourceDir = imageSourceDirOption.get
  val imageDestDir = imageDestDirOption.get
  createIfNotExists(imageDestDir)

  val tagDb = new TaggerDb("tag-db.sqlite")
  val untaggedImages = new UntaggedImages
  val tagMode = new TagMode(untaggedImages, tagDb, imageSourceDir, imageDestDir)

  val searchResults = new SearchResults
  val searchMode = new SearchMode(imageSourceDir, tagDb, searchResults, 5)

  val activeMode = new ActiveMode
  val modeSwitcher = new ModeSwitcher(activeMode)

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
