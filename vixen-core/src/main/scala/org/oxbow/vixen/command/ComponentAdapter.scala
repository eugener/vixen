package org.oxbow.vixen.command

import com.vaadin.ui.Button
import com.vaadin.ui.MenuBar


object ComponentAdapter {
    def apply( component: AnyRef ): Option[ComponentAdapter[_]] = Option(component).map{
        case btn: Button          => ButtonAdapter(btn)
        case mn: MenuBar#MenuItem => MenuItemAdapter(mn)
    }
}

private[command] trait ComponentAdapter[T] extends CommandDefinition {
    val target: T
    def initAction( action: (AnyRef) => Unit ): Unit
}


private case class ButtonAdapter( target: Button ) extends ComponentAdapter[Button] {

    enabledProperty.addListener{ (_,_,newValue) => target.setEnabled(newValue) }
    captionProperty.addListener{ (_,_,newValue) => target.setCaption(newValue) }
    descriptionProperty.addListener{ (_,_,newValue) => target.setDescription(newValue) }
    iconProperty.addListener{ (_,_,newValue) => target.setIcon(newValue) }
    styleProperty.addListener{ (_,oldValue,newValue) =>
        target.removeStyleName(oldValue)
        target.addStyleName(newValue)
    }

    def initAction( action: (AnyRef) => Unit ): Unit = {
        Option(action).foreach{ ac =>
            target.addClickListener{ _ => if ( target.isEnabled ) ac(target) }
        }
    }

}

private case class MenuItemAdapter(target: MenuBar#MenuItem) extends ComponentAdapter[MenuBar#MenuItem] {

    enabledProperty.addListener{ (_,_,newValue) => target.setEnabled(newValue) }
    captionProperty.addListener{ (_,_,newValue) => target.setText(newValue) }
    descriptionProperty.addListener{ (_,_,newValue) => target.setDescription(newValue) }
    iconProperty.addListener{ (_,_,newValue) => target.setIcon(newValue) }

    def initAction( action: (AnyRef) => Unit ): Unit = {
        Option(action).foreach{ ac =>
            target.setCommand((selectedItem: MenuBar#MenuItem) => ac(selectedItem))
        }
    }

}
