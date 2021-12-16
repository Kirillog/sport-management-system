package ru.emkn.kotlin.sms.View

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp


data class TableHeader(val columnNames: List<String>) {
    @Composable
    fun draw() {
        var rowSize by remember { mutableStateOf(IntSize.Zero) }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .onSizeChanged {
                    rowSize = it
                },
            horizontalArrangement = Arrangement.SpaceAround,
        ) {

            for (name in columnNames) {
                Text(
                    name,
                    modifier = Modifier
                        .border(BorderStroke(1.dp, Color.Black))
                        .width((rowSize.width / columnNames.size).dp)
                )
            }
        }
    }
}