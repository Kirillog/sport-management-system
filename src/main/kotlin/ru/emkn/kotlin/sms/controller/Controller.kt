package ru.emkn.kotlin.sms.controller

import ru.emkn.kotlin.sms.io.FileLoader
import ru.emkn.kotlin.sms.io.Loader
import ru.emkn.kotlin.sms.io.Writer
import ru.emkn.kotlin.sms.model.Competition
import ru.emkn.kotlin.sms.model.Group
import ru.emkn.kotlin.sms.model.RuntimeDump
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

    fun announce(eventLoader: Loader, routesLoader: Loader) {
        require(state == State.CREATED)
        loadEvent(eventLoader)
        loadRoutes(routesLoader)
        state = State.ANNOUNCED
    }

    fun registerFromPath(group: Path, team: Path) {
        val groupLoader = getLoader(group)
        val teamLoader = getLoader(team)
        register(groupLoader, teamLoader)
    }

    fun register(groupLoader: Loader, teamLoader: Loader) {
        require(state == State.ANNOUNCED)
        loadGroups(groupLoader)
        loadTeams(teamLoader)
        state = State.REGISTER_OUT
    }

    fun toss() {
        require(state == State.REGISTER_OUT)
        Competition.toss.addAllParticipant()
        Competition.toss.build()
        state = State.TOSSED
    }

    private fun getLoader(path: Path): Loader {
        return when(path.extension) {
            "csv" -> FileLoader(path)
            else -> throw IllegalStateException("Unsupported file format for $path")
        }
    }

    fun loadGroups(loader: Loader) {
        Competition.groups.addAll(loader.loadGroups())
    }

    fun loadTeams(loader: Loader) {
        Competition.teams.addAll(loader.loadTeams())
    }

    fun loadRoutes(loader: Loader) {
        Competition.routes.addAll(loader.loadRoutes())
    }

    fun loadDump(loader: Loader) {
        RuntimeDump.addAllTimestamps(loader.loadTimestamps())
    }

    fun loadEvent(loader: Loader) {
        Competition.event = loader.loadEvent()
    }

    fun saveToss(writer: Writer) {
        writer.add(listOf("Номер", "Имя", "Фамилия", "Г.р.", "Команда", "Разр.", "Время старта"))
        writer.addAll(Group.byName.values.toList())
        writer.write()
    }

}