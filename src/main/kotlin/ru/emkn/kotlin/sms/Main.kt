package ru.emkn.kotlin.sms

import com.xenomachina.argparser.mainBody
import mu.KotlinLogging
import ru.emkn.kotlin.sms.view.GUI


private val logger = KotlinLogging.logger {}

fun main(args: Array<String>): Unit = mainBody {

    GUI.run()

}
