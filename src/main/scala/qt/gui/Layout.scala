package qt.gui

trait Layout extends Parent {

  protected def layout(w: Widget)

  override def +=(w: Widget) = {
    super.+=(w)
    layout(w)
  }

}
