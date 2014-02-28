package qt.image

import java.io.File
import scala.collection.mutable
import qt.image.Image
import qt.gui.{VBoxWidget, Parent}

// TODO replace this with a more general ImageView class?
class SequentialImageViewer(parent: Parent, imageFiles: Seq[File], imageWidth: Int, imageHeight: Int) {
  private var imageIndex = 0
  private var preloadedImage: Image = _
  private val imageCache = mutable.Queue[Image]()
  private val maxCacheSize = 5
  private var currentImage: Image = _

  def showNextImage() = {
    imageIndex += 1
    imageCache += preloadedImage

    if (imageCache.size > maxCacheSize) {
      val image = imageCache.dequeue()
      image.dispose()
    }

    addImage(preloadedImage)
    preloadedImage = preloadImage(imageIndex + 1)
  }

  def showFirstImage() = {
    addImage(new Image(imageFiles.head.toString, imageWidth, imageHeight))
    preloadedImage = preloadImage(1)
  }

  def preloadImage(index: Int) = new Image(imageFiles(index).toString, imageWidth, imageHeight)

  def showPreviousImage() = {
    imageIndex -= 1
    if (!imageCache.isEmpty)
      addImage(imageCache.dequeue())
    else
      addImage(new Image(imageFiles(imageIndex).toString, imageWidth, imageHeight))
  }

  def addImage(image: Image) = {
    if (currentImage != null) {
      parent -= image
      currentImage.hide()
    }

    parent += image
    currentImage = image
  }

  def hasPreviousImage = imageIndex > 0
  
  def hasNextImage = imageIndex < imageFiles.length - 1

  def getCurrentImage = currentImage

  def currentImageFile = imageFiles(imageIndex)

  def dispose() = {
    imageCache.foreach(_.dispose())
    imageCache.clear()
    parent -= currentImage
    currentImage.dispose()
  }
}
