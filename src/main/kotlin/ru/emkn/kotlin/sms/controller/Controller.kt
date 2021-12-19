package ru.emkn.kotlin.sms.controller

import org.jetbrains.exposed.sql.transactions.transaction
import ru.emkn.kotlin.sms.io.FileLoader
import ru.emkn.kotlin.sms.io.FileSaver
import ru.emkn.kotlin.sms.io.Loader
import ru.emkn.kotlin.sms.io.Saver
import ru.emkn.kotlin.sms.model.Competition
import ru.emkn.kotlin.sms.model.Team
import java.nio.file.Path
import kotlin.io.path.extension

enum class State {
    CREATED,
    ANNOUNCED,
    REGISTER_OUT,
    TOSSED,
    FINISHED
}

object CompetitionController {
    var state: State = State.CREATED


    fun announceFromPath(event: Path, checkpoints: Path, routes: Path) {
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
}

