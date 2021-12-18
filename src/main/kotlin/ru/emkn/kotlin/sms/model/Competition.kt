package ru.emkn.kotlin.sms.model

import ru.emkn.kotlin.sms.io.Loader
import java.time.LocalDate


/**
 * The widest class that stores all the information about the competition
 */
object Competition {

    var event = Event("Standard", LocalDate.of(2020, 1, 1))
    var toss = Toss()
    val checkpoints: MutableSet<Checkpoint> = mutableSetOf()
    val routes: MutableSet<Route> = mutableSetOf()
    val teams: MutableSet<Team> = mutableSetOf()
    val groups: MutableSet<Group> = mutableSetOf()

    var teamResult: TeamResult = TeamResultByAverageScore()
    var result: Result = ResultByTime()

    fun loadGroups(loader: Loader) {
        groups.addAll(loader.loadGroups())
    }

    fun loadTeams(loader: Loader) {
        teams.addAll(loader.loadTeams())
    }

    fun loadRoutes(loader: Loader) {
        routes.addAll(loader.loadRoutes())
    }

    fun add(route: Route) {
        routes.add(route)
    }

    fun add(team: Team) {
        teams.add(team)
    }

    fun add(group: Group) {
        groups.add(group)
    }

    fun add(participant: Participant) {}

    fun add(checkpoint: Checkpoint) {
        checkpoints.add(checkpoint)
    }

    fun calculateResult() {
        result.calculate()
        teamResult.calculate()
    }

    fun loadEvent(loader: Loader) {
        event = loader.loadEvent()
    }

    fun loadDump(loader: Loader) {
        RuntimeDump.addAllTimestamps(loader.loadTimestamps())
        checkpoints.addAll(RuntimeDump.timeStampDump.map { it.checkPoint })
    }

    fun toss() {
        toss.addAllParticipant()
        toss.build()
    }

    fun toss(loader: Loader) {
        toss.build(loader)
    }
}
