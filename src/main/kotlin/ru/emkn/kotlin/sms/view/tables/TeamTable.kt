package ru.emkn.kotlin.sms.view.tables

import org.jetbrains.exposed.sql.transactions.transaction
import ru.emkn.kotlin.sms.ObjectFields
import ru.emkn.kotlin.sms.controller.Controller
import ru.emkn.kotlin.sms.controller.Deleter
import ru.emkn.kotlin.sms.controller.Editor
import ru.emkn.kotlin.sms.model.Team
import ru.emkn.kotlin.sms.view.GUI
import ru.emkn.kotlin.sms.view.PathChooser
import javax.swing.JFileChooser

class TeamTable : Table<Team>() {

    private val team: List<Team>
        get() {
            return transaction { Team.all().toList() }
        }

    override val header = TableHeader<Team>(
        listOf(
            TableColumn<Team>(
                "Name",
                ObjectFields.Name, visible = true, readOnly = false,
                comparator = TableComparing.compareByString(ObjectFields.Name),
                getterGenerator = { { it.name } }
            ),
            TableColumn<Team>(
                "Score",
                ObjectFields.ResultType, visible = false, readOnly = true,
                comparator = TableComparing.compareByLong(ObjectFields.ResultType),
                getterGenerator = { { it.score.toString() } }
            )
        ),
        deleteButton = true
    )

    inner class TeamTableRow(private val team: Team) : TableRow() {
        override val id = team.id.value
        override val cells = header.makeTableCells(team, ::saveChanges)

        override fun saveChanges() {
            Editor.editTeam(team, changes)
        }

        override fun deleteAction() {
            Deleter.deleteTeam(id)
            state = State.Outdated
        }
    }

    override val creatingState = GUI.State.CreateTeam

    override val rows: List<TableRow>
        get() = team.map { TeamTableRow(it) }

    override val loadAction = {
        val selectedFile = PathChooser("Choose application folder or single file", "", "Application folder").choose(
            JFileChooser.FILES_AND_DIRECTORIES
        )
        Controller.loadTeams(selectedFile?.toPath())
        state = State.Outdated
    }
}