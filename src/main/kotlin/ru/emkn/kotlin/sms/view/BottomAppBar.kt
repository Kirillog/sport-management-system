package ru.emkn.kotlin.sms.view

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.material.BottomAppBar
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class BottomAppBar {


    var message = mutableStateOf("")

    fun setMessage(newMessage: String) {
        message.value = newMessage
    }

    companion object {
        val height = 50.dp
    }

}

@Composable
fun draw(bottomBar: BottomAppBar) {
    Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.fillMaxHeight()) {
        BottomAppBar(
                modifier = Modifier.height(BottomAppBar.height),
                backgroundColor = MaterialTheme.colors.primarySurface
        ) {
            Text(bottomBar.message.value)
        }
    }
}
