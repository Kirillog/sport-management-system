package ru.emkn.kotlin.sms.view

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.sun.nio.sctp.IllegalReceiveException
import ru.emkn.kotlin.sms.controller.CompetitionController
import ru.emkn.kotlin.sms.controller.State
import ru.emkn.kotlin.sms.view.MenuState.*
import ru.emkn.kotlin.sms.view.creators.*
import ru.emkn.kotlin.sms.view.tables.*
import java.io.File


class GUI {

    val participantsTable = ParticipantsTable()
    val eventTable = EventTable()
    val routeTable = RouteTable()
    val teamTable = TeamTable()
    val groupTable = GroupTable()
    val checkpointTable = CheckpointTable()
    val timestampTable = TimestampTable()

    val tables = listOf(participantsTable, eventTable, routeTable, teamTable, groupTable, checkpointTable)

    enum class State(val menuState: MenuState) {

        // create states
        CreateParticipant(Hided),
        CreateTeam(Hided),
        CreateCheckpoint(Hided),
        CreateRoute(Hided),
        CreateTimestamp(Hided),
        CreateEvent(Hided),
        CreateGroup(Hided),

        // other states
        InitialWindow(Blocked),
        EditAnnounceData(Preparing),
        EditRuntimeDump(Tossed),
        ShowResults(Result)
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
                ru.emkn.kotlin.sms.controller.State.EMPTY -> State.InitialWindow
                ru.emkn.kotlin.sms.controller.State.CREATED -> State.EditAnnounceData
                ru.emkn.kotlin.sms.controller.State.TOSSED -> State.EditRuntimeDump
                ru.emkn.kotlin.sms.controller.State.FINISHED -> State.ShowResults
            }
        )
    }
}

fun mainContent() {
    application {
        Window(onCloseRequest = ::exitApplication, title = "Sport Management System") {
            val gui = remember { GUI() }
            val bottomBar = remember { BottomAppBar() }
            drawMenuBar(gui, this, bottomBar)
            when (gui.state.value) {
                GUI.State.InitialWindow -> drawInvitationMessage(bottomBar)
                GUI.State.EditAnnounceData -> {
                    StateSwitcher.setUnTossed(gui)
                    drawTables(gui, bottomBar)
                }
                GUI.State.ShowResults -> {
                    StateSwitcher.setResulted(gui)
                    drawTables(gui, bottomBar)
                }
                GUI.State.CreateParticipant -> draw(gui, bottomBar, ParticipantCreator())
                GUI.State.CreateCheckpoint -> draw(gui, bottomBar, CheckpointCreator())
                GUI.State.CreateRoute -> draw(gui, bottomBar, RoutesCreator())
                GUI.State.CreateEvent -> draw(gui, bottomBar, EventCreator())
                GUI.State.CreateTimestamp -> draw(gui, bottomBar, TimestampCreator())
                GUI.State.CreateTeam -> draw(gui, bottomBar, TeamCreator())
                GUI.State.EditRuntimeDump -> {
                    StateSwitcher.setTossed(gui)
                    StateSwitcher.setUnResulted(gui)
                    drawTables(gui, bottomBar)
                }
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
