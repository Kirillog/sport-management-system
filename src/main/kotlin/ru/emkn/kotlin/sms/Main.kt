package ru.emkn.kotlin.sms

import com.xenomachina.argparser.mainBody
import mu.KotlinLogging
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import ru.emkn.kotlin.sms.model.*

private val logger = KotlinLogging.logger {}

fun main(args: Array<String>): Unit = mainBody {

    logger.info { "Program started" }
    Database.connect(
        "jdbc:h2:./data/testDB;AUTO_SERVER=TRUE", driver = "org.h2.Driver",
        user = "scott", password = "tiger"
    )

    transaction {
//        // print sql to std-out
        addLogger(StdOutSqlLogger)
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

    val toss = Toss()

    val participant = transaction {
        Participant.new {
            name = "Petia"
            surname = "Pupkin"
            team = Team.find { TeamTable.name eq "Samara" }.first()
            group = Group.find { GroupTable.name eq "M10" }.first()
            birthdayYear = 2020
            tossID = toss.id
        }
    }
    println("${participant.name}, ${participant.surname}")
    participant.name = "kek"
    println("${participant.name}, ${participant.surname}")

//    val path = Path("competitions/competition-1")
//    CompetitionController.announceFromPath(
//        path.resolve("input/event.csv"),
//        path.resolve("input/courses.csv")
//    )
//
//    CompetitionController.registerFromPath(
//        path.resolve("input/classes.csv"),
//        path.resolve("applications")
//    )
//
//    CompetitionController.toss()
//    val writer = Writer(File("test.csv"), FileType.CSV)
//    CompetitionController.saveToss(writer)
}
