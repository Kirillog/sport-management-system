package ru.emkn.kotlin.sms.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

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