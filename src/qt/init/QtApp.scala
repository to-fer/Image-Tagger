package qt.init

import com.trolltech.qt.gui.QApplication
import qt.gui.Window

/**
 * Convenient marker trait making use of DelayedInit to perform Qt initialization and execution.
 */
trait QtApp extends App {
  val mainWindow: Window

  override def delayedInit(body: => Unit) = {
    QApplication.initialize(new Array[String](0))
    body
    mainWindow.show()
    QApplication.exec()
  }
}
