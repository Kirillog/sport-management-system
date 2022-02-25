package ru.emkn.kotlin.sms.view

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import ru.emkn.kotlin.sms.view.tables.drawTable

object TableChooser {
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
        drawTopAppBar()
        drawTable(
            view,
            when (TableChooser.state) {
                TableChooser.Table.Event -> eventTable
                TableChooser.Table.Checkpoints -> checkpointTable
                TableChooser.Table.Routes -> routeTable
                TableChooser.Table.Participants -> participantTable
                TableChooser.Table.Teams -> teamTable
                TableChooser.Table.Groups -> groupTable
                TableChooser.Table.Timestamps -> timestampTable
            }
        )
    }
}
