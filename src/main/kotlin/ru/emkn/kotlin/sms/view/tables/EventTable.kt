package ru.emkn.kotlin.sms.view.tables

import org.jetbrains.exposed.sql.transactions.transaction
import ru.emkn.kotlin.sms.ObjectFields
import ru.emkn.kotlin.sms.controller.Editor
import ru.emkn.kotlin.sms.model.Competition
import ru.emkn.kotlin.sms.model.Event
import ru.emkn.kotlin.sms.view.TopAppBar
import java.time.format.DateTimeFormatter

class EventTable : Table<Event>() {

    private val event
        get() = transaction { Event.all().toList() }

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
                getterGenerator = {
                    {
                        val pattern = "dd.MM.yyyy"
                        val formatter = DateTimeFormatter.ofPattern(pattern)
                        it.date.format(formatter)
                    }
                }
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

    override val rows: List<TableRow>
        get() = event.map { EventTableRow(it) }
}