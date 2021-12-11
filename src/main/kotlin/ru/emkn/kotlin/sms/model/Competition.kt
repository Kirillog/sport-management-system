package ru.emkn.kotlin.sms.model

import mu.KotlinLogging
import java.time.LocalDate

private val logger = KotlinLogging.logger {}

/**
 * The widest class that stores all the information about the competition
 */
object Competition {

    var event = Event("", LocalDate.of(2020, 1, 1))
    var toss = Toss()
    val checkPoints: MutableSet<CheckPoint> = mutableSetOf()
    val routes: MutableSet<Route> = mutableSetOf()
    val teams: MutableSet<Team> = mutableSetOf()
    val groups: MutableSet<Group> = mutableSetOf()

    var dump = RuntimeDump()
}
