package ru.emkn.kotlin.sms.view.tables

import ru.emkn.kotlin.sms.ObjectFields
import ru.emkn.kotlin.sms.controller.Controller
import ru.emkn.kotlin.sms.controller.Deleter
import ru.emkn.kotlin.sms.controller.Editor
import ru.emkn.kotlin.sms.model.Checkpoint
import ru.emkn.kotlin.sms.view.View
import ru.emkn.kotlin.sms.view.PathChooser

class CheckpointTable : Table<Checkpoint>() {

    private val checkpoints: List<Checkpoint>
        get() = Checkpoint.all().toList()

    override val header = TableHeader(listOf(
            TableColumn<Checkpoint>(
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
    ), iconsBar = true)

    inner class CheckpointTableRow(private val checkpoint: Checkpoint) : TableRow() {

        override val id = checkpoint.id.value

        override val cells = header.makeTableCells(checkpoint, ::saveChanges)

        override fun saveChanges() {
            Editor.editCheckpoint(checkpoint, changes)
        }

        override fun deleteAction() {
            Deleter.deleteCheckpoint(id)
            state = State.Outdated
        }
    }

    override val creatingState = View.State.CreateCheckpoint
    override val loadAction = {
        val checkpointsFile = PathChooser("Choose checkpoints", ".csv", "Checkpoints").choose()
        Controller.loadCheckpoints(checkpointsFile?.toPath())
        state = State.Outdated
    }

    override val rows: List<TableRow>
        get() = checkpoints.map { CheckpointTableRow(it) }
}
