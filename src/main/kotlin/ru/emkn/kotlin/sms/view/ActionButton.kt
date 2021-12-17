package ru.emkn.kotlin.sms.view

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable

class ActionButton(private val text: String, private val action: () -> Unit) {
    @Composable
    fun draw() {
        Button(onClick = action) {
            Text(text)
        }
    }
}