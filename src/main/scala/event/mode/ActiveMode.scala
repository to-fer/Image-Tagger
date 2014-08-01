package event.mode

import event.Observable

import scalafx.scene.Parent


class ActiveMode extends Observable {
  private var _currentMode: Option[Mode] = None
  private var _currentModeView: Option[Parent] = None
  
  def currentMode = _currentMode

  def currentModeView = _currentModeView

  def setModeAndView(newMode: Mode, newView: Parent): Unit = {
    _currentMode = Some(newMode)
    _currentModeView = Some(newView)
    notifyObservers()
  }
}
