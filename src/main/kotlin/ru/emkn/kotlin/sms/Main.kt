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
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.io.path.Path
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

    val writer = Writer(competition.path.resolve("protocols/results.csv").toFile(), FileType.CSV)
    writer.add(
        listOf(
            "Место", "Номер", "Имя", "Фамилия", "Г.р.", "Разр.", "Время старта", "Время финиша", "Отставание"
        )
    )
    groups.forEach { group ->
        writer.add(group.name)
        group.members.sortedBy { it.place?.number }.forEach { participant ->
            writer.add(participant, ::formatterForPersonalResults)
        }
    }
    writer.write()
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
            Target.TEAM_RESULT -> TODO()
        }

        logger.info { "Program successfully finished" }
    } catch (error: Exception) {
        logger.info { "Wow, that's a big surprise, program was fault" }
        logger.error { error.message }
    }
}
