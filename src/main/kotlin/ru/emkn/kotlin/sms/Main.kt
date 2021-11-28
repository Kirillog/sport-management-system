package ru.emkn.kotlin.sms

//https://github.com/xenomachina/kotlin-argparser
//https://github.com/doyaaaaaken/kotlin-csv
//https://github.com/Kotlin/kotlinx-datetime

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.mainBody
import mu.KotlinLogging
import ru.emkn.kotlin.sms.io.Writer
import ru.emkn.kotlin.sms.io.formTimestamps
import ru.emkn.kotlin.sms.objects.*
import java.nio.file.Path
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.io.path.Path
import kotlin.math.max
import kotlin.time.ExperimentalTime
import kotlin.time.toKotlinDuration

private val logger = KotlinLogging.logger {}

fun getTimestampsByParticipant(groups: List<Group>, timestamps: List<TimeStamp>): Map<Participant, List<TimeStamp>> {
    val participantById = groups.flatMap { it.members }.associateBy { it.id ?: 0 }
    return timestamps.sortedBy { it.time }.groupBy { it.participantId }
        .mapKeys { participantById[it.key] ?: throw IllegalStateException() }
}

fun getCourseByParticipant(groups: List<Group>): Map<Participant, Course> =
    groups.flatMap { group -> group.members.associateWith { group.course }.toList() }.toMap()

fun getGroupByParticipant(groups: List<Group>) : Map<Participant, Group> {
    val groupByName = groups.associateBy {it.name}
    return groups.flatMap { it.members }.associateWith { groupByName[it.group] ?: throw IllegalStateException() }
}

fun fillTimestamps(groups: List<Group>, timestamps: List<TimeStamp>) {
    val timestampsByParticipant = getTimestampsByParticipant(groups, timestamps)
    groups.flatMap { it.members }.forEach {
        it.timeStamps = timestampsByParticipant[it]
        it.finishTime = it.timeStamps?.last()?.time
    }
}

fun getNotCheaters(groups: List<Group>): List<Participant> {
    val courseByParticipant = getCourseByParticipant(groups)
    return groups.flatMap { it.members }.filter { participant ->
        val course = courseByParticipant[participant]
        val isBanned = course?.checkPoints?.map { it.id } != participant.timeStamps?.map { it.checkPointId }
        if (isBanned) {
            logger.info {
                "Participant ${participant.id} ${participant.name} ${participant.surname} " +
                        "was disqualified for violating the course."
            }
        }
        !isBanned
    }
}

fun fillFinishData(participants: List<Participant>) {
    participants.groupBy { it.group }.forEach { (groupName, members) ->
        val sortedGroup = members.sortedByDescending { it.time }
        val leaderFinishTime = sortedGroup[0].time
        if (leaderFinishTime == null) {
            logger.info { "Not a single participant finished" }
            return@forEach
        }
        sortedGroup.forEachIndexed { place, participant ->
            val time = participant.time
            requireNotNull(time) { "Banned user cannot finish the competition" }
            participant.place = Participant.Place(
                place + 1,
                leaderFinishTime - participant.time
            )
        }
    }
}

@OptIn(ExperimentalTime::class)
private fun formatterForPersonalResults(participant: Participant) = listOf(
    participant.place?.number?.toString(),
    participant.id?.toString(),
    participant.name,
    participant.surname,
    participant.birthdayYear.toString(),
    participant.grade,
    participant.startTime?.format(DateTimeFormatter.ISO_LOCAL_TIME),
    participant.finishTime?.format(DateTimeFormatter.ISO_LOCAL_TIME),
    participant.place?.laggingFromLeader?.toKotlinDuration()?.toString()
)

fun personalResultsTarget(path: Path) {
    val competition = makeCompetitionFromStartingProtocol(path)
    val timestamps = formTimestamps(competition.path)
    val groups = competition.groups
    fillTimestamps(groups, timestamps)
    val trueParticipants = getNotCheaters(groups)
    fillFinishData(trueParticipants)
    sortGroupsByPlace(groups)

    val writer = Writer(competition.path.resolve("protocols/results.csv").toFile(), FileType.CSV)
    writer.add(
        listOf(
            "Место", "Номер", "Имя", "Фамилия", "Г.р.", "Разр.", "Время старта", "Время финиша", "Отставание"
        )
    )
    groups.forEach { group ->
        writer.add(group.name)
        group.members.forEach { participant ->
            writer.add(participant, ::formatterForPersonalResults)
        }
    }
    writer.write()
}

fun sortGroupsByPlace(groups : List<Group>) {
    groups.forEach { group ->
        val (banned, notBanned) = group.members.partition { it.place == null }
        group.members = notBanned.sortedBy { it.place?.number } + banned
    }
}

fun teamResultsTarget(path : Path) {
    val competition = makeCompetitionFromStartingProtocol(path)
    val timestamps = formTimestamps(competition.path)
    fillTimestamps(competition.groups, timestamps)
    val trueParticipants = getNotCheaters(competition.groups)
    fillFinishData(trueParticipants)
    sortGroupsByPlace(competition.groups)
    calculateResultsForTeams(competition)

    val writer = Writer(competition.path.resolve("protocols/teamResults.csv").toFile(), FileType.CSV)
    writer.add(
        listOf(
            "Место", "Команда", "Результат"
        )
    )
    competition.teams.sortedByDescending { it.result }.forEachIndexed { index, team ->
        writer.add(team) {
            listOf(listOf((index + 1).toString(), it.name, it.getResult().toString()))
        }
    }
    writer.write()
}

fun calculateResultsForTeams(
    competition: Competition,
) {
    val groupByParticipant = getGroupByParticipant(competition.groups)
    competition.teams.forEach { team ->
        team.result = team.members.sumOf {
            if (it.place == null)
                0
            else {
                val group = groupByParticipant[it]
                requireNotNull(group) { "Group of $it hasn't been found" }
                val groupLeaderResult = group.members[0].getDurationTime()
                val time = it.getDurationTime()
                max(0, (100 * (2 - time / groupLeaderResult)).toLong())
            }
        }
    }
}

operator fun Duration.div(other: Duration): Double {
    return this.seconds.toDouble() / other.seconds
}

fun tossTarget(competitionPath: Path) :Competition {
    val competition = Competition(competitionPath, Target.TOSS)
    competition.simpleToss(LocalTime.NOON, 5)
    val writer = Writer(competition.path.resolve("protocols/toss.csv").toFile(), FileType.CSV)

    writer.add(listOf("Номер", "Имя", "Фамилия", "Г.р.", "Команда", "Разр.", "Время старта"))
    writer.addAll(competition.groups)
    writer.write()
    return competition
}

fun main(args: Array<String>): Unit = mainBody {

    logger.info { "Program started" }
    val parsedArgs = ArgParser(args).parseInto(::ArgumentsFormat)
    val competitionPath = Path(parsedArgs.competitionsRoot).resolve(parsedArgs.competitionName)
    try {
        when (parsedArgs.target) {
            Target.TOSS -> tossTarget(competitionPath)
            Target.PERSONAL_RESULT -> personalResultsTarget(competitionPath)
            Target.TEAM_RESULT -> teamResultsTarget(competitionPath)
        }

        logger.info { "Program successfully finished" }
    } catch (error: Exception) {
        logger.info { "Wow, that's a big surprise, program was fault" }
        logger.error { error.message }
    }
}
