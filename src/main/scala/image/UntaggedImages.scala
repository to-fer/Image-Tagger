package image

import java.io.File
import qt.image.Image
import qt.util.Screen

class UntaggedImages {
  private var observerList = List.empty[() => Unit]
  private var _untaggedImageFiles = Seq.empty[File]
  private var imageIndex: Int = -1 // Starts at -1, since it will be 0 after the first call to nextImage()
  private var preloadedImage: Option[Image] = None
  private var _currentImage: Image = _
  private var _previousImage: Option[Image] = None
  private val (screenWidth, screenHeight) = Screen.size

  def untaggedImageFiles_=(files: Seq[File]): Unit = 
    _untaggedImageFiles = files
    
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

  def notifyObservers() = 
    observerList.foreach(_())
  
  def addObserver(observer: () => Unit): Unit = 
      observerList = observer :: observerList
}