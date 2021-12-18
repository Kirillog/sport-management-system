package ru.emkn.kotlin.sms.view.tables

import ru.emkn.kotlin.sms.ObjectFields
import ru.emkn.kotlin.sms.controller.Editor
import ru.emkn.kotlin.sms.model.Participant
import ru.emkn.kotlin.sms.view.TableColumn
import ru.emkn.kotlin.sms.view.TableHeader
import ru.emkn.kotlin.sms.view.creators.ParticipantCreator

class ParticipantsTable(participants: List<Participant>) : Table<Participant>() {

    override val header = TableHeader(listOf(
        TableColumn<Participant>(
            "ID",
            ObjectFields.ID,
            visible = true, readOnly = true,
            getterGenerator = { { it.id.toString() } }
        ),
        TableColumn(
            "Name",
            ObjectFields.Name,
            visible = true, readOnly = false,
            getterGenerator = { { it.name } }
        ),
        TableColumn(
            "Surname",
            ObjectFields.Surname,
            visible = true, readOnly = false,
            getterGenerator = { { it.surname } }
        ),
        TableColumn(
            "Group",
            ObjectFields.Group,
            visible = true, readOnly = false,
            getterGenerator = { { it.group.name } }
        ),
        TableColumn(
            "Birthday Year",
            ObjectFields.BirthdayYear,
            visible = true, readOnly = false,
            getterGenerator = { { it.birthdayYear.toString() } }
        ),
        TableColumn(
            "Grade",
            ObjectFields.Grade, visible = true, readOnly = false,
            getterGenerator = { { it.grade ?: "" } }
        ),
        TableColumn(
            "Team",
            ObjectFields.Team,
            visible = true, readOnly = false,
            getterGenerator = { { it.team.name } }
        ),
        TableColumn(
            "Start time",
            ObjectFields.StartTime,
            visible = false, readOnly = false,
            getterGenerator = { { it.startTime.toString() } }
        )
    ))

    inner class ParticipantTableRow(private var participant: Participant) : TableRow() {

        override val cells = header.makeTableCells(participant, ::saveChanges)

        override fun saveChanges() {
            Editor.editParticipant(participant, changes)
        }
    }

    override val rows = participants.map { ParticipantTableRow(it) }

    override val itemCreator = ParticipantCreator()
}
