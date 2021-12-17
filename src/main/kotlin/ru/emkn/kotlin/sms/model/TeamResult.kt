package ru.emkn.kotlin.sms.model

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
}