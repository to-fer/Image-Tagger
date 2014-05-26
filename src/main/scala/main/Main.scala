package main

import tag.db.SlickTagDb
import java.nio.file.{Files, Paths}
import qt.init.QtApp
import gui.{SearchModeView, MainWindow}
import event.mode._
import event.CommandListener
import qt.gui.{StackedWidget, Widget}
import qt.util.Screen
import scala.Some
import image.{SearchResults, UntaggedImages}

object Main extends QtApp {
  val commandListener = new CommandListener
  override val mainWindow = new MainWindow(commandListener)
  
  val imageSourceDir = Paths.get(System.getProperty("user.home"), "images")
  if (!Files.exists(imageSourceDir))
    Files.createDirectory(imageSourceDir)
  val imageDestDir = Paths.get(System.getProperty("user.home"), "images", "tagged")
  if (!Files.exists(imageDestDir))
    Files.createDirectory(imageDestDir)

  val tagDb = new SlickTagDb("tag-db.sqlite")
  val untaggedImages = new UntaggedImages
  val tagMode = new TagMode(untaggedImages, tagDb, imageSourceDir, imageDestDir, "tag")
  val (screenWidth, screenHeight) = Screen.size
  val tagModeView = new StackedWidget {
    width = screenWidth
    height = screenHeight
  }
  var modeViewMap = Map.empty[Mode, Widget]
  modeViewMap = Map(tagMode -> tagModeView) ++ modeViewMap

  val searchResults = new SearchResults
  val searchMode = new SearchMode(imageSourceDir, tagDb, searchResults, 5, "search")
  val searchModeView = new SearchModeView
  modeViewMap = Map(searchMode -> searchModeView.viewWidget) ++ modeViewMap

  val activeMode = new ActiveMode
  val modeSwitcher = new ModeSwitcher(activeMode, modeViewMap)
  
  val modeSwitchObserver = () => {
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
    untaggedImages.previousImage match {
      case Some(image) =>
        tagModeView -= image
      case None =>
    }
    tagModeView += untaggedImages.currentImage
  }
  untaggedImages.addObserver(showNextImageObserver)

  val searchObserver = () =>
    searchModeView.show(searchResults.imagePaths)
  searchResults.addObserver(searchObserver)

  val modeSwitchHandler = new ModeSwitchHandler(modeSwitcher, searchMode = searchMode, tagMode = tagMode)
  commandListener.modeSwitchHandler = modeSwitchHandler

  searchMode.start()
  modeSwitcher.switch(searchMode)
}
