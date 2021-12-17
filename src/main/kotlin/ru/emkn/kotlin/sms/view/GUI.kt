package ru.emkn.kotlin.sms.view

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
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
        ParticipantsTable(participants).draw()
    }
}
