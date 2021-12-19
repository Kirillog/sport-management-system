package ru.emkn.kotlin.sms.controller

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import ru.emkn.kotlin.sms.*
import ru.emkn.kotlin.sms.io.FileLoader
import ru.emkn.kotlin.sms.io.FileSaver
import ru.emkn.kotlin.sms.io.Loader
import ru.emkn.kotlin.sms.io.Saver
import ru.emkn.kotlin.sms.model.*
import java.io.File
import java.nio.file.Path
import kotlin.io.path.absolute
import kotlin.io.path.extension
import kotlin.io.path.nameWithoutExtension

enum class State {
    EMPTY,
    CREATED,
    ANNOUNCED,
    REGISTER_OUT,
    TOSSED,
    FINISHED
}

object CompetitionController {
    var state: State = State.EMPTY


    fun announceFromPath(event: Path?, checkpoints: Path?, routes: Path?) {
        event ?: throw IllegalArgumentException("event is not chosen")
        checkpoints ?: throw IllegalArgumentException("checkpoints are not chosen")
        routes ?: throw IllegalArgumentException("routes are not chosen")

        val eventLoader = getLoader(event)
        val checkPoints = getLoader(checkpoints)
        val routesLoader = getLoader(routes)
        announce(eventLoader, checkPoints, routesLoader)
    }

    private fun announce(eventLoader: Loader, checkpointsLoader: Loader, routesLoader: Loader) {
        require(state == State.CREATED)
        transaction {
            Competition.loadEvent(eventLoader)
            Competition.loadCheckpoints(checkpointsLoader)
            Competition.loadRoutes(routesLoader)
        }
        state = State.ANNOUNCED
    }

    fun registerFromPath(group: Path, team: Path) {
        val groupLoader = getLoader(group)
        val teamLoader = getLoader(team)
        register(groupLoader, teamLoader)
    }

    private fun register(groupLoader: Loader, teamLoader: Loader) {
        require(state == State.ANNOUNCED)
        transaction {
            Competition.loadGroups(groupLoader)
            Competition.loadTeams(teamLoader)
        }
        state = State.REGISTER_OUT
    }

    fun toss() {
        require(state == State.REGISTER_OUT)
        transaction {
            Competition.toss()
        }
        state = State.TOSSED
    }

    fun groupsAndTossFromPath(group: Path, toss: Path) {
        require(state == State.ANNOUNCED)
        state = State.TOSSED
        val groupLoader = getLoader(group)
        val tossLoader = getLoader(toss)
        transaction {
            Competition.loadGroups(groupLoader)
            Competition.toss(tossLoader)
            Competition.teams.addAll(Team.all().toSet())
        }
    }

    fun registerResultsFromPath(checkPoints: Path) {
        require(state == State.TOSSED)
        transaction {
            val checkPointLoader = getLoader(checkPoints)
            Competition.loadDump(checkPointLoader)
        }
        state = State.FINISHED
    }

    fun calculateResult() {
        require(state == State.FINISHED)
        transaction {
            Competition.calculateResult()
        }
    }

    private fun getLoader(path: Path): Loader {
        return when (path.extension) {
            "csv" -> FileLoader(path)
            "" -> FileLoader(path)
            else -> throw IllegalStateException("Unsupported file format for $path")
        }
    }

    private fun getSaver(path: Path): Saver {
        return when (path.extension) {
            "csv" -> FileSaver(path.toFile())
            else -> throw IllegalStateException("Unsupported file format for $path")
        }
    }

    fun saveResultsToPath(results: Path) = transaction {
        getSaver(results).saveResults()
    }

    fun saveTossToPath(toss: Path) = transaction {
        getSaver(toss).saveToss()
    }

    fun saveTeamResultsToPath(results: Path) = transaction {
        getSaver(results).saveTeamResults()
    }

    fun getControllerState() = state

    private fun getDBState(): State {
        var res: State = State.CREATED

        transaction {
            if (!Checkpoint.all().empty()) {
                res = State.ANNOUNCED
            } else if (!Group.all().empty()) {
                res = State.REGISTER_OUT
            } else if (!TossTable.selectAll().empty()) {
                res = State.TOSSED
            } else if (!PersonalResultTable.selectAll().empty()) {
                res = State.FINISHED
            }
        }

        state = res
        return res
    }

    fun createDB(file: File?) {
        if (file == null) throw IllegalArgumentException("File was not chosen")
        if (file.exists()) throw IllegalArgumentException("File already exists")
        file.createNewFile()
        connectDB(file)
    }

    fun connectDB(file: File?) {
        require(state == State.EMPTY)
        if (file == null) throw IllegalArgumentException("File wasn't chosen")
        if (!file.isFile) throw IllegalStateException("It must be a file, not a directory")
        if (!file.canRead()) throw IllegalStateException("File does not readable")
        if (!file.canWrite()) throw IllegalStateException("File does not writable")
        var fileName = file.toPath().toAbsolutePath().toString()
        if (fileName.takeLast(6) == ".mv.db") {
            fileName = fileName.dropLast(6)
        } else {
            throw IllegalArgumentException("File should has extension .mv.db")
        }
        Database.connect("$DB_HEADER:$fileName", driver = DB_DRIVER)

        transaction {
            DB_TABLES.forEach {
                SchemaUtils.create(it)
            }
        }
        getDBState()
    }
}


