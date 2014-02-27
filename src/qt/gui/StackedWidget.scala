package qt.gui

import com.trolltech.qt.gui.{QWidget, QStackedLayout}
import com.trolltech.qt.core.Qt

class StackedWidget extends Widget with Parent {
  private val stackedLayout = new QStackedLayout(delegate)
  stackedLayout.setStackingMode(QStackedLayout.StackingMode.StackAll)

  override protected def layout(w: Widget): Unit = {
    stackedLayout.addStackedWidget(w.delegate)
    stackedLayout.setAlignment(w.delegate, w.alignment:_*)
  }
}
