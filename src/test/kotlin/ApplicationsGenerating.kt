import ru.emkn.kotlin.sms.FileType
import ru.emkn.kotlin.sms.io.Writer
import ru.emkn.kotlin.sms.objects.Participant
import ru.emkn.kotlin.sms.objects.Team
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.random.Random

val grades = listOf("1р", "2р", "3р", "1ю", "2ю", "3ю")
val ages = listOf(10, 12, 14, 16, 18, 20, 22, 24, 26, 28, 30, 40, 45, 50, 60, 70, 80)

fun generateParticipant(id: Int, teamName: String, random: Random): Participant {
    val name = "name$id"
    val surname = "surname$id"
    val birthdayYear = random.nextInt(1950, 2012)
    val group = (if (random.nextBoolean()) "М" else "Ж") +
            "${ages.first { it >= 2021 - birthdayYear }}"
    val grade: String? =
        if (random.nextInt(10) == 0)
            grades[random.nextInt(grades.size)]
        else
            null
    return Participant(name, surname, birthdayYear, group, teamName, grade)
}

fun generateTeam(id: Int, teamSize: Int, random: Random): Team {
    val name = "team$id"
    val members = List(teamSize) {
        generateParticipant(it, name, random)
    }
    return Team(name, members)
}

fun generateApplication(applicationPath: Path, id: Int, applicationSize: Int, random: Random): Team {
    val file = applicationPath.toFile()
    val team = generateTeam(id, applicationSize, random)
    val writer = Writer(file, FileType.CSV)
    writer.add(team) {
        listOf(listOf(it.name)) +
                listOf(listOf("Имя", "Фамилия", "Г.р.", "Группа", "Разр.")) +
                it.members.map { participant ->
                    val result = mutableListOf(
                        participant.name,
                        participant.surname,
                        participant.birthdayYear.toString(),
                        participant.group
                    )
                    val grade = participant.grade
                    if (grade != null)
                        result.add(grade)
                    else
                        result.add("")
                    result
                }
    }
    writer.write()
    return team
}

fun generateApplications(
    applicationsDirectory: Path,
    applicationsCount: Int = 3,
    maxApplicationSize: Int = 10,
    random: Random = Random(0)
): List<Team> {
    val dir = applicationsDirectory.toFile()
    if (!dir.isDirectory) {
        throw Exception("Path \"$dir\" is not a directory")
    }

    val teamsList = mutableListOf<Team>()
    for (i in 1..applicationsCount) {
        val applicationSize = random.nextInt(1, maxApplicationSize)
        teamsList.add(generateApplication(applicationsDirectory.resolve("$i.csv"), i, applicationSize, random))
    }
    return teamsList
}

fun main() {
    generateApplications(Path("test_generator"))
}
