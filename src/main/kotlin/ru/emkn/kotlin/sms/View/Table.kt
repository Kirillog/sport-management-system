package ru.emkn.kotlin.sms.View

import androidx.compose.runtime.Composable

interface TableRow {

    @Composable
    fun draw()

    fun saveChanges()
}

interface Table {

    @Composable
    fun draw()
}
