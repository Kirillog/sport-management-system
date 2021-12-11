package ru.emkn.kotlin.sms.objects

import mu.KotlinLogging
import java.time.LocalDate

private val logger = KotlinLogging.logger {}

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

    var event = Event("", LocalDate.of(2020, 1, 1))
    var toss = Toss()
    val checkPoints: MutableSet<CheckPoint> = mutableSetOf()
    val routes: MutableSet<Route> = mutableSetOf()
    val teams: MutableSet<Team> = mutableSetOf()
    val groups: MutableSet<Group> = mutableSetOf()

    var dump = RuntimeDump()

    fun loadGroups(loader: Loader) {
        groups.addAll(loader.loadGroups())
    }

    fun loadTeams(loader: Loader) {
        teams.addAll(loader.loadTeams())
    }

    fun loadRoutes(loader: Loader) {
        routes.addAll(loader.loadRoutes())
    }

    fun loadDump(loader: Loader) {
        dump.addAllTimestamps(loader.loadTimestamps())
    }
}
