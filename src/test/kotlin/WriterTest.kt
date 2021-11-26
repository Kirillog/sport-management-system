
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import org.junit.Test
import ru.emkn.kotlin.sms.FileType
import ru.emkn.kotlin.sms.io.Writer
import ru.emkn.kotlin.sms.objects.Course
import ru.emkn.kotlin.sms.objects.Group
import ru.emkn.kotlin.sms.objects.Participant
import ru.emkn.kotlin.sms.objects.Team
import java.io.File
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

    private val participantDump = listOf(
        listOf("Vasya", "Pupkin", "1998", "Human", "Team A", "I"),
        listOf("Petya", "Loopkin", "2189", "Human", "Team B", ""),
        listOf("Picathu", "Yellow", "201", "Pokemon", "Team A", "IV"),
        listOf("Squirtle", "Blue", "211", "Pokemon", "Team B", "")
    )

    @Test
    fun testWriteApplicant() {
        val appA = participant[0]
        val appB = participant[1]

        writer.add(appA)
        writer.add(appB)
        writer.addAll(listOf(appB, appA))
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
        val teamA = Team(name = "Team A",  listOf(participant[0], participant[2]))
        val teamB = Team(name = "Team B", listOf(participant[1], participant[3]))

        writer.add(teamA)
        writer.addAll(listOf(teamB))
        writer.write()

        val correct = listOf(
            listOf("Team A", "", "", "", "", ""),
            participantDump[0],
            participantDump[2],
            listOf("Team B", "", "", "", "", ""),
            participantDump[1],
            participantDump[3]
        )
        val csvData = csvReader().readAll(file)
        assertEquals(correct, csvData)
    }

    @Test
    fun testWriteGroup() {
        val groupA = Group(name = "Human", course = Course("Course for 'Human'", listOf()),
            listOf(participant[0], participant[1])
        )
        val groupB = Group(name = "Pokemon", course = Course("Course for 'Pokemon'", listOf()),
            listOf(participant[2], participant[3])
        )

        writer.add(groupA)
        writer.addAll(listOf(groupB))
        writer.write()

        val correct = listOf(
            listOf("Human", "", "", "", "", ""),
            participantDump[0],
            participantDump[1],
            listOf("Pokemon", "", "", "", "", ""),
            participantDump[2],
            participantDump[3]
        )
        val csvData = csvReader().readAll(file)
        assertEquals(correct, csvData)
    }
}

