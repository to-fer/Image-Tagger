package qt.gui

import com.trolltech.qt.gui.QGridLayout

class GridWidget extends Widget with Layout {
  private val gridLayout = new QGridLayout(delegate)
  private var _spacing = 0
  private var _maxColumns = 5

  // Make sure we call setSpacing to default spacing to 0
  spacing = 0

  def spacing = _spacing
  def spacing_=(s: Int) = {
    _spacing = s
    gridLayout.setSpacing(s)
  }

  def maxColumns = _maxColumns
  def maxColumns_=(maxColumns: Int) =
    if (maxColumns != _maxColumns) {
      _maxColumns = maxColumns
      content.foreach(layout)
    }

  override protected def layout(w: Widget): Unit = {
    val rowNum = content.size / maxColumns
    val colNum = content.size % maxColumns
    gridLayout.addWidget(w.delegate, rowNum, colNum)
  }
}
