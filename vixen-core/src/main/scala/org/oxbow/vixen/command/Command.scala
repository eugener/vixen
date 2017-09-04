package org.oxbow.vixen.command

import javafx.beans.property.{BooleanProperty, SimpleBooleanProperty, SimpleStringProperty, StringProperty}

import com.vaadin.ui.Button

import scala.collection.mutable

object Command {

    def apply( caption: String )( perform: AnyRef => Unit ): Command = {
        val cmd = new Command {
            val action: (AnyRef) => Unit = perform
        }
        cmd.caption = caption
        cmd
    }
}


private[command] trait CommandDefinition {
    private[command] val action: AnyRef => Unit
    private[command] final val enabledProperty: BooleanProperty = new SimpleBooleanProperty(true)
    private[command] final var captionProperty: StringProperty = new SimpleStringProperty("")
}

trait Command extends CommandDefinition {

    private val assignedComponents = mutable.ListBuffer[ComponentAdapter[_]]()

    def enabled: Boolean = enabledProperty.get
    def enabled_=( value: Boolean ): Unit = enabledProperty.set(value)

    def caption: String = captionProperty.get
    def caption_=( value: String ): Unit = captionProperty.set(value)


    //TODO add more properties (icon, description, tooltip etc.)

    /**
      * Assignes command to the given component
      * @param component
      */
    def bindTo(component: AnyRef ): Unit = {
        Option(component).map{
            case btn: Button => ButtonAdapter(btn, action )
        }.foreach { adapter =>
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

