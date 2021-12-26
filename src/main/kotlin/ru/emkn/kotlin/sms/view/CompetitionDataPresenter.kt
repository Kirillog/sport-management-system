package ru.emkn.kotlin.sms.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
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

    var state by mutableStateOf(Table.Event)
}

object AppTopBar {
    val buttons = listOf(
        ActionButton("Events") {
            CompetitionDataPresenter.state = CompetitionDataPresenter.Table.Event
        },
        ActionButton("Checkpoints") {
            CompetitionDataPresenter.state = CompetitionDataPresenter.Table.Checkpoints
        },
        ActionButton("Routes") {
            CompetitionDataPresenter.state = CompetitionDataPresenter.Table.Routes
        },
        ActionButton("Participants") {
            CompetitionDataPresenter.state = CompetitionDataPresenter.Table.Participants
        },
        ActionButton("Teams") {
            CompetitionDataPresenter.state = CompetitionDataPresenter.Table.Teams
        },
        ActionButton("Groups") {
            CompetitionDataPresenter.state = CompetitionDataPresenter.Table.Groups
        },
        ActionButton("Timestamps", visible = false) {
            CompetitionDataPresenter.state = CompetitionDataPresenter.Table.Timestamps
        }
    )
}

@Composable
fun drawAppTopBar() {
    TopAppBar(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            AppTopBar.buttons.forEach {
                draw(it)
            }
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
            when (CompetitionDataPresenter.state) {
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

