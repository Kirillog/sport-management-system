import org.jetbrains.exposed.sql.transactions.transaction
import ru.emkn.kotlin.sms.FileType
import ru.emkn.kotlin.sms.controller.CompetitionController
import ru.emkn.kotlin.sms.controller.State
import ru.emkn.kotlin.sms.io.MultilineWritable
import ru.emkn.kotlin.sms.io.Writer
import ru.emkn.kotlin.sms.model.*
import java.io.File
import java.nio.file.Path
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.io.path.Path
import kotlin.random.Random

fun CompetitionController.groupsAndTossFromPath(group: Path, toss: Path) {
    state = State.TOSSED
    val groupLoader = getLoader(group)
    val tossLoader = getLoader(toss)
    transaction {
        Competition.loadGroups(groupLoader)
        Competition.toss(tossLoader)
        Competition.teams.addAll(Team.all().toSet())
    }
}


data class CheckPointsProtocol(val checkPoint: Checkpoint, val protocol: List<Timestamp>) :
    MultilineWritable {
    override fun toMultiline(): List<List<String>> {
        return listOf(listOf(checkPoint.name)) + listOf(listOf("Номер", "Время")) +
                protocol.map { listOf(it.participant.id.toString(), it.time.format(DateTimeFormatter.ISO_LOCAL_TIME)) }
    }
}

data class ParticipantsProtocol(val participant: Participant, val protocol: List<Timestamp>) :
    MultilineWritable {
    override fun toMultiline(): List<List<String>> {
        return listOf(listOf(participant.id.toString())) + listOf(listOf("Номер пункта", "Время")) +
                protocol.map { listOf(it.checkpoint.name, it.time.format(DateTimeFormatter.ISO_LOCAL_TIME)) }
    }
}

fun Generator.generateParticipantsProtocol(
    participant: Participant,
    route: Route,
    maxFinishTime: LocalTime,
    random: Random
): ParticipantsProtocol {
    return transaction {
        val startTime = participant.startTime
        val startSeconds = startTime.toSecondOfDay()
        val maxFinishSeconds: Int = maxFinishTime.toSecondOfDay()
        val times = listOf(startTime) + List(route.checkpoints.toList().size - 1) {
            val randomTime = random.nextInt(startSeconds, maxFinishSeconds)
            LocalTime.ofSecondOfDay(randomTime.toLong())
        }.sorted()

        ParticipantsProtocol(
            participant,
            route.checkpoints.zip(times) { checkpoint, time -> Timestamp.create(time, checkpoint.id, participant.id) }
        )
    }
}

private fun convertParticipantProtocolsIntoCheckPointProtocols(participantProtocols: List<ParticipantsProtocol>): List<CheckPointsProtocol> {
    return participantProtocols.flatMap { it.protocol }
        .groupBy { it.checkpoint }
        .map { CheckPointsProtocol(it.key, it.value) }
}

fun Generator.generateCheckPointProtocols(
    competitionPath: Path,
    protocolsDir: Path,
    random: Random = Random(0)
): List<CheckPointsProtocol> {
    CompetitionController.loadEvent(competitionPath.resolve("input/event.csv"))
    CompetitionController.loadCheckpoints(competitionPath.resolve("input/checkpoints.csv"),)
    CompetitionController.loadRoutes(competitionPath.resolve("input/courses.csv"))
    CompetitionController.announce()

    CompetitionController.groupsAndTossFromPath(
        group = competitionPath.resolve("input/classes.csv"),
        toss = competitionPath.resolve("protocols/toss.csv")
    )

    return transaction {
        val participantsProtocols = mutableListOf<ParticipantsProtocol>()
        Participant.all().forEach { participant ->
            val course = participant.group.route
            val protocol = generateParticipantsProtocol(participant, course, LocalTime.MAX, random)
            participantsProtocols.add(protocol)
        }

        val checkPointProtocols = convertParticipantProtocolsIntoCheckPointProtocols(participantsProtocols)
        for (protocol in checkPointProtocols) {
            val writer = Writer(File("$protocolsDir/checkpoint${protocol.checkPoint.name}.csv"), FileType.CSV)
            writer.add(protocol)
            writer.write()
        }
        checkPointProtocols
    }
}

fun main() {
    val path = Path("competitions/competition-1/checkpoints")
    generate(path) {
        generateCheckPointProtocols(Path("competitions/competition-1"), path)
    }
}
