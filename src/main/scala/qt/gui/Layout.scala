package qt.gui

trait Layout extends Parent {
  private[gui] var containerMap = Map[Widget, Container]()

  protected def layout(w: Widget)

  override def +=(w: Widget) = {
    super.+=(w)

    val containedWidget = new Container(w)
    containerMap = containerMap + (w -> containedWidget)
    layout(containedWidget)
  }

}
