package model

import java.io.File
import gui.qt.image.Image
import gui.qt.util.Screen
import event.Observable

class UntaggedImages extends Observable {
  private var _untaggedImageFiles = Seq.empty[File]
  private var imageIndex: Int = -1
  private var preloadedImage: Option[Image] = None
  private var _currentImage: Image = _
  private var _previousImage: Option[Image] = None
  private val (screenWidth, screenHeight) = Screen.size

  def untaggedImageFiles_=(files: Seq[File]): Unit = {
    _untaggedImageFiles = files
    // Starts at -1 so nextImage() sets the correct image as the current image (the one at index 0)
    imageIndex = -1
    nextImage()
  }

  def untaggedImageFiles = _untaggedImageFiles
  
  def nextImage() = {
    if (hasNextImage) {
      _previousImage = Some(currentImage)
      imageIndex += 1

      preloadedImage match {
        case Some(image) =>
          _currentImage = image
        case None =>
          _currentImage = loadImage(_untaggedImageFiles(imageIndex).toString)
      }

      if (hasNextImage)
        preloadedImage = Some(loadImage(_untaggedImageFiles(imageIndex + 1).toString))

      notifyObservers()
    }
    else
      throw new Exception("There is no next image; you've run out of images to tag!")
  }
  
  private def loadImage(imageFilePath: String) =
    new Image(imageFilePath, screenWidth, screenHeight)
  
  def hasNextImage() = 
    imageIndex < untaggedImageFiles.size
  
  def currentImageFile = _untaggedImageFiles(imageIndex)

  def currentImage = _currentImage

  def previousImage = _previousImage
}
