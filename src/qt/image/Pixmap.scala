package qt.image

import com.trolltech.qt.gui.QPixmap
import com.trolltech.qt.core.Qt
import Image.fitSize

object Pixmap {
  def pixmap(imagePath: String, width: Int, height: Int) = {
    val newSize = fitSize(imagePath, width, height)
    new QPixmap(imagePath).scaled(newSize, Qt.AspectRatioMode.KeepAspectRatio, Qt.TransformationMode.SmoothTransformation)
  }
}
