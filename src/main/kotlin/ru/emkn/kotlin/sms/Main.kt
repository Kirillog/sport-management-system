package ru.emkn.kotlin.sms

//https://github.com/xenomachina/kotlin-argparser
//https://github.com/doyaaaaaken/kotlin-csv
//https://github.com/Kotlin/kotlinx-datetime

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.mainBody
import kotlinx.datetime.toJavaLocalDate
import mu.KotlinLogging
import ru.emkn.kotlin.sms.io.Writer
import ru.emkn.kotlin.sms.io.formTimestamps
import ru.emkn.kotlin.sms.io.formTossedGroups
import ru.emkn.kotlin.sms.objects.*
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.io.path.Path

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
    groups.flatMap {it.members}.forEach { it.timeStamps = timestampsByParticipant[it] }
}

fun getNotCheaters(groups: List<Group>): List<Participant> {
    val courseByParticipant = getCourseByParticipant(groups)
    return groups.flatMap { it.members }.filter { participant ->
        val course = courseByParticipant[participant]
        val isBanned = course?.checkPoints != participant.timeStamps?.map { it.checkPointId }
        if (isBanned) {
            logger.info {
                "Participant ${participant.id} ${participant.name} ${participant.surname} " +
                        "was disqualified for violating the course."
            }
        }
        !isBanned
    }
}

fun fillFinishData(participants: List<Participant>, competition: Competition) {
    val finishTimeByParticipant = participants.associateWith {
        it.timeStamps?.last()?.time ?: throw IllegalStateException("Lost time for finished not banned participant")
    }
    participants.groupBy { it.group }.forEach { (groupName, members) ->
        val sortedGroup = members.sortedBy { finishTimeByParticipant[it] }
        val leaderFinish = members[0].finishData
        if (leaderFinish == null) {
            logger.info { "Not a single participant finished" }
            return@forEach
        }
        sortedGroup.forEachIndexed { place, participant ->
            val time = finishTimeByParticipant.getOrElse(participant) {
                throw IllegalStateException("Can not found time for participant")
            }
            val date = competition.event.date.toJavaLocalDate()
            participant.finishData = Participant.FinishData(
                time, place,
                Duration.between(leaderFinish.time.atDate(date), time.atDate(date))
            )
        }
    }
}

private fun formatterForPersonalResults(participant: Participant) = listOf(
    participant.finishData?.place?.toString(),
    participant.id?.toString(),
    participant.name,
    participant.surname,
    participant.birthdayYear.toString(),
    participant.grade,
    participant.startTime?.format(DateTimeFormatter.ISO_LOCAL_TIME),
    participant.finishData?.time?.format(DateTimeFormatter.ISO_LOCAL_TIME),
    participant.finishData?.laggingFromLeader?.toString()
)

fun personalResultsTarget(competition: Competition) {
    val groups = formTossedGroups(competition.path)
    val timestamps = formTimestamps(competition.path)
    fillTimestamps(groups, timestamps)
    val trueParticipants = getNotCheaters(groups)
    fillFinishData(trueParticipants, competition)

    val writer = Writer(competition.path.resolve("protocols/results.csv").toFile(), FileType.CSV)
    writer.add(
        listOf(
            "Место", "Номер", "Имя", "Фамилия", "Г.р.", "Разр.", "Время старта", "Время финиша", "Отставание"
        )
    )
    competition.groups.forEach { group ->
        writer.add(group.name)
        group.members.forEach { participant ->
            writer.add(participant, ::formatterForPersonalResults)
        }
    }
    writer.write()
}

fun tossTarget(competition: Competition) {
    competition.simpleToss(LocalTime.NOON, 5)
    val writer = Writer(competition.path.resolve("protocols/toss.csv").toFile(), FileType.CSV)

    writer.add(listOf("Номер", "Имя", "Фамилия", "Г.р.", "Команда", "Разр.", "Время старта"))
    writer.addAll(competition.groups)
    writer.write()
}

fun main(args: Array<String>): Unit = mainBody {

    logger.info { "Program started" }
    val parsedArgs = ArgParser(args).parseInto(::ArgumentsFormat)
    val competitionPath = Path(parsedArgs.competitionsRoot).resolve(parsedArgs.competitionName)

    val competition = Competition(competitionPath)
    logger.info { "Competition files read success" }

    when (parsedArgs.target) {
        Target.TOSS -> tossTarget(competition)
        Target.PERSONAL_RESULT -> personalResultsTarget(competition)
        Target.TEAM_RESULT -> TODO()
    }
    logger.info { "Program successfully finished" }
}
