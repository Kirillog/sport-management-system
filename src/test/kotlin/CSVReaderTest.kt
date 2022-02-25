import com.github.doyaaaaaken.kotlincsv.util.MalformedCSVException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import ru.emkn.kotlin.sms.ObjectFields
import ru.emkn.kotlin.sms.io.CSVReader
import java.io.File
import java.io.IOException
import kotlin.io.path.Path

internal class CSVReaderTest {
    private val resources = Path("src/test/resources")

    private fun readTeam(file: File): List<Map<ObjectFields, String>>? =
        CSVReader(file).team()

    private fun readCourses(file: File): List<Map<ObjectFields, String>>? =
        CSVReader(file).courses()

    private fun readEvent(file: File): List<Map<ObjectFields, String>>? =
        CSVReader(file).event()

    private fun readGroups(file: File): List<Map<ObjectFields, String>>? =
        CSVReader(file).groups()

    private fun readTimeStamps(file: File): List<Map<ObjectFields, String>>? =
        CSVReader(file).timestamps()

    private fun readToss(file: File): List<Map<ObjectFields, String>>? =
        CSVReader(file).toss()

    private fun readCheckpoints(file: File): List<Map<ObjectFields, String>>? =
        CSVReader(file).checkPoints()

    interface ReadTest {
        @Test
        fun simple()

    }

    @Nested
    inner class IncorrectFilesTest {
        @Test
        fun emptyFiles() {
            val file = kotlin.io.path.createTempFile().toFile()
            assertEquals(null, readTeam(file))
            assertEquals(null, readCourses(file))
            assertEquals(null, readGroups(file))
        }

        @Test
        fun incorrectHeader() {
            val coursesFile = resources.resolve("incorrectFiles/incorrectHeader.csv").toFile()
            assertThrows<IOException> { readCourses(coursesFile) }
        }

        @Test
        fun emptyContent() {
            val teamFile = resources.resolve("incorrectFiles/emptyTeam.csv").toFile()
            val coursesFile = resources.resolve("incorrectFiles/emptyCourses.csv").toFile()
            assertEquals(null, readTeam(teamFile))
            assertEquals(null, readCourses(coursesFile))
        }

        @Test
        fun incorrectCSVFormat() {
            val teamFile = resources.resolve("incorrectFiles/incorrectCSVFormat.csv").toFile()
            assertThrows<MalformedCSVException> { readTeam(teamFile) }
        }
    }

    @Nested
    inner class ReadTeamTest : ReadTest {
        @Test
        override fun simple() {
            val teamFile = resources.resolve("applications/simple.csv").toFile()
            val team = listOf(
                mapOf(
                    ObjectFields.Name to "Дмитрий",
                    ObjectFields.Surname to "Исаков",
                    ObjectFields.BirthdayYear to "2009",
                    ObjectFields.Group to "М12",
                    ObjectFields.Team to "СЕБЕЖ",
                    ObjectFields.Grade to ""
                ),
                mapOf(
                    ObjectFields.Name to "Михаил",
                    ObjectFields.Surname to "Гришмановский",
                    ObjectFields.BirthdayYear to "2005",
                    ObjectFields.Group to "М16",
                    ObjectFields.Team to "СЕБЕЖ",
                    ObjectFields.Grade to "2р"
                )
            )
            assertEquals(team, readTeam(teamFile))
        }

    }

    @Nested
    inner class ReadCoursesTest : ReadTest {

        @Test
        override fun simple() {
            val coursesFile = resources.resolve("courses/simple.csv").toFile()
            val routes = listOf(
                mapOf(
                    ObjectFields.Name to "МЖ9 10",
                    ObjectFields.Type to "Полный",
                    ObjectFields.Amount to "",
                    ObjectFields.CheckPoints to "32,46,34,33,53"
                ),
                mapOf(
                    ObjectFields.Name to "Ж14",
                    ObjectFields.Type to "Выборочный",
                    ObjectFields.Amount to "4",
                    ObjectFields.CheckPoints to "47,46,45,34,33,32"
                ),
                mapOf(
                    ObjectFields.Name to "Ж12",
                    ObjectFields.Type to "Полный",
                    ObjectFields.Amount to "",
                    ObjectFields.CheckPoints to "32,46"
                ),
            )
            assertEquals(
                routes,
                readCourses(coursesFile)
            )
        }
    }

    @Nested
    inner class ReadGroups : ReadTest {
        @Test
        override fun simple() {
            val file = resources.resolve("groups/simple.csv").toFile()
            val groups = listOf(
                mapOf(
                    ObjectFields.Name to "М10",
                    ObjectFields.ResultType to "Время",
                    ObjectFields.RouteName to "МЖ9 10"
                ),
                mapOf(
                    ObjectFields.Name to "М12",
                    ObjectFields.ResultType to "Стоимость",
                    ObjectFields.RouteName to "М12"
                ),
                mapOf(
                    ObjectFields.Name to "М14",
                    ObjectFields.ResultType to "Время",
                    ObjectFields.RouteName to "М14 20"
                )
            )
            assertEquals(groups, readGroups(file))
        }

    }

    @Nested
    inner class ReadEventsTest : ReadTest {
        @Test
        override fun simple() {
            val file = resources.resolve("events/simple.csv").toFile()
            val event = listOf(
                mapOf(
                    ObjectFields.Name to "Первенство пятой бани",
                    ObjectFields.Date to "01.01.2022"
                )
            )
            assertEquals(event, readEvent(file))
        }

    }

    @Nested
    inner class ReadCheckPoints : ReadTest {
        @Test
        override fun simple() {
            val file = resources.resolve("checkpoints/simple.csv").toFile()
            val checkPoints = listOf(
                mapOf(
                    ObjectFields.Name to "0",
                    ObjectFields.Weight to "9"
                ),
                mapOf(
                    ObjectFields.Name to "3",
                    ObjectFields.Weight to "2"
                ),
                mapOf(
                    ObjectFields.Name to "4",
                    ObjectFields.Weight to "1"
                ),
            )
            assertEquals(checkPoints, readCheckpoints(file))
        }

    }

    @Nested
    inner class ReadTimeStamps : ReadTest {
        @Test
        override fun simple() {
            val file = resources.resolve("timestamps/simple.csv").toFile()
            val timestamps = listOf(
                mapOf(
                    ObjectFields.Time to "21:22:30",
                    ObjectFields.ID to "100",
                    ObjectFields.Name to "12"
                ),
                mapOf(
                    ObjectFields.Time to "19:15:30",
                    ObjectFields.ID to "102",
                    ObjectFields.Name to "12"
                ),
                mapOf(
                    ObjectFields.Time to "17:15:56",
                    ObjectFields.ID to "105",
                    ObjectFields.Name to "12"
                )
            )
            assertEquals(timestamps, readTimeStamps(file))
        }

    }

    @Nested
    inner class ReadParticipants : ReadTest {
        @Test
        override fun simple() {
            val file = resources.resolve("protocols/simple.csv").toFile()
            val participants = listOf(
                mapOf(
                    ObjectFields.Name to "Анна",
                    ObjectFields.Surname to "Сосницкая",
                    ObjectFields.BirthdayYear to "2013",
                    ObjectFields.Group to "Ж10",
                    ObjectFields.Team to "0-ПСКОВ",
                    ObjectFields.Grade to "1р",
                    ObjectFields.ID to "101",
                    ObjectFields.StartTime to "12:00:00"
                ),
                mapOf(
                    ObjectFields.Name to "АРТЁМ",
                    ObjectFields.Surname to "КАЧНОВ",
                    ObjectFields.BirthdayYear to "2008",
                    ObjectFields.Group to "МЖ14",
                    ObjectFields.Team to "ВЕЛИКОЛУКСКИЙ РАЙОН",
                    ObjectFields.Grade to "",
                    ObjectFields.ID to "128",
                    ObjectFields.StartTime to "12:05:00"
                ),
                mapOf(
                    ObjectFields.Name to "АЛЕКСАНДРА",
                    ObjectFields.Surname to "ЛОВЦОВА",
                    ObjectFields.BirthdayYear to "2014",
                    ObjectFields.Group to "МЖ14",
                    ObjectFields.Team to "ВЕЛИКИЕ ЛУКИ",
                    ObjectFields.Grade to "",
                    ObjectFields.ID to "102",
                    ObjectFields.StartTime to "12:10:00"
                ),
            )
            assertEquals(participants, readToss(file))
        }
    }
}
