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

    val cmd = Command("Vixen Command"){ _ => Notification.show("Vixen Command executed!") }
    cmd.description = "Vixen description"
    cmd.style = ValoTheme.BUTTON_PRIMARY
    cmd.icon = new ExternalResource("https://cdn2.iconfinder.com/data/icons/designer-skills/128/github-repository-svn-manage-files-contribute-branch-32.png")

    override protected def init(request: VaadinRequest): Unit = {

        val content = new VerticalLayout
        setContent(content)

        val button = new Button("Hello World!")
        cmd.bindTo(button)

        val button2 = new Button("Another Button")
        cmd.bindTo(button2)

        val check = new CheckBox("Button enabled")
        check.setValue(cmd.enabled)
        check.addValueChangeListener( e => cmd.enabled = check.getValue)

        content.addComponent(button)
        content.addComponent(button2)
        content.addComponent(check)

    }

}
