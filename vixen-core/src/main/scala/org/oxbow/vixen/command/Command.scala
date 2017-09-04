package org.oxbow.vixen.command

import javafx.beans.property.{BooleanProperty, SimpleBooleanProperty, SimpleStringProperty, StringProperty}

import com.vaadin.ui.Button

import scala.collection.mutable

object Command {

    def apply( caption: String )( action: AnyRef => Unit ): Command = {
        val cmd = new Command {
            val perform: (AnyRef) => Unit = action
        }
        cmd.caption = caption
        cmd
    }
}


trait Command {

    private val assignedComponents = mutable.ListBuffer[ComponentAdapter[_]]()

    val perform: AnyRef => Unit

    // enabled property
    private val enabledProperty: BooleanProperty = new SimpleBooleanProperty(true)

    def enabled: Boolean = enabledProperty.get
    def enabled_=( value: Boolean ): Unit = enabledProperty.set(value)


    // caption property
    private var captionProperty: StringProperty = new SimpleStringProperty()

    def caption: String = captionProperty.get
    def caption_=( value: String ): Unit = captionProperty.set(value)


    /**
      * Assignes command to the given component
      * @param component
      */
    def bindTo(component: AnyRef ): Unit = {
        Option(component).map{
            case btn: Button => ButtonAdapter(btn, perform )
        }.foreach { adapter =>

            println("enabledProperty: " + enabledProperty.get)

            adapter.enabledProperty.bind(enabledProperty)
            adapter.captionProperty.bind(captionProperty)
            assignedComponents += adapter
        }
    }

    def unbind(component: AnyRef): Unit = {
        Option(component)
            .flatMap{ c => assignedComponents.find(_.component == c) }
            .foreach { adapter =>
                assignedComponents -= adapter
                adapter.enabledProperty.unbind()
                adapter.captionProperty.unbind()
            }
    }


}

private trait ComponentAdapter[T] {
    val component: T
    val action: AnyRef => Unit

    final val enabledProperty = new SimpleBooleanProperty(true)
    final val captionProperty = new SimpleStringProperty()
}

private case class ButtonAdapter( component: Button, action: AnyRef => Unit ) extends ComponentAdapter[Button] {

    component.addClickListener{ _ => if ( component.isEnabled ) action(component) }

    enabledProperty.addListener{ (_,_,newValue) =>
        println("enabled: " + newValue)
        component.setEnabled(newValue)
    }

    captionProperty.addListener{ (_,_,newValue) =>
        println("caption: " + newValue)
        component.setCaption(newValue)
    }

}
