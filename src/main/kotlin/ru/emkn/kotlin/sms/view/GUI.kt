package ru.emkn.kotlin.sms.view

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.sun.nio.sctp.IllegalReceiveException
import ru.emkn.kotlin.sms.ObjectFields
import ru.emkn.kotlin.sms.controller.CompetitionController
import ru.emkn.kotlin.sms.controller.Creator
import ru.emkn.kotlin.sms.model.Event
import ru.emkn.kotlin.sms.view.creators.CheckpointCreator
import ru.emkn.kotlin.sms.view.creators.ParticipantCreator
import ru.emkn.kotlin.sms.view.tables.CheckpointTable
import ru.emkn.kotlin.sms.view.tables.EventTable
import ru.emkn.kotlin.sms.view.tables.ParticipantsTable
import ru.emkn.kotlin.sms.view.tables.TimestampTable
import java.io.File

//TODO: сделать честную переотрисовку нашего окна.
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
        CreateCheckpoint,
        CreateTimestamp,
        Reload,
        CheckDataBaseState,
        LoadOrCreateCompetitionData,
        EditCompetitionData
    }

    private var state = mutableStateOf(State.LoadOrCreateDataBase)
    private val statesStack = mutableListOf(state.value)

    private val participantsTable by lazy { mutableStateOf(ParticipantsTable()) }
    private val eventTable by lazy { mutableStateOf(EventTable()) }
    private val checkpointTable by lazy { mutableStateOf(CheckpointTable()) }

    // TODO routes table
    private val timestampsTable by lazy { mutableStateOf(TimestampTable()) }

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
        LoadOrCreate(
            question = "Load database or create new one?",
            loadTitle = "select database file",
            createTitle = "choose path for new database",
            loadAction = {
                CompetitionController.connectDB(it)
                pushState(State.CheckDataBaseState)
            },
            createAction = {
                CompetitionController.createDB(it)
                pushState(State.LoadOrCreateCompetitionData)
            },
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
        ).draw()
    }

    enum class EditCompetitionState {
        EventEditing,
        CheckpointsEditing,
        RoutesEditing
    }

    @Composable
    private fun editCompetitionData() {

        val state = remember { mutableStateOf(EditCompetitionState.EventEditing) }

        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                ActionButton("events") {
                    state.value = EditCompetitionState.EventEditing
                }.draw()
                ActionButton("checkpoints") {
                    state.value = EditCompetitionState.CheckpointsEditing
                }.draw()
                ActionButton("Routes") {
                    TODO()
                }.draw()
            }
            when (state.value) {
                EditCompetitionState.EventEditing -> eventTable.value.draw()
                EditCompetitionState.CheckpointsEditing -> checkpointTable.value.draw()
                EditCompetitionState.RoutesEditing -> TODO()
            }
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
                ru.emkn.kotlin.sms.controller.State.ANNOUNCED -> State.EditCompetitionData
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
                State.CreateCheckpoint -> CheckpointCreator().draw()
                State.CreateTimestamp -> TODO()
                else -> throw IllegalReceiveException("Forbidden state of GUI")
            }
        }
    }
}
