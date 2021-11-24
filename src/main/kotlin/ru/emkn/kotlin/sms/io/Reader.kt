package ru.emkn.kotlin.sms.io

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import mu.KotlinLogging
import ru.emkn.kotlin.sms.objects.*
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

fun formGroupsList(teams: List<Team>): List<Group> {
    val reader = csvReader()
    val file = File(competitionPath + "input/classes.csv")
    val map = reader.open(file) {
        CSVReader(file, this).groupsToCourses()
    } ?: throw IllegalArgumentException("Cannot read ${file.name}, program was terminated")
    val courses = formCoursesList().associateBy { course -> course.name }
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

fun formCoursesList(): List<Course> {
    val reader = csvReader()
    val file = File(competitionPath + "input/courses.csv")
    return reader.open(file) {
        CSVReader(file, this).courses()
    } ?: throw IllegalArgumentException("Cannot read ${file.name}, program was terminated")
}

fun formEventsList(): List<Event> {
    val reader = csvReader()
    val file = File(competitionPath + "input/event.csv")
    return reader.open(file) {
        CSVReader(file, this).events()
    } ?: throw IllegalArgumentException("Cannot read ${file.name}, program was terminated")
}