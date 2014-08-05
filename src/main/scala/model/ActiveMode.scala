package model

import event.Observable
import event.mode.Mode


class ActiveMode extends Observable {
  private var _currentMode: Option[Mode] = None

  def currentMode = _currentMode

  def setMode(newMode: Mode): Unit = {
    _currentMode = Some(newMode)
    notifyObservers()
  }
}
