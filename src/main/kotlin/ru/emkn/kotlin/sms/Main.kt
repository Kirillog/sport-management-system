package ru.emkn.kotlin.sms

import com.xenomachina.argparser.mainBody
import mu.KotlinLogging
import ru.emkn.kotlin.sms.controller.CompetitionController
import kotlin.io.path.Path

import ru.emkn.kotlin.sms.controller.*
import ru.emkn.kotlin.sms.io.Writer
import java.io.File

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction

private val logger = KotlinLogging.logger {}

fun main(args: Array<String>): Unit = mainBody {

    Database.connect("jdbc:h2:./data/testDB", driver = "org.h2.Driver")

    transaction {
        // print sql to std-out
        addLogger(StdOutSqlLogger)
    }

    logger.info { "Program started" }
}
