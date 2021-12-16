package ru.emkn.kotlin.sms.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import mu.KotlinLogging
import ru.emkn.kotlin.sms.maxTextLength


private val logger = KotlinLogging.logger {}

class TableCell(private val getText: () -> String, private val saveText: () -> Unit = {}) {

    var newText = mutableStateOf(getText())
        private set

    @Composable
    @OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
    fun draw(width: Dp) {

        BasicTextField(newText.value,
            modifier = Modifier
                .background(if (getText() == newText.value) Color.White else Color.LightGray)
                .border(BorderStroke(1.dp, Color.Black))
                .width(width)
                .onPreviewKeyEvent {
                    if (it.type == KeyEventType.KeyDown && it.key == Key.Enter) {
                        saveChanges()
                    }
                    false
                },
            onValueChange = {
                newText.value = it.replace("\n", "").take(maxTextLength)
            })
    }

    private fun saveChanges() {
        saveText()
        logger.debug { "changes saved" }
    }
}