package ru.emkn.kotlin.sms.view

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf

object TopAppBar {
    enum class State {
        ShowMessage
    }

    private var barState = mutableStateOf(State.ShowMessage)
    private var message = mutableStateOf("")

    @Composable
    private fun showMessage() {
        Text(message.value)
    }

    fun setMessage(newMessage: String) {
        message.value = newMessage
        barState.value = State.ShowMessage
    }

    @Composable
    fun draw() {
        TopAppBar(backgroundColor = MaterialTheme.colors.primarySurface) {
            when (barState.value) {
                State.ShowMessage -> showMessage()
            }
        }
    }
}