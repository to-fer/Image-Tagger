package gui.qt.gui

class Window extends StackedWidget {
  private var _fullScreen = false
  private var _maximized = false

  def title = delegate.windowTitle
  def title_=(title: String): Unit = delegate.setWindowTitle(title)

  def fullScreen = _fullScreen
  def fullScreen_=(fullScreen: Boolean): Unit =
    _fullScreen = fullScreen

  def maximized = _maximized
  def maximized_=(maximized: Boolean): Unit =
    _maximized = maximized

  override def show(): Unit =
    if (fullScreen)
      delegate.showFullScreen()
    else if (maximized)
      delegate.showMaximized()
    else
      delegate.show()
}
