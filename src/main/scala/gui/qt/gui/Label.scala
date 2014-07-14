package gui.qt.gui

import com.trolltech.qt.gui.{QFrame, QLabel}

class Label(override val delegate: QLabel = new QLabel) extends Widget {
  def frameShape_=(fShape: QFrame.Shape) = delegate.setFrameShape(fShape)
  def frameShape = delegate.frameShape

  def frameShadow_=(fShadow: QFrame.Shadow) = delegate.setFrameShadow(fShadow)
  def frameShadow = delegate.frameShadow

  def text_=(t: String) = delegate.setText(t)
  def text = delegate.text
}