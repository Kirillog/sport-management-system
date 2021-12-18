package ru.emkn.kotlin.sms.view

import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults.buttonColors
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

class ActionButton(
    private val text: String,
    private val action: () -> Unit
) {
    @Composable
    fun draw() {
        Button(
            onClick = action,
            colors = buttonColors(backgroundColor = Color.Gray)
        ) {
            Text(text)
        }
    }
}