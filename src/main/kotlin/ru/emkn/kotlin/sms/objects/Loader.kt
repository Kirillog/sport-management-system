package ru.emkn.kotlin.sms.objects

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import mu.KotlinLogging
import ru.emkn.kotlin.sms.io.CSVReader
import java.io.File
import java.nio.file.Path

private val logger = KotlinLogging.logger {}

interface Loader {
    fun loadGroups(): Set<Group>
    fun loadTeams(): Set<Team>
    fun loadRoutes(): Set<Route>
    fun loadTimestamps(): Set<TimeStamp>
}

class FileLoader(path: Path) : Loader {
    private val reader = csvReader()
    private val file = path.toFile()

    override fun loadGroups(): Set<Group> =
        reader.open(file) {
            CSVReader(file, this).groups()
        } ?: throw IllegalArgumentException("Cannot read file ${file.name}")

    override fun loadRoutes(): Set<Route> =
        reader.open(file) {
            CSVReader(file, this).courses()
        } ?: throw IllegalArgumentException("Cannot read file ${file.name}")


    override fun loadTeams(): Set<Team> =
        file.walk().filter(File::isFile).map { file ->
            logger.debug { "Processing ${file.name}" }
            reader.open(file) {
                CSVReader(file, this).team()
            }
        }.filterNotNull().toSet()

    override fun loadTimestamps(): Set<TimeStamp> =
        file.walk().filter { it.isFile && it.extension == "csv" }.map { file ->
            logger.debug { "Processing ${file.name}" }
            reader.open(file) {
                CSVReader(file, this).timestamps()
            }
        }.filterNotNull().flatten().toSet()
}

