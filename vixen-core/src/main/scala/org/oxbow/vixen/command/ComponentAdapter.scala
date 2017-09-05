package org.oxbow.vixen.command

import com.vaadin.ui.Button
import com.vaadin.ui.MenuBar


object ComponentAdapter {
    def apply( component: AnyRef, action: (AnyRef) => Unit ): Option[ComponentAdapter[_]] = Option(component).map{
        case btn: Button          => ButtonAdapter( btn, action )
        case mn: MenuBar#MenuItem => MenuItemAdapter( mn, action )
    }
}

private[command] trait ComponentAdapter[T] extends CommandDefinition {
    val target: T
}


private case class ButtonAdapter(target: Button, action: AnyRef => Unit ) extends ComponentAdapter[Button] {

    target.addClickListener{ _ => if ( target.isEnabled ) action(target) }
    enabledProperty.addListener{ (_,_,newValue) => target.setEnabled(newValue) }
    captionProperty.addListener{ (_,_,newValue) => target.setCaption(newValue) }
    descriptionProperty.addListener{ (_,_,newValue) => target.setDescription(newValue) }
    iconProperty.addListener{ (_,_,newValue) => target.setIcon(newValue) }
    styleProperty.addListener{ (_,oldValue,newValue) =>
        target.removeStyleName(oldValue)
        target.addStyleName(newValue)
    }

}

private case class MenuItemAdapter(target: MenuBar#MenuItem, action: AnyRef => Unit ) extends ComponentAdapter[MenuBar#MenuItem] {

    target.setCommand((selectedItem: MenuBar#MenuItem) => action(selectedItem))
    enabledProperty.addListener{ (_,_,newValue) => target.setEnabled(newValue) }
    captionProperty.addListener{ (_,_,newValue) => target.setText(newValue) }
    descriptionProperty.addListener{ (_,_,newValue) => target.setDescription(newValue) }
    iconProperty.addListener{ (_,_,newValue) => target.setIcon(newValue) }

}
