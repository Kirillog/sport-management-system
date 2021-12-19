package ru.emkn.kotlin.sms

import com.xenomachina.argparser.mainBody
import mu.KotlinLogging
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import ru.emkn.kotlin.sms.controller.CompetitionController
import ru.emkn.kotlin.sms.model.*
import kotlin.io.path.Path

private val logger = KotlinLogging.logger {}

fun main(args: Array<String>): Unit = mainBody {

    logger.info { "Program started" }
    Database.connect(
        "jdbc:h2:./data/testDB", driver = "org.h2.Driver",
        user = "scott", password = "tiger"
    )

    val dbTables = listOf(
        ParticipantTable,
        GroupTable,
        TeamTable,
        RouteTable,
        CheckpointTable,
        RouteCheckpointsTable,
        TossTable,
        TimestampTable,
        ResultTable
    )

    transaction {
        dbTables.forEach {
            SchemaUtils.create(it)
//            it.deleteAll()
        }
    }

    val path = Path("competitions/competition-1")
    CompetitionController.announceFromPath(
        event = path.resolve("input/event.csv"),
        checkpoints = path.resolve("input/checkpoints.csv"),
        routes = path.resolve("input/courses.csv")
    )

    CompetitionController.registerFromPath(
        group = path.resolve("input/classes.csv"),
        team = path.resolve("applications")
    )

    CompetitionController.toss()
    CompetitionController.saveTossToPath(
        toss = path.resolve("protocols/toss.csv")
    )

    CompetitionController.registerResultsFromPath(checkPoints = path.resolve("checkpoints"))

    CompetitionController.calculateResult()
    CompetitionController.saveResultsToPath(path.resolve("protocols/results.csv"))
    CompetitionController.saveTeamResultsToPath(path.resolve("protocols/team_results.csv"))
}
