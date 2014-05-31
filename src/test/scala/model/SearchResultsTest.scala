package model

import org.specs2.mutable.Specification

class SearchResultsTest extends Specification {
  "SearchResults" should {
    "set imagePaths" in {
      val results = new SearchResults
      val testPaths = "test1.png" :: "test2.jpg" :: "test3.gif" :: Nil
      results.imagePaths = testPaths
      results.imagePaths mustEqual testPaths
    }

    "call notifyObservers() after state is mutated" in {
      val results = new SearchResults
      val testPaths = "test1.png" :: "test2.jpg" :: "test3.gif" :: Nil
      var notifyPaths = Seq.empty[String]
      val observer = () => {
        notifyPaths = results.imagePaths
      }
      results.addObserver(observer)
      results.imagePaths = testPaths
      notifyPaths mustEqual testPaths
    }
  }
}
