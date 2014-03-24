package qt.gui

import com.trolltech.qt.gui.QStackedLayout
import com.trolltech.qt.gui.QStackedLayout.StackingMode
import scala.collection.mutable
import com.trolltech.qt.core.Qt.AlignmentFlag

class StackedWidget extends Widget with Layout {
  private val stackedLayout = new QStackedLayout(delegate)

  stackingMode = StackingMode.StackAll

  def stackingMode_=(stackingMode: StackingMode) =
    stackedLayout.setStackingMode(stackingMode)
  def stackingMode = stackedLayout.stackingMode

  def currentWidget_=(w: Widget) = {
    if (stackingMode != StackingMode.StackOne)
      throw new UnsupportedOperationException("StackedWidget is not in StackOne stacking mode.")
    stackedLayout.setCurrentWidget(w.delegate)
  }
  def currentWidget =
    stackedLayout.currentWidget

  // TODO Container causes problem for currentWidget_=
  override protected def layout(w: Widget): Unit = {
    stackedLayout.addStackedWidget(w.delegate)
  }
}
