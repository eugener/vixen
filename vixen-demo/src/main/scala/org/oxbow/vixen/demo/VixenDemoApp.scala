package org.oxbow.vixen.demo


import javax.servlet.annotation.WebServlet

import com.vaadin.annotations.{Theme, Title, VaadinServletConfiguration}
import com.vaadin.server.{ExternalResource, VaadinRequest, VaadinServlet}
import com.vaadin.ui._
import com.vaadin.ui.themes.ValoTheme
import org.oxbow.vixen.command.Command

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

        val cmd: Command = Command("Vixen Command"){ _ => Notification.show("Vixen Command executed!") }
        cmd.description = "Vixen description"
        cmd.style = ValoTheme.BUTTON_PRIMARY
        cmd.icon = new ExternalResource(
            "https://cdn2.iconfinder.com/data/icons/designer-skills/128/github-repository-svn-manage-files-contribute-branch-32.png")


        val menu = new MenuBar
        val fileItem = menu.addItem("File", null)
        val menuItem = fileItem.addItem("", null)
        cmd.bindTo(menuItem)

        val button = new Button("Hello World!")
        cmd.bindTo(button)

        val button2 = new Button("Another Button")
        cmd.bindTo(button2)

        val check = new CheckBox("Enable components")
        check.setValue(cmd.enabled)
        check.addValueChangeListener( _ => cmd.enabled = check.getValue)

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
