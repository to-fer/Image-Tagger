package qt.gui

import com.trolltech.qt.gui.QStackedLayout
import com.trolltech.qt.gui.QStackedLayout.StackingMode

class StackedWidget extends Widget with Layout {
  private val stackedLayout = new QStackedLayout(delegate)

  stackingMode = StackingMode.StackAll

  def stackingMode_=(stackingMode: StackingMode) =
    stackedLayout.setStackingMode(stackingMode)

  def stackingMode = stackedLayout.stackingMode

  def currentWidget_=(w: Widget) = {
    stackedLayout.setCurrentWidget(w.delegate)
  }

  def currentWidget =
    stackedLayout.currentWidget

  override protected def layout(w: Widget): Unit = {
    stackedLayout.addStackedWidget(w.delegate)
  }
}
