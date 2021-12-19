package ru.emkn.kotlin.sms.view.tables

import ru.emkn.kotlin.sms.ObjectFields
import ru.emkn.kotlin.sms.model.Event
import ru.emkn.kotlin.sms.view.creators.EventCreator

class EventTable(event: Event) : Table<Event>() {

    override val header = TableHeader(
        listOf(
            TableColumn<Event>(
                "Название",
                ObjectFields.Name, visible = true, readOnly = false,
                comparator = TableComparing.compareByString(ObjectFields.Name),
                getterGenerator = { { it.name } }
            ),
            TableColumn<Event>(
                "Дата",
                ObjectFields.Date,
                visible = true, readOnly = false,
                comparator = TableComparing.compareByLocalDate(ObjectFields.Date),
                getterGenerator = { { it.date.toString() } }
            )
        )
    )

    inner class EventTableRow(event: Event) : TableRow() {
        override val cells = header.makeTableCells(event, ::saveChanges)

        override fun saveChanges() {
            TODO()
        }

        override fun deleteAction(id: Int) {
            TODO()
        }
    }

    override val rows: List<TableRow> = listOf(EventTableRow(event))
    override val itemCreator = EventCreator()
}