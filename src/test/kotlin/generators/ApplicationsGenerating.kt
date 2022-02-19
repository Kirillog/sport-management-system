package generators

import org.jetbrains.exposed.sql.transactions.transaction
import ru.emkn.kotlin.sms.FileType
import ru.emkn.kotlin.sms.io.Writer
import ru.emkn.kotlin.sms.model.Participant
import ru.emkn.kotlin.sms.model.Team
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.random.Random

val possibleGrades = listOf("1р", "2р", "3р", "1ю", "2ю", "3ю")
val possibleGenders = listOf("М", "Ж")
val possibleGroupAges = listOf(10, 12, 14, 16, 18, 20, 22, 24, 26, 28, 30, 40, 45, 50, 60, 70, 80)

fun Generator.generateParticipant(id: Int, teamName: String, random: Random): Participant {
    val name = "name$id-$teamName"
    val surname = "surname$id"
    val birthdayYear = random.nextInt(1950, 2012)
    val group = possibleGenders.random() + possibleGroupAges.random().toString()
    val grade: String? =
        if (random.nextInt(10) == 0)
            possibleGrades[random.nextInt(possibleGrades.size)]
        else
            null
    return Participant.create(name, surname, birthdayYear, group, teamName, grade)
}

fun Generator.generateTeam(id: Int, teamSize: Int, random: Random): Team {
    val teamName = "team$id"
    val team = Team.create(teamName)
    List(teamSize) {
        generateParticipant(it, teamName, random)
    }
    return team
}

fun Generator.generateApplication(applicationPath: Path, id: Int, applicationSize: Int, random: Random): Team {
    val file = applicationPath.toFile()
    val team = generateTeam(id, applicationSize, random)
    val writer = Writer(file, FileType.CSV)
    writer.addAll(Team.formatterForApplications(team))
    writer.write()
    return team
}

fun Generator.generateApplications(
    applicationsDirectory: Path,
    applicationsCount: Int = 3,
    maxApplicationSize: Int = 10,
    random: Random = Random(0)
): List<Team> {
    return transaction {
        val dir = applicationsDirectory.toFile()
        if (!dir.isDirectory) {
            throw Exception("Path \"$dir\" is not a directory")
        }

        val teamsList = mutableListOf<Team>()
        for (i in 1..applicationsCount) {
            val applicationSize = random.nextInt(1, maxApplicationSize)
            teamsList.add(generateApplication(applicationsDirectory.resolve("$i.csv"), i, applicationSize, random))
        }
        teamsList
    }
}

fun main() {
    val path = Path("test_generator")
    generate(path) {
        generateGroups(path)
        generateApplications(path)
    }
}
