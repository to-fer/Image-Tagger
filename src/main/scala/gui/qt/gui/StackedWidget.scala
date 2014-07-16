package gui.qt.gui

import com.trolltech.qt.gui.QLayout.SizeConstraint
import com.trolltech.qt.gui.QLayout.SizeConstraint._
import com.trolltech.qt.gui.QSizePolicy.Policy._
import com.trolltech.qt.gui.QStackedLayout.StackingMode
import com.trolltech.qt.gui.{QSizePolicy, QStackedLayout}

class StackedWidget extends Widget with Layout {
  private val stackedLayout = new QStackedLayout(delegate)

  stackingMode = StackingMode.StackAll

  def stackingMode_=(stackingMode: StackingMode): Unit =
    stackedLayout.setStackingMode(stackingMode)

  def stackingMode = stackedLayout.stackingMode

  def currentWidget_=(w: Widget): Unit = {
    stackedLayout.setCurrentWidget(w.delegate)
  }

  def currentWidget =
    stackedLayout.currentWidget

  /*
    QLayout's SizeConstraint preempts the QSizePolicy of its parent QWidget, so we have to override
    this method and have it set the SizeConstraint of the inner QStackedLayout instead.
   */
  override def sizePolicy_=(policy: QSizePolicy): Unit = {
    super.sizePolicy_=(policy)
    // Some of these mappings are sloppy.
    def policyToConstraint(policy: QSizePolicy.Policy): SizeConstraint = policy match {
      case Expanding => SetNoConstraint // Sloppy
      case Fixed => SetFixedSize
      case Ignored => SetNoConstraint
      case Maximum => SetMaximumSize
      case Minimum => SetMinimumSize
      case MinimumExpanding => SetMinimumSize // Sloppy
      case Preferred => SetNoConstraint // sloppy
    }

    // Only uses vertical policy (SizeConstraint doesn't make a distinction between vertical/horizontal constraints).
    stackedLayout.setSizeConstraint(policyToConstraint(policy.verticalPolicy))
  }

  override protected def layout(w: Widget): Unit = {
    stackedLayout.addStackedWidget(w.delegate)
  }
}
