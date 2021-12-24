import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import ru.emkn.kotlin.sms.FileType
import ru.emkn.kotlin.sms.io.Writer
import ru.emkn.kotlin.sms.model.*
import java.io.File
import java.io.IOException
import java.nio.file.Path
import kotlin.io.path.*
import kotlin.random.Random

fun getAllGroups(): List<String> {
    val result = mutableListOf<String>()
    for (gender in listOf("М", "Ж")) {
        for (age in possibleGroupAges) {
            result.add("$gender$age")
        }
    }
    return result
}

fun Generator.generateGroups(
    path: Path,
    groupNames: List<String> = getAllGroups(),
    maxCheckPointsCount: Int = 10,
    random: Random = Random(0)
): List<Group> {
    return transaction {
        val coursesList = generateCourses(path, groupNames.size, maxCheckPointsCount, maxCheckPointsCount)
        val resultTypes = List(groupNames.size) {
            if (random.nextBoolean())
                ResultType.TIME
            else
                ResultType.WEIGHT
        }
        val groups = groupNames.mapIndexed { index, name ->
            Group.create(name, resultTypes[index], coursesList[index].name)
        }
        val classesFile = path.resolve("classes.csv").toFile()
        val classesWriter = Writer(classesFile, FileType.CSV)
        classesWriter.add(listOf("Название группы", "Дистанция", "Результат"))
        classesWriter.addAll(groups.mapIndexed { index, group ->
            listOf(group.name, group.route.name, resultTypes[index].russian)
        })
        classesWriter.write()
        groups
    }
}

fun <T> generate(path: Path, gen: Generator.() -> T): T {
    if (path.exists() && !path.isDirectory()) {
        throw IOException("path $path is not a directory")
    }
    if (path.notExists())
        path.createDirectory()

    Database.connect("jdbc:h2:./${path}/testDB", driver = "org.h2.Driver")

    val dbTables = listOf(
        RouteCheckpointsTable,
        TossTable,
        PersonalResultTable,
        TeamResultTable,
        TimestampTable,
        CheckpointTable,
        ParticipantTable,
        GroupTable,
        RouteTable,
        TeamTable
    )

    transaction {
        dbTables.forEach {
            SchemaUtils.create(it)
        }
    }

    val gennedObject = Generator.gen()
    File("./${path}/testDB.mv.db").delete()
    return gennedObject
}

fun main() {
    val path = Path("test_generator")
    generate(path) {
        generateGroups(path)
    }
}