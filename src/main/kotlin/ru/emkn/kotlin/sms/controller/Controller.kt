package ru.emkn.kotlin.sms.controller

import mu.KotlinLogging
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import ru.emkn.kotlin.sms.*
import ru.emkn.kotlin.sms.io.FileLoader
import ru.emkn.kotlin.sms.io.FileSaver
import ru.emkn.kotlin.sms.io.Loader
import ru.emkn.kotlin.sms.io.Saver
import ru.emkn.kotlin.sms.model.*
import java.io.File
import java.nio.file.Path
import kotlin.io.path.extension

private val logger = KotlinLogging.logger {}

enum class State {
    EMPTY,
    CREATED,
    TOSSED,
    FINISHED
}

object StateTable : IntIdTable("state") {
    val state = enumerationByName("state", MAX_TEXT_FIELD_SIZE, State::class)
}

object Controller {

    var state: State = State.EMPTY
        set(state) {
            field = state
            transaction {
                StateTable.deleteAll()
                StateTable.insert {
                    it[this.state] = state
                }
            }
        }

    private fun load(path: Path?, trueState: State, loadFunc: Loader.() -> Unit) {
        path ?: throw IllegalArgumentException("path is not chosen")
        if (state != trueState) throw IllegalStateException("For this load state must be $trueState")
        transaction {
            getLoader(path).loadFunc()
        }
    }

    fun loadEvent(path: Path?) = load(path, State.CREATED) { loadEvent() }

    fun loadCheckpoints(path: Path?) = load(path, State.CREATED) { loadCheckpoints() }

    fun loadRoutes(path: Path?) = load(path, State.CREATED) { loadRoutes() }

    fun loadGroups(path: Path?) = load(path, State.CREATED) { loadGroups() }

    fun loadTeams(path: Path?) = load(path, State.CREATED) { loadTeams() }

    fun loadTimestamps(path: Path?) = load(path, State.TOSSED) { loadTimestamps() }

    //TODO: сделать приватными undoToss и undoResult

    fun undo() {
        when (state) {
            State.TOSSED -> undoToss()
            State.FINISHED -> undoResult()
            else -> throw IllegalStateException("State must be TOSSED or FINISHED")
        }
    }

    fun undoToss() {
        require(state == State.TOSSED)
        transaction {
            TossTable.deleteAll()
            Competition.toss = Toss()
        }
        state = State.CREATED
        logger.info { "Toss was canceled "}
    }

    fun undoResult() {
        require(state == State.FINISHED)
        transaction {
            PersonalResultTable.deleteAll()
            TeamResultTable.deleteAll()
        }
        state = State.TOSSED
        logger.info { "Result was canceled "}
    }

    fun toss() {
        require(state == State.CREATED)
        transaction {
            Competition.toss()
        }
        state = State.TOSSED
        logger.info { "Competition tossed" }
    }

    fun result() {
        require(state == State.TOSSED)
        transaction {
            Competition.calculateResult()
        }
        state = State.FINISHED
        logger.info { "Competition finished" }
    }

    fun getLoader(path: Path): Loader {
        return when (path.extension) {
            "csv" -> FileLoader(path, FileType.CSV)
            "" -> FileLoader(path, FileType.CSV)
            else -> throw IllegalStateException("Unsupported file format for $path")
        }
    }

    private fun getSaver(path: Path): Saver {
        return when (path.extension) {
            "csv" -> FileSaver(path.toFile())
            else -> throw IllegalStateException("Unsupported file format for $path")
        }
    }

    //TODO: разобраться как нам сохранять через getSaver
    fun saveResultsToPath(results: Path) = transaction {
        getSaver(results).saveResults()
    }

    fun saveTossToPath(toss: Path) = transaction {
        getSaver(toss).saveToss()
    }

    fun saveTeamResultsToPath(results: Path) = transaction {
        getSaver(results).saveTeamResults()
    }

    fun getControllerState() = state

    fun createDB(file: File?) {
        if (file == null) throw IllegalArgumentException("File was not chosen")
        if (file.exists()) throw IllegalArgumentException("File already exists")
        file.createNewFile()
        logger.info { "Database created" }
        connectDB(file)
    }

    fun connectDB(file: File?) {
        require(state == State.EMPTY)
        if (file == null) throw IllegalArgumentException("File wasn't chosen")
        if (!file.isFile) throw IllegalStateException("It must be a file, not a directory")
        if (!file.canRead()) throw IllegalStateException("File does not readable")
        if (!file.canWrite()) throw IllegalStateException("File does not writable")
        var fileName = file.toPath().toAbsolutePath().toString()
        if (fileName.takeLast(6) == ".mv.db") {
            fileName = fileName.dropLast(6)
        } else {
            throw IllegalArgumentException("File should has extension .mv.db")
        }
        Database.connect("$DB_HEADER:$fileName", driver = DB_DRIVER)

        transaction {
            DB_TABLES.forEach {
                SchemaUtils.create(it)
            }
            val query = StateTable.selectAll()
            state = if (query.empty()) State.CREATED else query.first()[StateTable.state]
        }
        logger.info { "Database connected" }
    }
}