package ru.emkn.kotlin.sms.objects

import mu.KotlinLogging
import ru.emkn.kotlin.sms.Target
import ru.emkn.kotlin.sms.io.formEvent
import ru.emkn.kotlin.sms.io.formGroupsList
import ru.emkn.kotlin.sms.io.formTeamsList
import ru.emkn.kotlin.sms.io.formTossedGroups
import java.nio.file.Path
import java.time.LocalTime
import kotlin.random.Random

private val logger = KotlinLogging.logger {}

/**
 * The widest class that stores all the information about the competition
 */
data class Competition(
    val event: Event,
    val path: Path,
    val teams: List<Team>,
    val groups: List<Group>,
) {

    companion object {
        /**
         * Function to create an instance of the competition needed for the toss.
         * Needs all files from input folder
         */
        fun makeCompetition(path: Path): Competition {
            val event = formEvent(path)
            val teams = formTeamsList(path)
            val groups = formGroupsList(teams, path)
            return Competition(event, path, teams, groups)
        }

        private fun convertGroupsToTeams(groups: List<Group>): List<Team> =
            groups.flatMap { it.members }.groupBy { it.team }.map {
                Team(it.key, it.value)
            }

        /**
         * Function to create an instance of the competition needed for formation the results.
         * Needs all files from input folder and file with the toss
         */
        fun makeCompetitionFromStartingProtocol(path: Path): Competition {
            val event = formEvent(path)
            val groups = formTossedGroups(path)
            val teams = convertGroupsToTeams(groups)
            return Competition(event, path, teams, groups)
        }
    }

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
            group.members.shuffled(Random(0)).forEach { participant ->
                participant.startTime = currentTime
                currentTime = currentTime.plusMinutes(deltaMinutes)
                participant.id = currentId++
            }
        }
    }
}
