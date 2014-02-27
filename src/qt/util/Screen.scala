package qt.util

import com.trolltech.qt.gui.QDesktopWidget

object Screen {
  def size: (Int, Int) = {
    val desktopWidget = new QDesktopWidget
    val screenRect = desktopWidget.screenGeometry
    (screenRect.width, screenRect.height)
  }
}
