package main

import tag.db.SlickTagDb
import java.nio.file.{Files, Paths}
import qt.init.QtApp
import gui.MainWindow
import event.mode._
import event.CommandListener
import qt.gui.{StackedWidget, Widget}
import qt.util.Screen
import image.UntaggedImages
import scala.Some

object Main extends QtApp {
  val commandListener = new CommandListener
  override val mainWindow = new MainWindow(commandListener)

  val (screenWidth, screenHeight) = Screen.size
  var modeViewMap = Map.empty[Mode, Widget]
  
  val imageSourceDir = Paths.get(System.getProperty("user.home"), "images")
  if (!Files.exists(imageSourceDir))
    Files.createDirectory(imageSourceDir)
  val imageDestDir = Paths.get(System.getProperty("user.home"), "images", "tagged")
  if (!Files.exists(imageDestDir))
    Files.createDirectory(imageDestDir)

  val tagDb = new SlickTagDb("tag-db.sqlite")
  val untaggedImages = new UntaggedImages
  val tagMode = new TagMode(untaggedImages, tagDb, imageSourceDir, imageDestDir, "tag")
  val tagModeView = new StackedWidget {
    width = screenWidth
    height = screenHeight
  }
  modeViewMap = Map(tagMode -> tagModeView) ++ modeViewMap

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
        mainWindow.viewMode = newModeView
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

  val modeSwitchHandler = new ModeSwitchHandler(modeSwitcher, modeViewMap.keys.toSeq)
  commandListener.modeSwitchHandler = modeSwitchHandler

  // TODO remove this, this is temporary. Should start in search mode.
  tagMode.start()
  modeSwitcher.switch(tagMode)
}
