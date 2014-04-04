package qt.gui

import com.trolltech.qt.gui.{QWidget, QVBoxLayout}
import com.trolltech.qt.core.Qt.{WidgetAttribute, AlignmentFlag}

class Container(childWidget: QWidget, alignmentList: Seq[AlignmentFlag]) extends Widget {
  def this(cWidget: Widget) = this(cWidget.delegate, cWidget.alignment)

  private val layout = new QVBoxLayout(delegate)
  alignment = alignmentList

  // Required to make the Container transparent so it doesn't block other widgets from appearing.
  delegate.setStyleSheet("background: transparent;")
  // Required so the Container doesn't block mouse events intended for other Widgets.
  delegate.setAttribute(WidgetAttribute.WA_TransparentForMouseEvents)

  layout.addWidget(childWidget, 0, alignmentList:_*)
}
