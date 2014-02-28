package qt.image

import com.trolltech.qt.gui._
import java.io.File
import com.trolltech.qt.core.QSize
import qt.gui.Widget

object Image {
  def imageFilesIn(path: String): Seq[File] = {
    val files = new File(path).listFiles
    if (files == null)
      return List[File]()

    val imageFiles = files filter (f => isImage(f.toString))
    imageFiles
  }

  def isImage(path: String) = {
    val imageRegex = "(\\.(png|PNG|jpg|JPG|jpeg|JPEG|gif|GIF|apng|APNG))$".r
    imageRegex.findFirstIn(path).isDefined
  }

  def isAnimated(path: String) = {
    val animatedRegex = "(\\.(gif|GIF|apng|APNG))$".r
    animatedRegex.findFirstIn(path).isDefined
  }

  def makeLabel(imagePath: String, width: Int, height: Int) = {
    val label = new QLabel
    if (isAnimated(imagePath))
      label.setMovie(Movie.movie(imagePath, width, height))
    else
      label.setPixmap(Pixmap.pixmap(imagePath, width, height))
    
    label
  }

  def fitSize(path: String, fitWidth: Int, fitHeight: Int) = {
    val imageSize = size(path)

    val aspectRatio = imageSize.width/imageSize.height.toDouble
    val newSize =
      if (imageSize.width > fitWidth || imageSize.height > fitHeight)
        if (imageSize.width > imageSize.height) {
          val newWidth = fitWidth
          val newHeight = (newWidth/aspectRatio).toInt
          new QSize(newWidth, newHeight)
        }
        else {
          val newHeight = fitHeight
          val newWidth = (aspectRatio * newHeight).toInt
          new QSize(newWidth, newHeight)
        }
      else
        imageSize
    newSize
  }
  
  def size(path: String) = {
    val pixmap = new QPixmap(path)
    // TODO does this do anything?
    QPixmapCache.remove(pixmap.cacheKey.toString)

    val pixmapSize = pixmap.size

    pixmap.dispose()

    pixmapSize
  }
}

class Image(val path: String) extends Widget {
  require(new File(path).exists)

  val movie: Option[QMovie] = if (Image.isAnimated(path)) Some(Movie.movie(path)) else None
  private var _pixmap: Option[QPixmap] = if (movie.isEmpty) Some(new QPixmap(path)) else None
  def pixmap = _pixmap

  def this(p: String, w: Int, h: Int) = {
    this(p)
    width = w
    height = h
    if (movie.isDefined) movie.get.setScaledSize(new QSize(w, h))
    _pixmap = if(pixmap.isDefined) Some(pixmap.get.scaled(w, h)) else None
  }

  val labelDelegate = new QLabel
  override val delegate = labelDelegate

  if (!movie.isEmpty)
    labelDelegate.setMovie(movie.get)
  else if (!pixmap.isEmpty)
    labelDelegate.setPixmap(pixmap.get)
  else
    throw new IllegalArgumentException(path + " is a null image!")

  def dispose() = {
    if (movie.isEmpty)
      pixmap.get.dispose()
    else
      movie.get.dispose()
    labelDelegate.dispose()
  }
}