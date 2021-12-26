package ru.emkn.kotlin.sms.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import ru.emkn.kotlin.sms.view.tables.draw

object CompetitionDataPresenter {
    enum class Table {
        Event,
        Checkpoints,
        Routes,
        Teams,
        Groups,
        Participants,
        Timestamps
    }

    var state = mutableStateOf(Table.Event)
}

object AppTopBar {
    val buttons = listOf(
        ActionButton("Events") {
            CompetitionDataPresenter.state.value = CompetitionDataPresenter.Table.Event
        },
        ActionButton("Checkpoints") {
            CompetitionDataPresenter.state.value = CompetitionDataPresenter.Table.Checkpoints
        },
        ActionButton("Routes") {
            CompetitionDataPresenter.state.value = CompetitionDataPresenter.Table.Routes
        },
        ActionButton("Participants") {
            CompetitionDataPresenter.state.value = CompetitionDataPresenter.Table.Participants
        },
        ActionButton("Teams") {
            CompetitionDataPresenter.state.value = CompetitionDataPresenter.Table.Teams
        },
        ActionButton("Groups") {
            CompetitionDataPresenter.state.value = CompetitionDataPresenter.Table.Groups
        },
        ActionButton("Timestamps", visible = false) {
            CompetitionDataPresenter.state.value = CompetitionDataPresenter.Table.Timestamps
        }
    )
}

@Composable
fun drawAppTopBar() {
    TopAppBar(
        modifier = Modifier.fillMaxWidth(),
    ) {
        AppTopBar.buttons.forEach {
            draw(it)
        }
    }
}

@Composable
fun drawTables(gui: GUI, bottomAppBar: BottomAppBar) {

    val eventTable = remember { gui.eventTable }
    val checkpointTable = remember { gui.checkpointTable }
    val routeTable = remember { gui.routeTable }
    val teamTable = remember { gui.teamTable }
    val groupTable = remember { gui.groupTable }
    val participantTable = remember { gui.participantsTable }
    val timestampTable = remember { gui.timestampTable }

    Column {
        drawAppTopBar()
        bottomAppBar.setMessage("You should load events -> checkpoints -> routes -> groups -> teams")
        draw(
            gui, bottomAppBar,
            when (CompetitionDataPresenter.state.value) {
                CompetitionDataPresenter.Table.Event -> eventTable
                CompetitionDataPresenter.Table.Checkpoints -> checkpointTable
                CompetitionDataPresenter.Table.Routes -> routeTable
                CompetitionDataPresenter.Table.Participants -> participantTable
                CompetitionDataPresenter.Table.Teams -> teamTable
                CompetitionDataPresenter.Table.Groups -> groupTable
                CompetitionDataPresenter.Table.Timestamps -> timestampTable
                else -> TODO()
            }
        )
    }
}

