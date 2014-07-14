package gui.qt.image

import com.trolltech.qt.gui.QPixmap
import com.trolltech.qt.core.Qt
import Image.fitSize
import scala.concurrent.Future
import gui.qt.Application.executionContext
import scala.util.Success

object Pixmap {
  def pixmap(imagePath: String, width: Int, height: Int): Future[QPixmap] = {
    val newSize = fitSize(imagePath, width, height)
    pixmap(imagePath).andThen {
      case Success(pix) => Future {
        pix.scaled(newSize,
          Qt.AspectRatioMode.KeepAspectRatio,
          Qt.TransformationMode.SmoothTransformation)
      }
    }
  }

  def pixmap(imagePath: String): Future[QPixmap] = Future {
    new QPixmap(imagePath)
  }
}
