package qt.gui

import qt.util.QtDelegate
import com.trolltech.qt.gui.QWidget
import com.trolltech.qt.core.Qt.AlignmentFlag

class Widget(override val delegate: QWidget = new QWidget) extends QtDelegate[QWidget] {
  private var _alignment: Seq[AlignmentFlag] = List(AlignmentFlag.AlignCenter)

  def height = delegate.height
  def height_=(h: Int) = delegate.setFixedHeight(h)

  def width = delegate.width
  def width_=(w: Int) = delegate.setFixedWidth(w)

  def alignment = _alignment
  def alignment_=(alignmentFlags: Seq[AlignmentFlag]) =
    _alignment = alignmentFlags

  def show() = delegate.show()
  def hide() = delegate.hide()
}
