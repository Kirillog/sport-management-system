import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.util.MalformedCSVException
import kotlinx.datetime.LocalDate
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import ru.emkn.kotlin.sms.io.CSVReader
import ru.emkn.kotlin.sms.objects.*
import java.io.File
import java.io.IOException
import kotlin.io.path.Path

internal class CSVReaderTest {
    private val resources = Path("src/test/resources")

    private fun readTeam(file: File): Team? {
        return csvReader().open(file) {
            CSVReader(file, this).team()
        }
    }

    private fun readCourses(file: File): List<Course>? {
        return csvReader().open(file) {
            CSVReader(file, this).courses()
        }
    }

    private fun readEvents(file: File): List<Event>? {
        return csvReader().open(file) {
            CSVReader(file, this).events()
        }
    }

    private fun readGroupsToCourses(file: File): Map<String, String>? {
        return csvReader().open(file) {
            CSVReader(file, this).groupsToCourses()
        }
    }

    interface ReadTest {
        fun simple()
        fun incorrectTypeOfField()
    }

    @Nested
    inner class IncorrectFilesTest {
        @Test
        fun emptyFiles() {
            val file = kotlin.io.path.createTempFile().toFile()
            assertEquals(null, readTeam(file))
            assertEquals(null, readCourses(file))
            assertEquals(null, readGroupsToCourses(file))
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
            assertEquals(
                Team(
                    "СЕБЕЖ", listOf(
                        Participant("Дмитрий", "Исаков", 2009, "М12", "СЕБЕЖ", null),
                        Participant("Михаил", "Гришмановский", 2005, "М16", "СЕБЕЖ", "2р")
                    )
                ),
                readTeam(teamFile)
            )

        }

        @Test
        override fun incorrectTypeOfField() {
            val teamFile = resources.resolve("applications/incorrectTypeOfField.csv").toFile()
            assertEquals(
                Team(
                    "ПСКОВ,РУСЬ", listOf(
                        Participant("АЛЛА", "НИКИТИНА", 1939, "VIP", "ПСКОВ,РУСЬ", "1р"),
                        Participant("МИХАИЛ", "ЖЕЛЕЗНЫЙ", 2007, "М14", "ПСКОВ,РУСЬ", null)
                    )
                ), readTeam(teamFile)
            )
        }
    }

    @Nested
    inner class ReadCoursesTest : ReadTest {
        @Test
        override fun incorrectTypeOfField() {
            val coursesFile = resources.resolve("courses/incorrectTypeOfField.csv").toFile()
            assertEquals(
                listOf(
                    Course("М18 21 40 50", listOf(CheckPoint(31), CheckPoint(32))),
                    Course("Ж14", listOf(CheckPoint(47), CheckPoint(46), CheckPoint(45), CheckPoint(34)))
                ),
                readCourses(coursesFile)
            )
        }

        @Test
        override fun simple() {
            val coursesFile = resources.resolve("courses/simple.csv").toFile()
            assertEquals(
                listOf(
                    Course(
                        "МЖ9 10",
                        listOf(CheckPoint(32), CheckPoint(46), CheckPoint(34), CheckPoint(33), CheckPoint(53))
                    ),
                    Course(
                        "Ж14",
                        listOf(
                            CheckPoint(47),
                            CheckPoint(46),
                            CheckPoint(45),
                            CheckPoint(34),
                            CheckPoint(33),
                            CheckPoint(32)
                        )
                    ),
                    Course("Ж12", listOf(CheckPoint(32), CheckPoint(46)))
                ),
                readCourses(coursesFile)
            )
        }
    }

    @Nested
    inner class ReadGroupsToCoursesTest : ReadTest {
        @Test
        override fun simple() {
            val file = resources.resolve("groups/simple.csv").toFile()
            assertEquals(mapOf("М10" to "МЖ9 10", "М12" to "М12", "М14" to "М14 20"), readGroupsToCourses(file))
        }

        @Test
        override fun incorrectTypeOfField() {
            val file = resources.resolve("groups/incorrectTypeOfField.csv").toFile()
            assertEquals(mapOf("М10" to "МЖ9 10", "М16" to "М16 Ж6"), readGroupsToCourses(file))
        }
    }

    @Nested
    inner class ReadEventsTest : ReadTest {
        @Test
        override fun simple() {
            val file = resources.resolve("events/simple.csv").toFile()
            assertEquals(listOf(Event("Первенство пятой бани", LocalDate.parse("2022-01-01"))), readEvents(file))
        }

        @Test
        override fun incorrectTypeOfField() {
            val file = resources.resolve("events/incorrectTypeOfField.csv").toFile()
            assertEquals(listOf<Event>(), readEvents(file))
        }
    }
}