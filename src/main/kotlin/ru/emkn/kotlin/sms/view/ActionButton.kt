package ru.emkn.kotlin.sms.view

import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

data class ActionButton(
    val text: String,
    val visible: Boolean = true,
    val action: () -> Unit
)

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