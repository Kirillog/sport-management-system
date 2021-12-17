package ru.emkn.kotlin.sms.controller

import ru.emkn.kotlin.sms.io.*
import ru.emkn.kotlin.sms.model.Competition
import ru.emkn.kotlin.sms.model.Group
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


    fun announceFromPath(event: Path, routes: Path) {
        val eventLoader = getLoader(event)
        val routesLoader = getLoader(routes)
        announce(eventLoader, routesLoader)
    }

    private fun announce(eventLoader: Loader, routesLoader: Loader) {
        require(state == State.CREATED)
        Competition.loadEvent(eventLoader)
        Competition.loadRoutes(routesLoader)
        state = State.ANNOUNCED
    }

    fun registerFromPath(group: Path, team: Path) {
        val groupLoader = getLoader(group)
        val teamLoader = getLoader(team)
        register(groupLoader, teamLoader)
    }

    private fun register(groupLoader: Loader, teamLoader: Loader) {
        require(state == State.ANNOUNCED)
        Competition.loadGroups(groupLoader)
        Competition.loadTeams(teamLoader)
        state = State.REGISTER_OUT
    }

    fun toss() {
        require(state == State.REGISTER_OUT)
        Competition.toss()
        state = State.TOSSED
    }

    fun groupsAndTossFromPath(group: Path, toss: Path) {
        require(state == State.ANNOUNCED)
        val groupLoader = getLoader(group)
        val tossLoader = getLoader(toss)
        Competition.loadGroups(groupLoader)
        Competition.toss(tossLoader)
        Competition.teams.addAll(Team.all().toSet())
        state = State.TOSSED
    }

    fun registerResultsFromPath(checkPoints: Path) {
        require(state == State.TOSSED)
        val checkPointLoader = getLoader(checkPoints)
        Competition.loadDump(checkPointLoader)
        state = State.FINISHED
    }

    fun calculatePersonalResults() {
        require(state == State.FINISHED)
        Competition.calculateResult()
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

    fun saveResultsToPath(results: Path) =
        getSaver(results).saveResults()

    fun saveTossToPath(toss: Path) =
        getSaver(toss).saveToss()

    fun saveTeamResultsToPath(results: Path) =
        getSaver(results).saveTeamResults()

    fun saveToss(writer: Writer) {
        writer.add(listOf("Номер", "Имя", "Фамилия", "Г.р.", "Команда", "Разр.", "Время старта"))
        writer.addAll(Group.all().toList())
        writer.write()
    }
}
