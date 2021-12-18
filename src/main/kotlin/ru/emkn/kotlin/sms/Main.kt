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
        "jdbc:h2:./data/testDB;AUTO_SERVER=TRUE", driver = "org.h2.Driver",
        user = "scott", password = "tiger"
    )

    transaction {
        SchemaUtils.create(
            ParticipantTable,
            GroupTable,
            TeamTable,
            RouteTable,
            CheckpointTable,
            RouteCheckpointsTable,
            TossTable
        )
//        val checkpoint1 = Checkpoint.create("first check", 1)
//        val checkpoint2 = Checkpoint.create("second check", 2)
//        val route = Route.new {
//            name = "Main route"
//        }
//        RouteCheckpointsTable.insert {
//            it[this.route] = route.id
//            it[checkpoint] = checkpoint1.id
//        }
//        RouteCheckpointsTable.insert {
//            it[this.route] = route.id
//            it[checkpoint] = checkpoint2.id
//        }
//        Team.new {
//            name = "Samara"
//        }
//        Group.new {
//            name = "M10"
//            routeID = Route.all().first().id
//        }
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
}
