package ru.emkn.kotlin.sms

import com.sksamuel.hoplite.ConfigLoader
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

fun main(args: Array<String>) {
    logger.info { "Program started" }
    try {
        val config = ConfigLoader().loadConfigOrThrow<Competition>("/config.json")
        logger.info { "Started $config competition" }
    } catch (exc: Exception) {
        logger.error { "Cannot decoder configs from configuration file:" }
        logger.error { exc.message }
    }
}
