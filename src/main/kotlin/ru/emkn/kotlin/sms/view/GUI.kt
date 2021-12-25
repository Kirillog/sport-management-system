package ru.emkn.kotlin.sms.view

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
import ru.emkn.kotlin.sms.controller.CompetitionController
import ru.emkn.kotlin.sms.view.creators.CheckpointCreator
import ru.emkn.kotlin.sms.view.creators.ParticipantCreator
import ru.emkn.kotlin.sms.view.tables.CheckpointTable
import ru.emkn.kotlin.sms.view.tables.EventTable
import ru.emkn.kotlin.sms.view.tables.ParticipantsTable
import ru.emkn.kotlin.sms.view.tables.TimestampTable
import java.io.File

//TODO: сделать честную переотрисовку нашего окна.
class GUI {

    enum class State {
        LoadOrCreateDataBase,
        ShowParticipants,
        CreateParticipant,
        CreateCheckpoint,
        CreateRoutes,
        CreateTimestamp,
        Reload,
        CheckDataBaseState,
        EditAnnounceData,
        EditRegisterData
    }

    var state = mutableStateOf(State.LoadOrCreateDataBase)
    private val statesStack = mutableListOf(state.value)

    private val participantsTable by lazy { mutableStateOf(ParticipantsTable()) }
    val eventTable by lazy { mutableStateOf(EventTable()) }
    val checkpointTable by lazy { mutableStateOf(CheckpointTable()) }

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



/*    @Composable
    private fun loadOrCreateCompetitionData() {
        ButtonsChooser(
            "Load or create competition parameters?",
            listOf(
                ActionButton("Load") {
                    val eventFile = PathChooser("Choose event", ".csv", "Event").choose()
                    val checkpointsFile = PathChooser("Choose checkpoints", ".csv", "Checkpoints").choose()
                    val routesFile = PathChooser("Choose routes", ".csv", "Routes").choose()
                    try {
                        CompetitionController.loadEvent(eventFile?.toPath())
                        CompetitionController.loadCheckpoints(checkpointsFile?.toPath())
                        CompetitionController.loadRoutes(routesFile?.toPath())
                        pushState(State.EditAnnounceData)
                    } catch (e: Exception) {
                        TopAppBar.setMessage(e.message ?: "Undefined error")
                    }

                },
                ActionButton("Create") {
                    Creator.createEventIfEmpty("Название", LocalDate.of(2021, 12, 31))
                    pushState(State.EditAnnounceData)
                }
            )
        ).draw()
    }*/

    enum class EditCompetitionState {
        EventEditing,
        CheckpointsEditing,
        RoutesEditing
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


    fun pushDataBaseState() {
        pushState(
            when (CompetitionController.getControllerState()) {
                ru.emkn.kotlin.sms.controller.State.CREATED -> State.EditAnnounceData
                ru.emkn.kotlin.sms.controller.State.ANNOUNCED -> State.EditRegisterData
                else -> TODO()
            }
        )
    }


}

fun mainContent() {
    application {
        Window(onCloseRequest = ::exitApplication, title = "Sport Management System") {
            app()
        }
    }
}

@Composable
private fun app() {
    val gui = remember { GUI() }
    println(gui.state.value)
    while (gui.state.value == GUI.State.Reload)
        gui.popState()
    val participantsTable = remember { ParticipantsTable() }
    Column {
        TopAppBar.draw()
        when (gui.state.value) {
            GUI.State.LoadOrCreateDataBase -> loadOrCreateDataBase(gui)
//                State.LoadOrCreateCompetitionData -> loadOrCreateCompetitionData()
            GUI.State.EditAnnounceData -> CompetitionDataEditor().draw(gui)
            GUI.State.CheckDataBaseState -> gui.pushDataBaseState()
            GUI.State.ShowParticipants -> participantsTable.draw(gui)
            GUI.State.CreateParticipant -> ParticipantCreator().draw(gui)
            GUI.State.CreateCheckpoint -> CheckpointCreator().draw(gui)
            GUI.State.CreateTimestamp -> TODO()
            else -> throw IllegalReceiveException("Forbidden state of GUI")
        }
    }
}

@Composable
private fun editCompetitionData(gui: GUI) {
    val state = remember { mutableStateOf(GUI.EditCompetitionState.EventEditing) }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            ActionButton("events") {
                state.value = GUI.EditCompetitionState.EventEditing
            }.draw(gui)
            ActionButton("checkpoints") {
                state.value = GUI.EditCompetitionState.CheckpointsEditing
            }.draw(gui)
            ActionButton("Routes") {
                TODO()
            }.draw(gui)
        }
        when (state.value) {
            GUI.EditCompetitionState.EventEditing -> gui.eventTable.value.draw(gui)
            GUI.EditCompetitionState.CheckpointsEditing -> gui.checkpointTable.value.draw(gui)
            GUI.EditCompetitionState.RoutesEditing -> TODO()
        }
    }
}

@Composable
private fun loadOrCreateDataBase(gui: GUI) {
    LoadOrCreate(
        question = "Load database or create new one?",
        loadTitle = "select database file",
        createTitle = "choose path for new database",
        loadAction = {
            CompetitionController.connectDB(it)
            gui.pushDataBaseState()
        },
        createAction = {
            CompetitionController.createDB(it)
            gui.pushState(GUI.State.EditAnnounceData)
        },
        fileExtension = ".mv.db",
        fileExtensionDescription = "Database"
    ).draw(gui)
}