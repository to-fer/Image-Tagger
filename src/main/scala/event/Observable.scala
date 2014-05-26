package event

trait Observable {
  private var observers = Seq.empty[() => Unit]

  def addObserver(observer: () => Unit) =
    observers = Seq(observer) ++ observers

  protected def notifyObservers(): Unit =
    observers.foreach(_())
}