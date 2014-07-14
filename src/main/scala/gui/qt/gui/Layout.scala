package gui.qt.gui

trait Layout extends Parent {

  protected def layout(w: Widget): Unit

  override def +=(w: Widget): Unit = {
    super.+=(w)
    layout(w)
  }

}
