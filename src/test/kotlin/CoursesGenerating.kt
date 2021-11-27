
import ru.emkn.kotlin.sms.FileType
import ru.emkn.kotlin.sms.io.Writer
import ru.emkn.kotlin.sms.objects.CheckPoint
import ru.emkn.kotlin.sms.objects.Course
import java.io.File
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.random.Random

fun generateCourse(id: Int, maxCheckPointsCount: Int, courseLength: Int, random: Random): Course {
    val name = "course$id"
    val checkpointsInCourse = MutableList(maxCheckPointsCount) { CheckPoint(it) }
    checkpointsInCourse.shuffle(random)
    while (checkpointsInCourse.size > courseLength)
        checkpointsInCourse.removeAt(checkpointsInCourse.lastIndex)
    return Course(name, checkpointsInCourse)
}


fun generateCourses(path : Path, coursesAmount : Int = 10, maxCheckPointsCount: Int = 10, maxCourseLength : Int = 11, random: Random = Random(0)) : List<Course> {
    val coursesList = List(coursesAmount) {
        val courseLength = random.nextInt(2, maxCourseLength)
        generateCourse(it, maxCheckPointsCount, courseLength, random)
    }
    val file = path.resolve("courses.csv").toFile()
    val writer = Writer(file, FileType.CSV)
    val maxLength = coursesList.maxOf { it.checkPoints.size }
    writer.add(listOf("Название") + List(maxLength) { "${it + 1}" })
    writer.addAllLines(coursesList)
    writer.write()
    return coursesList
}

fun main() {
    File("test_generator").mkdir()
    generateCourses(Path("test_generator"))
}