package gui

import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.{Pane, StackPane}
import scalafx.stage.Screen

class TagModeView extends ModeView {
  override val root: Pane = new StackPane {
    style = "-fx-background-color: black;"
  }

  def show(im: Image): Unit = {
    root.content = new ImageView {
      preserveRatio = true
      smooth = true
      fitWidth = Screen.primary.bounds.width
      fitHeight = Screen.primary.bounds.height
      image = im
    }
  }
}
