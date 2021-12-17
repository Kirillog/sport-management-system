package ru.emkn.kotlin.sms.model

import java.time.Duration
import kotlin.math.max

class TeamResultByAverageScore : TeamResult {
    override var state: TeamResult.State = TeamResult.State.PREPARING

    override var score: Map<Team, Long> = mapOf()

    override fun calculate() {
        val tempScore: MutableMap<Team, Long> = mutableMapOf()
        Competition.teams.forEach { team ->
            tempScore[team] = team.members.sumOf {
                if (it.positionInGroup.place == 0)
                    0
                else {
                    val group = Competition.result.sortMembersIn(it.group)
                    val groupLeaderResult = group.first().runTime
                    val time = it.runTime
                    max(0L, (100 * (2 - time / groupLeaderResult)).toLong())
                }
            }
        }
        score = tempScore
        state = TeamResult.State.COMPLETED
    }
}

operator fun Duration.div(other: Duration): Double {
    return this.seconds.toDouble() / other.seconds
}
