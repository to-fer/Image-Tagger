package gui.qt.image

import org.specs2.mutable.Specification
import java.nio.file._
import org.specs2.specification.{Step, Fragments}
import java.io.IOException
import java.nio.file.attribute.BasicFileAttributes

class ImageFilesTest extends ImageFileSpec {
  import imageDirSetup.testImageDir

  "ImageFiles" should {

    "imageFilesIn('directory path with no images')" in {
      val imageFiles = ImageFiles.imageFilesIn(testImageDir.toString)
      imageFiles must be empty
    }

    "imageFilesIn('directory path with images')" in {
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

    "isAnimated('path that denotes an animated image file')" in {
      val testFilePaths = List("test.gif", "test.apng")
      testFilePaths.forall(ImageFiles.isAnimated) mustEqual true
    }

    "isAnimated('path that doesn't denote an animated image file')" in {
      val testFilePaths = List("test.png", "test.jpg")
      testFilePaths.forall(!ImageFiles.isAnimated(_)) mustEqual true
    }

  }
}

object imageDirSetup {
  lazy val testImageDir = Paths.get("test-images")
  lazy val setupDbDir = {
    if (Files.exists(testImageDir))
      cleanImageDir
    Files.createDirectory(testImageDir)
  }
  lazy val cleanImageDir = {
    testImageDir.toFile.listFiles.foreach(_.delete)
    Files.delete(testImageDir)
  }
}

trait ImageFileSpec extends Specification {
  lazy val db = imageDirSetup
  override def map(fs: => Fragments) = Step(db.setupDbDir) ^ fs ^ Step(db.cleanImageDir)
}
