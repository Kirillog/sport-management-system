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
import ru.emkn.kotlin.sms.objects.Competition
import ru.emkn.kotlin.sms.objects.Participant
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.io.path.Path

private val logger = KotlinLogging.logger {}

fun personalResultsTarget(competition: Competition) {
    val groups = formTossedGroups(competition.path)
    val groupByName = groups.associateBy { it.name }
    val participants = groups.flatMap { it.members }
    val participantById = participants.associateBy { it.id ?: 0 }
    val finishTimeByParticipant = mutableMapOf<Participant, LocalTime>()
    formTimestamps(competition.path).groupBy { it.participantId }
        .mapKeys { participantById[it.key] ?: throw IllegalStateException() }
        .forEach { (participant, timeStamps) ->
            val course = timeStamps.sortedBy { it.time }
            participant.timeStamps = course
            groupByName[participant.group]
            if (course != groupByName.getOrElse(participant.group) {
                    throw IllegalStateException("Can not found group by name")
                }.course.checkPoints) {
                logger.info {
                    "Participant ${participant.id} ${participant.name} ${participant.surname} " +
                            "was disqualified for violating the course."
                }
                return@forEach
            }
            finishTimeByParticipant[participant] = course.last().time
        }
    groups.forEach { group ->
        val sortedGroup = group.members.sortedBy { finishTimeByParticipant[it] }
        val leaderFinish = group.members[0].finishData
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

    val writer = Writer(competition.path.resolve("protocols/results.csv").toFile(), FileType.CSV)
    writer.add(
        listOf(
            "Место",
            "Номер",
            "Имя",
            "Фамилия",
            "Г.р.",
            "Разр.",
            "Время старта",
            "Время финиша",
            "Отставание"
        )
    )
    competition.groups.forEach { group ->
        writer.add(group.name)
        group.members.forEach { participant ->
            writer.add(participant
            ) {
                listOf(
                    it.finishData?.place?.toString(),
                    it.id?.toString(),
                    it.name,
                    it.surname,
                    it.birthdayYear.toString(),
                    it.grade,
                    it.startTime?.format(DateTimeFormatter.ISO_LOCAL_TIME),
                    it.finishData?.time?.format(DateTimeFormatter.ISO_LOCAL_TIME),
                    it.finishData?.laggingFromLeader?.toString()
                )
            }
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
