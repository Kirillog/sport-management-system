import ru.emkn.kotlin.sms.FileType
import ru.emkn.kotlin.sms.controller.CompetitionController
import ru.emkn.kotlin.sms.io.MultilineWritable
import ru.emkn.kotlin.sms.io.Writer
import ru.emkn.kotlin.sms.model.CheckPoint
import ru.emkn.kotlin.sms.model.Participant
import ru.emkn.kotlin.sms.model.Route
import ru.emkn.kotlin.sms.model.TimeStamp
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
                protocol.map { listOf(it.participant.id.toString(), it.time.format(DateTimeFormatter.ISO_LOCAL_TIME)) }
    }
}

data class ParticipantsProtocol(val participant: Participant, val protocol: List<TimeStamp>) :
    MultilineWritable {
    override fun toMultiline(): List<List<String>> {
        return listOf(listOf(participant.id.toString())) + listOf(listOf("Номер пункта", "Время")) +
                protocol.map { listOf(it.checkPoint.id.toString(), it.time.format(DateTimeFormatter.ISO_LOCAL_TIME)) }
    }
}

fun generateParticipantsProtocol(
    participant: Participant,
    route: Route,
    maxFinishTime: LocalTime,
    random: Random
): ParticipantsProtocol {
    val startTime = participant.startTime
    val startSeconds = startTime.toSecondOfDay()
    val maxFinishSeconds: Int = maxFinishTime.toSecondOfDay()
    val times = listOf(startTime) + List(route.checkPoints.size - 1) {
        val randomTime = random.nextInt(startSeconds, maxFinishSeconds)
        LocalTime.ofSecondOfDay(randomTime.toLong())
    }.sorted()

    return ParticipantsProtocol(
        participant,
        route.checkPoints.zip(times) { checkpoint, time -> TimeStamp(time, checkpoint, participant.id) }
    )
}

fun convertParticipantProtocolsIntoCheckPointProtocols(participantProtocols: List<ParticipantsProtocol>): List<CheckPointsProtocol> {
    return participantProtocols.flatMap { it.protocol }
        .groupBy { it.checkPoint.id }
        .map { CheckPointsProtocol(CheckPoint(it.key), it.value) }
}

fun generateCheckPointProtocols(
    competitionPath: Path,
    protocolsDir: Path,
    random: Random = Random(0)
): List<CheckPointsProtocol> {
    CompetitionController.announceFromPath(
        event = competitionPath.resolve("input/event.csv"),
        routes = competitionPath.resolve("input/courses.csv")
    )
    CompetitionController.groupsAndTossFromPath(
        group = competitionPath.resolve("input/classes.csv"),
        toss = competitionPath.resolve("protocols/toss.csv")
    )

    val participantsProtocols = mutableListOf<ParticipantsProtocol>()
    Participant.byId.values.forEach { participant ->
        val course = participant.group.route
        val protocol = generateParticipantsProtocol(participant, course, LocalTime.MAX, random)
        participantsProtocols.add(protocol)
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

    val protocolsDir = "competitions/competition-1/checkpoints"
    if (!File(protocolsDir).exists()) {
        File(protocolsDir).mkdirs()
    }
    generateCheckPointProtocols(Path("competitions/competition-1"), Path(protocolsDir), random)
}
