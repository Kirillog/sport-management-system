package ru.emkn.kotlin.sms.view

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
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
                }
        )
    }
}

