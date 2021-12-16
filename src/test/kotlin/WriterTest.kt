import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import org.junit.jupiter.api.Test
import ru.emkn.kotlin.sms.FileType
import ru.emkn.kotlin.sms.io.Writer
import ru.emkn.kotlin.sms.model.Route
import ru.emkn.kotlin.sms.model.Group
import ru.emkn.kotlin.sms.model.Participant
import ru.emkn.kotlin.sms.model.Team
import java.io.File
import java.time.Duration
import java.time.LocalTime
import kotlin.test.assertEquals


class WriterTest {
    private val fileName = "WriterTest.csv"
    private val file = File(fileName).also(File::deleteOnExit)
    private val writer = Writer(file, FileType.CSV)

    private val participant = listOf(
        Participant("Vasya", "Pupkin", 1998, "Human", "Team A", "I"),
        Participant("Petya", "Loopkin", 2189, "Human", "Team B"),
        Participant("Picathu", "Yellow", 201, "Pokemon", "Team A", "IV"),
        Participant("Squirtle", "Blue", 211, "Pokemon", "Team B")
    )

    private fun formatter(it: Participant): List<String?> = listOf(
        it.id.toString(),
        it.name,
        it.surname,
        it.birthdayYear.toString(),
        it.group,
        it.team,
        it.grade
    )

    private val participantDump = listOf(
        listOf("", "Vasya", "Pupkin", "1998", "Human", "Team A", "I"),
        listOf("", "Petya", "Loopkin", "2189", "Human", "Team B", ""),
        listOf("", "Picathu", "Yellow", "201", "Pokemon", "Team A", "IV"),
        listOf("", "Squirtle", "Blue", "211", "Pokemon", "Team B", "")
    )

    @Test
    fun testWriteFilledParticipant() {
        val appA = participant[0]
        val appB = participant[1]
        appA.id = 100
        appB.id = 500
        appA.startTime = LocalTime.of(10, 10, 10)
        appA.finishTime = LocalTime.of(23, 59, 59)
        appA.positionInGroup = Participant.PositionInGroup(1, Duration.ofSeconds(0))
        appB.startTime = LocalTime.of(0, 0, 0)
        appB.finishTime = LocalTime.of(11, 12, 13)
        appB.positionInGroup = Participant.PositionInGroup(2, Duration.ofSeconds(4))
        val filledParticipantDump = listOf(
            listOf("1","100", "Vasya", "Pupkin", "1998", "Human", "Team A", "I", "10:10:10", "23:59:59", "0h 0m 0s"),
            listOf("2", "500", "Petya", "Loopkin", "2189", "Human", "Team B", "", "00:00:00", "11:12:13", "0h 0m 4s")
        )

        writer.add(appA)
        writer.add(appB)
        writer.write()

        val correct = listOf(
            filledParticipantDump[0],
            filledParticipantDump[1]
        )
        val csvData = csvReader().readAll(file)
        assertEquals(correct, csvData)
    }

    @Test
    fun testWriteParticipantWithFormatter() {
        val appA = participant[0]
        val appB = participant[1]

        writer.add(appA, ::formatter)
        writer.add(appB, ::formatter)
        writer.add(appB, ::formatter)
        writer.add(appA, ::formatter)
        writer.write()

        val correct = listOf(
            participantDump[0],
            participantDump[1],
            participantDump[1],
            participantDump[0]
        )
        val csvData = csvReader().readAll(file)
        assertEquals(correct, csvData)
    }

    @Test
    fun testWriteString() {
        writer.add("Hello")
        writer.add("CSV")
        writer.add(listOf("string", "input"))
        writer.write()

        val correct = listOf(
            listOf("Hello", ""),
            listOf("CSV", ""),
            listOf("string", "input")
        )
        val csvData = csvReader().readAll(file)
        assertEquals(correct, csvData)
    }

    @Test
    fun testWriteTeam() {
        val teamA = Team(name = "Team A", listOf(participant[0], participant[2]))
        val teamB = Team(name = "Team B", listOf(participant[1], participant[3]))

        writer.add(teamA)
        writer.addAll(listOf(teamB))
        writer.write()

        val dumpWithoutTeam =
            participantDump.map { line -> line.toMutableList().also { it.removeAt(5); it.add("") } }

        val correct = listOf(
            listOf("Team A", "", "", "", "", "", ""),
            dumpWithoutTeam[0],
            dumpWithoutTeam[2],
            listOf("Team B", "", "", "", "", "", ""),
            dumpWithoutTeam[1],
            dumpWithoutTeam[3]
        )
        val csvData = csvReader().readAll(file)
        assertEquals(correct, csvData)
    }

    @Test
    fun testWriteGroup() {
        val groupA = Group(
            name = "Human", route = Route("Course for 'Human'", listOf()),
            listOf(participant[0], participant[1])
        )
        val groupB = Group(
            name = "Pokemon", route = Route("Course for 'Pokemon'", listOf()),
            listOf(participant[2], participant[3])
        )

        writer.add(groupA)
        writer.addAll(listOf(groupB))
        writer.write()

        val dumpWithoutTeam =
            participantDump.map { line -> line.toMutableList().also { it.removeAt(4); it.add("") }}

        val correct = listOf(
            listOf("Human", "", "", "", "", "", ""),
            dumpWithoutTeam[0],
            dumpWithoutTeam[1],
            listOf("Pokemon", "", "", "", "", "", ""),
            dumpWithoutTeam[2],
            dumpWithoutTeam[3]
        )
        val csvData = csvReader().readAll(file)
        assertEquals(correct, csvData)
    }
}

