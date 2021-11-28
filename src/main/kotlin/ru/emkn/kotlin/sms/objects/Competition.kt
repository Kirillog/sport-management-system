package ru.emkn.kotlin.sms.objects

import mu.KotlinLogging
import ru.emkn.kotlin.sms.Target
import ru.emkn.kotlin.sms.io.formEvent
import ru.emkn.kotlin.sms.io.formGroupsList
import ru.emkn.kotlin.sms.io.formTeamsList
import ru.emkn.kotlin.sms.io.formTossedGroups
import java.nio.file.Path
import java.time.LocalTime

private val logger = KotlinLogging.logger {}

fun makeCompetition(path: Path): Competition {
    val event = formEvent(path)
    val teams = formTeamsList(path)
    val groups = formGroupsList(teams, path)
    return Competition(event, path, teams, groups)
}

fun makeCompetitionFromStartingProtocol(path: Path): Competition {
    val event = formEvent(path)
    val groups = formTossedGroups(path)
    val teams = convertGroupsToTeams(groups)
    return Competition(event, path, teams, groups)
}

fun convertGroupsToTeams(groups: List<Group>): List<Team> =
    groups.flatMap { it.members }.groupBy { it.team }.map {
        Team(it.key, it.value)
    }

data class Competition(
    val event: Event,
    val path: Path,
    val teams: List<Team>,
    val groups: List<Group>,
) {

    constructor(competition: Competition) : this(
        competition.event,
        competition.path,
        competition.teams,
        competition.groups,
    )

    constructor(path: Path, target: Target) : this(
        when (target) {
            Target.TOSS ->
                makeCompetition(path)
            Target.PERSONAL_RESULT ->
                makeCompetitionFromStartingProtocol(path)
            else ->
                makeCompetitionFromStartingProtocol(path)
        }
    ) {
        logger.info { "Competition files read success" }
    }

    fun simpleToss(startTime: LocalTime, deltaMinutes: Long) {
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
