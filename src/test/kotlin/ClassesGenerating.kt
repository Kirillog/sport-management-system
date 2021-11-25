import ru.emkn.kotlin.sms.FileType
import ru.emkn.kotlin.sms.io.MultilineWritable
import ru.emkn.kotlin.sms.io.Writer
import ru.emkn.kotlin.sms.objects.Course
import java.io.File
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

class WritableCoursesForGroups(private val coursesForGroups: List<Pair<String, Course>>) : MultilineWritable {
    override fun toMultiline(): List<List<String>> {
        return listOf(listOf("Группа", "дистанция")) +
                coursesForGroups.map { (group, course) ->
                    listOf(group, course.name)
                }
    }
}

fun generateCoursesForGroups(
    groups: List<String>,
    maxCheckPointsCount: Int,
    random: Random
): List<Pair<String, Course>> {
    val result = mutableListOf<Pair<String, Course>>()
    for (groupId in groups.indices) {
        val courseLength = random.nextInt(2, maxCheckPointsCount + 1)
        val course = generateCourse(groupId, maxCheckPointsCount, courseLength, random)
        result.add(Pair(groups[groupId], course))
    }
    return result
}

fun main() {
    val random = Random(0)
    val allGroups = getAllGroups()
    val coursesForGroups = generateCoursesForGroups(allGroups, 10, random)
    val writableCoursesForGroups = WritableCoursesForGroups(coursesForGroups)
    val classesFile = File("test_generator/classes.csv")
    val classesWriter = Writer(classesFile, FileType.CSV)
    classesWriter.add(writableCoursesForGroups)
    classesWriter.write()
    val coursesFile = File("test_generator/course.csv")
    val coursesWriter = Writer(coursesFile, FileType.CSV)
    coursesWriter.add(WritableCourses(coursesForGroups.map { it.second }))
    coursesWriter.write()

}