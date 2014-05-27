package qt.init

import com.trolltech.qt.gui.QApplication

/**
 * Convenient marker trait making use of DelayedInit to perform Qt initialization and execution.
 */
trait QtApp {
  var args = Array.empty[String]
  def start(): Unit

  def main(args: Array[String]) = {
    this.args = args
    QApplication.initialize(args)
    start()
    QApplication.exec()
  }
}
