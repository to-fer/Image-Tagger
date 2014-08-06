package gui

import util.JavaFXExecutionContext.javaFxExecutionContext

import scala.concurrent.Future
import scalafx.geometry.Pos
import scalafx.scene.control.ScrollPane
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.TilePane
import scalafx.stage.Screen

class SearchModeView(imagesPerRow: Int = 5) extends ModeView {
  override val root: ScrollPane = new ScrollPane {
    fitToWidth = true
    fitToHeight = true
    hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
    vbarPolicy = ScrollPane.ScrollBarPolicy.AS_NEEDED
  }

  private val imageViewGrid = new TilePane {
    style = "-fx-background-color: black;"
  }
  root.content = imageViewGrid

  private val imageWidth = Screen.primary.bounds.width/imagesPerRow
  private val imageHeight = Screen.primary.bounds.height/imagesPerRow
  private var shownImages = Seq.empty[Image]

  def show(imagePaths: Seq[String]): Unit = Future {
    shownImages = imagePaths.map(path => {
      new Image(path, true)
    })
    val imageViews = shownImages.map(i => new ImageView {
      alignmentInParent = Pos.CENTER
      preserveRatio = true
      smooth = true
      fitWidth = imageWidth
      fitHeight = imageHeight
      image = i
    })
    imageViewGrid.content = imageViews
  }

  // To avoid memory problems
  def hideImages(): Unit = {
    imageViewGrid.children.clear()
  }
}
