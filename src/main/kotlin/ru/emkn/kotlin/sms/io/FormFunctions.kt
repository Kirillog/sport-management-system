package ru.emkn.kotlin.sms.io

/*import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import mu.KotlinLogging
import ru.emkn.kotlin.sms.objects.*
import java.io.File
import java.nio.file.Path

private val logger = KotlinLogging.logger {}

/**
 * Returns list of [Team] located in [competitionPath]/applications/ directory.
 *
 * Filters incorrect applications.
 */

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

/**
 * Converts list of [Team] to list of [Group] using information about [Route] and [GroupToCourse] in [competitionPath].
 */

fun formGroupsList(teams: List<Team>, competitionPath: Path): List<Group> {
    val map = formMapGroupsToCourses(competitionPath)
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

/**
 * Returns map from name of [Group] to name of [Route] located in [competitionPath]/input/classes.csv.
 *
 * Throws [IllegalArgumentException] if file cannot be read.
 */

fun formMapGroupsToCourses(competitionPath: Path): Map<String, String> {
    val reader = csvReader()
    val file = competitionPath.resolve("input/classes.csv").toFile()
    val map = reader.open(file) {
        CSVReader(file, this).groupsToCourses()
    } ?: throw IllegalArgumentException("Cannot read ${file.name}, program was terminated")
    return map
}

/**
 * Returns list of [Route] located in [competitionPath]/input/courses.csv.
 *
 * Throws [IllegalArgumentException] if file cannot be read.
 */

fun formCoursesList(competitionPath: Path): List<Route> {
    val reader = csvReader()
    val file = competitionPath.resolve("input/courses.csv").toFile()
    return reader.open(file) {
        CSVReader(file, this).courses()
    } ?: throw IllegalArgumentException("Cannot read ${file.name}, program was terminated")
}

/**
 * Returns list of [Event] located in [competitionPath]/input/event.csv.
 *
 * Throws [IllegalArgumentException] if file cannot be read.
 */

fun formEvent(competitionPath: Path): Event {
    val reader = csvReader()
    val file = competitionPath.resolve("input/event.csv").toFile()

    val allEvents = reader.open(file) {
        CSVReader(file, this).events()
    } ?: throw IllegalArgumentException("Cannot read ${file.name}, program was terminated")

    when (allEvents.size) {
        0 -> throw IllegalStateException("${file.name} is empty, program was terminated")
        1 -> Unit
        else -> logger.warn { "In file ${file.name} more than one event. Using first" }
    }
    return allEvents[0]
}

/**
 * Returns list of [TimeStamp] located in [competitionPath]/checkpoints directory.
 */

fun formTimestamps(competitionPath: Path): List<TimeStamp> {
    val reader = csvReader()
    val dir = competitionPath.resolve("checkpoints/").toFile()
    return dir.walk().filter { it.isFile && it.extension == "csv" }.map { file ->
        logger.debug { "Processing ${file.name}" }
        reader.open(file) {
            CSVReader(file, this).timestamps()
        }
    }.toList().filterNotNull().flatten()
}

/**
 * Returns list of tossed [Group] located in [competitionPath]/protocols/toss.csv.
 *dir
 * Throws [IllegalArgumentException] if file cannot be read.
 */

fun formTossedGroups(competitionPath: Path): List<Group> {
    val reader = csvReader()
    val file = competitionPath.resolve("protocols/toss.csv").toFile()
    val courses = formCoursesList(competitionPath).associateBy { course -> course.name }
    val map = formMapGroupsToCourses(competitionPath)
    val participants = reader.open(file) {
        CSVReader(file, this).participants()
    } ?: throw IllegalArgumentException("Cannot read ${file.name}, program was terminated")
    return participants.groupBy(Participant::group)
        .map { group ->
            Group(
                group.key, courses[map[group.key]]
                    ?: throw IllegalArgumentException("${group.key} doesn't have appropriate course"),
                group.value
            )
        }
}*/