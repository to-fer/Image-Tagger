package qt.gui

import com.trolltech.qt.gui.QVBoxLayout

class Container(childWidget: Widget) extends Widget {
  val layout = new QVBoxLayout(delegate)
  layout.addWidget(childWidget.delegate, 0, childWidget.alignment:_*)
}