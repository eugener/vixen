package org.oxbow.vixen.grid

import com.vaadin.event.selection.SelectionEvent
import com.vaadin.ui.Grid
import org.oxbow.vixen.command.Command

import scala.collection.JavaConverters._

object GridCommands {

    def itemUpdateCommand[T](  grid: Grid[T], caption: String )( updateItem: T => T ): GridItemUpdateCommand[T] = {
        new GridItemUpdateCommand(grid, caption) {
            override def update(selection: T): T = updateItem(selection)
        }
    }

}


abstract class GridItemUpdateCommand[T]( override val grid: Grid[T], caption: String ) extends AbstractGridCommand(grid, caption) {

    override protected val action: (AnyRef) => Unit = { source: AnyRef =>

        grid.getSelectedItems.asScala.headOption.foreach{ selectedItem =>
            val newItem = update(selectedItem)
//            grid.getDataProvider.r
            grid.getDataProvider.refreshItem(newItem)
        }


    }

    protected def getEnabledCondition: Boolean = grid.getSelectedItems.size() == 1

    def update( selection: T ): T
}

abstract class AbstractGridCommand[T]( val grid: Grid[T],  text: String ) extends Command {

    this.caption = text
    grid.getSelectionModel.addSelectionListener((event: SelectionEvent[T]) => enabled = getEnabledCondition)

    protected def getEnabledCondition: Boolean
}