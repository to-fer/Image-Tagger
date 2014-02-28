package qt.gui

import qt.util.{Container, QtDelegate}
import com.trolltech.qt.gui.{QFrame, QPalette, QSizePolicy, QWidget}
import com.trolltech.qt.core.Qt.AlignmentFlag
import com.trolltech.qt.core.QPoint

class Widget(override val delegate: QWidget = new QWidget) extends QtDelegate[QWidget] {
  private var _alignment: Seq[AlignmentFlag] = List(AlignmentFlag.AlignCenter)
  delegate.setAutoFillBackground(true)

  def height = delegate.height
  def height_=(h: Int) = delegate.setFixedHeight(h)

  def width = delegate.width
  def width_=(w: Int) = delegate.setFixedWidth(w)

  def alignment = _alignment
  def alignment_=(alignmentFlags: Seq[AlignmentFlag]) =
    _alignment = alignmentFlags

  def show() = delegate.show()
  def hide() = delegate.hide()

  def parent = delegate.parentWidget
  def parent_=(w: Widget) = delegate.setParent(w.delegate)

  def move(x: Int, y: Int) = delegate.move(x, y)

  def sizePolicy = delegate.sizePolicy
  def sizePolicy_=(sizePolicy: QSizePolicy) = delegate.setSizePolicy(sizePolicy)

  def backgroundRole = delegate.backgroundRole
  def backgroundRole_=(colorRole: QPalette.ColorRole) =
    delegate.setBackgroundRole(colorRole)
}
