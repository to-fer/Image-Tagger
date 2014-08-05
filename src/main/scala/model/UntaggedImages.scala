package model

import event.Observable

class UntaggedImages extends Observable {
  private var _untaggedImageFileURIs = Seq.empty[String]
  private var imageIndex: Int = 0 // Always stays 1 ahead of currently displayed image URI unless tagging is almost done.

  def untaggedImageFileURIs_=(fileURIs: Seq[String]): Unit = {
    _untaggedImageFileURIs = fileURIs
  }

  def untaggedImageFileURIs = _untaggedImageFileURIs
  
  def nextImageURI(): Unit = {
    if (hasNext)
      imageIndex += 1
    notifyObservers()
  }
  
  def hasNext: Boolean = imageIndex < _untaggedImageFileURIs.length  
  
  def currentURI = _untaggedImageFileURIs(imageIndex)
}
