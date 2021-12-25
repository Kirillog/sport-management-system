package ru.emkn.kotlin.sms.view.tables

import org.jetbrains.exposed.sql.transactions.transaction
import ru.emkn.kotlin.sms.ObjectFields
import ru.emkn.kotlin.sms.controller.CompetitionController
import ru.emkn.kotlin.sms.controller.Deleter
import ru.emkn.kotlin.sms.controller.Editor
import ru.emkn.kotlin.sms.model.Group
import ru.emkn.kotlin.sms.view.PathChooser

class GroupTable : Table<Group>() {

    private val group: List<Group>
        get() {
            return transaction { Group.all().toList() }
        }

    override val header = TableHeader<Group>(
        listOf(
            TableColumn<Group>(
                "Name",
                ObjectFields.Name, visible = true, readOnly = false,
                comparator = TableComparing.compareByString(ObjectFields.Name),
                getterGenerator = { { it.name } }
            ),
            TableColumn(
                "Result type",
                ObjectFields.ResultType, visible = true, readOnly = false,
                comparator = TableComparing.compareByString(ObjectFields.ResultType),
                getterGenerator = { { it.personalResult.toString() } }
            ),
            TableColumn(
                "Route name",
                ObjectFields.RouteName, visible = true, readOnly = false,
                comparator = TableComparing.compareByString(ObjectFields.RouteName),
                getterGenerator = { { it.route.name } }
            )
        ),
        deleteButton = true
    )

    inner class GroupTableRow(private val group: Group) : TableRow() {
        override val id = group.id.value
        override val cells = header.makeTableCells(group, ::saveChanges)

        override fun saveChanges() {
            Editor.editGroup(group, changes)
        }

        override fun deleteAction() {
            Deleter.deleteGroup(id)
            state = State.Outdated
        }
    }

    override val rows: List<TableRow>
        get() = group.map { GroupTableRow(it) }

    override val loadAction = {
        val selectedFile = PathChooser("Choose groups", ".csv", "Group").choose()
        CompetitionController.loadGroups(selectedFile?.toPath())
        state = State.Outdated
    }
}
