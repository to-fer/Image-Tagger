package gui

import qt.gui.{Parent, Widget}

trait Layout extends Parent {
  protected def layout(w: Widget)

  override def content_=(w: Widget) = {
    super.content = w
    layout(w)
  }

  override def +=(w: Widget) = {
    super.+=(w)
    layout(w)
  }
}
