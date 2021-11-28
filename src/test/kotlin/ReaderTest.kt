
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.TestInstance
import ru.emkn.kotlin.sms.io.*
import ru.emkn.kotlin.sms.objects.Competition
import ru.emkn.kotlin.sms.tossTarget
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.createDirectory
import kotlin.test.assertEquals


// TODO up to 3
private const val repetitionNumber = 3

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class ReaderTest {
    private val path = Path("src/test/.temp/")

    private fun init(subDirectory: String = "input"): Path {
        val currentPath = path.resolve(subDirectory)
        currentPath.createDirectory()
        return currentPath
    }

    @BeforeAll
    fun setUp() {
        path.createDirectory()
    }

    @AfterAll
    fun setDown() {
        path.toFile().deleteRecursively()
    }

    @RepeatedTest(repetitionNumber)
    fun formTeamsListTest() {
        val currentPath = init("applications")
        val generatedTeams = generateApplications(currentPath, 10, 15).toSet()
        val teams = formTeamsList(path).toSet()
        assertEquals(generatedTeams, teams)
        currentPath.toFile().deleteRecursively()
    }

    @RepeatedTest(repetitionNumber)
    fun formCoursesListTest() {
        val currentPath = init()
        val generatedCourses = generateCourses(currentPath).toSet()
        val courses = formCoursesList(path).toSet()
        assertEquals(generatedCourses, courses)
        currentPath.toFile().deleteRecursively()

    }

    @RepeatedTest(repetitionNumber)
    fun formMapGroupsToCoursesTest() {
        val currentPath = init()
        val generatedMap = generateCoursesForGroups(currentPath).mapValues { it.value.name }
        val map = formMapGroupsToCourses(path)
        assertEquals(generatedMap, map)
        currentPath.toFile().deleteRecursively()

    }

    @RepeatedTest(repetitionNumber)
    fun formGroupsListTest() {
        val dir = init("test/")
        val currentPath = init("test/input")
        val applicationPath = init("test/applications")
        val generatedTeams = generateApplications(applicationPath)
        val generatedGroups = generateGroups(currentPath, generatedTeams).toSet()
        val groups = formGroupsList(generatedTeams, dir).toSet()
        assertEquals(generatedGroups, groups)
        dir.toFile().deleteRecursively()
    }

    @RepeatedTest(repetitionNumber)
    fun formEventTest() {
        val currentPath = init()
        val eventsList = generateEvents(currentPath)
        val event = formEvent(path)
        assertEquals(eventsList[0], event)
        currentPath.toFile().deleteRecursively()
    }

    @RepeatedTest(repetitionNumber)
    fun formTossedGroupsListTest() {
        val competitionPath = init("competition")
        val initPath = init("competition/input")
        val applicationPath = init("competition/applications")
        init("competition/protocols")
        generateGroups(initPath, generateApplications(applicationPath))
        generateEvents(initPath)
        val competition = Competition(competitionPath)
        tossTarget(competition)
        val generatedGroups = competition.groups.toSet()
        val groups = formTossedGroups(competitionPath).toSet()
        assertEquals(generatedGroups, groups)
        competitionPath.toFile().deleteRecursively()
    }

    @RepeatedTest(repetitionNumber)
    fun formTimeStampsListTest() {
        val competitionPath = init("competition")
        val initPath = init("competition/input")
        val applicationPath = init("competition/applications")
        val checkPointPath = init("competition/checkpoints")
        init("competition/protocols")
        generateGroups(initPath, generateApplications(applicationPath))
        generateEvents(initPath)
        tossTarget(Competition(competitionPath))
        val checkPointsProtocols = generateCheckPointProtocols(competitionPath, checkPointPath)
        val checkPoints = formTimestamps(competitionPath)
        assertEquals(checkPointsProtocols.flatMap { it.protocol }.toSet(), checkPoints.toSet())
        competitionPath.toFile().deleteRecursively()
    }
}