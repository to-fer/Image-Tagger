package qt.util

import com.trolltech.qt.core.Qt.AlignmentFlag
import com.trolltech.qt.gui.{QVBoxLayout, QWidget}

class Container(child: QWidget, align: AlignmentFlag*) {
  val delegate = new QWidget
  new QVBoxLayout(delegate) {
    addWidget(child, 0, align:_*)
  }
}