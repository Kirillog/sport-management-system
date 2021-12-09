
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import org.junit.jupiter.api.Test
import ru.emkn.kotlin.sms.objects.*
import ru.emkn.kotlin.sms.targets.calculateResultsForTeams
import ru.emkn.kotlin.sms.targets.teamResultsTarget
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import kotlin.io.path.Path
import kotlin.test.assertContentEquals

internal class TeamResultTest {
    private val path = Path("src/test/resources/competition-3")

    private val participants = listOf(
        Participant("Анна", "Сосницкая", 2013, "Ж10", "0-ПСКОВ", "1р", 101, LocalTime.of(12, 0, 0)),
        Participant("АРТЁМ", "КАЧНОВ", 2008, "МЖ14", "ВЕЛИКОЛУКСКИЙ РАЙОН", null, 128, LocalTime.of(12, 5, 0)),
        Participant("АЛЕКСАНДРА", "ЛОВЦОВА", 2014, "МЖ14", "ВЕЛИКИЕ ЛУКИ", null, 102, LocalTime.of(12, 10, 0)),
        Participant("ЗАХАР", "МАЖАРОВ", 2012, "М10", "ВЕЛИКОЛУКСКИЙ РАЙОН", null, 121, LocalTime.of(13, 45, 0)),
        Participant("РОМАН", "МЕРЦАЛОВ", 2013, "М10", "0-ПСКОВ", "3р", 125, LocalTime.of(13, 55, 0))
    )

    private val cours = listOf(
        Route("МЖ10 14", listOf(CheckPoint(1), CheckPoint(2), CheckPoint(3), CheckPoint(4)))
    )

    private val teams = listOf(
        Team("0-ПСКОВ", listOf(participants[0], participants[4])),
        Team("ВЕЛИКОЛУКСКИЙ РАЙОН", listOf(participants[1], participants[3])),
        Team("ВЕЛИКИЕ ЛУКИ", listOf(participants[2]))
    )

    private val groups = listOf(
        Group("МЖ14", cours[0], listOf(participants[1], participants[2])),
        Group("М10", cours[0], listOf(participants[3], participants[4])),
        Group("Ж10", cours[0], listOf(participants[0]))
    )

    private val competition = Competition(
        Event("Test event", LocalDate.now()),
        path.resolve("competition"),
        teams,
        groups
    )

    @Test
    fun calculateResultsForTeamTest() {
        var delta = 5L
        participants.forEachIndexed { index, participant ->
            val startTime = participant.getStartTime()
            participant.finishTime = startTime.plusMinutes(delta)
            participant.positionInGroup = Participant.PositionInGroup(index + 1, Duration.ofMinutes(5))
            delta += 10L
        }
        calculateResultsForTeams(competition)
        val teamResults = competition.teams.map { it.getResult() }
        assertContentEquals(teamResults, listOf(171, 200, 33))
    }

    @Test
    fun teamResultTargetTest() {
        val competition = teamResultsTarget(path)
        val expectedResult = listOf(
            listOf("Место", "Команда", "Результат"),
            listOf("1", "ЦДиЮТиЭ", "285"),
            listOf("2", "ПСКОВ,РУСЬ", "183"),
            listOf("3", "ПСКОВ", "92")
        ).flatten()
        val result = csvReader().readAll(path.resolve("protocols/teamResults.csv").toFile()).flatten()
        assertContentEquals(expectedResult, result)
    }
}