package ru.emkn.kotlin.sms.view.tables

import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import ru.emkn.kotlin.sms.ObjectFields
import ru.emkn.kotlin.sms.controller.Deleter
import ru.emkn.kotlin.sms.controller.Editor
import ru.emkn.kotlin.sms.model.*
import ru.emkn.kotlin.sms.model.GroupTable
import ru.emkn.kotlin.sms.model.TeamTable
import ru.emkn.kotlin.sms.view.GUI
import java.time.LocalTime
import java.time.format.DateTimeFormatter

fun Participant.Companion.getPrint(): List<ParticipantPrint> {
    val teamById = TeamTable.selectAll().associate { it[TeamTable.id] to it[TeamTable.name] }
    val groupById = GroupTable.selectAll().associate { it[GroupTable.id] to it[GroupTable.name] }
    val startTimeById = TossTable.selectAll().associate { it[TossTable.participantID] to it[TossTable.startTime] }
    val resultById = PersonalResultTable.selectAll().associateBy { it[PersonalResultTable.participantID] }

    return Participant.all().map {
        ParticipantPrint(
            it.id.value,
            it.name,
            it.surname,
            it.birthdayYear,
            it.grade,
            groupById[it.groupID] ?: throw IllegalStateException("Team with id for participant doesnt exist"),
            teamById[it.teamID] ?: throw IllegalStateException("Group with id for participant doesnt exist"),
            startTimeById[it.id],
            resultById[it.id]?.get(PersonalResultTable.finishTime),
            resultById[it.id]?.get(PersonalResultTable.penalty),
            resultById[it.id]?.get(PersonalResultTable.placeInGroup),
            resultById[it.id]?.get(PersonalResultTable.deltaFromLeader),
            it
        )
    }
}

data class ParticipantPrint(
    val id: Int,
    val name: String,
    val surname: String,
    val birthdayYear: Int,
    val grade: String?,
    val groupName: String,
    val teamName: String,
    val startTime: LocalTime?,
    val finishTime: LocalTime?,
    val penalty: Int?,
    val placeInGroup: Int?,
    val deltaFromLeader: Int?,
    val entry: Participant
)

class ParticipantsTable : Table<ParticipantPrint>() {

    private val participants
        get() = transaction { Participant.getPrint() }

    private fun correctTime(time : LocalTime?) : String =
            if (time == null)
                "No time"
            else
                time.format(DateTimeFormatter.ISO_TIME)

    override val header = TableHeader(listOf(
        TableColumn<ParticipantPrint>(
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
            getterGenerator = { { it.groupName } }
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
            getterGenerator = { { it.teamName } }
        ),
        TableColumn(
            "Start time",
            ObjectFields.StartTime,
            visible = false, readOnly = false,
            comparator = TableComparing.compareByLocalTime(ObjectFields.StartTime),
            getterGenerator = { { correctTime(it.startTime) } }
        ),
        TableColumn(
            "Finish time",
            ObjectFields.FinishTime,
            visible = false, readOnly = false,
            comparator = TableComparing.compareByLocalTime(ObjectFields.FinishTime),
            getterGenerator = { { correctTime(it.finishTime) } }
        ),
        TableColumn(
            "Penalty",
            ObjectFields.Penalty,
            visible = false, readOnly = true,
            comparator = TableComparing.compareByInt(ObjectFields.Penalty),
            getterGenerator = { { it.penalty.toString() } }
        ),
        TableColumn(
            "Place in group",
            ObjectFields.PlaceInGroup,
            visible = false, readOnly = true,
            comparator = TableComparing.compareByInt(ObjectFields.PlaceInGroup),
            getterGenerator = { { it.placeInGroup.toString() } }
        ),
        TableColumn(
            "Delta from group leader",
            ObjectFields.DeltaFromLeader,
            visible = false, readOnly = true,
            comparator = TableComparing.compareByInt(ObjectFields.DeltaFromLeader),
            getterGenerator = { { it.deltaFromLeader.toString() } }
        )
    ), deleteButton = true)

    inner class ParticipantTableRow(private val participant: ParticipantPrint) : TableRow() {

        override val cells = header.makeTableCells(participant, ::saveChanges)

        override fun saveChanges() {
            Editor.editParticipant(participant.entry, changes)
        }

        override fun deleteAction() {
            Deleter.deleteParticipant(id)
            state = State.Outdated
        }

        override val id: Int = participant.id
    }

    override val rows
        get() = participants.map { ParticipantTableRow(it) }

    override val creatingState = GUI.State.CreateParticipant
    override var loadButton = false
}
