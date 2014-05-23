package event

trait Observable {
  private val observers = Seq.empty[() => Unit]

  def addObserver(observer: () => Unit) =
    observers :+ observer

  protected def notifyObservers(): Unit =
    observers.foreach(_())
}
