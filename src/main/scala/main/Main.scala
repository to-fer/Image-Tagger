package main

import java.nio.file.{Files, Path, Paths}

import com.typesafe.scalalogging.slf4j.LazyLogging
import event.CommandListener
import event.mode._
import gui.{MainWindow, SearchModeView}
import model.{SearchResults, UntaggedImages}
import tag.db.SlickTagDb

import scalafx.application.JFXApp
import scalafx.scene.Parent
import scalafx.scene.image.ImageView
import scalafx.scene.layout.StackPane

object Main extends JFXApp with LazyLogging {

  val commandListener = new CommandListener
  val mainWindow = new MainWindow(commandListener)
  stage = mainWindow

  def createIfNotExists(path: Path): Unit =
    if (!Files.exists(path)) {
      logger.info(s"$path does not exist. Creating.")
      Files.createDirectory(path)
    }

  // TODO use config file instead of hard coding this
  val imageSourceDir = Paths.get(sys.env("HOME"), "images", "pony")
  createIfNotExists(imageSourceDir)
  val imageDestDir = imageSourceDir resolve "Tagged"
  createIfNotExists(imageDestDir)

  val tagDb = new SlickTagDb("tag-db.sqlite")
  val untaggedImages = new UntaggedImages
  val tagMode = new TagMode(untaggedImages, tagDb, imageSourceDir, imageDestDir)
  val tagModeView = new StackPane {
    style = "-fx-background-color: black;"
  }
  var modeViewMap = Map.empty[Mode, Parent]
  modeViewMap = Map(tagMode -> tagModeView) ++ modeViewMap

  val searchResults = new SearchResults
  val searchMode = new SearchMode(imageSourceDir, tagDb, searchResults, 5)
  val searchModeView = new SearchModeView
  modeViewMap = Map(searchMode -> searchModeView.imageViewScroll) ++ modeViewMap

  val activeMode = new ActiveMode
  val modeSwitcher = new ModeSwitcher(activeMode, modeViewMap)

  val modeSwitchObserver = () => {
    // TODO clean this up?
    val noModeSwitchErrorMsg = "Mode observer was notified, but no mode switch has occurred!"
    activeMode.currentMode match {
      case Some(newMode) => {
        commandListener.commandHandler = newMode.commandHandler
      }
      case None =>
        throw new Exception(noModeSwitchErrorMsg)
    }
    activeMode.currentModeView match {
      case Some(newModeView) =>
        mainWindow.currentModeView = newModeView
      case None =>
        throw new Exception(noModeSwitchErrorMsg)
    }
  }
  activeMode.addObserver(modeSwitchObserver)

  val showNextImageObserver = () => {
    untaggedImages.previousImage
    val current = untaggedImages.currentImage
    logger.debug(s"Showing next image $current")
    tagModeView.children.clear()
    tagModeView.children.add(new ImageView(current))
    ()
  }
  untaggedImages.addObserver(showNextImageObserver)

  val searchObserver = () => {
    logger.debug("Showing search results of size " + searchResults.imagePaths.length)
    searchModeView.show(searchResults.imagePaths)
  }
  searchResults.addObserver(searchObserver)

  val modeSwitchHandler = new ModeSwitchHandler(modeSwitcher, searchMode = searchMode, tagMode = tagMode)
  commandListener.modeSwitchHandler = modeSwitchHandler

  searchMode.start()
  modeSwitcher.switch(searchMode)
}
