package ru.emkn.kotlin.sms.objects

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import mu.KotlinLogging
import ru.emkn.kotlin.sms.io.CSVReader
import java.io.File
import java.nio.file.Path

private val logger = KotlinLogging.logger {}

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

interface Loader {
    fun loadGroups(): Set<Group>
    fun loadTeams(): Set<Team>
    fun loadRoutes(): Set<Route>
    fun loadTimestamps(): Set<TimeStamp>
}

class FileLoader(path: Path) : Loader {
    private val reader = csvReader()
    private val file = path.toFile()

    override fun loadGroups(): Set<Group> =
        reader.open(file) {
            CSVReader(file, this).groups()
        } ?: throw IllegalArgumentException("Cannot read file ${file.name}")

    override fun loadRoutes(): Set<Route> =
        reader.open(file) {
            CSVReader(file, this).courses()
        } ?: throw IllegalArgumentException("Cannot read file ${file.name}")


    override fun loadTeams(): Set<Team> =
        file.walk().filter(File::isFile).map { file ->
            logger.debug { "Processing ${file.name}" }
            reader.open(file) {
                CSVReader(file, this).team()
            }
        }.filterNotNull().toSet()

    override fun loadTimestamps(): Set<TimeStamp> =
        file.walk().filter { it.isFile && it.extension == "csv" }.map { file ->
            logger.debug { "Processing ${file.name}" }
            reader.open(file) {
                CSVReader(file, this).timestamps()
            }
        }.filterNotNull().flatten().toSet()
}

/**
 * The widest class that stores all the information about the competition
 */
data class Competition(var event: Event) {

    val checkPoints: MutableSet<CheckPoint> = mutableSetOf()
    val routes: MutableSet<Route> = mutableSetOf()
    val teams: MutableSet<Team> = mutableSetOf()
    val groups: MutableSet<Group> = mutableSetOf()

    val state: CompetitionStates = CompetitionStates.ANNOUNCED
    val dump = RuntimeDump()

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
