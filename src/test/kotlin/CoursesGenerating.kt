import org.jetbrains.exposed.sql.transactions.transaction
import ru.emkn.kotlin.sms.FileType
import ru.emkn.kotlin.sms.io.Writer
import ru.emkn.kotlin.sms.model.Checkpoint
import ru.emkn.kotlin.sms.model.Route
import ru.emkn.kotlin.sms.model.RouteType
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.random.Random

fun Generator.generateCourse(
    id: Int,
    checkPointList: List<Checkpoint>,
    courseLength: Int,
    random: Random
): Route {
    val name = "course$id"
    val checkpointsInCourse = checkPointList.toMutableList()
    checkpointsInCourse.shuffle(random)
    while (checkpointsInCourse.size > courseLength)
        checkpointsInCourse.removeAt(checkpointsInCourse.lastIndex)
    val type = when (random.nextBoolean()) {
        false ->
            RouteType.SELECTIVE
        true ->
            RouteType.FULL
    }
    val amount = when (type) {
        RouteType.FULL ->
            checkpointsInCourse.size
        RouteType.SELECTIVE ->
            random.nextInt(1, checkpointsInCourse.size + 1)
    }
    return Route.create(name, checkpointsInCourse, type, amount)
}

fun Generator.generateCheckpoints(path: Path, random: Random = Random(0), n: Int = 11): List<Checkpoint> {
    return transaction {
        val file = path.resolve("checkpoints.csv").toFile()
        val writer = Writer(file, FileType.CSV)
        writer.add(listOf("Номер К/П", "Стоимость"))
        val checkpoints = List(n) { Checkpoint.create(it.toString(), random.nextInt(10)) }
        writer.addAllLines(checkpoints)
        writer.write()
        checkpoints
    }
}


fun Generator.generateCourses(
    path: Path,
    coursesAmount: Int = 10,
    maxCheckPointsCount: Int = 10,
    maxCourseLength: Int = 11,
    random: Random = Random(0)
): List<Route> {
    return transaction {
        val checkPointList = generateCheckpoints(path, random, maxCheckPointsCount)
        val coursesList = List(coursesAmount) {
            val courseLength = random.nextInt(2, maxCourseLength)
            generateCourse(it, checkPointList, courseLength, random)
        }
        val file = path.resolve("courses.csv").toFile()
        val writer = Writer(file, FileType.CSV)
        val maxLength = coursesList.maxOf { it.checkpoints.count() }.toInt()
        writer.add(listOf("Название", "Тип", "Количество К/П") + List(maxLength) { "${it + 1}" })
        writer.addAllLines(coursesList)
        writer.write()
        coursesList
    }
}

fun main() {
    val path = Path("test_generator")
    generate(path) {
        generateCourses(path)
    }
}