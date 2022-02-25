package ru.emkn.kotlin.sms.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

object TopAppBar {
    val buttons = listOf(
        ActionButton("Events") {
            TableChooser.state = TableChooser.Table.Event
        },
        ActionButton("Checkpoints") {
            TableChooser.state = TableChooser.Table.Checkpoints
        },
        ActionButton("Routes") {
            TableChooser.state = TableChooser.Table.Routes
        },
        ActionButton("Participants") {
            TableChooser.state = TableChooser.Table.Participants
        },
        ActionButton("Teams") {
            TableChooser.state = TableChooser.Table.Teams
        },
        ActionButton("Groups") {
            TableChooser.state = TableChooser.Table.Groups
        },
        ActionButton("Timestamps", visible = false) {
            TableChooser.state = TableChooser.Table.Timestamps
        }
    )
}

@Composable
fun drawTopAppBar() {
    TopAppBar(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            TopAppBar.buttons.forEach {
                drawActionButton(it)
            }
        }
    }
}
