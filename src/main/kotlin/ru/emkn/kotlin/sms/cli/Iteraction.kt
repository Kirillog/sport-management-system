package ru.emkn.kotlin.sms.cli

import mu.KotlinLogging
import ru.emkn.kotlin.sms.controller.Controller
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.listDirectoryEntries

private val logger = KotlinLogging.logger {}

val cliHelp = """
    
""".trimIndent()

const val skipMsg = "It seems like nothing happens..."

var workingDirectory = Paths.get("").toAbsolutePath()

fun loadObjFromPath(obj: String, file: Path) {
    when (obj) {
        "event" -> Controller.loadEvent(file)
        "checkpoints" -> Controller.loadCheckpoints(file)
        "routes" -> Controller.loadRoutes(file)
        "groups" -> Controller.loadGroups(file)
        "teams" -> Controller.loadTeams(file)
        "timestamps" -> Controller.loadTimestamps(file)
        else -> println(skipMsg)
    }
}

fun loadCompetitionMetadata(competitionDir: Path) {
    loadObjFromPath("event", competitionDir.resolve("input/event.csv"))
    loadObjFromPath("checkpoints", competitionDir.resolve("input/checkpoints.csv"))
    loadObjFromPath("routes", competitionDir.resolve("input/routes.csv"))
    loadObjFromPath("groups", competitionDir.resolve("input/groups.csv"))
    loadObjFromPath("teams", competitionDir.resolve("teams"))
}

fun loadDB(file: Path) {
    val dbFile = file.toFile()

    if (dbFile.exists()) {
        Controller.connectDB(dbFile)
    } else {
        Controller.createDB(dbFile)
        println("Created new DB")
    }
}

fun changeDirectory(path: Path) {
    workingDirectory = workingDirectory.resolve(path).normalize()
}

fun viewDirectory() {
    println(workingDirectory.listDirectoryEntries().joinToString("\n") { it.fileName.toString() })
}

fun main(args: Array<String>) {
    logger.info { "Program started in CLI mode" }

    val dbFilename = if (args.isNotEmpty()) {
        args[0]
    } else {
        println("Enter database path to work with")
        readLine() ?: ""
    }.trim()

    loadDB(workingDirectory.resolve(dbFilename))

    println("Use 'h' or 'help' to display list of commands")

    while (true) {
        print("$workingDirectory: ")
        val userInput = readLine()?.split(' ') ?: List(0) { "" }
        try {
            when (userInput.getOrNull(0)) {
                null, "exit" -> break
                "help" -> println(cliHelp)
                "load" -> loadObjFromPath(userInput[1], workingDirectory.resolve(userInput[2]))
                "loadAll" -> loadCompetitionMetadata(workingDirectory.resolve(userInput[1]))
                "toss" -> Controller.toss()
                "result" -> Controller.result()
                "undo" -> Controller.undo()
                "add" -> TODO()
                "delete" -> TODO()
                "edit" -> TODO()
                "cd" -> changeDirectory(Path.of(userInput[1]))
                "ls" -> viewDirectory()
                else -> println(skipMsg)
            }
        } catch (err: Exception) {
            println(err)
        }
    }
}
