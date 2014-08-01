package gui.qt.image

import java.io.File

import scala.util.matching.Regex

object ImageFiles {

  lazy val animatedImageExtensions = "gif" :: "GIF" :: "apng" :: "APNG" :: Nil
  lazy val imageExtensions = "png" :: "PNG" :: "jpg" :: "JPG" :: animatedImageExtensions

  def isImage(path: String): Boolean = {
    val imageRegex = fileExtensionRegex(imageExtensions)
    imageRegex.findFirstIn(path).isDefined
  }

  def isAnimated(path: String): Boolean = {
    val animatedRegex = fileExtensionRegex(animatedImageExtensions)
    animatedRegex.findFirstIn(path).isDefined
  }

  private def fileExtensionRegex(extensions: List[String]): Regex = {
    val imageExtStr = extensions.mkString("|")
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