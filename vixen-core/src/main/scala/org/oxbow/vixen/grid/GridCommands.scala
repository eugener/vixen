package org.oxbow.vixen.grid

import com.vaadin.data.provider.ListDataProvider
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

            grid.getDataProvider match {
                case ldp: ListDataProvider[T] =>
                    ldp.getItems.remove(selectedItem)
                    ldp.getItems.add(newItem)
                    grid.deselectAll()
                    grid.getDataProvider.refreshAll()
                    grid.select(newItem)

                    ldp.getItems match {
                        case lst: java.util.List[T] =>
                            grid.scrollTo( lst.indexOf(newItem))
                        case x =>
                            logError(s"Cannot execute 'scrollTo'. Unsupported items collection: ${x.getClass.getName}. Only java.util.List is supported")
                    }
                case x =>
                    logError(s"Update command cannot be executed. Unsupported grid data provider ${x.getClass.getName}")

                //TODO look into different data providers
            }

        }


    }

    protected def getEnabledCondition: Boolean = grid.getSelectedItems.size() == 1

    def update( selection: T ): T
}

abstract class AbstractGridCommand[T]( val grid: Grid[T],  text: String ) extends Command {

    this.caption = text
    grid.getSelectionModel.addSelectionListener((event: SelectionEvent[T]) => enabled = getEnabledCondition)

    protected def getEnabledCondition: Boolean

    protected def logError(msg: String): Unit = System.err.println(msg)
}