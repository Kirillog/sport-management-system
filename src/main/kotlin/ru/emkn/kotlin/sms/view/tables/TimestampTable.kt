package ru.emkn.kotlin.sms.view.tables

import ru.emkn.kotlin.sms.ObjectFields
import ru.emkn.kotlin.sms.controller.Deleter
import ru.emkn.kotlin.sms.controller.Editor
import ru.emkn.kotlin.sms.model.Timestamp
import ru.emkn.kotlin.sms.view.GUI
import ru.emkn.kotlin.sms.view.creators.ItemCreator
import ru.emkn.kotlin.sms.view.creators.TimestampCreator

class TimestampTable : Table<Timestamp>() {

    private val timestamps
        get() = Timestamp.all()

    //TODO: решить конфликт имён
    override val header = TableHeader(listOf(
        TableColumn<Timestamp>(
        "ID",
            ObjectFields.ID,
            visible = false, readOnly = true,
            comparator = TableComparing.compareByInt(ObjectFields.ID),
            getterGenerator = { { it.id.toString() } }
        ),
        TableColumn(
        "Time",
            ObjectFields.Time,
            visible = true, readOnly = false,
            comparator = TableComparing.compareByLocalTime(ObjectFields.Time),
            getterGenerator = { { it.time.toString() } }
        ),
        TableColumn(
            "Checkpoint",
            ObjectFields.Name,
            visible = true, readOnly = false,
            comparator = TableComparing.compareByString(ObjectFields.Name),
            getterGenerator = { { it.checkpoint.toString() } }
        ),
        TableColumn(
            "Participant",
            ObjectFields.ID,
            visible = true, readOnly = false,
            comparator = TableComparing.compareByString(ObjectFields.ID),
            getterGenerator = { { it.participant.toString() }}
        )
    ))

    inner class TimestampTableRow(private val timestamp: Timestamp): TableRow() {

        override val cells = header.makeTableCells(timestamp, ::saveChanges)

        override fun saveChanges() {
            Editor.editTimestamp(timestamp, changes)
        }

        override fun deleteAction(id: Int) {
            Deleter.deleteTimestamp(id)
        }
    }

    override val rows: List<TableRow>
        get() = timestamps.map { TimestampTableRow(it) }

    override val creatingState = GUI.State.CreateTimestamp
}
