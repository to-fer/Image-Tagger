package gui.qt.gui

import com.trolltech.qt.gui.{QFrame, QLabel}

class Label(override val delegate: QLabel = new QLabel) extends Widget {
  def frameShape_=(fShape: QFrame.Shape): Unit = delegate.setFrameShape(fShape)
  def frameShape = delegate.frameShape

  def frameShadow_=(fShadow: QFrame.Shadow): Unit = delegate.setFrameShadow(fShadow)
  def frameShadow = delegate.frameShadow

  def text_=(t: String): Unit = delegate.setText(t)
  def text = delegate.text
}