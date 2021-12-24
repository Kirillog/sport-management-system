package ru.emkn.kotlin.sms.view

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.sun.nio.sctp.IllegalReceiveException
import ru.emkn.kotlin.sms.controller.CompetitionController
import ru.emkn.kotlin.sms.view.creators.CheckpointCreator
import ru.emkn.kotlin.sms.view.creators.ParticipantCreator
import ru.emkn.kotlin.sms.view.creators.draw
import ru.emkn.kotlin.sms.view.tables.ParticipantsTable
import ru.emkn.kotlin.sms.view.tables.draw
import java.io.File

//TODO: сделать честную переотрисовку нашего окна.
class GUI {

    enum class State {
        LoadOrCreateDataBase,
        ShowParticipants,
        CreateParticipant,
        CreateCheckpoint,
        CreateTimestamp,
        CheckDataBaseState,
        EditAnnounceData,
        EditRegisterData
    }

    var state = mutableStateOf(State.LoadOrCreateDataBase)
    private val statesStack = mutableListOf(state.value)

    //val timestampsTable by lazy { mutableStateOf(TimestampTable()) }
    val participantsTable by lazy { mutableStateOf(ParticipantsTable()) }
    // TODO routes table

    fun pushState(newState: State) {
        state.value = newState
        statesStack.add(newState)
    }

    fun popState() {
        statesStack.removeLast()
        state.value = statesStack.lastOrNull() ?: throw IllegalStateException("GUI stack error")
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

@Composable
private fun app() {
    val gui = remember { GUI() }
    println(gui.state.value)
    Column {
        TopAppBar.draw()
        when (gui.state.value) {
            GUI.State.LoadOrCreateDataBase -> loadOrCreateDataBase(gui)
            GUI.State.EditAnnounceData -> drawCompetitionDataEditor(gui)
            GUI.State.CheckDataBaseState -> gui.pushDataBaseState()
            GUI.State.ShowParticipants -> draw(gui, gui.participantsTable.value)
            GUI.State.CreateParticipant -> draw(gui, ParticipantCreator())
            GUI.State.CreateCheckpoint -> draw(gui, CheckpointCreator())
            GUI.State.CreateTimestamp -> TODO()
            else -> throw IllegalReceiveException("Forbidden state of GUI")
        }
    }
}

@Composable
private fun loadOrCreateDataBase(gui: GUI) {
    draw(
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
        )
    )
}