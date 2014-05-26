package gui

import qt.util.Screen
import qt.gui.{GridWidget, ScrollWidget}
import qt.image.Image
import com.trolltech.qt.gui.QFrame

class SearchModeView(imagesPerRow: Int = 5) {
  private val (screenWidth, screenHeight) = Screen.size
  val viewWidget = new ScrollWidget {
    width = screenWidth
    height = screenHeight
  }
  private val imageWidth = screenWidth/imagesPerRow
  private val imageHeight = screenHeight/imagesPerRow
  private var shownImages = Seq.empty[Image]

  def show(imagePaths: Seq[String]): Unit = {
    shownImages = imagePaths.map(path => {
      new Image(path, imageWidth, imageHeight)   {
        frameShape = QFrame.Shape.Box
      }
    })
    val imageGridWidget = new GridWidget
    shownImages.foreach(imageGridWidget +=)
    viewWidget.content = imageGridWidget
  }

  def hideImages(): Unit = {
    shownImages.foreach(_.dispose())
    viewWidget.content(0).dispose()
  }

}
