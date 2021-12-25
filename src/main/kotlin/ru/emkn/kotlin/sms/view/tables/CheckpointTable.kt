package ru.emkn.kotlin.sms.view.tables

import org.jetbrains.exposed.sql.transactions.transaction
import ru.emkn.kotlin.sms.ObjectFields
import ru.emkn.kotlin.sms.controller.CompetitionController
import ru.emkn.kotlin.sms.controller.Deleter
import ru.emkn.kotlin.sms.controller.Editor
import ru.emkn.kotlin.sms.model.Checkpoint
import ru.emkn.kotlin.sms.view.GUI
import ru.emkn.kotlin.sms.view.PathChooser

class CheckpointTable : Table<Checkpoint>() {

    private val checkpoints
        get() = transaction { Checkpoint.all().toList() }

    override val header = TableHeader(listOf(
        TableColumn<Checkpoint>(
            "ID",
            ObjectFields.ID,
            visible = false, readOnly = true,
            comparator = TableComparing.compareByInt(ObjectFields.ID),
            getterGenerator = { { it.id.toString() } }
        ),
        TableColumn(
            "Name",
            ObjectFields.Name,
            visible = true, readOnly = false,
            comparator = TableComparing.compareByString(ObjectFields.Name),
            getterGenerator = { { it.name } }
        ),
        TableColumn(
            "Weight",
            ObjectFields.Weight,
            visible = true, readOnly = false,
            comparator = TableComparing.compareByInt(ObjectFields.Weight),
            getterGenerator = { { it.weight.toString() } }
        )
    ), deleteButton = true)

    inner class CheckpointTableRow(private val checkpoint: Checkpoint) : TableRow() {

        override val id = checkpoint.id.value

        override val cells = header.makeTableCells(checkpoint, ::saveChanges)

        override fun saveChanges() {
            Editor.editCheckpoint(checkpoint, changes)
        }

        override fun deleteAction() {
            Deleter.deleteCheckpoint(id)
        }
    }

    override val creatingState = GUI.State.CreateCheckpoint
    override val loadAction: (GUI) -> Unit = {
        val checkpointsFile = PathChooser("Choose checkpoints", ".csv", "Checkpoints").choose()
        CompetitionController.loadCheckpoints(checkpointsFile?.toPath())
        it.reload()
    }

    override val rows: List<TableRow>
        get() = checkpoints.map { CheckpointTableRow(it) }
}
