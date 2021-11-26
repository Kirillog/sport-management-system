import kotlinx.datetime.LocalDate
import ru.emkn.kotlin.sms.FileType
import ru.emkn.kotlin.sms.io.MultilineWritable
import ru.emkn.kotlin.sms.io.Writer
import ru.emkn.kotlin.sms.objects.*
import java.io.File
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.io.path.Path
import kotlin.random.Random


data class CheckPointsProtocol(val checkPoint: CheckPoint, val protocol: List<Pair<Participant, LocalTime>>) :
    MultilineWritable {
    override fun toMultiline(): List<List<String>> {
        return listOf(listOf(checkPoint.id.toString())) +
                protocol.map { listOf(it.first.name, it.second.format(DateTimeFormatter.ISO_LOCAL_TIME)) }
    }
}

data class ParticipantsProtocol(val participant: Participant, val protocol: List<Pair<CheckPoint, LocalTime>>) :
    MultilineWritable {
    override fun toMultiline(): List<List<String>> {
        return listOf(listOf(participant.id.toString())) +
                protocol.map { listOf(it.first.id.toString(), it.second.format(DateTimeFormatter.ISO_LOCAL_TIME)) }
    }
}

fun generateParticipantsProtocol(
    participant: Participant,
    course: Course,
    maxFinishTime: LocalTime,
    random: Random
): ParticipantsProtocol {
    val startTime = participant.startTime ?: throw IllegalStateException("start time have to be set up")
    val startSeconds = startTime.toSecondOfDay()
    val maxFinishSeconds: Int = maxFinishTime.toSecondOfDay()
    val times = List(course.checkPoints.size) {
        val randomTime = random.nextInt(startSeconds, maxFinishSeconds)
        LocalTime.ofSecondOfDay(randomTime.toLong())
    }.sorted()

    return ParticipantsProtocol(
        participant,
        course.checkPoints.zip(times) { checkpoint, time -> Pair(checkpoint, time) })
}

fun buildGroups(teams: List<Team>, courses: Map<String, Course>): List<Group> {
    val groups = teams.flatMap(Team::members).groupBy { it.group }
    return groups.map {
        val course = courses[it.key] ?: throw IllegalStateException("course has to be found")
        Group(it.key, course, it.value)
    }
}

data class TimeStamp(val participant: Participant, val checkPoint: CheckPoint, val time: LocalTime)

fun convertParticipantProtocolsIntoCheckPointProtocols(participantProtocols: List<ParticipantsProtocol>): List<CheckPointsProtocol> {
    return participantProtocols
        .flatMap { it.protocol.map { i -> TimeStamp(it.participant, i.first, i.second) } }
        .groupBy { it.checkPoint }
        .mapValues { i -> i.value.map { Pair(it.participant, it.time) } }
        .map { CheckPointsProtocol(it.key, it.value) }
}

fun main() {
    val random = Random(0)
    val courses = generateCoursesForGroups(getAllGroups(), 10, random)
    val teams = List(10) {
        val teamSize = random.nextInt(2, 5)
        generateTeam(it, teamSize, random)
    }
    val groups = buildGroups(teams, courses)

    val protocolsDir = "test_generator/protocols"
    if (!File(protocolsDir).exists()) {
        File(protocolsDir).mkdir()
    }
    val competition = Competition(
        Event("test-event", LocalDate(2021, 11, 25)),
        Path(""),
        teams,
        groups
    )
    competition.simpleToss(LocalTime.NOON, 5)
    val participantsProtocols = mutableListOf<ParticipantsProtocol>()
    for (team in competition.teams) {
        for (participant in team.members) {
            val course = courses[participant.group] ?: throw IllegalStateException("course has to be found")
            val protocol = generateParticipantsProtocol(participant, course, LocalTime.MAX, random)
            participantsProtocols.add(protocol)
            val writer = Writer(File("$protocolsDir/participant${participant.id}"), FileType.CSV)
            writer.add(protocol)
            writer.write()
        }
    }

    val checkPointProtocols = convertParticipantProtocolsIntoCheckPointProtocols(participantsProtocols)
    for (protocol in checkPointProtocols) {
        val writer = Writer(File("$protocolsDir/checkpoint${protocol.checkPoint.id}"), FileType.CSV)
        writer.add(protocol)
        writer.write()
    }
}