package gui.qt.image

import java.nio.file._

import file.ImageFiles
import org.specs2._

class ImageFilesTest extends mutable.Specification {

  "ImageFiles" should {
    "find image files" in {
      "directory containing NO image files" in new ImageFileDir("directory-containing-NO-image-files") {
        val imageFiles = ImageFiles.imageFilesIn(testImageDir.toString)
        imageFiles must be empty
      }

      "directory containing image files" in new ImageFileDir("directory-containing-image-files") {
        // Filter out all uppercase extensions because they aren't differentiated from
        // a file with the same name but with a lowercase extension when they're created.
        val expectedImagePaths =
          ImageFiles.imageExtensions.filter(str => str.charAt(str.length - 1).isLower).map(ext => testImageDir.resolve(s"$ext-test.$ext"))
        val testFilePaths = expectedImagePaths ++ Seq("not-an-image.txt", "not-an-image.doc").map(testImageDir.resolve)
        val testFiles = testFilePaths.map(_.toFile)
        testFiles.foreach(_.createNewFile())

        val imageFiles = ImageFiles.imageFilesIn(testImageDir.toString)
        val expectedImageFiles = expectedImagePaths.map(_.toFile)
        expectedImageFiles.sorted.sameElements(imageFiles.sorted) mustEqual true
      }
    }

    "identify image file types" in {
      "animated image files" in {
        val testFilePaths = List("test.gif", "test.apng")
        testFilePaths.forall(ImageFiles.isAnimated) mustEqual true
      }

      "still image files" in {
        val testFilePaths = List("test.png", "test.jpg")
        testFilePaths.forall(!ImageFiles.isAnimated(_)) mustEqual true
      }
    }
  }
}

class ImageFileDir(testImageDirString: String) extends mutable.BeforeAfter {
  lazy val testImageDir = Paths.get(testImageDirString)

  def before = {
    if (Files.exists(testImageDir))
      deleteImageDir()

    Files.createDirectory(testImageDir)
  }

  def after = deleteImageDir()

  private def deleteImageDir() = {
    val testImageDirContents = testImageDir.toFile.listFiles
    if (testImageDirContents != null)
      testImageDirContents.foreach(_.delete())

    Files.delete(testImageDir)
  }
}

