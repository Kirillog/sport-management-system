package ru.emkn.kotlin.sms.view.tables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import ru.emkn.kotlin.sms.ObjectFields
import ru.emkn.kotlin.sms.view.ActionButton
import ru.emkn.kotlin.sms.view.GUI
import ru.emkn.kotlin.sms.view.draw

const val tableDeleteButtonWidth = 10

abstract class Table<T> {

    enum class State {
        Updated, Outdated
    }

    open fun update() {}

    abstract inner class TableRow {

        val header
            get() = this@Table.header

        abstract val cells: Map<ObjectFields, TableCell>
        abstract val id: Int

        open val changes: Map<ObjectFields, String>
            get() = header.columns.filter { it.visible }.associate {
                val cell = cells[it.field] ?: throw IllegalStateException("Cell of ${it.field} not exists")
                it.field to cell.shownText.value
            }

        fun delete() {
            if (!header.deleteButton)
                return
            deleteAction()
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

    var state by mutableStateOf(State.Updated)

    open val creatingState: GUI.State? = null
    open val loadAction: () -> Unit = {}
}

@Composable
fun <T> draw(tableRow: Table<T>.TableRow) {
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
        if (tableRow.header.deleteButton)
            Box(modifier = Modifier.border(BorderStroke(1.dp, Color.Black))) {
                IconButton(
                    onClick = { tableRow.delete() },
                    modifier = Modifier.size(tableDeleteButtonWidth.dp)
                ) {
                    Icon(imageVector = Icons.Filled.Close, contentDescription = "Delete", tint = Color.Black)
                }
            }
        val elementsInRow = tableRow.header.columns.count { it.visible }

        val cellWidth = if (tableRow.header.deleteButton)
            ((rowSize.width - tableDeleteButtonWidth) / elementsInRow).dp
        else
            (rowSize.width / elementsInRow).dp

        for (columnHeader in tableRow.header.columns) {
            if (columnHeader.visible)
                tableRow.cells[columnHeader.field]?.draw(cellWidth, columnHeader.readOnly)
                    ?: throw IllegalStateException("Cell of ${columnHeader.field} not exists")
        }
    }
}

@Composable
fun <F, T : Table<F>> draw(gui: GUI, table: T) {
    if (table.state == Table.State.Outdated)
        table.state = Table.State.Updated
    Column {
        draw(table.header)
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            table.rows.sortedWith(table.header.comparator).forEach {
                draw(it)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                val createState = table.creatingState
                draw(
                    ActionButton("Load") {
                        table.loadAction()
                    }
                )
                if (createState != null) {
                    draw(
                        ActionButton("Add") {
                            gui.pushState(createState)
                        }
                    )
                }
            }
        }
    }
}
