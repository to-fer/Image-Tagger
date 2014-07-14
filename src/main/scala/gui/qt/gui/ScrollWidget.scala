package gui.qt.gui

import com.trolltech.qt.gui.{QFrame, QScrollArea, QWidget}

class ScrollWidget(override val delegate: QWidget = new QWidget) extends Widget with Parent {
  val scroll = new QScrollArea(delegate) {
    setFrameShape(QFrame.Shape.NoFrame)
  }

  override def width_=(w: Int) =
    scroll.setFixedWidth(w)

  override def height_=(h: Int) =
    scroll.setFixedHeight(h)

  override def +=(w: Widget) = {
    super.+=(w)
    scroll.setWidget(w.delegate)
  }
}