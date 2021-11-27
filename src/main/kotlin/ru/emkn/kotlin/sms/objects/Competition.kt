package ru.emkn.kotlin.sms.objects

import mu.KotlinLogging
import ru.emkn.kotlin.sms.io.formEvent
import ru.emkn.kotlin.sms.io.formGroupsList
import ru.emkn.kotlin.sms.io.formTeamsList
import java.nio.file.Path
import java.time.LocalTime

private val logger = KotlinLogging.logger {}

fun makeCompetition(path: Path): Competition {
    val event = formEvent(path)
    val teams = formTeamsList(path)
    val groups = formGroupsList(teams, path)
    return Competition(event, path, teams, groups)
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
        var currentTime = startTime
        groups.forEach { group ->
            group.members.shuffled().forEach { participant ->
                participant.startTime = currentTime
                currentTime = currentTime.plusMinutes(deltaMinutes)
                participant.id = currentId++
            }
        }
    }
}
