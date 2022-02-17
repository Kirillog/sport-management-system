package ru.emkn.kotlin.sms.model

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.batchInsert

object TeamResultTable : IntIdTable("team_results") {
    val teamID = reference("team", TeamTable)
    val score = long("score")
}


interface TeamResult {

    enum class State {
        PREPARING, COMPLETED
    }

    var state: State

    var score: Map<Team, Long>

    fun getScore(team: Team): Long {
        require(state == State.COMPLETED)
        return score[team] ?: 0
    }

    fun sortTeams(teams: Set<Team>): List<Team> =
        teams.sortedByDescending { score[it] }

    fun calculate()

    fun saveToDB() {
        TeamResultTable.batchInsert(score.toList(), ignore = false, shouldReturnGeneratedValues = false) { (team, score) ->
            this[TeamResultTable.teamID] = team.id
            this[TeamResultTable.score] = score
        }
    }
}