package qt.image

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
      val testFilePaths = ImageFiles.imageExtentions.map(ext => testImageDir.resolve(s"$ext-test.$ext"))
      val testFiles = testFilePaths.map(_.toFile)
      testFiles.foreach(_.createNewFile())

      val imageFiles = ImageFiles.imageFilesIn(testImageDir.toString)
      val containsAll = imageFiles.forall(testFiles.contains)
      containsAll mustEqual true
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
