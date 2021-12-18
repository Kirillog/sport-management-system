package ru.emkn.kotlin.sms.io

import mu.KotlinLogging
import ru.emkn.kotlin.sms.model.*
import java.io.File
import java.nio.file.Path

private val logger = KotlinLogging.logger {}

class FileLoader(path: Path) : Loader {
    private val file = path.toFile()

    fun error(): Nothing =
        throw IllegalArgumentException("Cannot read file ${file.name}")

    override fun loadEvent(): Event =
        CSVReader(file).event() ?: error()

    override fun loadGroups(): Set<Group> =
        CSVReader(file).groups() ?: error()

    override fun loadRoutes(): Set<Route> =
        CSVReader(file).courses() ?: error()

    override fun loadTeams(): Set<Team> =
        file.walk().filter(File::isFile).map { file ->
            logger.debug { "Processing ${file.name}" }
            CSVReader(file).team()
        }.filterNotNull().toSet()

    override fun loadTimestamps(): Set<Timestamp> =
        file.walk().filter { it.isFile && it.extension == "csv" }.map { file ->
            logger.debug { "Processing ${file.name}" }
            CSVReader(file).timestamps()
        }.filterNotNull().flatten().toSet()

    override fun loadCheckpoints(): Set<Checkpoint> =
        CSVReader(file).checkPoints() ?: error()

    override fun loadToss() =
        CSVReader(file).toss() ?: error()
}

