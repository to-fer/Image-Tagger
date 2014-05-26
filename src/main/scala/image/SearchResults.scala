package image

import event.Observable

class SearchResults extends Observable {
  private var _imagePaths = Seq.empty[String]

  def imagePaths = _imagePaths
  def imagePaths_=(imagePaths: Seq[String]) = {
    _imagePaths = imagePaths
    notifyObservers()
  }
}
