package ru.emkn.kotlin.sms.view.tables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.emkn.kotlin.sms.maxTextLength
import ru.emkn.kotlin.sms.view.BottomAppBar


data class TableCell(val getText: () -> String, val saveText: () -> Unit = {}) {

    var shownText: MutableState<String> = mutableStateOf(getText())
}

@Composable
@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
fun draw(tableCell: TableCell, width: Dp, readOnly: Boolean, bottomAppBar: BottomAppBar) {
    val backgroundColor = remember { mutableStateOf(Color.White) }

    BasicTextField(
        tableCell.shownText.value,
        modifier = Modifier
            .border(BorderStroke(1.dp, Color.Black))
            .width(width)
            .onPreviewKeyEvent {
                if (it.type == KeyEventType.KeyDown && it.key == Key.Enter) {
                    try {
                        tableCell.saveText()
                        backgroundColor.value = Color.White
                        bottomAppBar.setMessage("Saved")
                    } catch (e: Exception) {
                        bottomAppBar.setMessage(e.message ?: "Undefined error")
                    }
                }
                false
            }.background(backgroundColor.value),
        onValueChange = {
            val newText = it.replace("\n", "").take(maxTextLength)
            if (newText != tableCell.shownText.value) {
                tableCell.shownText.value = newText
                backgroundColor.value = Color.LightGray
            }
        },
        readOnly = readOnly
    )
}