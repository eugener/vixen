package org.oxbow.vixen.demo


import javax.servlet.annotation.WebServlet

import com.vaadin.annotations.{Title, VaadinServletConfiguration}
import com.vaadin.server.VaadinRequest
import com.vaadin.ui._
import com.vaadin.server.VaadinServlet
import org.oxbow.vixen.command.Command

@WebServlet(value = Array("/*"), asyncSupported = true)
@VaadinServletConfiguration( productionMode = false, ui = classOf[VixenDemoUI])
class VixenDemoServlet extends VaadinServlet {}


@Title("Vixen Demo")
class VixenDemoUI extends UI {

    override protected def init(request: VaadinRequest): Unit = {

        val content = new VerticalLayout
        setContent(content)

        val button = new Button("Hello World!")
        val cmd = Command("Vixen Command"){ _ => Notification.show("Vixen Command executed!") }
        cmd.bindTo(button)

        val check = new CheckBox("Button enabled")
        check.setValue(cmd.enabled)
        check.addValueChangeListener( e => cmd.enabled = check.getValue)

        content.addComponent(button)
        content.addComponent(check)

    }

}
