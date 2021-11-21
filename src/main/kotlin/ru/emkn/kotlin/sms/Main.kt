package ru.emkn.kotlin.sms

//https://github.com/xenomachina/kotlin-argparser
import com.xenomachina.argparser.*
//https://github.com/doyaaaaaken/kotlin-csv
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
//https://github.com/Kotlin/kotlinx-datetime

import com.sksamuel.hoplite.ConfigLoader
import mu.KotlinLogging
import ru.emkn.kotlin.sms.objects.Competition
import java.io.File

private val logger = KotlinLogging.logger {}

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

    val member = Member()

    val writer = Writer(File("test.csv"), Filetype.CSV)
    writer.add("mem")
    writer.add(member)
    writer.write()
    writer.add("mem")
    writer.write()

    val writer2 = Writer(File("test.json"), Filetype.JSON)
    writer2.add(member)
    writer2.add("mem")
    writer2.write()
}
