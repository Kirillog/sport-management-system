package ru.emkn.kotlin.sms.view

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.paddingFrom
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.material.BottomAppBar
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import ru.emkn.kotlin.sms.model.*

private val routes = listOf(
    Route("М18", listOf(CheckPoint(1), CheckPoint(2)))
)

private val groups = listOf(
    Group("М18", "М18")
)

private val teams = listOf(
    Team("divAn")
)

private val participants = listOf(
    Participant("Vsevolod", "Vaskin", 2003, "М18", "divAn", "КМС"),
    Participant("Vsevolod", "Vaskin", 2003, "М18", "divAn", "КМС"),
    Participant("Vsevolod", "Vaskin", 2003, "М18", "divAn", "КМС"),
    Participant("Vsevolod", "Vaskin", 2003, "М18", "divAn", "КМС"),
    Participant("Vsevolod", "Vaskin", 2003, "М18", "divAn", "КМС"),
    Participant("Vsevolod", "Vaskin", 2003, "М18", "divAn", "КМС"),
    Participant("Vsevolod", "Vaskin", 2003, "М18", "divAn", "КМС"),
    Participant("Vsevolod", "Vaskin", 2003, "М18", "divAn", "КМС"),
    Participant("Vsevolod", "Vaskin", 2003, "М18", "divAn", "КМС"),
    Participant("Vsevolod", "Vaskin", 2003, "М18", "divAn", "КМС"),
    Participant("Vsevolod", "Vaskin", 2003, "М18", "divAn", "КМС"),
    Participant("Vsevolod", "Vaskin", 2003, "М18", "divAn", "КМС"),
    Participant("Vsevolod", "Vaskin", 2003, "М18", "divAn", "КМС"),
    Participant("Vsevolod", "Vaskin", 2003, "М18", "divAn", "КМС"),
    Participant("Vsevolod", "Vaskin", 2003, "М18", "divAn", "КМС"),
    Participant("Vsevolod", "Vaskin", 2003, "М18", "divAn", "КМС"),
)

object GUI {
    fun run() = application {
        Window(onCloseRequest = ::exitApplication) {
            app()
        }
    }

    @Preview
    @Composable
    private fun app() {

        Column {
            TopAppBar.draw()
            ParticipantsTable(participants).draw()
        }

    }
}
