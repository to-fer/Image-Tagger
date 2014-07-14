package gui.qt.init

import com.trolltech.qt.gui.QApplication

/**
 * Convenience trait used to initialize Qt.
 */
trait QtApp {
  var args = Array.empty[String]
  def start(): Unit

  def main(args: Array[String]): Unit = {
    this.args = args
    QApplication.initialize(args)
    start()
    QApplication.exec()
  }
}
