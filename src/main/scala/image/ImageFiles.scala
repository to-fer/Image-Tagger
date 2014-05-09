package image

import java.io.File
import qt.image.Image.isImage

object ImageFiles {
  def imageFilesIn(path: String): Seq[File] = {
    val files = new File(path).listFiles
    if (files == null)
      return List[File]()

    val imageFiles = files filter (f => isImage(f.toString))
    imageFiles
  }
}
