package ru.emkn.kotlin.sms.view

import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

class ActionButton(
        val text: String,
        visible: Boolean = true,
        val action: () -> Unit
) {
    var visible by mutableStateOf(visible)
}

@Composable
fun draw(actionButton: ActionButton) {
    if (actionButton.visible)
        Button(
                onClick = { actionButton.action() },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray)
        ) {
            Text(actionButton.text)
        }
}