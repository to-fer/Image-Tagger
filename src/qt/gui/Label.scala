package qt.gui

import com.trolltech.qt.gui.{QFrame, QLabel}

class Label(override val delegate: QLabel = new QLabel) extends Widget {
  def frameShape_=(fShape: QFrame.Shape) = delegate.setFrameShape(fShape)
  def frameShadow_=(fShadow: QFrame.Shadow) = delegate.setFrameShadow(fShadow)
}