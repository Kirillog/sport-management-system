package ru.emkn.kotlin.sms.controller

import ru.emkn.kotlin.sms.io.Loader
import ru.emkn.kotlin.sms.model.*
import ru.emkn.kotlin.sms.io.Writer

enum class State {
    CREATED,
    ANNOUNCED,
    REGISTER_OUT,
    TOSSED,
    FINISHED
}

object CompetitionController {
    var state: State = State.CREATED

    fun announce(eventLoader: Loader, routesLoader: Loader) {
        require(state == State.CREATED)
//        Competition.loadEvent(eventLoader)
//        Competition.loadRoutes(routesLoader)
        state = State.ANNOUNCED
    }

    fun saveToss(writer: Writer) {
        writer.add(listOf("Номер", "Имя", "Фамилия", "Г.р.", "Команда", "Разр.", "Время старта"))
        writer.addAll(Group.byName.values.toList())
        writer.write()
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
        Competition.dump.addAllTimestamps(loader.loadTimestamps())
    }
}