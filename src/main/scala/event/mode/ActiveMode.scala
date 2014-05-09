package event.mode

import qt.gui.Widget


class ActiveMode {

  private var _currentMode: Option[Mode] = None
  private var _currentModeView: Option[Widget] = None
  private var observerList = List.empty[() => Unit]
  
  def currentMode = _currentMode

  def currentModeView = _currentModeView

  def setModeAndView(newMode: Mode, newView: Widget): Unit = {
    _currentMode = Some(newMode)
    _currentModeView = Some(newView)
    notifyObservers()
  }

  private def notifyObservers(): Unit = 
    observerList.foreach(_())
  
  def addObserver(observer: () => Unit): Unit =
    observerList = observer :: observerList 
  
}
