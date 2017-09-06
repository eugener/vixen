package org.oxbow.vixen.demo


import java.util
import java.util.UUID
import javax.servlet.annotation.WebServlet

import com.vaadin.annotations.{Title, VaadinServletConfiguration}
import com.vaadin.server.{ExternalResource, VaadinRequest, VaadinServlet}
import com.vaadin.ui._
import com.vaadin.ui.themes.ValoTheme
import org.oxbow.vixen.command.Command
import org.oxbow.vixen.command.Command._
import org.oxbow.vixen.grid.GridView
import org.oxbow.vixen.grid.GridCommands._

import scala.beans.BeanProperty

@WebServlet(value = Array("/*"), asyncSupported = true)
@VaadinServletConfiguration( productionMode = false, ui = classOf[VixenDemoUI])
class VixenDemoServlet extends VaadinServlet {}


@Title("Vixen Demo")
class VixenDemoUI extends UI {


    override protected def init(request: VaadinRequest): Unit = {

        val tabs = new TabSheet
        tabs.setStyleName(ValoTheme.TABSHEET_FRAMED)
        tabs.addTab( getCommandTab, "Commands" )
        tabs.addTab( getTableViewTab,  "GridView" )

        val layout = new VerticalLayout()
        val title = new Label("Vixen Demo")
        title.addStyleName(ValoTheme.LABEL_BOLD)
        title.addStyleName(ValoTheme.LABEL_COLORED)
        title.addStyleName(ValoTheme.LABEL_HUGE)
        layout.addComponent(title)
//        layout.addComponentsAndExpand(tabs)
        layout.addComponent(tabs)

        setContent(layout)

    }

    def getCommandTab: AbstractLayout = {

        val vCommand: Command = Command("Vixen Command"){ _ => Notification.show("Vixen Command executed!") }
        vCommand.description = "Vixen description"
        vCommand.style = ValoTheme.BUTTON_PRIMARY
        vCommand.icon = new ExternalResource(
            "https://cdn2.iconfinder.com/data/icons/designer-skills/128/github-repository-svn-manage-files-contribute-branch-32.png")


        val group = CommandGroup("Command Group 1")(
            vCommand,
            vCommand,
            CommandGroup ("Command Group 2") (
                vCommand
            )
        )

        val menu = buildMenu( new MenuBar, group, group )
        menu.setStyleName(ValoTheme.MENUBAR_BORDERLESS)

        val button = new Button("Hello World!")
        vCommand.bindTo(button)

        val button2 = new Button("Another Button")
        vCommand.bindTo(button2)

        val check = new CheckBox("Enable components")
        check.setValue(vCommand.enabled)
        check.addValueChangeListener( _ => vCommand.enabled = check.getValue)

        val content = new VerticalLayout
        content.addComponent(check)
        content.addComponent(new Label("One command controls all the components below"))
        content.addComponent(menu)
        content.addComponent(button)
        content.addComponent(button2)

        content


    }


    val people = util.Arrays.asList(
        Person( "Pamela", "Mccaster", "developer"),
        Person("Floretta", " Shorts", "manager"),
        Person("Gonzalo", " Maples", "manager"),
        Person("Lucas", " Gamon", "developer"),
        Person("Kris", " Rasmussen", "manager"),
        Person("Collene", " Studstill", "director"),
        Person("Agnus", " Rosenau", "developer"),
        Person("Enola", " Orsborn", "manager"),
        Person("Gisele", " Cartledge", "manager"),
        Person("Nicky", " Fick", "developer")
    )

    def getTableViewTab: AbstractLayout = {

        val view = new GridView[Person]

        view.grid.setItems(people)
        view.grid.addColumn( _.firstName ).setCaption("First Name")
        view.grid.addColumn( _.lastName ).setCaption("Last Name")
        view.grid.addColumn( _.position ).setCaption("Position")

        view.commands.addAll(
            Command("Insert")(),
            itemUpdateCommand(view.grid, "Update") { selection: Person =>
               new Person("George", "Washington", "President")
            },
            Command("Delete")()
        )

        view
    }


}

case class Person(
     @BeanProperty var firstName: String,
     @BeanProperty var lastName: String,
     @BeanProperty var position: String
) {
    private val id = UUID.randomUUID().toString

    override def hashCode(): Int = id.hashCode()

    override def equals(obj: scala.Any): Boolean = obj match {
        case p: Person => p.id == id
        case _ => false
    }
}
