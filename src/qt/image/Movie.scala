package qt.image

import com.trolltech.qt.gui.QMovie
import Image.fitSize

object Movie {
  def movie(path: String, width: Int, height: Int) = {
    val movie = new QMovie(path)

    val scaledSize = fitSize(path, width, height)
    movie.setScaledSize(scaledSize)
    movie.start()

    movie
  }
}
