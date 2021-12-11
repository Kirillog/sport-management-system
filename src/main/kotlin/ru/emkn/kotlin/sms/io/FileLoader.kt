package ru.emkn.kotlin.sms.io

import mu.KotlinLogging
import ru.emkn.kotlin.sms.model.*
import java.io.File
import java.nio.file.Path
import java.time.LocalTime

private val logger = KotlinLogging.logger {}

class FileLoader(path: Path) : Loader {
    private val file = path.toFile()
    private val reader = CSVReader(file)
    override fun loadEvent(): Event =
        reader.event() ?: throw IllegalArgumentException("Cannot read file ${file.name}")

    override fun loadGroups(): Set<Group> =
        reader.groups() ?: throw IllegalArgumentException("Cannot read file ${file.name}")

    override fun loadRoutes(): Set<Route> =
        reader.courses() ?: throw IllegalArgumentException("Cannot read file ${file.name}")

    override fun loadTeams(): Set<Team> =
        file.walk().filter(File::isFile).map { file ->
            logger.debug { "Processing ${file.name}" }
            CSVReader(file).team()
        }.filterNotNull().toSet()

    override fun loadTimestamps(): Set<TimeStamp> =
        file.walk().filter { it.isFile && it.extension == "csv" }.map { file ->
            logger.debug { "Processing ${file.name}" }
            CSVReader(file).timestamps()
        }.filterNotNull().flatten().toSet()

    override fun loadToss(): Map<Participant, LocalTime> =
        reader.toss() ?: throw IllegalArgumentException("Cannot read ${file.name}")
}

