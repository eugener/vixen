package org.oxbow.vixen.command

import javafx.beans.property._

import com.vaadin.server.Resource
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
    private[command] final val enabledProperty: BooleanProperty       = new SimpleBooleanProperty(true)
    private[command] final var captionProperty: StringProperty        = new SimpleStringProperty("")
    private[command] final var descriptionProperty: StringProperty    = new SimpleStringProperty("")
    private[command] final var iconProperty: ObjectProperty[Resource] = new SimpleObjectProperty[Resource]()
    private[command] final var styleProperty: StringProperty          = new SimpleStringProperty("")
}

trait Command extends CommandDefinition {

    private val assignedComponents = mutable.ListBuffer[ComponentAdapter[_]]()

    def enabled: Boolean = enabledProperty.get
    def enabled_=( value: Boolean ): Unit = enabledProperty.set(value)

    def caption: String = captionProperty.get
    def caption_=( value: String ): Unit = captionProperty.set(value)

    def description: String = descriptionProperty.get
    def description_=( value: String ): Unit = descriptionProperty.set(value)

    def icon: Resource = iconProperty.get
    def icon_=( value: Resource ): Unit = iconProperty.set(value)

    def style: String = styleProperty.get
    def style_=( value: String ): Unit = styleProperty.set(value)


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
            adapter.descriptionProperty.bind(descriptionProperty)
            adapter.iconProperty.bind(iconProperty)
            adapter.styleProperty.bind(styleProperty)
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
                adapter.descriptionProperty.unbind()
                adapter.iconProperty.unbind()
                adapter.styleProperty.unbind()
            }
    }


}

