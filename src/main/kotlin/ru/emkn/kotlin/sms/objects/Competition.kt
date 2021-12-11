package ru.emkn.kotlin.sms.objects

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import mu.KotlinLogging
import ru.emkn.kotlin.sms.io.CSVReader
import java.io.File
import java.nio.file.Path
import java.time.LocalTime

private val logger = KotlinLogging.logger {}

abstract class Toss {
    val participants = mutableSetOf<Participant>()
    val startTimeByParticipant = mutableMapOf<Participant, LocalTime>()

    fun getParticipantStartTime(participant: Participant): LocalTime

    fun addParticipant(participant: Participant)

    fun addAllParticipant() {
        Participant.byId.values.forEach { this.addParticipant(it) }
    }

    fun completeToss()
}

class SimpleToss() : Toss {

    override val participants = mutableSetOf<Participant>()

    override val startTimeByParticipant = mutableMapOf<Participant, LocalTime> ()

    enum class State {
        PREPARING, TOSSED
    }

    var state = State.PREPARING

    override fun completeToss() {

        state = State.TOSSED
    }

    override fun addParticipant(participant: Participant) {
        require(state == State.PREPARING)
        participants.add(participant)
    }

    override fun getParticipantStartTime(participant: Participant): LocalTime {
        require(state == State.TOSSED)
        return startTimeByParticipant.getOrElse(participant) {
            throw IllegalStateException("This participant ${participant.id} has not been tossed")
        }
    }
}

class RuntimeDump() {

    fun addTimestamp(timeStamp: TimeStamp) {
        TODO("Add checkpoint to checkPointDump")
    }

    fun addAllTimestamps(timeStamps: Set<TimeStamp>) {
        TODO("Add checkpoint to checkPointDump")
    }

    fun completeDump() {
        TODO("fill participantDump by checkPointDump")
        //TODO("А ещё лучше автоматически добавлять сразу в addCheckpoint")
    }

    val checkPointDump: MutableMap<CheckPoint, List<TimeStamp>> = mutableMapOf()
    val participantDump: Map<Participant, List<TimeStamp>> = mapOf()
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
