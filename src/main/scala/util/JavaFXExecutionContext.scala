package util

import java.util.concurrent.Executor
import javafx.application.Platform

import scala.concurrent.ExecutionContext

object JavaFXExecutionContext {
  implicit val javaFxExecutionContext: ExecutionContext = ExecutionContext.fromExecutor(new Executor {
    def execute(command: Runnable): Unit = Platform.runLater(command)
  })
}