package ru.emkn.kotlin.sms.objects

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import mu.KotlinLogging
import ru.emkn.kotlin.sms.io.CSVReader
import java.io.File
import java.nio.file.Path
import java.time.LocalTime
import kotlin.random.Random

private val logger = KotlinLogging.logger {}

open class Toss {

    enum class State {
        PREPARING, TOSSED
    }

    var state = State.PREPARING
        protected set
    protected val participants = mutableSetOf<Participant>()
    val startTimeByParticipant = mutableMapOf<Participant, LocalTime>()

    private fun addParticipant(participant: Participant) {
        require(state == State.PREPARING)
        participants.add(participant)
    }

    private fun build(loader: Loader) {
        //TODO()
    }

    private fun addAllParticipant() {
        require(state == State.PREPARING)
        Participant.byId.values.forEach { this.addParticipant(it) }
    }

    private fun getParticipantStartTime(participant: Participant): LocalTime {
        require(state == State.TOSSED)
        return startTimeByParticipant.getOrElse(participant) {
            throw IllegalStateException("This participant ${participant.id} has not been tossed")
        }
    }

    fun build() {
        var currentId = 100
        var currentTime = LocalTime.NOON
        val deltaMinutes = 5L
        participants.groupBy { it.group }.forEach { (group, members) ->
            members.shuffled(Random(0)).forEach { participant ->
                startTimeByParticipant[participant] = currentTime
                currentTime = currentTime.plusMinutes(deltaMinutes)
                participant.id = currentId++
            }
        }
        state = State.TOSSED
    }
}


enum class CompetitionStates {
    ANNOUNCED,
    REGISTER_OUT,
    TOSSED,
    FINISHED
}

/**
 * The widest class that stores all the information about the competition
 */
data class Competition(var event: Event, var toss: Toss) {

    val checkPoints: MutableSet<CheckPoint> = mutableSetOf()
    val routes: MutableSet<Route> = mutableSetOf()
    val teams: MutableSet<Team> = mutableSetOf()
    val groups: MutableSet<Group> = mutableSetOf()

    val state: CompetitionStates = CompetitionStates.ANNOUNCED
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
