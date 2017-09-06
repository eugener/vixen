package org.oxbow.vixen

import javafx.beans.{InvalidationListener, Observable}
import javafx.collections.{FXCollections, ListChangeListener, ObservableList}

import com.vaadin.ui.themes.ValoTheme
import com.vaadin.ui.{Grid, MenuBar, VerticalLayout}
import org.oxbow.vixen.command.Command
import org.oxbow.vixen.command.Command._

import collection.JavaConverters._

class GridView[T] extends VerticalLayout {

    protected val toolBar = new MenuBar
    toolBar.setWidth("100%")
    toolBar.setStyleName(ValoTheme.MENUBAR_BORDERLESS)

    val grid = new Grid[T]
    grid.setWidth("100%")


    val commands: ObservableList[Command] = FXCollections.observableArrayList[Command]()
    commands.addListener( new InvalidationListener {
        override def invalidated(observable: Observable): Unit = {
            toolBar.getItems.clear()
            buildMenu( toolBar, commands.asScala: _* )
        }
    })


    addComponent(toolBar)
    addComponent(grid)


}
