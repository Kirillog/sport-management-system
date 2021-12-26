import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.*
import ru.emkn.kotlin.sms.DB_TABLES
import ru.emkn.kotlin.sms.FileType
import ru.emkn.kotlin.sms.controller.CompetitionController
import ru.emkn.kotlin.sms.controller.State
import ru.emkn.kotlin.sms.io.FileLoader
import java.io.File
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.createDirectory
import kotlin.test.assertEquals


private const val repetitionNumber = 3

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class FileLoaderTest {
    private val path = Path("src/test/temp/")

    private fun init(subDirectory: String = "input"): Path {
        val currentPath = path.resolve(subDirectory)
        currentPath.createDirectory()
        return currentPath
    }

    private fun <T> loadTransaction(path: Path, loads: FileLoader.() -> T): T = transaction {
        FileLoader(path, FileType.CSV).loads()
    }

    @BeforeEach
    fun connect() {
        Database.connect("jdbc:h2:./${path}/testDB", driver = "org.h2.Driver")

        transaction {
            DB_TABLES.forEach {
                SchemaUtils.create(it)
            }
        }
    }

    @AfterEach
    fun disconnect() {
        File("./${path}/testDB.mv.db").delete()
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
    fun formCheckPointListTest() {
        val currentPath = init()
        val generatedCheckpoints = Generator.generateCheckpoints(currentPath).toSet()
        val checkpoints = loadTransaction(currentPath.resolve("checkpoints.csv")) {
            loadCheckpoints()
        }
        assertEquals(generatedCheckpoints, checkpoints)
        currentPath.toFile().deleteRecursively()
    }

    @RepeatedTest(repetitionNumber)
    fun formCoursesListTest() {
        val currentPath = init()
        val generatedCourses = Generator.generateCourses(currentPath)
        val courses = loadTransaction(currentPath.resolve("courses.csv")) { loadRoutes() }
        transaction {
            assertEquals(generatedCourses.toSet(), courses)
        }
        currentPath.toFile().deleteRecursively()
    }

    @RepeatedTest(repetitionNumber)
    fun formEmptyGroupsTest() {
        val currentPath = init()
        val generatedGroups = Generator.generateGroups(currentPath)
        val groups = loadTransaction(currentPath.resolve("classes.csv")) { loadGroups() }
        transaction {
            assertEquals(generatedGroups.toSet(), groups)
        }
        currentPath.toFile().deleteRecursively()
    }

    @RepeatedTest(repetitionNumber)
    fun formGroupsListTest() {
        val dir = init("test/")
        val currentPath = init("test/input")
        val applicationPath = init("test/applications")
        val generatedGroups = Generator.generateGroups(currentPath)
        val generatedTeams = Generator.generateApplications(applicationPath, 10, 15)
        val groups = loadTransaction(currentPath.resolve("classes.csv")) { loadGroups() }
        transaction {
            assertEquals(generatedGroups.toSet(), groups)
        }
        dir.toFile().deleteRecursively()
    }

    @RepeatedTest(repetitionNumber)
    fun formTeamsListTest() {
        val dir = init("test/")
        val currentPath = init("test/applications")
        val groupsPath = init("test/input")
        val generatedGroups = Generator.generateGroups(groupsPath)
        val generatedTeams = Generator.generateApplications(currentPath, 10, 15)
        val teams = loadTransaction(currentPath) { loadTeams() }
        transaction {
            assertEquals(generatedTeams.toSet(), teams)
        }
        dir.toFile().deleteRecursively()
    }


    @RepeatedTest(repetitionNumber)
    fun formEventTest() {
        val currentPath = init()
        val eventsList = Generator.generateEvents(currentPath)
        val event = loadTransaction(currentPath.resolve("event.csv")) { loadEvent() }
        assertEquals(eventsList[0], event)
        currentPath.toFile().deleteRecursively()
    }


    @Test
    fun formTimeStampsListTest() {
        val competitionPath = init("competition")
        val initPath = init("competition/input")
        val applicationPath = init("competition/applications")
        val checkPointPath = init("competition/checkpoints")
        val protocolPath = init("competition/protocols")
        Generator.generateEvents(initPath)
        Generator.generateGroups(initPath)
        Generator.generateApplications(applicationPath)
        disconnect()
        CompetitionController.createDB(File("./${path}/testDB.mv.db"))
        CompetitionController.loadEvent(initPath.resolve("event.csv"))
        CompetitionController.loadCheckpoints(initPath.resolve("checkpoints.csv"))
        CompetitionController.loadRoutes(initPath.resolve("courses.csv"))
        CompetitionController.loadGroups(initPath.resolve("classes.csv"))
        CompetitionController.toss()
        CompetitionController.saveTossToPath(protocolPath.resolve("toss.csv"))
        CompetitionController.state = State.EMPTY
        disconnect()
        CompetitionController.createDB(File("./${path}/testDB.mv.db"))
        val checkPointsProtocols =
            Generator.generateCheckPointProtocols(competitionPath, checkPointPath).flatMap { it.protocol }
        val checkPoints = loadTransaction(checkPointPath) { loadTimestamps() }
        transaction {
            assertEquals(checkPointsProtocols.toSet(), checkPoints.toSet())
        }
        competitionPath.toFile().deleteRecursively()
    }
}