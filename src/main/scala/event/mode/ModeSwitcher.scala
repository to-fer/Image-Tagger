package event.mode

import qt.gui.Widget
import command.CommandResult

class ModeSwitcher(activeMode: ActiveMode, modeViewMap: Map[Mode, Widget]) {
  def switch(newMode: Mode): Unit =
    activeMode.setModeAndView(newMode, modeViewMap(newMode))
}
