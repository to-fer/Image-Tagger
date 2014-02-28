package qt.gui

import com.trolltech.qt.gui.QFrame

class Frame extends Widget {
  val frame = new QFrame
  override val delegate = frame

  frame.setFrameShadow(QFrame.Shadow.Plain)
}