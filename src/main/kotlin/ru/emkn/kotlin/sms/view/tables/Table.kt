package ru.emkn.kotlin.sms.view.tables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import ru.emkn.kotlin.sms.ObjectFields
import ru.emkn.kotlin.sms.view.ActionButton
import ru.emkn.kotlin.sms.view.GUI
import ru.emkn.kotlin.sms.view.ItemCreator
import ru.emkn.kotlin.sms.view.TableHeader

abstract class Table<T> {

    abstract inner class TableRow {

        abstract val cells: Map<ObjectFields, TableCell>

        open val changes: Map<ObjectFields, String>
            get() = header.columns.filter { it.visible }.associate {
                val cell = cells[it.field] ?: throw IllegalStateException("Cell of ${it.field} not exists")
                it.field to cell.shownText.value
            }

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
                val elementsInRow = header.columns.count { it.visible }
                for (columnHeader in header.columns) {
                    if (columnHeader.visible)
                        cells[columnHeader.field]?.draw((rowSize.width / elementsInRow).dp, columnHeader.readOnly)
                            ?: throw IllegalStateException("Cell of ${columnHeader.field} not exists")
                }
            }
        }

        abstract fun saveChanges()
    }

    abstract val header: TableHeader<T>
    abstract val rows: List<TableRow>
    abstract val itemCreator: ItemCreator<T>

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
                ActionButton("Add") {
                    GUI.pushState(GUI.State.CreateParticipant)
                }.draw()
            }
        }
    }
}
