package ru.emkn.kotlin.sms.view

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.sun.nio.sctp.IllegalReceiveException
import ru.emkn.kotlin.sms.ObjectFields
import ru.emkn.kotlin.sms.controller.CompetitionController
import ru.emkn.kotlin.sms.controller.Creator
import ru.emkn.kotlin.sms.model.Event
import ru.emkn.kotlin.sms.view.creators.ParticipantCreator
import ru.emkn.kotlin.sms.view.tables.ParticipantsTable
import java.io.File

object GUI {
    fun run() = application {
        Window(onCloseRequest = ::exitApplication, title = "Sport Management System") {
            app()
        }
    }

    enum class State {
        LoadOrCreateDataBase,
        ShowParticipants,
        CreateParticipant,
        Reload,
        CheckDataBaseState,
        LoadOrCreateCompetitionData,
        EditCompetitionData
    }

    private var state = mutableStateOf(State.LoadOrCreateDataBase)
    private val statesStack = mutableListOf(state.value)

    private val participantsTable = mutableStateOf(ParticipantsTable())

    fun pushState(newState: State) {
        state.value = newState
        statesStack.add(newState)
    }

    fun reload() {
        pushState(State.Reload)
    }

    fun popState() {
        statesStack.removeLast()
        state.value = statesStack.lastOrNull() ?: throw IllegalStateException("GUI stack error")
    }

    @Composable
    private fun loadOrCreateDataBase() {

        val action: (File?) -> Unit = {
            CompetitionController.connectDB(it)
            pushState(State.CheckDataBaseState)
        }

        LoadOrCreate(
            question = "Load database or create new one?",
            loadTitle = "select database file",
            createTitle = "choose path for new database",
            loadAction = action,
            createAction = action,
            fileExtension = ".mv.db",
            fileExtensionDescription = "Database"
        ).draw()
    }

    @Composable
    private fun loadOrCreateCompetitionData() {
        ButtonsChooser(
            "Load or create competition parameters?",
            listOf(
                ActionButton("Load") {
                    val eventFile = PathChooser("Choose event", ".csv", "Event").choose()
                    val checkpointsFile = PathChooser("Choose checkpoints", ".csv", "Checkpoints").choose()
                    val routesFile = PathChooser("Choose routes", ".csv", "Routes").choose()
                    try {
                        CompetitionController.announceFromPath(
                            eventFile?.toPath(),
                            checkpointsFile?.toPath(),
                            routesFile?.toPath()
                        )
                        pushState(State.EditCompetitionData)
                    } catch (e: Exception) {
                        TopAppBar.setMessage(e.message ?: "Undefined error")
                    }

                },
                ActionButton("Create") {
                    Creator.createEvent()
                    pushState(State.EditCompetitionData)
                }
            )
        )
    }

    @Composable
    private fun editCompetitionData() {
        Column {
            TODO()
        }
    }

    fun chooseFileAndProcess(
        chooserTitle: String,
        chooserFileExtension: String,
        chooserFileDescription: String,
        action: (File?) -> Unit
    ) {
        val file = PathChooser(chooserTitle, chooserFileExtension, chooserFileDescription).choose()
        try {
            action(file)
        } catch (e: Exception) {
            TopAppBar.setMessage(e.message ?: "Undefined error")
        }
    }



    private fun checkDataBaseState() {
        pushState(
            when (CompetitionController.getControllerState()) {
                ru.emkn.kotlin.sms.controller.State.CREATED -> State.LoadOrCreateCompetitionData
                else -> TODO()
            }
        )
    }

    @Preview
    @Composable
    private fun app() {
        println(state.value)
        while (state.value == State.Reload)
            popState()
        val participantsTable = remember { ParticipantsTable() }
        Column {
            TopAppBar.draw()
            when (state.value) {
                State.LoadOrCreateDataBase -> loadOrCreateDataBase()
                State.LoadOrCreateCompetitionData -> loadOrCreateCompetitionData()
                State.EditCompetitionData -> editCompetitionData()
                State.CheckDataBaseState -> checkDataBaseState()
                State.ShowParticipants -> participantsTable.draw()
                State.CreateParticipant -> ParticipantCreator().draw()
                else -> throw IllegalReceiveException("Forbidden state of GUI")
            }
        }
    }
}
