package gui.qt.image

import com.trolltech.qt.gui.QMovie
import Image.fitSize
import scala.concurrent.Future
import gui.qt.Application.executionContext
import scala.util.Success

object Movie {
  def movie(path: String): Future[QMovie] = Future {
    val movie = new QMovie(path)
    movie.start()
    movie
  }

  def movie(path: String, width: Int, height: Int): Future[QMovie] = {
    val m = movie(path)
    m.andThen {
      case Success(mov) => {
        val scaledSize = fitSize(path, width, height)
        mov.setScaledSize(scaledSize)
      }
    }
  }
}
