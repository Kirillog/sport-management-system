package ru.emkn.kotlin.sms.view.tables

import ru.emkn.kotlin.sms.ObjectFields
import ru.emkn.kotlin.sms.model.Event
import ru.emkn.kotlin.sms.view.ItemCreator
import ru.emkn.kotlin.sms.view.TableColumn
import ru.emkn.kotlin.sms.view.TableHeader
import ru.emkn.kotlin.sms.view.creators.EventCreator

class EventTable(event: Event) : Table<Event>() {

    override val header = TableHeader(
        listOf(
            TableColumn<Event>(
                "Название",
                ObjectFields.Name, visible = true, readOnly = false,
                getterGenerator = { { it.name } }
            ),
            TableColumn<Event>(
                "Дата",
                ObjectFields.Date,
                visible = true, readOnly = false,
                getterGenerator = { { it.date.toString() } }
            )
        )
    )

    inner class EventTableRow(event: Event) : TableRow() {
        override val cells = header.makeTableCells(event, ::saveChanges)

        override fun saveChanges() {
            TODO()
        }
    }

    override val rows: List<TableRow> = listOf(EventTableRow(event))
    override val itemCreator = EventCreator()
}