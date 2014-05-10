package qt.image

import java.io.File

object ImageFiles {

  lazy val animatedImageExtentions = "gif" :: "GIF" :: "apng" :: "APNG" :: Nil
  lazy val imageExtentions = "png" :: "PNG" :: "gif" :: "GIF" :: "jpg" :: "JPG" :: animatedImageExtentions

  def isImage(path: String) = {
    val imageRegex = fileExtentionRegex(imageExtentions)
    imageRegex.findFirstIn(path).isDefined
  }

  def isAnimated(path: String) = {
    val animatedRegex = fileExtentionRegex(animatedImageExtentions)
    animatedRegex.findFirstIn(path).isDefined
  }

  private def fileExtentionRegex(extentions: List[String]) = {
    val imageExtStr = extentions.mkString("|")
    ("(\\.(" + imageExtStr + "))$").r
  }

  def imageFilesIn(path: String): Seq[File] = {
    val files = new File(path).listFiles
    if (files == null)
      return List.empty[File]

    val imageFiles = files.filter(f => isImage(f.getName))
    imageFiles
  }
}
