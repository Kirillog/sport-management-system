package ru.emkn.kotlin.sms.io

import mu.KotlinLogging
import ru.emkn.kotlin.sms.FileType
import ru.emkn.kotlin.sms.ObjectFields
import ru.emkn.kotlin.sms.controller.Creator
import ru.emkn.kotlin.sms.model.*
import java.io.File
import java.nio.file.Path

private val logger = KotlinLogging.logger {}

class FileLoader(path: Path, val fileType: FileType) : Loader {
    private val file = path.toFile()

    private fun getReader(file: File): Reader =
        when (fileType) {
            FileType.CSV -> CSVReader(file)
            FileType.JSON -> TODO()
        }

    private fun error(): Nothing =
        throw IllegalArgumentException("Cannot read file ${file.name}")

    override fun loadEvent(): Event {
        val table = getReader(file).event() ?: error()
        val events = table.mapNotNull {
            try {
                Creator.createEventFrom(it)
            } catch (err: IllegalArgumentException) {
                null
            }
        }
        if (events.isEmpty())
            logger.warn { "List of events is empty" }
        else if (events.size > 1)
            logger.warn { "There is some events, chose first from them" }
        return events.first()
    }

    override fun loadGroups(): Set<Group> {
        val groups = getReader(file).groups() ?: error()
        return groups.mapNotNull {
            try {
                Creator.createGroupFrom(it)
            } catch (err: IllegalArgumentException) {
                null
            }
        }.toSet()
    }

    override fun loadRoutes(): Set<Route> {
        val routes = getReader(file).courses() ?: error()
        return routes.mapNotNull {
            try {
                Creator.createRouteFrom(it)
            } catch (err: IllegalArgumentException) {
                null
            }
        }.toSet()

    }

    override fun loadTeams(): Set<Team> {
        return file.walk().filter(File::isFile).map { file ->
            logger.debug { "Processing ${file.name}" }
            val lines = getReader(file).team()
            if (lines == null) {
                logger.info { "${file.name} was skipped" }
                null
            } else {
                val teamName =
                    lines.first()[ObjectFields.Team] ?: throw IllegalStateException("Participant doesn't have team")
                val team = Creator.createTeamFrom(mapOf(ObjectFields.Name to teamName))
                lines.mapIndexedNotNull { index, it ->
                    try {
                        Creator.createParticipantFrom(it)
                    } catch (err: IllegalArgumentException) {
                        logger.info { "Participant with number ${index + 1} was skipped" }
                        null
                    }
                }
                team
            }

        }.filterNotNull().toSet()
    }

    override fun loadTimestamps(): Set<Timestamp> {
        return file.walk().filter { it.isFile && it.extension == "csv" }.map { file ->
            logger.debug { "Processing ${file.name}" }
            val lines = getReader(file).timestamps()
            if (lines == null) {
                logger.info { "${file.name} was skipped" }
                null
            } else {
                val timeStamps = lines.mapIndexedNotNull { index, it ->
                    try {
                        Creator.createTimeStampFrom(it)
                    } catch (err: IllegalArgumentException) {
                        logger.info { "Timestamp with number ${index + 1} was ignored" }
                        null
                    }
                }.toSet()
                if (timeStamps.isEmpty())
                    logger.warn { "List of timestamps is empty" }
                timeStamps
            }
        }.filterNotNull().flatten().toSet()
    }

    override fun loadCheckpoints(): Set<Checkpoint> {
        val checkpoints = getReader(file).checkPoints() ?: error()
        return checkpoints.mapNotNull {
            try {
                Creator.createCheckPointFrom(it)
            } catch (err: IllegalArgumentException) {
                null
            }
        }.toSet()
    }

    override fun loadToss() {
        val toss = getReader(file).toss() ?: error()
        val teamNames = toss.map {
            it[ObjectFields.Team] ?: throw IllegalStateException("Participant doesn't have team")
        }.toSet()
        teamNames.forEach {
            try {
                Creator.createTeamFrom(mapOf(ObjectFields.Name to it))
            } catch (err: IllegalArgumentException) {
                logger.warn { "Team $it was ignored" }
            }
        }
        val participants = (toss.sortedBy { Creator.convert<Int>(it[ObjectFields.ID]) }).mapNotNull {
            try {
                Creator.createParticipantFrom(it)
            } catch (err: IllegalArgumentException) {
                logger.warn { "Participant was ignored" }
                null
            }
        }
        if (participants.isEmpty())
            logger.warn { "List of participants is empty" }
    }
}

