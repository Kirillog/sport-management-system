package ru.emkn.kotlin.sms.model

import ru.emkn.kotlin.sms.io.SingleLineWritable
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * Class created for every people in application lists.
 * Contain meta information from application lists and run result, if participant finished.
 */
class Participant : SingleLineWritable {
    var name: String
    var surname: String
    var birthdayYear: Int
    var grade: String?
    var group: Group
    var team: Team
    val id: Int

    var startTime: LocalTime
        get() = Competition.toss.getParticipantStartTime(this)
        set(time) {
            Competition.toss.startTimeByParticipant[this] = time
        }

    val runTime: Duration
        get() = Duration.between(startTime, finishTime)

    val finishTime: LocalTime?
        get() = Competition.result.getParticipantFinishTime(this)

    val positionInGroup: Result.PositionInGroup
        get() = Competition.result.getPositionInGroup(this)

    constructor(
        name: String,
        surname: String,
        birthdayYear: Int,
        group: String,
        team: String,
        grade: String? = null
    ) {
        this.name = name
        this.surname = surname
        this.birthdayYear = birthdayYear
        this.grade = grade
        this.group = Group.byName[group] ?: throw IllegalArgumentException("Can not find group $group")
        this.group.members.add(this)
        this.team = Team.byName[team] ?: throw IllegalArgumentException("Can not find team $team")
        this.team.members.add(this)
        id = nextFreeId++.also { byId[it] = this }
    }

    constructor(
        name: String,
        surname: String,
        birthdayYear: Int,
        group: String,
        team: String,
        participantId: Int,
        startTime: LocalTime,
        grade: String? = null
    ) {
        this.name = name
        this.surname = surname
        this.birthdayYear = birthdayYear
        this.grade = grade
        this.id = participantId.also { byId[it] = this }
        this.group = Group.byName[group] ?: throw IllegalArgumentException("Can not find group $group")
        this.group.members.add(this)
        this.team = Team.byName[team] ?: Team(team)
        this.team.members.add(this)
        this.startTime = startTime
    }

    companion object {
        private var nextFreeId = 100
        val byId: MutableMap<Int, Participant> = mutableMapOf()
    }

    fun change(
        name: String,
        surname: String,
        birthdayYear: Int,
        group: String,
        team: String,
        grade: String?
    ) {
        this.name = name
        this.surname = surname
        this.birthdayYear = birthdayYear
        this.grade = grade
        if (group != this.group.name) {
            this.group.members.remove(this)
            this.group = Group.byName[group] ?: throw IllegalArgumentException("Can not find group $group")
        }
        if (team != this.team.name) {
            this.team.members.remove(this)
            this.team = Team.byName[team] ?: throw IllegalArgumentException("Can not find team $team")
        }
    }

    override fun toLine() =
        listOf(id, name, surname, birthdayYear, team, grade, startTime.format(DateTimeFormatter.ISO_LOCAL_TIME))
}

fun Duration.toIntervalString(): String = "${this.toHoursPart()}h ${this.toMinutesPart()}m ${this.toSecondsPart()}s"

/**
 * Declare output format for participant used by [ru.emkn.kotlin.sms.io.Writer]
 */

fun formatterParticipantForApplications(participant: Participant) = listOf(
    participant.name,
    participant.surname,
    participant.team,
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