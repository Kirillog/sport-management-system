package ru.emkn.kotlin.sms.view.tables

import ru.emkn.kotlin.sms.ObjectFields
import ru.emkn.kotlin.sms.controller.Editor
import ru.emkn.kotlin.sms.model.Event
import ru.emkn.kotlin.sms.view.TopAppBar
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

    inner class EventTableRow(private val event: Event) : TableRow() {
        override val cells = header.makeTableCells(event, ::saveChanges)

        override fun saveChanges() {
            Editor.editEvent(event, changes)
        }

        override fun deleteAction(id: Int) {
            TopAppBar.setMessage("You cant delete event metadata. Only change")
        }
    }

    override val rows: List<TableRow> = listOf(EventTableRow(event))
    override val itemCreator = EventCreator()
}