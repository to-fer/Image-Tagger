package qt.gui

import com.trolltech.qt.gui.QStackedLayout
import qt.util.Container

class StackedWidget extends Widget with Parent {
  private val stackedLayout = new QStackedLayout(delegate)
  stackedLayout.setStackingMode(QStackedLayout.StackingMode.StackAll)

  override protected def layout(w: Widget): Unit = {
    stackedLayout.addStackedWidget(w.delegate)
    stackedLayout.setAlignment(w.delegate, w.alignment:_*)
  }
}
