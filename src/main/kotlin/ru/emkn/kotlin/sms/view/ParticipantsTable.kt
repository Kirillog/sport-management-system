package ru.emkn.kotlin.sms.view

import mu.KotlinLogging
import ru.emkn.kotlin.sms.ObjectFields
import ru.emkn.kotlin.sms.controller.Editor
import ru.emkn.kotlin.sms.model.Participant

private val logger = KotlinLogging.logger {}

class ParticipantsTable(participants: List<Participant>) : Table<Participant>() {

    override val header = TableHeader(
        listOf(
            TableColumn<Participant>("ID", ObjectFields.ID, true) { { it.id.toString() } },
            TableColumn<Participant>("Name", ObjectFields.Name, true) { { it.name } },
            TableColumn<Participant>("Surname", ObjectFields.Surname, true) { { it.surname } },
            TableColumn<Participant>("Group", ObjectFields.Group, true) { { it.group.name } },
            TableColumn<Participant>("Birthday Year", ObjectFields.BirthdayYear, true) { { it.birthdayYear.toString() } },
            TableColumn<Participant>("Grade", ObjectFields.Grade, true) { { it.grade ?: "" } },
            TableColumn<Participant>("Team", ObjectFields.Team, true) { { it.team.name } },
            TableColumn<Participant>("Start time", ObjectFields.StartTime, false) { { it.startTime.toString() } }
        )
    )

    inner class ParticipantTableRow(private var participant: Participant) : TableRow() {

        override val cells = header.makeTableCells(participant, ::saveChanges)

        override fun saveChanges() {
            Editor.editParticipant(participant, changes)
        }
    }

    override val rows = participants.map { ParticipantTableRow(it) }
}
