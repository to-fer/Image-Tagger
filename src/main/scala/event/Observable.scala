package event

trait Observable {
  private var observers = Seq.empty[() => Unit]

  def addObserver(observer: () => Unit): Unit =
    observers = observers :+ observer

  protected def notifyObservers(): Unit =
    observers.foreach(_())
}