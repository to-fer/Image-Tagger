package gui.qt.image

import com.trolltech.qt.gui._
import java.io.File
import com.trolltech.qt.core.{Qt, QSize}
import com.trolltech.qt.core.Qt.{AlignmentFlag, AspectRatioMode, TransformationMode}
import gui.qt.gui.Label
import scala.concurrent.Future
import gui.qt.Application.executionContext
import scala.util.Success
import ImageFiles._

object Image {
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

class Image(val path: String) extends Label {
  require(new File(path).exists)

  // Alignment defaults to center
  alignment = AlignmentFlag.AlignCenter

  private val movie: Option[Future[QMovie]] =
    if (isAnimated(path)) Some(Movie.movie(path)) else None
  private var pixmap: Option[Future[QPixmap]] =
    if (movie.isEmpty) Some( Future { new QPixmap(path) }) else None

  if (movie.isDefined)
    movie.get.andThen {
      case Success(mov) => delegate.setMovie(mov)
    }
  else if (pixmap.isDefined)
    pixmap.get.andThen {
      case Success(pix) => delegate.setPixmap(pix)
    }
  else
    throw new IllegalArgumentException(path + " is a null image!")

  def this(p: String, w: Int, h: Int) = {
    this(p)
    width = w
    height = h

    if (movie.isDefined) {
      movie.get.andThen {
        case Success(mov) => Future {
          mov.setScaledSize(new QSize(w, h))
          delegate.setMovie(mov)
        }
      }
    }

    pixmap = if(pixmap.isDefined) {
      Some(pixmap.get.map {
        pix => {
          pix.scaled(w, h,
            AspectRatioMode.KeepAspectRatio,
            TransformationMode.SmoothTransformation)
        }
      }.andThen {
        case Success(pix) => delegate.setPixmap(pix)
      })
    } else None
  }

  override def alignment_=(alignmentFlags: Seq[AlignmentFlag]) = {
    super.alignment_=(alignmentFlags)
    delegate.setAlignment(alignmentFlags:_*)
  }

  override def alignment_=(alignmentFlag: AlignmentFlag) = {
    super.alignment_=(alignmentFlag)
    delegate.setAlignment(alignmentFlag)
  }

  override def dispose() = {
    if (pixmap.isDefined)
      pixmap.get.andThen {
        case Success(pix) => pix.dispose()
      }
    else {
      movie.get.andThen {
        case Success(mov) => {
          mov.stop()
          mov.dispose()
        }
      }
    }

    delegate.dispose()
  }

  override def toString = path
}