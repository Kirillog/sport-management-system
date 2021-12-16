package ru.emkn.kotlin.sms.model

import java.time.Duration
import java.time.LocalTime

/**
 * Class created for every people in application lists.
 * Contain meta information from application lists and run result, if participant finished.
 */
class Participant {
    val name: String
    val surname: String
    val birthdayYear: Int
    val grade: String?
    val group: Group
    val team: Team
    val id: Int

    var startTime: LocalTime
        get() = Competition.toss.getParticipantStartTime(this)
        set(time) {
            Competition.toss.startTimeByParticipant[this] = time
        }

    val runTime: Duration
        get() = Duration.between(finishTime, startTime)

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
        grade: String? = null,
        id: Int,
        startTime: LocalTime
    ) {
        this.name = name
        this.surname = surname
        this.birthdayYear = birthdayYear
        this.grade = grade
        this.id = id
        this.group = Group.byName[group] ?: throw IllegalArgumentException("Can not find group $group")
        this.group.members.add(this)
        this.team = Team.byName[team] ?: throw IllegalArgumentException("Can not find team $team")
        this.team.members.add(this)
    }

    companion object {
        private var nextFreeId = 100
        val byId: MutableMap<Int, Participant> = mutableMapOf()
    }

}