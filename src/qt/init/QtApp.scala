package qt.init

import com.trolltech.qt.gui.QApplication

/**
 * Convenient marker trait making use of DelayedInit to perform Qt initialization and execution.
 */
trait QtApp extends App {
  override def delayedInit(body: => Unit) = {
    QApplication.initialize(new Array[String](0))
    body
    QApplication.exec()
  }
}
