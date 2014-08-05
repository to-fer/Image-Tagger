package event.mode

import model.ActiveMode

import scalafx.scene.Parent

class ModeSwitcher(activeMode: ActiveMode){
  def switch(newMode: Mode): Unit =
    activeMode.setMode(newMode)
}
