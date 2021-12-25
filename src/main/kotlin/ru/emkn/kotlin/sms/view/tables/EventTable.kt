package ru.emkn.kotlin.sms.view.tables

import org.jetbrains.exposed.sql.transactions.transaction
import ru.emkn.kotlin.sms.ObjectFields
import ru.emkn.kotlin.sms.controller.CompetitionController
import ru.emkn.kotlin.sms.controller.Editor
import ru.emkn.kotlin.sms.model.Event
import ru.emkn.kotlin.sms.view.GUI
import ru.emkn.kotlin.sms.view.PathChooser
import java.time.format.DateTimeFormatter

class EventTable : Table<Event>() {

    private val event: List<Event>
        get() {
            return transaction { Event.all().toList() }
        }

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
        ),
        deleteButton = false,
        filtering = false
    )

    inner class EventTableRow(private val event: Event) : TableRow() {

        override val id = event.id.value
        override val cells = header.makeTableCells(event, ::saveChanges)

        override fun saveChanges() {
            Editor.editEvent(event, changes)
        }
    }

    override var addButton: Boolean
        get() = rows.isEmpty()
        set(value) {}

    override val creatingState: GUI.State = GUI.State.CreateEvent

    override val rows: List<TableRow>
        get() = event.map { EventTableRow(it) }

    override val loadAction = {
        val eventFile = PathChooser("Choose event", ".csv", "Event").choose()
        CompetitionController.loadEvent(eventFile?.toPath())
        state = State.Outdated
    }
}