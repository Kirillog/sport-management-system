package ru.emkn.kotlin.sms.targets

import ru.emkn.kotlin.sms.FileType
import ru.emkn.kotlin.sms.io.Writer
import ru.emkn.kotlin.sms.objects.Competition
import java.nio.file.Path
import java.time.Duration
import kotlin.math.max

fun teamResultsTarget(path : Path) {
    val competition = prepareCompetition(path)
    calculateResultsForTeams(competition)

    val writer = Writer(competition.path.resolve("protocols/teamResults.csv").toFile(), FileType.CSV)
    writer.add(
        listOf(
            "Место", "Команда", "Результат"
        )
    )
    competition.teams.sortedByDescending { it.result }.forEachIndexed { index, team ->
        writer.add(team) {
            listOf(listOf((index + 1).toString(), it.name, it.getResult().toString()))
        }
    }
    writer.write()
}

fun calculateResultsForTeams(
    competition: Competition,
) {
    val groupByParticipant = getGroupByParticipant(competition.groups)
    competition.teams.forEach { team ->
        team.result = team.members.sumOf {
            if (it.place == null)
                0
            else {
                val group = groupByParticipant[it]
                requireNotNull(group) { "Group of $it hasn't been found" }
                val groupLeaderResult = group.members[0].getDurationTime()
                val time = it.getDurationTime()
                max(0, (100 * (2 - time / groupLeaderResult)).toLong())
            }
        }
    }
}

operator fun Duration.div(other: Duration): Double {
    return this.seconds.toDouble() / other.seconds
}
