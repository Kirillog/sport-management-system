import mu.KotlinLogging
import ru.emkn.kotlin.sms.FileType
import ru.emkn.kotlin.sms.io.Writer
import ru.emkn.kotlin.sms.model.Group
import ru.emkn.kotlin.sms.model.Participant
import ru.emkn.kotlin.sms.model.Route
import ru.emkn.kotlin.sms.model.Team
import java.nio.file.Path
import kotlin.io.path.*
import kotlin.math.max
import kotlin.random.Random

private val logger = KotlinLogging.logger {}

fun getAllGroups(): List<String> {
    val result = mutableListOf<String>()
    for (gender in listOf("М", "Ж")) {
        for (age in possibleGroupAges) {
            result.add("$gender$age")
        }
    }
    return result
}

fun generateCoursesForGroups(
    path: Path,
    groups: List<String> = getAllGroups(),
    maxCheckPointsCount: Int = 10,
    random: Random = Random(0)
): Map<String, Route> {
    val result = mutableMapOf<String, Route>()
    var maxLength = 0
    for (groupId in groups.indices) {
        val courseLength = random.nextInt(2, maxCheckPointsCount + 1)
        val course = generateCourse(groupId, maxCheckPointsCount, courseLength, random)
        maxLength = max(maxLength, courseLength)
        result[groups[groupId]] = course
    }
    val classesFile = path.resolve("classes.csv").toFile()
    val classesWriter = Writer(classesFile, FileType.CSV)
    classesWriter.add(listOf("Группа", "Дистанция"))
    classesWriter.addAll(result.map { (group, course) ->
        listOf(group, course.name)
    })
    classesWriter.write()
    val coursesFile = path.resolve("courses.csv").toFile()
    val coursesWriter = Writer(coursesFile, FileType.CSV)
    coursesWriter.add(listOf("Название") + List(maxLength) { "${it + 1}" })
    coursesWriter.addAllLines(result.map { it.value })
    coursesWriter.write()
    return result
}

fun generateGroups(currentPath: Path, generatedTeams: List<Team>): List<Group> {
    val generatedGroups = generatedTeams.flatMap { it.members }.groupBy(Participant::group)
    val courses = generateCoursesForGroups(currentPath, generatedGroups.keys.toList())
    return generatedGroups.map {
        val course = courses[it.key] ?: throw IllegalStateException("course has to be found")
        Group(it.key, course, it.value)
    }
}

fun main() {
    val path = Path("test_generator")
    if (path.exists() && !path.isDirectory()) {
        logger.error { "path $path is not a directory" }
        return
    }
    if (path.notExists())
        path.createDirectory()
    generateCoursesForGroups(path)
}