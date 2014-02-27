package qt.gui

import com.trolltech.qt.gui.QVBoxLayout

class VBoxWidget extends Widget with Parent {
  private val vBoxLayout = new QVBoxLayout(delegate)

  override protected def layout(w: Widget): Unit = {
    vBoxLayout.addWidget(w.delegate)
    vBoxLayout.setAlignment(w.delegate, w.alignment:_*)
  }
}
