package gui

import com.trolltech.qt.gui.{QStackedLayout, QWidget, QLayout}

class Layout(private val delegate: QLayout, widgetList: List[QWidget] = Nil) {
  def +(w: QWidget): Layout = {
    if (delegate.isInstanceOf[QStackedLayout])
      delegate.asInstanceOf[QStackedLayout].addStackedWidget(w)
    else
      delegate.addWidget(w)
    new Layout(delegate, w :: widgetList)
  }
}
