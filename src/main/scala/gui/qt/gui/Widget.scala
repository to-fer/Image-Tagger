package gui.qt.gui

import gui.qt.util.QtDelegate
import com.trolltech.qt.gui.{QFrame, QPalette, QSizePolicy, QWidget}
import com.trolltech.qt.core.Qt.AlignmentFlag
import com.trolltech.qt.core.QPoint

class Widget(val delegate: QWidget = new QWidget) extends QtDelegate[QWidget] {
  private var _alignment: Seq[AlignmentFlag] = List(AlignmentFlag.AlignCenter)

  def height = delegate.height
  def height_=(h: Int): Unit = delegate.setFixedHeight(h)

  def width = delegate.width
  def width_=(w: Int): Unit = delegate.setFixedWidth(w)

  def alignment = _alignment
  def alignment_=(alignmentFlags: Seq[AlignmentFlag]) =
    _alignment = alignmentFlags
  def alignment_=(alignmentFlag: AlignmentFlag) =
    _alignment = Seq(alignmentFlag)

  def show(): Unit = delegate.show()
  def hide(): Unit = delegate.hide()

  def parent = delegate.parentWidget
  def parent_=(w: Widget): Unit = delegate.setParent(w.delegate)

  def move(x: Int, y: Int): Unit = delegate.move(x, y)

  def sizePolicy = delegate.sizePolicy
  def sizePolicy_=(sizePolicy: QSizePolicy): Unit = delegate.setSizePolicy(sizePolicy)

  def backgroundRole = delegate.backgroundRole
  def backgroundRole_=(colorRole: QPalette.ColorRole): Unit =
    delegate.setBackgroundRole(colorRole)

  def focus(): Unit = delegate.setFocus()

  def styleSheet_=(styleString: String): Unit = delegate.setStyleSheet(styleString)
  def styleSheet = delegate.styleSheet()

  def dispose(): Unit = delegate.dispose()
}
