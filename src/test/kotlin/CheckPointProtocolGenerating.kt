import ru.emkn.kotlin.sms.FileType
import ru.emkn.kotlin.sms.io.*
import ru.emkn.kotlin.sms.model.*
import java.io.File
import java.nio.file.Path
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.io.path.Path
import kotlin.random.Random


data class CheckPointsProtocol(val checkPoint: CheckPoint, val protocol: List<TimeStamp>) :
    MultilineWritable {
    override fun toMultiline(): List<List<String>> {
        return listOf(listOf(checkPoint.id.toString())) + listOf(listOf("Номер", "Время")) +
                protocol.map { listOf(it.participantId.toString(), it.time.format(DateTimeFormatter.ISO_LOCAL_TIME)) }
    }
}

data class ParticipantsProtocol(val participant: Participant, val protocol: List<TimeStamp>) :
    MultilineWritable {
    override fun toMultiline(): List<List<String>> {
        return listOf(listOf(participant.id.toString())) + listOf(listOf("Номер пункта", "Время")) +
                protocol.map { listOf(it.checkPointId.toString(), it.time.format(DateTimeFormatter.ISO_LOCAL_TIME)) }
    }
}

fun generateParticipantsProtocol(
    participant: Participant,
    route: Route,
    maxFinishTime: LocalTime,
    random: Random
): ParticipantsProtocol {
    val startTime = participant.getStartTime()
    val startSeconds = startTime.toSecondOfDay()
    val maxFinishSeconds: Int = maxFinishTime.toSecondOfDay()
    val times = listOf(startTime) + List(route.checkPoints.size - 1) {
        val randomTime = random.nextInt(startSeconds, maxFinishSeconds)
        LocalTime.ofSecondOfDay(randomTime.toLong())
    }.sorted()

    return ParticipantsProtocol(
        participant,
        route.checkPoints.zip(times) { checkpoint, time -> TimeStamp(time, checkpoint.id, participant.getId()) }
    )
}

fun convertParticipantProtocolsIntoCheckPointProtocols(participantProtocols: List<ParticipantsProtocol>): List<CheckPointsProtocol> {
    return participantProtocols.flatMap { it.protocol }
        .groupBy { it.checkPointId }
        .map { CheckPointsProtocol(CheckPoint(it.key), it.value) }
}

fun generateCheckPointProtocols(
    competitionPath: Path,
    protocolsDir: Path,
    random: Random = Random(0)
): List<CheckPointsProtocol> {
    val groups = formTossedGroups(competitionPath)
    val teams = groups.flatMap { it.members }.groupBy { it.team }.map { Team(it.key, it.value) }
    val crs = formCoursesList(competitionPath).associateBy { it.name }
    val courses = formMapGroupsToCourses(competitionPath).mapValues { crs[it.value] }

    val participantsProtocols = mutableListOf<ParticipantsProtocol>()
    for (team in teams) {
        for (participant in team.members) {
            val course = courses[participant.group] ?: throw IllegalStateException("course has to be found")
            val protocol = generateParticipantsProtocol(participant, course, LocalTime.MAX, random)
            participantsProtocols.add(protocol)
        }
    }

    val checkPointProtocols = convertParticipantProtocolsIntoCheckPointProtocols(participantsProtocols)
    for (protocol in checkPointProtocols) {
        val writer = Writer(File("$protocolsDir/checkpoint${protocol.checkPoint.id}.csv"), FileType.CSV)
        writer.add(protocol)
        writer.write()
    }
    return checkPointProtocols
}

fun main() {
    val random = Random(0)

    val protocolsDir = "competitions/competition-3/checkpoints"
    if (!File(protocolsDir).exists()) {
        File(protocolsDir).mkdirs()
    }
    generateCheckPointProtocols(Path("competitions/competition-3"), Path(protocolsDir), random)
}
