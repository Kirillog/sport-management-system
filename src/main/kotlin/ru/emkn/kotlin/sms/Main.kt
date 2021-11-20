package ru.emkn.kotlin.sms

//https://github.com/xenomachina/kotlin-argparser
import com.xenomachina.argparser.*
//https://github.com/doyaaaaaken/kotlin-csv
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
//https://github.com/Kotlin/kotlinx-datetime
import kotlinx.datetime.*

import com.sksamuel.hoplite.ConfigLoader
import mu.KotlinLogging
import java.io.File

private val logger = KotlinLogging.logger {}

class MyArgs(parser: ArgParser) {
    val verbose by parser.flagging("-v", "--verbose", help="enable verbose mode")

    val name by parser.storing("-n", "--name", help="user name").default<String>("kek")

    val count by parser.storing("-c", "--count", help="test counter") { toIntOrNull() ?: println("null here") }
}

fun main(args: Array<String>) = mainBody {
    logger.info { "Program started" }
    try {
        val config = ConfigLoader().loadConfigOrThrow<Competition>("/config.json")
        logger.info { "Started $config competition" }
    } catch (exc: Exception) {
        logger.error { "Cannot decoder configs from configuration file:" }
        logger.error { exc.message }
    }
    val parsedArgs = ArgParser(args).parseInto(::MyArgs)
    println("Hello ${parsedArgs.name}!")

    val csvFile = File("sample-data/classes.csv")
    val csvData = csvReader().readAllWithHeader(csvFile)
//    val csvData = csvReader().readAll(csvFile)
    println(csvData.toString())
}
