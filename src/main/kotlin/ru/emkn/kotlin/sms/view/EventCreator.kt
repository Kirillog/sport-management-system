package ru.emkn.kotlin.sms.view

import mu.KotlinLogging
import ru.emkn.kotlin.sms.model.Event

private val logger = KotlinLogging.logger {}

class EventCreator : ItemCreator<Event>() {

    override val fields = listOf(
        ItemCreatorField("Название", "name"),
        ItemCreatorField("Дата", "date")
    )
    
    override fun create() {
        logger.info { changes }
        // TODO()
        // createEventFrom(changes)
    }

}