package ru.emkn.kotlin.sms.io

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import mu.KotlinLogging
import ru.emkn.kotlin.sms.objects.Course
import ru.emkn.kotlin.sms.objects.Event
import ru.emkn.kotlin.sms.objects.Team
import java.io.File

abstract class Reader(protected val file: File) {

    abstract fun team(): Team?

    abstract fun groupsToCourses(): Map<String, String>?

    abstract fun courses(): List<Course>?

    abstract fun events(): List<Event>?
}

interface Readable

private val logger = KotlinLogging.logger {}

private const val competitionPath = "competitions/competition-1/"

fun formTeamsList(): List<Team> {
    val reader = csvReader()
    val dir = File(competitionPath + "applications/")
    return dir.walk().filter(File::isFile).map { file ->
        logger.debug { "Processing ${file.name}" }
        reader.open(file) {
            CSVReader(file, this).team()
        }
    }.toList().filterNotNull()
}
