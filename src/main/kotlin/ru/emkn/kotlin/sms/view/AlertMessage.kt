package ru.emkn.kotlin.sms.view

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier

class AlertMessage(private val message: String) {

    var isShow = mutableStateOf(true)

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun draw() {
        if (isShow.value) {
            AlertDialog(
                modifier = Modifier.fillMaxWidth(),
                onDismissRequest = {},
                title = { Text(message) },
                confirmButton = {
                    Button(onClick = {
                        isShow.value = false
                    }) {
                        Text("ОК")
                    }
                }
            )
        }
    }
}