import ru.emkn.kotlin.sms.FileType
import ru.emkn.kotlin.sms.io.MultilineWritable
import ru.emkn.kotlin.sms.io.Writer
import ru.emkn.kotlin.sms.objects.Course
import ru.emkn.kotlin.sms.objects.Group
import ru.emkn.kotlin.sms.objects.Participant
import ru.emkn.kotlin.sms.objects.Team
import java.io.File
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.createDirectory
import kotlin.random.Random

fun getAllGroups(): List<String> {
    val result = mutableListOf<String>()
    for (gender in listOf("М", "Ж")) {
        for (age in ages) {
            result.add("$gender$age")
        }
    }
    return result
}

class WritableCoursesForGroups(private val coursesForGroups: Map<String, Course>) : MultilineWritable {
    override fun toMultiline(): List<List<String>> {
        return listOf(listOf("Группа", "Дистанция")) +
                coursesForGroups.map { (group, course) ->
                    listOf(group, course.name)
                }
    }
}

fun generateCoursesForGroups(
    path: Path,
    groups: List<String> = getAllGroups(),
    maxCheckPointsCount: Int = 10,
    random: Random = Random(0)
): Map<String, Course> {
    val result = mutableMapOf<String, Course>()
    for (groupId in groups.indices) {
        val courseLength = random.nextInt(2, maxCheckPointsCount + 1)
        val course = generateCourse(groupId, maxCheckPointsCount, courseLength, random)
        result[groups[groupId]] = course
    }
    val writableCoursesForGroups = WritableCoursesForGroups(result)
    val classesFile = path.resolve("classes.csv").toFile()
    val classesWriter = Writer(classesFile, FileType.CSV)
    classesWriter.add(writableCoursesForGroups)
    classesWriter.write()
    val coursesFile = path.resolve("courses.csv").toFile()
    val coursesWriter = Writer(coursesFile, FileType.CSV)
    coursesWriter.add(WritableCourses(result.map { it.value }))
    coursesWriter.write()
    return result
}

fun generateGroups(currentPath: Path, generatedTeams: List<Team>): List<Group> {
    val generatedGroups = generatedTeams.flatMap { it.members }.groupBy(Participant::group)
    val courses = generateCoursesForGroups(currentPath, generatedGroups.keys.toList())
    return generatedGroups.map { Group(it.key, courses[it.key]!!, it.value) }
}

fun main() {
    val path = Path("test_generator")
    path.createDirectory()
    generateCoursesForGroups(path)
}