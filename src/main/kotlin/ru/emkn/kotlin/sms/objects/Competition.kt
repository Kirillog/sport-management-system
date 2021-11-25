package ru.emkn.kotlin.sms.objects

import ru.emkn.kotlin.sms.io.formEvent
import ru.emkn.kotlin.sms.io.formGroupsList
import ru.emkn.kotlin.sms.io.formTeamsList
import java.nio.file.Path
import java.time.LocalTime

fun makeCompetition(path: Path): Competition {
    return try {
        val event = formEvent(path)
        val teams = formTeamsList(path)
        val groups = formGroupsList(teams, path)
        Competition(event, path, teams, groups)
    } catch (error: Exception) {
        TODO()
    }
}

data class Competition(val event: Event, val path: Path, val teams: List<Team>, val groups: List<Group>) {

    constructor(competition: Competition) : this(
        competition.event,
        competition.path,
        competition.teams,
        competition.groups
    )

    constructor(path: Path) : this(makeCompetition(path))

    fun simpleToss(startTime: LocalTime, deltaMinutes: Long) {
        //TODO("подумать как получше реализовать id")
        var currentId = 100
        groups.forEach { group ->
            group.members.shuffled().forEach { participant ->
                participant.startTime = startTime.also { it.plusMinutes(deltaMinutes) }
                participant.id = currentId++
            }
        }
    }
}
