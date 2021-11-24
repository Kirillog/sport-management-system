package ru.emkn.kotlin.sms

//https://github.com/xenomachina/kotlin-argparser
//https://github.com/doyaaaaaken/kotlin-csv
//https://github.com/Kotlin/kotlinx-datetime

import com.sksamuel.hoplite.ConfigLoader
import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.mainBody
import mu.KotlinLogging
import ru.emkn.kotlin.sms.objects.Competition

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
}
