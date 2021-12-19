package ru.emkn.kotlin.sms

import com.xenomachina.argparser.mainBody
import mu.KotlinLogging
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import ru.emkn.kotlin.sms.controller.CompetitionController
import ru.emkn.kotlin.sms.model.*
import java.io.File
import kotlin.io.path.Path

private val logger = KotlinLogging.logger {}

fun main(args: Array<String>): Unit = mainBody {

    logger.info { "Program started" }
    File("./data/testDB.mv.db").delete()
    Database.connect("jdbc:h2:./data/testDB", driver = "org.h2.Driver")

    val dbTables = listOf(
        RouteCheckpointsTable,
        TossTable,
        ResultTable,
        TimestampTable,
        CheckpointTable,
        ParticipantTable,
        GroupTable,
        RouteTable,
        TeamTable,
    )

    transaction {
        dbTables.forEach {
            SchemaUtils.create(it)
        }
    }

    val path = Path("competitions/competition-1")
    CompetitionController.announceFromPath(
        event = path.resolve("input/event.csv"),
        checkpoints = path.resolve("input/checkpoints.csv"),
        routes = path.resolve("input/courses.csv")
    )
    logger.info { "Competition announced" }

    CompetitionController.registerFromPath(
        group = path.resolve("input/classes.csv"),
        team = path.resolve("applications")
    )
    logger.info { "Competition registration out" }

    CompetitionController.toss()
    CompetitionController.saveTossToPath(
        toss = path.resolve("protocols/toss.csv")
    )
    logger.info { "Competition tossed" }

    CompetitionController.registerResultsFromPath(checkPoints = path.resolve("checkpoints"))
    logger.info { "Timestamps loaded" }

    CompetitionController.calculateResult()
    logger.info { "Results calculated" }

    CompetitionController.saveResultsToPath(path.resolve("protocols/results.csv"))
    CompetitionController.saveTeamResultsToPath(path.resolve("protocols/team_results.csv"))
    logger.info { "Results saved to files" }
}
