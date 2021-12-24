package ru.emkn.kotlin.sms.view.tables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import org.jetbrains.exposed.dao.id.EntityID
import ru.emkn.kotlin.sms.ObjectFields
import ru.emkn.kotlin.sms.view.ActionButton
import ru.emkn.kotlin.sms.view.GUI
import ru.emkn.kotlin.sms.view.creators.ItemCreator

const val tableDeleteButtonWidth = 10

abstract class Table<T> {

    open fun update() {}

    abstract inner class TableRow {

        abstract val cells: Map<ObjectFields, TableCell>
        abstract val id: Int

        open val changes: Map<ObjectFields, String>
            get() = header.columns.filter { it.visible }.associate {
                val cell = cells[it.field] ?: throw IllegalStateException("Cell of ${it.field} not exists")
                it.field to cell.shownText.value
            }

        @Composable
        open fun draw() {
            update()
            var rowSize by remember { mutableStateOf(IntSize.Zero) }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .onSizeChanged {
                        rowSize = it
                    },
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (header.deleteButton)
                    Box(modifier = Modifier.border(BorderStroke(1.dp, Color.Black))) {
                        IconButton(
                            onClick = { delete() },
                            modifier = Modifier.size(tableDeleteButtonWidth.dp)
                        ) {
                            Icon(imageVector = Icons.Filled.Close, contentDescription = "Delete", tint = Color.Black)
                        }
                    }
                val elementsInRow = header.columns.count { it.visible }

                val cellWidth = if (header.deleteButton)
                    ((rowSize.width - tableDeleteButtonWidth) / elementsInRow).dp
                else
                    (rowSize.width / elementsInRow).dp

                for (columnHeader in header.columns) {
                    if (columnHeader.visible)
                        cells[columnHeader.field]?.draw(cellWidth, columnHeader.readOnly)
                            ?: throw IllegalStateException("Cell of ${columnHeader.field} not exists")
                }
            }
        }

        private fun delete() {
            if (!header.deleteButton)
                return
            deleteAction()
            GUI.reload()
        }

        abstract fun saveChanges()
        open fun deleteAction() {}

        fun getData(field: ObjectFields): String {
            val cell = cells[field] ?: throw NoSuchElementException("No field $field in cell")
            return cell.getText()
        }
    }

    abstract val header: TableHeader<T>
    abstract val rows: List<TableRow>
    open val creatingState: GUI.State? = null

    @Composable
    open fun draw() {
        Column {
            header.draw()
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                rows.sortedWith(header.comparator).forEach {
                    it.draw()
                }
                val createState = creatingState
                if (createState != null)
                    ActionButton("Add") {
                        GUI.pushState(createState)
                    }.draw()
            }
        }
    }
}
