package ru.emkn.kotlin.sms.view

import androidx.compose.material.Button
import androidx.compose.material.ButtonColors
import androidx.compose.material.Text
import androidx.compose.runtime.Composable

class Button(
    private val text: String,
    private val colors: ButtonColors,
    private val action: () -> Unit
) {
    @Composable
    fun draw() {
        Button(onClick = action, colors = colors) {
            Text(text)
        }
    }
}