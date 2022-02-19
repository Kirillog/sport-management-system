package ru.emkn.kotlin.sms.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

data class ButtonsChooser(val question: String, val buttons: List<ActionButton>)

@Composable
fun draw(buttonsChooser: ButtonsChooser) {
    Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(buttonsChooser.question)
        for (button in buttonsChooser.buttons) {
            draw(button)
        }
    }
}