package org.oxbow.vixen.grid

import com.vaadin.data.provider.{DataProvider, ListDataProvider}
import com.vaadin.event.selection.SelectionEvent
import com.vaadin.ui.Grid
import org.oxbow.vixen.command.Command

import scala.collection.JavaConverters._


class GridItemUpdateCommand[T]( override val grid: Grid[T], text: String )(update: Option[T] => T) extends AbstractGridCommand(grid, text) {

    override protected val action: (AnyRef) => Unit = { source: AnyRef =>

        grid.getSelectedItems.asScala.headOption.foreach { selectedItem =>
            val newItem = update(Some(selectedItem))
            adapter.clearSelection()
            adapter.update(selectedItem, newItem)
            adapter.refreshAll()
            adapter.select(newItem)
            adapter.scrollTo(newItem)
        }


    }

    protected def getEnabledCondition: Boolean = grid.getSelectedItems.size() == 1

}


class GridItemInsertCommand[T]( override val grid: Grid[T], text: String )(insert: Option[T] => T) extends AbstractGridCommand(grid, text) {

    override protected val action: (AnyRef) => Unit = { source: AnyRef =>

        val newItem = insert(grid.getSelectedItems.asScala.headOption)
        adapter.clearSelection()
        adapter.add(newItem)
        adapter.refreshAll()
        adapter.select(newItem)
        adapter.scrollTo(newItem)

    }

    protected def getEnabledCondition: Boolean = true


}

class GridItemRemoveCommand[T]( override val grid: Grid[T], text: String )(remove: Option[T] => Boolean ) extends AbstractGridCommand(grid, text) {

    override protected val action: (AnyRef) => Unit = { source: AnyRef =>

        grid.getSelectedItems.asScala.headOption.foreach { selectedItem =>

            if (remove(Some(selectedItem))) {

                adapter.clearSelection()

                val afterSelection = adapter.afterDeleteSelection(selectedItem)
                adapter.remove(selectedItem)
                adapter.refreshAll()


                afterSelection.foreach { item =>
                    adapter.select(item)
                    adapter.scrollTo(item)
                }

            }

        }


    }


    protected def getEnabledCondition: Boolean = grid.getSelectedItems.size() == 1

}

abstract class AbstractGridCommand[T]( val grid: Grid[T], text: String ) extends Command {

    this.caption = text
    grid.getSelectionModel.addSelectionListener((event: SelectionEvent[T]) => enabled = getEnabledCondition)

    protected lazy val adapter = new DataProviderAdapter[T](grid)

    protected def getEnabledCondition: Boolean

}


private[grid] class DataProviderAdapter[T]( val grid: Grid[T] ) {

    protected def logError(msg: String): Unit = System.err.println(msg)


    def clearSelection(): Unit = grid.deselectAll()
    def unselect( item: T ): Unit = grid.deselect(item)
    def select( item: T ): Unit = grid.select(item)

    def refreshAll(): Unit = grid.getDataProvider.refreshAll()
    def refresh( item: T ): Unit = grid.getDataProvider.refreshItem(item)

    def afterDeleteSelection( item: T ): Option[T] = {

        grid.getDataProvider match {
            case ldp: ListDataProvider[T] =>
                val lst = ldp.getItems.asScala.toList
                lst.indexOf(item) match {
                    case 0 => if (lst.size == 1) None else Some(lst(1))
                    case idx => Some(lst(idx-1))
                }

            case x =>
               logError( s"'afterDeleteIndex' operation is only supported for ListDataProvider(not ${x.getClass.getSimpleName})")
               None
        }
    }


    def scrollTo( index: Int ): Unit = grid.scrollTo(index)

    def scrollTo( item: T): Unit = {
        grid.getDataProvider match {
            case ldp: ListDataProvider[T] =>
                val index = ldp.getItems match {
                    case lst: java.util.List[T] => lst.indexOf(item)
                    case items => items.asScala.toList.indexOf(item) // might be slow
                }
                grid.scrollTo(index);
            case x =>
                logError( s"'ScrollTo' operation is only supported for ListDataProvider(not ${x.getClass.getSimpleName})")
        }

    }

    def remove( item: T ): Boolean = grid.getDataProvider match {
        case ldp: ListDataProvider[T] => ldp.getItems.remove(item)
        case x =>
            logError( s"'Remove' operation is only supported for ListDataProvider(not ${x.getClass.getSimpleName})")
            false
    }

    def add( item: T ): Boolean = grid.getDataProvider match {
        case ldp: ListDataProvider[T] => ldp.getItems.add(item)
        case x =>
            logError( s"'Add' operation is only supported for ListDataProvider (not ${x.getClass.getSimpleName})")
            false
    }

    def update( fromItem: T, toItem: T ): Boolean = grid.getDataProvider match {
        case ldp: ListDataProvider[T] =>
            ldp.getItems match {
                case lst: java.util.List[T] =>
                    lst.add( lst.indexOf(fromItem), toItem )
                    lst.remove(fromItem)
                    true
                case items =>
                     val lst = items.asScala.toBuffer
                     lst.patch(lst.indexOf(fromItem), Seq(toItem),1)
                     true
//                    remove(fromItem)
//                    add(toItem)
            }

        case x =>
            logError( s"'Update' operation is only supported for ListDataProvider (not ${x.getClass.getSimpleName})")
            false
    }

}