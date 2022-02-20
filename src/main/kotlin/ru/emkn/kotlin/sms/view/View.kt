package ru.emkn.kotlin.sms.view

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.sun.nio.sctp.IllegalReceiveException
import ru.emkn.kotlin.sms.controller.Controller
import ru.emkn.kotlin.sms.view.MenuState.*
import ru.emkn.kotlin.sms.view.creators.*
import ru.emkn.kotlin.sms.view.tables.*


class View {

    // tables representing displayed data
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

    private fun pushStates(statesList: List<State>) {
        require(statesList.isNotEmpty()) { "Empty states list" }
        statesStack.addAll(statesList)
        state.value = statesList.last()
    }

    fun popState() {
        statesStack.removeLast()
        state.value = statesStack.lastOrNull() ?: throw IllegalStateException("GUI stack error")
    }

    fun pushDataBaseState() {
        when (Controller.getControllerState()) {
            ru.emkn.kotlin.sms.controller.State.EMPTY -> State.InitialWindow
            ru.emkn.kotlin.sms.controller.State.CREATED -> pushState(State.EditAnnounceData)
            ru.emkn.kotlin.sms.controller.State.TOSSED -> pushStates(
                listOf(
                    State.EditAnnounceData,
                    State.EditRuntimeDump
                )
            )
            ru.emkn.kotlin.sms.controller.State.FINISHED -> pushStates(
                listOf(
                    State.EditAnnounceData,
                    State.EditRuntimeDump,
                    State.ShowResults
                )
            )
        }
    }
}

fun mainContent() {
    application {
        Window(onCloseRequest = ::exitApplication, title = "Sport Management System") {
            val view = remember { View() }
            drawMenuBar(view, this)
            when (view.state.value) {
                View.State.InitialWindow -> {
                    BottomAppBar += "You should to load or create database to see something"
                }
                View.State.EditAnnounceData -> {
                    StateSwitcher.setUnTossed(view)
                    drawTables(view)
                    BottomAppBar += "You should load events -> checkpoints -> routes -> groups -> teams, then Navigate -> Toss"
                }
                View.State.ShowResults -> {
                    StateSwitcher.setResulted(view)
                    drawTables(view)
                    BottomAppBar += "All done!"
                }
                View.State.CreateParticipant -> draw(view, ParticipantCreator())
                View.State.CreateCheckpoint -> draw(view, CheckpointCreator())
                View.State.CreateRoute -> draw(view, RoutesCreator())
                View.State.CreateEvent -> draw(view, EventCreator())
                View.State.CreateTimestamp -> draw(view, TimestampCreator())
                View.State.CreateTeam -> draw(view, TeamCreator())
                View.State.EditRuntimeDump -> {
                    StateSwitcher.setTossed(view)
                    StateSwitcher.setUnResulted(view)
                    drawTables(view)
                    BottomAppBar += "You should load timestamps, then Navigate -> Result"
                }
                else -> throw IllegalReceiveException("Forbidden state of GUI")
            }
            drawBottomAppBar()
        }
    }
}
