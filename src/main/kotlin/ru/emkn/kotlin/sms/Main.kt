package ru.emkn.kotlin.sms

import com.xenomachina.argparser.mainBody
import mu.KotlinLogging
<<<<<<< HEAD
import ru.emkn.kotlin.sms.controller.CompetitionController
=======
import org.jetbrains.exposed.sql.*
>>>>>>> a132659 (db skeleton implementation)
import kotlin.io.path.Path

import ru.emkn.kotlin.sms.controller.*
import ru.emkn.kotlin.sms.io.Writer
import java.io.File

import org.jetbrains.exposed.sql.transactions.transaction
import ru.emkn.kotlin.sms.model.*

private val logger = KotlinLogging.logger {}

fun main(args: Array<String>): Unit = mainBody {

    logger.info { "Program started" }
    Database.connect("jdbc:h2:./data/testDB;AUTO_SERVER=TRUE", driver = "org.h2.Driver",
    user = "scott", password = "tiger")

//    transaction {
//        // print sql to std-out
//        addLogger(StdOutSqlLogger)
//        SchemaUtils.create(Participants, Groups, Teams)
//        Teams.insert {
//            it[name] = "Samara"
//        }
//        Groups.insert {
//            it[name] = "M10"
//        }
//        Participants.insert {
//            it[name] = "Vasya"
//            it[surname] = "Pupkin"
//            it[teamID] = Team.find { Teams.name eq "Samara" }.first().id
//            it[groupID] = Group.find { Groups.name eq "M10" }.first().id
//            it[birthdayYear] = 2021
//        }
//    }
//    return@mainBody

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
