package ru.emkn.kotlin.sms.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp

abstract class Table<T> {

    abstract inner class TableRow {

        abstract val cells: Map<String, TableCell>

        open val changes
            get() = cells.map { it.key to it.value.newText.value }.toMap()

        @Composable
        open fun draw() {
            var rowSize by remember { mutableStateOf(IntSize.Zero) }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .onSizeChanged {
                        rowSize = it
                    },
                horizontalArrangement = Arrangement.SpaceAround,
            ) {
                for ((_, cell) in cells) {
                    cell.draw((rowSize.width / cells.size).dp)
                }
            }
        }

        abstract fun saveChanges()
    }

    abstract val header: TableHeader<T>

    abstract val rows: List<TableRow>

    @Composable
    open fun draw() {
        Column {
            header.draw()
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                rows.forEach {
                    it.draw()
                }
            }
        }
    }
}
