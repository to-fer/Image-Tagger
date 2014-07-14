package gui.qt.gui

import com.trolltech.qt.gui.QVBoxLayout

class VBoxWidget extends Widget with Layout {
  private val vBoxLayout = new QVBoxLayout(delegate)

  override protected def layout(w: Widget): Unit =
    vBoxLayout.addWidget(w.delegate, 0, w.alignment:_*)
}