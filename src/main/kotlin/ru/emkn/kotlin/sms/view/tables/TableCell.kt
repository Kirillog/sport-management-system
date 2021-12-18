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
import ru.emkn.kotlin.sms.view.TopAppBar


class TableCell(private val getText: () -> String, private val saveText: () -> Unit = {}) {

    lateinit var shownText: MutableState<String>
        private set


    @Composable
    @OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
    fun draw(width: Dp, readOnly: Boolean) {
        if (!this::shownText.isInitialized)
            shownText = mutableStateOf(getText())

        var backgroundColor = remember { mutableStateOf(Color.White) }

        BasicTextField(shownText.value,
            modifier = Modifier
                .border(BorderStroke(1.dp, Color.Black))
                .width(width)
                .onPreviewKeyEvent {
                    if (it.type == KeyEventType.KeyDown && it.key == Key.Enter) {
                        try {
                            saveText()
                            backgroundColor.value = Color.White
                            TopAppBar.setMessage("Saved")
                        } catch (e: Exception) {
                            TopAppBar.setMessage(e.message ?: "Undefined error")
                        }
                    }
                    false
                }.background(backgroundColor.value),
            onValueChange = {
                val newText = it.replace("\n", "").take(maxTextLength)
                if (newText != shownText.value) {
                    shownText.value = newText
                    backgroundColor.value = Color.LightGray
                }
            },
            readOnly = readOnly
        )
    }
}