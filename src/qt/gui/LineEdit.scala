package qt.gui

import com.trolltech.qt.gui.QLineEdit

class LineEdit(override val delegate: QLineEdit = new QLineEdit) extends Widget(delegate) {
  private var returnPressedFunction: Option[() => Unit] = None

  def text = delegate.text
  def text_=(text: String) =
    delegate.setText(text)

  def returnPressed_=(fn: () => Unit) = {
    if (!returnPressedFunction.isEmpty)
      delegate.returnPressed.disconnect(returnPressedFunction.get)
    returnPressedFunction = Some(fn)
    delegate.returnPressed.connect(fn, "apply()")
  }


}
