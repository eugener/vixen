package org.oxbow.vixen.command

import javafx.beans.property._

import com.vaadin.server.Resource
import com.vaadin.ui.MenuBar

import scala.collection.mutable

object Command {

    def apply( caption: String )( perform: AnyRef => Unit ): Command = {
        val cmd = new Command {
            val action: (AnyRef) => Unit = perform
        }
        cmd.caption = caption
        cmd
    }

    def CommandGroup( caption: String )( commands: Command*): Command = {
        val cmd = new CommandGroup(commands.toArray)
        cmd.caption = caption
        cmd
    }

    type MenuBase = {
        def addItem( caption: String, cmd: MenuBar.Command ): MenuBar#MenuItem
    }

    def buildMenu[T <: MenuBase](parentMenu: T, commands: Command*): T = {

        def createMenuItem( parentMenu: T, cmd: Command ): MenuBar#MenuItem = {
            val menuItem = parentMenu.addItem("<Temp>", null)
            cmd.bindTo(menuItem)
            menuItem
        }

        commands.foreach {
            case group: CommandGroup =>
                println("Add command group")
                val menuItem = createMenuItem(parentMenu,group)
                buildMenu( menuItem, group.subCommands: _* )
            case cmd: Command =>
                println("Add command to " + parentMenu.asInstanceOf[Any].getClass.getSimpleName)
                createMenuItem(parentMenu,cmd)
        }
        parentMenu
    }


}

trait Command extends CommandDefinition {

    private val boundComponents = mutable.ListBuffer[ComponentAdapter[_]]()

    def enabled: Boolean = enabledProperty.get
    def enabled_=( value: Boolean ): Unit = enabledProperty.set(value)

    def caption: String = captionProperty.get
    def caption_=( value: String ): Unit = captionProperty.set(value)

    def description: String = descriptionProperty.get
    def description_=( value: String ): Unit = descriptionProperty.set(value)

    def icon: Resource = iconProperty.get
    def icon_=( value: Resource ): Unit = iconProperty.set(value)

    def style: String = styleProperty.get
    def style_=( value: String ): Unit = styleProperty.set(value)


    /**
      * Assigns command to the given component
      */
    def bindTo(component: AnyRef): Unit = {
        ComponentAdapter(component, action).foreach { adapter =>
            adapter.enabledProperty.bind(enabledProperty)
            adapter.captionProperty.bind(captionProperty)
            adapter.descriptionProperty.bind(descriptionProperty)
            adapter.iconProperty.bind(iconProperty)
            adapter.styleProperty.bind(styleProperty)
            boundComponents += adapter
        }
    }

    def unbind(component: AnyRef): Unit = {
        Option(component)
            .flatMap { c => boundComponents.find(_.target == c) }
            .foreach { adapter =>
                boundComponents -= adapter
                adapter.enabledProperty.unbind()
                adapter.captionProperty.unbind()
                adapter.descriptionProperty.unbind()
                adapter.iconProperty.unbind()
                adapter.styleProperty.unbind()
            }
    }


}


private[command] class CommandGroup( val subCommands: Array[Command] ) extends Command {
    override private[command] val action = { _: AnyRef => () }
}

private[command] trait CommandDefinition {
    private[command] val action: AnyRef => Unit
    private[command] final val enabledProperty: BooleanProperty       = new SimpleBooleanProperty(true)
    private[command] final val captionProperty: StringProperty        = new SimpleStringProperty("")
    private[command] final val descriptionProperty: StringProperty    = new SimpleStringProperty("")
    private[command] final val iconProperty: ObjectProperty[Resource] = new SimpleObjectProperty[Resource]()
    private[command] final val styleProperty: StringProperty          = new SimpleStringProperty("")
}