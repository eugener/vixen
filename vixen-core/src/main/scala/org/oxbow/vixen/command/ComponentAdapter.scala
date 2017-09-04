package org.oxbow.vixen.command

import javafx.beans.property.{SimpleBooleanProperty, SimpleStringProperty}

import com.vaadin.ui.Button


private trait ComponentAdapter[T] extends CommandDefinition {
    val component: T
}


private case class ButtonAdapter( component: Button, action: AnyRef => Unit ) extends ComponentAdapter[Button] {
    component.addClickListener{ _ => if ( component.isEnabled ) action(component) }
    enabledProperty.addListener{ (_,_,newValue) => component.setEnabled(newValue) }
    captionProperty.addListener{ (_,_,newValue) => component.setCaption(newValue) }
}
