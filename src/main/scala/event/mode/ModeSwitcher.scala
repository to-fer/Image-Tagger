package event.mode

import scalafx.scene.Parent

class ModeSwitcher(activeMode: ActiveMode, modeViewMap: Map[Mode, Parent]) {
  def switch(newMode: Mode): Unit =
    activeMode.setModeAndView(newMode, modeViewMap(newMode))
}
