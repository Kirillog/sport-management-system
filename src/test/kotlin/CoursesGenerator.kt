import ru.emkn.kotlin.sms.FileType
import ru.emkn.kotlin.sms.io.MultilineWritable
import ru.emkn.kotlin.sms.io.Writer
import ru.emkn.kotlin.sms.objects.CheckPoint
import ru.emkn.kotlin.sms.objects.Course
import java.io.File
import kotlin.random.Random

fun generateCourse(id: Int, maxCheckPointsCount: Int, courseLength: Int, random: Random): Course {
    val name = "course$id"
    val checkpointsInCourse = MutableList(maxCheckPointsCount) { CheckPoint(it) }
    checkpointsInCourse.shuffle(random)
    while (checkpointsInCourse.size > courseLength)
        checkpointsInCourse.removeAt(checkpointsInCourse.lastIndex)
    return Course(name, checkpointsInCourse)
}

class WritableCourses(private val courses: List<Course>) : MultilineWritable {
    override fun toMultiline(): List<List<String>> {
        val maxLength = courses.maxOf { it.checkPoints.size }
        val result = mutableListOf(listOf("Название") + List(maxLength) { "${it + 1}" })
        for (course in courses) {
            result.add(listOf(course.name) + course.checkPoints.map { it.id.toString() })
        }
        return result
    }
}


fun main() {
    val random = Random(0)
    val coursesList = List(10) {
        val courseLength = random.nextInt(2, 11)
        generateCourse(it, 10, courseLength, random)
    }
    val file = File("test_generator/course.csv")
    val writer = Writer(file, FileType.CSV)
    writer.add(WritableCourses(coursesList))
    writer.write()
}