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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

object BottomAppBar {

    var message = mutableStateOf("")

    val height = 50.dp

    operator fun plusAssign(string : String) {
        message.value = string
    }
}

@Composable
fun drawBottomAppBar() {
    val message = remember { BottomAppBar.message }
    Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.fillMaxHeight()) {
        BottomAppBar(
            modifier = Modifier.height(BottomAppBar.height),
            backgroundColor = MaterialTheme.colors.primarySurface
        ) {
            Text(message.value)
        }
    }
}
