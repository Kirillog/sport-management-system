package ru.emkn.kotlin.sms.model

import ru.emkn.kotlin.sms.io.Loader
import java.time.LocalDate

enum class CompetitionStates {
    CREATED,
    ANNOUNCED,
    REGISTER_OUT,
    TOSSED,
    FINISHED
}

/**
 * The widest class that stores all the information about the competition
 */
object Competition {
    val state: CompetitionStates = CompetitionStates.CREATED

    var event = Event("", LocalDate.of(0, 0, 0))
    var toss = Toss()
    val checkPoints: MutableSet<CheckPoint> = mutableSetOf()
    val routes: MutableSet<Route> = mutableSetOf()
    val teams: MutableSet<Team> = mutableSetOf()
    val groups: MutableSet<Group> = mutableSetOf()
    var result: Result = ResultByTime()

    fun loadGroups(loader: Loader) {
        groups.addAll(loader.loadGroups())
    }

    fun loadTeams(loader: Loader) {
        teams.addAll(loader.loadTeams())
    }

    fun loadRoutes(loader: Loader) {
        routes.addAll(loader.loadRoutes())
        checkPoints.addAll(routes.flatMap { it.checkPoints })
    }

    fun loadDump(loader: Loader) {
        RuntimeDump.addAllTimestamps(loader.loadTimestamps())
    }

}