package qt.image

import com.trolltech.qt.gui.QMovie
import Image.fitSize

object Movie {
  def movie(path: String): QMovie = {
    val movie = new QMovie(path)
    movie.start()
    movie
  }

  def movie(path: String, width: Int, height: Int): QMovie = {
    val m = movie(path)

    val scaledSize = fitSize(path, width, height)
    m.setScaledSize(scaledSize)

    m
  }
}
