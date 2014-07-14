package gui.qt.gui

import scala.collection.mutable.ArrayBuffer

trait Parent {
  private var _content = new ArrayBuffer[Widget]()

  def content = _content
  def content_=(w: Widget): Unit = {
    _content.clear()
    this += w
  }
  def content_=(sw: Seq[Widget]): Unit = {
    _content.clear()
    sw.foreach(+=)
  }

  def += (w: Widget): Unit =
    _content += w

  def -= (w: Widget): Unit = {
    _content -= w
  }
}
