package qt.gui

import com.trolltech.qt.gui.{QLayout, QGridLayout}
import qt.util.Container

class GridWidget extends Widget with Layout {
  private val gridLayout = new QGridLayout(delegate)
  gridLayout.setSpacing(0)

  private var _maxColumns = 5
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
    gridLayout.setAlignment(w.delegate, w.alignment:_*)
  }
}
