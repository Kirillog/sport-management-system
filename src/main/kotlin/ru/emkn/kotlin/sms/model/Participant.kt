package ru.emkn.kotlin.sms.model

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import ru.emkn.kotlin.sms.MAX_TEXT_FIELD_SIZE

import ru.emkn.kotlin.sms.io.SingleLineWritable
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter

object ParticipantTable : IntIdTable("participants") {
    val name: Column<String> = varchar("name", MAX_TEXT_FIELD_SIZE)
    val surname: Column<String> = varchar("surname", MAX_TEXT_FIELD_SIZE)
    val birthdayYear: Column<Int> = integer("birthdayYear")
    val grade: Column<String?> = varchar("grade", MAX_TEXT_FIELD_SIZE).nullable()

    val groupID: Column<EntityID<Int>> = reference("groups", GroupTable)
    val teamID: Column<EntityID<Int>> = reference("teams", TeamTable)

    val tossID: Column<Int> = integer("toss")
}

/**
 * Class created for every people in application lists.
 * Contain meta information from application lists and run result, if participant finished.
 */
class Participant(id: EntityID<Int>) : IntEntity(id), SingleLineWritable {
    companion object : IntEntityClass<Participant>(ParticipantTable) {

        fun create(
            name: String,
            surname: String,
            birthdayYear: Int,
            group: Group,
            team: Team,
            grade: String?
        ): Participant {
            return transaction {
                Participant.new {
                    this.name = name
                    this.surname = surname
                    this.birthdayYear = birthdayYear
                    this.group = group
                    this.team = team
                    this.grade = grade
                }
            }
        }

        fun create(
            name: String,
            surname: String,
            birthdayYear: Int,
            groupName: String,
            teamName: String,
            grade: String?
        ): Participant {
            return create(name, surname, birthdayYear, Group.findByName(groupName), Team.findByName(teamName), grade)
        }

        fun create(
            name: String,
            surname: String,
            birthdayYear: Int,
            groupName: String,
            teamName: String,
            startTime: LocalTime,
            grade: String?
        ): Participant {
            val participant = create(name, surname, birthdayYear, groupName, teamName, grade)
            participant.startTime = startTime
            return participant
        }

        private fun Duration.toIntervalString(): String =
            "${this.toHoursPart()}h ${this.toMinutesPart()}m ${this.toSecondsPart()}s"

        fun formatterParticipantForApplications(participant: Participant) = listOf(
            participant.name,
            participant.surname,
            participant.teamID,
            participant.birthdayYear,
            participant.grade,
        )

        fun formatterForPersonalResults(participant: Participant) = listOf(
            participant.positionInGroup.place,
            participant.id,
            participant.name,
            participant.surname,
            participant.birthdayYear,
            participant.grade,
            participant.startTime.format(DateTimeFormatter.ISO_LOCAL_TIME),
            participant.finishTime?.format(DateTimeFormatter.ISO_LOCAL_TIME),
            participant.positionInGroup.laggingFromLeader.toIntervalString()
        )
    }


    var name by ParticipantTable.name
    var surname by ParticipantTable.surname
    var birthdayYear by ParticipantTable.birthdayYear
    var grade: String? by ParticipantTable.grade
    private var groupID by ParticipantTable.groupID
    private var teamID by ParticipantTable.teamID
    var tossID by ParticipantTable.tossID

    var team: Team
        get() = transaction { Team[teamID] }
        set(team) {
            transaction {
                teamID = TeamTable.select { TeamTable.id eq team.id }.first()[TeamTable.id]
            }
        }
    var group: Group
        get() = transaction { Group[groupID] }
        set(group) {
            transaction {
                groupID = GroupTable.select { GroupTable.id eq group.id }.first()[GroupTable.id]
            }
        }

    var startTime: LocalTime
        get() = transaction {
            TossTable.select { (TossTable.tossID eq tossID) and (TossTable.participantID eq this@Participant.id) }
                .first()[TossTable.startTime]
        }
        set(time) {
            transaction {
                TossTable.select { (TossTable.tossID eq tossID) and (TossTable.participantID eq this@Participant.id) }
                    .first()[TossTable.startTime] = time
            }
        }

    val runTime: Duration
        get() = Duration.between(startTime, finishTime)

    val finishTime: LocalTime?
        get() = Competition.result.getParticipantFinishTime(this)

    val positionInGroup: Result.PositionInGroup
        get() = Competition.result.getPositionInGroup(this)

    fun change(
        name: String,
        surname: String,
        birthdayYear: Int,
        groupName: String,
        teamName: String,
        grade: String?
    ) {
        transaction {
            this@Participant.name = name
            this@Participant.surname = surname
            this@Participant.birthdayYear = birthdayYear
            this@Participant.grade = grade
        }
        this.team = Team.findByName(teamName)
        this.group = Group.findByName(groupName)
    }

    override fun toLine() =
        listOf(id, name, surname, birthdayYear, team, grade, startTime.format(DateTimeFormatter.ISO_LOCAL_TIME))
}
