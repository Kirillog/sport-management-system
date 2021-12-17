package ru.emkn.kotlin.sms.view

import ru.emkn.kotlin.sms.model.Event

class EventTable(event: Event) : Table<Event>() {

    override val header = TableHeader(
        listOf(
            TableColumn<Event>("Название", "name") { { it.name } },
            TableColumn<Event>("Дата", "date") { { it.date.toString() } }
        )
    )

    inner class EventTableRow(event: Event) : TableRow() {
        override val cells = header.makeTableCells(event, ::saveChanges)

        override fun saveChanges() {
            TODO()
        }
    }

    override val rows: List<TableRow> = listOf(EventTableRow(event))
}