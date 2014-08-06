package model

import event.Observable

class UntaggedImages extends Observable {
  private var _untaggedImageFileURIs = Seq.empty[String]
  // Always stays 1 ahead of currently displayed image URI unless tagging is almost done.
  private var nextImageIndex: Int = 0

  def untaggedImageFileURIs_=(fileURIs: Seq[String]): Unit = {
    _untaggedImageFileURIs = fileURIs
  }

  def untaggedImageFileURIs = _untaggedImageFileURIs
  
  def nextImageURI(): Unit = {
    if (hasNext)
      nextImageIndex += 1
    notifyObservers()
  }
  
  def hasNext: Boolean = nextImageIndex < _untaggedImageFileURIs.length
  
  def currentURI = _untaggedImageFileURIs(nextImageIndex - 1)

  def nextURI = _untaggedImageFileURIs(nextImageIndex)
}
