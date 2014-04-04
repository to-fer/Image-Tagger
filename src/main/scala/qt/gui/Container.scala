package qt.gui

import com.trolltech.qt.gui.{QWidget, QVBoxLayout}
import com.trolltech.qt.core.Qt.{WidgetAttribute, AlignmentFlag}

class Container(childWidget: QWidget, alignmentList: Seq[AlignmentFlag]) extends Widget {
  def this(cWidget: Widget) = this(cWidget.delegate, cWidget.alignment)

  private val layout = new QVBoxLayout(delegate)
  alignment = alignmentList
  delegate.setStyleSheet("background: transparent;")
  delegate.setAttribute(WidgetAttribute.WA_TransparentForMouseEvents)
  layout.addWidget(childWidget, 0, alignmentList:_*)
}
