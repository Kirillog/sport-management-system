package ru.emkn.kotlin.sms.view

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.sun.nio.sctp.IllegalReceiveException
import ru.emkn.kotlin.sms.ObjectFields
import ru.emkn.kotlin.sms.controller.Editor
import ru.emkn.kotlin.sms.model.*
import ru.emkn.kotlin.sms.view.creators.ParticipantCreator
import ru.emkn.kotlin.sms.view.tables.ParticipantsTable

private val routes = listOf(
    Route("М18", listOf(CheckPoint(1), CheckPoint(2))),
    Route("М19", listOf(CheckPoint(2), CheckPoint(1)))
)

private val groups = listOf(
    Group("М18", "М18"),
    Group("М19", "М19")
)

private val teams = listOf(
    Team("divAn"),
    Team("divAn-Bad"),
    Team("Sekira")
)

fun prepare() {
    for (i in 1..10) {
        Editor.createParticipantFrom(
            mapOf(
                ObjectFields.Name to "Vsevolod",
                ObjectFields.Surname to "Vaskin",
                ObjectFields.BirthdayYear to "2003",
                ObjectFields.Team to "divAn",
                ObjectFields.Grade to "КМС",
                ObjectFields.Group to "М19"
            )
        )
        Editor.createParticipantFrom(
            mapOf(
                ObjectFields.Name to "Nikolay",
                ObjectFields.Surname to "Chuhin",
                ObjectFields.BirthdayYear to "2003",
                ObjectFields.Team to "divAn",
                ObjectFields.Grade to "КМС",
                ObjectFields.Group to "М18"
            )
        )
        Editor.createParticipantFrom(
            mapOf(
                ObjectFields.Name to "Dmitry",
                ObjectFields.Surname to "Terenichev",
                ObjectFields.BirthdayYear to "2003",
                ObjectFields.Team to "divAn",
                ObjectFields.Grade to "МС",
                ObjectFields.Group to "М19"
            )
        )
        Editor.createParticipantFrom(
            mapOf(
                ObjectFields.Name to "Andrew",
                ObjectFields.Surname to "Horohorin",
                ObjectFields.BirthdayYear to "2003",
                ObjectFields.Team to "Sekira",
                ObjectFields.Grade to "МС",
                ObjectFields.Group to "М19"
            )
        )
        Editor.createParticipantFrom(
            mapOf(
                ObjectFields.Name to "Kirill",
                ObjectFields.Surname to "Mitkin",
                ObjectFields.BirthdayYear to "2003",
                ObjectFields.Team to "Sekira",
                ObjectFields.Grade to "champion",
                ObjectFields.Group to "М18"
            )
        )
    }
}

object GUI {
    fun run() = application {
        Window(onCloseRequest = ::exitApplication) {
            prepare()
            app()
        }
    }

    enum class State {
        ShowParticipants,
        CreateParticipant,
        Reload
    }

    private var state = mutableStateOf(State.ShowParticipants)
    private val statesStack = mutableListOf(state.value)
    private var needReload = mutableStateOf(false)

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

    @Preview
    @Composable
    private fun app() {
        while (state.value == State.Reload)
            popState()
        val participants = Participant.byId.values.toList()
        Column {
            TopAppBar.draw()
            when (state.value) {
                State.ShowParticipants -> ParticipantsTable(participants).draw()
                State.CreateParticipant -> {
                    ParticipantCreator().draw()
                }
                else -> throw IllegalReceiveException("Forbidden state of GUI")
            }
        }
    }
}
