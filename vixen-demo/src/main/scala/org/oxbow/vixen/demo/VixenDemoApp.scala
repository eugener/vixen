package org.oxbow.vixen.demo


import javax.servlet.annotation.WebServlet

import com.vaadin.annotations.{Title, VaadinServletConfiguration}
import com.vaadin.server.{ExternalResource, VaadinRequest, VaadinServlet}
import com.vaadin.ui._
import com.vaadin.ui.themes.ValoTheme
import org.oxbow.vixen.command.Command
import org.oxbow.vixen.command.Command._

@WebServlet(value = Array("/*"), asyncSupported = true)
@VaadinServletConfiguration( productionMode = false, ui = classOf[VixenDemoUI])
class VixenDemoServlet extends VaadinServlet {}


@Title("Vixen Demo")
class VixenDemoUI extends UI {


    override protected def init(request: VaadinRequest): Unit = {

        val tabs = new TabSheet
        tabs.setStyleName(ValoTheme.TABSHEET_FRAMED)
        tabs.addTab( getCommandTab, "Commands" )
        tabs.addTab( getTableViewTab,  "TableView" )

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

    def getTableViewTab: AbstractLayout = {
        new VerticalLayout
    }


}
