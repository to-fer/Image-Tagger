package gui.qt

import scala.concurrent.ExecutionContext
import java.util.concurrent.Executor
import com.trolltech.qt.core.QCoreApplication

object Application {
  implicit val executionContext = ExecutionContext.fromExecutor(new Executor {
    def execute(command: Runnable): Unit = QCoreApplication.invokeLater(command)
  })
}
