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
fun drawTables(view: View) {

    val eventTable = remember { view.eventTable }
    val checkpointTable = remember { view.checkpointTable }
    val routeTable = remember { view.routeTable }
    val teamTable = remember { view.teamTable }
    val groupTable = remember { view.groupTable }
    val participantTable = remember { view.participantsTable }
    val timestampTable = remember { view.timestampTable }

    Column {
        drawAppTopBar()
        draw(
                view,
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

