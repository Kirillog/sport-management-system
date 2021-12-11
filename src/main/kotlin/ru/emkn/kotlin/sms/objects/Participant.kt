package ru.emkn.kotlin.sms.objects

import java.time.LocalTime

/**
 * Class created for every people in application lists.
 * Contain meta information from application lists and run result, if participant finished.
 */
class Participant(
    val name: String,
    val surname: String,
    val birthdayYear: Int,
    group: String,
    team: String,
    val grade: String? = null
) {

    val group = Group.byName[group] ?: throw IllegalArgumentException("Can not find group $group")
    val team = Team.byName[team] ?: throw IllegalArgumentException("Can not find team $team")

    var startTime: LocalTime
        get() = Competition.toss.getParticipantStartTime(this)
        set(time) {
            Competition.toss.startTimeByParticipant[this] = time
        }

    init {
        this.group.members.add(this)
        this.team.members.add(this)
    }

    companion object {
        private var nextFreeId = 100
        val byId: MutableMap<Int, Participant> = mutableMapOf()
    }

    var id = nextFreeId++.also { byId[it] = this }
}