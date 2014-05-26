package event.mode

import qt.gui.Widget
import event.Observable


class ActiveMode extends Observable {
  private var _currentMode: Option[Mode] = None
  private var _currentModeView: Option[Widget] = None
  
  def currentMode = _currentMode

  def currentModeView = _currentModeView

  def setModeAndView(newMode: Mode, newView: Widget): Unit = {
    _currentMode = Some(newMode)
    _currentModeView = Some(newView)
    notifyObservers()
  }
}
