package org.oxbow.vixen.command

import com.vaadin.ui.Button
import com.vaadin.ui.MenuBar


private trait ComponentAdapter[T] extends CommandDefinition {
    val component: T
}


private case class ButtonAdapter( component: Button, action: AnyRef => Unit ) extends ComponentAdapter[Button] {
    component.addClickListener{ _ => if ( component.isEnabled ) action(component) }
    enabledProperty.addListener{ (_,_,newValue) => component.setEnabled(newValue) }
    captionProperty.addListener{ (_,_,newValue) => component.setCaption(newValue) }
    descriptionProperty.addListener{ (_,_,newValue) => component.setDescription(newValue) }
    iconProperty.addListener{ (_,_,newValue) => component.setIcon(newValue) }
    styleProperty.addListener{ (_,oldValue,newValue) =>
        component.removeStyleName(oldValue)
        component.addStyleName(newValue)
    }

}

private case class MenuItemAdapter(component: MenuBar#MenuItem, action: AnyRef => Unit ) extends ComponentAdapter[MenuBar#MenuItem] {

    component.setCommand((selectedItem: MenuBar#MenuItem) => action(selectedItem))
    enabledProperty.addListener{ (_,_,newValue) => component.setEnabled(newValue) }
    captionProperty.addListener{ (_,_,newValue) => component.setText(newValue) }
    descriptionProperty.addListener{ (_,_,newValue) => component.setDescription(newValue) }
    iconProperty.addListener{ (_,_,newValue) => component.setIcon(newValue) }

}
