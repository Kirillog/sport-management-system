import ru.emkn.kotlin.sms.FileType
import ru.emkn.kotlin.sms.io.MultilineWritable
import ru.emkn.kotlin.sms.io.Writer
import ru.emkn.kotlin.sms.objects.Participant
import ru.emkn.kotlin.sms.objects.Team
import java.io.File
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

class WriteableTeam(private val team: Team) : MultilineWritable {
    override fun toMultiline(): List<List<String>> {
        return listOf(listOf(team.name)) +
                team.members.map {
                    val result = mutableListOf(it.name, it.surname, it.birthdayYear.toString())
                    val grade = it.grade
                    if (grade != null)
                        result.add(grade)
                    result
                }
    }
}

fun generateApplication(applicationPath: String, id: Int, applicationSize: Int, random: Random) {
    val file = File(applicationPath)
    val team = generateTeam(id, applicationSize, random)
    val writer = Writer(file, FileType.CSV)
    writer.add(WriteableTeam(team))
    writer.write()
}

fun generateApplications(
    applicationsDirectory: String,
    applicationsCount: Int,
    maxApplicationSize: Int,
    random: Random
) {
    val dir = File(applicationsDirectory)

    if (!dir.isDirectory) {
        throw Exception("Path \"$dir\" is not a directory")
    }

    for (i in 1..applicationsCount) {
        val applicationSize = random.nextInt(1, maxApplicationSize)
        generateApplication("$applicationsDirectory/$i.csv", i, applicationSize, random)
    }
}

fun main() {
    generateApplications("test_generator", 3, 10, Random(1))
}
