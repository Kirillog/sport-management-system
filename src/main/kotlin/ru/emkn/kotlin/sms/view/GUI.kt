package ru.emkn.kotlin.sms.view

import androidx.compose.runtime.*
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.sun.nio.sctp.IllegalReceiveException
import ru.emkn.kotlin.sms.controller.CompetitionController
import ru.emkn.kotlin.sms.view.creators.*
import ru.emkn.kotlin.sms.view.tables.ParticipantsTable
import java.io.File


class GUI {

    val participantsTable = ParticipantsTable()

    enum class State {
        InitialWindow,
        // create states
        CreateParticipant,
        CreateTeam,
        CreateCheckpoint,
        CreateRoute,
        CreateTimestamp,
        CreateEvent,
        CreateGroup,
        // other states
        CheckDataBaseState,
        EditAnnounceData,
        EditRuntimeDump
    }

    var state = mutableStateOf(State.InitialWindow)
    private val statesStack = mutableListOf(state.value)


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
                else -> TODO()
            }
        )
    }
}

fun mainContent() {
    application {
        Window(onCloseRequest = ::exitApplication, title = "Sport Management System") {
            val gui = remember { GUI() }
            val bottomBar = remember { BottomAppBar() }
            println(gui.state.value)
            drawMenuBar(gui, this, bottomBar)
            when (gui.state.value) {
                GUI.State.InitialWindow -> drawInvitationMessage(bottomBar)
                GUI.State.EditAnnounceData -> drawTable(gui, bottomBar)
                GUI.State.CheckDataBaseState -> gui.pushDataBaseState()
                GUI.State.CreateParticipant -> draw(gui, bottomBar, ParticipantCreator())
                GUI.State.CreateCheckpoint -> draw(gui, bottomBar, CheckpointCreator())
                GUI.State.CreateRoute -> draw(gui, bottomBar, RoutesCreator())
                GUI.State.CreateEvent -> draw(gui, bottomBar, EventCreator())
                GUI.State.CreateTimestamp -> draw(gui, bottomBar, TimestampCreator())
                GUI.State.EditRuntimeDump -> draw(gui, bottomBar, gui.participantsTable)
                else -> throw IllegalReceiveException("Forbidden state of GUI")
            }
            draw(bottomBar)
        }
    }
}

fun chooseFileAndProcess(
    bottomAppBar: BottomAppBar,
    chooserTitle: String,
    chooserFileExtension: String,
    chooserFileDescription: String,
    action: (File?) -> Unit
) {
    val file = PathChooser(chooserTitle, chooserFileExtension, chooserFileDescription).choose()
    try {
        action(file)
    } catch (e: Exception) {
        bottomAppBar.setMessage(e.message ?: "Undefined error")
    }
}

private fun drawInvitationMessage(bottomAppBar: BottomAppBar) {
    bottomAppBar.setMessage("You should to load or create database to see something")
}
