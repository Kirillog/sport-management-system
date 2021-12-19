package ru.emkn.kotlin.sms.view.tables

import org.jetbrains.exposed.sql.transactions.transaction
import ru.emkn.kotlin.sms.ObjectFields
import ru.emkn.kotlin.sms.controller.Editor
import ru.emkn.kotlin.sms.model.Participant
import ru.emkn.kotlin.sms.view.GUI
import ru.emkn.kotlin.sms.view.creators.ParticipantCreator

class ParticipantsTable : Table<Participant>() {

    private val participants
        get() = transaction { Participant.all() }

    override val header = TableHeader(listOf(
        TableColumn<Participant>(
            "ID",
            ObjectFields.ID,
            visible = true, readOnly = true,
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
            "Surname",
            ObjectFields.Surname,
            visible = true, readOnly = false,
            comparator = TableComparing.compareByString(ObjectFields.Surname),
            getterGenerator = { { it.surname } }
        ),
        TableColumn(
            "Group",
            ObjectFields.Group,
            visible = true, readOnly = false,
            comparator = TableComparing.compareByString(ObjectFields.Group),
            getterGenerator = { { it.group.name } }
        ),
        TableColumn(
            "Birthday Year",
            ObjectFields.BirthdayYear,
            visible = true, readOnly = false,
            comparator = TableComparing.compareByInt(ObjectFields.BirthdayYear),
            getterGenerator = { { it.birthdayYear.toString() } }
        ),
        TableColumn(
            "Grade",
            ObjectFields.Grade,
            visible = true, readOnly = false,
            comparator = TableComparing.compareByString(ObjectFields.Grade),
            getterGenerator = { { it.grade ?: "" } }
        ),
        TableColumn(
            "Team",
            ObjectFields.Team,
            visible = true, readOnly = false,
            comparator = TableComparing.compareByString(ObjectFields.Team),
            getterGenerator = { { it.team.name } }
        ),
        TableColumn(
            "Start time",
            ObjectFields.StartTime,
            visible = false, readOnly = false,
            comparator = TableComparing.compareByLocalTime(ObjectFields.StartTime),
            getterGenerator = { { it.startTime.toString() } }
        )
    ))

    inner class ParticipantTableRow(private val participant: Participant) : TableRow() {

        override val cells = header.makeTableCells(participant, ::saveChanges)

        override fun saveChanges() {
            Editor.editParticipant(participant, changes)
        }

        override fun deleteAction(id: Int) {
            Editor.deleteParticipant(id)
        }
    }

    override val rows
        get() = participants.map { ParticipantTableRow(it) }

    override val creatingState = GUI.State.CreateParticipant
}
