package ru.emkn.kotlin.sms.io

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import mu.KotlinLogging
import ru.emkn.kotlin.sms.objects.*
import java.io.File
import java.nio.file.Path

abstract class Reader(protected val file: File) {

    abstract fun team(): Team?

    abstract fun groupsToCourses(): Map<String, String>?

    abstract fun courses(): List<Course>?

    abstract fun events(): List<Event>?

    abstract fun timestamps(): List<TimeStamp>?
}

interface Readable

private val logger = KotlinLogging.logger {}

fun formTeamsList(competitionPath: Path): List<Team> {
    val reader = csvReader()
    val dir = competitionPath.resolve("applications/").toFile()
    return dir.walk().filter(File::isFile).map { file ->
        logger.debug { "Processing ${file.name}" }
        reader.open(file) {
            CSVReader(file, this).team()
        }
    }.toList().filterNotNull()
}

fun formGroupsList(teams: List<Team>, competitionPath: Path): List<Group> {
    val reader = csvReader()
    val file = competitionPath.resolve("input/classes.csv").toFile()
    val map = reader.open(file) {
        CSVReader(file, this).groupsToCourses()
    } ?: throw IllegalArgumentException("Cannot read ${file.name}, program was terminated")
    val courses = formCoursesList(competitionPath).associateBy { course -> course.name }
    return teams.flatMap(Team::members)
        .groupBy(Participant::group)
        .map { group ->
            Group(
                group.key, courses[map[group.key]]
                    ?: throw IllegalArgumentException("${group.key} doesn't have appropriate course"),
                group.value
            )
        }
}

fun formCoursesList(competitionPath: Path): List<Course> {
    val reader = csvReader()
    val file = competitionPath.resolve("input/courses.csv").toFile()
    return reader.open(file) {
        CSVReader(file, this).courses()
    } ?: throw IllegalArgumentException("Cannot read ${file.name}, program was terminated")
}

fun formEvent(competitionPath: Path): Event {
    val reader = csvReader()
    val file = competitionPath.resolve("input/event.csv").toFile()

    val allEvents = reader.open(file) {
        CSVReader(file, this).events()
    } ?: throw IllegalArgumentException("Cannot read ${file.name}, program was terminated")

    when (allEvents.size) {
        0 -> throw IllegalStateException("${file.name} is empty, program was terminated")
        else -> logger.warn { "In file ${file.name} more than one event. Using first" }
    }
    return allEvents[0]
}