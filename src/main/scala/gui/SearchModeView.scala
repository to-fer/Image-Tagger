package gui

import util.JavaFXExecutionContext.javaFxExecutionContext

import scala.concurrent.Future
import scalafx.scene.control.ScrollPane
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.GridPane
import scalafx.stage.Screen

class SearchModeView(imagesPerRow: Int = 5) {
  val imageViewScroll = new ScrollPane {
    hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
    vbarPolicy = ScrollPane.ScrollBarPolicy.AS_NEEDED
  }

  private val imageViewGrid = new GridPane {
    style = "-fx-background-color: black;"
  }
  imageViewScroll.content = imageViewGrid

  private val imageWidth = Screen.primary.bounds.width/imagesPerRow
  private val imageHeight = Screen.primary.bounds.height/imagesPerRow
  private var shownImages = Seq.empty[Image]

  def show(imagePaths: Seq[String]): Unit = Future {
    shownImages = imagePaths.map(path => {
      new Image(path, imageWidth, imageHeight, true, true, true) 
    })
    val imageViews = shownImages.map(new ImageView(_))
    imageViewGrid.content = imageViews
  }

  def hideImages(): Unit = {
    imageViewScroll.content = null
  }

}
