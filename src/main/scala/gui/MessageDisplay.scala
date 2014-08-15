package gui

import scalafx.animation.{FadeTransition, PauseTransition, SequentialTransition}
import scalafx.geometry.Pos
import scalafx.scene.Node
import scalafx.scene.control.TextField
import scalafx.scene.effect.{DropShadow, Effect}
import scalafx.scene.paint.Color
import scalafx.util.Duration

class MessageDisplay(width: Int) {

  private val display = new TextField {
    alignment = Pos.CENTER
    maxWidth = MessageDisplay.this.width
    maxHeight = 25
    editable = false
    mouseTransparent = true
    focusTraversable = false
    alignmentInParent = Pos.TOP_CENTER
    opacity = 0 // message display is hidden until there is a message to be displayed
  }

  private var _isVisible: Boolean = false

  def isVisible_=(visible: Boolean): Unit =
    _isVisible = visible

  def isVisible: Boolean = _isVisible

  private def dropShadow(col: Color) = new DropShadow {
    offsetX = 0
    offsetY = 0
    color = col
    width = 50
    height = 50
  }

  private val messageDropShadow = dropShadow(Color.Blue)
  private val errorDropShadow = dropShadow(Color.Red)

  def newFadeOut = () => new FadeTransition {
    duration = Duration(200)
    node = display
    fromValue.bind(display.opacity) // Allows smooth transition from current opacity to toValue
    toValue = 0
  }

  private val transition = new SequentialTransition {
    children = List (
      new FadeTransition {
        duration = Duration(100)
        node = display
        fromValue.bind(display.opacity)
        toValue = 1
      },
      new PauseTransition(Duration(5000)),
      newFadeOut()
    )
  }

  private val fadeOutTransition = newFadeOut()

  def node: Node = display

  private def display(msg: String, effect: Effect): Unit = {
    display.effect = effect
    display.text = msg

    if (!isVisible) {
      isVisible = true
      fadeOutTransition.pause()
      transition.playFromStart()
    }
  }

  def displayMessage(msg: String): Unit = {
    display(msg, messageDropShadow)
  }

  def displayError(errorMsg: String): Unit = {
    display(errorMsg, errorDropShadow)
  }

  def hide(): Unit = {
    transition.pause()
    fadeOutTransition.playFromStart()
    isVisible = false
  }
}
