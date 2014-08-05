package gui

import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.StackPane
import scalafx.stage.Screen

class TagModeView extends ModeView {
  private var imageViewCache = List.empty[ImageView]
  override val root: StackPane = new StackPane {
    style = "-fx-background-color: black;"
  }

  // Cache images early So the loading of the next image when in tag mode appears instantaneous.
  def cache(imagePath: String): Unit = {
    imageViewCache = new ImageView {
      preserveRatio = true
      smooth = true
      fitWidth = Screen.primary.bounds.width
      fitHeight = Screen.primary.bounds.height
      image = new Image(imagePath, true)
    } :: imageViewCache
  }

  def showNext(): Unit = {
    println("Cache: " + imageViewCache.length)
    // Remove tail so cache always contains the current and next ImageView
    root.content = imageViewCache.tail(0)
    imageViewCache = imageViewCache.dropRight(1)
  }
}
