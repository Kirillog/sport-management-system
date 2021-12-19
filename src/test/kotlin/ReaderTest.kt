//
//import org.junit.jupiter.api.AfterAll
//import org.junit.jupiter.api.BeforeAll
//import org.junit.jupiter.api.RepeatedTest
//import org.junit.jupiter.api.TestInstance
//import ru.emkn.kotlin.sms.io.FileLoader
//import java.nio.file.Path
//import kotlin.io.path.Path
//import kotlin.io.path.createDirectory
//import kotlin.test.assertEquals
//
//
//// TODO up to 3
//private const val repetitionNumber = 3
//
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//internal class FileLoaderTest {
//    private val path = Path("src/test/.temp/")
//
//    private fun init(subDirectory: String = "input"): Path {
//        val currentPath = path.resolve(subDirectory)
//        currentPath.createDirectory()
//        return currentPath
//    }
//
//    @BeforeAll
//    fun setUp() {
//        path.createDirectory()
//    }
//
//    @AfterAll
//    fun setDown() {
//        path.toFile().deleteRecursively()
//    }
//
//    @RepeatedTest(repetitionNumber)
//    fun formTeamsListTest() {
//        val currentPath = init("applications")
//        val generatedTeams = generateApplications(currentPath, 10, 15).toSet()
//        val teams = FileLoader(path).loadTeams()
//        assertEquals(generatedTeams, teams)
//        currentPath.toFile().deleteRecursively()
//    }
//
//    @RepeatedTest(repetitionNumber)
//    fun formCoursesListTest() {
//        val currentPath = init()
//        val generatedCourses = generateCourses(currentPath).toSet()
//        val courses = FileLoader(path).loadRoutes()
//        assertEquals(generatedCourses, courses)
//        currentPath.toFile().deleteRecursively()
//
//    }
//
//    @RepeatedTest(repetitionNumber)
//    fun formEmptyGroupsTest() {
//        val currentPath = init()
//        val generatedMap = generateCoursesForGroups(currentPath).mapValues { it.value.name }
//        val map = FileLoader(path).loadGroups().associate { group ->
//            Pair(group.name, group.route.name)
//        }
//        assertEquals(generatedMap, map)
//        currentPath.toFile().deleteRecursively()
//
//    }
//
//    @RepeatedTest(repetitionNumber)
//    fun formGroupsListTest() {
//        val dir = init("test/")
//        val currentPath = init("test/input")
//        val applicationPath = init("test/applications")
//        val groups = FileLoader(dir).loadGroups()
//        val generatedTeams = generateApplications(applicationPath)
//        val generatedGroups = generateGroups(currentPath, generatedTeams).toSet()
//        assertEquals(generatedGroups, groups)
//        dir.toFile().deleteRecursively()
//    }
//
//    @RepeatedTest(repetitionNumber)
//    fun formEventTest() {
//        val currentPath = init()
//        val eventsList = generateEvents(currentPath)
//        val event = FileLoader(path).loadEvent()
//        assertEquals(eventsList[0], event)
//        currentPath.toFile().deleteRecursively()
//    }
//
////    @RepeatedTest(repetitionNumber)
////    fun formTossedGroupsListTest() {
////        val competitionPath = init("competition")
////        val initPath = init("competition/input")
////        val applicationPath = init("competition/applications")
////        init("competition/protocols")
////        generateGroups(initPath, generateApplications(applicationPath))
////        generateEvents(initPath)
////        val generatedGroups = tossTarget(competitionPath).groups.toSet()
////        val groups = formTossedGroups(competitionPath).toSet()
////        assertEquals(generatedGroups, groups)
////        competitionPath.toFile().deleteRecursively()
////    }
////
////    @RepeatedTest(repetitionNumber)
////    fun formTimeStampsListTest() {
////        val competitionPath = init("competition")
////        val initPath = init("competition/input")
////        val applicationPath = init("competition/applications")
////        val checkPointPath = init("competition/checkpoints")
////        init("competition/protocols")
////        generateGroups(initPath, generateApplications(applicationPath))
////        generateEvents(initPath)
////        tossTarget(competitionPath)
////        val checkPointsProtocols = generateCheckPointProtocols(competitionPath, checkPointPath)
////        val checkPoints = formTimestamps(competitionPath)
////        assertEquals(checkPointsProtocols.flatMap { it.protocol }.toSet(), checkPoints.toSet())
////        competitionPath.toFile().deleteRecursively()
////    }
//}