package ru.emkn.kotlin.sms

import com.xenomachina.argparser.mainBody
import mu.KotlinLogging
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import ru.emkn.kotlin.sms.controller.CompetitionController
import ru.emkn.kotlin.sms.model.*
import ru.emkn.kotlin.sms.view.GUI
import java.io.File
import kotlin.io.path.Path
import ru.emkn.kotlin.sms.controller.Creator


private val logger = KotlinLogging.logger {}

fun main(args: Array<String>): Unit = mainBody {

//    GUI.run()

}
